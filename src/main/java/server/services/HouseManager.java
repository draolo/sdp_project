package server.services;

import server.beans.HouseInfo;
import server.beans.HouseList;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;


@Path("house-manager")
public class HouseManager {

    @Path("add")
    @POST
    @Consumes({"application/json", "application/xml"})
    public Response addHouse(HouseInfo u){
        System.out.println("adding house "+u);
        return HouseList.getInstance().add(u)?Response.ok(HouseList.getInstance()).build():Response.status(Response.Status.CONFLICT).build();
    }

    @GET
    @Produces({"application/json", "application/xml"})
    public void getList(@Suspended final AsyncResponse asyncResponse) {
        System.out.println("requesting house list");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response result = null;
                try {
                    result = veryExpensiveOperation();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                asyncResponse.resume(result);
            }

            private Response veryExpensiveOperation() throws InterruptedException {
                Thread.sleep(20*1000);
                return Response.ok(HouseList.getInstance()).build();
            }
        }).start();
        System.out.println("aaa");
    }

    @Path("del/{id}")
    @DELETE
    @Consumes({"application/json", "application/xml"})
    public Response delWord(@PathParam("id") int id){
        System.out.println("delete request");
        if(HouseList.getInstance().del(id)){
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }



}