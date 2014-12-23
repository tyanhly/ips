package com.kiss.model;


public class MEObservedData extends com.kiss.core.Object{

    public static int ACCEL_UP=1;
    public static int ACCEL_DOWN=2;
    public static int ACCEL_OTHER=3;
    
    
    public int data;
    public MEObservedData(int data) throws MarkovException{
        if(data == ACCEL_UP || data == ACCEL_DOWN || data == ACCEL_OTHER){
            this.data = data;
        }else{
            throw new MarkovException(MarkovException.msgs.get(MarkovException.MARKOV_DATA_NOT_IN_RANGE));
        }
    }
    

    @Override
    public boolean equals(Object o) {
        MEObservedData obj = (MEObservedData) o;
        if (this.data == obj.data) {
            return true;
        }
        return false;
    }
//
    @Override
    public int hashCode() {
        return data ;
    }
}
