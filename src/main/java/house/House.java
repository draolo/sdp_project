package house;

import house.Coordinator.Coordinator;
import server.beans.comunication.HouseInfo;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class House {
    int id;
    ServerSocket socket;
    String serverBaseURL;
    Client houseClient;

    public House(int id, ServerSocket socket, String serverIp, int serverPort) {
        this.id = id;
        this.socket=socket;
        this.serverBaseURL="http://"+serverIp+":"+serverPort;
        houseClient= ClientBuilder.newClient();
    }

    public boolean register() throws UnknownHostException {
        HouseInfo me=new HouseInfo(id,socket.getLocalPort(), InetAddress.getLocalHost().getHostAddress());
        Response response= houseClient.target(serverBaseURL+"/house-manager/add").request(MediaType.APPLICATION_JSON).post(Entity.entity(me, MediaType.APPLICATION_JSON));
        if (response.getStatus()== Response.Status.OK.getStatusCode()){
            HouseInfo[] list=response.readEntity(HouseInfo[].class);
            if (list.length==1){
                Coordinator.getInstance().setCoordinator(list[0]);
            }
            for (HouseInfo h:list) {
                System.out.println(h);
                HouseList.getInstance().add(h);
            }
            return true;
        }
        return false;
    }


    public boolean unregister() {
        Response response= houseClient.target(serverBaseURL+"/house-manager/del/"+id).request().delete();
        return response.getStatus() == Response.Status.OK.getStatusCode();
    }
}
