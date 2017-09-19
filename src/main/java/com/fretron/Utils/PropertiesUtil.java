package com.fretron.Utils;

import com.fretron.Config;
import com.fretron.constants.Constants;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.streams.StreamsConfig;

import java.util.Properties;

/**
 * Created by PalodRohit on 7/11/2017.
 */
public class PropertiesUtil {

    /**
     * @param applicationId : Application id for stream App
     * @return properties for Stream
     */
    public static Properties initializeProperties(String applicationId, String schemaRegisty, String bootstrapServer, Config config) {

        final Properties streamsConfiguration = new Properties();
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        streamsConfiguration.put(StreamsConfig.APPLICATION_SERVER_CONFIG, config.getString(Constants.KEY_APPLICATION_SERVER));
        // streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, stateDir);
        streamsConfiguration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, config.getString(Constants.KEY_AUTO_OFFSET_RESET_CONFIG));
        streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, config.getString(Constants.KEY_COMMIT_INTERVAL_AUTO_CONFIG));

        return streamsConfiguration;
    }
}
