package server.services;

import server.beans.comunication.GlobalMeasurement;
import server.beans.comunication.LocalMeasurement;
import server.services.threads.measurementmanager.AddGlobalMeasurementsThread;
import server.services.threads.measurementmanager.AddLocalMeasurementsThread;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;


@Path("measurements")
public class MeasurementsManager {


    @Path("global")
    @POST
    @Consumes({"application/json", "application/xml"})
    public void addGlobal(@Suspended final AsyncResponse asyncResponse, GlobalMeasurement g){
        new AddGlobalMeasurementsThread(asyncResponse,g).start();
    }

    @Path("local")
    @POST
    @Consumes({"application/json", "application/xml"})
    public void addLocal(@Suspended final AsyncResponse asyncResponse, LocalMeasurement l){
        new AddLocalMeasurementsThread(asyncResponse,l).start();
    }


}