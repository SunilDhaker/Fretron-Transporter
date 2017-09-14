package com.fretron.transporter.UserManager;

import com.fretron.transporter.Context;
import com.fretron.transporter.Model.Command;
import com.fretron.transporter.Model.User;
import com.fretron.transporter.Utils.SpecificAvroSerde;
import com.fretron.transporter.constants.Constants;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;

import java.util.Properties;
import java.util.UUID;

public class UserManager1 {
    public KafkaStreams startStream(KStreamBuilder builder, SpecificAvroSerde<User> userSpecificAvroSerde,SpecificAvroSerde<Command> commandSpecificAvroSerde,Properties properties) {
        KStream<String,User> existingUserKS=builder.stream(Serdes.String(),userSpecificAvroSerde, Context.getConfig().getString(Constants.KEY_USERS_TOPIC));

        KStream<String,Command> commandKStream=builder
                .stream(Serdes.String(),commandSpecificAvroSerde,Context.getConfig().getString(Constants.KEY_COMMAND_TOPIC))
                .filter((key,value)->value.getType().contains("user"));

        // user create topology
       KStream<String,Command> createUserCommandKStream=commandKStream
               .filter((key,value)->value.getType().contains("create"));

       KTable<String,User> userKTable=existingUserKS.selectKey((key, value)->value.getEmail())
               .groupByKey().reduce((value,aggValue)->aggValue,"emailuserstore");


      KStream<String,UserAndCommand> joinedKStream = createUserCommandKStream.leftJoin(userKTable,
               (leftValue,rightValue)->new UserAndCommand(rightValue,leftValue),
       Serdes.String(),commandSpecificAvroSerde);

      KStream<String,UserAndCommand>[] branchedKS=joinedKStream.branch((key,value)->value.user!=null,
              (key,value)->value.user==null);


      branchedKS[1].mapValues((values)->{
          Command command=new Command();
          command.setType(values.command.getType());
          command.setErrorMessage("email already Exist");
          command.setData(null);
          command.setId(values.command.getId());
          command.setProcessTime(System.currentTimeMillis());
          command.setStatusCode(404);
          return command;
      }).selectKey((key,value)->value.getId()).to(Serdes.String(),commandSpecificAvroSerde,Context.getConfig().getString(Constants.KEY_COMMAND_TOPIC));

      branchedKS[0].mapValues((values)->{
          User user=userSpecificAvroSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_USERS_TOPIC),values.command.getData().array());
      return user;
      }).selectKey((key,value)-> UUID.randomUUID().toString())
              .to(Serdes.String(),userSpecificAvroSerde,Context.getConfig().getString(Constants.KEY_USERS_TOPIC));

      branchedKS[0].mapValues((values)->{
          Command command=new Command();
          command.setId(values.command.getId());
          command.setStatusCode(200);
          command.setProcessTime(System.currentTimeMillis());
          command.setData(values.command.getData());
          command.setType(command.getType());
          command.setErrorMessage(null);

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
