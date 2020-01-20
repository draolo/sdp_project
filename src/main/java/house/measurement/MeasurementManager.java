package house.measurement;

import house.Coordinator.Coordinator;
import house.Coordinator.CoordinatorStatus;
import server.beans.comunication.HouseInfo;
import server.beans.comunication.LocalMeasurement;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MeasurementManager {

    private static MeasurementManager instance;
    private HashMap<Integer, Double> measurement;
    private HashMap<Integer, Boolean> fresh;


    //singleton
    public synchronized static MeasurementManager getInstance(){
        if(instance==null)
            instance = new MeasurementManager();
        return instance;
    }

    private MeasurementManager(){
        measurement= new HashMap<>();
        fresh=new HashMap<>();
    }

    public synchronized void addHouse(HouseInfo h){
        //new house count as fresh measurement otherwise we could wait forever until a new measurement is send
        measurement.put(h.getId(),0.);
        fresh.put(h.getId(),true);
    }

    public synchronized void removeHouse(int id){
        measurement.remove(id);
        fresh.remove(id);
    }

    public synchronized void addStat(LocalMeasurement localMeasurement){
        measurement.put(localMeasurement.getId(),localMeasurement.getValue());
        fresh.put(localMeasurement.getId(),true);

        if(Coordinator.getInstance().getStatus() == CoordinatorStatus.COORDINATOR && areAllFresh()){
            // TODO: 20/01/2020 communicate the global measurement
            fresh.replaceAll((k,v) -> false);
        }
    }

    private boolean areAllFresh() {
        Collection<Boolean> values=fresh.values();
        Predicate<Boolean> isFalse = h -> !h;
        List<Boolean> result = values.stream().filter(isFalse)
                .collect(Collectors.toList());
        return result.size()==0;
    }


}
