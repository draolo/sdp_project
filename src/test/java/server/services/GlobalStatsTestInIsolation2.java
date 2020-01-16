package server.services;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import server.beans.comunication.LocalMeasurement;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class GlobalStatsTestInIsolation2 extends JerseyTest {

    @Override
    protected Application configure() {
        final ResourceConfig rc = new ResourceConfig();
        rc.packages("server.services");
        final Map<String, Object> config = new HashMap<>();
        config.put("com.sun.jersey.api.json.POJOMappingFeature", true);
        rc.addProperties(config);
        return rc;
    }

    @Test
    public void noMeasurementsGlobalTest(){
        Response response = target("admin/global/7").request(MediaType.APPLICATION_JSON_TYPE).get();
        LocalMeasurement[] list=response.readEntity(LocalMeasurement[].class);
        assertEquals("list must have length 0: ",0,list.length);

    }
}
