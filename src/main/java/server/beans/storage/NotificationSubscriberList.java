package server.beans.storage;

import server.beans.comunication.NotificationListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationSubscriberList {
    private List<NotificationListener> list;
    private static NotificationSubscriberList instance;

    private NotificationSubscriberList() {
        list = new ArrayList<>();
    }

    //singleton
    public synchronized static NotificationSubscriberList getInstance(){
        if(instance==null)
            instance = new NotificationSubscriberList();
        return instance;
    }

    public synchronized List<NotificationListener> getListenerList() {
        return new ArrayList<>(list);
    }

    public synchronized boolean subscribe(NotificationListener notificationListener){
        return list.add(notificationListener);
    }

    public synchronized boolean unsubscribe(NotificationListener toRemove) {
        for(NotificationListener element: list) {
            if (element.getIp().equals(toRemove.getIp()) && element.getPort()==toRemove.getPort()) {
                return list.remove(element);
            }
        }
        return false;
    }

}
