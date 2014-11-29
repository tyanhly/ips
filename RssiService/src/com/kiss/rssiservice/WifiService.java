package com.kiss.rssiservice;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class WifiService extends Service {
    private static String _TAG = "WiFiService";
    private final IBinder _mBinder = new WifiBinder();
    private List<ScanResult> _wifis = new ArrayList<ScanResult>();
    private WifiManager _wifiManager;

    private static final IntentFilter FILTER = new IntentFilter(
            WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

    /** Gets called by the system once we have the scan results. */
    BroadcastReceiver scanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(_TAG, "scanReceiver received!");
            _wifis = _wifiManager.getScanResults();
            // Request a scan again
            _wifiManager.startScan();
        }
    };

    /** Called when the service is being created. */
    @Override
    public void onCreate() {
        // Get WifiManager and make sure it's enabled
        _wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        if (!_wifiManager.isWifiEnabled()) {
            startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
        } else {
            // Request a scan
            _wifiManager.startScan();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        registerReceiver(scanReceiver, FILTER);
        return Service.START_NOT_STICKY;
    }

    /** Called when The service is no longer used and is being destroyed */
    @Override
    public void onDestroy() {
        unregisterReceiver(scanReceiver);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return _mBinder;
    }

    public class WifiBinder extends Binder {
        public WifiService getService() {
            return WifiService.this;
        }
    }

    public List<ScanResult> getListWifis() {
        return _wifis;
    }

}
