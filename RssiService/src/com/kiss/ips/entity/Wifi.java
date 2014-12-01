package com.kiss.ips.entity;

public class Wifi extends Wireless {

    public Wifi(int x, int y) {
        super(x, y);
        // TODO Auto-generated constructor stub
    }

    public Wifi(Position pos) {
        super(pos);
        // TODO Auto-generated constructor stub
    }

    private String ssid;

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public int estimateDistanceByRssi() {
        int tmp = (int) -rssi / 10;
        int t;
        switch (tmp) {
        case 0:
            t = 300;
        case 1:
            t = 500;
        case 2:

            t = 1000;
        case 3:
            // 1.1 - 1.9
            t = -rssi % 10;
            t = 1000 + t*100;
        case 4:
            // 2 - 6m
            t = -rssi % 10;
            t = 2000 + t*500;
        case 5:
            // 7 - 11m
            t = -rssi % 10;
            t = 7000 + t*500;
        case 6:

            // 12 - 16m
            t = -rssi % 10;
            t = 12000 + t*500;
        case 7:

            // 17 - 21m
            t = -rssi % 10;
            t = 17000 + t*500;

        case 8:

            // 22 - 26m
            t = -rssi % 10;
            t = 22000 + t*500;
        case 9:

            // 27 - 31m
            t = -rssi % 10;
            t = 27000 + t*500;
        case 10:

            // 32 - 36m
            t = -rssi % 10;
            t = 32000 + t*500;

        case 11:

            // 37 - 41m
            t = -rssi % 10;
            t = 37000 + t*500;

        case 12:

            // 42 - 47m
            t = -rssi % 10;
            t = 42000 + t*500;
        default:
            t = 47000;
        }
        currentDistance = (int) t;
        return currentDistance;
    }
}
