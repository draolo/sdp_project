package server.services;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import server.beans.comunication.HouseInfo;

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
        HouseInfo houseInfo= new HouseInfo(5,1240,"127.0.0.1");
        Response response = target("/house-manager/add").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(houseInfo, MediaType.APPLICATION_JSON_TYPE));
        HouseInfo[] list=response.readEntity(HouseInfo[].class);
        List<HouseInfo> l= Arrays.asList(list);
        Predicate<HouseInfo> byId = house -> house.getId() == 5;
        List<HouseInfo> result = l.stream().filter(byId)
                .collect(Collectors.toList());
        HouseInfo house2=result.get(0);
        assertEquals("result should contain the new house: ",house2.getIp(),houseInfo.getIp());
        assertEquals("result should contain the new house: ",house2.getId(),houseInfo.getId());
        assertEquals("result should contain the new house: ",house2.getPort(),houseInfo.getPort());

        assertTrue("result should contain the new house: ", list.length>=1);
    }






}