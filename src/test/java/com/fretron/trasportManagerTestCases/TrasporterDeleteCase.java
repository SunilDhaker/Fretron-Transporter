package com.fretron.trasportManagerTestCases;

import TransporterTest.AssertClass;
import Util.EmbeddedSingleNodeKafkaCluster;
import Util.IntegrationTestUtils;
import com.fretron.Context;
import com.fretron.Model.Command;
import com.fretron.Model.Transporter;
import com.fretron.Utils.SerdeUtils;
import com.fretron.Utils.SpecificAvroSerde;
import com.fretron.constants.Constants;
import com.fretron.transporter.TransporterManager.TransporterManager;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by anurag on 21-Sep-17.
 */
public class TrasporterDeleteCase {


    @ClassRule
    public static final EmbeddedSingleNodeKafkaCluster CLUSTER=new EmbeddedSingleNodeKafkaCluster();
    private static  String commandResultTopic,commandTopic,transporterTopic;

    @BeforeClass
    public static void startCluster() throws Exception {

        String[] args={new File("dev.xml").getAbsolutePath()};
        Context.init(args);

        commandResultTopic = Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC);
        commandTopic = Context.getConfig().getString(Constants.KEY_COMMAND_TOPIC);
        transporterTopic=Context.getConfig().getString(Constants.KEY_TRANSPORTER_TOPIC);

        CLUSTER.createTopic(commandResultTopic);
        CLUSTER.createTopic(commandTopic);
        CLUSTER.createTopic(transporterTopic);
    }

    @Test
    public void testTransporterDelete() throws Exception {

        KafkaStreams streams= new TransporterManager().createStream(CLUSTER.schemaRegistryUrl(),CLUSTER.bootstrapServers());

        new Thread(()->{
            streams.cleanUp();
            streams.start();
        }).start();


        new Thread(()-> {


            SpecificAvroSerde<Transporter> transporterManagerSpecificAvroSerde = SerdeUtils.createSerde(CLUSTER.schemaRegistryUrl());

            ArrayList<String> adminEmailList = new ArrayList<>();
            adminEmailList.add("AA@gmail.com");
            adminEmailList.add("BB@gmail.com");

            //create

            Transporter transporterCreate = new Transporter("1234", adminEmailList, null, false);

            String commandId = UUID.randomUUID().toString();


            Command command = new Command();
            command.setId(commandId);
            command.setStatusCode(200);
            command.setStartTime(System.currentTimeMillis());
            command.setType("transporter.created");
            command.setErrorMessage(null);
            command.setData(ByteBuffer.wrap(transporterManagerSpecificAvroSerde.serializer().serialize(transporterTopic, transporterCreate)));
            Producer<String, Command> commandProducer = getProducer(CLUSTER.bootstrapServers(), CLUSTER.schemaRegistryUrl());
            Future<RecordMetadata> md = commandProducer.send(new ProducerRecord<String, Command>(commandResultTopic, "key", command));
            try {
                System.out.println(md.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }


//delete Command-->

            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Transporter transporterUpdate = new Transporter("1234", null, null, false);

            Command commandUpdate = new Command();
            commandUpdate.setId(UUID.randomUUID().toString());
            commandUpdate.setType("transporter.delete.command");
            commandUpdate.setData(ByteBuffer.wrap(transporterManagerSpecificAvroSerde.serializer().serialize(transporterTopic, transporterUpdate)));
            Producer<String, Command> commandProducerUP = getProducer(CLUSTER.bootstrapServers(), CLUSTER.schemaRegistryUrl());
            Future<RecordMetadata> mdUP = commandProducerUP.send(new ProducerRecord<String, Command>(commandTopic, "key", commandUpdate));
            try {
                System.out.println(mdUP.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }).start();

        List<Command> actual =  IntegrationTestUtils.waitUntilMinValuesRecordsReceived(getConsumerProps("my-test-transporter" , CLUSTER) ,commandResultTopic,2, 120000);
        for (int index = 0 ; index<actual.size() ; index++){
            System.out.println(actual.get(index).toString());
        }

        assert (AssertClass.assertTestActualData(actual,"transporter.deleted",2));
    }

    public static <k,v> Producer<k , v> getProducer(String bootstrapServer,String schemaRegistry){

        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServer);
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

    public Properties getConsumerProps(String groupId , EmbeddedSingleNodeKafkaCluster CLUSTER)
    {
        final Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, CLUSTER.bootstrapServers());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, Serdes.String().deserializer().getClass());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        consumerProps.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, CLUSTER.schemaRegistryUrl());
        consumerProps.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
        consumerProps.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, CLUSTER.schemaRegistryUrl());
        consumerProps.put("specific.avro.reader", "true");
        return consumerProps;
    }




}
