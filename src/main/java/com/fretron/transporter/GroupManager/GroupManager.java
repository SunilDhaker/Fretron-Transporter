package com.fretron.transporter.GroupManager;

import com.fretron.Context;
import com.fretron.Model.*;
import com.fretron.Utils.PropertiesUtil;
import com.fretron.Utils.SerdeUtils;
import com.fretron.Utils.SpecificAvroSerde;
import com.fretron.constants.Constants;
import com.fretron.constants.ErrorMessages;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;

import java.nio.ByteBuffer;
import java.security.acl.Group;
import java.util.*;

public class GroupManager {

    public static String APP_ID;
    public SpecificAvroSerde<Command> commandSpecificAvroSerde;
    public SpecificAvroSerde<Transporter> transporterSpecificAvroSerde;
    public SpecificAvroSerde<Groups> groupSpecificAvroSerde;
    SpecificAvroSerde<EnrichedTransporterCommand> EnrichedTransporterCommandSerde ;

    public Serde<String> stringSerde;
    public KStreamBuilder streamBuilder;

    public KafkaStreams startStream(String schemaRegistry,String bootstrapServer) {
        APP_ID = Context.getConfig().getString(Constants.KEY_GROUP_APP_ID);


        final Properties streamsConfiguration = PropertiesUtil.initializeProperties(APP_ID, schemaRegistry,bootstrapServer, Context.getConfig());
        commandSpecificAvroSerde = SerdeUtils.createSerde(schemaRegistry);

        transporterSpecificAvroSerde = SerdeUtils.createSerde(schemaRegistry);
        groupSpecificAvroSerde = SerdeUtils.createSerde(schemaRegistry);
        EnrichedTransporterCommandSerde = SerdeUtils.createSerde(schemaRegistry);
        stringSerde = Serdes.String();
        streamBuilder = new KStreamBuilder();

        KStream<String, Command> commandKStream = streamBuilder.stream(stringSerde, commandSpecificAvroSerde,
                Context.getConfig().getString(Constants.KEY_COMMAND_TOPIC))
                .filter((key, value) -> value.getType().contains("group"));
        commandKStream.print("commandstream:");
        KStream<String, Command> commandResultKStream=streamBuilder.stream(stringSerde, commandSpecificAvroSerde,
                Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC))
                .filter((k, v) ->  v.getStatusCode() == 200);

        KTable<String, Transporter> transporterKTable = commandResultKStream
                .filter((k, v) -> v.getType().contains("transporter"))
                .mapValues((value) -> transporterSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_TRANSPORTER_TOPIC), value.getData().array()))
                .selectKey((key, value) -> value.getTransporterId())
                .groupByKey(stringSerde, transporterSpecificAvroSerde)
                .reduce((value, aggValue) -> aggValue, Context.getConfig().getString(Constants.KEY_TRANSPORTER_ID_STORE));
        KTable<String,Groups> groupsKTable = commandResultKStream
                .filter((k, v) -> v.getType().contains("group"))
                .mapValues((value) -> groupSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_GROUP_TOPIC), value.getData().array()))
                .selectKey((k,v)->v.getGroupId())
                .groupByKey(stringSerde,groupSpecificAvroSerde)
                .reduce((value, aggValue) -> aggValue, Context.getConfig().getString(Constants.KEY_GROUP_BY_ID_STORE));


        createGroup(commandKStream,transporterKTable);
        updateGroup(commandKStream,groupsKTable,transporterKTable);
        deleteGroup(commandKStream,groupsKTable,transporterKTable);
        KafkaStreams streams = new KafkaStreams(streamBuilder, streamsConfiguration);
        return streams;
    }
    public  void createGroup(KStream<String,Command> commandKStream,KTable<String,Transporter> transporterKTable)
    {
        // Group create topology
        KStream<String, Command> createGroupKStream = commandKStream
                .filter((key, value) -> value.getType().contains("create"));
        KStream<String,EnrichedTransporterCommand> commandOfGroupsAndTransporterKStream = createGroupKStream
                .mapValues((values) -> {
                    EnrichedTransporterCommand groupAndCommand = new EnrichedTransporterCommand();
                    groupAndCommand.setCommand(values);
                    groupAndCommand.setGroup(groupSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_GROUP_TOPIC), values.getData().array()));

                    return groupAndCommand;
                });

        KStream<String,EnrichedTransporterCommand>[] commandOfGroupsWithNullTransporterIdStream=commandOfGroupsAndTransporterKStream
                .branch((k,v)->v.group.getTransporterId()==null,(k,v)->v.group.getTransporterId()!=null);
        groupCreateFailed(commandOfGroupsWithNullTransporterIdStream[0],ErrorMessages.TRANSPORTER_ID_REQUIERD);

        KStream<String, EnrichedTransporterCommand> joinedKStream = commandOfGroupsWithNullTransporterIdStream[1]
                .selectKey((key, value) -> value.group.getTransporterId())
                .leftJoin(transporterKTable,
                        (leftValue, rightValue) -> new EnrichedTransporterCommand(leftValue.command,null,null, rightValue, leftValue.group),
                        stringSerde, EnrichedTransporterCommandSerde);

        /*
        branch joined k stream
         */
        KStream<String, EnrichedTransporterCommand>[] branchedStream = joinedKStream.branch( (k, v) -> v.transporter == null || v.transporter.isDeleted, (k, v) -> v.transporter != null  && v.transporter.isDeleted == false);

        groupCreateFailed(branchedStream[0],ErrorMessages.TRANSPORTER_ID_NOT_EXIST);


        KStream<String,Command> groupCreateSuccess=branchedStream[1].mapValues((values) -> {
            Groups group = groupSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_GROUP_TOPIC), values.command.getData().array());
            group.setGroupId(UUID.randomUUID().toString());
            Command command = new Command();
            command.setType("group.create.success");
            command.setStatusCode(200);
            command.setData(ByteBuffer.wrap(groupSpecificAvroSerde.serializer().serialize(Context.getConfig().getString(Constants.KEY_GROUP_TOPIC), group)));
            command.setErrorMessage(null);
            command.setProcessTime(System.currentTimeMillis());
            command.setStartTime(values.command.getStartTime());
            command.setId(values.command.getId());

            return command;
        }).selectKey((key, value) -> value.getId());

        groupCreateSuccess.to(stringSerde, commandSpecificAvroSerde, Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));
        groupCreateSuccess.print("Group Create:");
        KStream<String,EnrichedTransporterCommand> joinedGroupSuccessKStream =groupCreateSuccess
                .mapValues(value->{
                    EnrichedTransporterCommand groupsAndTransporter=new EnrichedTransporterCommand();
                    groupsAndTransporter.setCommand(value);
                    groupsAndTransporter.setGroup( groupSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_GROUP_TOPIC), value.getData().array()));
                    return groupsAndTransporter;
                })
                .selectKey((key, value) ->value.getGroup().getTransporterId() )
                .leftJoin(transporterKTable,
                        (leftValue, rightValue) -> new EnrichedTransporterCommand(leftValue.command,null,null,rightValue,leftValue.group),
                        stringSerde,EnrichedTransporterCommandSerde);


        KStream<String,Command> updatedTransporter=joinedGroupSuccessKStream.mapValues(value->{
            Transporter transporter=value.getTransporter();
            List<Groups> transporterGroups=transporter.getGroups();
            if(transporterGroups==null) {
                transporterGroups = new ArrayList<>();
                transporterGroups.add(value.group);
            }
            else {
                transporterGroups.add(value.group);
            }
            transporter.setGroups(transporterGroups);
            Command command= new Command();
            command.setType("transporter.updated");
            command.setData(ByteBuffer.wrap(transporterSpecificAvroSerde.serializer().serialize(Context.getConfig().getString(Constants.KEY_TRANSPORTER_TOPIC),transporter)));
            command.setId(value.command.getId());
            command.setProcessTime(System.currentTimeMillis());
            command.setStatusCode(200);
            return command;
        }).selectKey((key,value)-> value.getId());
        updatedTransporter.to(stringSerde, commandSpecificAvroSerde, Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));

        updatedTransporter.print("New Updated Transporter:");
    }

    public  void updateGroup(KStream<String,Command> commandKStream,KTable<String,Groups> groupsKTable,KTable<String,Transporter>transporterKTable)
    {

        // Group update topology

        KStream<String, EnrichedTransporterCommand> commandOfGroupsKStream = commandKStream
                .filter((key, value) -> value.getType().contains("update"))
                .mapValues((value) ->
                {
                    EnrichedTransporterCommand commandOfGroups=new EnrichedTransporterCommand();

                    commandOfGroups.setGroup(groupSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_GROUP_TOPIC),value.getData().array()));
                    commandOfGroups.setCommand(value);
                    return commandOfGroups;
                });
        KStream<String, EnrichedTransporterCommand>[]  branchedUpdateStream=commandOfGroupsKStream.branch((k,v)->v.group.getGroupId()==null , (k,v)->v.group.getGroupId()!=null);

        groupUpdateFailed(branchedUpdateStream[0],ErrorMessages.GROUP_ID_REQUIERD);

        KStream<String,EnrichedTransporterCommand> joinedKStream=branchedUpdateStream[1]
                .selectKey((k,v)-> v.getGroup().getGroupId())
                .leftJoin(groupsKTable,(leftValue,rightValue)->new EnrichedTransporterCommand(leftValue.command,null,null,null,rightValue),
                        stringSerde,EnrichedTransporterCommandSerde);
        KStream<String,EnrichedTransporterCommand>[] branchedjoinedKStream=joinedKStream.branch((key,value)->value.group==null ,(key,value)->value.group!=null);
        groupUpdateFailed(branchedjoinedKStream[0],ErrorMessages.GROUP_DOES_NOT_EXIST);

        KStream<String,Command> groupUpdateSuccess=branchedjoinedKStream[1].mapValues((values) -> {
            Groups groupOld=values.group;
            Groups groupNew= groupSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_GROUP_TOPIC),values.command.getData().array());
            if (groupNew.admin!=null)
                groupOld.getAdmin().addAll(groupNew.admin);
            if(groupNew.name!=null)
                groupOld.setName(groupNew.getName());
            if(groupNew.subgroups!=null)
                groupOld.getSubgroups().addAll(groupNew.subgroups);
            if(groupNew.members!=null)
                groupOld.getMembers().addAll(groupNew.members);
            Command command = new Command();
            command.setType("group.update.success");
            command.setStatusCode(200);
            command.setErrorMessage(null);
            command.setId(values.command.getId());
            command.setProcessTime(System.currentTimeMillis());
            command.setData(ByteBuffer.wrap(groupSpecificAvroSerde.serializer().serialize(Context.getConfig().getString(Constants.KEY_GROUP_TOPIC),groupOld)));
            return command;
        }).selectKey((k, v) -> v.getId());
        groupUpdateSuccess.to(stringSerde, commandSpecificAvroSerde, Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));
        groupUpdateSuccess.print("update Success:");


        KStream<String,EnrichedTransporterCommand> joinedGroupSuccessKStream =groupUpdateSuccess
                .mapValues(value->{
                    EnrichedTransporterCommand groupsAndTransporter=new EnrichedTransporterCommand();
                    groupsAndTransporter.setCommand(value);
                    groupsAndTransporter.setGroup( groupSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_GROUP_TOPIC), value.getData().array()));
                    groupsAndTransporter.getGroup();
                    return groupsAndTransporter;
                })
                .selectKey((key, value) ->value.getGroup().getTransporterId() )
                .leftJoin(transporterKTable,
                        (leftValue, rightValue) -> new EnrichedTransporterCommand(leftValue.command,null,null,rightValue,leftValue.group),
                        stringSerde,EnrichedTransporterCommandSerde);

        KStream<String,Command> updatedTransporter=joinedGroupSuccessKStream.mapValues(value->{
            Transporter transporter=value.getTransporter();

            List<Groups> transporterGroups=transporter.getGroups();
            if(transporterGroups==null)
                transporterGroups=new ArrayList<>();
            Map<String,Groups> groupsMap=new HashMap<>();
            for(Groups group:transporterGroups)
            {
                groupsMap.put(group.groupId,group);
            }

            groupsMap.put(value.group.groupId,value.group);
            transporterGroups=new ArrayList<>(groupsMap.values());
            transporter.setGroups(transporterGroups);
            Command command= new Command();
            command.setType("transporter.updated");
            command.setData(ByteBuffer.wrap(transporterSpecificAvroSerde.serializer().serialize(Context.getConfig().getString(Constants.KEY_TRANSPORTER_TOPIC),transporter)));
            command.setId(value.command.getId());
            command.setProcessTime(System.currentTimeMillis());
            command.setStatusCode(200);
            return command;
        }).selectKey((key,value)-> value.getId());
        updatedTransporter.print("Transporter Updated:");
        updatedTransporter.to(stringSerde, commandSpecificAvroSerde, Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));

    }

    public  void deleteGroup(KStream<String,Command> commandKStream,KTable<String,Groups> groupsKTable,KTable<String,Transporter>transporterKTable)
    {

        // Group update topology

        KStream<String, EnrichedTransporterCommand> commandOfGroupsKStream = commandKStream
                .filter((key, value) -> value.getType().contains("delete"))
                .mapValues((value) ->
                {
                    EnrichedTransporterCommand commandOfGroups=new EnrichedTransporterCommand();

                    commandOfGroups.setGroup(groupSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_GROUP_TOPIC),value.getData().array()));
                    commandOfGroups.setCommand(value);
                    return commandOfGroups;
                });
        KStream<String, EnrichedTransporterCommand>[]  branchedUpdateStream=commandOfGroupsKStream.branch((k,v)->v.group.getGroupId()==null , (k,v)->v.group.getGroupId()!=null);

        groupDeleteFailed(branchedUpdateStream[0],ErrorMessages.GROUP_ID_REQUIERD);

        KStream<String,EnrichedTransporterCommand> joinedKStream=branchedUpdateStream[1]
                .selectKey((k,v)-> v.getGroup().getGroupId())
                .leftJoin(groupsKTable,(leftValue,rightValue)->new EnrichedTransporterCommand(leftValue.command,null,null,null,rightValue),
                        stringSerde,EnrichedTransporterCommandSerde);
        KStream<String,EnrichedTransporterCommand>[] branchedjoinedKStream=joinedKStream.branch((key,value)->value.group==null ,(key,value)->value.group!=null);
        groupDeleteFailed(branchedjoinedKStream[0],ErrorMessages.GROUP_DOES_NOT_EXIST);

        KStream<String,Command> groupDeleteSuccess=branchedjoinedKStream[1].mapValues((values) -> {
            Groups groupOld=values.group;
            Command command = new Command();
            command.setType("group.delete.success");
            command.setStatusCode(200);
            command.setErrorMessage(null);
            command.setId(values.command.getId());
            command.setProcessTime(System.currentTimeMillis());
            command.setData(ByteBuffer.wrap(groupSpecificAvroSerde.serializer().serialize(Context.getConfig().getString(Constants.KEY_GROUP_TOPIC),groupOld)));
            return command;
        }).selectKey((k, v) -> v.getId());
        groupDeleteSuccess.to(stringSerde, commandSpecificAvroSerde, Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));
        groupDeleteSuccess.print("delete Success:");


        KStream<String,EnrichedTransporterCommand> joinedGroupSuccessKStream =groupDeleteSuccess
                .mapValues(value->{
                    EnrichedTransporterCommand groupsAndTransporter=new EnrichedTransporterCommand();
                    groupsAndTransporter.setCommand(value);
                    groupsAndTransporter.setGroup( groupSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_GROUP_TOPIC), value.getData().array()));
                    groupsAndTransporter.getGroup();
                    return groupsAndTransporter;
                })
                .selectKey((key, value) ->value.getGroup().getTransporterId() )
                .leftJoin(transporterKTable,
                        (leftValue, rightValue) -> new EnrichedTransporterCommand(leftValue.command,null,null,rightValue,leftValue.group),
                        stringSerde,EnrichedTransporterCommandSerde);

        KStream<String,Command> updatedTransporter=joinedGroupSuccessKStream.mapValues(value->{
            Transporter transporter=value.getTransporter();

            List<Groups> transporterGroups=transporter.getGroups();
            if(transporterGroups==null)
                transporterGroups=new ArrayList<>();
            Map<String,Groups> groupsMap=new HashMap<>();
            for(Groups group:transporterGroups)
            {
                groupsMap.put(group.groupId,group);
            }
            groupsMap.remove(value.group.groupId);
            transporterGroups=new ArrayList<>(groupsMap.values());
            transporter.setGroups(transporterGroups);
            Command command= new Command();
            command.setType("transporter.updated");
            command.setData(ByteBuffer.wrap(transporterSpecificAvroSerde.serializer().serialize(Context.getConfig().getString(Constants.KEY_TRANSPORTER_TOPIC),transporter)));
            command.setId(value.command.getId());
            command.setProcessTime(System.currentTimeMillis());
            command.setStatusCode(200);
            return command;
        }).selectKey((key,value)-> value.getId());
        updatedTransporter.print("Transporter Updated:");
        updatedTransporter.to(stringSerde, commandSpecificAvroSerde, Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));

    }
    public void groupCreateFailed(KStream<String,EnrichedTransporterCommand> stream,String errorMessage)
    {
        /*
         Send error message if transporter id doesn't match
        */
        KStream<String,Command> groupCreateFailed=stream.mapValues((values) -> {
            Command command = new Command();
            command.setType("group.create.failed");
            command.setStatusCode(404);
            command.setErrorMessage(errorMessage);
            command.setId(values.command.getId());
            command.setProcessTime(System.currentTimeMillis());
            command.setData(values.command.getData());
            command.setStartTime(values.command.getStartTime());

            return command;
        }).selectKey((k, v) -> v.getId());
        groupCreateFailed.to(stringSerde, commandSpecificAvroSerde, Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));
        groupCreateFailed.print("Create failed:");
    }

    public void groupUpdateFailed(KStream<String,EnrichedTransporterCommand> stream,String errorMessage)
    {

        KStream<String,Command> groupUpdateFailed=stream.mapValues((values) -> {
            Command command = new Command();
            command.setType("group.update.failed");
            command.setStatusCode(404);
            command.setErrorMessage(errorMessage);
            command.setId(values.command.getId());
            command.setProcessTime(System.currentTimeMillis());
            command.setData( values.command.getData());
            return command;
        }).selectKey((k, v) -> v.getId());
        groupUpdateFailed.to(stringSerde, commandSpecificAvroSerde, Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));
        groupUpdateFailed.print("update failed:");

    }
    public void groupDeleteFailed(KStream<String,EnrichedTransporterCommand> stream,String errorMessage)
    {

        KStream<String,Command> groupDeleteFailed=stream.mapValues((values) -> {
            Command command = new Command();
            command.setType("group.delete.failed");
            command.setStatusCode(404);
            command.setErrorMessage(errorMessage);
            command.setId(values.command.getId());
            command.setProcessTime(System.currentTimeMillis());
            command.setData( values.command.getData());
            return command;
        }).selectKey((k, v) -> v.getId());
        groupDeleteFailed.to(stringSerde, commandSpecificAvroSerde, Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));
        groupDeleteFailed.print("delete failed:");

    }

}

