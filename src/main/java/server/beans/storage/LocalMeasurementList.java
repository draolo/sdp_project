package server.beans.storage;



import server.beans.comunication.LocalMeasurement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class LocalMeasurementList {

    @XmlElement(name="local")
    private Map<Integer, List<LocalMeasurement>> stats;
    private static LocalMeasurementList instance;

    private LocalMeasurementList() {
        stats = new HashMap<>();
    }

    //singleton
    public synchronized static LocalMeasurementList getInstance(){
        if(instance==null)
            instance = new LocalMeasurementList();
        return instance;
    }

    public synchronized Map<Integer,List<LocalMeasurement>> getStats() {
        return new HashMap<>(stats);
    }

    public synchronized boolean add(LocalMeasurement stat){
        System.out.println("add local "+stat.toString());
        int id=stat.getId();
        List<LocalMeasurement> measurements=stats.getOrDefault(id,new ArrayList<>());
        measurements.add(stat);
        stats.put(id, measurements);
        return true;
    }

    public List<LocalMeasurement> getLastMeasurements(int id, int limit) {
        Map<Integer, List<LocalMeasurement>> map= LocalMeasurementList.getInstance().getStats();
        List<LocalMeasurement> list= map.get(id);
        if (list==null){
            list=new ArrayList<>();
        }
        Collections.sort(list);
        if(limit >0&& limit <list.size()){
            list=list.subList(0, limit);
        }
        return list;
    }

}