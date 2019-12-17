package server.beans;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class HouseInfo {

    private int id;
    private int port;
    private String ip;
    // TODO: 17/12/2019 choose if use ip as string or INetAddress

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
}
