package com.fretron.transporter.TransporterManager;

import com.fretron.Context;
import com.fretron.Model.Command;
import com.fretron.Model.Transporter;
import com.fretron.Utils.PropertiesUtil;
import com.fretron.constants.Constants;
import com.fretron.Utils.SpecificAvroSerde;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

/**
 * Created by anurag on 14-Sep-17.
 */
public class TransporterManager {


    public void  startStream()
    {
        final Properties streamsConfiguration = PropertiesUtil.initializeProperties(Context.getConfig().getString(Constants.KEY_TRANSPORTER_APP_ID), Context.getConfig().getString(Constants.KEY_SCHEMA_REGISTRY_URL), Context.getConfig().getString(Constants.KEY_BOOTSTRAP_SERVERS), Context.getConfig());

        // declaring all serdes
        final CachedSchemaRegistryClient schemaRegistry = new CachedSchemaRegistryClient(Context.getConfig().getString(Constants.KEY_SCHEMA_REGISTRY_URL), 100);
        final Map<String, String> serdeProps = Collections.singletonMap("schema.registry.url", Context.getConfig().getString(Constants.KEY_SCHEMA_REGISTRY_URL));
        //final SpecificAvroSerde<JoinedVehicleAndDevice> joinedCommandVehicleAndDeviceSerde = new SpecificAvroSerde<JoinedVehicleAndDevice>(schemaRegistry,serdeProps);
        final SpecificAvroSerde<Transporter> vehicleImeiSerde = new SpecificAvroSerde<>(schemaRegistry, serdeProps);
        vehicleImeiSerde.configure(serdeProps, false);
        final SpecificAvroSerde<Command> commandSerde = new SpecificAvroSerde<>(schemaRegistry, serdeProps);
        commandSerde.configure(serdeProps, false);
        Serde<String> stringSerde = Serdes.String();
        KStreamBuilder streamBuilder = new KStreamBuilder();

        KStream<String, Command> commandKStream = streamBuilder.stream(stringSerde, commandSerde, Context.getConfig().getString(Constants.KEY_COMMAND_TOPIC));



    }
}
