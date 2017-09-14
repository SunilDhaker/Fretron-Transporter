package com.fretron.transporter.UserManager;


import com.fretron.transporter.Context;
import com.fretron.transporter.Model.Command;
import com.fretron.transporter.Model.User;
import com.fretron.transporter.Utils.SpecificAvroSerde;
import com.fretron.transporter.constants.Constants;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.processor.StateStoreSupplier;
import org.apache.kafka.streams.state.Stores;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public class UserManager {
    public String stateStore;

    //get statestore

    KStreamBuilder builder = new KStreamBuilder();
    Properties properties= getProperties();
    SchemaRegistryClient schemaRegistryClient=new CachedSchemaRegistryClient(Context.getConfig().getString(Constants.KEY_SCHEMA_REGISTRY_URL),10);

    final Map<String, String> serdeProps = Collections.singletonMap("schema.registry.url", Context.getConfig().getString(Constants.KEY_SCHEMA_REGISTRY_URL));
    SpecificAvroSerde<User> userSpecificAvroSerde=new SpecificAvroSerde<>(schemaRegistryClient, serdeProps);

    SpecificAvroSerde<Command> commandSpecificAvroSerde=new SpecificAvroSerde<>(schemaRegistryClient, serdeProps);

    public KafkaStreams startStream() {

        //configure stores
        userSpecificAvroSerde.configure(serdeProps,false);
        commandSpecificAvroSerde.configure(serdeProps,false);

        //create state store
        stateStore=Context.getConfig().getString(Constants.KEY_USER_STATESTORE);
        StateStoreSupplier userStateStore= Stores.create(stateStore).withStringKeys().withValues(userSpecificAvroSerde).persistent().build();

        builder.addStateStore(userStateStore);

    KStream<String,User> userKStream=UserCommandHandler.getUserKStream(builder,commandSpecificAvroSerde,userSpecificAvroSerde);

    userKStream.transform(new UserTransformer(),stateStore);

    KafkaStreams kafkaStreams=new KafkaStreams(builder,properties);

    return kafkaStreams;
  }

    public Properties getProperties() {
        Properties properties=new Properties();

        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, Context.getConfig().getString(Constants.KEY_APPLICATION_ID));
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,Context.getConfig().getString(Constants.KEY_BOOTSTRAP_SERVERS));
        properties.put(StreamsConfig.APPLICATION_SERVER_CONFIG,Context.getConfig().getString(Constants.KEY_APPLICATION_SERVER));
        return properties;
    }
}
