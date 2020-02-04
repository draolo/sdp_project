package house.Coordinator;

import house.Configuration;
import house.Coordinator.election.ElectionManager;
import house.Coordinator.election.ElectionStatus;
import house.Message.Message;
import house.Message.MessageSender;
import house.Message.MessageType;
import house.houseListManager.EnterLeaveObserver;
import server.beans.comunication.HouseInfo;


public class Coordinator implements EnterLeaveObserver {
    private HouseInfo coordinator;
    private volatile CoordinatorStatus status;
    private long lastEdit;
    private static Coordinator instance;

    private Coordinator() {
        //System.err.println("COORDINATOR INIT");
        coordinator=null;
        status= CoordinatorStatus.NOT_SET;
        lastEdit=0;
    }

    //singleton
    public synchronized static Coordinator getInstance(){
        if(instance==null)
            instance = new Coordinator();
        return instance;
    }

    public void requestCoordinator() {
        Message coordinatorMessage = new Message(MessageType.COORDINATOR_REQUEST, Configuration.houseInfo);
        MessageSender.sendToEveryBody(coordinatorMessage);
        synchronized (this) {
            try {
                wait(60 * 1000);
                if (!isCoordinatorSet()) {
                    startElection();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //InterruptedException due to wait()
    public synchronized HouseInfo getCoordinator() throws InterruptedException {
        System.err.println("COORDINATOR REQUEST");
        while (true){
            System.err.println("COORDINATOR REQUEST IS BEING ANALYZED");
            System.err.println(this);
            if (!isCoordinatorSet()){
                System.err.println("UNKNOWN COORDINATOR REQUEST PAUSED");
                wait();
            }else {
                return coordinator;
            }
        }
    }


    public synchronized void setCoordinator(HouseInfo houseInfo, long timestamp) {
        if(timestamp>=lastEdit) {
            System.err.println("COORDINATOR SET TO " + houseInfo);
            coordinator = houseInfo;
            setStatus(CoordinatorStatus.SET);
            ElectionManager.getInstance().setStatus(ElectionStatus.ELECTION_END);
            HouseInfo mySelf = Configuration.houseInfo;
            if (houseInfo.getId() == mySelf.getId()) {
                setStatus(CoordinatorStatus.COORDINATOR);
            }
            System.err.println(this);
        }else {
            System.err.println("COORDINATOR NOT SET BECAUSE REQUEST IS OLDER THAN THE LAST ONE");

        }
    }

    public CoordinatorStatus getStatus() {
        return this.status;
    }

    public synchronized void setStatus(CoordinatorStatus s){
        System.err.println("STATUS SET TO "+s);
        this.status=s;
        if(isCoordinatorSet()){
            notifyAll();
        }
        System.err.println(this);
    }

    private boolean isCoordinatorSet(){
        return (status== CoordinatorStatus.SET || status==CoordinatorStatus.COORDINATOR);
    }

    public synchronized void startElection() {
        setStatus(CoordinatorStatus.IN_ELECTION); //election starts in a new thread to avoid deadlock, and to avoid HouseList.remove to be very time consuming
        Thread t=new Thread(()-> {
            try {
                ElectionManager.getInstance().beginElection();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.start();
    }

    @Override
    public String toString() {
        return "Coordinator{" +
                "coordinator=" + coordinator +
                ", status=" + status +
                '}';
    }

    @Override
    public void onEnter(HouseInfo h) {

    }

    @Override
    public synchronized void onLeave(HouseInfo h){
        if (coordinator!=null&&coordinator.getId()==h.getId()&&!Configuration.isStopping){
            startElection();
        }
    }
}
