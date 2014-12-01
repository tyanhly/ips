package com.kiss.ips.entity;

public class Wifi extends Wireless {

    private int rssi;
    public Wifi(String mac, Position pos) {
        super(mac, pos);
        // TODO Auto-generated constructor stub
    }


    private String ssid;

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public long estimateDistanceByRssi() {
        int tmp = (int) -rssi / 10;
        int t;
        switch (tmp) {
        case 0:
            t = 300;
            break;
        case 1:
            t = 500;
            break;
        case 2:

            t = 1000;
            break;
        case 3:
            // 1.1 - 1.9
            t = -rssi % 10;
            t = 1000 + t * 100;
            break;
        case 4:
            // 2 - 6m
            t = -rssi % 10;
            t = 2000 + t * 500;
            break;
        case 5:
            // 7 - 11m
            t = -rssi % 10;
            t = 7000 + t * 500;
            break;
        case 6:

            // 12 - 16m
            t = -rssi % 10;
            t = 12000 + t * 500;
            break;
        case 7:

            // 17 - 21m
            t = -rssi % 10;
            t = 17000 + t * 500;
            break;

        case 8:

            // 22 - 26m
            t = -rssi % 10;
            t = 22000 + t * 500;
            break;
        case 9:

            // 27 - 31m
            t = -rssi % 10;
            t = 27000 + t * 500;
            break;
        case 10:

            // 32 - 36m
            t = -rssi % 10;
            t = 32000 + t * 500;
            break;

        case 11:

            // 37 - 41m
            t = -rssi % 10;
            t = 37000 + t * 500;
            break;

        case 12:

            // 42 - 47m
            t = -rssi % 10;
            t = 42000 + t * 500;
            break;
        default:
            t = 47000;
            break;
        }
        currentDistance = (long) t;
        return currentDistance;
    }

    public int getRssi() {

        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
        estimateDistanceByRssi();
    }

}
