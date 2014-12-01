package com.kiss.math;

import com.kiss.ips.entity.Position;

public class MathUtil {

    public static MathUtil.F1Result solveF1(CircleEquation ce, SLineEquation sl)
            throws Exception {
        /**
         * F1 is: (x-x0)^2 + (y-y0)^2 - R^2 = 0 ax + by +c = 0
         * 
         */
        MathUtil.F1Result f1Result = new MathUtil.F1Result();
        float x0 = ce.getX0();
        float y0 = ce.getY0();
        float R = ce.getR();
        float a = sl.getA();
        float b = sl.getB();
        float c = sl.getC();

        if (a == 0) {
            float y = -c / b;
            QuadraticEquation qe = new QuadraticEquation(1, (float) (-2 * x0),
                    (float) (x0 * x0 + c * c / (b * b) - R * R));
            float xv1 = qe.getV1();
            float xv2 = qe.getV2();
            f1Result.p1.x = (int) xv1;
            f1Result.p2.x = (int) xv2;
            f1Result.p1.y = (int) y;
            f1Result.p2.y = (int) y;
            return f1Result;
        }
        float A = 1 + b * b / a / a;
        float B = 2 * b * c / a / a + 2 * b * x0 / a - 2 * y0;
        float C = c * c / a / a + 2 * c * x0 / a + x0 * x0 + y0 * y0 - R * R;
        QuadraticEquation qe = new QuadraticEquation(A, B, C);
        f1Result.p1.y = (int) qe.getV1();
        f1Result.p2.y = (int) qe.getV2();
        f1Result.p1.x = (int) sl.getX(f1Result.p1.y);
        f1Result.p2.x = (int) sl.getX(f1Result.p2.y);
        return f1Result;
    }

    public static class F1Result {
        public Position p1;
        public Position p2;

        public F1Result() {
        }
        
        public Position getCentral(){
            return new Position((int) (p1.x + p2.x)/2, (int) (p1.y + p2.y)/2);
        }
    }

}