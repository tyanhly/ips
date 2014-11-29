package com.kiss.rssiservice;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rssiservice.R;
import com.kiss.ips.entity.EMap;
import com.kiss.ips.entity.Position;
import com.kiss.ips.entity.Wifi;
import com.kiss.ips.model.MMap;

public class MainActivity extends Activity implements SensorEventListener{
    private WifiService s;
    private TextView ax;
    private TextView ay;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ax = (TextView) findViewById(R.id.ax);
        ay = (TextView) findViewById(R.id.ay);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder binder) {
            WifiService.WifiBinder b = (WifiService.WifiBinder) binder;
            s = b.getService();
            Toast.makeText(MainActivity.this, "set s = WifiService",
                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            s = null;
        }
    };

    @Override
    protected void onResume() {
        Log.d("TungLyLog", "onResume");
        super.onResume();
        Intent intent = new Intent(this, WifiService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        // startService(intent);
    }

    @Override
    protected void onPause() {
        Log.d("TungLyLog", "onPause");
        super.onPause();
        unbindService(mConnection);
    }

    @Override
    public void onDestroy() {
        Log.d("TungLyLog", "onDestroy");
        super.onDestroy();
        stopService(new Intent(getBaseContext(), WifiService.class));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            Position currentPos = getCurrentPosition(s.getListWifis(), MMap.getEMap());
        }
    }
    
    public Position getCurrentPosition(List<ScanResult> wifis, EMap emap ){
        
        MMap.setRssiForEmap(emap, wifis);
        MMap.getCurrentPosition(emap);
        
        return null;
        
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
        
    }

}