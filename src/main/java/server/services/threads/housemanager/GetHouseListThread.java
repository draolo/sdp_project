package server.services.threads.housemanager;

import server.beans.storage.HouseList;
import server.services.threads.AsyncResponseThread;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;

public class GetHouseListThread extends AsyncResponseThread {

    public GetHouseListThread(AsyncResponse response) {
        super(response);
    }

    @Override
    protected Response operation() {
                /*try {
                    Thread.sleep(20*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
        return Response.ok(HouseList.getInstance()).build();
    }
}
