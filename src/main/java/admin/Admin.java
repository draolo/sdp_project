package admin;

import server.beans.comunication.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;


public class Admin {
    Client connection;
    String serverBaseUrl;
    ServerSocket socket;

    public Admin(String serverIP, int serverPort, ServerSocket socket){
        this.serverBaseUrl="http://"+serverIP+":"+serverPort;
        connection= ClientBuilder.newClient();
        this.socket=socket;
    }

    public boolean subscribe() throws UnknownHostException {
        NotificationListener me = new NotificationListener(socket.getLocalPort(),InetAddress.getLocalHost().getHostAddress());
        Response response= connection.target(serverBaseUrl+"/notification/subscribe").request().post(Entity.entity(me, MediaType.APPLICATION_JSON));
        return response.getStatus()== Response.Status.OK.getStatusCode();
    }

    public boolean unsubscribe() throws UnknownHostException {
        NotificationListener me = new NotificationListener(socket.getLocalPort(),InetAddress.getLocalHost().getHostAddress());
        Response response= connection.target(serverBaseUrl+"/notification/unsubscribe").request().post(Entity.entity(me, MediaType.APPLICATION_JSON));
        return response.getStatus()== Response.Status.OK.getStatusCode();
    }

    HouseInfo[] getHouseList(){
        Response response= connection.target(serverBaseUrl+"/admin/list").request(MediaType.APPLICATION_JSON).get();
        HouseInfo[] list;
        if (fetchStatus(response.getStatus())){
            list=response.readEntity(HouseInfo[].class);
        }else {
            list= new HouseInfo[0];
        }
        return list;
    }

    LocalMeasurement[] getLocalMeasurements(int id, int limit){
        Response response= connection.target(serverBaseUrl+"/admin/local/"+id+"/"+limit).request(MediaType.APPLICATION_JSON).get();
        LocalMeasurement[] list;
        if (fetchStatus(response.getStatus())){
            list=response.readEntity(LocalMeasurement[].class);
        }else {
            list= new LocalMeasurement[0];
        }
        return list;
    }

    GlobalMeasurement[] getGlobalMeasurements(int limit){
        Response response= connection.target(serverBaseUrl+"/admin/global/"+limit).request(MediaType.APPLICATION_JSON).get();
        GlobalMeasurement[] list;
        if (fetchStatus(response.getStatus())){
            list=response.readEntity(GlobalMeasurement[].class);
        }else {
            list= new GlobalMeasurement[0];
        }
        return list;
    }


    Stats getLocalStats(int id, int limit){
        Response response= connection.target(serverBaseUrl+"/admin/local/stats/"+id+"/"+limit).request(MediaType.APPLICATION_JSON).get();
        Stats stats;
        if (fetchStatus(response.getStatus())){
            stats=response.readEntity(Stats.class);
        }else {
            stats=null;
        }
        return stats;
    }

    Stats getGlobalStats(int limit){
        Response response= connection.target(serverBaseUrl+"/admin/global/stats/"+limit).request(MediaType.APPLICATION_JSON).get();
        Stats stats;
        if (fetchStatus(response.getStatus())){
            stats=response.readEntity(Stats.class);
        }else {
            stats=null;
        }
        return stats;
    }


    public boolean fetchStatus(int status) {
        switch (status){
            case 200:{
                return true;
            }
            case 400:{
                System.out.println("YOUR REQUEST HAVE NOT BEEN PROCESSED BY THE SERVER DUE TO A SYNTAX ERROR");
                System.out.println("PLEASE CHECK YOUR INPUT AND RETRY");
                return false;
            }
            case 404:{
                System.out.println("THE HOST IS NOT AVAILABLE PLEASE CHECK IF THE IP AND THE PORT ARE CORRECT");
                System.out.println("ACTUAL HOST ADDRESS: "+serverBaseUrl);
                return false;
            }
            case 500:{
                System.out.println("THE SERVER HAS ENCOUNTER AN ERROR PROCESSING YOUR REQUEST");
                System.out.println("PLEASE CHECK YOUR INPUT AND RETRY");
                return false;
            }
            default:{
                System.out.println("THE SERVER HAVE ANSWER WITH RESPONSE STATUS: "+status);
                if (status<400){
                    return true;
                }else {
                    System.out.println("YOUR REQUEST COULD NOT BE PROCESSED");
                    return false;
                }
            }
        }
    }


}
