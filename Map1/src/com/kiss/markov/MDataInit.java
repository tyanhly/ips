package com.kiss.markov;

import java.util.HashMap;

import android.annotation.SuppressLint;

@SuppressLint("UseValueOf")
public class MDataInit extends HashMap<Integer, Integer>{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public static int ACCEL_UP=1;
    public static int ACCEL_DOWN=2;
    public static int ACCEL_XY=3;
    
    public MDataInit(){
        this.put(ACCEL_UP, 92);
        this.put(ACCEL_DOWN,92);
        this.put(ACCEL_XY,92);
    }
    
    public void put(int k, int v){
        this.put(new Integer(k), new Integer(v));
    }
}
