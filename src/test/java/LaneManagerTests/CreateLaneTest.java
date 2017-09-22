package LaneManagerTests;

import UserManagerTests.HelperClass;
import Util.EmbeddedSingleNodeKafkaCluster;
import Util.IntegrationTestUtils;
import com.fretron.Context;
import com.fretron.Model.Command;
import com.fretron.Model.Lane;
import com.fretron.Model.Transporter;
import com.fretron.Utils.SerdeUtils;
import com.fretron.Utils.SpecificAvroSerde;
import com.fretron.constants.Constants;
import com.fretron.transporter.LaneManager.LaneManager;
import com.fretron.transporter.LaneManager.LaneManagerDriver;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.streams.KafkaStreams;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;

public class CreateLaneTest {
    /*
    Test to create lane ,
    this test case would fail if transporter id doesn't match
     */

    @ClassRule
    public static final EmbeddedSingleNodeKafkaCluster CLUSTER = new EmbeddedSingleNodeKafkaCluster();
    private static String laneTopic,commandTopic,commandResultTopic,laneByUuid,transporterIdStore,transporterTopic;

    @BeforeClass
    public static void startCLuster() throws Exception {
        String args[] = {new File("dev.xml").getAbsolutePath()};
        Context.init(args);
        commandTopic= Context.getConfig().getString(Constants.KEY_COMMAND_TOPIC);
        commandResultTopic=Context.getConfig().getString(Constants.KEY_COMMAND_RESULT_TOPIC);
        laneTopic = Context.getConfig().getString(Constants.KEY_LANES_TOPIC);
        laneByUuid = Context.getConfig().getString(Constants.KEY_LANE_BY_UUID_STORE);
        transporterIdStore=Context.getConfig().getString(Constants.KEY_TRANSPORTER_ID_STORE);
        transporterTopic=Context.getConfig().getString(Constants.KEY_TRANSPORTER_TOPIC);

        CLUSTER.createTopic(commandTopic);
        CLUSTER.createTopic(commandResultTopic);
        CLUSTER.createTopic(laneTopic);
        CLUSTER.createTopic(laneByUuid);
        CLUSTER.createTopic(transporterIdStore);
        CLUSTER.createTopic(transporterTopic);
    }

    @Test
    public void startTest() throws InterruptedException {


        String schemaRegistry=CLUSTER.schemaRegistryUrl();
        String bootstrapServer=CLUSTER.bootstrapServers();

        new Thread(()->{
            KafkaStreams streams=new LaneManager().createLane(schemaRegistry,bootstrapServer);
            streams.cleanUp();
            streams.start();
        }).start();

        SpecificAvroSerde<Command> commandSerde = SerdeUtils.createSerde(schemaRegistry);
        SpecificAvroSerde<Lane> laneSerde = SerdeUtils.createSerde(schemaRegistry);
        SpecificAvroSerde<Transporter> transporterSerde = SerdeUtils.createSerde(schemaRegistry);

        Lane lane = new Lane(null,"123","type",null,null,90098.7878,"polymer",null,null,null,false);
        Transporter transporter = new Transporter("123",null,null,false);
        Producer<String,Command> producer = HelperClass.getProducer(bootstrapServer,schemaRegistry);

        new Thread(()->{
            Command command = new Command("transporter.created.success",
                    ByteBuffer.wrap(transporterSerde.serializer().serialize(transporterTopic,transporter)),
                    UUID.randomUUID().toString(),
                    200,
                    null,
                    2323233213l,
                    System.currentTimeMillis());


            producer.send(new ProducerRecord<>(commandResultTopic,UUID.randomUUID().toString(),command));

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Command command1 = new Command("lane.create.command",
                    ByteBuffer.wrap(laneSerde.serializer().serialize(laneTopic,lane)),
                    UUID.randomUUID().toString(),
                    200,
                    null,
                    1280809890l,
                    System.currentTimeMillis()
            );
            producer.send(new ProducerRecord<>(commandTopic,UUID.randomUUID().toString(),command1));
        }).start();

      List<Command> list = IntegrationTestUtils.waitUntilMinValuesRecordsReceived(HelperClass.getConsumerProps("gruops1",CLUSTER),commandResultTopic,2,12000);

      for(int i=0; i<list.size(); i++)
          System.out.println(list.get(i));

      assert AssertClass.assertThat(list,2,null);
    }
}
