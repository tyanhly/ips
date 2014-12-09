package com.kiss.ips.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.net.wifi.ScanResult;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.Utils;
import com.kiss.ips.entity.EMap;
import com.kiss.ips.entity.Ibeacon;
import com.kiss.ips.entity.Position;
import com.kiss.ips.entity.RPosition;
import com.kiss.ips.entity.Wifi;
import com.kiss.math.CircleEquation;
import com.kiss.math.MathUtil;
import com.kiss.math.SLineEquation;

public class MMap {

    private static EMap emap;

    public static EMap getEMap() {
        if (emap == null) {
            setEMap();
        }
        return emap;
    }

    private static EMap getSampleMap() {
        EMap tmpmap = new EMap();

        Map<String, Ibeacon> ibeacons = new HashMap<String, Ibeacon>();
        Map<String, Wifi> wifis = new HashMap<String, Wifi>();
        Ibeacon ib1 = new Ibeacon("e3:16:78:6b:f5:5e", new Position(4400, 0));
        Ibeacon ib2 = new Ibeacon("f5:d2:7d:c7:de:9d", new Position(4600, 4000));
        Ibeacon ib3 = new Ibeacon("d9:29:98:43:6d:49", new Position(300, 4000));
        Ibeacon ib4 = new Ibeacon("c2:77:57:87:48:16", new Position(300, 500));
        Ibeacon ib5 = new Ibeacon("ec:1c:62:7e:00:59", new Position(4600, 8800));
        Ibeacon ib6 = new Ibeacon("ea:bb:9c:f6:9c:7c", new Position(9000, 8100));
        Ibeacon ib7 = new Ibeacon("ff:5d:33:18:62:a5", new Position(300, 8800));
        Ibeacon ib8 = new Ibeacon("db:af:c1:79:5f:31", new Position(8800, 4000));
        Ibeacon ib9 = new Ibeacon("db:7f:fa:31:a3:56", new Position(8800, 500));

        ibeacons.put(ib1.getMac(), ib1);
        ibeacons.put(ib2.getMac(), ib2);
        ibeacons.put(ib3.getMac(), ib3);
        ibeacons.put(ib4.getMac(), ib4);
        ibeacons.put(ib5.getMac(), ib5);
        ibeacons.put(ib6.getMac(), ib6);
        ibeacons.put(ib7.getMac(), ib7);
        ibeacons.put(ib8.getMac(), ib8);
        ibeacons.put(ib9.getMac(), ib9);

        Wifi wifi1 = new Wifi("bc:ee:7b:e3:ae:c8", new Position(2800, 5600));
        wifi1.setSsid("KISS");
        wifis.put(wifi1.getSsid(), wifi1);

        Wifi wifi2 = new Wifi("20:aa:4b:a6:f4:41", new Position(2800, 18000));
        wifi2.setSsid("VTCA_Lau4_PhongKhach");
        wifis.put(wifi2.getSsid(), wifi2);

        tmpmap.setHeight(18000);
        tmpmap.setWidth(9200);
        tmpmap.setIbeacons(ibeacons);
        tmpmap.setWifis(wifis);
        return tmpmap;
    }

    public static void setEMap() {
        emap = getSampleMap();
    }

    public static void setWifiRssiForEmap(EMap emap, List<ScanResult> wifis) {

        if (wifis.size() > 0) {
            Map<String, Wifi> localWifis = emap.getWifis();

            // Log.d(Constants.ANDROID_LOG_TAG, "1. Static List: " +
            // emap.getWifis().keySet().toString());
            // Log.d(Constants.ANDROID_LOG_TAG, "2. ScanList: " +
            // wifis.toString());
            emap.currentWifis.clear();
            for (ScanResult wifi : wifis) {
                if (localWifis.containsKey(wifi.SSID)) {
                    Wifi currentWifi = localWifis.get(wifi.SSID);
                    currentWifi.setRssi(wifi.level);
                    emap.currentWifis.put(currentWifi.estimateDistanceByRssi()
                            + wifi.SSID, currentWifi);
                }
            }
            // Log.d(Constants.ANDROID_LOG_TAG, "3. CurrentList: " +
            // emap.currentWifis.keySet().toString());
            // emap.setWifis(localWifis);
        }
    }

    public static void setIbeaconRssiForEmap(EMap emap, List<Beacon> beacons) {

        if (beacons.size() > 0) {
            Map<String, Ibeacon> localBeacons = emap.getIbeacons();
            emap.currentIbeacons.clear();
            for (Beacon beacon : beacons) {
                if (localBeacons.containsKey(beacon.getMacAddress()
                        .toLowerCase())) {
                    Ibeacon ib = localBeacons.get(beacon.getMacAddress()
                            .toLowerCase());
                    ib.setCurrentDistance(getDistance(beacon));
                    ib.setBeacon(beacon);
                    emap.currentIbeacons.put(ib.getCurrentDistance()
                            + beacon.getMacAddress().toLowerCase(), ib);
                }
            }
        }
    }

    public static long getDistance(Beacon beacon) {
        double diagonal = Utils.computeAccuracy(beacon) * 1000;
        double height = 1000;
        double distance = Math.sqrt(diagonal * diagonal + height * height);
        return Math.round(distance);
    }

    public static Position getCurrentPoint(EMap emap, Position lastPos,
            RPosition currentRP) throws Exception {
        return getCurrentPointByIbeacon(emap, lastPos, currentRP);

    }

    public static Position getCurrentPointByIbeacon(EMap emap,
            Position lastPos, RPosition currentRP) throws Exception {
        long intervalTime = currentRP.getTime() - lastPos.getTime();
        Position currentPos = new Position(lastPos.x
                + currentRP.getOrientationX(intervalTime), lastPos.y
                + currentRP.getOrientationX(intervalTime), currentRP.getTime());
        return currentPos;

    }

    public static Position getLastPosition() {
        return null;
    }

    public static SLineEquation getSLineEquation(CircleEquation c1,
            CircleEquation c2) throws Exception {
        return MathUtil.getSquareSLineOnC1C2(c1, c2);

    }

    public static Position getMidPosition(CircleEquation c1, CircleEquation c2)
            throws Exception {
        SLineEquation sl = getSLineEquation(c1, c2);
        SLineEquation rsl = new SLineEquation(c1.getPoint(), c2.getPoint());
        Position p = new Position(sl, rsl);
        return p;
    }

}
