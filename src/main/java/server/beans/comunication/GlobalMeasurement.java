package server.beans.comunication;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GlobalMeasurement implements Comparable<GlobalMeasurement>{

    private double value;
    private long timestamp;


    public GlobalMeasurement(){

    }


    public GlobalMeasurement(double value, long timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "GlobalStat{" +
                ", value=" + value +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }

    //sort from new to old
    @Override
    public int compareTo(GlobalMeasurement g) {
        Long thisTimestamp = timestamp;
        Long otherTimestamp = g.getTimestamp();
        return (-1)*thisTimestamp.compareTo(otherTimestamp);
    }
}

