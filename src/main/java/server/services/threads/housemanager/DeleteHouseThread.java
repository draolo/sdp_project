package server.services.threads.housemanager;

import server.beans.storage.HouseList;
import server.services.threads.AsyncResponseThread;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;

public class DeleteHouseThread extends AsyncResponseThread {
    int id;

    public DeleteHouseThread(AsyncResponse response, int id) {
        super(response);
        this.id=id;
    }


    @Override
    protected Response operation() {
        if(HouseList.getInstance().del(id)){
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
