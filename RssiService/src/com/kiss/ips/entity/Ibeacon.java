package com.kiss.ips.entity;

import com.estimote.sdk.Beacon;


public class Ibeacon extends Wireless {
    private Beacon beacon;
    
    public Ibeacon(String mac, Position pos) {
        super(mac, pos);
    }

    public Ibeacon(String mac, Position pos, Beacon beacon) {
        super(mac, pos);
        this.beacon = beacon;
    }

    public Beacon getBeacon() {
        return beacon;
    }
    public void setBeacon(Beacon beacon) {
        this.beacon = beacon;
    }
    


}
