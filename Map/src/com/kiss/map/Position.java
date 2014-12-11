package com.kiss.map;

import java.util.ArrayList;

import com.kiss.config.Constants;

public class Position {
    public int x;
    public int y;
    public long time;
    public float[] v0 = new float[] { 0.0f, 0.0f, 0.0f }; // unit is m/s
    public ArrayList<Moving> movings = new ArrayList<Moving>(); // unit is m/s^2
    public static final int radius = 10;

    public ArrayList<Moving> getMovings() {
        return movings;
    }

    public void setMovings(ArrayList<Moving> movings) {
        this.movings = movings;
    }

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
        this.time = System.nanoTime();
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

    public float[] getV() {
        return _getV();
    }

    public float[] _getV() {
        float[] tmpV0 = new float[3];
        System.arraycopy(this.v0, 0, tmpV0, 0, 3);
        long tmpTime = this.time;
        for (int i = 0; i < movings.size(); i++) {
            float interval = (movings.get(i).time - tmpTime) * Constants.NS2S;
            tmpV0[0] = tmpV0[0] + this.movings.get(i).a[0] * interval;
            tmpV0[1] = tmpV0[1] + this.movings.get(i).a[1] * interval;
            tmpV0[2] = tmpV0[2] + this.movings.get(i).a[2] * interval;
            tmpTime = movings.get(i).time;
        }
        return tmpV0;
    }

    
    public Position getNextPosition(long curTime) {
        return new Position(this.x + getOrientationX(), this.y
                + getOrientationY(), curTime, getV());
    }

    public double getDistance(long intervalTime) {
        double x = _getOrientationX();
        double y = _getOrientationY();

        return Math.sqrt(x * x + y * y);
    }

    public double _getOrientationX() {
        double tmpS = 0;
        double tmpV = this.v0[0];
        long tmpTime = this.time;
        for (int i = 0; i < movings.size(); i++) {
            double interval = (movings.get(i).time - tmpTime) * Constants.NS2S;
            tmpS += (tmpV + this.movings.get(i).a[0] * interval / 2) * interval;
            tmpTime = movings.get(i).time;
        }
        return tmpS * Constants.PIXEL_ON_METER;
    }

    public int getOrientationX() {
        return (int) Math.round(_getOrientationX());
    }

    public double _getOrientationY() {
        double tmpS = 0;
        double tmpV = this.v0[1];
        long tmpTime = this.time;
        for (int i = 0; i < movings.size(); i++) {
            double interval = (movings.get(i).time - tmpTime) * Constants.NS2S;
            tmpS += (tmpV + this.movings.get(i).a[1] * interval / 2) * interval;
            tmpTime = movings.get(i).time;
        }
        return tmpS * Constants.PIXEL_ON_METER;
    }

    public int getOrientationY() {
        return (int) Math.round(_getOrientationY());
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

    public static int getRadius() {
        return radius;
    }

}
