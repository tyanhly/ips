package com.kiss.rssiservice;

import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kiss.ips.entity.EMap;
import com.kiss.ips.entity.Position;
import com.kiss.ips.entity.Wifi;
import com.kiss.ips.model.MMap;

public class MainActivity extends Activity implements SensorEventListener {
    private WifiService s;
    SensorManager mSensorManager;
    Sensor gyrosmeter;

    private TextView ax;
    private TextView ay;
    private ListView list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ax = (TextView) findViewById(R.id.ax);
        ay = (TextView) findViewById(R.id.ay);

        list = (ListView)findViewById(R.id.listView1);
        MainActivity.this.mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        
        MainActivity.this.gyrosmeter = mSensorManager
                .getDefaultSensor(Sensor.TYPE_GYROSCOPE);

    }

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder binder) {
            Log.d("TungLyLog", "onServiceConnected");
            WifiService.WifiBinder b = (WifiService.WifiBinder) binder;
            s = b.getService();
            Toast.makeText(MainActivity.this, "set s = WifiService",
                    Toast.LENGTH_SHORT).show();

        }

        
        public void onServiceDisconnected(ComponentName className) {
            Log.d("TungLyLog", "onServiceDisconnected");
            s = null;

        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("TungLyLog", "onResume");
        Intent intent = new Intent(this.getBaseContext(), WifiService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        startService(intent);

        MainActivity.this.mSensorManager.registerListener(MainActivity.this,
                gyrosmeter, SensorManager.SENSOR_DELAY_UI);

    }

    @Override
    protected void onPause() {
        Log.d("TungLyLog", "onPause");
        super.onPause();

        unbindService(mConnection);
        stopService(new Intent(getBaseContext(), WifiService.class));

        mSensorManager.unregisterListener(MainActivity.this);
    }

    @Override
    public void onDestroy() {
        Log.d("TungLyLog", "onDestroy");
        super.onDestroy();

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            if (s != null) {
                List<ScanResult> wifis = s.getListWifis();
                if (wifis.size() > 0) {
                    EMap emap = MMap.getEMap();
                    MMap.setRssiForEmap(emap, wifis);

                    setListView();
                    
                    Position currentPos = MMap.getCurrentPosition(emap);
                    
                    ax.setText("X: " + currentPos.x);
                    ay.setText("Y: " + currentPos.y);
                }
            }
        }
    }

    public void setListView(){

        String[] lwifis = new String[MMap.getEMap().currentWifis.size()];
        
        int i=0;
        for(Wifi w: MMap.getEMap().currentWifis.values()){
            lwifis[i++] = w.getMac() + " - RSSI: " + w.getRssi() + " - Dis:" + w.getCurrentDistance();
        }
        
        list.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
        android.R.layout.simple_list_item_1, lwifis));
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    
}