package com.kiss.ips.entity;

import com.kiss.math.CircleEquation;

public class Position {
    public Position() {
    }

    public Position(long x, long y) {
        this.x = x;
        this.y = y;
    }

    public long x;
    public long y;
    
    public CircleEquation getCircleEquationBySeconds(float sec) throws Exception{
        return new CircleEquation((float) this.x, (float) this.y, getMaxRBySeconds(sec));
    }
    
    public float getMaxRBySeconds(float sec){
        return sec*2*100;
    }

}
