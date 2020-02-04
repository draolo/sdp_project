package house;

import house.Message.MessageReceiver;
import house.smartMeter.SmartMeterSimulator;
import server.beans.comunication.HouseInfo;

import javax.ws.rs.client.Client;
import java.net.ServerSocket;

public class Configuration {
    public static HouseInfo houseInfo;
    public static Client toServer;
    public static ServerSocket acceptingSocket;
    public static String serverBaseURL;
    public static SmartMeterSimulator smartMeterSimulator;
    public static MessageReceiver messageReceiver;
    public static boolean isStopping;
}
