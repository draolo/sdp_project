package house;

import house.smartMeter.Buffer;
import house.smartMeter.Measurement;

public class SlidingBuffer implements Buffer {
    private final static int size=24;
    House house;
    int writeIn;
    int measurementNumber;
    Measurement[] measurements;

    public SlidingBuffer(House house) {
        this.house = house;
        this.writeIn=0;
        this.measurementNumber=0;
        this.measurements = new Measurement[size];
    }

    @Override
    public void addMeasurement(Measurement m) {
        measurements[writeIn]=m;
        writeIn++;
        measurementNumber++;
        if(measurementNumber==size){
            if(writeIn>=size){
                writeIn=0;
            }
            measurementNumber=size/2;
            // TODO: 20/01/2020 do something
            //calculate measurement
            //send measurement
        }
        //sliding window 24 measurement overlap 50%
    }
}
