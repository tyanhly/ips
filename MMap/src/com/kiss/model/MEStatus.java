package com.kiss.model;

import java.util.List;

public class MEStatus extends com.kiss.core.Object {
    public int a;
    public int less = 0;
    public int greater = 0;
    public double azimuth = 0;
    public long time;

    public static final int EPSILON_less = 2;
    public static final int EPSILON_greater = 2;

    public MEStatus(int a, long time, double azimuth) {
        this.a = a;
        this.time = time;
        this.azimuth = azimuth;
    }

    @Override
    public boolean equals(Object o) {
        MEStatus obj = (MEStatus) o;
        if (this.a == obj.a && less == obj.less && greater == obj.greater) {

            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return a * 1000000 + less* 1000 + greater;
    }

    public MEStatus(int currentId, List<MEStatus> acclList) {
        int less = 0, greater=0;

        MEStatus mes = acclList.get(currentId);
        int begin = (currentId > MTrainedData.maxLengthOfWinds) ? (currentId - MTrainedData.maxLengthOfWinds)
                : 0;
        int end = (currentId < acclList.size() - MTrainedData.maxLengthOfWinds) ? (currentId + MTrainedData.maxLengthOfWinds)
                : acclList.size();
        for (int j = begin; j < end; j++) {
            if (mes.a > acclList.get(j).a) {
                less++;
            }
            if (mes.a < acclList.get(j).a) {
                greater++;
            }
        }
        
        this.a = acclList.get(currentId).a;
        this.less = less;
        this.greater = greater;
        this.azimuth = acclList.get(currentId).azimuth * -1 + 90;
    }
}
