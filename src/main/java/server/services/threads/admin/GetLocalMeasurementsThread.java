package server.services.threads.admin;

import server.beans.comunication.LocalMeasurement;
import server.beans.storage.LocalMeasurementList;
import server.services.threads.AsyncResponseThread;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
        Map<Integer, List<LocalMeasurement>>  map= LocalMeasurementList.getInstance().getStats();
        List<LocalMeasurement> list= map.get(id);
        if (list==null){
            //return Response.status(Response.Status.NOT_FOUND).build();
            list=new ArrayList<>();
        }
        Collections.sort(list);
        if(limit>0&&limit<list.size()){
            list=list.subList(0,limit);
        }
        return Response.ok(list).build();
    }
}
