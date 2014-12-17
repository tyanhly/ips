package com.kiss.markov;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.util.Log;

public class Markov {
    public static int MAX_LENGTH_OF_WINDS = MTrainedData.MAX_LENGTH_OF_WINDS;
    public Set<MEStatus> statusSet = new HashSet<MEStatus>();
    public Set<MEObservedData> observedDataSet = new HashSet<MEObservedData>();
    public Map<MEStatus, Float> statusProbability = new HashMap<MEStatus, Float>();
    public Map<MEStatus, Map<MEStatus, Float>> transitionProbability = new HashMap<MEStatus, Map<MEStatus, Float>>();
    public Map<MEStatus, Map<MEObservedData, Float>> emissionProbability = new HashMap<MEStatus, Map<MEObservedData, Float>>();
    private MTrainedData mtrain;

    private void _init() throws MarkovException {
        SampleData sd = new SampleData();
        this.statusSet = sd.statusSet;
        this.observedDataSet = sd.observedDataSet;
        this.statusProbability = sd.statusProbability;
        this.transitionProbability = sd.transitionProbability;
        this.emissionProbability = sd.emissionProbability;
        mtrain = new MTrainedData();
    }

    public Markov() throws MarkovException {
        _init();
    }

    public MEObservedData getMEObservedData(MEStatus mes) {
        Float tmpCmp = null;
        MEObservedData result = null;
        try {
            Map<MEObservedData, Float> tmpValue;
            if (this.emissionProbability.containsKey(mes)) {
                tmpValue = this.emissionProbability.get(mes);
            } else {

                tmpValue = mtrain.getEstimateResult(mes);
            }

            MEObservedData up = new MEObservedData(MEObservedData.ACCEL_UP);
            MEObservedData down = new MEObservedData(MEObservedData.ACCEL_DOWN);
            MEObservedData other = new MEObservedData(
                    MEObservedData.ACCEL_OTHER);

            Log.d("OutputData",
                    "data:" + tmpValue.get(up) + ";" + tmpValue.get(other));
            tmpCmp = tmpValue.get(other);

            result = new MEObservedData(MEObservedData.ACCEL_OTHER);
            if (tmpCmp.floatValue() < tmpValue.get(up).floatValue()) {
                tmpCmp = tmpValue.get(up);
                result = new MEObservedData(MEObservedData.ACCEL_UP);
            } else if (tmpCmp.floatValue() < tmpValue.get(down).floatValue()) {
                tmpCmp = tmpValue.get(down);
                result = new MEObservedData(MEObservedData.ACCEL_DOWN);
            }
        } catch (MarkovException e) {
            // TODO Auto-generated catch block
            Log.d("TungLog", e.getMessage());
            e.printStackTrace();
        }
        return result;

    }
}
