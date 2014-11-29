package com.kiss.ips.entity;


public class Ibeacon extends Wireless {
    
    private String minor;
    private String major;
    private String uuid;
    
    public Ibeacon(int x, int y) {
        super(x, y);
    }

    public Ibeacon(Position pos) {
        super(pos);
    }
    
    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public int estimateDistanceByRssi() {
        return 0;
    }

}
