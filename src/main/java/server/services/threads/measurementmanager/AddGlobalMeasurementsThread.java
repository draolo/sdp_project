package server.services.threads.measurementmanager;

import server.beans.comunication.GlobalMeasurement;
import server.beans.storage.GlobalMeasurementList;
import server.services.threads.AsyncResponseThread;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;

public class AddGlobalMeasurementsThread extends AsyncResponseThread {

    GlobalMeasurement g;

    public AddGlobalMeasurementsThread(AsyncResponse response, GlobalMeasurement g) {
        super(response);
        this.g=g;
    }

    @Override
    protected Response operation() {
        return GlobalMeasurementList.getInstance().add(g)?Response.ok().build():Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
}
