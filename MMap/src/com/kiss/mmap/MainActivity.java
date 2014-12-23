package com.kiss.mmap;

import com.goatstone.util.SensorFusion;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

@SuppressLint("InlinedApi")
public class MainActivity extends Activity implements SensorEventListener {

    private static SensorManager sensorService;

    private MyView myView;

    public Handler mHandler;
    float[] accels = new float[4];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        
        myView = new MyView(this);
        setContentView(myView);

        mHandler = new Handler();
        sensorService = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        registerSensorManagerListeners();
        final Handler handler=new Handler();
        Runnable r=new Runnable() {
            public void run() {
                Log.d("StartEnd1","run");
                myView.calculate();
                myView.invalidate();
                handler.postDelayed(this, 200);
            }
        };
        handler.postDelayed(r, 2000);
    }

    public void registerSensorManagerListeners() {

        sensorService.registerListener(this,
                sensorService.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);
        sensorService.registerListener(this,
                sensorService.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_FASTEST);
        sensorService.registerListener(this,
                sensorService.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_FASTEST);
        

    }

    @Override
    public void onStop() {
        super.onStop();
        sensorService.unregisterListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorService.unregisterListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerSensorManagerListeners();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

//        Log.d("TungTest", "onSensorChanged");
        switch (event.sensor.getType()) {

        case Sensor.TYPE_GYROSCOPE:
            myView.setGyro(event);
            break;

        case Sensor.TYPE_MAGNETIC_FIELD:
            myView.setMag(event.values);
            break;
            
        case Sensor.TYPE_ACCELEROMETER:
            myView.setAccel(event);
            break;
        }

    }

}