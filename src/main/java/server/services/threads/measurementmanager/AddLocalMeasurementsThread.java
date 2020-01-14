package server.services.threads.measurementmanager;

import server.beans.comunication.LocalStat;
import server.beans.storage.LocalStats;
import server.services.threads.AsyncResponseThread;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;

public class AddLocalMeasurementsThread extends AsyncResponseThread {
    LocalStat l;

    public AddLocalMeasurementsThread(AsyncResponse response, LocalStat l) {
        super(response);
        this.l=l;
    }

    @Override
    protected Response operation() {
        return LocalStats.getInstance().add(l)?Response.ok().build():Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

    }
}
