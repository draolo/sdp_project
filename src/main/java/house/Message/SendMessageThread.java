package house.Message;

import com.google.gson.Gson;
import house.CommunicationWithServer;
import house.houseListManager.HouseList;
import server.beans.comunication.HouseInfo;

import java.io.DataOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public class SendMessageThread extends Thread {

    HouseInfo to;
    Message message;

    public SendMessageThread(Message message, HouseInfo to) {
        this.to = to;
        this.message = message;
        this.setName("Message sender to "+to.getId());
    }

    @Override
    public void run() {
        sendMessage(message,to);
    }

    public static void sendMessage(Message message, HouseInfo to){
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

}
