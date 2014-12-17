package com.kiss.markov;

import android.annotation.SuppressLint;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("UseValueOf")
public class MTrainedData {

    public static int MAX_LENGTH_OF_WINDS = 15;

    public Map<MEObservedData, Float> getEstimateResult(MEStatus mes)
            throws MarkovException {
        Map<MEObservedData, Float> result = new HashMap<MEObservedData, Float>();
        MEObservedData up = new MEObservedData(MEObservedData.ACCEL_UP);
        MEObservedData down = new MEObservedData(MEObservedData.ACCEL_DOWN);
        MEObservedData other = new MEObservedData(MEObservedData.ACCEL_OTHER);
        result.put(up, new Float(0.0f));
        result.put(down, new Float(0.0f));
        result.put(other, new Float(1.0f));

        Log.d("OutputData", "id: " + mes.id + "after: " + mes.after + ", before: " + mes.before);
        // Filter it not top or bottom
        if (mes.before * mes.after > 0) {
            Log.d("OutputData", "1");
            // it is top
            if (mes.before > 0) {
                Log.d("OutputData", "2");
                // it is up
                if (mes.id > getMinOfTop()) {
                    Log.d("OutputData", "3");
                    float rateUp = 0.0f, rateOther = 0.0f;
                    if (mes.before + mes.after > 15) {
                        Log.d("OutputData", "4");
                        rateUp = 1.0f;
                        rateOther =0.0f;
                    }else{
                        rateUp = 0.0f;
                        rateOther =1.0f;
                    }
                    result.put(up, new Float(rateUp));
                    result.put(other, new Float(rateOther));
                }
            }
            // it is bot
            else {
                // it is down
                if (mes.id < getMaxOfBottom()) {
                    float rateDown = (mes.before + mes.after)
                            / MAX_LENGTH_OF_WINDS / 2;
                    float rateOther = 1 - rateDown;
                    result.put(down, new Float(rateDown));
                    result.put(other, new Float(rateOther));
                }
            }
        }

        return result;
    }


    private int getMaxOfBottom() {
        return 90;
    }

    private int getMinOfTop() {
        return 105;
    }

}
