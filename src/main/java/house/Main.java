package house;

import house.Boost.BoostManager;
import house.Coordinator.Coordinator;
import house.Message.Message;
import house.Message.MessageReceiver;
import house.Message.MessageSender;
import house.Message.MessageType;
import house.houseListManager.HouseList;
import house.measurement.MeasurementManager;
import house.smartMeter.SmartMeterSimulator;
import server.beans.comunication.HouseInfo;

import javax.ws.rs.client.ClientBuilder;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


//see if args contains setup data
//if not ask setup data
//start smart meter
//register to admin server
//if ok continue else raise exception
//parsing house list
//enter the network

public class Main {
    public static void main(String[] args)  {
        Thread.currentThread().setName("Main-application");
        Scanner fromUser;
        Logger.getGlobal().setLevel(Level.WARNING);
        try {
            fromUser = new Scanner(System.in);
            initialSetup(args);
        }catch (Exception e){
            System.out.println("UNABLE TO PERFORM INITIAL SETUP CHECK THAT ALL THE PARAMETERS ARE CORRECT");
            System.out.println("USAGE Main id localPort serverIP serverPort");
            e.printStackTrace();
            return;
        }
        if (!joinTheNetwork()) {
            return;
        }
        SmartMeterSimulator smartMeterSimulator=new SmartMeterSimulator(Configuration.houseInfo.getId()+"",new SlidingBuffer());
        Configuration.smartMeterSimulator=smartMeterSimulator;
        smartMeterSimulator.start();
        while (true) {
            System.out.println("PRESS A Q TO EXIT");
            System.out.println("PRESS A B TO BOOST");
            String input=fromUser.nextLine().toUpperCase().trim();
            if(input.equals("Q")){
                Configuration.isStopping=true;
                break;
            }
            if(input.equals("B")){
                BoostManager.getInstance().request();
            }
        }
        leaveTheNetwork();
        Configuration.messageReceiver.stopMeNotSoGently();
        Configuration.smartMeterSimulator.stopMeGently();
        System.out.println("GOODBYE");

    }

    private static void initialSetup(String[] args) throws IOException {
        int id = Integer.parseInt(args[0]);
        int localPort = Integer.parseInt(args[1]);
        String ip = args[2];
        int port = Integer.parseInt(args[3]);
        String url = "http://" + ip + ":" + port;
        ServerSocket socket = new ServerSocket(localPort);
        Configuration.acceptingSocket = socket;
        Configuration.houseInfo = new HouseInfo(id, socket.getLocalPort(), InetAddress.getLocalHost().getHostAddress());
        Configuration.serverBaseURL = url;
        Configuration.toServer = ClientBuilder.newClient();
        HouseList.getInstance().addObserver(BoostManager.getInstance());
        HouseList.getInstance().addObserver(Coordinator.getInstance());
        HouseList.getInstance().addObserver(MeasurementManager.getInstance());
        MessageReceiver messageReceiver = new MessageReceiver(socket);
        Configuration.messageReceiver = messageReceiver;
        Configuration.isStopping = false;
        messageReceiver.start();
    }

    private static boolean joinTheNetwork() {
        try {
            HouseInfo[] list= CommunicationWithServer.register();
            for (HouseInfo h: list) {
                HouseList.getInstance().add(h);
            }
            if (list.length==1){
                Coordinator.getInstance().setCoordinator(list[0], TimeManager.getTime());
            }else {
                Coordinator.getInstance().requestCoordinator();
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("CONNECTION WITH SERVER FAILED CHECK THAT ALL THE PARAMETERS ARE CORRECT");
            try {
                Configuration.acceptingSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Configuration.messageReceiver.stopMeNotSoGently();
            return false;
        }
        Message welcomeMessage=new Message(MessageType.WELCOME, Configuration.houseInfo);
        MessageSender.sendToEveryBody(welcomeMessage);
        System.out.println("CONNECTED TO THE NETWORK");
        return true;
    }

    private static void leaveTheNetwork() {
        CommunicationWithServer.unregister();
        Message message=new Message(MessageType.GOODBYE, Configuration.houseInfo);
        MessageSender.sendToEveryBody(message);
        System.out.println("DISCONNECTED FROM THE NETWORK");
        HouseList.getInstance().clean();
    }

    public static void recovery(){
        leaveTheNetwork();
        joinTheNetwork();
    }

}
