package admin;

import server.beans.comunication.GlobalMeasurement;
import server.beans.comunication.HouseInfo;
import server.beans.comunication.LocalMeasurement;
import server.beans.comunication.Stats;

import javax.ws.rs.ProcessingException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // TODO: 17/01/2020 fetch input from inline invocation or direct ask to user
        int port=1340;
        String ip="127.0.0.1";
        Admin admin=new Admin(ip,port);
        Scanner userInput = new Scanner( System.in );
        //admin.registerNotification()
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
                        //admin.unregisterNotification()
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
        System.out.println("WHAT WOULD YOU LIKE TO DO:");
        System.out.println("1 VIEW HOUSE LIST");
        System.out.println("2 VIEW LAST HOUSE MEASUREMENTS");
        System.out.println("3 VIEW LAST GLOBAL MEASUREMENTS");
        System.out.println("4 VIEW LAST HOUSE MEASUREMENTS STATISTICS");
        System.out.println("5 VIEW LAST GLOB1AL MEASUREMENTS STATISTICS");
        System.out.println("0 EXIT");
    }

}
