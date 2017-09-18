package com.fretron.transporter.LaneManager;


import com.fretron.Context;
import com.fretron.Model.Command;
import com.fretron.Model.Lane;
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

    public KafkaStreams createLane(KStreamBuilder builder, SpecificAvroSerde<Command> commandSpecificAvroSerde, SpecificAvroSerde<Lane> laneSpecificAvroSerde, Properties properties) {
        KStream<String, Command> commandKStream = builder.stream(Serdes.String(), commandSpecificAvroSerde,
                Context.getConfig().getString(Constants.KEY_COMMAND_TOPIC))
                .filter((key, value) -> value.getType().contains("lane"));

        KStream<String, Command>[] branchedKStream = commandKStream
                .branch((key, value) -> value.getType().contains("create"),
                        (key, value) -> value.getType().contains("update"));

        commandKStream.print("command KStream : ");

        /*
        Creating lane for transporter
         */
        KStream<String, Command> createdLaneKStream = branchedKStream[0].mapValues((values) -> {
            Lane lane = laneSpecificAvroSerde.deserializer().deserialize(Constants.KEY_LANES_TOPIC, values.getData().array());
            lane.setUuid(UUID.randomUUID().toString());
            values.setProcessTime(System.currentTimeMillis());
            values.setStartTime(values.getStartTime());
            values.setData(ByteBuffer.wrap(laneSpecificAvroSerde.serializer().serialize(Context.getConfig().getString(Constants.KEY_LANES_TOPIC), lane)));
            values.setStatusCode(200);
            values.setErrorMessage(null);
            values.setType("lane.create.success");

            return values;
        }).selectKey((key, value) -> value.getId());

        createdLaneKStream.print("lane created :");
        createdLaneKStream.to(Serdes.String(), commandSpecificAvroSerde, Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));

        //Update lane topology
        /*
        creating ktable of lanes of lanes for update
         */
        KTable<String, Lane> laneKTable = builder.stream(Serdes.String(), commandSpecificAvroSerde, Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC))
                .filter((key, value) -> value.getType().contains("lane.create.success"))
                .mapValues(value -> laneSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_LANES_TOPIC), value.getData().array()))
                .selectKey((key, value) -> value.getUuid())
                .groupByKey(Serdes.String(), laneSpecificAvroSerde)
                .reduce((value, aggValue) -> aggValue, Context.getConfig().getString(Constants.KEY_LANE_BY_UUID_STORE));

        laneKTable.print("lane k table");

//        laneKTable.toStream().to(Serdes.String(), laneSpecificAvroSerde, Context.getConfig().getString(Constants.KEY_LANES_TOPIC));

        /*
        Join stream for existence of a lane
         */
        KStream<String, CommandAndLane> joinedKStream = branchedKStream[1].selectKey((key, value) ->
                laneSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_LANES_TOPIC), value.getData().array())
                        .getUuid())
                .leftJoin(laneKTable,
                        (leftValue, rightValue) -> new CommandAndLane(rightValue, leftValue),
                        Serdes.String(), commandSpecificAvroSerde);


      /*
      Branch joined kstream to verify existence of lane
       */
        KStream<String, CommandAndLane>[] branchJoinedStream = joinedKStream
                .branch((key, value) -> value.lane == null,
                        (key, value) -> value.lane != null);


       /*
       Send error message if lane doesn't exist
        */
        branchJoinedStream[0].mapValues((values) -> {
            Command command = new Command();
            command.setId(values.command.getId());
            command.setType("lane.update.failed");
            command.setErrorMessage("lane doesn't exist");
            command.setStatusCode(404);
            command.setData(values.command.getData());
            command.setProcessTime(System.currentTimeMillis());
            command.setStartTime(values.command.getStartTime());

            return command;
        }).selectKey((key, value) -> value.getId())
                .to(Serdes.String(), commandSpecificAvroSerde, Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));

      /*
      update values of lane if lane exist
       */
        KStream<String, Command> updatedLaneKS = branchJoinedStream[1].selectKey((key, value) -> value.command.getId())
                .mapValues((values) -> {
                    Lane updateLane = laneSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_LANES_TOPIC), values.command.getData().array());


                    if (updateLane.getBaseDestination() != null)
                        values.lane.setBaseDestination(updateLane.getBaseDestination());

                    if (updateLane.getBaseOrigin() != null)
                        values.lane.setBaseOrigin(updateLane.getBaseOrigin());

                    if (updateLane.getBaseMaterial() != null)
                        values.lane.setBaseMaterial(updateLane.getBaseMaterial());

                    if (updateLane.getBasePrice() != null)
                        values.lane.setBasePrice(updateLane.getBasePrice());

                    if (updateLane.getConsignee() != null)
                        values.lane.setConsignee(updateLane.getConsignee());

                    if (updateLane.getConsigner() != null)
                        values.lane.setConsigner(updateLane.getConsigner());

                    if (updateLane.getMaterial() != null)
                        values.lane.setMaterial(updateLane.getMaterial());

                    Command command = new Command();
                    command.setStartTime(values.command.getStartTime());
                    command.setProcessTime(System.currentTimeMillis());
                    command.setData(ByteBuffer.wrap(laneSpecificAvroSerde.serializer().serialize(Context.getConfig().getString(Constants.KEY_LANES_TOPIC), values.lane)));
                    command.setId(values.command.getId());
                    command.setErrorMessage(null);
                    command.setStatusCode(200);
                    command.setType("lane.update.success");

                    return command;
                });

        updatedLaneKS.to(Serdes.String(), commandSpecificAvroSerde, Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));
        laneKTable.print("lanekTable");

        KafkaStreams streams = new KafkaStreams(builder, properties);
        return streams;
    }

    /*
    helper class to store command and lane object
     */
    public static class CommandAndLane {
        Lane lane;
        Command command;

        CommandAndLane(Lane lane, Command command) {
            this.lane = lane;
            this.command = command;
        }
    }
}
