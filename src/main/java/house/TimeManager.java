package house;

public class TimeManager {
    private volatile static long delta=0;

    public synchronized static void addDelta(long variation){
        //System.err.println("updated timestamp delta ");
        delta+=variation;
    }

    public static long getTime(){
        return System.currentTimeMillis()+delta;
    }


}
