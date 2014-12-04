package com.kiss.math;


/**
 * Equation: ax + by + c = 0
 * @author tyanhly
 */
public class SLineEquation {
    /**
     * ax + by + c = 0
     */
    private double a;
    private double b;
    private double c;
    
    public SLineEquation(double a, double b, double c) throws Exception{
        if(a == 0 & b==0){
            throw new Exception("a=0 and b=0, it not a straight line");
        }
        this.a = a;
        this.b = b;
        this.c = c;
    }
    
    public SLineEquation(Point p1, Point p2){
        double vectory = p1.x - p2.x;
        double vectorx = -(p1.y - p2.y);
        this.a = vectorx;
        this.b = vectory;
        this.c = -vectorx*p1.x - vectory*p1.y;
        
    }
    
    public double getX(double y) throws Exception{
        if(a==0){
            throw new Exception("a=0, x = anything");
        }
        double x = -(c + b*y)/a;
        return x;
    }
    

    public double getY(double x) throws Exception{
        if(b==0){
            throw new Exception("b=0, x = anything");
        }
        double y = -(c + a*x)/b;
        return y;
    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

    public double getC() {
        return c;
    }

    public void setC(double c) {
        this.c = c;
    }
    
}
