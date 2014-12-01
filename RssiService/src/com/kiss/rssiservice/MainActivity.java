package com.kiss.rssiservice;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.kiss.ips.entity.EMap;
import com.kiss.ips.entity.Ibeacon;
import com.kiss.ips.entity.Position;
import com.kiss.ips.entity.Wifi;
import com.kiss.ips.model.MMap;

public class MainActivity extends Activity {// implements SensorEventListener {

    private WifiService s;
    SensorManager mSensorManager;
    Sensor gyrosmeter;
    private static final String TAG = "MainActivity";
    public static final String EXTRAS_TARGET_ACTIVITY = "extrasTargetActivity";
    public static final String EXTRAS_BEACON = "extrasBeacon";

    private static final int REQUEST_ENABLE_BT = 1234;
    private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid",
            null, null, null);

    private BeaconManager beaconManager;

    private TextView ax;
    private TextView ay;
    private ListView wifiList;
    private ListView ibeaconList;

    private MainActivity.MainAdapter wifiListAdapter;
    private MainActivity.MainAdapter ibeaconListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ax = (TextView) findViewById(R.id.ax);
        ay = (TextView) findViewById(R.id.ay);

        wifiList = (ListView) findViewById(R.id.listView1);
        ibeaconList = (ListView) findViewById(R.id.listView2);

        wifiListAdapter = new MainActivity.MainAdapter(getApplicationContext(),
                android.R.layout.simple_list_item_1);

        wifiList.setAdapter(wifiListAdapter);

        ibeaconListAdapter = new MainActivity.MainAdapter(
                getApplicationContext(), android.R.layout.simple_list_item_2);

        ibeaconList.setAdapter(ibeaconListAdapter);

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
                            EMap emap = MMap.getEMap();
                            MMap.setIbeaconRssiForEmap(emap, beacons);
                            MainActivity.this.updateValues(emap);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");
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
        Intent intent = new Intent(this.getBaseContext(), WifiService.class);

        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        startService(intent);
        registerReceiver(scanWifiReceiver,
                com.kiss.config.Constants.WIFI_SCANRESULT_FILTER);

    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();

        unbindService(mConnection);
        stopService(new Intent(getBaseContext(), WifiService.class));
        unregisterReceiver(scanWifiReceiver);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        beaconManager.disconnect();
        super.onDestroy();

    }

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder binder) {
            Log.d(TAG, "onServiceConnected");
            WifiService.WifiBinder b = (WifiService.WifiBinder) binder;
            s = b.getService();
            Toast.makeText(MainActivity.this, "set s = WifiService",
                    Toast.LENGTH_SHORT).show();

        }

        public void onServiceDisconnected(ComponentName className) {
            Log.d(TAG, "onServiceDisconnected");
            s = null;

        }
    };

    BroadcastReceiver scanWifiReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(com.kiss.config.Constants.ANDROID_LOG_TAG,
                    "scanReceiver received!");
            if (s != null) {
                List<ScanResult> wifis = MainActivity.this.s.getListWifis();
                if (wifis.size() > 0) {
                    EMap emap = MMap.getEMap();
                    MMap.setWifiRssiForEmap(emap, wifis);
                    MainActivity.this.updateValues(emap);
                }
            }
        }
    };

    private void connectToService() {
        Log.d(TAG, "connectToService");
        ibeaconListAdapter.replaceWith(new String[] {});
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
                } catch (RemoteException e) {
                    Toast.makeText(
                            MainActivity.this,
                            "Cannot start ranging, something terrible happened",
                            Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Cannot start ranging", e);
                }
            }
        });
    }

    public void updateValues(EMap emap) {

        MainActivity.this.setListView(emap);
        Position currentPos = MMap.getCurrentPoint(emap);
        if (currentPos != null) {
            MainActivity.this.ax.setText("X: " + currentPos.x);
            MainActivity.this.ay.setText("Y: " + currentPos.y);
        }

    }

    public void setListView(EMap emap) {

        String[] lwifis = new String[emap.currentWifis.size()];

        String[] ibeacons = new String[emap.currentIbeacons.size()];

        int i = 0;
        for (Wifi w : emap.currentWifis.values()) {
            lwifis[i++] = w.getMac() + " - RSSI: " + w.getRssi() + " - Dis:"
                    + w.getCurrentDistance();
        }
        for (Ibeacon ib : emap.currentIbeacons.values()) {
            ibeacons[i++] = ib.getMac() + " - RSSI: "
                    + ib.getBeacon().getRssi() + " - Dis:"
                    + ib.getCurrentDistance();
        }

        this.wifiListAdapter.replaceWith(lwifis);
        this.ibeaconListAdapter.replaceWith(ibeacons);

    }

    public static class MainAdapter extends ArrayAdapter<String> {
        String[] data;

        public MainAdapter(Context context, int resource) {
            super(context, resource);
            // TODO Auto-generated constructor stub
        }

        public void replaceWith(String[] data) {
            this.data = data;
            notifyDataSetChanged();
        }

    }
}