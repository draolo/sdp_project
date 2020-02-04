package house.Boost;

import house.TimeManager;
import server.beans.comunication.HouseInfo;

public class BoostRequest {
    HouseInfo from;
    long timestamp;

    public HouseInfo getFrom() {
        return from;
    }

    public void setFrom(HouseInfo from) {
        this.from = from;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public BoostRequest(HouseInfo from, long timestamp) {
        this.from = from;
        this.timestamp = timestamp;
    }

    public BoostRequest(HouseInfo houseInfo) {
        this.from=houseInfo;
        this.timestamp= TimeManager.getTime();
    }

    @Override
    public String toString() {
        return "BoostRequest{" +
                "from=" + from +
                ", timestamp=" + timestamp +
                '}';
    }
}
