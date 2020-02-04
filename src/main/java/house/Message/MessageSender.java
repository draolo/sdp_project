package house.Message;

import com.google.gson.Gson;
import house.CommunicationWithServer;
import house.houseListManager.HouseList;
import server.beans.comunication.HouseInfo;

import java.io.DataOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MessageSender {
    public static void sendTo(Message message, HouseInfo to){
        Gson gson= new Gson();
        try {
            Socket clientSocket = new Socket(to.getIp(), to.getPort());
            DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
            String jsonObj = gson.toJson(message);
            outToClient.writeBytes(jsonObj + '\n');
            clientSocket.close();
        }catch (UnknownHostException h){
            Logger.getGlobal().warning("UNABLE TO FETCH "+to+" ADDRESS, REMOVED FROM LOCAL HOUSE LIST");
            HouseList.getInstance().del(to);
        }catch (ConnectException ce){
            Logger.getGlobal().warning("UNABLE TO CONNECT TO "+to+" PROCESS IS PROBABLY DEAD");
            HouseList.getInstance().del(to);
            Message someoneIsDead=new Message(MessageType.DEAD,to);
            MessageSender.sendToEveryBody(someoneIsDead);
            CommunicationWithServer.unregister(to.getId());
            ce.printStackTrace();
        }catch (Exception e){
            Logger.getGlobal().severe("UNKNOWN ERROR OCCUR WHILE TRYING TO SEND MESSAGE "+message+" TO "+to);
            e.printStackTrace();
        }
    }
    public static void sendToEveryBody(Message message)  {
        sendToGroup(message,HouseList.getInstance().getHouseList());
    }
    public static void sendToGroup(Message message, List<HouseInfo> to)  {
        ArrayList<Thread> threads=new ArrayList<>();
        for (HouseInfo house: to) {
            threads.add(new SendMessageThread(message,house));
        }
        for (Thread thread: threads) {
            thread.start();
        }
        for (Thread thread: threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
