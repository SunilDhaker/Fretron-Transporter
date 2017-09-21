package com.fretron.transporter.TransporterManager;

import com.fretron.Context;
import com.fretron.constants.Constants;
import org.apache.kafka.streams.KafkaStreams;

/**
 * Created by anurag on 14-Sep-17.
 */
public class TransporterManagerDriver {

    public static void main(String[] args) throws Exception {
        Context.init(args);
        final String schemaRegistryUrl = Context.getConfig().getString(Constants.KEY_SCHEMA_REGISTRY_URL);
        final String bootstrapServer = Context.getConfig().getString(Constants.KEY_BOOTSTRAP_SERVERS);
        KafkaStreams streams= new TransporterManager().createStream(schemaRegistryUrl,bootstrapServer);
        streams.start();


    }
}
