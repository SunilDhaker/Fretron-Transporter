package com.fretron.transporter.LaneManager;

import com.fretron.Context;
import com.fretron.Model.Command;
import com.fretron.Model.Lane;
import com.fretron.Utils.PropertiesUtil;
import com.fretron.Utils.SpecificAvroSerde;
import com.fretron.constants.Constants;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import org.apache.kafka.streams.kstream.KStreamBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public class LaneManagerDriver {
    public static void main(String args[]) throws Exception {
        Context.init(args);

        Properties streamConfig= PropertiesUtil.initializeProperties(
                Context.getConfig().getString(Constants.KEY_APPLICATION_ID),
                Context.getConfig().getString(Constants.KEY_SCHEMA_REGISTRY_URL),
                Context.getConfig().getString(Constants.KEY_BOOTSTRAP_SERVERS),
                Context.getConfig()
        );

        final CachedSchemaRegistryClient schemaRegistryClient = new CachedSchemaRegistryClient(Context.getConfig().getString(Constants.KEY_SCHEMA_REGISTRY_URL),100);
        Map<String,String> map= Collections.singletonMap("schema.registry.url",Context.getConfig().getString(Constants.KEY_SCHEMA_REGISTRY_URL));

        SpecificAvroSerde<Command> commandSpecificAvroSerde=new SpecificAvroSerde<>(schemaRegistryClient,map);
        commandSpecificAvroSerde.configure(map,false);

        SpecificAvroSerde<Lane> laneSpecificAvroSerde=new SpecificAvroSerde<>(schemaRegistryClient,map);
        laneSpecificAvroSerde.configure(map,false);

        KStreamBuilder builder=new KStreamBuilder();

        new LaneManager().createLane(builder,commandSpecificAvroSerde,laneSpecificAvroSerde,streamConfig)
                .start();
    }
}
