package com.kiss.config;

import android.content.IntentFilter;
import android.net.wifi.WifiManager;

public class Constants {
    public final static String ANDROID_LOG_TAG = "TungLogTag";
    public static final IntentFilter WIFI_SCANRESULT_FILTER = new IntentFilter(
            WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
}
