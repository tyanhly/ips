package com.kiss.ips.entity;

import com.kiss.math.CircleEquation;

public abstract class Wireless {

    protected int rssi = 120;
    protected int currentDistance;
    protected String mac;
    protected int errorTerm;
    protected Position pos;

    public Wireless(Position pos){
        this.pos = pos;
    }
    
    public Wireless(int x, int y){
        this.pos = new Position(x, y);
    }
    public int getRssi() {
        
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
        estimateDistanceByRssi();
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getErrorTerm() {
        return errorTerm;
    }

    public void setErrorTerm(int errorTerm) {
        this.errorTerm = errorTerm;
    }

    public Position getPos() {
        return pos;
    }

    public void setPos(Position pos) {
        this.pos = pos;
    }

    public int getCurrentDistance() {
        return currentDistance;
    }

    public void setCurrentDistance(int currentDistance) {
        this.currentDistance = currentDistance;
    }

    public abstract int estimateDistanceByRssi() ;
    
    
    public CircleEquation exportToCircleEquation() throws Exception{
        CircleEquation ce = new CircleEquation((float) this.pos.x, (float) this.pos.y, (float) this.getCurrentDistance());
        return ce;
    }
    
}
