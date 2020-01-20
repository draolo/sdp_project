package house.Message;

import com.google.gson.Gson;
import house.Boost.BoostManager;
import house.Boost.BoostRequest;
import house.Coordinator.Coordinator;
import house.HouseList;
import house.measurement.MeasurementManager;
import server.beans.comunication.HouseInfo;
import server.beans.comunication.LocalMeasurement;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MessageDispatcher extends Thread {
    Socket connection;
    private BufferedReader inFromClient;
    private DataOutputStream outToClient;
    private Gson gson;

    public MessageDispatcher(Socket s) throws IOException {
        this.connection = s;
        gson=new Gson();
        try{
            this.inFromClient =
                    new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));

            this.outToClient =
                    new DataOutputStream(connection.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            s.close();
        }
    }

    @Override
    public void run() {

        Message message=gson.fromJson(inFromClient,Message.class);
        try {
            dispatchMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dispatchMessage(Message message) throws InterruptedException, IOException {
        switch (message.type){
            case WELCOME:{
                HouseInfo houseInfo = gson.fromJson(message.content.toString(),HouseInfo.class);
                HouseList.getInstance().add(houseInfo);
                return;
            }
            case GOODBYE:{
                HouseInfo houseInfo = gson.fromJson(message.content.toString(),HouseInfo.class);
                HouseList.getInstance().del(houseInfo.getId());
                return;
            }
            case MEASUREMENT:{
                LocalMeasurement localMeasurement = gson.fromJson(message.content.toString(),LocalMeasurement.class);
                MeasurementManager.getInstance().addStat(localMeasurement);
                return;
            }
            case COORDINATOR_REQUEST:{
                HouseInfo coordinator= Coordinator.getInstance().getCoordinator();
                Message reply=new Message(MessageType.COORDINATOR_REPLY, coordinator);
                String jsonObj = gson.toJson(reply);
                outToClient.writeBytes(jsonObj + '\n');
                return;
            }
            case COORDINATOR_REPLY:{
                HouseInfo houseInfo = gson.fromJson(message.content.toString(),HouseInfo.class);
                Coordinator.getInstance().setCoordinator(houseInfo);
                return;
            }
            case BOOST_REQUEST:{
                BoostRequest boostRequest = gson.fromJson(message.content.toString(),BoostRequest.class);
                BoostManager.getInstance().ack(boostRequest);
                return;
            }
            case BOOST_ACK:{
                HouseInfo houseInfo = gson.fromJson(message.content.toString(),HouseInfo.class);
                BoostManager.getInstance().getAck(houseInfo);
                return;
            }
            default:{

            }
        }
    }
}
