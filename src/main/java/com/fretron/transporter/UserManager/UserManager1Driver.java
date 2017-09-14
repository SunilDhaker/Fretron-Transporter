package com.fretron.transporter.UserManager;

import com.fretron.transporter.Context;
import com.fretron.transporter.Model.Command;
import com.fretron.transporter.Utils.SpecificAvroSerde;
import com.fretron.transporter.constants.Constants;
import com.fretron.transporter.Model.User;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStreamBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public class UserManager1Driver {


    public static void main(String args[]) throws Exception {
        Context.init(args);

        KStreamBuilder builder = new KStreamBuilder();
        Properties properties= getProperties();
        SchemaRegistryClient schemaRegistryClient=new CachedSchemaRegistryClient(Context.getConfig().getString(Constants.KEY_SCHEMA_REGISTRY_URL),10);

        final Map<String, String> serdeProps = Collections.singletonMap("schema.registry.url", Context.getConfig().getString(Constants.KEY_SCHEMA_REGISTRY_URL));
        SpecificAvroSerde<User> userSpecificAvroSerde=new SpecificAvroSerde<>(schemaRegistryClient, serdeProps);
        SpecificAvroSerde<Command> commandSpecificAvroSerde = new SpecificAvroSerde<>(schemaRegistryClient, serdeProps);

        userSpecificAvroSerde.configure(serdeProps, false);
        commandSpecificAvroSerde.configure(serdeProps, false);

        new UserManager1().startStream(builder,userSpecificAvroSerde,commandSpecificAvroSerde,properties).start();
    }

    public static Properties getProperties() {
        Properties properties=new Properties();

        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, Context.getConfig().getString(Constants.KEY_APPLICATION_ID));
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,Context.getConfig().getString(Constants.KEY_BOOTSTRAP_SERVERS));
        properties.put(StreamsConfig.APPLICATION_SERVER_CONFIG,Context.getConfig().getString(Constants.KEY_APPLICATION_SERVER));
        return properties;
    }
}
