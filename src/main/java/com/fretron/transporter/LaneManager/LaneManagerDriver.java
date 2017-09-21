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
        final String schemaRegistry = Context.getConfig().getString(Constants.KEY_SCHEMA_REGISTRY_URL);
        final String bootstrapServer = Context.getConfig().getString(Constants.KEY_BOOTSTRAP_SERVERS);

        new LaneManager().createLane(schemaRegistry,bootstrapServer)
                .start();
    }
}
