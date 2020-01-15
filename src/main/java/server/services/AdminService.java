package server.services;

import server.beans.comunication.GlobalMeasurement;
import server.beans.comunication.LocalMeasurement;
import server.beans.comunication.Stats;
import server.beans.storage.GlobalMeasurementList;
import server.beans.storage.HouseList;
import server.beans.storage.LocalMeasurementList;
import server.various.Statistics;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.List;


@Path("admin")
public class AdminService {

    @Path("list")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getList() {
        return Response.ok(HouseList.getInstance()).build();
    }

    @Path("local/{id}/{limit}")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getLocalMeasurement(@PathParam("id") int id, @PathParam("limit") int limit){
        List<LocalMeasurement> list = LocalMeasurementList.getInstance().getLastMeasurements(id, limit);
        return Response.ok(list).build();
    }

    @Path("global/{limit}")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getGlobalMeasurement(@PathParam("limit") int limit){
        List<GlobalMeasurement > list = GlobalMeasurementList.getInstance().getLastMeasurements(limit);
        return Response.ok(list).build();
    }

    @Path("local/stats/{id}/{limit}")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getLocalStats(@PathParam("id") int id, @PathParam("limit") int limit){
        List<LocalMeasurement> list = LocalMeasurementList.getInstance().getLastMeasurements(id, limit);
        double mean= Statistics.getMean(list);
        double sd=Statistics.getStdDev(list);
        Stats stats=new Stats(mean,sd);
        return Response.ok(stats).build();
    }

    @Path("global/stats/{limit}")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getGlobalStats(@PathParam("limit") int limit){
        List<GlobalMeasurement> list = GlobalMeasurementList.getInstance().getLastMeasurements(limit);
        double mean= Statistics.getMean(list);
        double sd=Statistics.getStdDev(list);
        Stats stats=new Stats(mean,sd);
        return Response.ok(stats).build();
    }

}
