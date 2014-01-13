package eu.liveandgov.wp1.sensor_miner.sensors.sensor_producers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import java.util.List;

import eu.liveandgov.wp1.sensor_miner.GlobalContext;
import eu.liveandgov.wp1.sensor_miner.connectors.sensor_queue.SensorQueue;
import eu.liveandgov.wp1.sensor_miner.sensors.SensorSerializer;

/**
 * Created by lukashaertel on 27.11.13.
 */
public class WifiHolder implements SensorHolder {
    public static String LOG_TAG = "WIFIH";

    private final SensorQueue sensorQueue;
    private final int     delay;
    private final Handler handler;
    private long lastScanRequest;

    public WifiHolder(SensorQueue sensorQueue,int delay, Handler handler)
    {
        this.sensorQueue = sensorQueue;
        this.delay = delay;
        this.handler = handler;
    }

    private void startNextScan() {
        if(GlobalContext.getWifiManager().startScan())
        {
            lastScanRequest = SystemClock.uptimeMillis();
        }
    }

    @Override
    public void startRecording() {
        Log.d(LOG_TAG, "Start Recording");
        GlobalContext.context.registerReceiver(scanResultsAvailableEndpoint, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION), null, handler);

        startNextScan();
    }

    @Override
    public void stopRecording() {
        Log.d(LOG_TAG, "Stop Recording");
        try {
            GlobalContext.context.unregisterReceiver(scanResultsAvailableEndpoint);
        } catch (IllegalArgumentException e) {
            Log.w(LOG_TAG, "Receiver already unregistered");
        }
    }

    private final BroadcastReceiver scanResultsAvailableEndpoint = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction()))
            {
                Log.d(LOG_TAG, "Scan results available");

                // Get receive-time of the intent in system uptime
                long scanEndtime = SystemClock.uptimeMillis();

                // Get scan results
                final List<ScanResult> scanResults = GlobalContext.getWifiManager().getScanResults();

                // If scan results are not null, push
                if(scanResults != null)
                {
                    // Push converted scan results to queue
                    sensorQueue.push(SensorSerializer.fromScanResults(scanResults));
                }

                // If results are on time, schedule the next scan at the handler with the given delay
                if(lastScanRequest + delay > scanEndtime)
                {
                    if(!handler.postAtTime(new Runnable() {
                        @Override
                        public void run() {
                            startNextScan();
                        }
                    }, lastScanRequest + delay))
                    {
                        // If failed to schedule, scan immediately
                        startNextScan();
                    }
                }
                else
                {
                    // Else, scan immediately
                    startNextScan();
                }
            }
            else
            {
                throw new IllegalStateException("Illegal configuration of broadcast receiver");
            }
        }
    };

}