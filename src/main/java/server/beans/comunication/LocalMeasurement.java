package server.beans.comunication;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LocalMeasurement implements Comparable<LocalMeasurement> {

    private int id;
    private double value;
    private long timestamp;


    public LocalMeasurement(){

    }


    public LocalMeasurement(int id, double value, long timestamp) {
        this.id = id;
        this.value = value;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        return "LocalStat{" +
                "id=" + id +
                ", value=" + value +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }

    //sort from new to old
    @Override
    public int compareTo(LocalMeasurement l) {
        Long thisTimestamp = timestamp;
        Long otherTimestamp = l.getTimestamp();
        return (-1)*thisTimestamp.compareTo(otherTimestamp);
    }
}

