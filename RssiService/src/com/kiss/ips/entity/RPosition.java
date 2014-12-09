package com.kiss.ips.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.kiss.config.Constants;

public class RPosition {
    long time;
    float v0[];
    float accelerates[];
    long intervalTime;
    
    public float[] getV(){
        return new float[]{
            accelerates[0]*intervalTime,
            accelerates[1]*intervalTime,
            accelerates[2]*intervalTime
        };
    }
    public RPosition(float accelerates[], float v0[], long time, long intervalTime) {
        this.accelerates = accelerates;
        this.intervalTime = intervalTime;
        this.v0 = v0;
        this.time = time;
    }

    public RPosition(float accelerates[], float v0[], long time) {
        this.accelerates = accelerates;
        this.time = time;
    }

    public double getDistance(long intervalTime) {
        double x = _getOrientationX(intervalTime);
        double y = _getOrientationY(intervalTime);

        return Math.sqrt(x * x + y * y);
    }

    public double _getOrientationX(long intervalTime) {
        double x = (v0[0] + accelerates[0] * intervalTime) * intervalTime;
        return x;
    }

    public long getOrientationX(long intervalTime) {
        return (long) _getOrientationX(intervalTime);
    }

    public double _getOrientationY(long intervalTime) {
        double y = (v0[1] + accelerates[1] * intervalTime) * intervalTime;
        return y;
    }

    public long getOrientationY(long intervalTime) {
        return (long) _getOrientationY(intervalTime);
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public float[] getAccelerates() {
        return accelerates;
    }

    public void setAccelerates(float[] accelerates) {
        this.accelerates = accelerates;
    }

    public float[] getV0() {
        return v0;
    }

    public void setV0(float[] v0) {
        this.v0 = v0;
    }

    public long getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(long intervalTime) {
        this.intervalTime = intervalTime;
    }

    public String toJSon() throws JSONException  {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("time", this.time);
        JSONArray arr = new JSONArray();
        for(float a : this.accelerates){
            arr.put(a);
        }
        
        jsonObj.put("accelerates", arr);

        JSONArray arrV = new JSONArray();
        for(float v : this.v0){
            arrV.put(v);
        }
        jsonObj.put("v0", arrV);
        jsonObj.put("intervalTime", this.intervalTime);
        return jsonObj.toString();
    }

    public RPosition(String jsonString) {
        JSONObject json;
        try {
            
            json = new JSONObject(jsonString);
            this.time = json.getLong("time");
            JSONArray accels = json.getJSONArray("accelerates");
            for(int i=0;i < accels.length();i++){   
                this.accelerates[i] = (float) accels.getDouble(i);
            }
            
            JSONArray v0 = json.getJSONArray("v0");
            for(int i=0;i < accels.length();i++){   
                this.v0[i] = (float) v0.getDouble(i);
            }
            
            if(json.has("intervalTime")){
                this.intervalTime = json.getLong("intervalTime");
            }
            
        } catch (JSONException e) {
            Log.d(Constants.ANDROID_LOG_TAG, e.getMessage());
            e.printStackTrace();
        }
    }

}
