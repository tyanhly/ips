package com.kiss.model.copy;

import java.util.List;

public class MEStatus extends com.kiss.core.Object {
    public int a;
    public int before = 0;
    public int after = 0;
    public double azimuth = 0;
    public long time;

    public static final int EPSILON_BEFORE = 2;
    public static final int EPSILON_AFTER = 2;

    public MEStatus(int id, double azimuth, long time) {
        this.a = id;
        this.azimuth = azimuth;
        this.time = time;
    }

    public MEStatus(int id, int before, int after, double azimuth) {
        this.a = id;
        this.before = before;
        this.after = after;
        this.azimuth = azimuth;
    }

    public MEStatus(int currentId, int[] arrIntData, double azimuth) {
        int length = arrIntData.length;
        int beforeTmp = 0;
        int afterTmp = 0;
        for (int pre = currentId - 1, next = currentId + 1, j = 0; j < Markov.maxLengthOfWinds
                && pre > -1 && next < length; pre--, next++, j++) {
            // before less than current
            if (arrIntData[pre] < arrIntData[currentId] && beforeTmp >= 0) {
                beforeTmp++;
            }
            // before greater than current
            if (arrIntData[pre] > arrIntData[currentId] && beforeTmp <= 0) {
                beforeTmp--;
            }
            // after less than current
            if (arrIntData[next] < arrIntData[currentId] && afterTmp >= 0) {
                afterTmp++;
            }
            // after greater than current
            if (arrIntData[pre] < arrIntData[currentId] && afterTmp <= 0) {
                afterTmp--;
            }
        }
        this.a = arrIntData[currentId];
        this.before = beforeTmp;
        this.after = afterTmp;
        this.azimuth = azimuth;
    }

    public MEStatus(int currentId, List<MEStatus> acclList) {
        int length = acclList.size();
        int beforeTmp = 0;
        int afterTmp = 0;

        // Log.d("OutputData", String.format(
        // "-5:%d;-4:%d;-3:%d;-2:%d;-1:%d;0:%d;1:%d;2:%d;3:%d;4:%d;5:%d;",
        // acclList.get(Markov.MAX_LENGTH_OF_WINDS-5),
        // acclList.get(Markov.MAX_LENGTH_OF_WINDS-4),
        // acclList.get(Markov.MAX_LENGTH_OF_WINDS-3),
        // acclList.get(Markov.MAX_LENGTH_OF_WINDS-2),
        // acclList.get(Markov.MAX_LENGTH_OF_WINDS-1),
        // acclList.get(Markov.MAX_LENGTH_OF_WINDS),
        // acclList.get(Markov.MAX_LENGTH_OF_WINDS+1),
        // acclList.get(Markov.MAX_LENGTH_OF_WINDS+2),
        // acclList.get(Markov.MAX_LENGTH_OF_WINDS+3),
        // acclList.get(Markov.MAX_LENGTH_OF_WINDS+4),
        // acclList.get(Markov.MAX_LENGTH_OF_WINDS+5)));

        boolean case1 = true, case2 = true, case3 = true, case4 = true;
        for (int pre = currentId - 1, next = currentId + 1, j = 0; j < Markov.maxLengthOfWinds
                && pre > -1 && next < length; pre--, next++, j++) {

            // before less than current
            if (acclList.get(pre).a < acclList.get(currentId).a
                    && beforeTmp >= 0 && case1) {
                beforeTmp++;
            } else {
                case1 = false;
            }
            // before greater than current
            if (acclList.get(pre).a > acclList.get(currentId).a
                    && beforeTmp <= 0 && case2) {
                beforeTmp--;
            } else {
                case2 = false;
            }
            // after less than current
            if (acclList.get(next).a < acclList.get(currentId).a
                    && afterTmp >= 0 && case3) {
                afterTmp--;
            } else {
                case3 = false;
            }
            // after greater than current
            if (acclList.get(pre).a < acclList.get(currentId).a
                    && afterTmp <= 0 && case4) {
                afterTmp++;
            } else {
                case4 = false;
            }
        }
        this.a = acclList.get(currentId).a;
        this.before = beforeTmp;
        this.after = afterTmp;
        this.azimuth = acclList.get(currentId).azimuth * -1 + 90;
    }

    @Override
    public boolean equals(Object o) {
        MEStatus obj = (MEStatus) o;
        if (this.a == obj.a
                && ((before - obj.before) * (before - obj.before) < EPSILON_BEFORE)
                && ((this.after - obj.after) * (this.after - obj.after) < EPSILON_AFTER)) {
            
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return a * 1000 + before * 10 + after;
    }
}
