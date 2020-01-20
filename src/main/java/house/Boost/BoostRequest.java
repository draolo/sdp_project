package house.Boost;

import server.beans.comunication.HouseInfo;

public class BoostRequest {
    HouseInfo from;
    long timestamp;

    public BoostRequest(HouseInfo from, long timestamp) {
        this.from = from;
        this.timestamp = timestamp;
    }
}
