package server.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/helloworld")
public class HelloWorld {

    @GET
    @Produces("text/plain")
    public String helloWorld(){
        /*try {
            Thread.sleep(60*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        return "Hello world!";

    }

    @GET
    @Path("{name}")
    @Produces("text/plain")
    public String helloWorldName(@PathParam("name") String name){
        return "Hello, "+name+"!";

    }
/*
    @GET
    @Produces("application/json")
    public String helloWorld2(){
        return "{\"message\": \"helloWorld\"}";
    }
*/
    @Path("/inner")
    @GET
    @Produces("text/plain")
    public String innerHello(){
        return "Inner Hello!";
    }

}
