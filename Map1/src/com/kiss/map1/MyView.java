package com.kiss.map1;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import com.kiss.config.Constants;

@SuppressLint("UseValueOf")
public class MyView extends View {

    public final int textSize = 20;
    private Paint bpaint;
    private Paint paint;
    private Paint ppaint;
    private Paint txtPaint;

    private int screenWidth;
    private int screenHeight;
    private int xRoot;
    private int yRoot;

    ArrayList<Float> accel = new ArrayList<Float>(2000);

    private Timer fuseTimer = new Timer();

    DecimalFormat d = new DecimalFormat("#.##");

    private LimitedArray<Position> posListData;

    public MyView(Context context) {
        super(context);
        _viewInit();
        _sensorsInit();

    }

    private void _sensorsInit() {

        fuseTimer.scheduleAtFixedRate(new calculateFusedOrientationTask(),
                1000, Constants.THREAD_UPDATE_STEP_MLS);
        // GUI stuff
        d.setRoundingMode(RoundingMode.HALF_UP);
        d.setMaximumFractionDigits(3);
        d.setMinimumFractionDigits(3);
    }

    private void _viewInit() {
        posListData = new LimitedArray<Position>(Constants.POS_SAMPLE_TOTAL);
        bpaint = new Paint();
        bpaint.setStyle(Paint.Style.FILL);
        bpaint.setColor(Color.WHITE);

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);

        ppaint = new Paint();
        ppaint.setStyle(Paint.Style.FILL);
        ppaint.setColor(Color.parseColor("#CD5C5C"));

        txtPaint = new Paint();
        txtPaint.setStyle(Paint.Style.FILL);
        txtPaint.setTextSize(30);
        txtPaint.setColor(Color.parseColor("#CD5C5C"));
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
        // _drawStartPoint(canvas);
        _drawPositions(canvas);
        _drawDebugInformation(canvas);
    }

    protected void _drawDebugInformation(Canvas canvas) {
        Position p = getLastMovingData();

        canvas.drawText(String.format("x=%d;y=%d", p.x, p.y), 30, 30, txtPaint);

        canvas.drawText(String.format("Pos: %d", posListData.size()),
                screenWidth - 330, screenHeight - 30, txtPaint);
    }

    protected void _drawPos(Canvas canvas, Position p, int redColor) {

        ppaint.setColor(redColor);
        canvas.drawCircle(toRX(p.x), toRY(p.y), Position.radius, ppaint);

    }

    protected void _drawPositions(Canvas canvas) {

        for (int d = 0, i = 0; i < posListData.size(); i++, d++) {
            int redColor = Color.rgb(d % 256, d / 256, d / 256);
            _drawPos(canvas, posListData.get(i), redColor);
        }
    }

    protected void _drawStartPoint(Canvas canvas) {
        _drawPos(canvas, new Position(0, 0), 200);
    }

    private int toRX(int x) {
        return x + xRoot;

    }

    private int toRY(int y) {
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
            yRunning -= Constants.PIXEL_ON_METER;

            canvas.drawCircle(xRoot, yRunning, 5, paint);
        }
        yRunning = yRoot;
        while (yRunning < screenHeight) {
            yRunning += Constants.PIXEL_ON_METER;

            canvas.drawCircle(xRoot, yRunning, 5, paint);
        }

        int xRunning = xRoot;
        while (xRunning > 0) {
            xRunning -= Constants.PIXEL_ON_METER;
            canvas.drawCircle(xRunning, yRoot, 5, paint);
        }
        xRunning = xRoot;
        while (xRunning < screenWidth) {
            xRunning += Constants.PIXEL_ON_METER;
            canvas.drawCircle(xRunning, yRoot, 5, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN: {
            posListData.clear();
            invalidate();
        }
        }

        return super.onTouchEvent(ev);
    }

    public void addPos(Position p) {
        posListData.add(p);
    }

    public Position getLastMovingData() {
        if (posListData.isEmpty()) {
            return new Position(0, 0, System.nanoTime());
        }
        return posListData.get(posListData.size() - 1);
    }

    public void setAccel(float[] accel) {
        float t = (float) Math.sqrt(accel[0] * accel[0] + accel[1] * accel[1]
                + accel[2] * accel[2]);
        this.accel.add(new Float(t));
    }

    public void invalidate() {
        super.invalidate();
    }

    private float getEsMinOfTopValue() {
        float targetNumberRate = (1224 - 50) / 1224;
         @SuppressWarnings("unchecked")
         ArrayList<Float> tmpArr = (ArrayList<Float>) this.accel.clone();
         Collections.sort(tmpArr);
         int estTargetAmount = Math.round(targetNumberRate * tmpArr.size());
        
         if(tmpArr.get(estTargetAmount)<10.2){
             return 10.2f;
         }
         return tmpArr.get(estTargetAmount);
    }

    public void cal() {
        float tmp;
        float min = getEsMinOfTopValue();
        float stepConstant = 0.4f * Constants.PIXEL_ON_METER;
        int i = 0;
        while ((this.accel.size() - 5) / 5 > 0) {

            tmp = (this.accel.get(i).floatValue()
                    + this.accel.get(i + 1).floatValue()
                    + this.accel.get(i + 2).floatValue()
                    + this.accel.get(i + 3).floatValue()
                    + this.accel.get(i + 4).floatValue()) / 5;
            if (tmp > min) {
                Position p = this.getLastMovingData();
                this.posListData.add(new Position(0, Math.round(p.y
                        + stepConstant)));
            }

            this.accel.remove(i);
            this.accel.remove(i + 1);
            this.accel.remove(i + 2);
            this.accel.remove(i + 3);
            this.accel.remove(i + 4);
        }

    }

    class calculateFusedOrientationTask extends TimerTask {
        public void run() {
            MyView.this.cal();
        }
    }
}
