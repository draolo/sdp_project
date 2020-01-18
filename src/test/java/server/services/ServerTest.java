package server.services;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import server.beans.comunication.GlobalMeasurement;
import server.beans.comunication.HouseInfo;
import server.beans.comunication.LocalMeasurement;
import server.beans.comunication.Stats;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ServerTest extends JerseyTest {

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
    public void addHouseTest() {
        HouseInfo houseInfo= new HouseInfo(1,1240,"127.0.0.1");
        Response response = target("/house-manager/add").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(houseInfo, MediaType.APPLICATION_JSON_TYPE));
        assertEquals("Http Response should be 200: ", Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void noDuplicateId() {
        HouseInfo houseInfo= new HouseInfo(2,1240,"127.0.0.1");
        Response response = target("/house-manager/add").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(houseInfo, MediaType.APPLICATION_JSON_TYPE));
        assertEquals("Http Response should be 200: ", Response.Status.OK.getStatusCode(), response.getStatus());
        houseInfo= new HouseInfo(2,1250,"127.0.0.1");
        response = target("/house-manager/add").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(houseInfo, MediaType.APPLICATION_JSON_TYPE));
        assertEquals("Http Response should NOT be 200 on duplicate: ", Response.Status.CONFLICT.getStatusCode(), response.getStatus());
    }

    @Test
    public void removeTest() {
        HouseInfo houseInfo= new HouseInfo(3,1240,"127.0.0.1");
        Response response = target("/house-manager/add").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(houseInfo, MediaType.APPLICATION_JSON_TYPE));
        assertEquals("Http Response should be 200: ", Response.Status.OK.getStatusCode(), response.getStatus());

        response = target("/house-manager/del/3").request().delete();
        assertEquals("Http Response should be 200: ", Response.Status.OK.getStatusCode(), response.getStatus());

        response = target("/house-manager/add").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(houseInfo, MediaType.APPLICATION_JSON_TYPE));
        assertEquals("now i should be able to reinsert it: ", Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void couldNotRemoveUnknownHouse() {
        Response response = target("/house-manager/del/4").request().delete();
        assertEquals("Http Response should be 404: ", Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void badRemoveRequest() {
        Response response = target("/house-manager/del/mimmo").request().delete();
        assertNotEquals("Http Response should be 40x: ", Response.Status.OK.getStatusCode(), response.getStatus());
        response = target("/house-manager/del/4").request().delete();
        assertEquals("server should stay up: ", Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void addHouseReturnTest() {
        final int thisHouseId = 5;
        HouseInfo houseInfo= new HouseInfo(thisHouseId,1240,"127.0.0.1");
        Response response = target("/house-manager/add").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(houseInfo, MediaType.APPLICATION_JSON_TYPE));
        HouseInfo[] list=response.readEntity(HouseInfo[].class);
        List<HouseInfo> l= Arrays.asList(list);
        Predicate<HouseInfo> byId = house -> house.getId() == thisHouseId;
        List<HouseInfo> result = l.stream().filter(byId)
                .collect(Collectors.toList());
        HouseInfo house2=result.get(0);
        assertEquals("result should contain the new house: ",house2.getIp(),houseInfo.getIp());
        assertEquals("result should contain the new house: ",house2.getId(),houseInfo.getId());
        assertEquals("result should contain the new house: ",house2.getPort(),houseInfo.getPort());
        assertTrue("result should contain the new house: ", list.length>=1);
    }

    @Test
    public void removeReallyRemove() {
        final int thisHouseId = 6;
        HouseInfo houseInfo= new HouseInfo(thisHouseId,1240,"127.0.0.1");
        Response response = target("/house-manager/add").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(houseInfo, MediaType.APPLICATION_JSON_TYPE));
        HouseInfo[] listBefore=response.readEntity(HouseInfo[].class);
        List<HouseInfo> l= Arrays.asList(listBefore);
        Predicate<HouseInfo> byId = house -> house.getId() == thisHouseId;
        List<HouseInfo> result = l.stream().filter(byId)
                .collect(Collectors.toList());
        HouseInfo house2=result.get(0);
        assertEquals("result should contain the new house: ",house2.getIp(),houseInfo.getIp());
        target("/house-manager/del/6").request().delete();
        response = target("/house-manager").request().get();
        HouseInfo[] listAfter=response.readEntity(HouseInfo[].class);
        l= Arrays.asList(listAfter);
        result = l.stream().filter(byId)
                .collect(Collectors.toList());
        assertEquals("List should not contains the removed house",0, result.size());
    }

    @Test
    public void localStatsAdd() {
        final int thisHouseId = 7;
        LocalMeasurement localMeasurement=new LocalMeasurement(thisHouseId,22.5,1234);
        Response response = target("/measurements/local").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(localMeasurement, MediaType.APPLICATION_JSON_TYPE));
        assertEquals("Http Response should be 200: ", Response.Status.OK.getStatusCode(), response.getStatus());
        response = target("admin/local/"+thisHouseId+"/0").request(MediaType.APPLICATION_JSON_TYPE).get();
        LocalMeasurement[] list=response.readEntity(LocalMeasurement[].class);
        LocalMeasurement last=list[0];
        assertEquals("the measurements must be the same",localMeasurement.getId(),last.getId());
        assertEquals("the measurements must be the same",localMeasurement.getValue(),last.getValue(), 0);
        assertEquals("the measurements must be the same",localMeasurement.getTimestamp(),last.getTimestamp());
    }

    @Test
    public void localStatsLimitTest() {
        final int thisHouseId = 8;
        for (int timestamp=1000;timestamp<=2000;timestamp+=10){
            LocalMeasurement localMeasurement=new LocalMeasurement(thisHouseId,22.5,timestamp);
            target("/measurements/local").request(MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.entity(localMeasurement, MediaType.APPLICATION_JSON_TYPE));
        }
        Response response = target("admin/local/"+thisHouseId+"/7").request(MediaType.APPLICATION_JSON_TYPE).get();
        LocalMeasurement[] list=response.readEntity(LocalMeasurement[].class);
        assertEquals("list must have the same length of the limit ",7,list.length);
        assertTrue("result must be sorted from newer to older",ServerTest.checkIfIsSorted(list));
        LocalMeasurement localMeasurement=list[0];
        assertEquals("last must be last ",2000,localMeasurement.getTimestamp());

    }

    @Test
    public void noMeasurementsTest(){
        final int thisHouseId = 9;
        Response response = target("admin/local/"+thisHouseId+"/7").request(MediaType.APPLICATION_JSON_TYPE).get();
        LocalMeasurement[] list=response.readEntity(LocalMeasurement[].class);
        assertEquals("list must have length 0: ",0,list.length);

    }

    // TODO: 16/01/2020 decide if its a bug or a feature on negative value return the full list
    /*
    @Test
    public void negativeMeasurementsLimitTest(){
        final int thisHouseId = 10;
        LocalMeasurement localMeasurement=new LocalMeasurement(thisHouseId,22.5,1234);
        target("/measurements/local").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(localMeasurement, MediaType.APPLICATION_JSON_TYPE));
        Response response = target("admin/local/"+thisHouseId+"/-5").request(MediaType.APPLICATION_JSON_TYPE).get();
        LocalMeasurement[] list=response.readEntity(LocalMeasurement[].class);
        assertEquals("list must have length 0: ",0,list.length);
    }
*/
    @Test
    public void outOfBoundMeasurementsLimitTest(){
        final int thisHouseId = 11;
        LocalMeasurement localMeasurement=new LocalMeasurement(thisHouseId,22.5,1234);
        target("/measurements/local").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(localMeasurement, MediaType.APPLICATION_JSON_TYPE));
        Response response = target("admin/local/"+thisHouseId+"/5").request(MediaType.APPLICATION_JSON_TYPE).get();
        LocalMeasurement[] list=response.readEntity(LocalMeasurement[].class);
        assertEquals("list must have length 0: ",1,list.length);
    }



    @Test
    public void outOfBoundGlobalMeasurementsLimitTest(){
        GlobalMeasurement globalMeasurement=new GlobalMeasurement(22.5,1);
        target("/measurements/global").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(globalMeasurement, MediaType.APPLICATION_JSON_TYPE));
        Response response = target("admin/global/5000").request(MediaType.APPLICATION_JSON_TYPE).get();
        LocalMeasurement[] list=response.readEntity(LocalMeasurement[].class);
        assertTrue("list must have length 0: ",list.length<5000);
    }


    @Test
    public void globalStatsLimitTest() {
        for (int timestamp=1000;timestamp<=2000;timestamp+=10){
            GlobalMeasurement globalMeasurement=new GlobalMeasurement(22.5,timestamp);
            target("/measurements/global").request(MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.entity(globalMeasurement, MediaType.APPLICATION_JSON_TYPE));
        }
        Response response = target("admin/global/7").request(MediaType.APPLICATION_JSON_TYPE).get();
        GlobalMeasurement[] list=response.readEntity(GlobalMeasurement[].class);
        assertEquals("list must have the same length of the limit ",7,list.length);
        assertTrue("result must be sorted from newer to older",ServerTest.checkIfIsSorted(list));
        GlobalMeasurement globalMeasurement=list[0];
        assertEquals("last must be last ",2000,globalMeasurement.getTimestamp());

    }

    @Test
    public void wrongStatsTest() {
        final int thisHouseId = 7;
        GlobalMeasurement globalMeasurement = new GlobalMeasurement(22.5, 1234);
        Response response = target("/measurements/local").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(globalMeasurement, MediaType.APPLICATION_JSON_TYPE));
        assertNotEquals("Http Response should be 200: ", Response.Status.OK.getStatusCode(), response.getStatus());
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

    @Test
    public void noMeasurementsGlobalTest(){
        Response response = target("admin/global/7").request(MediaType.APPLICATION_JSON_TYPE).get();
        LocalMeasurement[] list=response.readEntity(LocalMeasurement[].class);
        assertEquals("list must have length 0: ",0,list.length);

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

    public static boolean checkIfIsSorted(GlobalMeasurement[] array){
        for (int i = 0; i < array.length - 1; i++) {
            if (array[i].getTimestamp() < array[i + 1].getTimestamp())
                return false;
        }
        return true;
    }












}