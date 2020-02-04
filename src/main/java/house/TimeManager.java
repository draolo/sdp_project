package house;

import java.util.logging.Logger;

public class TimeManager {
    private volatile static long delta=0;

    private synchronized static void addDelta(long variation){
        Logger.getGlobal().fine("updated timestamp delta ");
        delta+=variation;
    }

    public static long getTime(){
        return System.currentTimeMillis()+delta;
    }


    public synchronized static void testAndSet(long timestamp) {
        long difference=timestamp-getTime();
        if(difference>=0){
            addDelta(difference+1);
        }
    }

    //custom sleep with already try and catch used for debug
    public static void sleep(int seconds){
        Logger.getGlobal().warning("SYSTEM IN PAUSE");
        try {
            Thread.sleep(seconds*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Logger.getGlobal().warning("SYSTEM RESUME");
    }
}
