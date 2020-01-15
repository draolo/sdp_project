package server.services.threads.admin;

import server.beans.comunication.GlobalMeasurement;
import server.beans.comunication.Stats;
import server.beans.storage.GlobalMeasurementList;
import server.services.threads.AsyncResponseThread;
import server.various.Statistics;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import java.util.List;

public class GetGlobalStatsThread extends AsyncResponseThread {
    int limit;

    public GetGlobalStatsThread(AsyncResponse asyncResponse, int limit) {
        super(asyncResponse);
        this.limit=limit;
    }

    @Override
    protected Response operation() {
        List<GlobalMeasurement> list = GlobalMeasurementList.getInstance().getLastMeasurements(limit);
        double mean= Statistics.getMean(list);
        double sd=Statistics.getStdDev(list);
        Stats stats=new Stats(mean,sd);
        return Response.ok(stats).build();
    }
}
