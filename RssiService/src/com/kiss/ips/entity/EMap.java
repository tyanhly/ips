package com.kiss.ips.entity;

import java.util.Map;
import java.util.TreeMap;

public class EMap {

    private int height;
    private int width;
    private Map<String, Ibeacon> ibeacons;
    private Map<String, Wifi> wifis;
    
    public TreeMap<String, Wifi> currentWifis;
    public TreeMap<String, Wifi> currentIbeacons;

    public EMap() {
    }

    public EMap(int height, int width, Map<String, Ibeacon> ibeacons, Map<String, Wifi> wifis) {
        this.height = height;
        this.width = width;
        this.ibeacons = ibeacons;
        this.wifis = wifis;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public Map<String, Ibeacon> getIbeacons() {
        return ibeacons;
    }

    public void setIbeacons(Map<String, Ibeacon> ibeacons) {
        this.ibeacons = ibeacons;
    }

    public Map<String, Wifi> getWifis() {
        return wifis;
    }

    public void setWifis(Map<String, Wifi> wifis) {
        this.wifis = wifis;
    }

}
