package com.kiss.rssiservice;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kiss.ips.entity.EMap;
import com.kiss.ips.entity.Position;
import com.kiss.ips.entity.Wifi;
import com.kiss.ips.model.MMap;

public class MainActivity extends Activity {
    SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;

    private TextView ax;
    private TextView ay;
    private ListView list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ax = (TextView) findViewById(R.id.ax);
        ay = (TextView) findViewById(R.id.ay);

        list = (ListView) findViewById(R.id.listView1);

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("TungLyLog", "onResume");
        Intent intent = new Intent(this, WifiServiceImpl.class);
        // bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        startService(intent);

    }

    @Override
    protected void onPause() {
        Log.d("TungLyLog", "onPause");
        super.onPause();

        // unbindService(mConnection);
        stopService(new Intent(this, WifiServiceImpl.class));

    }

    @Override
    public void onDestroy() {
        Log.d("TungLyLog", "onDestroy");
        super.onDestroy();

    }

    

    public class WifiServiceImpl extends WifiService {
        public void setListView() {

            String[] lwifis = new String[MMap.getEMap().currentWifis.size()];

            int i = 0;
            for (Wifi w : MMap.getEMap().currentWifis.values()) {
                lwifis[i++] = w.getMac() + " - RSSI: " + w.getRssi() + " - Dis:"
                        + w.getCurrentDistance();
            }

            MainActivity.this.list.setAdapter(new ArrayAdapter<String>(MainActivity.this.getApplicationContext(),
                    android.R.layout.simple_list_item_1, lwifis));
        }
        
        public void onWifiReceived() {
            List<ScanResult> wifis = this.getListWifis();
            if (wifis.size() > 0) {
                EMap emap = MMap.getEMap();
                MMap.setRssiForEmap(emap, wifis);

                this.setListView();

                Position currentPos = MMap.getCurrentPosition(emap);

                MainActivity.this.ax.setText("X: " + currentPos.x);
                MainActivity.this.ay.setText("Y: " + currentPos.y);
            }
        }
    }
}