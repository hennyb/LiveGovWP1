package eu.liveandgov.wp1.server.sensor_helper.sensor_value_objects;

import eu.liveandgov.wp1.server.sensor_helper.SampleType;
import eu.liveandgov.wp1.server.sensor_helper.SensorValueInterface;

public class LacSensorValue extends AbstractMotionSensorValue implements SensorValueInterface {

    public LacSensorValue(long timestamp, String id, float x, float y, float z) {
        super(timestamp, id, x, y, z);
    }

    public String toSSF() {
        return "LAC," + baseSSF();
    }

    public SampleType getType(){
        return SampleType.LAC;
    }

}
