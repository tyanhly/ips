package com.kiss.map;

import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

@SuppressLint("InlinedApi")
public class MainActivity extends Activity {

    private static SensorManager sensorService;
    private ArrayList<Sensor> sensors;

    private float[] rotation = new float[4];
    private float[] linear = new float[4];

    private float[] rotationMatrix = new float[16];
    private float[] linearOffset = new float[4];

    private float[] rotationMatrixInverse = new float[16];
    private MyView myView;
    
    float[] accels = new float[4];
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        myView = new MyView(this);
        setContentView(myView);
        

        sensorService = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensors = new ArrayList<Sensor>();
        addSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        addSensor(Sensor.TYPE_ROTATION_VECTOR);
    }
    

    private void addSensor(int sensorType) {
        Sensor sensor = sensorService.getDefaultSensor(sensorType);
        sensors.add(sensor);
        sensorService.registerListener(mySensorEventListener, sensor,
                SensorManager.SENSOR_DELAY_FASTEST);
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

                SensorManager.getRotationMatrixFromVector(rotationMatrix, rotation);
                android.opengl.Matrix.invertM(rotationMatrixInverse, 0, rotationMatrix , 0);
                
                android.opengl.Matrix.multiplyMV(accels, 0 , rotationMatrixInverse, 0, linear, 0);
                //compassView.updateData(out, 10);
                Position pos = myView.getLastMovingData();
                
                pos.setAccels(accels);
                long time = new Date().getTime();
                long t = time - pos.getTime();
//                android.util.Log.i("time", "interval:" +t);
                myView.addPos(pos.getNextPosition(t));
                
                break;
        }
    }
}