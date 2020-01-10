package server.services.threads.housemanager;

import server.beans.HouseInfo;
import server.beans.HouseList;
import server.services.threads.AsyncResponseThread;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;

public class AddHouseThread extends AsyncResponseThread {
    HouseInfo h;

    public AddHouseThread(AsyncResponse response, HouseInfo h) {
        super(response);
        this.h=h;
    }

    @Override
    protected Response operation() {
        return HouseList.getInstance().add(h)?Response.ok(HouseList.getInstance()).build():Response.status(Response.Status.CONFLICT).build();
    }
}
