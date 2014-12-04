package com.kiss.ips.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.net.wifi.ScanResult;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.Utils;
import com.kiss.ips.entity.EMap;
import com.kiss.ips.entity.Ibeacon;
import com.kiss.ips.entity.Position;
import com.kiss.ips.entity.Wifi;
import com.kiss.ips.entity.Wireless;
import com.kiss.math.MathUtil;
import com.kiss.math.SLineEquation;

public class MMapbak {

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
        Ibeacon ib1 = new Ibeacon("e3:16:78:6b:f5:5e",new Position(4300, 0));
        Ibeacon ib2 = new Ibeacon("f5:d2:7d:c7:de:9d",new Position(4600, 4000));
        Ibeacon ib3 = new Ibeacon("d9:29:98:43:6d:49",new Position(400, 6000));
        Ibeacon ib4 = new Ibeacon("c2:77:57:87:48:16",new Position(4000, 6000));

        ibeacons.put(ib1.getMac(), ib1);
        ibeacons.put(ib2.getMac(), ib2);
        ibeacons.put(ib3.getMac(), ib3);
        ibeacons.put(ib4.getMac(), ib4);

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

//            Log.d(Constants.ANDROID_LOG_TAG, "1. Static List: " + emap.getWifis().keySet().toString());
//            Log.d(Constants.ANDROID_LOG_TAG, "2. ScanList: " + wifis.toString());
            emap.currentWifis.clear();
            for (ScanResult wifi : wifis) {
                if (localWifis.containsKey(wifi.SSID)) {
                    Wifi currentWifi = localWifis.get(wifi.SSID);
                    currentWifi.setRssi(wifi.level);
                    emap.currentWifis
                            .put(currentWifi.estimateDistanceByRssi()
                                    + wifi.SSID, currentWifi);
                }
            }
//            Log.d(Constants.ANDROID_LOG_TAG, "3. CurrentList: " + emap.currentWifis.keySet().toString());
//            emap.setWifis(localWifis);
        }
    }
    

    public static void setIbeaconRssiForEmap(EMap emap, List<Beacon> beacons) {

        if (beacons.size() > 0) {
            Map<String, Ibeacon> localBeacons = emap.getIbeacons();
            emap.currentIbeacons.clear();
            for (Beacon beacon : beacons) {
                if (localBeacons.containsKey(beacon.getMacAddress().toLowerCase())) {
                    Ibeacon ib = localBeacons.get(beacon.getMacAddress().toLowerCase());
                    ib.setCurrentDistance((long) (Utils.computeAccuracy(beacon)*1000));
                    ib.setBeacon(beacon);
                    emap.currentIbeacons
                            .put(ib.getCurrentDistance() + beacon.getMacAddress().toLowerCase(),ib);
                }
            }
        }
    }

    
    public static Position getCurrentPoint(EMap emap) {
        if(emap.currentIbeacons.size() > 0){
            return getCurrentPointByIbeacon(emap);
        }else{
            return getCurrentPointByWifis(emap);
        }
    }

    public static Position getCurrentPointByWifis(EMap emap) {
        
        Set<Map.Entry<String, Wifi>> wifiSet = emap.currentWifis.entrySet();
        Iterator<Map.Entry<String, Wifi>> iterator = wifiSet.iterator();
        int wifiSize = wifiSet.size();
        
        if(wifiSize == 0){
            return null;
        }else if(wifiSize == 1){
            Map.Entry<String, Wifi> en = (Map.Entry<String, Wifi>) iterator.next();
            return en.getValue().getPos();
        }else if(wifiSize == 2){
            Map.Entry<String, Wifi> en1 = (Map.Entry<String, Wifi>) iterator.next();
            Map.Entry<String, Wifi> en2 = (Map.Entry<String, Wifi>) iterator.next();
            return caculateMidPosition(en1.getValue(), en2.getValue());
        }else{
            Map.Entry<String, Wifi> en1 = (Map.Entry<String, Wifi>) iterator.next();
            List<Position> ps= new ArrayList<Position>();
            int c=1;
            while (iterator.hasNext()) {
                Map.Entry<String, Wifi> tmpEn = (Map.Entry<String, Wifi>) iterator.next();
                if(c>2){
                    break;
                }else {
                    Position p = caculateMidPosition(en1.getValue(), tmpEn.getValue());
                    if(p.x<0 || p.y<0){
                        continue;
                    }else{
                        ps.add(p);
                        c++;
                    }
                }
            }
            long vx = 0;
            long vy = 0;
            int i = 0;
            for(Position pos: ps){
                vx +=pos.x;
                vy +=pos.y;
                i++;
            }
            return new Position((long) vx/i, (long) vy/i);
        }

    }
    
    public static Position getCurrentPointByIbeacon(EMap emap) {
        
        Set<Entry<String, Ibeacon>> ibeaconSet = emap.currentIbeacons.entrySet();
        Iterator<Entry<String, Ibeacon>> iterator = ibeaconSet.iterator();
        int ibeaconSize = ibeaconSet.size();
        
        if(ibeaconSize == 0){
            return null;
        }else if(ibeaconSize == 1){
            Map.Entry<String, Ibeacon> en = (Map.Entry<String, Ibeacon>) iterator.next();
            return en.getValue().getPos();
        }else if(ibeaconSize == 2){
            Map.Entry<String, Ibeacon> en1 = (Map.Entry<String, Ibeacon>) iterator.next();
            Map.Entry<String, Ibeacon> en2 = (Map.Entry<String, Ibeacon>) iterator.next();
            return caculateMidPosition(en1.getValue(), en2.getValue());
        }else{
            Map.Entry<String, Ibeacon> en1 = (Map.Entry<String, Ibeacon>) iterator.next();
            List<Position> ps= new ArrayList<Position>();
            int c=1;
            while (iterator.hasNext()) {
                Map.Entry<String, Ibeacon> tmpEn = (Map.Entry<String, Ibeacon>) iterator.next();
                if(c>2){
                    break;
                }else {
                    Position p = caculateMidPosition(en1.getValue(), tmpEn.getValue());
                    if(p.x<0 || p.y<0){
                        continue;
                    }else{
                        ps.add(p);
                        c++;
                    }
                }
            }
            long vx = 0;
            long vy = 0;
            int i = 0;
            for(Position pos: ps){
                vx +=pos.x;
                vy +=pos.y;
                i++;
            }
            return new Position((long) vx/i, (long) vy/i);
        }
    }
    
    
    
    public static Position caculateMidPosition(Wireless w1, Wireless w2){

        long x1 = w1.getPos().x;
        long y1 = w1.getPos().y;
        long R1 = w1.getCurrentDistance();
        long x2 = w2.getPos().x;
        long y2 = w2.getPos().y;
        long R2 = w2.getCurrentDistance();
        if(R1==0){
            return w1.getPos();
        }else if(R2==0){
            return w2.getPos();
        }
        Log.d(com.kiss.config.Constants.ANDROID_LOG_TAG, "w1: " + w1.getCurrentDistance() + "- w2:" + w2.getCurrentDistance());
        double a = 2*(x2-x1);
        double b = 2*(y2-y1);
        double c = x1*x1 + y1*y1 - (x2*x2 - y2*y2) - R1*R1 + R2*R2;
        try {
            SLineEquation sl = new SLineEquation(a, b, c);
            MathUtil.F1Result result = MathUtil.solveF1(w1.exportToCircleEquation(), sl);
            return result.getEstimateResult();
        } catch (Exception e) {
            Log.d(com.kiss.config.Constants.ANDROID_LOG_TAG, "Wireless data error: " + e.getMessage());
            return w1.getPos();
        }
    }

}
