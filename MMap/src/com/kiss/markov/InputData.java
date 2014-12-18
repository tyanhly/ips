package com.kiss.markov;

public class InputData {
    int a;
    double azimuth;
    long time;
    public InputData(int a, double azimuth){
        this.a = a;
        this.azimuth = azimuth;
    }
    public InputData(int a, double azimuth, long time){
        this.a = a;
        this.azimuth = azimuth;
        this.time = time;
    }
}
