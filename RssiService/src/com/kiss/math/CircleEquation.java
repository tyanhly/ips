package com.kiss.math;

/**
 * Equation: (x-x0)^2 + (y-y0)^2 - R^2 = 0
 * @author tyanhly
 */
public class CircleEquation {
    /**
     * (x-x0)^2 + (y-y0)^2 - R^2 = 0
     */
    private double x0;
    private double y0;
    private double R;
    
    public CircleEquation(double x0, double y0, double R) throws Exception{
        if(R==0){
            throw new Exception("R=0; it is not a circle");
        }
        this.x0 = x0;
        this.y0 = y0;
        this.R = R;
    }

    public double getX(double y) throws Exception{
        if(y-y0 > R){
            throw new Exception("y-y0>R; Data fail");
        }
        double x = (double) Math.sqrt(R*R - (y-y0)*(y-y0));
        return x;
    }
    

    public double getY(double x) throws Exception{
        if(x-x0 > R){
            throw new Exception("x-x0>R; Data fail");
        }
        double y = (double) Math.sqrt(R*R - (x-x0)*(x-x0));
        return y;
    }

    public double getX0() {
        return x0;
    }

    public void setX0(double x0) {
        this.x0 = x0;
    }

    public double getY0() {
        return y0;
    }

    public void setY0(double y0) {
        this.y0 = y0;
    }

    public double getR() {
        return R;
    }

    public void setR(double r) {
        R = r;
    }
    
    public Point getPoint(){
        return new Point((long) x0, (long) y0);
    }

    /**
     * IN: 1
     * OUT: -1
     * ON: 0
     * @param p
     * @return
     */
    public int getInOutOn(Point p){
        double tmp = this.R * this.R - p.x*p.x + p.y*p.y;
        if(tmp > 0){
            return 1;
        }else if(tmp<0){
            return -1;
        }else{
            return 0;
        }
    }
}
