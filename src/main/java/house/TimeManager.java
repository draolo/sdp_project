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
}
