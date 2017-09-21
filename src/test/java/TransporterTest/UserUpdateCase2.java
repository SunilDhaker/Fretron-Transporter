package TransporterTest;

import Util.EmbeddedSingleNodeKafkaCluster;
import Util.IntegrationTestUtils;
import com.fretron.Context;
import com.fretron.Model.Command;
import com.fretron.Model.Groups;
import com.fretron.Model.Transporter;
import com.fretron.Model.User;
import com.fretron.Utils.SerdeUtils;
import com.fretron.Utils.SpecificAvroSerde;
import com.fretron.constants.Constants;
import com.fretron.transporter.UserManager.UserManager;
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

public class UserUpdateCase2 {
    /*
  update a user with group id null
*/
    @ClassRule
    public static final EmbeddedSingleNodeKafkaCluster CLUSTER=new EmbeddedSingleNodeKafkaCluster();
    private static String commandResultTopic,commandTopic,transporterTopic,transporterIdStore,userTopic,groupByIdStore,userByEmailStore,app;
    private static String schemaRegistry,bootStrapServer;
    @BeforeClass
    public static void startCluster() throws Exception {
        Context.init(new String[]{new File("dev.xml").getAbsolutePath()});

        commandResultTopic=Context.getConfig().getString(com.fretron.constants.Constants.KEY_COMMAND_RESULT_TOPIC);
        commandTopic=Context.getConfig().getString(com.fretron.constants.Constants.KEY_COMMAND_TOPIC);
        transporterTopic=Context.getConfig().getString(com.fretron.constants.Constants.KEY_TRANSPORTER_TOPIC);
        transporterIdStore=Context.getConfig().getString(com.fretron.constants.Constants.KEY_TRANSPORTER_ID_STORE);
        userTopic=Context.getConfig().getString(com.fretron.constants.Constants.KEY_USERS_TOPIC);
        groupByIdStore=Context.getConfig().getString(com.fretron.constants.Constants.KEY_GROUP_BY_ID_STORE);
        userByEmailStore=Context.getConfig().getString(com.fretron.constants.Constants.KEY_USER_BY_EMAIL_STORE);
        app=Context.getConfig().getString(Constants.KEY_APPLICATION_ID);

        CLUSTER.createTopic(commandResultTopic);
        CLUSTER.createTopic(commandTopic);
        CLUSTER.createTopic(transporterTopic);
        CLUSTER.createTopic(transporterIdStore);
        CLUSTER.createTopic(userTopic);
        CLUSTER.createTopic(groupByIdStore);
        CLUSTER.createTopic(userByEmailStore);

        schemaRegistry=CLUSTER.schemaRegistryUrl();
        bootStrapServer=CLUSTER.bootstrapServers();
    }

    @Test
    public void startTest() throws InterruptedException {

        SpecificAvroSerde<User> userSerde= SerdeUtils.createSerde(schemaRegistry);
        SpecificAvroSerde<Transporter> transporterSerde= SerdeUtils.createSerde(schemaRegistry);

        KafkaStreams streams = new UserManager().startStream(bootStrapServer,schemaRegistry);
        streams.cleanUp();

        new Thread(()->{
            streams.start();
        }).start();



        User user=new User(null,"xyz","xyz@gmail.com","1234567890","123",null,false);
        Transporter transporter = new Transporter("123",null,getGroups(),false);
        User existingUser=new User("565","xyz","xyz@gmail.com","1234567890","123","001",false);


        new Thread(()->{
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Command command2 = new Command( "user.create.success",
                    ByteBuffer.wrap(userSerde.serializer().serialize(userTopic,existingUser)),
                    UUID.randomUUID().toString(),
                    200,
                    null,
                    12345678902L,
                    System.currentTimeMillis());
            Producer<String, Command> commandProducer1= HelperClass.getProducer(CLUSTER.bootstrapServers(),CLUSTER.schemaRegistryUrl());
            commandProducer1.send(new ProducerRecord<String, Command>(commandResultTopic , UUID.randomUUID().toString(), command2));

            try {
                Thread.sleep(10000);
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
            commandProducer.send(new ProducerRecord<String, Command>(commandResultTopic , UUID.randomUUID().toString(), command1));

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Command command = new Command( "user.update.command",
                    ByteBuffer.wrap(userSerde.serializer().serialize(userTopic,user)),
                    UUID.randomUUID().toString(),
                    200,
                    null,
                    12345678902L,
                    System.currentTimeMillis());

            Producer<String,Command> producer=HelperClass.getProducer(bootStrapServer,schemaRegistry);
            producer.send(new ProducerRecord<>(commandTopic, UUID.randomUUID().toString(),command));
        }).start();

        List<Command> actual = IntegrationTestUtils.waitUntilMinValuesRecordsReceived(HelperClass.getConsumerProps("group.v1",CLUSTER),commandResultTopic,3,120000);

        for(int i=0; i<actual.size(); i++)
            System.out.println(actual.get(i));

        assert AssertClass.assertThat(actual,3,null);
    }

    public ArrayList<Groups> getGroups() {
        ArrayList<Groups> list=new ArrayList<>();
        Groups groups=new Groups("001",null,null,"kk",null,null);

        list.add(groups);

        return list;
    }
}
