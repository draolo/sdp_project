package server.beans.comunication;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Stats {
    double mean;
    double sd;

    public Stats() {
    }

    public Stats(double mean, double sd) {
        this.mean = mean;
        this.sd = sd;
    }

    @Override
    public String toString() {
        return "Stats{" +
                "mean=" + mean +
                ", sd=" + sd +
                '}';
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public double getSd() {
        return sd;
    }

    public void setSd(double sd) {
        this.sd = sd;
    }

}
