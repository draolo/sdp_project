package house;

import house.measurement.MeasurementManager;
import server.beans.comunication.HouseInfo;

import java.util.ArrayList;
import java.util.List;

public class HouseList {
    private List<HouseInfo> houseList;
    private static HouseList instance;

    private HouseList() {
        houseList = new ArrayList<>();
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
        for(HouseInfo h: houseList) {
            if (h.getId()==newHouse.getId()) {
                return false;
            }
        }
        MeasurementManager.getInstance().addHouse(newHouse);
        return houseList.add(newHouse);

    }

    public synchronized boolean del(int id) {
        for(HouseInfo h: houseList) {
            if (h.getId()==id) {
                MeasurementManager.getInstance().removeHouse(id);
                return houseList.remove(h);
            }
        }
        return false;
    }
}
