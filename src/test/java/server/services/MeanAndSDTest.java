package server.services;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import server.beans.comunication.GlobalMeasurement;
import server.beans.comunication.LocalMeasurement;
import server.beans.comunication.Stats;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class MeanAndSDTest extends JerseyTest {

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
    public void localMeanAndSDTest() {
        final int thisHouseId = 8;
        double value=10;
        for (int timestamp=1000;timestamp<=2000;timestamp+=10){
            LocalMeasurement localMeasurement=new LocalMeasurement(thisHouseId,value,timestamp);
            target("/measurements/local").request(MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.entity(localMeasurement, MediaType.APPLICATION_JSON_TYPE));
            value+=11;
        }
        Response response = target("admin/local/stats/"+thisHouseId+"/7").request(MediaType.APPLICATION_JSON_TYPE).get();
        Stats stats=response.readEntity(Stats.class);
        Response response2 = target("admin/local/stats/"+thisHouseId+"/0").request(MediaType.APPLICATION_JSON_TYPE).get();
        Stats stats2=response2.readEntity(Stats.class);
        assertEquals("mean ",1077,stats.getMean(),0.05);
        assertEquals("standard deviation: ",23.762715894162,stats.getSd(),0.05);
        assertEquals("mean ",560,stats2.getMean(),0.05);
        assertEquals("standard deviation: ",322.30187712764,stats2.getSd(),0.05);
    }

    @Test
    public void noMeasurementTest() {
        final int thisHouseId = 10;
        Response response = target("admin/local/stats/"+thisHouseId+"/7").request(MediaType.APPLICATION_JSON_TYPE).get();
        Stats stats=response.readEntity(Stats.class);
        assertEquals("mean ",0,stats.getMean(),0.05);
        assertEquals("standard deviation: ",0,stats.getSd(),0.05);

    }

    @Test
    public void oneMeasurementTest() {
        final int thisHouseId = 11;
        LocalMeasurement localMeasurement=new LocalMeasurement(thisHouseId,22.5,1);
        target("/measurements/local").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(localMeasurement, MediaType.APPLICATION_JSON_TYPE));
        Response response = target("admin/local/stats/"+thisHouseId+"/7").request(MediaType.APPLICATION_JSON_TYPE).get();
        Stats stats=response.readEntity(Stats.class);
        assertEquals("mean ",22.5,stats.getMean(),0.05);
        assertEquals("standard deviation: ",0,stats.getSd(),0.05);

    }

    @Test
    public void GlobalMeanAndSDTest() {
        double value=10;
        for (int timestamp=1000;timestamp<=2000;timestamp+=10){
            GlobalMeasurement globalMeasurement=new GlobalMeasurement(value,timestamp);
            target("/measurements/global").request(MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.entity(globalMeasurement, MediaType.APPLICATION_JSON_TYPE));
            value+=11;
        }
        Response response = target("admin/global/stats/7").request(MediaType.APPLICATION_JSON_TYPE).get();
        Stats stats=response.readEntity(Stats.class);
        Response response2 = target("admin/global/stats/0").request(MediaType.APPLICATION_JSON_TYPE).get();
        Stats stats2=response2.readEntity(Stats.class);
        assertEquals("mean ",1077,stats.getMean(),0.05);
        assertEquals("standard deviation: ",23.762715894162,stats.getSd(),0.05);
        assertEquals("mean ",560,stats2.getMean(),0.05);
        assertEquals("standard deviation: ",322.30187712764,stats2.getSd(),0.05);
    }


}
