package house.measurement;

import house.CommunicationWithServer;
import house.Coordinator.Coordinator;
import house.Coordinator.CoordinatorStatus;
import house.houseListManager.EnterLeaveObserver;
import server.beans.comunication.GlobalMeasurement;
import server.beans.comunication.HouseInfo;
import server.beans.comunication.LocalMeasurement;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;
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
        Logger.getGlobal().finer("MEASUREMENT MANAGER INIT");
        measurements = new HashMap<>();
        fresh=new HashMap<>();
    }

    public synchronized void addHouse(HouseInfo h){
        //new house count as fresh measurement otherwise we could wait forever until a new measurement is send
        Logger.getGlobal().finer("ADDING NEW HOUSE "+h);
        measurements.put(h.getId(),new LocalMeasurement(h.getId(),0.,0));
        fresh.put(h.getId(),true);
        Logger.getGlobal().finer(this.toString());
    }

    public synchronized void removeHouse(int id){
        Logger.getGlobal().finer("REMOVING HOUSE "+id);
        measurements.remove(id);
        fresh.remove(id);
        Logger.getGlobal().finer(this.toString());
    }

    public synchronized void addStat(LocalMeasurement localMeasurement){
        Logger.getGlobal().finer("ADDING NEW MEASUREMENT "+localMeasurement);
        LocalMeasurement old= measurements.getOrDefault(localMeasurement.getId(),new LocalMeasurement(localMeasurement.getId(),0.,0));
        if(old.getTimestamp()<localMeasurement.getTimestamp()){
            measurements.put(localMeasurement.getId(), localMeasurement);
            fresh.put(localMeasurement.getId(), true);
            Logger.getGlobal().finer(this.toString());
            if (Coordinator.getInstance().getStatus() == CoordinatorStatus.COORDINATOR && areAllFresh()) {
                Logger.getGlobal().fine("ALL MEASUREMENT ARE FRESH "+localMeasurement);
                GlobalMeasurement globalMeasurement = this.globalValue();
                CommunicationWithServer.sendGlobalMeasurement(globalMeasurement);
                fresh.replaceAll((k, v) -> false);
                Logger.getGlobal().fine("RESET MEASUREMENT");
                Logger.getGlobal().fine(this.toString());
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
