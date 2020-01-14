package server.services.threads.measurementmanager;

import server.beans.comunication.GlobalStat;
import server.beans.storage.GlobalStats;
import server.services.threads.AsyncResponseThread;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;

public class AddGlobalMeasurementsThread extends AsyncResponseThread {

    GlobalStat g;

    public AddGlobalMeasurementsThread(AsyncResponse response, GlobalStat g) {
        super(response);
        this.g=g;
    }

    @Override
    protected Response operation() {
        return GlobalStats.getInstance().add(g)?Response.ok().build():Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
}
