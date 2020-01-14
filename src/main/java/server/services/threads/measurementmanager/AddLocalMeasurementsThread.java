package server.services.threads.measurementmanager;

import server.beans.comunication.LocalMeasurement;
import server.beans.storage.LocalMeasurementList;
import server.services.threads.AsyncResponseThread;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;

public class AddLocalMeasurementsThread extends AsyncResponseThread {
    LocalMeasurement l;

    public AddLocalMeasurementsThread(AsyncResponse response, LocalMeasurement l) {
        super(response);
        this.l=l;
    }

    @Override
    protected Response operation() {
        // TODO: 14/01/2020 in a real world scenario we could check if the house is part of the network and make a little bit of validation based on the timestamp
        return LocalMeasurementList.getInstance().add(l)?Response.ok().build():Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

    }
}
