package server.services.threads.admin;

import server.beans.comunication.GlobalMeasurement;
import server.beans.storage.GlobalMeasurementList;
import server.services.threads.AsyncResponseThread;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import java.util.List;

public class GetGlobalMeasurementsThread extends AsyncResponseThread {
    private int limit;

    public GetGlobalMeasurementsThread(AsyncResponse asyncResponse, int limit) {
        super(asyncResponse);
        this.limit=limit;
    }

    @Override
    protected Response operation() {
        List<GlobalMeasurement> list = GlobalMeasurementList.getInstance().getLastMeasurements(limit);
        return Response.ok(list).build();
    }


}
