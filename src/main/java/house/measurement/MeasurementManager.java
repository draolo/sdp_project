package house.measurement;

import house.Coordinator.Coordinator;
import house.Coordinator.CoordinatorStatus;
import house.CommunicationWithServer;
import house.houseListManager.EnterLeaveObserver;
import house.houseListManager.HouseList;
import server.beans.comunication.GlobalMeasurement;
import server.beans.comunication.HouseInfo;
import server.beans.comunication.LocalMeasurement;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MeasurementManager implements EnterLeaveObserver {

    private static MeasurementManager instance;
    private HashMap<Integer, LocalMeasurement> measurements;
    private HashMap<Integer, Boolean> fresh;


    //singleton
    public synchronized static MeasurementManager getInstance(){
        if(instance==null)
            instance = new MeasurementManager();
        return instance;
    }

    private MeasurementManager(){
        //System.err.println("MEASUREMENT MANAGER INIT");
        measurements = new HashMap<>();
        fresh=new HashMap<>();
        HouseList.getInstance().addObserver(this);
    }

    public synchronized void addHouse(HouseInfo h){
        //new house count as fresh measurement otherwise we could wait forever until a new measurement is send
        //System.err.println("ADDING NEW HOUSE "+h);
        measurements.put(h.getId(),new LocalMeasurement(h.getId(),0.,0));
        fresh.put(h.getId(),true);
        //System.err.println(this);
    }

    public synchronized void removeHouse(int id){
        //System.err.println("REMOVING HOUSE "+id);
        measurements.remove(id);
        fresh.remove(id);
    }

    public synchronized void addStat(LocalMeasurement localMeasurement){
        //System.err.println("ADDING NEW MEASUREMENT "+localMeasurement);
        LocalMeasurement old= measurements.getOrDefault(localMeasurement.getId(),new LocalMeasurement(localMeasurement.getId(),0.,0));
        if(old.getTimestamp()<localMeasurement.getTimestamp()){
            measurements.put(localMeasurement.getId(), localMeasurement);
            fresh.put(localMeasurement.getId(), true);
            //System.err.println(this);
            if (Coordinator.getInstance().getStatus() == CoordinatorStatus.COORDINATOR && areAllFresh()) {
                //System.err.println("ALL MEASUREMENT ARE FRESH "+localMeasurement);
                GlobalMeasurement globalMeasurement = this.globalValue();
                CommunicationWithServer.sendGlobalMeasurement(globalMeasurement);
                fresh.replaceAll((k, v) -> false);
                //System.err.println("RESET MEASUREMENT");
                //System.err.println(this);
            }
        }
    }

    private GlobalMeasurement globalValue(){
        double sum=0.;
        long timestamp=0;

        for (LocalMeasurement measurement: measurements.values()) {
            sum+=measurement.getValue();
            timestamp=Math.max(timestamp,measurement.getTimestamp());
        }
        return new GlobalMeasurement(sum, timestamp);
    }

    private boolean areAllFresh() {
        Collection<Boolean> values=fresh.values();
        Predicate<Boolean> isFalse = h -> !h;
        List<Boolean> result = values.stream().filter(isFalse)
                .collect(Collectors.toList());
        return result.size()==0;
    }

    @Override
    public String toString() {
        return "MeasurementManager{" +
                "measurement=" + measurements +
                ", fresh=" + fresh +
                '}';
    }

    @Override
    public void onEnter(HouseInfo h) {
        addHouse(h);
    }

    @Override
    public void onLeave(HouseInfo h) {
        removeHouse(h.getId());
    }
}
