package house;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;
import server.beans.comunication.HouseInfo;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class HouseTest extends JerseyTest {
    @Override
    protected Application configure() {
        final ResourceConfig rc = new ResourceConfig();
        rc.packages("server.services");
        final Map<String, Object> config = new HashMap<>();
        config.put("com.sun.jersey.api.json.POJOMappingFeature", true);
        rc.addProperties(config);
        return rc;
    }

    private String serverIp;
    private int serverPort;

    @Before
    public void init() throws MalformedURLException, UnknownHostException {
        URI uri=target().getUri();
        this.serverIp= InetAddress.getByName(uri.toURL().getHost()).getHostAddress();
        this.serverPort = uri.getPort();
    }

    @Test
    public void registerHouseTest() throws IOException {
        ServerSocket socket =new ServerSocket(0);
        int thisHouseId=1;
        House house= new House(thisHouseId,socket,serverIp,serverPort);
        house.register();
        Response response = target("/house-manager").request(MediaType.APPLICATION_JSON_TYPE)
                .get();
        HouseInfo[] list=response.readEntity(HouseInfo[].class);
        List<HouseInfo> l= Arrays.asList(list);
        Predicate<HouseInfo> byId = h -> h.getId() == thisHouseId;
        List<HouseInfo> result = l.stream().filter(byId)
                .collect(Collectors.toList());
        HouseInfo house2=result.get(0);
        assertEquals("result should contain the new house: ",house2.getIp(),InetAddress.getLocalHost().getHostAddress());
        assertEquals("result should contain the new house: ",house2.getId(),thisHouseId);
        assertEquals("result should contain the new house: ",house2.getPort(),socket.getLocalPort());
    }

    @Test
    public void doubleRegistrationErrorTest() throws IOException {
        ServerSocket socket =new ServerSocket(0);
        ServerSocket socketX =new ServerSocket(0);
        int thisHouseId=2;
        House house= new House(thisHouseId,socket,serverIp,serverPort);
        house.register();
        House houseX= new House(thisHouseId,socketX,serverIp,serverPort);
        assertFalse("double registration must fail: ",houseX.register());
    }

    @Test
    public void unregisterTest() throws IOException {
        ServerSocket socket =new ServerSocket(0);
        int thisHouseId=3;
        House house= new House(thisHouseId,socket,serverIp,serverPort);
        house.register();
        Response response = target("/house-manager").request(MediaType.APPLICATION_JSON_TYPE)
                .get();
        HouseInfo[] list=response.readEntity(HouseInfo[].class);
        List<HouseInfo> l= Arrays.asList(list);
        Predicate<HouseInfo> byId = h -> h.getId() == thisHouseId;
        List<HouseInfo> result = l.stream().filter(byId)
                .collect(Collectors.toList());
        HouseInfo house2=result.get(0);
        assertEquals("result should contain the new house: ",house2.getIp(),InetAddress.getLocalHost().getHostAddress());
        assertEquals("result should contain the new house: ",house2.getId(),thisHouseId);
        assertEquals("result should contain the new house: ",house2.getPort(),socket.getLocalPort());
        house.unregister();
        response = target("/house-manager").request(MediaType.APPLICATION_JSON_TYPE)
                .get();
        list=response.readEntity(HouseInfo[].class);
        l= Arrays.asList(list);
        result = l.stream().filter(byId)
                .collect(Collectors.toList());

        assertEquals("result should contain the new house: ",0,result.size());
    }

    @Test
    public void houseListGeneration() throws IOException {
        ServerSocket socket =new ServerSocket(0);
        int thisHouseId=4;
        House house= new House(thisHouseId,socket,serverIp,serverPort);
        house.register();
        List<HouseInfo> l= HouseList.getInstance().getHouseList();
        Predicate<HouseInfo> byId = h -> h.getId() == thisHouseId;
        List<HouseInfo> result = l.stream().filter(byId)
                .collect(Collectors.toList());
        HouseInfo house2=result.get(0);
        assertEquals("result should contain the new house: ",house2.getIp(),InetAddress.getLocalHost().getHostAddress());
        assertEquals("result should contain the new house: ",house2.getId(),thisHouseId);
        assertEquals("result should contain the new house: ",house2.getPort(),socket.getLocalPort());

    }



}