package com.fretron.transporter.LaneManager;


import com.fretron.Context;
import com.fretron.Model.Command;
import com.fretron.Model.EnrichedTransporterCommand;
import com.fretron.Model.Lane;
import com.fretron.Model.Transporter;
import com.fretron.Utils.PropertiesUtil;
import com.fretron.Utils.SerdeUtils;
import com.fretron.Utils.SpecificAvroSerde;
import com.fretron.constants.Constants;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;

import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.UUID;

public class LaneManager {

    public KafkaStreams createLane(String schemaRegistry,String bootstarpServer) {
        KStreamBuilder builder = new KStreamBuilder();

        Properties properties= PropertiesUtil.initializeProperties(Context.getConfig().getString(Constants.KEY_APPLICATION_ID),schemaRegistry,bootstarpServer,Context.getConfig());
/*
Serdes
 */
        SpecificAvroSerde<Command> commandSpecificAvroSerde= SerdeUtils.createSerde(schemaRegistry);
        SpecificAvroSerde<Lane> laneSerde = SerdeUtils.createSerde(schemaRegistry);
        SpecificAvroSerde<EnrichedTransporterCommand> commandOfLaneSerde = SerdeUtils.createSerde(schemaRegistry);
        SpecificAvroSerde<Transporter> transporterSerde = SerdeUtils.createSerde(schemaRegistry);
        /*
        KStream from command topic
         */
        KStream<String, EnrichedTransporterCommand> commandKStream = builder.stream(Serdes.String(), commandSpecificAvroSerde,
                Context.getConfig().getString(Constants.KEY_COMMAND_TOPIC))
                .filter((key,value)->value.getType().contains("lane"))
                .mapValues((values)-> new EnrichedTransporterCommand(values,laneSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_LANES_TOPIC),values.getData().array()),null,null,null));

        /*
        KStream from command result topic
         */
        KStream<String,Command> commandResultKS=builder.stream(Serdes.String(),commandSpecificAvroSerde,Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));

        /*
        filtering from command stream
         */
        KStream<String, EnrichedTransporterCommand>[] branchedKStream = commandKStream
                .branch((key, value) -> value.getCommand().getType().contains("create") ,
                        (key, value) -> value.getCommand().getType().contains("update"),
                         (key, value) -> value.getCommand().getType().contains("delete"));

        commandKStream.print("command KStream : ");

        /*
        create transporter ktable
         */
        KTable<String,Transporter> transporterKTable = commandResultKS
                .filter((key,value)->value.getType().contains("transporter") && value.getStatusCode()==200)
                .mapValues((values)->transporterSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_TRANSPORTER_TOPIC),values.getData().array()))
                .selectKey((key,value)->value.getTransporterId())
                .groupByKey(Serdes.String(),transporterSerde)
                .reduce((value,aggValue)->aggValue,Context.getConfig().getString(Constants.KEY_TRANSPORTER_ID_STORE));

       /*
       making sure that transporter id exists
        */
              KStream<String,EnrichedTransporterCommandAndTransporter> joinedKstream = branchedKStream[0]
                                    .selectKey((Key,value)->value.getLane().getTransporterId())
                                    .leftJoin(transporterKTable,
                                            (leftValue,rightValue)->new EnrichedTransporterCommandAndTransporter(rightValue,leftValue),
                                            Serdes.String(),commandOfLaneSerde);

              KStream<String,EnrichedTransporterCommandAndTransporter> branchJoinedStream[] = joinedKstream.branch((key,value)->value.transporter==null || value.transporter.isDeleted,
                      (key,value)->value.transporter!=null && value.transporter.isDeleted==false);

        branchJoinedStream[0].print("null");
        branchJoinedStream[1].print("not null");
              /*
              create lane if transporter id exist
               */
              branchJoinedStream[1]
                      .mapValues((values)->{
                       Lane lane=values.commandOfLane.lane;
                       lane.setUuid(UUID.randomUUID().toString());

                       Command command = new Command();
                       command.setData(ByteBuffer.wrap(laneSerde.serializer().serialize(Context.getConfig().getString(Constants.KEY_LANES_TOPIC),lane)));
                       command.setStatusCode(200);
                       command.setProcessTime(System.currentTimeMillis());
                       command.setType("lane.create.success");
                       command.setStartTime(values.commandOfLane.command.getStartTime());
                       command.setErrorMessage(null);
                       command.setId(values.commandOfLane.command.getId());

                       return command;
                      }).selectKey((key,value)->value.getId())
                      .to(Serdes.String(),commandSpecificAvroSerde,Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));
/*
Send error message if transporter id doesn't exist
 */
sendErrorMessage(branchJoinedStream[0],"lane.create.failed","transporter id doesn't exist",commandSpecificAvroSerde);


        //Update lane topology
        /*
        creating ktable of lanes of lanes for update
         */
        KTable<String, Lane> laneKTable = commandResultKS
                .filter((key, value) -> value.getType().contains("lane") && value.getStatusCode()==200)
                .mapValues(value -> laneSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_LANES_TOPIC), value.getData().array()))
                .selectKey((key, value) -> value.getUuid())
                .groupByKey(Serdes.String(), laneSerde)
                .reduce((value, aggValue) -> aggValue, Context.getConfig().getString(Constants.KEY_LANE_BY_UUID_STORE));

        laneKTable.print("lane k table");

//        laneKTable.toStream().to(Serdes.String(), laneSpecificAvroSerde, Context.getConfig().getString(Constants.KEY_LANES_TOPIC));

        /*
        Join stream for existence of a lane
         */
        KStream<String, EnrichedTransporterCommandAndTransporter> joinedKStream = branchedKStream[1].selectKey((key, value)->value.getLane()
                        .getUuid())
                .leftJoin(laneKTable,
                        (leftValue, rightValue) -> new EnrichedTransporterCommandAndTransporter(null,new EnrichedTransporterCommand(leftValue.command,rightValue,null,null,null)),
                        Serdes.String(), commandOfLaneSerde);


      /*
      Branch joined kstream to verify existence of lane
       */
        KStream<String, EnrichedTransporterCommandAndTransporter>[] branchJoinedLaneKStream = joinedKStream
                .branch((key, value) -> value.commandOfLane.lane == null,
                        (key, value) -> value.commandOfLane.lane != null);


       /*
       Send error message if lane doesn't exist
        */
        sendErrorMessage(branchJoinedLaneKStream[0],"lane.update.failed","lane doesn't exist",commandSpecificAvroSerde);

      /*
      update values of lane if lane exist
       */
        KStream<String, Command> updatedLaneKS = branchJoinedLaneKStream[1].selectKey((key, value) -> value.commandOfLane.command.getId())
                .mapValues((values) -> {
                    Lane updateLane = laneSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_LANES_TOPIC), values.commandOfLane.command.getData().array());

                    if (updateLane.getBaseDestination() != null)
                        values.commandOfLane.lane.setBaseDestination(updateLane.getBaseDestination());

                    if (updateLane.getBaseOrigin() != null)
                        values.commandOfLane.lane.setBaseOrigin(updateLane.getBaseOrigin());

                    if (updateLane.getBaseMaterial() != null)
                        values.commandOfLane.lane.setBaseMaterial(updateLane.getBaseMaterial());

                    if (updateLane.getBasePrice() != null)
                        values.commandOfLane.lane.setBasePrice(updateLane.getBasePrice());

                    if (updateLane.getConsignee() != null)
                        values.commandOfLane.lane.setConsignee(updateLane.getConsignee());

                    if (updateLane.getConsigner() != null)
                        values.commandOfLane.lane.setConsigner(updateLane.getConsigner());

                    if (updateLane.getMaterial() != null)
                        values.commandOfLane.lane.setMaterial(updateLane.getMaterial());

                    Command command = new Command();
                    command.setStartTime(values.commandOfLane.command.getStartTime());
                    command.setProcessTime(System.currentTimeMillis());
                    command.setData(ByteBuffer.wrap(laneSerde.serializer().serialize(Context.getConfig().getString(Constants.KEY_LANES_TOPIC), values.commandOfLane.lane)));
                    command.setId(values.commandOfLane.command.getId());
                    command.setErrorMessage(null);
                    command.setStatusCode(200);
                    command.setType("lane.update.success");

                    return command;
                });

        updatedLaneKS.to(Serdes.String(), commandSpecificAvroSerde, Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));

        commandResultKS.print("command result : ");


        //Lane Delete Topology-->

        KTable<String, Lane> laneKTableByUUID = commandResultKS
                .filter((key, value) -> value.getType().contains("lane") && value.getStatusCode()==200)
                .mapValues(value -> laneSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_LANES_TOPIC), value.getData().array()))
                .selectKey((key, value) -> value.getUuid())
                .groupByKey(Serdes.String(), laneSerde)
                .reduce((value, aggValue) -> aggValue, Context.getConfig().getString(Constants.KEY_LANE_BY_UUID_STORE));


        KStream<String, EnrichedTransporterCommandAndTransporter> joinedKStreamDelete = branchedKStream[2].selectKey((key, value)->value.getLane()
                .getUuid())
                .leftJoin(laneKTableByUUID,
                        (leftValue, rightValue) -> new EnrichedTransporterCommandAndTransporter(null,new EnrichedTransporterCommand(leftValue.command,rightValue,null,null,null)),
                        Serdes.String(), commandOfLaneSerde);

        KStream<String, EnrichedTransporterCommandAndTransporter>[] branchJoinedLaneKStreamDelete = joinedKStreamDelete
                .branch((key, value) -> value.commandOfLane.lane == null|| value.commandOfLane.lane.getIsDeleted()==true ,
                        (key, value) -> value.commandOfLane.lane != null);

        sendErrorMessage(branchJoinedLaneKStreamDelete[0],"lane.delete.failed","lane doesn't exist",commandSpecificAvroSerde);

      /*
      delete values of lane if lane exist
       */
       branchJoinedLaneKStreamDelete[1].selectKey((key, value) -> value.commandOfLane.command.getId())
                .mapValues((values) -> {
               Lane oldLane= values.commandOfLane.getLane();
                oldLane.setIsDeleted(true);

                    Command command = new Command();
                    command.setStartTime(values.commandOfLane.command.getStartTime());
                    command.setProcessTime(System.currentTimeMillis());
                    command.setData(ByteBuffer.wrap(laneSerde.serializer().serialize(Context.getConfig().getString(Constants.KEY_LANES_TOPIC), oldLane)));
                    command.setId(values.commandOfLane.command.getId());
                    command.setErrorMessage(null);
                    command.setStatusCode(200);
                    command.setType("lane.delete.success");
                    return command;
                }).to(Serdes.String(), commandSpecificAvroSerde, Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));

       KafkaStreams streams = new KafkaStreams(builder, properties);
        return streams;
    }

    public void sendErrorMessage(KStream<String,EnrichedTransporterCommandAndTransporter> stream,String type,String errorMessage,SpecificAvroSerde<Command> commandSpecificAvroSerde) {
        stream.mapValues((values) -> {
            Command command = new Command();
            command.setId(values.commandOfLane.command.getId());
            command.setType(type);
            command.setErrorMessage(errorMessage);
            command.setStatusCode(404);
            command.setData(values.commandOfLane.command.getData());
            command.setProcessTime(System.currentTimeMillis());
            command.setStartTime(values.commandOfLane.command.getStartTime());

            return command;
        }).selectKey((key, value) -> value.getId())
                .to(Serdes.String(), commandSpecificAvroSerde, Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));
    }

    /*
    helper class to store command and lane object
     */
    public static class EnrichedTransporterCommandAndTransporter {
        Transporter transporter;
        EnrichedTransporterCommand commandOfLane;

        EnrichedTransporterCommandAndTransporter(Transporter transporter, EnrichedTransporterCommand commandOfLane) {
            this.transporter = transporter;
            this.commandOfLane = commandOfLane;
        }
    }
}
