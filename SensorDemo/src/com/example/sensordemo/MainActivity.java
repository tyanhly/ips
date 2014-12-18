package com.example.sensordemo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.goatstone.util.SensorFusion;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
@SuppressLint("InlinedApi")
public class MainActivity extends Activity implements SensorEventListener {

    SensorManager sensorManager;
    TextView accel;
    TextView accell;
    TextView mag;
    TextView magu;
    TextView gyros;
    TextView gyrosu;
    Button start;
    Button stop;
    int isTracking = 0;
    Map<String, String> data = new HashMap<String, String>();

    private Timer fuseTimer = new Timer();

    private SensorFusion sensorFusion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accell = (TextView) findViewById(R.id.accell);
        accel = (TextView) findViewById(R.id.accel);
        magu = (TextView) findViewById(R.id.magu);
        mag = (TextView) findViewById(R.id.mag);
        gyros = (TextView) findViewById(R.id.gyros);
        gyrosu = (TextView) findViewById(R.id.gyrosu);
        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);

        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        registerSensorManagerListeners();

        sensorFusion = new SensorFusion();
        sensorFusion.setMode(SensorFusion.Mode.FUSION);
    }

    public void registerSensorManagerListeners() {

        // sensorManager
        // .registerListener(this, sensorManager
        // .getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
        // SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);

         sensorManager.registerListener(this,
         sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
         SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorManager
                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_FASTEST);

        // sensorManager.registerListener(this,
        // sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
        // SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorManager
                .getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_FASTEST);

        fuseTimer.scheduleAtFixedRate(new requestHttp(), 2000, 2000);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        Time now = new Time();
        now.setToNow();
        String time = now.format("%M:%S");

        float azimuthValue = (float) sensorFusion.getAzimuth()*-1+90;
        String text;
        switch (event.sensor.getType()) {

        case Sensor.TYPE_ACCELEROMETER:

            text = "Accel: \n" + getString(event.values);
            sensorFusion.setAccel(event.values);
            sensorFusion.calculateAccMagOrientation();
            accel.setText(text);
            addData("TYPE_ACCELEROMETER", String.format("%s\t%.3f\t%.3f\t%.3f\t%.2f",
                    time, event.values[0], event.values[1], event.values[2],azimuthValue));
            
            break;
        case Sensor.TYPE_MAGNETIC_FIELD:
            text = "MAG: " + getString(event.values);
            sensorFusion.setMagnet(event.values);
            mag.setText(text);
            addData("TYPE_MAGNETIC_FIELD", String.format(
                    "%s\t%.3f\t%.3f\t%.3f", time, event.values[0],
                    event.values[1], event.values[2]));

            break;
        case Sensor.TYPE_GYROSCOPE:
            
            text = "GYRO: \n" + getString(event.values);
            sensorFusion.gyroFunction(event);
            gyros.setText(text);
            addData("TYPE_GYROSCOPE", String.format("%s\t%.3f\t%.3f\t%.3f",
                    time, event.values[0], event.values[1], event.values[2]));
            break;

        default:
            break;
        }
    }

    public void startTracking(View v) {
        isTracking = 1;
        start.setText("Start -" + isTracking);
        stop.setText("Stop -" + isTracking);

    }

    public void stopTracking(View v) {
        callHttp();
        isTracking = 0;
        start.setText("Start -" + isTracking);
        stop.setText("Stop -" + isTracking);

    }

    @Override
    public void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerSensorManagerListeners();
    }

    public String getString(float[] values) {
        String text = "";
        int i = 0;
        for (float v : values) {
            text += String.format("v[%d]=%.4f\n", i, v);
            i++;
            if (i == 3)
                break;
        }
        return text;
    }

    protected class DownloadPage extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {

            String responseStr = null;

            try {
                for (String url : urls) {
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    HttpGet get = new HttpGet(url);
                    
                    HttpResponse httpResponse = httpClient.execute(get);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    responseStr = EntityUtils.toString(httpEntity);
                }

            } catch (Exception e) {
                Log.d("Test", e.getMessage());
            }
            return responseStr;
        }

        protected void onPostExecute(String result) {
            // @Todo if need
        }
    }

    public void onClickSend(View v) {
        callHttp();
    }

    class requestHttp extends TimerTask {
        public void run() {
            callHttp();

        }
    }

    public void callHttp() {
        if (MainActivity.this.isTracking == 1) {
            HashMap<String, String> t = new HashMap<String, String>(
                    MainActivity.this.data);
            MainActivity.this.data.clear();
            for (Map.Entry<String, String> entry : t.entrySet()) {
                String params = entry.getValue();
                String file = entry.getKey();
                if (params.contains(".")&& file.contains("T")) {
                    try {
                        String url;
                        url = "http://192.168.30.17/php?file=" + file + "&p="
                                + URLEncoder.encode(params, "utf-8");

                        Log.d("Test", url);
                        DownloadPage task = new DownloadPage();
                        task.execute(new String[] { url });
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Log.d("Test", e.getMessage());
                    }
                }

            }
        }
    }

}
