package server.beans.storage;



import server.beans.comunication.LocalStat;

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
public class LocalStats {

    @XmlElement(name="local")
    private Map<Integer, List<LocalStat>> stats;
    private static LocalStats instance;

    private LocalStats() {
        stats = new HashMap<>();
    }

    //singleton
    public synchronized static LocalStats getInstance(){
        if(instance==null)
            instance = new LocalStats();
        return instance;
    }

    public synchronized Map<Integer,List<LocalStat>> getStats() {
        return new HashMap<>(stats);
    }

    public synchronized boolean add(LocalStat stat){
        int id=stat.getId();
        List<LocalStat> measurements=stats.getOrDefault(id,new ArrayList<>());
        measurements.add(stat);
        stats.put(id, measurements);
        return true;
    }

}