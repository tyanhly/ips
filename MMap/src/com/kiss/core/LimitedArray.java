package com.kiss.core;

import java.util.ArrayList;

public class LimitedArray<T> extends ArrayList<T>{


    /**
     * 
     */
    private static final long serialVersionUID = 7921510738797668570L;
    private int maxSize;

    public LimitedArray(int size){
        this.maxSize = size;
    }

    public boolean add(T p){
        boolean r = super.add(p);
        if (size() > maxSize){
            removeRange(0, size() - maxSize - 1);
        }
        return r;
    }

}
