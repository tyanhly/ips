package com.kiss.map;

import java.util.ArrayList;

public class MovingData extends ArrayList<Position>{


    /**
     * 
     */
    private static final long serialVersionUID = 7921510738797668570L;
    private int maxSize;

    public MovingData(int size){
        this.maxSize = size;
    }

    public boolean add(Position p){
        boolean r = super.add(p);
        if (size() > maxSize){
            removeRange(0, size() - maxSize - 1);
        }
        return r;
    }

}
