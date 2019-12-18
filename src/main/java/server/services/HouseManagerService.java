package server.services;

import server.beans.HouseInfo;
import server.beans.HouseList;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("house-manager")
public class HouseManagerService {

    @Path("add")
    @POST
    @Consumes({"application/json", "application/xml"})
    public Response addHouse(HouseInfo u){
        return HouseList.getInstance().add(u)?Response.ok().build():Response.status(Response.Status.CONFLICT).build();
    }

    @GET
    @Produces({"application/json", "application/xml"})
    public Response getList(){
        return Response.ok(HouseList.getInstance()).build();
    }

    @Path("del/{id}")
    @DELETE
    @Consumes({"application/json", "application/xml"})
    public Response delWord(@PathParam("id") int id){
        if(HouseList.getInstance().del(id)){
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }



}