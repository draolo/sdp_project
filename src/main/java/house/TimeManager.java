package house;

import java.util.logging.Logger;

public class TimeManager {
    private volatile static long delta=0;

    public synchronized static void addDelta(long variation){
        Logger.getGlobal().fine("updated timestamp delta ");
        delta+=variation;
    }

    public static long getTime(){
        return System.currentTimeMillis()+delta;
    }


}
