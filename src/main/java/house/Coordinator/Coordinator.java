package house.Coordinator;

import server.beans.comunication.HouseInfo;

public class Coordinator {
    private HouseInfo coordinator;
    private CoordinatorStatus status;
    private static Coordinator instance;

    private Coordinator() {
        coordinator=null;
        status= CoordinatorStatus.NOT_SET;
    }

    //singleton
    public synchronized static Coordinator getInstance(){
        if(instance==null)
            instance = new Coordinator();
        return instance;
    }

    public synchronized HouseInfo getCoordinator() throws InterruptedException {
        while (true){
            if (status!= CoordinatorStatus.SET){
                wait();
            }else {
                return coordinator;
            }
        }
    }

    public synchronized void setCoordinator(HouseInfo houseInfo) {
        coordinator=houseInfo;
        setStatus(CoordinatorStatus.SET);
    }

    public synchronized CoordinatorStatus getStatus() {
        return this.status;
    }

    public synchronized void setStatus(CoordinatorStatus s){
        this.status=s;
        if(s== CoordinatorStatus.SET){
            notifyAll();
        }
    }
}
