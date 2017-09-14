package commander;

import com.fretron.transporter.Context;
import com.fretron.transporter.constants.Constants;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

/**
 * Created by anurag on 13-Sep-17.
 */
public class Server implements Runnable  {

        public static String BASE_URI="" ;

        public static void main( String[] args ) throws Exception
        {
            Context.init(args);
            BASE_URI=Context.getConfig().getString(Constants.KEY_BASE_URL);
            new Thread(new Server()).start();

        }
        public static ResourceConfig create() {
            final ResourceConfig resourceConfig=new ResourceConfig().registerClasses(Resources.class);

            return resourceConfig;
        }

        public void run() {
            URI rui = UriBuilder.fromUri(BASE_URI).build();
            final HttpServer grizzlyServer = GrizzlyHttpServerFactory.createHttpServer(rui, create() , false);

            try {
                grizzlyServer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

