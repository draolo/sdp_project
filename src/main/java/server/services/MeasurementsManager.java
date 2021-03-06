package server.services;

import server.beans.comunication.GlobalMeasurement;
import server.beans.comunication.HouseInfo;
import server.beans.comunication.LocalMeasurement;
import server.beans.comunication.Notification;
import server.beans.storage.GlobalMeasurementList;
import server.beans.storage.LocalMeasurementList;
import server.various.SendNotification;

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
        //in a real world scenario we could check if the house is part of the network and make a little bit of validation based on the timestamp
        return LocalMeasurementList.getInstance().add(l)?Response.ok().build():Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @Path("boost")
    @POST
    @Consumes({"application/json"})
    public Response boost(HouseInfo houseInfo){
        //in a real world scenario we could check if the house is part of the network and make a little bit of validation based on the timestamp
        Notification n=new Notification("HOUSE "+houseInfo.getId()+" IS NOW USING BOOST");
        new SendNotification(n).start();
        return Response.ok().build();
    }




}