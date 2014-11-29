package com.kiss.math;


class QuadraticEquation {
    private float a;
    private float b;
    private float c;
    private float discriminant;
    QuadraticEquation(float newA, float newB, float newC) throws Exception {
        if(newA ==0 ){
            throw new Exception("a=0; that is not a quadratic equation");
        }
        a = newA;
        b = newB;
        c = newC;
        discriminant = (b*b - 4 * a * c);
    }
    
    public float getDiscriminant() {
        return discriminant;
    }
    public float getV1(){
        return (float) (-b + Math.sqrt(getDiscriminant()))/(2*a);
    }
    public float getV2(){
        return (float) (-b - Math.sqrt(getDiscriminant()))/(2*a);
    }
}