package com.transporter.UserManager;

import com.transporter.Context;
import com.transporter.Model.Command;
import com.transporter.Model.User;
import com.transporter.Utils.SpecificAvroSerde;
import com.transporter.constants.Constants;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;

public class UserCommandHandler {

   public static KStream getUserKStream(KStreamBuilder builder, SpecificAvroSerde<Command> commandSerde,SpecificAvroSerde<User> userSerde) {
      KStream<String,Command> commandKStream=builder
               .stream(Serdes.String(),commandSerde,Context.getConfig().getString(Constants.KEY_COMMAND_TOPIC))
              .filter((key,value)->value.getType().contains("user.create.command"));

      commandKStream.mapValues((values)->{
         User user=userSerde.deserializer().deserialize(Context.getConfig().getString(Constants.KEY_USERS_TOPIC),values.getData().array());

         return user;
      });
      return commandKStream;
    }
}
