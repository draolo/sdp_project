package house.Message;

import java.net.ServerSocket;
import java.net.Socket;

public class MessageReceiver extends Thread{
    ServerSocket socket;
    boolean stop;

    public MessageReceiver(ServerSocket socket) {
        this.socket = socket;
        stop=false;
    }

    @Override
    public void run() {
        while (!stop){
            try {
                Socket s= socket.accept();
                MessageDispatcher messageDispatcher=new MessageDispatcher(s);
                messageDispatcher.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
