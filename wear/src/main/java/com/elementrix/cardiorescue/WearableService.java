package com.elementrix.cardiorescue;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

public class WearableService extends Service implements SensorEventListener {

    private SensorManager mSensorManager;
    private int currentValue=0;
    private static final String LOG_TAG = "DrCardiac";
    private GoogleApiClient mGoogleApiClient;

    public WearableService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG,"Wearable Service Created");
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        // delay SENSOR_DELAY_UI is sufficiant
        boolean res = mSensorManager.registerListener(this, mHeartRateSensor,  SensorManager.SENSOR_DELAY_UI);
        Log.d(LOG_TAG, " sensor registered: " + (res ? "yes" : "no"));

        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Wearable.API).build();
        mGoogleApiClient.connect();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG,"Wearable Service Started on Command");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
        Log.d(LOG_TAG,"Wearable Service Destroyed");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // is this a heartbeat event and does it have data?
        if(sensorEvent.sensor.getType()==Sensor.TYPE_HEART_RATE && sensorEvent.values.length>0 ) {
            int newValue = Math.round(sensorEvent.values[0]);
            //Log.d(LOG_TAG,sensorEvent.sensor.getName() + " changed to: " + newValue);
            // only do something if the value differs from the value before and the value is not 0.
            if(currentValue != newValue && newValue!=0) {
                // save the new value
                currentValue = newValue;
                // send the value to the listener
                Log.d(LOG_TAG,"sending new value to listener: " + newValue);
                sendMessageToHandheld(Integer.toString(newValue));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /**
     * sends a string message to the connected handheld using the google api client (if available)
     * @param message
     */
    private void sendMessageToHandheld(final String message) {

        if (mGoogleApiClient == null)
            return;

        Log.d(LOG_TAG,"sending a message to handheld: "+message);

        // use the api client to send the heartbeat value to our handheld
        final PendingResult<NodeApi.GetConnectedNodesResult> nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient);
        nodes.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult result) {
                final List<Node> nodes = result.getNodes();
                if (nodes != null) {
                    for (int i=0; i<nodes.size(); i++) {
                        final Node node = nodes.get(i);
                        Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), message, null);
                    }
                }
            }
        });

    }


}
