package com.kiss.mmap;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.SensorEvent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.goatstone.util.SensorFusion;
import com.kiss.config.Constants;
import com.kiss.markov.MEObservedData;
import com.kiss.markov.MEStatus;
import com.kiss.markov.MTrainedData;
import com.kiss.markov.Markov;
import com.kiss.markov.MarkovException;
import com.kiss.markov.SampleData;

@SuppressLint({ "UseValueOf", "NewApi" })
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

    ArrayList<Integer> accel = new ArrayList<Integer>();

    private Timer fuseTimer = new Timer();

    DecimalFormat d = new DecimalFormat("#.##");

    private SensorFusion sensorFusion;

    ArrayList<Integer> accelExecute = new ArrayList<Integer>();
    private Markov mk;
    private LimitedArray<Position> posListData;

    public MyView(Context context) {
        super(context);

        sensorFusion = new SensorFusion();
        sensorFusion.setMode(SensorFusion.Mode.FUSION);

        try {
            mk = new Markov();
        } catch (MarkovException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        _viewInit();
        _sensorsInit();

    }

    private void _sensorsInit() {

        // fuseTimer.scheduleAtFixedRate(new calculateFusedOrientationTask(),
        // 1000, Constants.THREAD_UPDATE_STEP_MLS);
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
        // xRoot = 0;
        // yRoot = 0;
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

    public void printSampleTestData() {

        // SampleData t;
        // try {
        // t = new SampleData();
        // int i=0;
        // for(MEStatus es : t.statusSet){
        // Log.d("SampleDataTest", i++ + ":" + es.id);
        // }
        // } catch (MarkovException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        printSampleTestData();
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

    public void setAccel(SensorEvent event) {

        sensorFusion.setAccel(event.values);
        sensorFusion.calculateAccMagOrientation();

        int t = (int) Math.round(Math.sqrt(event.values[0] * event.values[0]
                + event.values[1] * event.values[1] + event.values[2]
                * event.values[2]) * 10.0f);
        this.accel.add(new Integer(t));
    }

    public void setGyro(SensorEvent event) {

        sensorFusion.gyroFunction(event);
    }

    public void setMag(float[] mag) {
        sensorFusion.setMagnet(mag);
    }

    public void calculate() {
        accelExecute.addAll(accel);
        Log.d("StartEnd", "Start" + accelExecute.size());
        this.accel.clear();

        // Log.d("Test", "Size1: " + accelExecute.size());
        while (accelExecute.size() > Markov.MAX_LENGTH_OF_WINDS * 2) {

            MEStatus mes = new MEStatus(Markov.MAX_LENGTH_OF_WINDS,
                    accelExecute, sensorFusion.getAzimuth());
            MEObservedData result = mk.getMEObservedData(mes);
            if (result.data == MEObservedData.ACCEL_UP) {

                this.posListData.add(getEsPostion(mes));
                Log.d("StartEnd", "add");
            }
            accelExecute.remove(0);
        }
        // Log.d("Test", "Size2: " + accelExecute.size());

        Log.d("StartEnd", "End" + accelExecute.size());
    }

    public Position getEsPostion(MEStatus mse) {

        Position p = this.getLastMovingData();
        float stepConstant = 0.4f * Constants.PIXEL_ON_METER;
        double azimuth = mse.azimuth;
        double rotationMap = 0;
        double rotation = azimuth + rotationMap;
        Position result = new Position((int) Math.round(p.x + stepConstant
                * Math.sin(rotation)), (int) Math.round(p.y + stepConstant
                * Math.cos(rotation)));
        return result;
    }

    public void invalidate() {
        super.invalidate();
    }

    class calculateFusedOrientationTask extends TimerTask {
        public void run() {
            if (MyView.this.accel.size() > Markov.MAX_LENGTH_OF_WINDS * 2)
                MyView.this.calculate();
        }
    }
}
