package com.kiss.rssiservice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.kiss.ips.entity.EMap;
import com.kiss.ips.entity.Position;
import com.kiss.ips.model.MMap;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("InlinedApi")
public class MapActivity extends Activity {// implements

    private static final String TAG = "MapActivity";
    public static final String EXTRAS_TARGET_ACTIVITY = "extrasTargetActivity";
    public static final String EXTRAS_BEACON = "extrasBeacon";

    private static final int REQUEST_ENABLE_BT = 1234;
    private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid",
            null, null, null);

    private BeaconManager beaconManager;

    private TextView ax;
    private TextView ay;
    private ImageView point;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private EMap emap;
    private SensorManager mSensorManager;

    private float[] rotation = new float[4];
    private float[] linear = new float[4];

    private float[] rotationMatrix = new float[16];
    private float[] linearOffset = new float[4];
    private ArrayList<Sensor> sensors;
    private float[] rotationMatrixInverse = new float[16];
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ax = (TextView) findViewById(R.id.ax);
        ay = (TextView) findViewById(R.id.ay);
        point = (ImageView) findViewById(R.id.point);
        
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        com.estimote.sdk.utils.L.enableDebugLogging(false);
        // Configure BeaconManager.
        beaconManager = new BeaconManager(this);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region,
                    final List<Beacon> beacons) {
                // Note that results are not delivered on UI thread.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (beacons.size() > 0) {
                            MapActivity.this.emap = MMap.getEMap();
                            MMap.setIbeaconRssiForEmap(MapActivity.this.emap, beacons);
                        }
                    }
                });
            }
        });

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        addSensor(Sensor.TYPE_ROTATION_VECTOR);
        addSensor(Sensor.TYPE_LINEAR_ACCELERATION);

    }


    private void addSensor(int sensorType) {
        Sensor sensor = mSensorManager.getDefaultSensor(sensorType);
        sensors.add(sensor);
        mSensorManager.registerListener(mySensorEventListener, sensor,
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");
    }


    private final SensorEventListener mySensorEventListener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            calibrate(event);
        }

    };

    int i=0;
    private void calibrate(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ROTATION_VECTOR:
                System.arraycopy(event.values, 0, rotation, 0, 3);
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                if(linearOffset[0] == 0){
                    System.arraycopy(event.values, 0, linearOffset, 0, 3);
                }

                linear[0] = event.values[0] - linearOffset[0];
                linear[1] = event.values[1] - linearOffset[1];
                linear[2] = event.values[2] - linearOffset[2];
                
                if(i%50==0){
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, rotation);
                    android.opengl.Matrix.invertM(rotationMatrixInverse, 0, rotationMatrix , 0);
                    float[] orientation = new float[4];
                    android.opengl.Matrix.multiplyMV(orientation, 0 , rotationMatrixInverse, 0, linear, 0);
                    MapActivity.this.updateValues(MapActivity.this.emap, linearOffset, orientation);
                }
                
                break;
        }
        
    }
    
    @Override
    protected void onStop() {
        Log.d(TAG, "OnStop");
        try {
            beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);
        } catch (RemoteException e) {
            Log.d(TAG, "Error while stopping ranging", e);
        }

        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");

        // Check if device supports Bluetooth Low Energy.
        if (!beaconManager.hasBluetooth()) {
            Toast.makeText(this, "Device does not have Bluetooth Low Energy",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // If Bluetooth is not enabled, let user enable it.
        if (!beaconManager.isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            connectToService();
        }
//        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
//        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
      
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();

        stopService(new Intent(getBaseContext(), WifiService.class));
//        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        beaconManager.disconnect();
        super.onDestroy();

    }


    private void connectToService() {
        Log.d(TAG, "connectToService");
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
                } catch (RemoteException e) {
                    Toast.makeText(
                            MapActivity.this,
                            "Cannot start ranging, something terrible happened",
                            Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Cannot start ranging", e);
                }
            }
        });
    }

    public void updateValues(EMap emap) {
        try {

            Position defaultPos = new Position(0, 0);

            String jsonLastPosString = sharedPref.getString(
                    com.kiss.config.Constants.LAST_POSITION_STRING_KEY,
                    defaultPos.toJSon());

            Position lastPos = new Position(jsonLastPosString);

            Position currentPos = MMap.getCurrentPoint(emap, lastPos);

            if (currentPos != null) {
                MapActivity.this.ax.setText("X: " + currentPos.x);
                MapActivity.this.ay.setText("Y: " + currentPos.y);
                RelativeLayout.LayoutParams lp = (LayoutParams) point
                        .getLayoutParams();

                lp.setMargins((int) currentPos.x / 10, (int) currentPos.y / 10,
                        0, 0);
                point.setVisibility(1);

                point.setLayoutParams(lp);
                currentPos.setTime((new Date()).getTime());
                editor.putString(
                        com.kiss.config.Constants.LAST_POSITION_STRING_KEY,
                        currentPos.toJSon());
                editor.commit();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            // throw e;
            e.printStackTrace();

        }

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}