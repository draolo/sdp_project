package server.beans.storage;



import server.beans.comunication.LocalMeasurement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        int id=stat.getId();
        List<LocalMeasurement> measurements=stats.getOrDefault(id,new ArrayList<>());
        measurements.add(stat);
        stats.put(id, measurements);
        return true;
    }

}