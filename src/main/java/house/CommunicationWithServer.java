package house;

import house.Boost.BoostRequest;
import server.beans.comunication.GlobalMeasurement;
import server.beans.comunication.HouseInfo;
import server.beans.comunication.LocalMeasurement;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

public class CommunicationWithServer {

    public static HouseInfo[] register() throws Exception {
        HouseInfo me=Configuration.houseInfo;
        Response response= Configuration.toServer.target(Configuration.serverBaseURL+"/house-manager/add").request(MediaType.APPLICATION_JSON).post(Entity.entity(me, MediaType.APPLICATION_JSON));
        if (response.getStatus()== Response.Status.OK.getStatusCode()) {
            return response.readEntity(HouseInfo[].class);
        }
        throw new Exception("CONNECTION FAILED");
    }

    public static boolean sendGlobalMeasurement(GlobalMeasurement globalMeasurement){
        try {
            Response response = Configuration.toServer.target(Configuration.serverBaseURL + "/measurements/global").request(MediaType.APPLICATION_JSON).post(Entity.entity(globalMeasurement, MediaType.APPLICATION_JSON));
            return response.getStatus() == Response.Status.OK.getStatusCode();
        }catch (Exception e){
            Logger.getGlobal().warning("UNABLE TO COMMUNICATE WITH SERVER");
            e.printStackTrace();
            return false;
        }
    }

    public static boolean sendLocalMeasurement(LocalMeasurement localMeasurement){
        try {
            Response response = Configuration.toServer.target(Configuration.serverBaseURL + "/measurements/local").request(MediaType.APPLICATION_JSON).post(Entity.entity(localMeasurement, MediaType.APPLICATION_JSON));
            return response.getStatus() == Response.Status.OK.getStatusCode();
        }catch (Exception e){
            Logger.getGlobal().warning("UNABLE TO COMMUNICATE WITH SERVER");
            e.printStackTrace();
            return false;
        }
    }

    public static boolean sendBoostNotification(BoostRequest boostRequest){
        try {
            Response response = Configuration.toServer.target(Configuration.serverBaseURL + "/measurements/boost").request(MediaType.APPLICATION_JSON).post(Entity.entity(boostRequest.getFrom(), MediaType.APPLICATION_JSON));
            return response.getStatus() == Response.Status.OK.getStatusCode();
        }catch (Exception e){
            Logger.getGlobal().warning("UNABLE TO COMMUNICATE WITH SERVER");
            e.printStackTrace();
            return false;
        }
    }

    public static boolean unregister() {
        return unregister(Configuration.houseInfo.getId());
    }

    public static boolean unregister(int id) {
        try {
            Response response = Configuration.toServer.target(Configuration.serverBaseURL + "/house-manager/del/" + id).request().delete();
            return response.getStatus() == Response.Status.OK.getStatusCode();
        }catch (Exception e){
            Logger.getGlobal().warning("UNABLE TO COMMUNICATE WITH SERVER");
            e.printStackTrace();
            return false;
        }
    }
}
