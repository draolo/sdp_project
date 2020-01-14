package server.services.threads.admin;

import server.services.threads.AsyncResponseThread;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;

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
        // TODO: 14/01/2020 implement this shit
        return Response.ok().build();
    }
}
