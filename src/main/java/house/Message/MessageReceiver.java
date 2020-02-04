package house.Message;

import house.Configuration;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MessageReceiver extends Thread{
    ServerSocket socket;
    volatile boolean stop = false;

    public MessageReceiver(ServerSocket socket) {
        this.socket = socket;
        this.setName("unique message receiver");
    }

    public void stopMeNotSoGently() {
        stop = true;
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("FAILED TO CLOSE THE SOCKET");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!stop){
            try {
                Socket s= socket.accept();
                MessageDispatcher messageDispatcher=new MessageDispatcher(s);
                messageDispatcher.start();
            } catch (Exception e) {
                if(Configuration.isStopping){
                    break;
                }else {
                    e.printStackTrace();
                }
            }
        }
    }
}
