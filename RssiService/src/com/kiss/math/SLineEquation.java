package com.kiss.math;

/**
 * Equation: ax + by + c = 0
 * @author tyanhly
 */
public class SLineEquation {
    /**
     * ax + by + c = 0
     */
    private float a;
    private float b;
    private float c;
    
    public SLineEquation(float a, float b, float c) throws Exception{
        if(a == 0 & b==0){
            throw new Exception("a=0 and b=0, it not a straight line");
        }
        this.a = a;
        this.b = b;
        this.c = c;
    }
    
    public float getX(float y) throws Exception{
        if(a==0){
            throw new Exception("a=0, x = anything");
        }
        float x = -(c + b*y)/a;
        return x;
    }
    

    public float getY(float x) throws Exception{
        if(b==0){
            throw new Exception("b=0, x = anything");
        }
        float y = -(c + a*x)/b;
        return y;
    }

    public float getA() {
        return a;
    }

    public void setA(float a) {
        this.a = a;
    }

    public float getB() {
        return b;
    }

    public void setB(float b) {
        this.b = b;
    }

    public float getC() {
        return c;
    }

    public void setC(float c) {
        this.c = c;
    }
    
}
