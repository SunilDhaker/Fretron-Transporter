package commander;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fretron.Context;
import com.fretron.transporter.Model.Command;
import com.fretron.transporter.Model.User;
import com.fretron.Utils.SpecificAvroDeserializer;
import com.fretron.constants.Constants;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.glassfish.jersey.server.ManagedAsync;
import org.json.JSONObject;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
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
    KafkaConsumer<String, Command> consumer;
    SchemaRegistryClient registryClient;
    Runnable r;

    HashMap<String, AsyncResponse> pendingResponces = new HashMap<>();
    ArrayList<String> keys;
    HashMap<String,String> className;
    HashMap<String,String> topicName;
    public static String COMMAND_TOPIC,COMMAND_RESULT_TOPIC;

    public Resources() {
        className= new HashMap<>();
        COMMAND_TOPIC=Context.getConfig().getString(Constants.KEY_COMMAND_TOPIC);
        COMMAND_RESULT_TOPIC=Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC);

        topicName= new HashMap<>();
        keys= new ArrayList<>();
        String getallClass = Context.getConfig().getString(Constants.KEY_MODEL_CLASS);
        String[] splitBySpace = getallClass.split(" ");

        for (int i=0;i<splitBySpace.length;i++)
        {
            String[] splitByHash=splitBySpace[i].split("#");
            className.put(splitByHash[0],splitByHash[1]);
            topicName.put(splitByHash[0],splitByHash[2]);
            keys.add(splitByHash[0]);

        }

        try {

            Properties props = new Properties();
            props.put("bootstrap.servers", Context.getConfig().getString(Constants.KEY_BOOTSTRAP_SERVERS));
            props.put("acks", "all");
            props.put("retries", 0);
            props.put("batch.size", 16384);
            props.put("linger.ms", 1);
            props.put("buffer.memory", 33554432);
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("schema.registry.url", Context.getConfig().getString(Constants.KEY_SCHEMA_REGISTRY_URL));
            props.put("value.serializer", KafkaAvroSerializer.class);
            producer = new KafkaProducer<String, Command>(props);


            Properties props2 = new Properties();
            props2.put("bootstrap.servers", Context.getConfig().getString(Constants.KEY_BOOTSTRAP_SERVERS));
            props2.put("group.id", "commander");
            props2.put("acks", "all");
            //If the request fails, the producer can automatically retry,
            props2.put("retries", 0);
            //Specify buffer size in config
            props2.put("batch.size", 16384);
            //Reduce the no of requests less than 0
            props2.put("linger.ms", 1);
            //The buffer.memory controls the total amount of memory available to the producer for buffering.
            props2.put("buffer.memory", 33554432);
            props2.put("key.deserializer",
                    "org.apache.kafka.common.serialization.StringDeserializer");
            props2.put("schema.registry.url", Context.getConfig().getString(Constants.KEY_SCHEMA_REGISTRY_URL));
            props2.put("specific.avro.reader", "true");
            props2.put("value.deserializer", KafkaAvroDeserializer.class);
            registryClient = new CachedSchemaRegistryClient(Context.getConfig().getString(Constants.KEY_SCHEMA_REGISTRY_URL), 1000);
            consumer = new KafkaConsumer<String, Command>(props2);


            //Start the consumer to resolve requests
            consumer.subscribe(Arrays.asList(COMMAND_RESULT_TOPIC));
            r = () -> {
                while (true) {
                    ConsumerRecords<String, Command> records = consumer.poll(1000);
                    Iterator<ConsumerRecord<String, Command>> iterator = records.iterator();
                    while (iterator.hasNext()) {
                        ConsumerRecord<String, Command> currentRecord = iterator.next();
                        if (pendingResponces.containsKey(currentRecord.value().getId())) {
                            pendingResponces.get(currentRecord.key()).resume(
                                    parse(currentRecord.value()).toString());
                        }
                    }
                }
            };
            Thread t = new Thread(r);
            t.start();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object parse(Command value) {

        String type = value.type;
        HashMap<String, String> props2 = new HashMap<>();
        props2.put("schema.registry.url", Context.getConfig().getString(Constants.KEY_SCHEMA_REGISTRY_URL));
        props2.put("specific.avro.reader", "true");
        try {
            for(int i=0;i<keys.size();i++) {
                String k = keys.get(i);
                if (type.contains(k)) {
                    Class modelClassName=Class.forName(className.get(k));
                    SpecificRecord cod = (SpecificRecord) modelClassName.newInstance();
                    JSONObject jsonObject = new JSONObject();
                    ByteBuffer data = value.getData();
                    String topic = Context.getConfig().getString(topicName.get(k));
                    Object d = (new SpecificAvroDeserializer<>(registryClient, props2)).deserialize(topic, data.array());
                    JSONObject dataObjJson = new JSONObject(d.toString());
                    jsonObject.put(Constants.KEY_GET_TYPE,type);
                    jsonObject.put(k,dataObjJson);
                    jsonObject.put(Constants.KEY_GET_STATUS,value.getStatusCode());
                    jsonObject.put(Constants.KEY_GET_ERROR,value.getErrorMessage());
                    return jsonObject;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
        return value;

    }

    @POST
    @Path("commandSync")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    @ManagedAsync
    public void commandSyn(String jsonCommand, final @Suspended AsyncResponse ar) {
        Command commandBuilder = new Command();
        try {
            String data;
            JsonNode node = objectMapper.readTree(jsonCommand);
            String type = node.get("type").asText();
            data = node.get("data").toString();
            Logger.getGlobal().log(Level.INFO, data);
            for(int i=0;i<keys.size();i++) {
                String k = keys.get(i);
                if (type.contains(k)) {
                    String topic = Context.getConfig().getString(topicName.get(k));
                    String classN= className.get(k);
                    byte[] bytes = genericDataSerializerForClassAndTopic(topic,classN,data);
                    commandBuilder.setType(type);
                    commandBuilder.setData(ByteBuffer.wrap(bytes));
                    break;
                }
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }
        commandBuilder.setId(UUID.randomUUID().toString());


        try {
            ProducerRecord<String, Command> record = new ProducerRecord<>(COMMAND_TOPIC, UUID.randomUUID().toString(), commandBuilder);
            System.out.println(record.toString());
            producer.send(record);
            pendingResponces.put(commandBuilder.getId(), ar);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public  byte[] genericDataSerializerForClassAndTopic(String topic,String className,String data) throws ClassNotFoundException, IOException {
        Class classObject=Class.forName(className);
        Object object = objectMapper.readValue(data,classObject);
        System.out.println("Type of object is : " + object.getClass().getName());
        System.out.println("Is Instance of SpecificAvroRecord : " + (object instanceof SpecificRecord));
        byte[] bytes = (new KafkaAvroSerializer(registryClient)).serialize(topic, object);
        return  bytes;
    }

}


