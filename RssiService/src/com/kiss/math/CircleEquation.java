package com.kiss.math;

/**
 * Equation: (x-x0)^2 + (y-y0)^2 - R^2 = 0
 * @author tyanhly
 */
public class CircleEquation {
    /**
     * (x-x0)^2 + (y-y0)^2 - R^2 = 0
     */
    private float x0;
    private float y0;
    private float R;
    
    public CircleEquation(float x0, float y0, float R) throws Exception{
        if(R==0){
            throw new Exception("R=0; it is not a circle");
        }
        this.x0 = x0;
        this.y0 = y0;
        this.R = R;
    }

    public float getX(float y) throws Exception{
        if(y-y0 > R){
            throw new Exception("y-y0>R; Data fail");
        }
        float x = (float) Math.sqrt(R*R - (y-y0)*(y-y0));
        return x;
    }
    

    public float getY(float x) throws Exception{
        if(x-x0 > R){
            throw new Exception("x-x0>R; Data fail");
        }
        float y = (float) Math.sqrt(R*R - (x-x0)*(x-x0));
        return y;
    }

    public float getX0() {
        return x0;
    }

    public void setX0(float x0) {
        this.x0 = x0;
    }

    public float getY0() {
        return y0;
    }

    public void setY0(float y0) {
        this.y0 = y0;
    }

    public float getR() {
        return R;
    }

    public void setR(float r) {
        R = r;
    }
    

}
