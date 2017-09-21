package com.fretron.trasportManagerTestCases;

/**
 * Created by anurag on 21-Sep-17.
 */
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
import java.util.concurrent.Future;


/**
 * Created by anurag on 15-Sep-17.
 */
public class TrasporterCreateCase {


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
    public void testTransporterCreate() throws Exception {

        KafkaStreams streams= new TransporterManager().createStream(CLUSTER.schemaRegistryUrl(),CLUSTER.bootstrapServers());
        streams.cleanUp();
        streams.start();


        //Serdes required


        SpecificAvroSerde<Transporter> transporterManagerSpecificAvroSerde= SerdeUtils.createSerde(CLUSTER.schemaRegistryUrl());

        ArrayList<String> adminEmailList=new ArrayList<>();
        adminEmailList.add("AA@gmail.com");
        adminEmailList.add("BB@gmail.com");
        adminEmailList.add("CC@gmail.com");
        adminEmailList.add("aaa@gmail.com");

        Transporter transporter = new Transporter("12",adminEmailList,null,false);

        String commandId=  UUID.randomUUID().toString();


        Command command = new Command();
        command.setId(commandId);
        command.setType("transporter.create.command");
        command.setData(ByteBuffer.wrap(transporterManagerSpecificAvroSerde.serializer().serialize(transporterTopic,transporter)));
        Producer<String, Command> commandProducer= getProducer(CLUSTER.bootstrapServers(),CLUSTER.schemaRegistryUrl());
        Future<RecordMetadata> md = commandProducer.send(new ProducerRecord<String, Command>(commandTopic , "key" , command ));
        System.out.println(md.get());


        List<Command> actual =  IntegrationTestUtils.waitUntilMinValuesRecordsReceived(getConsumerProps("my-test-transporter" , CLUSTER) ,commandResultTopic,1, 120000);
        for (int index = 0 ; index<actual.size() ; index++){
            System.out.println(actual.get(index).toString());
        }
        assert (AssertClass.assertTestActualData(actual,"transporter.created",1));
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
