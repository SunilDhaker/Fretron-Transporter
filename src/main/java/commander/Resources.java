package commander;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fretron.transporter.Model.Command;
import com.fretron.transporter.Model.User;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by anurag on 13-Sep-17.
 */
@Path("home")
@Singleton
public class Resources {
    ObjectMapper objectMapper = new ObjectMapper();
    KafkaProducer<String, Command> producer;
    SchemaRegistryClient registryClient;

    public Resources() {
        try {

            Properties props = new Properties();
            props.put("bootstrap.servers", "localhost:9092");
            props.put("acks", "all");
            props.put("retries", 0);
            props.put("batch.size", 16384);
            props.put("linger.ms", 1);
            props.put("buffer.memory", 33554432);
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("schema.registry.url", "http://localhost:8081");
            props.put("value.serializer", KafkaAvroSerializer.class);
            registryClient = new CachedSchemaRegistryClient("http://localhost:8081", 1000);
            producer = new KafkaProducer<String, Command>(props);


        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    @POST
    @Path("command")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getCommands(String jsonCommands)
    {
        Command command = new Command();
        try {

            JsonNode node = objectMapper.readTree(jsonCommands);
            String type = node.get("type").asText();
            if (type.contains("user")) {
                String data = node.get("data").toString();
                Logger.getGlobal().log(Level.INFO, data);
                User user = objectMapper.readValue(data, User.class);
                command.setType(type);
                byte[] bytes = (new KafkaAvroSerializer(registryClient)).serialize("transport", user);
                command.setData(ByteBuffer.wrap(bytes));

            }


        } catch (Throwable t) {
            //e.printStackTrace();
            t.printStackTrace();
        }
        command.setId(UUID.randomUUID().toString());


        try {
            ProducerRecord<String, Command> record = new ProducerRecord<String, Command>("command1", UUID.randomUUID().toString(), command);
            producer.send(record);

        } catch (Exception e) {
            e.printStackTrace();
        }
        String dataString = command.toString();
        System.out.println(dataString);
        return "{\"Succcess\" : \" OK\" }";


    }


}