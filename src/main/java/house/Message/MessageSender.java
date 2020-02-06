package house.Message;

import house.houseListManager.HouseList;
import server.beans.comunication.HouseInfo;

import java.util.ArrayList;
import java.util.List;

public class MessageSender {

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

    public static void sendTo(Message message, HouseInfo to) {
        List<HouseInfo> list = new ArrayList<>();
        list.add(to);
        sendToGroup(message,list);
    }
}
