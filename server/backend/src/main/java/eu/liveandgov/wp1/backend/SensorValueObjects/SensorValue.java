package eu.liveandgov.wp1.backend.SensorValueObjects;

import eu.liveandgov.wp1.backend.format.SampleType;

public abstract class SensorValue {
	public SampleType type;
	public long timestamp;
	public String id;
}
