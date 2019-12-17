package house;

import house.smartMeter.Buffer;
import house.smartMeter.Measurement;

public class SlidingBuffer implements Buffer {
    @Override
    public void addMeasurement(Measurement m) {
        //sliding window 24 measurement overlap 50%
    }
}
