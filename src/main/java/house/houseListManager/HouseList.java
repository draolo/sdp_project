package house.houseListManager;

import server.beans.comunication.HouseInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class HouseList {
    private List<HouseInfo> houseList;
    private static HouseList instance;
    private Set<EnterLeaveObserver> observers;

    private HouseList() {
        Logger.getGlobal().finer("INIT HOUSE LIST");
        houseList = new ArrayList<>();
        observers = new HashSet<>();
    }

    //singleton
    public synchronized static HouseList getInstance(){
        if(instance==null)
            instance = new HouseList();
        return instance;
    }

    public synchronized List<HouseInfo> getHouseList() {
        return new ArrayList<>(houseList);
    }

    public synchronized boolean add(HouseInfo newHouse){
        Logger.getGlobal().fine("ADDING HOUSE "+newHouse);
        for(HouseInfo h: houseList) {
            if (h.getId()==newHouse.getId()) {
                return false;
            }
        }
        houseList.add(newHouse);
        notifyEnter(newHouse);
        return true;
    }

    public synchronized boolean del(HouseInfo house) {
        Logger.getGlobal().info("REMOVING HOUSE "+ house.getId());
        boolean ret=houseList.removeIf(h->h.getId()==house.getId()&&h.getPort()==house.getPort()&&h.getIp().equals(house.getIp()));
        if (ret){
            Logger.getGlobal().info("REMOVED HOUSE "+house.getId());
            notifyLeave(house);
        }
        Logger.getGlobal().info("notify complete");
        return ret;
    }

    @Override
    public String toString() {
        return "HouseList{" +
                "houseList=" + houseList +
                '}';
    }

    public boolean addObserver(EnterLeaveObserver observer){
        return observers.add(observer);
    }

    public boolean removeObserver(EnterLeaveObserver observer){
        return observers.remove(observer);
    }
    
    private void notifyEnter(HouseInfo h){
        for (EnterLeaveObserver observer:observers) {
            observer.onEnter(h);
        }
    }
    private void notifyLeave(HouseInfo h){
        for (EnterLeaveObserver observer:observers) {
            observer.onLeave(h);
        }

    }

    public synchronized void clean(){
        List<HouseInfo> list=this.getHouseList();
        for (HouseInfo houseInfo:list) {
            this.del(houseInfo);
        }
    }
}
