package TransporterTest;

import Util.EmbeddedSingleNodeKafkaCluster;
import com.fretron.Context;
import com.fretron.Model.Command;
import com.fretron.Model.User;
import com.fretron.Utils.PropertiesUtil;
import com.fretron.Utils.SerdeUtils;
import com.fretron.Utils.SpecificAvroSerde;
import com.fretron.constants.Constants;
import com.fretron.transporter.UserManager.UserManager;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.streams.KafkaStreams;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.UUID;

public class TestUserManager {
    @ClassRule
    public static final EmbeddedSingleNodeKafkaCluster CLUSTER=new EmbeddedSingleNodeKafkaCluster();
    public static String commandResultTopic,commandTopic,transporterTopic,transporterIdStore,userTopic,groupByIdStore,userByEmailStore;

    @BeforeClass
    public static void startCluster() throws Exception {
        Context.init(new String[]{new File("dev.xml").getAbsolutePath()});

        commandResultTopic=Context.getConfig().getString(com.fretron.constants.Constants.KEY_COMMAND_RESULT_TOPIC);
        commandTopic=Context.getConfig().getString(com.fretron.constants.Constants.KEY_COMMAND_TOPIC);
        transporterTopic=Context.getConfig().getString(com.fretron.constants.Constants.KEY_TRANSPORTER_TOPIC);
        transporterIdStore=Context.getConfig().getString(com.fretron.constants.Constants.KEY_TRANSPORTER_ID_STORE);
        userTopic=Context.getConfig().getString(com.fretron.constants.Constants.KEY_USERS_TOPIC);
        groupByIdStore=Context.getConfig().getString(com.fretron.constants.Constants.KEY_GROUP_BY_ID_STORE);
        userByEmailStore=Context.getConfig().getString(com.fretron.constants.Constants.KEY_USER_BYEMAIL_STORE);

        CLUSTER.createTopic(commandResultTopic);
        CLUSTER.createTopic(commandTopic);
        CLUSTER.createTopic(transporterTopic);
        CLUSTER.createTopic(transporterIdStore);
        CLUSTER.createTopic(userTopic);
        CLUSTER.createTopic(groupByIdStore);
        CLUSTER.createTopic(userByEmailStore);
    }

    @Test
    public void startTest() {

        String schemaRegistry=CLUSTER.schemaRegistryUrl();
        String bootStrapServer=CLUSTER.bootstrapServers();

        KafkaStreams streams = new UserManager().startStream(bootStrapServer,schemaRegistry);
        streams.cleanUp();
        streams.start();


        Properties properties= PropertiesUtil.initializeProperties(Context.getConfig().getString(Constants.KEY_APPLICATION_ID),schemaRegistry,bootStrapServer,Context.getConfig());
        SpecificAvroSerde<User> userSerde= SerdeUtils.createSerde(schemaRegistry);

        SpecificAvroSerde<Command> commandSerdes=SerdeUtils.createSerde(schemaRegistry);



        User user=new User(null,"xyz","xyz@gmail.com","1234567890","123","23",false);

        Command command=new Command();

        command.setType("user.create.command");
        command.setStartTime(System.currentTimeMillis());
        command.setData(ByteBuffer.wrap(userSerde.serializer().serialize(userTopic,user)));
        command.setErrorMessage(null);
        command.setStatusCode(200);
        command.setId(UUID.randomUUID().toString());

        Producer<String,Command> producer=HelperClass.getProducer(bootStrapServer,schemaRegistry);
        producer.send(new ProducerRecord<>(commandTopic, UUID.randomUUID().toString(),command));
    }
}
