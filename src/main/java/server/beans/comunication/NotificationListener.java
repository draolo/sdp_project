package server.beans.comunication;

public class NotificationListener {
    private int port;
    private String ip;

    @Override
    public String toString() {
        return "NotificationListener{" +
                "port=" + port +
                ", ip='" + ip + '\'' +
                '}';
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public NotificationListener(int port, String ip) {
        this.port = port;
        this.ip = ip;
    }

    public NotificationListener() {

    }
}
