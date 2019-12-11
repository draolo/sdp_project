package server;


import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StartServer {

    private static final String HOST = "localhost";
    private static final int PORT = 1340;

    public static void main(String[] args) throws IOException {
        String BASE_URI ="http://"+HOST+":"+PORT+"/";
        final ResourceConfig rc = new PackagesResourceConfig("server.services");
        final Map<String, Object> config = new HashMap<>();
        config.put("com.sun.jersey.api.json.POJOMappingFeature", true);
        rc.setPropertiesAndFeatures(config);
        HttpServer server = HttpServerFactory.create(BASE_URI,rc);
        server.setExecutor(null);
        server.start();


        System.out.println("Server running!");
        System.out.println("Server started on: http://"+HOST+":"+PORT);

        System.out.println("Hit return to stop...");
        System.in.read();
        System.out.println("Stopping server");
        server.stop(0);
        System.out.println("Server stopped");
    }
}
