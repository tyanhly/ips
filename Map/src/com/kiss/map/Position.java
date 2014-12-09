package com.kiss.map;

import java.util.Date;

public class Position {
    public int x;
    public int y;
    public long time;
    public float[] v0 = new float[] { 0, 0, 0 }; // unit is m
    public float[] accels = new float[] { 0, 0, 0 }; // unit is m
    public static final int radius = 10;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
        this.time = new Date().getTime();

    }

    public Position(int x, int y, long time) {
        this.x = x;
        this.y = y;
        this.time = time;
    }

    public Position(int x, int y, long time, float[] v0) {
        this.x = x;
        this.y = y;
        this.time = time;
        this.v0 = v0;
    }

    public Position(int x, int y, long time, float[] v0, float[] accels) {
        this.x = x;
        this.y = y;
        this.time = time;
        this.v0 = v0;
        this.accels = accels;
    }

    public float[] getV(long intervalTime) {
        return new float[] { accels[0] * intervalTime,
                accels[1] * intervalTime, accels[2] * intervalTime };
    }

    public Position getNextPosition(long intervalTime) {
        return new Position(
                this.x + getOrientationX(intervalTime), 
                this.y + getOrientationY(intervalTime), 
                new Date().getTime(),
                getV(intervalTime));
    }

    public double getDistance(long intervalTime) {
        double x = _getOrientationX(intervalTime);
        double y = _getOrientationY(intervalTime);

        return Math.sqrt(x * x + y * y);
    }

    public double _getOrientationX(long intervalTime) {
        double x = (v0[0] + accels[0] * intervalTime / 1000) * intervalTime
                / 1000;
        return x * MyView.PIXEL_ON_METER;
    }

    public int getOrientationX(long intervalTime) {
        return (int) _getOrientationX(intervalTime);
    }

    public double _getOrientationY(long intervalTime) {
        double y = (v0[1] + accels[1] * intervalTime / 1000) * intervalTime
                / 1000;
        return y * MyView.PIXEL_ON_METER;
    }

    public int getOrientationY(long intervalTime) {
        return (int) _getOrientationY(intervalTime);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public float[] getV0() {
        return v0;
    }

    public void setV0(float[] v0) {
        this.v0 = v0;
    }

    public float[] getAccels() {
        return accels;
    }

    public void setAccels(float[] accels) {
        this.accels = accels;
    }

    public static int getRadius() {
        return radius;
    }

}
