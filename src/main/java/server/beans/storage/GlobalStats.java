package server.beans.storage;



import server.beans.comunication.GlobalStat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class GlobalStats {

    @XmlElement(name="global")
    private List<GlobalStat> stats;
    private static GlobalStats instance;

    private GlobalStats() {
        stats = new ArrayList<>();
    }

    //singleton
    public synchronized static GlobalStats getInstance(){
        if(instance==null)
            instance = new GlobalStats();
        return instance;
    }

    public synchronized List<GlobalStat> getStats() {
        return new ArrayList<>(stats);
    }

    public synchronized boolean add(GlobalStat stat){
        return stats.add(stat);

    }

}