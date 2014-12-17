package com.kiss.markov;

import java.util.List;

import android.util.Log;

public class MEStatus extends com.kiss.core.Object {
    public int id;
    public int before = 0;
    public int after = 0;
    public long time;

    public MEStatus(int id, int before, int after) {
        this.id = id;
        this.before = before;
        this.after = after;
    }

    public MEStatus(int currentId, int[] arrIntData) {
        int length = arrIntData.length;
        int beforeTmp = 0;
        int afterTmp = 0;
        for (int pre = currentId - 1, next = currentId + 1, j = 0; j < MTrainedData.MAX_LENGTH_OF_WINDS
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
        this.id = arrIntData[currentId];
        this.before = beforeTmp;
        this.after = afterTmp;
    }

    public MEStatus(int currentId, List<Integer> acclList) {
        int length = acclList.size();
        int beforeTmp = 0;
        int afterTmp = 0;

         Log.d("OutputData", String.format(
         "-5:%d;-4:%d;-3:%d;-2:%d;-1:%d;0:%d;1:%d;2:%d;3:%d;4:%d;5:%d;",
         acclList.get(Markov.MAX_LENGTH_OF_WINDS-5),
         acclList.get(Markov.MAX_LENGTH_OF_WINDS-4),
         acclList.get(Markov.MAX_LENGTH_OF_WINDS-3),
         acclList.get(Markov.MAX_LENGTH_OF_WINDS-2),
         acclList.get(Markov.MAX_LENGTH_OF_WINDS-1),
         acclList.get(Markov.MAX_LENGTH_OF_WINDS),
         acclList.get(Markov.MAX_LENGTH_OF_WINDS+1),
         acclList.get(Markov.MAX_LENGTH_OF_WINDS+2),
         acclList.get(Markov.MAX_LENGTH_OF_WINDS+3),
         acclList.get(Markov.MAX_LENGTH_OF_WINDS+4),
         acclList.get(Markov.MAX_LENGTH_OF_WINDS+5)));

        boolean case1 = true, case2 = true, case3 = true, case4 = true;
        for (int pre = currentId - 1, next = currentId + 1, j = 0; j < Markov.MAX_LENGTH_OF_WINDS
                && pre > -1 && next < length; pre--, next++, j++) {

            // before less than current
            if (acclList.get(pre) < acclList.get(currentId) && beforeTmp >= 0
                    && case1) {
                beforeTmp++;
            } else {
                case1 = false;
            }
            // before greater than current
            if (acclList.get(pre) > acclList.get(currentId) && beforeTmp <= 0
                    && case2) {
                beforeTmp--;
            } else {
                case2 = false;
            }
            // after less than current
            if (acclList.get(next) < acclList.get(currentId) && afterTmp >= 0
                    && case3) {
                afterTmp--;
            } else {
                case3 = false;
            }
            // after greater than current
            if (acclList.get(pre) < acclList.get(currentId) && afterTmp <= 0
                    && case4) {
                afterTmp++;
            } else {
                case4 = false;
            }
        }
        this.id = acclList.get(currentId);
        this.before = beforeTmp;
        this.after = afterTmp;
    }

    @Override
    public boolean equals(Object o) {
        MEStatus obj = (MEStatus) o;
        if (this.id == obj.id) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id * 1000 + before * 10 + after;
    }
}
