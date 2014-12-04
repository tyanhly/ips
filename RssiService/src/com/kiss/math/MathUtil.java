package com.kiss.math;

import com.kiss.ips.entity.Position;

public class MathUtil {

    public static MathUtil.F1Result solveF1(CircleEquation ce, SLineEquation sl)
            throws Exception {
        /**
         * F1 is: (x-x0)^2 + (y-y0)^2 - R^2 = 0
         *         ax + by +c = 0
         * 
         */
        MathUtil.F1Result f1Result = new MathUtil.F1Result();
        double x0 = ce.getX0();
        double y0 = ce.getY0();
        double R = ce.getR();
        double a = sl.getA();
        double b = sl.getB();
        double c = sl.getC();

        if (a == 0) {
            double y = -c / b;
            QuadraticEquation qe = new QuadraticEquation(1, (double) (-2 * x0),
                    (double) (x0 * x0 + c * c / (b * b) - R * R));
            double xv1 = qe.getV1();
            double xv2 = qe.getV2();
            f1Result.p1 = new Position((int) xv1, (int) y);
            f1Result.p2 = new Position((int) xv2, (int) y);
            return f1Result;
        }
        double A = 1 + b * b / a / a;
        double B = 2 * b * c / a / a + 2 * b * x0 / a - 2 * y0;
        double C = c * c / a / a + 2 * c * x0 / a + x0 * x0 + y0 * y0 - R * R;
        QuadraticEquation qe = new QuadraticEquation(A, B, C);
        f1Result.p1.y = (int) qe.getV1();
        f1Result.p2.y = (int) qe.getV2();
        f1Result.p1.x = (int) sl.getX(f1Result.p1.y);
        f1Result.p2.x = (int) sl.getX(f1Result.p2.y);
        return f1Result;
    }

    public static SLineEquation getSquareSLineOnC1C2(CircleEquation c1, CircleEquation c2) throws Exception{

        double a = 2 * (c2.getX0() - c1.getX0());
        double b = 2 * (c2.getY0() - c1.getY0());
        double c = c1.getX0()*c1.getX0() + c1.getY0()*c1.getY0() - c1.getR()*c1.getR() 
                 - (c2.getX0()*c2.getX0() + c2.getY0()*c2.getY0() - c2.getR()*c2.getR()) ;
        SLineEquation sl = new SLineEquation(a, b, c);
        return sl;
    }
    
    public static class F1Result {
        public Position p1;
        public Position p2;

        public F1Result() {
        }
        
        public Position getEstimateResult(){
            long x = (p1.x + p2.x)/2;
            long y = (p1.y + p2.y)/2;
            
            return new Position((x<0)?0:x, (y<0)?0:y);
        }
    }

}