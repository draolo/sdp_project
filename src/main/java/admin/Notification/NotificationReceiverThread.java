package admin.Notification;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class NotificationReceiverThread extends Thread {
    ServerSocket socket;
    volatile boolean stop;
    public NotificationReceiverThread(ServerSocket socket) {
        this.socket = socket;
        stop=false;
    }

    public void stopMeNotSoGently() {
        stop = true;
        try {
            socket.close();
        } catch (IOException e) {
            Logger.getGlobal().warning("FAILED TO CLOSE THE SOCKET");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(!stop){
            try {
                Socket connection= socket.accept();
                NotificationParserThread parser=new NotificationParserThread(connection);
                parser.start();
            } catch (Exception e) {
                if (stop) {
                    break;
                } else {
                    e.printStackTrace();
                }
            }
        }
    }
}
