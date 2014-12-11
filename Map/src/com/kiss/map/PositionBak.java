package com.kiss.map;

import com.kiss.config.Constants;

public class PositionBak {
    public int x;
    public int y;
    public long time;
    public float[] v0 = new float[] { 0.0f, 0.0f, 0.0f }; // unit is m/s
    public float[] earthAccels = new float[] { 0.0f, 0.0f, 0.0f }; // unit is m/s^2
    public static final int radius = 10;

    public PositionBak(int x, int y) {
        this.x = x;
        this.y = y;
        this.time = System.nanoTime();
    }

    public PositionBak(int x, int y, long time) {
        this.x = x;
        this.y = y;
        this.time = time;
    }

    public PositionBak(int x, int y, long time, float[] v0) {
        this.x = x;
        this.y = y;
        this.time = time;
        this.v0 = v0;
    }

    public float[] getV(long intervalTime) {
        return new float[] { earthAccels[0] * intervalTime * Constants.NS2S,
                earthAccels[1] * intervalTime * Constants.NS2S,
                earthAccels[2] * intervalTime * Constants.NS2S };
    }

    public PositionBak getNextPosition(long curTime) {
        long intervalTime = curTime - this.time;
        return new PositionBak(this.x + getOrientationX(intervalTime), this.y
                + getOrientationY(intervalTime), curTime, getV(intervalTime));
    }

    public double getDistance(long intervalTime) {
        double x = _getOrientationX(intervalTime);
        double y = _getOrientationY(intervalTime);

        return Math.sqrt(x * x + y * y);
    }

    public double _getOrientationX(long intervalTime) {
        double x = (v0[0] + earthAccels[0] * intervalTime * Constants.NS2S) * intervalTime
                * Constants.NS2S;

        return x * Constants.PIXEL_ON_METER;
    }

    public int getOrientationX(long intervalTime) {
        return (int)Math.round(_getOrientationX(intervalTime));
    }

    public double _getOrientationY(long intervalTime) {
        double y = (v0[1] + earthAccels[1] * intervalTime * Constants.NS2S) * intervalTime
                * Constants.NS2S;
        return y * Constants.PIXEL_ON_METER;
    }

    public int getOrientationY(long intervalTime) {
        return (int) Math.round(_getOrientationY(intervalTime));
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

    public float[] getEarthAccels() {
        return earthAccels;
    }

    public void setEarthAccels(float[] accels) {
        this.earthAccels = accels;
    }

    public static int getRadius() {
        return radius;
    }

}
