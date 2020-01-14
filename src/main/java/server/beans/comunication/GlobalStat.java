package server.beans.comunication;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GlobalStat {

    private double value;
    private long timestamp;


    public GlobalStat(){

    }


    public GlobalStat(double value, long timestamp) {
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
}

