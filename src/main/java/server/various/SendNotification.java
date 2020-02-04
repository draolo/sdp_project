package server.various;

import server.beans.comunication.Notification;
import server.beans.comunication.NotificationListener;
import server.beans.storage.NotificationSubscriberList;

import java.util.ArrayList;

public class SendNotification extends Thread {
    public SendNotification(Notification n) {
        this.n = n;
    }

    Notification n;

    @Override
    public void run(){
        ArrayList<Thread> threads=new ArrayList<>();
        for (NotificationListener notificationListener: NotificationSubscriberList.getInstance().getListenerList()) {
            threads.add(new SendNotificationThread(notificationListener,n));
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
