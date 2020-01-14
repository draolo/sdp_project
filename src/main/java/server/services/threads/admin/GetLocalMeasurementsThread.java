package server.services.threads.admin;

import server.beans.comunication.LocalMeasurement;
import server.beans.storage.LocalMeasurementList;
import server.services.threads.AsyncResponseThread;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import java.util.List;

public class GetLocalMeasurementsThread extends AsyncResponseThread {
    int id;
    int limit;

    public GetLocalMeasurementsThread(AsyncResponse response, int id, int limit) {
        super(response);
        this.id=id;
        this.limit=limit;
    }

    @Override
    protected Response operation() {
        List<LocalMeasurement> list = LocalMeasurementList.getInstance().getLastMeasurements(id, limit);
        return Response.ok(list).build();
    }


}
