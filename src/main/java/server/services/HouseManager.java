package server.services;

import server.beans.comunication.HouseInfo;
import server.beans.storage.HouseList;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;


@Path("house-manager")
public class HouseManager {

    @GET
    @Produces({"application/json", "application/xml"})
    public Response getList() {
        return Response.ok(HouseList.getInstance()).build();
    }

    @Path("add")
    @POST
    @Consumes({"application/json", "application/xml"})
    public Response addHouse(HouseInfo h){
        return HouseList.getInstance().add(h)?Response.ok(HouseList.getInstance()).build():Response.status(Response.Status.CONFLICT).build();
    }


    @Path("del/{id}")
    @DELETE
    @Consumes({"application/json", "application/xml"})
    public Response delHouse(@PathParam("id") int id){
        if(HouseList.getInstance().del(id)){
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }


}