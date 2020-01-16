package server.various;

import server.beans.comunication.GlobalMeasurement;
import java.util.List;

public class Statistics {
    public static double getMean(List<? extends GlobalMeasurement> data) {
        double sum = 0.0;
        if(data.size()<=0){
            return sum;
        }
        for(GlobalMeasurement measurement : data){
            sum += measurement.getValue();
        }
        return sum/data.size();
    }

    public static double getVariance(List<? extends GlobalMeasurement> data) {
        if(data.size()<=1){
            return 0;
        }
        double mean = getMean(data);
        double temp = 0;
        for(GlobalMeasurement measurement :data){
            temp += (measurement.getValue()-mean)*(measurement.getValue()-mean);
        }
        return temp/(data.size()-1);
    }

    public static double getStdDev(List<? extends GlobalMeasurement> data) {
        return Math.sqrt(getVariance(data));
    }

}
