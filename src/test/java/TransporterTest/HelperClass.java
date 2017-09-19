package TransporterTest;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

import java.util.Properties;

public class HelperClass {

    public  static  <K,V>Producer<K,V> getProducer(String bootStrapServer, String schemaRegistry){
        Properties props = new Properties();
        props.put("bootstrap.servers", bootStrapServer);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        props.put("schema.registry.url", schemaRegistry);
        props.put("value.serializer",KafkaAvroSerializer.class) ;
        return new KafkaProducer<>(props);
    }
}
