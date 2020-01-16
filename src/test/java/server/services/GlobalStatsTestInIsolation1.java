package server.services;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import server.beans.comunication.GlobalMeasurement;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class GlobalStatsTestInIsolation1 extends JerseyTest {
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
    public void globalStatsAdd() {
        GlobalMeasurement globalMeasurement=new GlobalMeasurement(22.5,2);
        Response response = target("/measurements/global").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(globalMeasurement, MediaType.APPLICATION_JSON_TYPE));
        assertEquals("Http Response should be 200: ", Response.Status.OK.getStatusCode(), response.getStatus());
        response = target("admin/global/0").request(MediaType.APPLICATION_JSON_TYPE).get();
        GlobalMeasurement[] list=response.readEntity(GlobalMeasurement[].class);
        GlobalMeasurement last=list[0];
        assertEquals("the measurements must be the same",globalMeasurement.getValue(),last.getValue(), 0);
        assertEquals("the measurements must be the same",globalMeasurement.getTimestamp(),last.getTimestamp());
    }
}
