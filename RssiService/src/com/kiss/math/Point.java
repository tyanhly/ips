package com.kiss.math;

import org.json.JSONException;
import org.json.JSONObject;

public class Point {

    public long x;
    public long y;

    public Point() {
    }

    public Point(long x, long y) {
        this.x = x;
        this.y = y;
    }

    public Point(String jsonString){
        JSONObject json;
        try {
            json = new JSONObject(jsonString);
            this.x = json.getLong("x");
            this.y = json.getLong("y");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public Point(SLineEquation sl1, SLineEquation sl2) throws Exception {

        double y, x;
        SLineEquation l1, l2;
        if (sl1.getA() * sl2.getB() == sl1.getB() * sl2.getA()) {
            throw new Exception("2 Lines is parallel");
        }
        if (sl1.getB() != 0) {
            l1 = sl1;
            l2 = sl2;
        } else {
            l1 = sl2;
            l2 = sl1;
        }

        x = (l2.getB() * l1.getC() - l1.getB() * l2.getC())
                / (l1.getB() * l2.getA() - l2.getB() * l1.getA());
        y = (-l1.getA()*x - l1.getC()) / l1.getB();
        this.x = Math.round(x);
        this.y = Math.round(y);
    }
    
    protected JSONObject _toJSon() throws JSONException{
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("x", this.x);
        jsonObj.put("y", this.y);
        return jsonObj;
    }
    
    public String toJSon() {
        try {
            return _toJSon().toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }
}
