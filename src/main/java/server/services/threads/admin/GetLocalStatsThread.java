package server.services.threads.admin;

import server.beans.comunication.LocalMeasurement;
import server.beans.comunication.Stats;
import server.beans.storage.LocalMeasurementList;
import server.services.threads.AsyncResponseThread;
import server.various.Statistics;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import java.util.List;

public class GetLocalStatsThread extends AsyncResponseThread {
    int id;
    int limit;
    public GetLocalStatsThread(AsyncResponse asyncResponse, int id, int limit) {
        super(asyncResponse);
        this.limit=limit;
        this.id=id;
    }

    @Override
    protected Response operation() {
        List<LocalMeasurement> list = LocalMeasurementList.getInstance().getLastMeasurements(id, limit);
        double mean= Statistics.getMean(list);
        double sd=Statistics.getStdDev(list);
        Stats stats=new Stats(mean,sd);
        return Response.ok(stats).build();
    }
}
