package house.Boost;

import house.Configuration;
import house.CommunicationWithServer;
import house.houseListManager.EnterLeaveObserver;
import house.houseListManager.HouseList;
import house.Message.Message;
import house.Message.MessageSender;
import house.Message.MessageType;
import server.beans.comunication.HouseInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class BoostManager implements EnterLeaveObserver {
    private BoostRequest actual;
    private BoostStatus status;
    private static BoostManager instance;
    private HashMap<Integer,Boolean> ack;

    private BoostManager() {
        Logger.getGlobal().finer("INITIALIZING BOOT MANAGER");
        actual=null;
        status= BoostStatus.NOT_IN_USE;
    }

    //singleton
    public synchronized static BoostManager getInstance(){
        if(instance==null)
            instance = new BoostManager();
        return instance;
    }

    public boolean request() {
        synchronized (HouseList.getInstance()) { //avoid deadlock
            synchronized (this) {
                Logger.getGlobal().info("BEGINNING BOOST REQUEST");
                if (status == BoostStatus.AWAIT) {
                    System.out.println("BOOST ALREADY REQUESTED");
                    return false;
                } else {
                    setStatus(BoostStatus.AWAIT);
                    actual = new BoostRequest(Configuration.houseInfo);
                    ack = new HashMap<>();
                    for (HouseInfo houseInfo : HouseList.getInstance().getHouseList()) {
                        ack.put(houseInfo.getId(), false);
                    }
                    BoostRequest boostRequest = actual;
                    Message message = new Message(MessageType.BOOST_REQUEST, boostRequest);
                    //Thread.sleep(15000);
                    MessageSender.sendToEveryBody(message);
                    Logger.getGlobal().info("REQUEST STARTED AWAITING FOR ACK");
                    return true;
                }
            }
        }
    }


    public synchronized void getAck(BoostAck ack){
        Logger.getGlobal().info("RECEIVED ACK FROM: "+ack.ackBy.getId());
        if (status!=BoostStatus.AWAIT || actual.timestamp!=ack.boostRequest.timestamp){
            return;
        }
        HouseInfo by= ack.ackBy;
        this.ack.put(by.getId(),true);
        Collection<Boolean> acks=this.ack.values();
        Predicate<Boolean> isTrue = h -> h;
        List<Boolean> result = acks.stream().filter(isTrue)
                .collect(Collectors.toList());
        Logger.getGlobal().info("RECEIVED "+result.size()+" ACK OUT OF "+acks.size());
        if(result.size()>=acks.size()-1){
            System.out.println("BOOST START");
            boost();
            System.out.println("BOOST END");
        }
    }

    private synchronized void boost(){
        setStatus(BoostStatus.BOOSTING);
        CommunicationWithServer.sendBoostNotification(actual);
        try {
            Configuration.smartMeterSimulator.boost();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setStatus(BoostStatus.NOT_IN_USE);
    }


    public synchronized boolean ack(BoostRequest b){
        Logger.getGlobal().info("RECEIVED BOOST REQUEST "+b);
        while (true){
            Logger.getGlobal().info("REQUEST "+b+" IS BEING ANALYZED");
            if (b.from.getId()==Configuration.houseInfo.getId()){
                Logger.getGlobal().info("REQUEST "+b+"  ACCEPTED BECAUSE COMES FROM ME");
                //accept the request because come from myself
                return true;
            }
            switch (status){
                case AWAIT:{
                    if (ack.getOrDefault(b.from.getId(),false)){
                        Logger.getGlobal().info("REQUEST "+b+" NOT ACCEPTED BECAUSE HE ALREADY ACK ME");
                        //don't ack if he have already ack me
                        break;
                    }
                    if (!ack.containsKey(b.from.getId())){
                        Logger.getGlobal().info("REQUEST "+b+" NOT ACCEPTED BECAUSE HE WASN'T IN THE NETWORK");
                        //older member use the force: if have send my request before that he enter into the network
                        // I will not ack him because he never got my request and so will never ack me back
                        break;
                    }
                    if(b.timestamp<actual.timestamp){
                        Logger.getGlobal().info("REQUEST "+b+" ACCEPTED BECAUSE OLDER THAN MINE "+actual);
                        return true;
                    }if(b.timestamp==actual.timestamp){
                        if(b.from.getId()<actual.from.getId()){
                            Logger.getGlobal().info("REQUEST "+b+" ACCEPTED BECAUSE IT HAS BETTER ID AND SAME TIMESTAMP "+actual);
                            return true;
                        }
                    }
                    break;
                }
                case NOT_IN_USE:{
                    Logger.getGlobal().info("REQUEST "+b+" ACCEPTED BECAUSE RESOURCE IS NOT IN USE");
                    return true;
                }
                default:{
                    Logger.getGlobal().info("REQUEST "+b+" NOT ACCEPTED BECAUSE WE ARE USING BOOST OR UNKNOWN REASON");
                    Logger.getGlobal().info(this.toString());
                    break;
                }
            }
            Logger.getGlobal().info("REQUEST "+b+" IS NOW WAITING");
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Logger.getGlobal().info("REQUEST "+b+" IS NOT WAITING");
        }
    }

    public synchronized void setStatus(BoostStatus s){
        Logger.getGlobal().info("STATUS CHANGED TO "+s);
        this.status=s;
        if(s==BoostStatus.NOT_IN_USE){
            notifyAll();
        }
        Logger.getGlobal().fine(this.toString());
    }

    @Override
    public String toString() {
        return "BoostManager{" +
                "actual=" + actual +
                ", status=" + status +
                ", ack=" + ack +
                '}';
    }

    @Override
    public void onEnter(HouseInfo h) {

    }

    @Override
    public synchronized void onLeave(HouseInfo h) {
        if (status==BoostStatus.AWAIT&&!Configuration.isStopping){
            BoostAck mockACK= new BoostAck(h, actual);
            getAck(mockACK);
        }
    }
}
