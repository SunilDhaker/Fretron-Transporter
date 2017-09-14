package com.fretron.transporter.UserManager;

import com.fretron.Context;
import com.fretron.Utils.SpecificAvroSerde;
import com.fretron.constants.Constants;
import com.fretron.transporter.Model.Command;
import com.fretron.transporter.Model.User;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;

import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.UUID;

public class UserManager {
    public KafkaStreams startStream(KStreamBuilder builder, SpecificAvroSerde<User> userSpecificAvroSerde, SpecificAvroSerde<Command> commandSpecificAvroSerde, Properties properties) {
        KStream<String,Command> commandKStream=builder
                .stream(Serdes.String(),commandSpecificAvroSerde, Context.getConfig().getString(Constants.KEY_COMMAND_TOPIC))
                .filter((key,value)->value.getType().contains("user"));

        //commandKStream.print("commandKS :");

//        commandKStream.mapValues((values)-> userSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_USERS_TOPIC),values.getData().array()))
//                .to(Serdes.String(),userSpecificAvroSerde,Context.getConfig().getString(Constants.KEY_USERS_TOPIC));

        KStream<String,User> existingUserKS=builder.stream(Serdes.String(),userSpecificAvroSerde,
                Context.getConfig().getString(Constants.KEY_USERS_TOPIC));

        existingUserKS.print("Existing customers");
        // user create topology
        KStream<String,Command> createUserCommandKStream=commandKStream
                .filter((key,value)->value.getType().contains("create"))
                .selectKey((key,value)->userSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_USERS_TOPIC),value.getData().array()).getEmail());

        createUserCommandKStream.print("create ::");

        KTable<String,User> userKTable=existingUserKS.selectKey((key, value)->value.getEmail())
                .groupByKey(Serdes.String(),userSpecificAvroSerde).reduce((value,aggValue)->aggValue,Context.getConfig().getString(Constants.KEY_USER_BYEMAIL_STORE));

        userKTable.print("userKTable");

        KStream<String,UserAndCommand> joinedKStream = createUserCommandKStream.leftJoin(userKTable,
                (leftValue,rightValue)->new UserAndCommand(rightValue,leftValue),
                Serdes.String(),commandSpecificAvroSerde);

        KStream<String,UserAndCommand>[] branchedKS=joinedKStream.branch((key,value)->value.user!=null,
                (key,value)->value.user==null);

        branchedKS[0].mapValues((values)->{
            Command command=new Command();
            command.setType("user.create.failed");
            command.setErrorMessage("email already Exist");
            command.setData(ByteBuffer.wrap(userSpecificAvroSerde.serializer().serialize(Context.getConfig().getString(Constants.KEY_USERS_TOPIC),values.user)));
            command.setId(values.command.getId());
            command.setProcessTime(System.currentTimeMillis());
            command.setStatusCode(404);
            return command;
        }).selectKey((key,value)->value.getId()).to(Serdes.String(),commandSpecificAvroSerde,Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));

    KStream<String,UserAndCommand> userCreatedStream = branchedKS[1].mapValues((values)-> {
                User user = userSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_USERS_TOPIC), values.command.getData().array());
                user.setUserId(UUID.randomUUID().toString());

                values.command.setData(ByteBuffer.wrap(userSpecificAvroSerde.serializer().serialize(Context.getConfig().getString(Constants.KEY_USERS_TOPIC),user)));

                UserAndCommand userAndCommand=new UserAndCommand(user,values.command);
                return userAndCommand;
            });


    userCreatedStream.mapValues((values)->{
        User user=userSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_USERS_TOPIC),values.command.getData().array());

        return user;
    }).to(Serdes.String(),userSpecificAvroSerde,Context.getConfig().getString(Constants.KEY_USERS_TOPIC));

        userCreatedStream.mapValues((values)->{
            Command command=new Command();
            command.setId(values.command.getId());
            command.setStatusCode(200);
            command.setProcessTime(System.currentTimeMillis());
            command.setData(values.command.getData());
            command.setType("user.create.success");
            command.setErrorMessage("");

            return command;
        }).selectKey((key,value)->value.getId())
                .to(Serdes.String(),commandSpecificAvroSerde,Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));

        KafkaStreams kafkaStreams=new KafkaStreams(builder,properties);
        return kafkaStreams;
    }

    static class UserAndCommand {
        Command command;
        User user;
        UserAndCommand(User user,Command command) {
            this.user=user;
            this.command=command;
        }
    }
}

