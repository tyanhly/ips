package com.kiss.rssiservice;

import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class MapActivity extends Activity {// implements SensorEventListener {

    private WifiService s;
    SensorManager mSensorManager;
    Sensor gyrosmeter;
    private static final String TAG = "MapActivity";
    public static final String EXTRAS_TARGET_ACTIVITY = "extrasTargetActivity";
    public static final String EXTRAS_BEACON = "extrasBeacon";

    private static final int REQUEST_ENABLE_BT = 1234;
    private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid",
            null, null, null);

    private BeaconManager beaconManager;

    private TextView ax;
    private TextView ay;
    private ImageView map;
    private ImageView point;
    


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ax = (TextView) findViewById(R.id.ax);
        ay = (TextView) findViewById(R.id.ay);
        map = (ImageView) findViewById(R.id.map);
        point = (ImageView) findViewById(R.id.point);

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
                            EMap emap = MMap.getEMap();
                            MMap.setIbeaconRssiForEmap(emap, beacons);
                            MapActivity.this.updateValues(emap);
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

    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();

        unbindService(mConnection);
        stopService(new Intent(getBaseContext(), WifiService.class));
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
            Toast.makeText(MapActivity.this, "set s = WifiService",
                    Toast.LENGTH_SHORT).show();

        }

        public void onServiceDisconnected(ComponentName className) {
            Log.d(TAG, "onServiceDisconnected");
            s = null;

        }
    };

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

        Position currentPos = MMap.getCurrentPoint(emap);
        if (currentPos != null) {
            MapActivity.this.ax.setText("X: " + currentPos.x);
            MapActivity.this.ay.setText("Y: " + currentPos.y);
            RelativeLayout.LayoutParams lp = (LayoutParams) point.getLayoutParams();
            
            lp.setMargins((int) currentPos.x/10, (int) currentPos.y/10, 0, 0);
            point.setVisibility(1); 
            
            point.setLayoutParams(lp);
        }

    }


}