package server.beans.comunication;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LocalMeasurement extends GlobalMeasurement {

    private Integer id;

    public LocalMeasurement(){
        super();
        id=null;
    }


    public LocalMeasurement(int id, double value, long timestamp) {
        super(value,timestamp);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "LocalStat{" +
                "id=" + id +
                ", value=" + this.getValue() +
                ", timestamp='" + this.getTimestamp() + '\'' +
                '}';
    }
}

