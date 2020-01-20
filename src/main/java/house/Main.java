package house;

import house.Boost.BoostManager;
import house.smartMeter.SmartMeterSimulator;

import java.io.IOException;
import java.net.ServerSocket;


//see if args contains setup data
//if not ask setup data
//start smart meter
//register to admin server
//if ok continue else raise exception
//parsing house list
//enter the network

public class Main {
    public static void main(String[] args) throws IOException {
        int id=1;
        String ip="127.0.0.1";
        int port=1340;
        String url="http://"+ip+":"+port;
        ServerSocket socket=new ServerSocket(0);
        House house=new House(id,socket,ip,port);
        try {
            house.register();
        }catch (Exception e){
            System.out.println("CONNECTION WITH SERVER FAILED");
            return;
        }
        SmartMeterSimulator smartMeterSimulator=new SmartMeterSimulator(id+"",new SlidingBuffer(house));
        BoostManager.setSmartMeter(smartMeterSimulator);
        smartMeterSimulator.start();


        System.out.println("PRESS A KEY TO EXIT");
        System.in.read();
        house.unregister();
        System.out.println("GOODBYE");
    }
}
