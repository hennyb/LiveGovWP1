package eu.liveandgov.sensorcollectorv3.human_activity_recognition;

import eu.liveandgov.sensorcollectorv3.configuration.IntentAPI;
import eu.liveandgov.sensorcollectorv3.configuration.SsfFileFormat;
import eu.liveandgov.sensorcollectorv3.connectors.implementations.IntentEmitter;
import eu.liveandgov.sensorcollectorv3.connectors.implementations.Multiplexer;
import eu.liveandgov.sensorcollectorv3.connectors.implementations.PrefixFilter;
import eu.liveandgov.sensorcollectorv3.connectors.implementations.SampleEmitter;
import eu.liveandgov.wp1.human_activity_recognition.HarPipeline;
import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;
import eu.liveandgov.wp1.human_activity_recognition.connectors.Pipeline;
import eu.liveandgov.wp1.human_activity_recognition.containers.MotionSensorValue;

/**
 * Pipeline class that consumes accelerometer values and produces an activity stream.
 *
 * Created by hartmann on 10/20/13.
 */
public class HarAdapter implements Consumer<String> {

    private final PrefixFilter filter;
    private final MotionSensorValueProducer parseProd;
    private final Pipeline<MotionSensorValue, String> harPipeline;

    public HarAdapter(){
        // ACC filter
        filter = new PrefixFilter();
        filter.addFilter("ACC");

        // Parser
        parseProd = new MotionSensorValueProducer();
        filter.setConsumer(parseProd);

        // HAR
        harPipeline = new HarPipeline(1000);
        parseProd.setConsumer(harPipeline);

        // Multiplex samples, in order for multiple consumers to connect
        Multiplexer<String> multiplexer = new Multiplexer<String>();
        harPipeline.setConsumer(multiplexer);

        // Publish samples as Intent and as Sensor Sample.
        multiplexer.addConsumer(new IntentEmitter(IntentAPI.RETURN_ACTIVITY, IntentAPI.FIELD_ACTIVITY));
        multiplexer.addConsumer(new SampleEmitter(SsfFileFormat.SSF_ACTIVITY) );
    }

    @Override
    public void push(String m) {
        filter.push(m);
    }

}
