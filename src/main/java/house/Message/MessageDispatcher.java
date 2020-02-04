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
            System.err.println("SEVERE: UNABLE TO FETCH MESSAGE");
            e.printStackTrace();
            return;
        }
        //System.err.println("RECEIVED MESSAGE "+message);
        long delta=message.timestamp- TimeManager.getTime();
        if(delta>0){
            //probably never used in the demonstration because all the process are on the same machine and so have the same time
            // but very useful to enforce partial ordination in a real life scenario
            TimeManager.addDelta(delta+1);
        }
        try {
            dispatchMessage(message);
        } catch (InterruptedException e) {

            e.printStackTrace();
        } catch (Exception e){
            System.err.println("SEVERE: UNABLE TO PROCESS MESSAGE");
            System.err.println(message);
            e.printStackTrace();
        }finally {
            try {
                connection.close();
            } catch (IOException e) {
                System.err.println("AN ERROR OCCUR WHILE CLOSING THE SOCKET");
                e.printStackTrace();
            }
        }

    }

    private void dispatchMessage(Message message) throws InterruptedException {
        switch (message.type){
            case WELCOME:{
                //System.err.println("REQUEST TYPE WELCOME");
                HouseInfo houseInfo = gson.fromJson(message.content.toString(),HouseInfo.class);
                HouseList.getInstance().add(houseInfo);
                //System.err.println(HouseList.getInstance());
                return;
            }
            case GOODBYE:{
                //System.err.println("REQUEST TYPE GOODBYE");
                HouseInfo houseInfo = gson.fromJson(message.content.toString(),HouseInfo.class);
                HouseList.getInstance().del(houseInfo);
                //System.err.println(HouseList.getInstance());
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
                //System.err.println("REQUEST TYPE DEAD");
                HouseInfo houseInfo = gson.fromJson(message.content.toString(),HouseInfo.class);
                HouseList.getInstance().del(houseInfo);
                if (houseInfo.getId()==Configuration.houseInfo.getId() && !Configuration.isStopping){
                    Main.recovery();
                }
                //System.err.println(HouseList.getInstance());
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
