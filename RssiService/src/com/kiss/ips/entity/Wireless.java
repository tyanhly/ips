package com.kiss.ips.entity;

import com.kiss.math.CircleEquation;

public abstract class Wireless {

    protected long currentDistance;
    protected String mac;
    protected Position pos;

    public Wireless(String mac, Position pos){
        this.pos = pos;
        this.mac = mac;
    }
    
    public Wireless(Position pos){
        this.pos = pos;
    }
    
    public Wireless(int x, int y){
        this.pos = new Position(x, y);
    }
    public String getMac() {
        return mac.toLowerCase();
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public Position getPos() {
        return pos;
    }

    public void setPos(Position pos) {
        this.pos = pos;
    }

    public long getCurrentDistance() {
        return currentDistance;
    }

    public void setCurrentDistance(long currentDistance) {
        this.currentDistance = currentDistance;
    }
    
    public CircleEquation exportToCircleEquation() throws Exception{
        CircleEquation ce = new CircleEquation((float) this.pos.x, (float) this.pos.y, (float) this.getCurrentDistance());
        return ce;
    }
    
}
