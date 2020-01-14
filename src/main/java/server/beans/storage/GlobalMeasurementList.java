package server.beans.storage;



import server.beans.comunication.GlobalMeasurement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class GlobalMeasurementList {

    @XmlElement(name="global")
    private List<GlobalMeasurement> stats;
    private static GlobalMeasurementList instance;

    private GlobalMeasurementList() {
        stats = new ArrayList<>();
    }

    //singleton
    public synchronized static GlobalMeasurementList getInstance(){
        if(instance==null)
            instance = new GlobalMeasurementList();
        return instance;
    }

    public synchronized List<GlobalMeasurement> getStats() {
        return new ArrayList<>(stats);
    }

    public synchronized boolean add(GlobalMeasurement stat){
        return stats.add(stat);

    }

}