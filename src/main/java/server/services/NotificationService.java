package server.services;

import server.beans.comunication.NotificationListener;
import server.beans.storage.NotificationSubscriberList;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("notification")
public class NotificationService {
    @GET
    @Produces({"application/json"})
    public Response getList() {
        return Response.ok(NotificationSubscriberList.getInstance().getListenerList()).build();
    }

    @Path("subscribe")
    @POST
    @Consumes({"application/json"})
    public Response addSubscriber(NotificationListener h){
        return NotificationSubscriberList.getInstance().subscribe(h)?Response.ok().build():Response.status(Response.Status.BAD_REQUEST).build();
    }

    @Path("unsubscribe")
    @POST
    @Produces({"application/json"})
    @Consumes({"application/json"})
    public Response removeSubscriber(NotificationListener h){
        return NotificationSubscriberList.getInstance().unsubscribe(h)?Response.ok().build():Response.status(Response.Status.BAD_REQUEST).build();
    }

}
