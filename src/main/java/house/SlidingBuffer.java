package house;

import house.Message.Message;
import house.Message.MessageSender;
import house.Message.MessageType;
import house.smartMeter.Buffer;
import house.smartMeter.Measurement;
import server.beans.comunication.LocalMeasurement;

public class SlidingBuffer implements Buffer {
    private final static int size=24;
    int writeIn;
    int measurementNumber;
    Measurement[] measurements;

    public SlidingBuffer() {
        this.writeIn=0;
        this.measurementNumber=0;
        this.measurements = new Measurement[size];
    }

    @Override
    public void addMeasurement(Measurement m) {
        measurements[writeIn]=m;
        writeIn++;
        measurementNumber++;
        if(measurementNumber>=size){
            if(writeIn>=size){
                writeIn=0;
            }
            //System.err.println("generated new local measurement");
            LocalMeasurement localMeasurement=calculateLocalMeasurement();
            sendMeasurement(localMeasurement);
            measurementNumber=size/2;

        }
        //sliding window 24 measurement overlap 50%
    }

    private LocalMeasurement calculateLocalMeasurement() {
        double value=0.;
        long timestamp=0;
        for (Measurement m:measurements) {
            value+=m.getValue();
            timestamp= Math.max(m.getTimestamp(), timestamp);
        }
        return new LocalMeasurement(Configuration.houseInfo.getId(),value/size,timestamp);
    }

    private void sendMeasurement(LocalMeasurement localMeasurement){
        CommunicationWithServer.sendLocalMeasurement(localMeasurement);
        Message message=new Message(MessageType.MEASUREMENT, localMeasurement);
        MessageSender.sendToEveryBody(message);
    }
}
