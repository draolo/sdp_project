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
import java.util.stream.Collectors;

public class BoostManager implements EnterLeaveObserver {
    private BoostRequest actual;
    private BoostStatus status;
    private static BoostManager instance;
    private HashMap<Integer,Boolean> ack;

    private BoostManager() {
        //System.err.println("INITIALIZING BOOT MANAGER");
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
                System.err.println("BEGINNING BOOST REQUEST");
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
                    System.err.println("REQUEST STARTED AWAITING FOR ACK");
                    return true;
                }
            }
        }
    }

    //METHOD BOOST THROW InterruptedException do to smartmeter.boost
    public synchronized void getAck(BoostAck ack) throws InterruptedException {
        System.err.println("RECEIVED ACK FROM: "+ack.ackBy.getId());
        if (status!=BoostStatus.AWAIT || actual.timestamp!=ack.boostRequest.timestamp){
            return;
        }
        HouseInfo by= ack.ackBy;
        this.ack.put(by.getId(),true);
        Collection<Boolean> acks=this.ack.values();
        Predicate<Boolean> isTrue = h -> h;
        List<Boolean> result = acks.stream().filter(isTrue)
                .collect(Collectors.toList());
        System.err.println("RECEIVED "+result.size()+" ACK OUT OF "+acks.size());
        if(result.size()>=acks.size()-1){
            System.err.println("BOOST START");
            boost();
            System.err.println("BOOST END");
        }
    }

    private synchronized void boost() throws InterruptedException {
        setStatus(BoostStatus.BOOSTING);
        CommunicationWithServer.sendBoostNotification(actual);
        Configuration.smartMeterSimulator.boost();
        setStatus(BoostStatus.NOT_IN_USE);
    }

    //InterruptedException do to wait()
    public synchronized boolean ack(BoostRequest b) throws InterruptedException {
        System.err.println("RECEIVED BOOST REQUEST "+b);
        while (true){
            System.err.println("REQUEST "+b+" IS BEING ANALYZED");
            if (b.from.getId()==Configuration.houseInfo.getId()){
                System.err.println("REQUEST "+b+"  ACCEPTED BECAUSE COMES FROM ME");
                //accept the request because come from myself
                return true;
            }
            switch (status){
                case AWAIT:{
                    if (ack.getOrDefault(b.from.getId(),false)){
                        System.err.println("REQUEST "+b+" NOT ACCEPTED BECAUSE HE ALREADY ACK ME");
                        //don't ack if he have already ack me
                        break;
                    }
                    if (!ack.containsKey(b.from.getId())){
                        System.err.println("REQUEST "+b+" NOT ACCEPTED BECAUSE HE WASN'T IN THE NETWORK");
                        //older member use the force: if have send my request before that he enter into the network
                        // I will not ack him because he never got my request and so will never ack me back
                        break;
                    }
                    if(b.timestamp<actual.timestamp){
                        System.err.println("REQUEST "+b+" ACCEPTED BECAUSE OLDER THAN MINE "+actual);
                        return true;
                    }if(b.timestamp==actual.timestamp){
                        if(b.from.getId()<actual.from.getId()){
                            System.err.println("REQUEST "+b+" ACCEPTED BECAUSE IT HAS BETTER ID AND SAME TIMESTAMP "+actual);
                            return true;
                        }
                    }
                    break;
                }
                case NOT_IN_USE:{
                    System.err.println("REQUEST "+b+" ACCEPTED BECAUSE RESOURCE IS NOT IN USE");
                    return true;
                }
                default:{
                    System.err.println("REQUEST "+b+" NOT ACCEPTED BECAUSE WE ARE USING BOOST OR UNKNOWN REASON");
                    System.err.println(this);
                    break;
                }
            }
            System.err.println("REQUEST "+b+" IS NOW WAITING");
            wait();
            System.err.println("REQUEST "+b+" IS NOT WAITING");
        }
    }

    public synchronized void setStatus(BoostStatus s){
        System.err.println("STATUS CHANGED TO "+s);
        this.status=s;
        if(s==BoostStatus.NOT_IN_USE){
            notifyAll();
        }
        //System.err.println(this);
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
        if (status==BoostStatus.AWAIT){
            BoostAck mockACK= new BoostAck(h, actual);
            try {
                getAck(mockACK);
            } catch (InterruptedException e) {
                System.err.println("FAILED TO VALIDATE SELF MADE ACK");
                e.printStackTrace();
            }
        }
    }
}
