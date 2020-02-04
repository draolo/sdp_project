package house.Coordinator.election;

import house.Configuration;
import house.Message.Message;
import house.Message.MessageSender;
import house.Message.MessageType;
import house.houseListManager.HouseList;
import server.beans.comunication.HouseInfo;

import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ElectionManager {
    private static ElectionManager instance;
    private ElectionStatus status;

    private ElectionManager() {
        Logger.getGlobal().finer("INITIALIZING ELECTION MANAGER");
        status= ElectionStatus.ELECTION_END;
    }

    //singleton
    public synchronized static ElectionManager getInstance(){
        if(instance==null)
            instance = new ElectionManager();
        return instance;
    }

    public synchronized void beginElection(){
        if(status!=ElectionStatus.ELECTION_END){
            return;
        }
        do {
            setStatus(ElectionStatus.LEADER);
            List<HouseInfo> list = HouseList.getInstance().getHouseList();
            Predicate<HouseInfo> isBigger = h -> h.getId() > Configuration.houseInfo.getId();
            List<HouseInfo> result = list.stream().filter(isBigger)
                    .collect(Collectors.toList());
            if (result.size() == 0) {
                winner();
                return;
            }
            Message message = new Message(MessageType.ELECTION, Configuration.houseInfo);
            MessageSender.sendToGroup(message, result);
            try {
                wait(30 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (status == ElectionStatus.LEADER) {
                winner();
                return;
            }
        }while (status!=ElectionStatus.ELECTION_END);
    }

    private void winner() {
        Message message=new Message(MessageType.COORDINATOR_REPLY, Configuration.houseInfo);
        MessageSender.sendToEveryBody(message);
        setStatus(ElectionStatus.ELECTION_END);
    }

    public synchronized void setStatus(ElectionStatus electionStatus){
        this.status=electionStatus;
        if (electionStatus==ElectionStatus.ELECTION_END){
            notifyAll();
        }
    }

    public synchronized void receiveAlive(HouseInfo from) {
        if (status == ElectionStatus.LEADER) {
            if (from.getId() > Configuration.houseInfo.getId()) {
                setStatus(ElectionStatus.ELECTION_START);
            }
        }
    }

    public synchronized void receiveElectionRequest(HouseInfo from){
        switch (status){
            case LEADER:{
                if (Configuration.houseInfo.getId()>from.getId()){
                    Message message=new Message(MessageType.ELECTION_ALIVE, Configuration.houseInfo);
                    MessageSender.sendTo(message,from);
                }
            }case ELECTION_END:{
                if (Configuration.houseInfo.getId()>from.getId()) {
                    Message message = new Message(MessageType.ELECTION_ALIVE, Configuration.houseInfo);
                    MessageSender.sendTo(message, from);
                    beginElection();
                }
            }case ELECTION_START:{
                if (Configuration.houseInfo.getId()>from.getId()){
                    Message message=new Message(MessageType.ELECTION_ALIVE, Configuration.houseInfo);
                    MessageSender.sendTo(message,from);
                }
            }
        }
    }



}
