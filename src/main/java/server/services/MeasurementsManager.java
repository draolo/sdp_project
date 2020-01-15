package server.services;

import server.beans.comunication.GlobalMeasurement;
import server.beans.comunication.LocalMeasurement;
import server.beans.storage.GlobalMeasurementList;
import server.beans.storage.LocalMeasurementList;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;


@Path("measurements")
public class MeasurementsManager {


    @Path("global")
    @POST
    @Consumes({"application/json"})
    public Response addGlobal(GlobalMeasurement g){
        return GlobalMeasurementList.getInstance().add(g)? Response.ok().build():Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @Path("local")
    @POST
    @Consumes({"application/json"})
    public Response addLocal(LocalMeasurement l){
        // TODO: 14/01/2020 in a real world scenario we could check if the house is part of the network and make a little bit of validation based on the timestamp
        return LocalMeasurementList.getInstance().add(l)?Response.ok().build():Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }


}