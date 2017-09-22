package com.fretron.transporter.TransporterManager;

import com.fretron.Context;
import com.fretron.Model.Command;
import com.fretron.Model.EnrichedTransporterCommand;
import com.fretron.Model.Groups;
import com.fretron.Model.Transporter;
import com.fretron.Utils.PropertiesUtil;
import com.fretron.Utils.SerdeUtils;
import com.fretron.constants.Constants;
import com.fretron.Utils.SpecificAvroSerde;
import com.fretron.constants.ErrorMessages;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Reducer;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created by anurag on 14-Sep-17.
 */
public class TransporterManager {


    public  KafkaStreams createStream(String SchemaRegistryURL,String bootstrapServer)

    {

        final Properties streamsConfiguration = PropertiesUtil.initializeProperties(Context.getConfig().getString(Constants.KEY_TRANSPORTER_APP_ID),SchemaRegistryURL, bootstrapServer, Context.getConfig());

//declaring Serdes
        Serde<String> stringSerde = Serdes.String();
        SpecificAvroSerde<Transporter> transporterSpecificAvroSerde = SerdeUtils.createSerde(SchemaRegistryURL);
        SpecificAvroSerde<Command> commandSerde = SerdeUtils.createSerde(SchemaRegistryURL);
        SpecificAvroSerde<EnrichedTransporterCommand> commandOfTransporterSerde = SerdeUtils.createSerde(SchemaRegistryURL);
        KStreamBuilder streamBuilder = new KStreamBuilder();


        /*
        read from command topic and filter by trasnsporter
         */
        KStream<String, Command> transporterCommandKStream = streamBuilder
                .stream(stringSerde, commandSerde, Context.getConfig().getString(Constants.KEY_COMMAND_TOPIC)).filter((key,value)->value.getType().contains("transporter"));

        transporterCommandKStream.print("Command KStream ");

        //read from coomandResult Topic and filter by statuscode 200
        KStream<String, Command> commandResult = streamBuilder
                .stream(stringSerde, commandSerde, Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC)).filter((k,v)->v.getType().contains("transporter")&& v.getStatusCode()==200);


        KStream<String,Transporter> existingCommandStream=commandResult.mapValues((values)->{
            Transporter transporter= transporterSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_TRANSPORTER_TOPIC),values.getData().array());
            return transporter;
        });
        existingCommandStream.print("Command result KStream");


        KTable<String,Transporter> existingTransporterStreamByTrasporterId=existingCommandStream.selectKey((k, v) -> v.getTransporterId())
                .groupByKey(Serdes.String(), transporterSpecificAvroSerde).reduce(new Reducer<Transporter>() {
                    @Override
                    public Transporter apply(Transporter transporter, Transporter t1) {
                        return t1;
                    }
                }, Context.getConfig().getString(Constants.KEY_TRANSPORTER_ID_STORE));

        existingTransporterStreamByTrasporterId.print("transporter KTable by transporter id");
        //Call various topologies---->

        createTransporter(transporterCommandKStream,commandSerde,transporterSpecificAvroSerde);

        updateTransporter(transporterCommandKStream,existingTransporterStreamByTrasporterId,commandSerde,commandOfTransporterSerde, transporterSpecificAvroSerde);

        deleteTransporter(transporterCommandKStream,existingTransporterStreamByTrasporterId,commandSerde,commandOfTransporterSerde, transporterSpecificAvroSerde);

        return new KafkaStreams(streamBuilder, streamsConfiguration);


    }
    //create Topology
    /*
    transporter can be create  multiple times , there is no any validation
     */

    public static void createTransporter(KStream<String,Command> transporterCommandKStream,SpecificAvroSerde<Command> commandSerde,SpecificAvroSerde<Transporter> transporterSpecificAvroSerde)

    {

        KStream<String,Command> commandCreateKStream =transporterCommandKStream.filter((k,v)->v.getType().contains("create.command"));

        commandCreateKStream.mapValues((values)->{
            Transporter transporter=transporterSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_TRANSPORTER_TOPIC), values.getData().array());
            transporter.setTransporterId(UUID.randomUUID().toString());
            Command command = new Command();
            command.setStartTime(values.getStartTime());
            command.setType("transporter.created");
            command.setId(values.getId());
            command.setProcessTime(System.currentTimeMillis());
            command.setStatusCode(200);
            command.setData(ByteBuffer.wrap(transporterSpecificAvroSerde.serializer().serialize(Context.getConfig().getString(Constants.KEY_TRANSPORTER_TOPIC),transporter)));
            return command;
        }).selectKey((k, v) -> {
            return v.getId();
        }).to(Serdes.String(), commandSerde, Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));

    }

    /*
    for update, transporter should be exist otherwise update will be fail.
     */
    public static void  updateTransporter(KStream<String,Command> transporterCommandKStream,KTable<String,Transporter> existingTransporterStreamByTrasporterId,SpecificAvroSerde<Command> commandSerde,SpecificAvroSerde<EnrichedTransporterCommand> commandOfTransporterSerde, SpecificAvroSerde<Transporter> transporterSpecificAvroSerde)
    {

        KStream<String,Command> commandTransporterUpdateKStream =transporterCommandKStream.filter((k,v)->v.getType().contains("update.command"))
                .selectKey((key,value)->transporterSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_TRANSPORTER_TOPIC),value.getData().array()).getTransporterId());



        KStream<String,EnrichedTransporterCommand> commandTransporterKStreamByID=commandTransporterUpdateKStream.mapValues((v) ->{
            Transporter transporter=transporterSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_TRANSPORTER_TOPIC), v.getData().array());

            Command  command=v;
            EnrichedTransporterCommand commandOfModel =new EnrichedTransporterCommand();
            commandOfModel.setTransporter(transporter);
            commandOfModel.setCommand(command);
            return  commandOfModel;

        }).selectKey((k,v)->v.getTransporter().getTransporterId());

        commandTransporterKStreamByID.print(" update KStream by uuid :");


        //join command stream and existing trasporter KTable, check transporter exist or not
        KStream<String,EnrichedJoinedCommand> enrichedJoinedCommandKStreamBranch[] =
                commandTransporterKStreamByID
                        .leftJoin(existingTransporterStreamByTrasporterId,
                                (leftValue, rightValue)->new EnrichedJoinedCommand(leftValue,rightValue),
                                Serdes.String(),commandOfTransporterSerde)

                        .branch((key,value)->value.existingTransporter!=null && value.existingTransporter.isDeleted==false,
                                (key,value)->true);

        //brach[0] is succeess -->

        enrichedJoinedCommandKStreamBranch[0].mapValues((values)->{
            Transporter oldTransporter=values.existingTransporter;
            Transporter newTransporter=values.commandOfTransporter.getTransporter();


            if (newTransporter.getAdminEmail()!=null)
            {
                List<String> oldemailList= oldTransporter.getAdminEmail();

                oldemailList.addAll(newTransporter.getAdminEmail());
                HashSet<String> hashSet =new HashSet<>(oldemailList);

                oldTransporter.setAdminEmail(new ArrayList<>(hashSet));
            }

            if (newTransporter.getGroups()!=null)
            {
                List<Groups> oldGroupList=oldTransporter.getGroups();
                oldGroupList.addAll(newTransporter.getGroups());
                oldTransporter.setGroups(oldGroupList);

            }
            Command command=new Command();
            command.setType("transporter.updated");
            command.setData(ByteBuffer.wrap(transporterSpecificAvroSerde.serializer().serialize(Context.getConfig().getString(Constants.KEY_TRANSPORTER_TOPIC),oldTransporter)));
            command.setId(values.commandOfTransporter.getCommand().getId());
            command.setProcessTime(System.currentTimeMillis());
            command.setStatusCode(200);
            return command;
        }).selectKey((key,value)->value.getId()).to(Serdes.String(),commandSerde,Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));


        //branch[1] is error -->
        enrichedJoinedCommandKStreamBranch[1].mapValues((values)->{return getErrorMessage(values,"transporter.update.fail", ErrorMessages.TRANSPORTER_NOT_EXIST,404,transporterSpecificAvroSerde);
        }).selectKey((k, v) -> {
            return v.getId();
        }).to(Serdes.String(), commandSerde, Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));

    }


    /*In delete topology , transporter should be exist
    otherwise deletion will be failed*/


    public static void  deleteTransporter(KStream<String,Command> transporterCommandKStream,KTable<String,Transporter> existingTransporterStreamByTrasporterId,SpecificAvroSerde<Command> commandSerde,SpecificAvroSerde<EnrichedTransporterCommand> commandOfTransporterSerde, SpecificAvroSerde<Transporter> transporterSpecificAvroSerde)

    {
        KStream<String,Command> commandTransporterUpdateKStream =transporterCommandKStream.filter((k,v)->v.getType().contains("delete.command"))
                .selectKey((key,value)->transporterSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_TRANSPORTER_TOPIC),value.getData().array()).getTransporterId());

        KStream<String,EnrichedTransporterCommand> commandTransporterKStreamByID=commandTransporterUpdateKStream.mapValues((v) ->{
            Transporter transporter=transporterSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_TRANSPORTER_TOPIC), v.getData().array());

            Command  command=v;
            EnrichedTransporterCommand commandOfModel =new EnrichedTransporterCommand();
            commandOfModel.setTransporter(transporter);
            commandOfModel.setCommand(command);
            return  commandOfModel;

        }).selectKey((k,v)->v.getTransporter().getTransporterId());


//check transporter exist or not ,create two braches
        KStream<String,EnrichedJoinedCommand> enrichedJoinedCommandKStreamBranch[] =
                commandTransporterKStreamByID
                        .leftJoin(existingTransporterStreamByTrasporterId,
                                (leftValue,rightValue)->new EnrichedJoinedCommand(leftValue,rightValue),
                                Serdes.String(),commandOfTransporterSerde)

                        .branch((key,value)->value.existingTransporter!=null && value.existingTransporter.isDeleted==false,
                                (key,value)->true);

        //branch[[0] is success--> deleted successfull
        enrichedJoinedCommandKStreamBranch[0].mapValues((values)->{
            Transporter oldTransporter=values.existingTransporter;
            oldTransporter.setIsDeleted(true);

            Command command=new Command();
            command.setType("transporter.deleted");
            command.setData(ByteBuffer.wrap(transporterSpecificAvroSerde.serializer().serialize(Context.getConfig().getString(Constants.KEY_TRANSPORTER_TOPIC),oldTransporter)));
            command.setId(values.commandOfTransporter.getCommand().getId());
            command.setProcessTime(System.currentTimeMillis());
            command.setStatusCode(200);
            return command;
        }).selectKey((key,value)->value.getId()).to(Serdes.String(),commandSerde,Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));

        //branch[1] is error-->delete failed

        enrichedJoinedCommandKStreamBranch[1].mapValues((values)->{return getErrorMessage(values,"transporter.delete.fail",ErrorMessages.TRANSPORTER_NOT_EXIST,404,transporterSpecificAvroSerde);
        }).selectKey((k, v) -> {
            return v.getId();
        }).to(Serdes.String(), commandSerde, Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));

    }

    public static Command getErrorMessage(EnrichedJoinedCommand values,String type,String errorMessage,int statusCode,SpecificAvroSerde<Transporter> transporterSpecificAvroSerde)

    {
        EnrichedTransporterCommand commandOfTransporter=values.commandOfTransporter;

        Command command = new Command();
        command.setStartTime(commandOfTransporter.getCommand().getStartTime());
        command.setType(type);
        command.setId(commandOfTransporter.getCommand().getId());
        command.setProcessTime(System.currentTimeMillis());
        command.setErrorMessage(errorMessage);
        command.setStatusCode(statusCode);
        command.setData(ByteBuffer.wrap(transporterSpecificAvroSerde.serializer().serialize(Context.getConfig().getString(Constants.KEY_TRANSPORTER_TOPIC), commandOfTransporter.getTransporter())));
        return command;
    }

    private static  class EnrichedJoinedCommand {
        public Transporter existingTransporter;
        public EnrichedTransporterCommand commandOfTransporter;


        public EnrichedJoinedCommand(EnrichedTransporterCommand commandOfModel,Transporter existingTransporter)
        {
            this.commandOfTransporter=commandOfModel;
            this.existingTransporter=existingTransporter;


        }

    }
}

