package com.kiss.ips.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import android.net.wifi.ScanResult;
import android.util.Log;

import com.kiss.ips.entity.EMap;
import com.kiss.ips.entity.Ibeacon;
import com.kiss.ips.entity.Position;
import com.kiss.ips.entity.Wifi;
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

        Ibeacon ib = new Ibeacon(40000, 0);
        ibeacons.put(ib.getUuid(), ib);

        Wifi wifi = new Wifi(32000, 56000);
        wifi.setSsid("KISS");
        wifis.put(wifi.getSsid(), wifi);

        tmpmap.setHeight(15000);
        tmpmap.setWidth(10000);
        tmpmap.setIbeacons(ibeacons);
        tmpmap.setWifis(wifis);
        return tmpmap;
    }

    public static void setEMap() {
        emap = getSampleMap();
    }

    public static void setRssiForEmap(EMap emap, List<ScanResult> wifis) {

        if (wifis.size() > 0) {
            Map<String, Wifi> localWifis = emap.getWifis();
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
//            emap.setWifis(localWifis);
        }
    }
    
    

    public static Position getCurrentPosition(EMap emap) {
        
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
            int x1 = en1.getValue().getPos().x;
            int y1 = en1.getValue().getPos().y;
            int R1 = en1.getValue().getCurrentDistance();
            int x2 = en2.getValue().getPos().x;
            int y2 = en2.getValue().getPos().y;
            int R2 = en1.getValue().getCurrentDistance();
            float a = 2*(x2-x1);
            float b = 2*(y2-y1);
            float c = x1*x1 + y1*y1 - (x2*x2 - y2*y2) - R1*R1 + R2*R2;
            try {
                SLineEquation sl = new SLineEquation(a, b, c);
                MathUtil.F1Result result = MathUtil.solveF1(en1.getValue().exportToCircleEquation(), sl);
                return result.getCentral();
            } catch (Exception e) {
                Log.d("TungLog", "Wifi data error: " + e.getMessage());
                return en1.getValue().getPos();
            }
        }else if(wifiSize > 2){
//            Map.Entry<String, Wifi> en1 = (Map.Entry<String, Wifi>) iterator.next();
//            while (iterator.hasNext()) {
//                
//            }
            return null;
        }
        return null;

    }

}
