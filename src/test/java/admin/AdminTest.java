package admin;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;
import server.beans.comunication.HouseInfo;

import javax.ws.rs.client.Entity;
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

public class AdminTest extends JerseyTest {
    @Override
    protected Application configure() {
        final ResourceConfig rc = new ResourceConfig();
        rc.packages("server.services");
        final Map<String, Object> config = new HashMap<>();
        config.put("com.sun.jersey.api.json.POJOMappingFeature", true);
        rc.addProperties(config);
        return rc;
    }

    private Admin admin;

    @Before
    public void init() throws IOException {
        URI uri=target().getUri();
        ServerSocket s=new ServerSocket(0);
        admin=new Admin(InetAddress.getByName(uri.toURL().getHost()).getHostAddress(),uri.getPort(),s);
    }

    @Test
    public void getHouseListTest() {
        for (int i=0;i<10;i++){
            HouseInfo houseInfo= new HouseInfo(i,1240,"127.0.0.1");
            Response response = target("/house-manager/add").request(MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.entity(houseInfo, MediaType.APPLICATION_JSON_TYPE));
        }
        HouseInfo[] list=admin.getHouseList();
        assertEquals("number of the house must be 10: ", 10, list.length);
        List<HouseInfo> l= Arrays.asList(list);
        Predicate<HouseInfo> byId7 = house -> house.getId() == 7;
        List<HouseInfo> result = l.stream().filter(byId7)
                .collect(Collectors.toList());
        HouseInfo house=result.get(0);
        assertEquals("random house test id: ", 7, house.getId());
        assertEquals("random house test port: ", 1240, house.getPort());
        assertEquals("random house test ip: ", "127.0.0.1", house.getIp());
        Predicate<HouseInfo> byId15 = h -> h.getId() == 15;
        result = l.stream().filter(byId15)
                .collect(Collectors.toList());
        assertEquals("random should not be in the list: ", 0, result.size());
    }

}