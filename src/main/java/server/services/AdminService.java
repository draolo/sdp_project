package server.services;

import server.services.threads.admin.GetGlobalMeasurementsThread;
import server.services.threads.admin.GetGlobalStatsThread;
import server.services.threads.admin.GetLocalMeasurementsThread;
import server.services.threads.admin.GetLocalStatsThread;
import server.services.threads.housemanager.GetHouseListThread;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;


@Path("admin")
public class AdminService {

    @Path("list")
    @GET
    @Produces({"application/json", "application/xml"})
    public void getList(@Suspended final AsyncResponse asyncResponse) {
        new GetHouseListThread(asyncResponse).start();
    }

    @Path("local/{id}/{limit}")
    @GET
    @Produces({"application/json", "application/xml"})
    public void getLocalMeasurement(@Suspended final AsyncResponse asyncResponse,@PathParam("id") int id, @PathParam("limit") int limit){
        new GetLocalMeasurementsThread(asyncResponse,id,limit).start();
    }

    @Path("global/{limit}")
    @GET
    @Produces({"application/json", "application/xml"})
    public void getGlobalMeasurement(@Suspended final AsyncResponse asyncResponse, @PathParam("limit") int limit){
        new GetGlobalMeasurementsThread(asyncResponse,limit).start();
    }

    @Path("local/stats/{id}/{limit}")
    @GET
    @Produces({"application/json", "application/xml"})
    public void getLocalStats(@Suspended final AsyncResponse asyncResponse,@PathParam("id") int id, @PathParam("limit") int limit){
        new GetLocalStatsThread(asyncResponse,id, limit).start();
    }

    @Path("global/stats/{limit}")
    @GET
    @Produces({"application/json", "application/xml"})
    public void getGlobalStats(@Suspended final AsyncResponse asyncResponse, @PathParam("limit") int limit){
        new GetGlobalStatsThread(asyncResponse,limit).start();
    }

}
