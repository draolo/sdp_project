package house.Message;

import server.beans.comunication.HouseInfo;

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
        MessageSender.sendTo(message,to);
    }
}
