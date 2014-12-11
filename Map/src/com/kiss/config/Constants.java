package com.kiss.config;

import android.content.IntentFilter;
import android.net.wifi.WifiManager;

public class Constants {
    public final static String ANDROID_LOG_TAG = "TungLogTag";
    public static final IntentFilter WIFI_SCANRESULT_FILTER = new IntentFilter(
            WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
    public static final String LAST_POSITION_STRING_KEY = "LastPositionKey";
    public static final String LAST_RPOSITION_STRING_KEY = "LastRPositionKey";

    public static final int THREAD_UPDATE_VECTOR_TIME_CONSTANT = 30;
    public static final int PIXEL_ON_METER = 100;
    public static final int MOVING_SAMPLE_TOTAL = 100;
    public static final float EPSILON = 0.000000001f;
    public static final float NS2S = 1.0f / 1000000000.0f;
    public static final float FILTER_COEFFICIENT = 0.70f; // complemtry filter
}
