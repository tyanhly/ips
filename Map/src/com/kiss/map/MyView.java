package com.kiss.map;

import java.util.Date;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

public class MyView extends View {

    public final int textSize = 20;
    public static final int PIXEL_ON_METER = 10;
    public static final int MOVING_SAMPLE_TOTAL = 1000;
    private Paint bpaint;
    private Paint paint;
    private Paint ppaint;
    private int screenWidth;
    private int screenHeight;
    private int xRoot;
    private int yRoot;

    private MovingData movingData;
    public MyView(Context context) {
        super(context);
        _init();

    }

    private void _init() {
        movingData = new MovingData(MOVING_SAMPLE_TOTAL);
        bpaint = new Paint();
        bpaint.setStyle(Paint.Style.FILL);
        bpaint.setColor(Color.WHITE);
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        ppaint = new Paint();
        ppaint.setStyle(Paint.Style.FILL);
        ppaint.setColor(Color.parseColor("#CD5C5C"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        screenWidth = getWidth();
        screenHeight = getHeight();
        xRoot = screenWidth / 2;
        yRoot = screenHeight / 2;
        canvas.drawPaint(bpaint);
        _drawCoordinate(canvas);
//        _drawStartPoint(canvas);
        _drawPositions(canvas);
    }

    protected void _drawPos(Canvas canvas, Position p, int redColor) {

        ppaint.setColor(redColor);
        canvas.drawCircle(toRX(p.x), toRY(p.y), Position.radius, ppaint);
    }

    
    protected void _drawPositions(Canvas canvas){
        for(int d=256*10-1, i = movingData.size()-1; i >= 0 ; i--, d--){
            int redColor = Color.rgb(d%256, 100-d/256, 100 -d/256);
            _drawPos(canvas, movingData.get(i), redColor);
        }
    }

    protected void _drawStartPoint(Canvas canvas) {
        _drawPos(canvas, new Position(0, 0), 200);
    }
    
    private int toRX(int x){
        return x + xRoot;
        
    }

    private int toRY(int y){
        return y + yRoot;
        
    }
    protected void _drawCoordinate(Canvas canvas) {


        canvas.drawLine(xRoot, 0, xRoot, screenHeight, paint);
        canvas.drawLine(0, yRoot, screenWidth, yRoot, paint);

        paint.setTextSize(textSize);
        canvas.drawText("y:" + yRoot, xRoot + 2, 20, paint);
        canvas.drawText("x:" + xRoot, screenWidth - 60, yRoot + 20, paint);

        canvas.drawCircle(xRoot, yRoot, 5, paint);

        int yRunning = yRoot;
        while (yRunning > 0) {
            yRunning -= PIXEL_ON_METER;

            canvas.drawCircle(xRoot, yRunning, 5, paint);
        }
        yRunning = yRoot;
        while (yRunning < screenHeight) {
            yRunning += PIXEL_ON_METER;

            canvas.drawCircle(xRoot, yRunning, 5, paint);
        }

        int xRunning = xRoot;
        while (xRunning > 0) {
            xRunning -= PIXEL_ON_METER;
            canvas.drawCircle(xRunning, yRoot, 5, paint);
        }
        xRunning = xRoot;
        while (xRunning < screenWidth) {
            xRunning += PIXEL_ON_METER;
            canvas.drawCircle(xRunning, yRoot, 5, paint);
        }
    }
    

    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        
        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                movingData.clear();
                invalidate();
            }
        }
        
        return super.onTouchEvent(ev);
    }
    

    public void addPos(Position p) {
        movingData.add(p);
        invalidate();
    }


    public Position getLastMovingData(){
        if(movingData.isEmpty()){
            return new Position(0,0, (new Date().getTime()-10));
        }
        return movingData.get(movingData.size()-1);
    }
    
    
}
