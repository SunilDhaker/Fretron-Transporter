package com.fretron.Utils;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import org.apache.avro.specific.SpecificRecord;

import java.util.HashMap;

/**
 * Created by PalodRohit on 7/11/2017.
 */
public class SerdeUtils {

    public static <T extends SpecificRecord> SpecificAvroSerde<T> createSerde(String schemaRegistryUrl) {
        final CachedSchemaRegistryClient schemaRegistry = new CachedSchemaRegistryClient(schemaRegistryUrl, 100);

        HashMap<String, String> serdeProps = new HashMap<>();
        serdeProps.put("schema.registry.url", schemaRegistryUrl);
        serdeProps.put("specific.avro.reader", "true");
        SpecificAvroSerde<T> newSerde = new SpecificAvroSerde<T>(schemaRegistry, serdeProps);
        newSerde.configure(serdeProps, false);
        return newSerde;
    }

}
