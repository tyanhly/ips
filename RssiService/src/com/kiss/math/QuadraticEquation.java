package com.kiss.math;


class QuadraticEquation {
    private double a;
    private double b;
    private double c;
    private double discriminant;
    QuadraticEquation(double newA, double newB, double newC) throws Exception {
        if(newA ==0 ){
            throw new Exception("a=0; that is not a quadratic equation");
        }
        a = newA;
        b = newB;
        c = newC;
        discriminant = (b*b - 4 * a * c);
    }
    
    public double getDiscriminant() {
        return discriminant;
    }
    public double getV1(){
        return (double) (-b + Math.sqrt(getDiscriminant()))/(2*a);
    }
    public double getV2(){
        return (double) (-b - Math.sqrt(getDiscriminant()))/(2*a);
    }
}