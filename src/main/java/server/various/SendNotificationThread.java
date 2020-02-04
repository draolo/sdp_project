package server.various;

import com.google.gson.Gson;
import server.beans.comunication.Notification;
import server.beans.comunication.NotificationListener;
import server.beans.storage.NotificationSubscriberList;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.logging.Logger;

public class SendNotificationThread extends Thread {
    Notification notification;
    NotificationListener to;

    public SendNotificationThread(NotificationListener notificationListener, Notification n) {
        this.to=notificationListener;
        this.notification=n;
    }

    @Override
    public void run() {
        Gson gson= new Gson();
        try {
            Socket clientSocket = new Socket(to.getIp(), to.getPort());
            DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
            String jsonObj = gson.toJson(notification);
            outToClient.writeBytes(jsonObj + '\n');
            clientSocket.close();
        }catch (ConnectException ce){
            Logger.getGlobal().warning("UNABLE TO CONNECT TO "+to+" PROCESS IS PROBABLY DEAD");
            NotificationSubscriberList.getInstance().unsubscribe(to);
            ce.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
