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

    public Position getNextPosition(long curTime) {
        return new Position(this.x + getOrientationX(), this.y
                + getOrientationY(), curTime, getV());
    }

    public double getDistance(long intervalTime) {
        double x = _getOrientationXv2();
        double y = _getOrientationYv2();

        return Math.sqrt(x * x + y * y);
    }

    public float[] getV() {
        return _getVv2();
    }

    public int getOrientationX() {
        return (int) Math.round(_getOrientationXv2());
    }


    public int getOrientationY() {
        return (int) Math.round(_getOrientationYv2());
    }
    public float[] _getVv1() {
        float[] tmpV0 = new float[3];
        float a0 = 0.0f, a1 = 0.0f, a2 = 0.0f;
        for (int i = 0; i < movings.size(); i++) {
            a0 += this.movings.get(i).a[0];
            a1 += this.movings.get(i).a[2];
            a2 += this.movings.get(i).a[1];
        }

        a0 = a0 / movings.size();
        a1 = a1 / movings.size();
        a2 = a2 / movings.size();
        long interval = Math.round((this.time - movings.get(0).time)
                * Constants.NS2S);
        
        tmpV0[0] = a0 * interval;
        tmpV0[1] = a1 * interval;
        tmpV0[2] = a2 * interval;
        return tmpV0;
    }
    public double _getOrientationXv1() {
        double tmpS = 0;
        double tmpV = this.v0[0];

        float a0 = 0.0f;
        for (int i = 0; i < movings.size(); i++) {
            a0 += this.movings.get(i).a[0];
        }
        a0 = a0 / movings.size();
        long interval = Math.round((this.time - movings.get(0).time)
                * Constants.NS2S);
        tmpS = (tmpV * interval + 1 / 2 * a0 * a0 * interval)
                * Constants.PIXEL_ON_METER;
        return tmpS;
    }

    public double _getOrientationYv1() {
        double tmpS = 0;
        double tmpV = this.v0[1];

        float a1 = 0.0f;
        for (int i = 0; i < movings.size(); i++) {
            a1 += this.movings.get(i).a[1];
        }
        a1 = a1 / movings.size();
        long interval = Math.round((this.time - movings.get(0).time)
                * Constants.NS2S);
        tmpS = (tmpV * interval + 1 / 2 * a1 * a1 * interval)
                * Constants.PIXEL_ON_METER;
        return tmpS;
    }


    public float[] _getVv2() {
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

    public double _getOrientationXv2() {
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

    public double _getOrientationYv2() {
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
