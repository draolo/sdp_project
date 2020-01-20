package house.Boost;

import house.HouseList;
import house.smartMeter.SmartMeterSimulator;
import server.beans.comunication.HouseInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BoostManager {
    private BoostRequest actual;
    private BoostStatus status;
    private static BoostManager instance;
    private HashMap<Integer,Boolean> ack;
    static SmartMeterSimulator smartMeterSimulator;

    private BoostManager() {
        actual=null;
        status= BoostStatus.NOT_IN_USE;
    }

    public synchronized static void setSmartMeter(SmartMeterSimulator sm){
        smartMeterSimulator=sm;
    }

    //singleton
    public synchronized static BoostManager getInstance(){
        if(instance==null)
            instance = new BoostManager();
        return instance;
    }

    public synchronized boolean request(BoostRequest b){
        if (status==BoostStatus.AWAIT){
            System.out.println("BOOST ALREADY REQUESTED");
            return false;
        }else {
            setStatus(BoostStatus.AWAIT);
            actual=b;
            ack= new HashMap<>();
            for (HouseInfo houseInfo:HouseList.getInstance().getHouseList()) {
                ack.put(houseInfo.getId(),false);
            }
            return true;
        }
    }

    public synchronized void getAck(HouseInfo by) throws InterruptedException {
        System.err.println("RECEIVED ACK FROM: "+by.getId());
        if (status!=BoostStatus.AWAIT){
            return;
        }
        ack.put(by.getId(),true);
        Collection<Boolean> acks=ack.values();
        Predicate<Boolean> isTrue = h -> h;
        List<Boolean> result = acks.stream().filter(isTrue)
                .collect(Collectors.toList());
        if(result.size()>=acks.size()-1){
            System.err.println("BOOST START");
            boost();
            System.err.println("BOOST END");
        }
    }

    private synchronized void boost() throws InterruptedException {
        setStatus(BoostStatus.BOOSTING);
        smartMeterSimulator.boost();
        setStatus(BoostStatus.NOT_IN_USE);
    }

    public synchronized boolean ack(BoostRequest b) throws InterruptedException {
        while (true){
            switch (status){
                case AWAIT:{
                    if (ack.getOrDefault(b.from.getId(),false)){
                        //don't ack if i have already ack me
                        break;
                    }
                    if (!ack.containsKey(b.from.getId())){
                        //older member use the force: if have send my request before that he enter into the network
                        // I will not ack him because he never got my request and so will never ack me back
                        break;
                    }
                    if(b.timestamp<actual.timestamp){
                        return true;
                    }if(b.timestamp==actual.timestamp){
                        return b.from.getId()<actual.from.getId();
                    }
                    break;
                }
                case NOT_IN_USE:{
                    return true;
                }
                default:{
                    break;
                }
            }
            wait();
        }
    }

    public synchronized void setStatus(BoostStatus s){
        this.status=s;
        if(s==BoostStatus.NOT_IN_USE){
            notifyAll();
        }
    }
}
