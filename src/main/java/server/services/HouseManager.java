package server.services;

import server.beans.comunication.HouseInfo;
import server.beans.comunication.Notification;
import server.beans.storage.HouseList;
import server.various.SendNotification;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;


@Path("house-manager")
public class HouseManager {

    @GET
    @Produces({"application/json"})
    public Response getList() {
        return Response.ok(HouseList.getInstance().getHouseList()).build();
    }

    @Path("add")
    @POST
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response addHouse(HouseInfo h){
        if(HouseList.getInstance().add(h)) {
            Notification n=new Notification("HOUSE "+h.getId()+" IS NOW PART OF THE NETWORK ON "+h.getIp()+":"+h.getPort());
            new SendNotification(n).start();
            return Response.ok(HouseList.getInstance().getHouseList()).build();
        }else {
            return Response.status(Response.Status.CONFLICT).build();
        }
    }


    @Path("house/{id}")
    @DELETE
    public Response delHouse(@PathParam("id") int id){
        if(HouseList.getInstance().del(id)){
            Notification n=new Notification("HOUSE "+id+" HAS LEFT THE NETWORK");
            new SendNotification(n).start();
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @Path("house/{id}")
    @GET
    @Consumes({"application/json"})
    public Response getHouse(@PathParam("id") int id){
        List<HouseInfo> list=HouseList.getInstance().getHouseList();
        list.removeIf(houseInfo -> houseInfo.getId()!=id);
        if(list.size()>0){
            return Response.ok(list.get(0)).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }



}