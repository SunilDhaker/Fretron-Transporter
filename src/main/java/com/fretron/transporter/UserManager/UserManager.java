package com.fretron.transporter.UserManager;

import com.fretron.Context;
import com.fretron.Utils.SpecificAvroSerde;
import com.fretron.constants.Constants;
import com.fretron.Model.Command;
import com.fretron.Model.User;
import com.fretron.constants.ErrorMessages;
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

//       commandKStream.mapValues((values)-> userSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_USERS_TOPIC),values.getData().array()))
//                .to(Serdes.String(),userSpecificAvroSerde,Context.getConfig().getString(Constants.KEY_USERS_TOPIC));

        KStream<String,User> existingUserKS=builder.stream(Serdes.String(),userSpecificAvroSerde,
                Context.getConfig().getString(Constants.KEY_USERS_TOPIC));

        existingUserKS.print("Existing customers");
        // user create topology
        KStream<String,Command> createUserCommandKStream=commandKStream
                .filter((key,value)->value.getType().contains("create"))
                .selectKey((key,value)->userSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_USERS_TOPIC),value.getData().array()).getEmail());

        createUserCommandKStream.print("create ::");

        KTable<String,User> userByEmailKTable=existingUserKS.selectKey((key, value)->value.getEmail())
                .groupByKey(Serdes.String(),userSpecificAvroSerde).reduce((value,aggValue)->aggValue,Context.getConfig().getString(Constants.KEY_USER_BYEMAIL_STORE));

        KStream<String,UserAndCommand> joinedKStream = createUserCommandKStream.leftJoin(userByEmailKTable,
                (leftValue,rightValue)->new UserAndCommand(rightValue,leftValue),
                Serdes.String(),commandSpecificAvroSerde);

        KStream<String,UserAndCommand>[] branchedKS=joinedKStream.branch((key,value)->value.user!=null,
                (key,value)->value.user==null);

        branchedKS[0].mapValues((values)->{
            Command command=new Command();
            command.setType("user.create.failed");
            command.setErrorMessage(ErrorMessages.USER_EMAIL_ALREADY_EXIST);
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



        //update customer topology
        KStream<String,Command> updateUserCommandKStream=commandKStream
                .filter((key,value)->value.getType().contains("update"))
                .selectKey((key,value)->userSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_USERS_TOPIC),value.getData().array()).getEmail());

        KStream<String,UserAndCommand> joinedUserUpdateKStream = updateUserCommandKStream.leftJoin(userByEmailKTable,
                (leftValue,rightValue)->new UserAndCommand(rightValue,leftValue),
                Serdes.String(),commandSpecificAvroSerde);

        KStream<String,UserAndCommand>[] branchedUserUpdateStream=joinedUserUpdateKStream.branch((key,value)->value.user==null || value.user.isDeleted!=null,
                (key,value)->value.user!=null || value.user.isDeleted==null );
        KStream<String,Command> errorUserUpdateStream= branchedUserUpdateStream[0]
        .mapValues((values)->{
            Command command=new Command();
            command.setType("user.update.failed");
            command.setErrorMessage(ErrorMessages.USER_EMAIL_NOT_EXIST);
            command.setData( values.command.getData());
            command.setId(values.command.getId());
            command.setProcessTime(System.currentTimeMillis());
            command.setStatusCode(404);
            return command;
        }).selectKey((key,value)->value.getId());
        errorUserUpdateStream.print();
        errorUserUpdateStream.to(Serdes.String(),commandSpecificAvroSerde,Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));


        KStream<String,Command> okUserUpdateStream=branchedUserUpdateStream[1].mapValues((value)->{
            User newUser=userSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_USERS_TOPIC),value.command.getData().array());

            User oldUser=value.user;
            Command command=new Command();
            command.setId(value.command.getId());
            command.setStatusCode(200);
            command.setProcessTime(System.currentTimeMillis());
            command.setType("user.update.success");
            command.setErrorMessage("");
            if(newUser.mobile!=null){
                oldUser.setMobile(newUser.getMobile());
            }
            if(newUser.name!=null){
                oldUser.setName(newUser.getName());
            }
            command.setData( ByteBuffer.wrap(userSpecificAvroSerde.serializer().serialize("user",oldUser)));
            return command;
        }).selectKey((key,value)->value.getId());
        okUserUpdateStream.print();
        okUserUpdateStream.to(Serdes.String(),commandSpecificAvroSerde,Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC));


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

