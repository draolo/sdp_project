package house.houseListManager;

import server.beans.comunication.HouseInfo;

public interface EnterLeaveObserver {
    void onEnter(HouseInfo h);
    void onLeave(HouseInfo h);
}
