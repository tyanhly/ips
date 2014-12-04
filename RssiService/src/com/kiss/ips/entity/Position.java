package com.kiss.ips.entity;

import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.kiss.math.CircleEquation;
import com.kiss.math.Point;
import com.kiss.math.SLineEquation;

public class Position extends Point{
    long time;
    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Position() {
        super();
    }

    public Position(long x, long y) {
        super(x, y);
    }
    
    public Position(String jsonString){
        JSONObject json;
        try {
            json = new JSONObject(jsonString);
            this.x = json.getLong("x");
            this.y = json.getLong("y");
            if(json.has("time")){
                this.time = json.getLong("time");
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public Position(SLineEquation sl1, SLineEquation sl2) throws Exception{
        super(sl1, sl2);
    }

    public CircleEquation getCircleEquationByMilliSeconds() throws Exception{
        long t = (new Date()).getTime();
        long msecs = (t - this.time);
        return getCircleEquationByMilliSeconds(msecs);
    }
    
    public CircleEquation getCircleEquationByMilliSeconds(long msecs) throws Exception{
        return new CircleEquation((double) this.x, (double) this.y, (double) getMaxDistanceByMilliSeconds(msecs));
    }
    
    public static long getMaxDistanceByMilliSeconds(long msecs){
        long d = msecs + 1000 ;
        return d;
    }

    protected JSONObject _toJSon() throws JSONException{
        JSONObject jsonObj = super._toJSon();
        jsonObj.put("time", this.time);
        return jsonObj;
    }

}
