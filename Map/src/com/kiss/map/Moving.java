package com.kiss.map;

public class Moving {

    public float[] a = new float[3];
    public long time;
    
    public Moving(float[] a, long time)
    {
        System.arraycopy(a, 0, this.a, 0, 3);
        this.time = time;
    }
}
