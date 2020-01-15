package server.beans.storage;



import server.beans.comunication.HouseInfo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "houseList")
@XmlAccessorType (XmlAccessType.FIELD)
public class HouseList {

    @XmlElement(name="houses")
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
        return houseList.add(newHouse);
    }

    public synchronized boolean del(int id) {
        for(HouseInfo h: houseList) {
            if (h.getId()==id) {
                return houseList.remove(h);
            }
        }
        return false;
    }
}