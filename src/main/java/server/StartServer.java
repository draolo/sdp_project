package server;


import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class StartServer {



    public static void main(String[] args) throws IOException, URISyntaxException {
        String HOST;
        int PORT;
        try {
           HOST=args[0];
           PORT=Integer.parseInt(args[1]);
       }catch (Exception e){
            System.out.println("INVALID PARAMETER");
            System.out.println("USAGE: StartServer host port");
            e.printStackTrace();
            return;
        }
        String BASE_URI ="http://"+HOST+":"+PORT+"/";
        URI uri=new URI(BASE_URI);
        //final ResourceConfig rc = new ResourceConfig(HelloWorld.class, HouseManager.class, MeasurementsManager.class);
        final ResourceConfig rc = new ResourceConfig();
        rc.packages("server.services");
        final Map<String, Object> config = new HashMap<>();
        config.put("com.sun.jersey.api.json.POJOMappingFeature", true);
        rc.addProperties(config);
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(uri,rc);
        server.start();
        System.out.println("Server running!");
        System.out.println("Server started on: http://"+HOST+":"+PORT);
        System.out.println("Hit return to stop...");
        System.in.read();
        System.out.println("Stopping server");
        server.shutdownNow();
        System.out.println("Server stopped");
    }
}
