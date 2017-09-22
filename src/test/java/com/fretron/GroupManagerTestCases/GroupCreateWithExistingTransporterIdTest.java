package com.fretron.GroupManagerTestCases;

import UserManagerTests.AssertClass;
import UserManagerTests.HelperClass;
import Util.EmbeddedSingleNodeKafkaCluster;
import Util.IntegrationTestUtils;
import com.fretron.Context;
import com.fretron.Model.Command;
import com.fretron.Model.Groups;
import com.fretron.Model.Transporter;
import com.fretron.Utils.SerdeUtils;
import com.fretron.Utils.SpecificAvroSerde;
import com.fretron.constants.Constants;
import com.fretron.transporter.GroupManager.GroupManager;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.streams.KafkaStreams;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

public class GroupCreateWithExistingTransporterIdTest {

    /*
   Test of success creation of group if transporter id  match
    */
    @ClassRule
    public static final EmbeddedSingleNodeKafkaCluster CLUSTER=new EmbeddedSingleNodeKafkaCluster();
    private static String commandResultTopic,commandTopic,transporterTopic,transporterIdStore,groupTopic,groupByIdStore,app;
    private static String schemaRegistry,bootStrapServer;

    @BeforeClass
    public static void startCluster() throws Exception {
        Context.init(new String[]{new File("dev.xml").getAbsolutePath()});

        commandResultTopic=Context.getConfig().getString(com.fretron.constants.Constants.KEY_COMMAND_RESULT_TOPIC);
        commandTopic=Context.getConfig().getString(com.fretron.constants.Constants.KEY_COMMAND_TOPIC);
        transporterTopic=Context.getConfig().getString(com.fretron.constants.Constants.KEY_TRANSPORTER_TOPIC);
        transporterIdStore=Context.getConfig().getString(com.fretron.constants.Constants.KEY_TRANSPORTER_ID_STORE);
        groupTopic=Context.getConfig().getString(com.fretron.constants.Constants.KEY_GROUP_TOPIC);
        groupByIdStore=Context.getConfig().getString(com.fretron.constants.Constants.KEY_GROUP_BY_ID_STORE);
        app=Context.getConfig().getString(Constants.KEY_APPLICATION_ID);

        CLUSTER.createTopic(commandResultTopic);
        CLUSTER.createTopic(commandTopic);
        CLUSTER.createTopic(transporterTopic);
        CLUSTER.createTopic(transporterIdStore);
        CLUSTER.createTopic(groupTopic);
        CLUSTER.createTopic(groupByIdStore);

        schemaRegistry=CLUSTER.schemaRegistryUrl();
        bootStrapServer=CLUSTER.bootstrapServers();
    }

    @Test
    public void startTest() throws InterruptedException {

        SpecificAvroSerde<Groups> groupSerde= SerdeUtils.createSerde(schemaRegistry);
        SpecificAvroSerde<Transporter> transporterSerde= SerdeUtils.createSerde(schemaRegistry);

        KafkaStreams streams = new GroupManager().startStream(schemaRegistry,bootStrapServer);
        streams.cleanUp();

        new Thread(()->{
            streams.start();
        }).start();



        Groups groups=new Groups("123",null,null,"fretron",getAdmin(),null);
        Transporter transporter = new Transporter("123",null,null,false);

        new Thread(()->{
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Command command1 = new Command( "transporter.create.success",
                    ByteBuffer.wrap(transporterSerde.serializer().serialize(transporterTopic,transporter)),
                    UUID.randomUUID().toString(),
                    200,
                    null,
                    12345678902L,
                    System.currentTimeMillis());
        Producer<String, Command> commandProducer= HelperClass.getProducer(CLUSTER.bootstrapServers(),CLUSTER.schemaRegistryUrl());
            Future<RecordMetadata> md =commandProducer.send(new ProducerRecord<String, Command>(commandResultTopic , UUID.randomUUID().toString(), command1));

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Command command = new Command( "group.create.command",
                    ByteBuffer.wrap(groupSerde.serializer().serialize(groupTopic,groups)),
                    UUID.randomUUID().toString(),
                    200,
                    null,
                    12345678902L,
                    System.currentTimeMillis());

            Producer<String,Command> producer= HelperClass.getProducer(bootStrapServer,schemaRegistry);
            producer.send(new ProducerRecord<>(commandTopic, UUID.randomUUID().toString(),command));

        }).start();

        List<Command> actual =  IntegrationTestUtils.waitUntilMinValuesRecordsReceived(HelperClass.getConsumerProps("my-group-test" , CLUSTER) ,commandResultTopic,3, 120000);
        for (int index = 0 ; index<actual.size() ; index++){
            System.out.println(actual.get(index).toString());
        }
        assert AssertClass.assertThat(actual,3,null);
    }

    public ArrayList<String> getAdmin() {
        ArrayList<String> list=new ArrayList<>();
        list.add("abc@");
        list.add("xyz@");

        return list;
    }
}
