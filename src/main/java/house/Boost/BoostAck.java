package house.Boost;

import server.beans.comunication.HouseInfo;

public class BoostAck {
    HouseInfo ackBy;
    BoostRequest boostRequest;

    public BoostAck(HouseInfo ackBy, BoostRequest boostRequest) {
        this.ackBy = ackBy;
        this.boostRequest = boostRequest;
    }

    public HouseInfo getAckBy() {
        return ackBy;
    }

    public void setAckBy(HouseInfo ackBy) {
        this.ackBy = ackBy;
    }

    public BoostRequest getBoostRequest() {
        return boostRequest;
    }

    public void setBoostRequest(BoostRequest boostRequest) {
        this.boostRequest = boostRequest;
    }

    @Override
    public String toString() {
        return "BoostAck{" +
                "ackBy=" + ackBy +
                ", boostRequest=" + boostRequest +
                '}';
    }
}
