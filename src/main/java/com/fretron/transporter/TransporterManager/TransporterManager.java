package com.fretron.transporter.TransporterManager;

import com.fretron.Context;
import com.fretron.Model.Command;
import com.fretron.Model.CommandOfTransporter;
import com.fretron.Model.Transporter;
import com.fretron.Utils.PropertiesUtil;
import com.fretron.Utils.SerdeUtils;
import com.fretron.constants.Constants;
import com.fretron.Utils.SpecificAvroSerde;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Reducer;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

/**
 * Created by anurag on 14-Sep-17.
 */
public class TransporterManager {


    public  KafkaStreams createStream(String SchemaRegistryURL,String bootstrapServer)

    {

        final Properties streamsConfiguration = PropertiesUtil.initializeProperties(Context.getConfig().getString(Constants.KEY_TRANSPORTER_APP_ID),SchemaRegistryURL, bootstrapServer, Context.getConfig());


       Serde<String> stringSerde = Serdes.String();
        SpecificAvroSerde<Transporter> transporterSpecificAvroSerde = SerdeUtils.createSerde(SchemaRegistryURL);
        SpecificAvroSerde<Command> commandSerde = SerdeUtils.createSerde(SchemaRegistryURL);
        SpecificAvroSerde<CommandOfTransporter> commandOfTransporterSerde = SerdeUtils.createSerde(SchemaRegistryURL);
        KStreamBuilder streamBuilder = new KStreamBuilder();

        KStream<String, Command> commandFilterKStream = streamBuilder
                .stream(stringSerde, commandSerde, Context.getConfig().getString(Constants.KEY_COMMAND_TOPIC)).filter((key,value)->value.getType().contains("transporter"));

        commandFilterKStream.print("Command KStream");

        KStream<String, Command> commandResult = streamBuilder
                .stream(stringSerde, commandSerde, Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC)).filter((k,v)->v.getType().contains("transporter")&& v.getStatusCode()==200);

        commandResult.print("Command result KStream");

        KStream<String,Transporter> existingCommandStream=commandResult.mapValues((values)->{
            Transporter transporter= transporterSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_TRANSPORTER_TOPIC),values.getData().array());
            return transporter;
        });



        KTable<String,Transporter> existingTransporterStreamByTrasporterId=existingCommandStream.selectKey((k, v) -> v.getTransporterId())
                .groupByKey(Serdes.String(), transporterSpecificAvroSerde).reduce(new Reducer<Transporter>() {
                    @Override
                    public Transporter apply(Transporter transporter, Transporter t1) {
                        return t1;
                    }
                }, Context.getConfig().getString(Constants.KEY_TRANSPORTER_ID_STORE));

        existingTransporterStreamByTrasporterId.print("transporter KTable by transporter id");
        //Call various topologies---->

        createTransporter(commandFilterKStream,commandSerde,transporterSpecificAvroSerde);

        updateTransporter(commandFilterKStream,existingTransporterStreamByTrasporterId,commandSerde,commandOfTransporterSerde, transporterSpecificAvroSerde);

        deleteTransporter(commandFilterKStream,existingTransporterStreamByTrasporterId,commandSerde,commandOfTransporterSerde, transporterSpecificAvroSerde);

        return new KafkaStreams(streamBuilder, streamsConfiguration);
        //streams.start();

    }

    public static void createTransporter(KStream<String,Command> commandFilterKStream,SpecificAvroSerde<Command> commandSerde,SpecificAvroSerde<Transporter> transporterSpecificAvroSerde)

    {

        KStream<String,Command> commandCreateKStream =commandFilterKStream.filter((k,v)->v.getType().contains("create.command"));

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

    public static void  updateTransporter(KStream<String,Command> commandFilterKStream,KTable<String,Transporter> existingTransporterStreamByTrasporterId,SpecificAvroSerde<Command> commandSerde,SpecificAvroSerde<CommandOfTransporter> commandOfTransporterSerde, SpecificAvroSerde<Transporter> transporterSpecificAvroSerde)
    {

        KStream<String,Command> commandTransporterUpdateKStream =commandFilterKStream.filter((k,v)->v.getType().contains("update.command"))
                .selectKey((key,value)->transporterSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_TRANSPORTER_TOPIC),value.getData().array()).getTransporterId());



        KStream<String,CommandOfTransporter> commandTransporterKStreamByID=commandTransporterUpdateKStream.mapValues((v) ->{
            Transporter transporter=transporterSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_TRANSPORTER_TOPIC), v.getData().array());

            CommandOfTransporter commandOfTransporter=new CommandOfTransporter();
            commandOfTransporter.setData(transporter);
            commandOfTransporter.setId(v.getId());
            commandOfTransporter.setType(v.getType());
            commandOfTransporter.setErrorMessage(null);
            commandOfTransporter.setProcessTime(System.currentTimeMillis());
            commandOfTransporter.setStartTime(v.getStartTime());
            commandOfTransporter.setStatusCode(200);
            return commandOfTransporter;
        }).selectKey((k,v)->v.getData().getTransporterId());

        commandTransporterKStreamByID.print(" update KStream by uuid :");


        KStream<String,EnrichedJoinedCommand> enrichedJoinedCommandKStreamBranch[] =
                commandTransporterKStreamByID
                        .leftJoin(existingTransporterStreamByTrasporterId,
                                (leftValue, rightValue)->new EnrichedJoinedCommand(leftValue,rightValue),
                                Serdes.String(),commandOfTransporterSerde)

                        .branch((key,value)->value.existingTransporter!=null && value.existingTransporter.isDeleted==false,
                                (key,value)->true);

        enrichedJoinedCommandKStreamBranch[0].mapValues((values)->{
            Transporter oldTransporter=values.existingTransporter;
            Transporter newTransporter=values.commandOfTransporter.getData();

            if (newTransporter.getAdminEmail()!=null)
            {
                oldTransporter.setAdminEmail(newTransporter.getAdminEmail());

            }

            if (newTransporter.getGroups()!=null)
            {
                oldTransporter.setGroups(newTransporter.getGroups());

            }
            Command command=new Command();
            command.setType("transporter.updated");
            command.setData(ByteBuffer.wrap(transporterSpecificAvroSerde.serializer().serialize(Context.getConfig().getString(Constants.KEY_TRANSPORTER_TOPIC),oldTransporter)));
            command.setId(values.commandOfTransporter.getId());
            command.setProcessTime(System.currentTimeMillis());
            command.setStatusCode(200);
            return command;
        }).selectKey((key,value)->value.getId()).to(Serdes.String(),commandSerde,Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));

        enrichedJoinedCommandKStreamBranch[1].mapValues((values)->{
            CommandOfTransporter commandOfTransporter=values.commandOfTransporter;

            Command command = new Command();
            command.setStartTime(commandOfTransporter.getStartTime());
            command.setType("transporter.update.fail");
            command.setId(values.commandOfTransporter.getId());
            command.setProcessTime(System.currentTimeMillis());
            command.setErrorMessage("transporter does not exist");
            command.setStatusCode(404);
            command.setData(ByteBuffer.wrap(transporterSpecificAvroSerde.serializer().serialize(Context.getConfig().getString(Constants.KEY_TRANSPORTER_TOPIC), commandOfTransporter.getData())));
            return command;
        }).selectKey((k, v) -> {
            return v.getId();
        }).to(Serdes.String(), commandSerde, Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));

    }

    public static void  deleteTransporter(KStream<String,Command> commandFilterKStream,KTable<String,Transporter> existingTransporterStreamByTrasporterId,SpecificAvroSerde<Command> commandSerde,SpecificAvroSerde<CommandOfTransporter> commandOfTransporterSerde, SpecificAvroSerde<Transporter> transporterSpecificAvroSerde)

    {
        KStream<String,Command> commandTransporterUpdateKStream =commandFilterKStream.filter((k,v)->v.getType().contains("delete.command"))
                .selectKey((key,value)->transporterSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_TRANSPORTER_TOPIC),value.getData().array()).getTransporterId());

        KStream<String,CommandOfTransporter> commandTransporterKStreamByID=commandTransporterUpdateKStream.mapValues((v) ->{
            Transporter transporter=transporterSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_TRANSPORTER_TOPIC), v.getData().array());

            CommandOfTransporter commandOfTransporter=new CommandOfTransporter();
            commandOfTransporter.setData(transporter);
            commandOfTransporter.setId(v.getId());
            commandOfTransporter.setType(v.getType());
            commandOfTransporter.setErrorMessage(null);
            commandOfTransporter.setProcessTime(System.currentTimeMillis());
            commandOfTransporter.setStartTime(v.getStartTime());
            commandOfTransporter.setStatusCode(200);
            return commandOfTransporter;
        }).selectKey((k,v)->v.getData().getTransporterId());



        KStream<String,EnrichedJoinedCommand> enrichedJoinedCommandKStreamBranch[] =
                commandTransporterKStreamByID
                        .leftJoin(existingTransporterStreamByTrasporterId,
                                (leftValue,rightValue)->new EnrichedJoinedCommand(leftValue,rightValue),
                                Serdes.String(),commandOfTransporterSerde)

                        .branch((key,value)->value.existingTransporter!=null && value.existingTransporter.isDeleted==false,
                                (key,value)->true);

        enrichedJoinedCommandKStreamBranch[0].mapValues((values)->{
            Transporter oldTransporter=values.existingTransporter;
            oldTransporter.setIsDeleted(true);

            Command command=new Command();
            command.setType("transporter.deleted");
            command.setData(ByteBuffer.wrap(transporterSpecificAvroSerde.serializer().serialize(Context.getConfig().getString(Constants.KEY_TRANSPORTER_TOPIC),oldTransporter)));
            command.setId(values.commandOfTransporter.getId());
            command.setProcessTime(System.currentTimeMillis());
            command.setStatusCode(200);
            return command;
        }).selectKey((key,value)->value.getId()).to(Serdes.String(),commandSerde,Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));

        enrichedJoinedCommandKStreamBranch[1].mapValues((values)->{
            CommandOfTransporter commandOfTransporter=values.commandOfTransporter;

            Command command = new Command();
            command.setStartTime(commandOfTransporter.getStartTime());
            command.setType("transporter.delete.fail");
            command.setId(commandOfTransporter.getId());
            command.setProcessTime(System.currentTimeMillis());
            command.setErrorMessage("transporter does not exist");
            command.setStatusCode(404);
            command.setData(ByteBuffer.wrap(transporterSpecificAvroSerde.serializer().serialize(Context.getConfig().getString(Constants.KEY_TRANSPORTER_TOPIC), commandOfTransporter.getData())));
            return command;
        }).selectKey((k, v) -> {
            return v.getId();
        }).to(Serdes.String(), commandSerde, Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));


    }

    private static  class EnrichedJoinedCommand {
        public Transporter existingTransporter;
        public CommandOfTransporter commandOfTransporter;


        public EnrichedJoinedCommand(CommandOfTransporter commandOfTransporter,Transporter existingTransporter)
        {
            this.commandOfTransporter=commandOfTransporter;
            this.existingTransporter=existingTransporter;


        }

    }
}

