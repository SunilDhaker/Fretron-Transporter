package com.fretron.transporter.UserManager;

import com.fretron.Context;
import com.fretron.Model.*;
import com.fretron.Utils.SpecificAvroSerde;
import com.fretron.constants.Constants;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStreamBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public class UserManagerDriver {


    public static void main(String args[]) throws Exception {
        Context.init(args);


        Properties properties = getProperties();

        new UserManager().startStream(properties).start();
    }

    public static Properties getProperties() {
        Properties properties = new Properties();

        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, Context.getConfig().getString(Constants.KEY_APPLICATION_ID));
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, Context.getConfig().getString(Constants.KEY_BOOTSTRAP_SERVERS));
        properties.put(StreamsConfig.APPLICATION_SERVER_CONFIG, Context.getConfig().getString(Constants.KEY_APPLICATION_SERVER));
        return properties;
    }
}
