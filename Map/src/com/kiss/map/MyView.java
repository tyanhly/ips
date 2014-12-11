package com.kiss.map;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.kiss.config.Constants;

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

    float accel[] = new float[4];
    float magnet[] = new float[4];
    float gyro[] = new float[4];
    private float[] gyroMatrix = new float[9];
    private float[] gyroOrientation = new float[4];

    /**
     * We need to compute these infomration
     **/
    private float[] earthAccel = new float[4];
    /**
     * CODO
     **/

    private float[] accMagOrientation = new float[4];
    private float[] fusedOrientation = new float[4];
    private float[] rotationMatrix = new float[9];

    private long timestamp = 0;
    private boolean initState = true;

    private Timer fuseTimer = new Timer();

    DecimalFormat d = new DecimalFormat("#.##");

    private LimitedArray<Position> posListData;
    private ArrayList<Moving> movingListData;

    public MyView(Context context) {
        super(context);
        _viewInit();
        _sensorsInit();

    }

    private void _sensorsInit() {

        earthAccel[3] = 0; // [0,0,0,0]

        // rotation = 0
        gyroOrientation[0] = 0.0f;
        gyroOrientation[1] = 0.0f;
        gyroOrientation[2] = 0.0f;

        // initialise gyroMatrix with identity matrix
        // rotation = 0
        gyroMatrix[0] = 1.0f;
        gyroMatrix[1] = 0.0f;
        gyroMatrix[2] = 0.0f;
        gyroMatrix[3] = 0.0f;
        gyroMatrix[4] = 1.0f;
        gyroMatrix[5] = 0.0f;
        gyroMatrix[6] = 0.0f;
        gyroMatrix[7] = 0.0f;
        gyroMatrix[8] = 1.0f;
        fuseTimer.scheduleAtFixedRate(new calculateFusedOrientationTask(),
                2000, Constants.THREAD_UPDATE_VECTOR_TIME_CONSTANT);
        // GUI stuff
        d.setRoundingMode(RoundingMode.HALF_UP);
        d.setMaximumFractionDigits(3);
        d.setMinimumFractionDigits(3);
    }

    private void _viewInit() {
        posListData = new LimitedArray<Position>(Constants.POS_SAMPLE_TOTAL);
        movingListData = new LimitedArray<Moving>(Constants.MOVING_SAMPLE_TOTAL);
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
        
        canvas.drawText(String.format("Accel: %.3f;%.3f;%.3f", this.accel[0],
                this.accel[1], this.accel[2]), 30, screenHeight - 150, txtPaint);
        
        canvas.drawText(String.format("Gyros: %.3f;%.3f;%.3f", this.gyro[0],
                this.gyro[1], this.gyro[2]), 30, screenHeight - 110, txtPaint);
        
        canvas.drawText(String.format("accMagOrientation: %.3f;%.3f;%.3f",
                this.accMagOrientation[0], this.accMagOrientation[1],
                this.accMagOrientation[2]), 30, screenHeight - 70, txtPaint);
        
        canvas.drawText(String.format("earthAccel: %.3f;%.3f;%.3f",
                this.earthAccel[0], this.earthAccel[1], this.earthAccel[2]),
                30, screenHeight - 30, txtPaint);

        canvas.drawText(String.format("Moving: %d, Pos: %d",movingListData.size(),posListData.size()),
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

    // public void invalidate() {
    // invalidate();
    // }

    public Position getLastMovingData() {
        if (posListData.isEmpty()) {
            return new Position(0, 0,  System.nanoTime());
        }
        return posListData.get(posListData.size() - 1);
    }

    public void setMag(float[] mag) {
        System.arraycopy(mag, 0, this.magnet, 0, 3);
    }

    public void setGyro(float[] gyro) {
        System.arraycopy(gyro, 0, this.gyro, 0, 3);
        gyroFunction(gyro);
    }

    public void setAccel(float[] accel) {

        System.arraycopy(accel, 0, this.accel, 0, 3);
        calculateAccMagOrientation();
    }

    // calculates orientation angles from accelerometer and magnetometer output
    public void calculateAccMagOrientation() {

        if (magnet == null)
            return;

        if (SensorManager
                .getRotationMatrix(rotationMatrix, null, accel, magnet)) {
            SensorManager.getOrientation(rotationMatrix, accMagOrientation);
        }
    }

    public void gyroFunction(float[] gyro) {

        if (accMagOrientation == null)
            return;

        if (initState) {
            float[] initMatrix = new float[9];
            initMatrix = get3x3RotationMatrixFromEuler(accMagOrientation);
            float[] test = new float[3];
            SensorManager.getOrientation(initMatrix, test);

//            Log.d("MatrixTest", String.format(
//                    "(%.3f,%.3f,%.3f) == (%.3f,%.3f,%.3f)",
//                    accMagOrientation[0], accMagOrientation[1],
//                    accMagOrientation[2], test[0], test[1], test[2]));

            gyroMatrix = matrixMultiplication3x3(gyroMatrix, initMatrix);
            initState = false;
        }

        float[] deltaVector = new float[4];
        if (timestamp != 0) {
            final float dT = (System.nanoTime() - timestamp) * Constants.NS2S;
            getRotationVectorFromGyro(this.gyro, deltaVector, dT / 2.0f);
        }

        timestamp = System.nanoTime();

        // convert rotation vector into rotation matrix
        float[] deltaMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(deltaMatrix, deltaVector);

        // apply the new rotation interval on the gyroscope based rotation
        // matrix
        gyroMatrix = matrixMultiplication3x3(gyroMatrix, deltaMatrix);

        SensorManager.getOrientation(gyroMatrix, gyroOrientation);
        Log.d("TungTest", "movingData:" + posListData.size());
       

    }

    private float[] get3x3RotationMatrixFromEuler(float[] o) {
        float[] xM = new float[9];
        float[] yM = new float[9];
        float[] zM = new float[9];

        float sinX = (float) Math.sin(o[1]);
        float cosX = (float) Math.cos(o[1]);
        float sinY = (float) Math.sin(o[2]);
        float cosY = (float) Math.cos(o[2]);
        float sinZ = (float) Math.sin(o[0]);
        float cosZ = (float) Math.cos(o[0]);

        // rotation about x-axis (pitch)
        xM[0] = 1.0f;
        xM[1] = 0.0f;
        xM[2] = 0.0f;
        xM[3] = 0.0f;
        xM[4] = cosX;
        xM[5] = sinX;
        xM[6] = 0.0f;
        xM[7] = -sinX;
        xM[8] = cosX;

        // rotation about y-axis (roll)
        yM[0] = cosY;
        yM[1] = 0.0f;
        yM[2] = sinY;
        yM[3] = 0.0f;
        yM[4] = 1.0f;
        yM[5] = 0.0f;
        yM[6] = -sinY;
        yM[7] = 0.0f;
        yM[8] = cosY;

        // rotation about z-axis (azimuth)
        zM[0] = cosZ;
        zM[1] = sinZ;
        zM[2] = 0.0f;
        zM[3] = -sinZ;
        zM[4] = cosZ;
        zM[5] = 0.0f;
        zM[6] = 0.0f;
        zM[7] = 0.0f;
        zM[8] = 1.0f;

        // rotation order is y, x, z (roll, pitch, azimuth)
        float[] resultMatrix = matrixMultiplication3x3(xM, yM);
        resultMatrix = matrixMultiplication3x3(zM, resultMatrix);
        return resultMatrix;
    }

    private void getRotationVectorFromGyro(float[] gyroValues,
            float[] deltaRotationVector, float timeFactor) {
        float[] normValues = new float[3];
        float omegaMagnitude = (float) Math
                .sqrt(gyroValues[0] * gyroValues[0] + gyroValues[1]
                        * gyroValues[1] + gyroValues[2] * gyroValues[2]);

        if (omegaMagnitude > Constants.EPSILON) {
            normValues[0] = gyroValues[0] / omegaMagnitude;
            normValues[1] = gyroValues[1] / omegaMagnitude;
            normValues[2] = gyroValues[2] / omegaMagnitude;
        }

        float thetaOverTwo = omegaMagnitude * timeFactor;
        float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
        float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
        deltaRotationVector[0] = sinThetaOverTwo * normValues[0];
        deltaRotationVector[1] = sinThetaOverTwo * normValues[1];
        deltaRotationVector[2] = sinThetaOverTwo * normValues[2];
        deltaRotationVector[3] = cosThetaOverTwo;
    }

    private float[] matrixMultiplication3x3(float[] A, float[] B) {
        float[] result = new float[9];

        result[0] = A[0] * B[0] + A[1] * B[3] + A[2] * B[6];
        result[1] = A[0] * B[1] + A[1] * B[4] + A[2] * B[7];
        result[2] = A[0] * B[2] + A[1] * B[5] + A[2] * B[8];

        result[3] = A[3] * B[0] + A[4] * B[3] + A[5] * B[6];
        result[4] = A[3] * B[1] + A[4] * B[4] + A[5] * B[7];
        result[5] = A[3] * B[2] + A[4] * B[5] + A[5] * B[8];

        result[6] = A[6] * B[0] + A[7] * B[3] + A[8] * B[6];
        result[7] = A[6] * B[1] + A[7] * B[4] + A[8] * B[7];
        result[8] = A[6] * B[2] + A[7] * B[5] + A[8] * B[8];

        return result;
    }

    class calculateFusedOrientationTask extends TimerTask {
        public void run() {
            final float oneMinusCoeff = 1.0f - Constants.FILTER_COEFFICIENT;
            final double halfPi = -0.5 * Math.PI;
            final double twoPi = 2 * Math.PI;

            for (int i = 0; i < 3; i++) {
                if (gyroOrientation[i] < halfPi && accMagOrientation[i] > 0.0) {
                    fusedOrientation[i] = (float) (Constants.FILTER_COEFFICIENT
                            * (gyroOrientation[i] + twoPi) + oneMinusCoeff
                            * accMagOrientation[i]);
                    fusedOrientation[i] -= (fusedOrientation[i] > Math.PI) ? twoPi
                            : 0;
                } else if (accMagOrientation[i] < halfPi
                        && gyroOrientation[i] > 0.0) {
                    fusedOrientation[i] = (float) (Constants.FILTER_COEFFICIENT
                            * gyroOrientation[i] + oneMinusCoeff
                            * (accMagOrientation[i] + twoPi));
                    fusedOrientation[i] -= (fusedOrientation[i] > Math.PI) ? twoPi
                            : 0;
                } else {
                    fusedOrientation[i] = Constants.FILTER_COEFFICIENT
                            * gyroOrientation[i] + oneMinusCoeff
                            * accMagOrientation[i];
                }
            }

            gyroMatrix = get3x3RotationMatrixFromEuler(fusedOrientation); // 3x3
            System.arraycopy(fusedOrientation, 0, gyroOrientation, 0, 3);

            // fusedOrientation euler angle representation

            /**
             * CODO
             **/
            float[] rotationMatrixInverse = new float[16]; // 4x4
            float[] rotationMatrix = new float[16]; // 4x4
            //SensorManager.getRotationMatrixFromVector(rotationMatrix,
            //        fusedOrientation);
            
            rotationMatrix[0] = gyroMatrix[0];
            rotationMatrix[1] = gyroMatrix[1];
            rotationMatrix[2] = gyroMatrix[2];
            rotationMatrix[3] = 0;
            rotationMatrix[4] = gyroMatrix[3];
            rotationMatrix[5] = gyroMatrix[4];
            rotationMatrix[6] = gyroMatrix[5];
            rotationMatrix[7] = 0;
            rotationMatrix[8] = gyroMatrix[6];
            rotationMatrix[9] = gyroMatrix[7];
            rotationMatrix[10] = gyroMatrix[8];
            rotationMatrix[11] = 0;
            rotationMatrix[12] = 0;
            rotationMatrix[13] = 0;
            rotationMatrix[14] = 0;
            rotationMatrix[15] = 1;

            // android.opengl like 4x4
            android.opengl.Matrix.invertM(rotationMatrixInverse, 0,
                    rotationMatrix, 0);
            accel[3] = 0;
            android.opengl.Matrix.multiplyMV(earthAccel, 0,
                    rotationMatrixInverse, 0, accel, 0);
            
            movingListData.add(new Moving(earthAccel, System.nanoTime()));
            
            Log.d("EarthAccel",String.format("%.3f, %.3f, %.3f", earthAccel[0], earthAccel[1], earthAccel[2]));
            
            // Add next position
            if(movingListData.size()==Constants.MOVING_SAMPLE_TOTAL){

                Position pos = MyView.this.getLastMovingData();
                pos.setMovings(MyView.this.movingListData);
                MyView.this.addPos(pos.getNextPosition( System.nanoTime()));
                MyView.this.movingListData.clear();
            }
        }
    }
}
