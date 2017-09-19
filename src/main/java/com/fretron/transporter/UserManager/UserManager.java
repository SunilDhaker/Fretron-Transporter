package com.fretron.transporter.UserManager;

import com.fretron.Context;
import com.fretron.Model.*;
import com.fretron.Utils.PropertiesUtil;
import com.fretron.Utils.SerdeUtils;
import com.fretron.Utils.SpecificAvroSerde;
import com.fretron.constants.Constants;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Properties;
import java.util.UUID;

public class UserManager {
    public KafkaStreams startStream(String bootStrapServers, String schemaRegistry) {


        KStreamBuilder builder = new KStreamBuilder();
        Properties properties = PropertiesUtil.initializeProperties(Context.getConfig().getString(Constants.KEY_APPLICATION_ID), schemaRegistry, bootStrapServers, Context.getConfig());


        //Serdes
        SpecificAvroSerde<Command> commandSpecificAvroSerde = SerdeUtils.createSerde(schemaRegistry);
        SpecificAvroSerde<Transporter> transporterSerde = SerdeUtils.createSerde(schemaRegistry);
        SpecificAvroSerde<User> userSpecificAvroSerde = SerdeUtils.createSerde(schemaRegistry);
        SpecificAvroSerde<CommandOfUserGroupsAndTransporter> commandOfUserGroupsAndTransporterSerde = SerdeUtils.createSerde(schemaRegistry);
        SpecificAvroSerde<Groups> groupsSerde = SerdeUtils.createSerde(schemaRegistry);


/*
user stream from command topic
 */
        KStream<String, Command> commandKStream = builder
                .stream(Serdes.String(), commandSpecificAvroSerde, Context.getConfig().getString(Constants.KEY_COMMAND_TOPIC))
                .filter((key, value) -> value.getType().contains("user"));

        commandKStream.print("command ks : ");

        /*

         */
        KStream<String, CommandOfUserGroupsAndTransporter> commandOfUserGroupsAndTransporterKStream = commandKStream
                .mapValues((values) -> {
                    CommandOfUserGroupsAndTransporter userAndCommand = new CommandOfUserGroupsAndTransporter();
                    userAndCommand.setCommand(values);
                    userAndCommand.setUser(userSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_USERS_TOPIC), values.getData().array()));

                    return userAndCommand;
                });

        /*
        commandresult stream from  commandresult topic
         */
        KStream<String, Command> commandResultKS = builder.stream(Serdes.String(), commandSpecificAvroSerde, Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));


        /*
     KTable of existing users
      */
        KTable<String, User> userKTable = commandResultKS
                .filter((key, value) -> value.getType().contains("user") && value.getStatusCode() == 200)
                .mapValues((value) -> userSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_USERS_TOPIC), value.getData().array()))
                .selectKey((key, value) -> value.getEmail())
                .groupByKey(Serdes.String(), userSpecificAvroSerde)
                .reduce((value, aggValue) -> aggValue, Context.getConfig().getString(Constants.KEY_USER_BYEMAIL_STORE));


        KStream<String, CommandOfUserGroupsAndTransporter> joinedKStreamCommandAndUser = commandOfUserGroupsAndTransporterKStream
                .filter((key, value) -> value.command.getType().contains("user.create"))
                .selectKey((key, value) -> value.user.getEmail())
                .leftJoin(userKTable,
                        (leftValue, rightValue) -> new CommandOfUserGroupsAndTransporter(leftValue.command, rightValue, leftValue.transporter, leftValue.group),
                        Serdes.String(), commandOfUserGroupsAndTransporterSerde);


        /*
        branch joined kstream
         */
        KStream<String, CommandOfUserGroupsAndTransporter>[] branchedKStreamCommandUser = joinedKStreamCommandAndUser
                .branch((key, value) -> value.user == null || value.user.isDeleted,
                        (key, value) -> value.user != null && value.user.isDeleted == false);


        /*
        Error message if user doesn't exist
         */
        branchedKStreamCommandUser[1].mapValues((value) -> {
            Command command = new Command();
            command.setId(value.command.getId());
            command.setStartTime(value.command.getStartTime());
            command.setProcessTime(System.currentTimeMillis());
            command.setErrorMessage("user already exist");
            command.setData(value.command.getData());
            command.setStatusCode(404);
            command.setType("user.create.failed");

            return command;
        }).selectKey((key, value) -> value.getId())
                .to(Serdes.String(), commandSpecificAvroSerde, Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));

       /*
       get transport stream from commandresult stream
        */
        KStream<String, Transporter> transporterKStream = commandResultKS
                .filter((key, value) -> value.getType().contains("transporter") && value.getStatusCode() == 200)
                .mapValues((value) -> transporterSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_TRANSPORTER_TOPIC), value.getData().array()));

        KTable<String, Transporter> transporterKTable = transporterKStream.selectKey((key, value) -> value.getTransporterId()).groupByKey(Serdes.String(), transporterSerde)
                .reduce((value, aggValue) -> aggValue, Context.getConfig().getString(Constants.KEY_TRANSPORTER_ID_STORE));


        KStream<String, CommandOfUserGroupsAndTransporter> joinedKStreamUserTransporter = branchedKStreamCommandUser[0]
                .selectKey((key, value) -> userSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_USERS_TOPIC), value.command.getData().array()).getTransporterId())
                .leftJoin(transporterKTable,
                        (leftValue, rightValue) -> new CommandOfUserGroupsAndTransporter(leftValue.command, leftValue.user, rightValue, leftValue.group),
                        Serdes.String(), commandOfUserGroupsAndTransporterSerde);

        /*
        branch joined k stream
         */
        KStream<String, CommandOfUserGroupsAndTransporter>[] branchedStreamUserTransporter = joinedKStreamUserTransporter
                .branch((k, v) -> v.transporter == null || v.transporter.isDeleted,
                        (k, v) -> v.transporter != null && v.transporter.isDeleted == false);

        /*
         Send error message if transporter id doesn't match
        */
        branchedStreamUserTransporter[0].mapValues((values) -> {
            Command command = new Command();
            command.setType("user.create.failed");
            command.setStatusCode(404);
            command.setErrorMessage("transporter id doesn't exist");
            command.setId(values.command.getId());
            command.setProcessTime(System.currentTimeMillis());
            command.setData(values.command.getData());
            command.setStartTime(values.command.getStartTime());

            return command;
        }).selectKey((k, v) -> v.getId()).to(Serdes.String(), commandSpecificAvroSerde, Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));

/*
   ktable of existing groups
 */
        KTable<String, Groups> groupsKTable = transporterKStream
                .flatMap((key, value) -> {
                    ArrayList<KeyValue<String, Groups>> list = new ArrayList<>();
                    for (int i = 0; i < value.getGroups().size(); i++)
                        list.add(KeyValue.pair(value.getTransporterId(), value.getGroups().get(i)));

                    return list;
                })
                .selectKey((key, value) -> value.getGroupId())
                .groupByKey(Serdes.String(), groupsSerde)
                .reduce((value, aggValue) -> aggValue, Context.getConfig().getString(Constants.KEY_GROUP_BY_ID_STORE));

        groupsKTable.print("group kTable");
/*
check whether group id provided by user exist or not
 */
        KStream<String, CommandOfUserGroupsAndTransporter> joinedKStreamWithGroup = branchedStreamUserTransporter[1]
                .selectKey((key, value) -> {
                    key = userSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_USERS_TOPIC), value.command.getData().array()).getGroupId();
                    if (key != null)
                        return key;

                    return value.transporter.getTransporterId();
                })
                .leftJoin(groupsKTable,
                        (leftValue, rightValue) -> new CommandOfUserGroupsAndTransporter(leftValue.command, leftValue.user, leftValue.transporter, rightValue),
                        Serdes.String(),
                        commandOfUserGroupsAndTransporterSerde);
/*
   Branch joinedkstream with groups to verify group exist or not
 */

        KStream<String, CommandOfUserGroupsAndTransporter>[] branchedJoinedKStream = joinedKStreamWithGroup
                .branch((key, value) -> value.group == null && userSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_USERS_TOPIC), value.command.getData().array()).getGroupId() != null,
                        (key, value) -> value.group == null && userSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_USERS_TOPIC), value.command.getData().array()).getGroupId() == null,
                        (key, value) -> value.group != null && value.transporter.isDeleted == false);

        branchedJoinedKStream[0].print("1");
        branchedJoinedKStream[1].print("2");
        branchedJoinedKStream[2].print("3");


/*
Send error message if group doesn't exist
 */
        branchedJoinedKStream[0].mapValues((value) -> {
            Command command = new Command();
            command.setStartTime(value.command.getStartTime());
            command.setData(value.command.getData());
            command.setId(value.command.getId());
            command.setProcessTime(System.currentTimeMillis());
            command.setErrorMessage("Group doesn't exist");
            command.setStatusCode(404);
            command.setType("user.create.failed");

            return command;
        }).selectKey((key, value) -> value.getId())
                .to(Serdes.String(), commandSpecificAvroSerde, Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));
/*
create user after verifying groups
 */
        createUser(branchedJoinedKStream[1], userSpecificAvroSerde, commandSpecificAvroSerde);
        createUser(branchedJoinedKStream[2], userSpecificAvroSerde, commandSpecificAvroSerde);


// user update topology
        KStream<String, CommandOfUserGroupsAndTransporter> joinKStreamToUpdate = commandOfUserGroupsAndTransporterKStream
                .filter((key, value) -> value.command.getType().contains("user.update"))
                .selectKey((key, value) -> value.user.getEmail())
                .leftJoin(userKTable,
                        (leftValue, rightValue) -> new CommandOfUserGroupsAndTransporter(leftValue.command, rightValue, leftValue.transporter, leftValue.group),
                        Serdes.String(), commandOfUserGroupsAndTransporterSerde);

/*
branch to verify existence of user
 */
        KStream<String, CommandOfUserGroupsAndTransporter>[] branchUpdateJoinKstream = joinKStreamToUpdate
                .branch((key, value) -> value.user == null || value.user.isDeleted,
                        (key, value) -> value.user != null && value.user.isDeleted == false);

/*
//Send error message if user does not exist
// */
        branchUpdateJoinKstream[0].mapValues((value) -> {
            Command command = new Command();
            command.setId(value.command.getId());
            command.setType("user.update.failed");
            command.setStatusCode(404);
            command.setErrorMessage("user not found");
            command.setData(value.command.getData());
            command.setProcessTime(System.currentTimeMillis());
            command.setStartTime(value.command.getStartTime());

            return command;
        }).selectKey((key, value) -> value.getId()).to(Serdes.String(), commandSpecificAvroSerde, Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));

/*
Update values if user exist
 */
        KStream<String, CommandOfUserGroupsAndTransporter> checkGroup = branchUpdateJoinKstream[1]
                .selectKey((key, value) -> {
                    String k = userSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_USERS_TOPIC), value.command.getData().array()).getGroupId();
                    if (k == null)
                        return value.user.getName();
                    return k;
                }).leftJoin(groupsKTable,
                        (leftValue, rigthValue) -> new CommandOfUserGroupsAndTransporter(leftValue.command, leftValue.user, leftValue.transporter, rigthValue),
                        Serdes.String(), commandOfUserGroupsAndTransporterSerde);


        /*
        Branch joined stream to verify group eistence
         */
        KStream<String, CommandOfUserGroupsAndTransporter>[] branchcheckGroupStream = checkGroup
                .branch((key, value) -> value.group == null && userSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_USERS_TOPIC), value.command.getData().array()).getGroupId() != null,
                        (key, value) -> value.group == null && userSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_USERS_TOPIC), value.command.getData().array()).getGroupId() == null,
                        (key, value) -> value.group != null);

        /*
        Send error message if group doesn't exist
         */
        branchcheckGroupStream[0].mapValues((value) -> {
            Command command = new Command();
            command.setId(value.command.getId());
            command.setType("user.update.failed");
            command.setStatusCode(404);
            command.setErrorMessage("group doesn't exist");
            command.setData(value.command.getData());
            command.setProcessTime(System.currentTimeMillis());
            command.setStartTime(value.command.getStartTime());

            return command;
        }).selectKey((key, value) -> value.getId()).to(Serdes.String(), commandSpecificAvroSerde, Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));


        /*
        update user if group exist  and send success message
         */
        updateUser(branchcheckGroupStream[1], userSpecificAvroSerde, commandSpecificAvroSerde);
        updateUser(branchcheckGroupStream[2], userSpecificAvroSerde, commandSpecificAvroSerde);

//delete user topology
        KStream<String, CommandOfUserGroupsAndTransporter> userToDeleteKStream = commandOfUserGroupsAndTransporterKStream
                .filter((key, value) -> value.command.getType().contains("user.delete"))
                .selectKey((key, value) -> value.user.getEmail())
                .leftJoin(userKTable,
                        (leftValue, rigthValue) -> new CommandOfUserGroupsAndTransporter(leftValue.command, rigthValue, leftValue.transporter, leftValue.group),
                        Serdes.String(), commandOfUserGroupsAndTransporterSerde);

/*
branch to check wether user exists or not
 */
        KStream<String, CommandOfUserGroupsAndTransporter>[] branchJoinDeleteUser = userToDeleteKStream
                .branch((key, value) -> value.user == null || value.user.isDeleted,
                        (key, value) -> value.user != null && value.user.isDeleted == false);

/*
Send error message if user does not exist
 */
        branchJoinDeleteUser[0].mapValues((value) -> {
            Command command = value.command;
            command.setErrorMessage("user not found");
            command.setType("user.delete.failed");
            command.setStatusCode(404);
            command.setProcessTime(System.currentTimeMillis());

            return command;
        }).selectKey((key, value) -> value.getId()).to(Serdes.String(), commandSpecificAvroSerde, Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));

/*
delete user if exist
 */
        branchJoinDeleteUser[1].mapValues((values) -> {
            Command command = new Command();
            User user = values.user;
            user.isDeleted = true;

            command.setId(values.command.getId());
            command.setProcessTime(System.currentTimeMillis());
            command.setStatusCode(200);
            command.setType("user.delete.success");
            command.setErrorMessage(null);
            command.setData(ByteBuffer.wrap(userSpecificAvroSerde.serializer().serialize(Context.getConfig().getString(Constants.KEY_USERS_TOPIC), user)));
            command.setStartTime(values.command.getStartTime());

            return command;
        }).selectKey((key, value) -> value.getId())
                .to(Serdes.String(), commandSpecificAvroSerde, Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));

        commandResultKS.print("command result stream : ");


        KafkaStreams kafkaStreams = new KafkaStreams(builder, properties);
        return kafkaStreams;
    }

    public void updateUser(KStream<String, CommandOfUserGroupsAndTransporter> kStream, SpecificAvroSerde<User> userSpecificAvroSerde, SpecificAvroSerde<Command> commandSpecificAvroSerde) {
        kStream.mapValues((values) -> {
            User user = userSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_USERS_TOPIC), values.command.getData().array());
            Command command = new Command();
            command.setId(values.command.getId());

            if (user.getName() == null)
                user.setName(values.user.getName());
            if (user.getMobile() == null)
                user.setMobile(values.user.getMobile());
            if (user.getGroupId() == null)
                user.setGroupId(values.user.getGroupId());

            user.setUserId(values.user.getUserId());
            user.setTransporterId(values.user.getTransporterId());

            command.setId(values.command.getId());
            command.setStartTime(values.command.getStartTime());
            command.setProcessTime(System.currentTimeMillis());
            command.setData(ByteBuffer.wrap(userSpecificAvroSerde.serializer().serialize(Context.getConfig().getString(Constants.KEY_USERS_TOPIC), user)));
            command.setErrorMessage(null);
            command.setStatusCode(200);
            command.setType("user.update.success");

            return command;
        }).selectKey((key, value) -> value.getId()).to(Serdes.String(), commandSpecificAvroSerde, Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));
    }

    public void createUser(KStream<String, CommandOfUserGroupsAndTransporter> kStream, SpecificAvroSerde<User> userSpecificAvroSerde, SpecificAvroSerde<Command> commandSpecificAvroSerde) {
        kStream.mapValues((value) -> {
            Command command = new Command();
            User user = userSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_USERS_TOPIC), value.command.getData().array());
            User user1 = new User(UUID.randomUUID().toString(), user.getName(), user.getEmail(), user.getMobile(), user.getTransporterId(), user.getGroupId(), user.getIsDeleted());

            command.setId(value.command.getId());
            command.setStatusCode(200);
            command.setErrorMessage(null);
            command.setType("user.create.success");
            command.setData(ByteBuffer.wrap(userSpecificAvroSerde.serializer().serialize(Context.getConfig().getString(Constants.KEY_USERS_TOPIC), user1)));
            command.setStartTime(value.command.getStartTime());
            command.setProcessTime(System.currentTimeMillis());

            return command;
        }).selectKey((key, value) -> value.getId())
                .to(Serdes.String(), commandSpecificAvroSerde, Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));
    }
}

