package com.kiss.model;

public class MEStatus extends com.kiss.core.Object {
    public int a;
    public int less = 0;
    public double azimuth = 0;
    public long time;

    public static final int EPSILON_BEFORE = 2;
    public static final int EPSILON_AFTER = 2;

    public MEStatus(int a, long time, double azimuth){
        this.a = a;
        this.time = time;
        this.azimuth = azimuth;
    }
    @Override
    public boolean equals(Object o) {
        MEStatus obj = (MEStatus) o;
        if (this.a == obj.a && less == obj.less) {
            
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return a * 1000 + less;
    }
}
