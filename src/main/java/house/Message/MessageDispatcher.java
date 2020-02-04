package house.Message;

import com.google.gson.Gson;
import house.Boost.BoostAck;
import house.Boost.BoostManager;
import house.Boost.BoostRequest;
import house.Configuration;
import house.Coordinator.Coordinator;
import house.Coordinator.election.ElectionManager;
import house.Main;
import house.houseListManager.HouseList;
import house.TimeManager;
import house.measurement.MeasurementManager;
import server.beans.comunication.HouseInfo;
import server.beans.comunication.LocalMeasurement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Logger;

public class MessageDispatcher extends Thread {
    Socket connection;
    private BufferedReader inFromClient;
    private Gson gson;

    public MessageDispatcher(Socket s){
        this.connection = s;
        gson=new Gson();
        try{
            this.inFromClient =new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.setName("message dispatcher");
    }

    @Override
    public void run() {
        Message message;
        try {
            message=gson.fromJson(inFromClient,Message.class);
        }catch (Exception e){
            Logger.getGlobal().severe("SEVERE: UNABLE TO FETCH MESSAGE");
            e.printStackTrace();
            return;
        }
        Logger.getGlobal().fine("RECEIVED MESSAGE "+message);

        //probably never used in the demonstration because all the process are on the same machine and so have the same time
        // but very useful to enforce partial ordination in a real life scenario
        TimeManager.testAndSet(message.timestamp);

        try {
            dispatchMessage(message);
        }catch (Exception e){
            Logger.getGlobal().severe("SEVERE: UNABLE TO PROCESS MESSAGE");
            Logger.getGlobal().severe(message.toString());
            e.printStackTrace();
        }finally {
            try {
                connection.close();
            } catch (IOException e) {
                Logger.getGlobal().warning("AN ERROR OCCUR WHILE CLOSING THE SOCKET");
                e.printStackTrace();
            }
        }

    }

    private void dispatchMessage(Message message){
        switch (message.type){
            case WELCOME:{
                Logger.getGlobal().fine("REQUEST TYPE WELCOME");
                HouseInfo houseInfo = gson.fromJson(message.content.toString(),HouseInfo.class);
                HouseList.getInstance().add(houseInfo);
                Logger.getGlobal().fine(HouseList.getInstance().toString());
                return;
            }
            case GOODBYE:{
                Logger.getGlobal().fine("REQUEST TYPE GOODBYE");
                HouseInfo houseInfo = gson.fromJson(message.content.toString(),HouseInfo.class);
                HouseList.getInstance().del(houseInfo);
                Logger.getGlobal().fine(HouseList.getInstance().toString());
                return;
            }
            case MEASUREMENT:{
                LocalMeasurement localMeasurement = gson.fromJson(message.content.toString(),LocalMeasurement.class);
                MeasurementManager.getInstance().addStat(localMeasurement);
                return;
            }
            case COORDINATOR_REQUEST:{
                HouseInfo coordinator= Coordinator.getInstance().getCoordinator();
                HouseInfo from = gson.fromJson(message.content.toString(),HouseInfo.class);
                Message reply=new Message(MessageType.COORDINATOR_REPLY, coordinator);
                MessageSender.sendTo(reply,from);
                return;
            }
            case COORDINATOR_REPLY:{
                HouseInfo houseInfo = gson.fromJson(message.content.toString(),HouseInfo.class);
                Coordinator.getInstance().setCoordinator(houseInfo, message.timestamp);
                return;
            }
            case BOOST_REQUEST:{
                BoostRequest boostRequest = gson.fromJson(message.content.toString(),BoostRequest.class);
                if(BoostManager.getInstance().ack(boostRequest)){
                    Message ack=new Message(MessageType.BOOST_ACK, new BoostAck(Configuration.houseInfo,boostRequest));
                    MessageSender.sendTo(ack,boostRequest.getFrom());
                }
                return;
            }
            case BOOST_ACK:{
                BoostAck ack = gson.fromJson(message.content.toString(),BoostAck.class);
                BoostManager.getInstance().getAck(ack);
                return;
            }
            case DEAD:{
                Logger.getGlobal().fine("REQUEST TYPE DEAD");
                HouseInfo houseInfo = gson.fromJson(message.content.toString(),HouseInfo.class);
                HouseList.getInstance().del(houseInfo);
                if (houseInfo.getId()==Configuration.houseInfo.getId() && !Configuration.isStopping){
                    Main.recovery();
                }
                Logger.getGlobal().fine(HouseList.getInstance().toString());
                return;
            }
            case ELECTION:{
                HouseInfo houseInfo = gson.fromJson(message.content.toString(),HouseInfo.class);
                ElectionManager.getInstance().receiveElectionRequest(houseInfo);
            }
            case ELECTION_ALIVE:{
                HouseInfo houseInfo = gson.fromJson(message.content.toString(),HouseInfo.class);
                ElectionManager.getInstance().receiveAlive(houseInfo);
            }
            default:{

            }
        }
    }
}
