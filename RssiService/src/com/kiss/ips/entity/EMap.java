package com.kiss.ips.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class EMap {

    private int height;
    private int width;
    private Map<String, Ibeacon> ibeacons = new HashMap<String, Ibeacon>();
    private Map<String, Wifi> wifis = new HashMap<String, Wifi>();;
    
    public TreeMap<String, Wifi> currentWifis = new TreeMap<String, Wifi>();
    public TreeMap<String, Ibeacon> currentIbeacons= new TreeMap<String, Ibeacon>();

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
