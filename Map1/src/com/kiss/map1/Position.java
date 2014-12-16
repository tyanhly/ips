package com.kiss.map1;


public class Position {
    public int x;
    public int y;
    public long time;
    public static final int radius = 10;

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

    public static int getRadius() {
        return radius;
    }

}
