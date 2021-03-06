package server.beans.comunication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "house")
@XmlAccessorType(XmlAccessType.FIELD)
public class HouseInfo {

    private int id;
    private int port;
    private String ip;
    // TODO: 17/12/2019 choose if use ip as string or INetAddress


    public HouseInfo(){

    }

    public HouseInfo(int id, int port, String ip) {
        this.id = id;
        this.port = port;
        this.ip = ip;
    }

    public int getId() {
        return id;
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "HouseInfo{" +
                "id=" + id +
                ", port=" + port +
                ", ip='" + ip + '\'' +
                '}';
    }
}
