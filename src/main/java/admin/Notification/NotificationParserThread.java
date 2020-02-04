package admin.Notification;

import com.google.gson.Gson;
import server.beans.comunication.Notification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class NotificationParserThread extends Thread {
    Socket socket;

    public NotificationParserThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            Gson gson=new Gson();
            BufferedReader inFromClient =new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Notification n =gson.fromJson(inFromClient,Notification.class);
            System.out.println(n.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
