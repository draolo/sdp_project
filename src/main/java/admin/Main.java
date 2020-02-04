package admin;

import admin.Notification.NotificationReceiverThread;
import server.beans.comunication.GlobalMeasurement;
import server.beans.comunication.HouseInfo;
import server.beans.comunication.LocalMeasurement;
import server.beans.comunication.Stats;

import javax.ws.rs.ProcessingException;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        int serverPort;
        String serverIp;
        int localPort;
        try {
            serverPort=Integer.parseInt(args[2]);
            serverIp=args[1];
            localPort=Integer.parseInt(args[0]);
        }catch (Exception e){
            System.out.println("INVALID PARAMETER");
            System.out.println("USAGE: Main localPort serverIp serverPort");
            e.printStackTrace();
            return;
        }

        ServerSocket serverSocket=new ServerSocket(localPort);
        Admin admin=new Admin(serverIp,serverPort,serverSocket);
        Scanner userInput = new Scanner( System.in );
        NotificationReceiverThread receiver=new NotificationReceiverThread(serverSocket);
        receiver.start();
        if(!admin.subscribe()){
            System.out.println("NOTIFICATION SERVICE NOT AVAILABLE");
        }
        do {
            try {
                printMenu();
                int choice = userInput.nextInt();
                switch (choice) {
                    case 1: {
                        HouseInfo[] list = admin.getHouseList();
                        for (HouseInfo houseInfo : list) {
                            System.out.println(houseInfo);
                        }
                        break;
                    }
                    case 2: {
                        System.out.println("ENTER THE HOUSE ID");
                        int id = userInput.nextInt();
                        System.out.println("HOW MANY MEASUREMENTS DO YOU WANT (ZERO FOR ALL)");
                        int limit = userInput.nextInt();
                        LocalMeasurement[] list = admin.getLocalMeasurements(id, limit);
                        for (LocalMeasurement localMeasurement : list) {
                            System.out.println(localMeasurement);
                        }
                        break;
                    }
                    case 3: {
                        System.out.println("HOW MANY MEASUREMENTS DO YOU WANT (ZERO FOR ALL)");
                        int limit = userInput.nextInt();
                        GlobalMeasurement[] list = admin.getGlobalMeasurements(limit);
                        for (GlobalMeasurement globalMeasurement : list) {
                            System.out.println(globalMeasurement);
                        }
                        break;
                    }
                    case 4: {
                        System.out.println("ENTER THE HOUSE ID");
                        int id = userInput.nextInt();
                        System.out.println("HOW MANY MEASUREMENTS DO YOU WANT (ZERO FOR ALL)");
                        int limit = userInput.nextInt();
                        Stats stat = admin.getLocalStats(id, limit);
                        if (stat != null) {
                            System.out.println("MEAN: " + stat.getMean());
                            System.out.println("STANDARD DEVIATION: " + stat.getSd());
                        } else {
                            System.out.println("STATS NOT AVAILABLE");
                        }
                        break;
                    }
                    case 5: {
                        System.out.println("HOW MANY MEASUREMENTS DO YOU WANT (ZERO FOR ALL)");
                        int limit = userInput.nextInt();
                        Stats stat = admin.getGlobalStats(limit);
                        if (stat != null) {
                            System.out.println("MEAN: " + stat.getMean());
                            System.out.println("STANDARD DEVIATION: " + stat.getSd());
                        } else {
                            System.out.println("STATS NOT AVAILABLE");
                        }
                        break;
                    }
                    case 0: {
                        if (!admin.unsubscribe()){
                            System.err.println("FAILED TO UNSUBSCRIBE TO NOTIFICATION SERVICE");
                        }
                        receiver.stopMeGently();
                        return;
                    }
                    default: {
                        break;
                    }
                }
            }catch (ProcessingException pe){
                admin.fetchStatus(404);
                pe.printStackTrace();
            }catch (Exception e){
                System.out.println("SOMETHING WENT WRONG :(");
                e.printStackTrace();
                userInput=new Scanner(System.in);
            }
        }while (true);
    }

    public static void printMenu(){
        System.out.println();
        System.out.println("WHAT WOULD YOU LIKE TO DO:");
        System.out.println("1 VIEW HOUSE LIST");
        System.out.println("2 VIEW LAST HOUSE MEASUREMENTS");
        System.out.println("3 VIEW LAST GLOBAL MEASUREMENTS");
        System.out.println("4 VIEW LAST HOUSE MEASUREMENTS STATISTICS");
        System.out.println("5 VIEW LAST GLOBAL MEASUREMENTS STATISTICS");
        System.out.println("0 EXIT");
    }

}
