package admin.Notification;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NotificationReceiverThread extends Thread {
    ServerSocket socket;
    volatile boolean stop;
    public NotificationReceiverThread(ServerSocket socket) {
        this.socket = socket;
        stop=false;
    }

    public void stopMeGently() {
        stop = true;
    }

    @Override
    public void run() {
        while(!stop){
            try {
                Socket connection= socket.accept();
                NotificationParserThread parser=new NotificationParserThread(connection);
                parser.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
