package server.services;

import server.beans.comunication.HouseInfo;
import server.services.threads.housemanager.AddHouseThread;
import server.services.threads.housemanager.DeleteHouseThread;
import server.services.threads.housemanager.GetHouseListThread;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;


@Path("house-manager")
public class HouseManager {

    @GET
    @Produces({"application/json", "application/xml"})
    public void getList(@Suspended final AsyncResponse asyncResponse) {
        new GetHouseListThread(asyncResponse).start();
    }

    @Path("add")
    @POST
    @Consumes({"application/json", "application/xml"})
    public void addHouse(@Suspended final AsyncResponse asyncResponse,HouseInfo u){
        new AddHouseThread(asyncResponse,u).start();
    }


    @Path("del/{id}")
    @DELETE
    @Consumes({"application/json", "application/xml"})
    public void delHouse(@Suspended final AsyncResponse asyncResponse, @PathParam("id") int id){
        new DeleteHouseThread(asyncResponse,id).start();
    }


}