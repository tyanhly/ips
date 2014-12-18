package com.kiss.markov;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.util.Log;

import com.kiss.core.LimitedArray;

public class Markov {
    public static int maxLengthOfWinds = MTrainedData.maxLengthOfWinds;
    public static int maxLengthOfChain = 100;
    public Set<MEStatus> statusSet = new HashSet<MEStatus>();
    public Set<MEObservedData> observedDataSet = new HashSet<MEObservedData>();
    public Map<MEStatus, Float> statusProbability = new HashMap<MEStatus, Float>();
    public Map<MEStatus, Map<MEStatus, Float>> transitionProbability = new HashMap<MEStatus, Map<MEStatus, Float>>();
    public Map<MEStatus, Map<MEObservedData, Float>> emissionProbability = new HashMap<MEStatus, Map<MEObservedData, Float>>();
    public LimitedArray<MEStatus> markovChain = new LimitedArray<MEStatus>(
            maxLengthOfChain);
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

    private void _updateMarkov() {
        // if(markovChain.size()>MAX_LENGTH_OF_CHAIN-2)
        //
        // for(MEStatus mes :markovChain){
        // statusSet.add(mes);
        // _updateStatusProbability(mes);
        // _updateTransactionProbability(mes);
        // _updateEmissionProbability(mes);
        // }
    }

    private void _updateEmissionProbability(MEStatus mes) {

    }

    private void _updateTransactionProbability(MEStatus mes) {

    }

    private void _updateStatusProbability(MEStatus mes) {
        int size = statusProbability.size();
        Float value = 0.0f;
        if (statusProbability.containsKey(mes)) {
            value = statusProbability.get(mes);
        }
        value = value + 1 / size;
        statusProbability.put(mes, value);
    }

    public MEObservedData getMEObservedData(MEStatus mes) {
        Float tmpCmp = null;
        MEObservedData result = null;
        try {
            markovChain.add(mes);
            Map<MEObservedData, Float> tmpValue;
            if (this.emissionProbability.containsKey(mes)) {
                // Log.i("Emission", "Emission:" + mes.toString());
                tmpValue = this.emissionProbability.get(mes);
            } else {

                // Log.i("Emission", "Training:" + mes.toString());
                tmpValue = this.getEstimateResult(mes);
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

    public Map<MEObservedData, Float> getEstimateResult(MEStatus mes) {
        Map<MEObservedData, Float> result = new HashMap<MEObservedData, Float>();
        MEObservedData up = null;
        MEObservedData down = null;
        MEObservedData other = null;
        try {
            up = new MEObservedData(MEObservedData.ACCEL_UP);
            down = new MEObservedData(MEObservedData.ACCEL_DOWN);
            other = new MEObservedData(MEObservedData.ACCEL_OTHER);
        } catch (MarkovException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        result.put(up, 0.0f);
        result.put(down, 0.0f);
        result.put(other, 1.0f);

        Log.d("OutputData", "id: " + mes.id + "after: " + mes.after
                + ", before: " + mes.before);
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
                    if (mes.before + mes.after > MTrainedData.maxLengthOfWinds - 2
                            && mes.after > 1 && mes.before > 1) {
                        Log.d("OutputData", "4");
                        rateUp = 1.0f;
                        rateOther = 0.0f;
                    } else {
                        rateUp = 0.0f;
                        rateOther = 1.0f;
                    }
                    result.put(up, rateUp);
                    result.put(other, rateOther);
                }
            }
            // it is bot
            else {
                // it is down
                if (mes.id < getMaxOfBottom()) {
                    float rateDown = (mes.before + mes.after)
                            / MTrainedData.maxLengthOfWinds / 2;
                    float rateOther = 1 - rateDown;
                    result.put(down, rateDown);
                    result.put(other, rateOther);
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

    public void setStatusSet(List<InputData> inputDataList) {

        for (int i = 0; i < inputDataList.size(); i++) {
            statusSet.add(new MEStatus(i, inputDataList));
        }
    }

    public void setObservedDataSet() {
        try {

            observedDataSet.add(new MEObservedData(MEObservedData.ACCEL_DOWN));
            observedDataSet.add(new MEObservedData(MEObservedData.ACCEL_UP));
            observedDataSet.add(new MEObservedData(MEObservedData.ACCEL_OTHER));
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void setStatusProbability(List<InputData> inputDataList) {
        for (MEStatus es : statusSet) {
            this.statusProbability.put(es, 0.0f);
        }
        MEStatus tmpStatus = new MEStatus(0, inputDataList);
        for (int i = 1; i < inputDataList.size() - 1; i++) {
            MEStatus tmpStatusI = new MEStatus(i, inputDataList);
            if (tmpStatus.equals(tmpStatusI)) {
                this.statusProbability.put(tmpStatus, this.statusProbability
                        .get(tmpStatus).floatValue() + 1);

            }
            tmpStatus = new MEStatus(i, inputDataList);
        }
        for (MEStatus es : statusSet) {
            this.statusProbability.put(es,
                    this.statusProbability.get(tmpStatus)
                            / this.statusProbability.size());
        }
    }

    public void setTransitionProbability(List<InputData> inputDataList) {

        Map<MEStatus, Float> initValue = new HashMap<MEStatus, Float>();
        for (MEStatus es : statusSet) {
            this.transitionProbability.put(es, initValue);
        }

        MEStatus currentStatus = new MEStatus(0, inputDataList);
        for (int i = 1; i < inputDataList.size() - 1; i++) {
            MEStatus tmpStatus = new MEStatus(i, inputDataList);
            Map<MEStatus, Float> currentValue = this.transitionProbability
                    .get(currentStatus);
            if (currentValue.containsKey(tmpStatus)) {
                currentValue.put(tmpStatus, currentValue.get(tmpStatus) + 1.0f);
            } else {
                currentValue.put(tmpStatus, 1.0f);
            }
            this.transitionProbability.put(currentStatus, currentValue);
        }
        for (Map.Entry<MEStatus, Map<MEStatus, Float>> entry : this.transitionProbability
                .entrySet()) {
            Map<MEStatus, Float> currentValue = entry.getValue();
            int numberOfElement = currentValue.size();
            for (Map.Entry<MEStatus, Float> es : currentValue.entrySet()) {
                es.setValue(es.getValue().floatValue() / numberOfElement);
            }
            entry.setValue(currentValue);
        }
    }

    public void setEmissionProbability(List<InputData> inputDataList) {

        Map<MEObservedData, Float> initValue = new HashMap<MEObservedData, Float>();
        try {
            initValue.put(new MEObservedData(MEObservedData.ACCEL_UP), 0.0f);
            initValue.put(new MEObservedData(MEObservedData.ACCEL_DOWN), 0.0f);
            initValue.put(new MEObservedData(MEObservedData.ACCEL_OTHER), 0.0f);
        } catch (MarkovException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (MEStatus es : statusSet) {
            this.emissionProbability.put(es, initValue);
        }

        for (int i = 0; i < inputDataList.size() - 1; i++) {
            MEStatus currentStatus = new MEStatus(i, inputDataList);
                this.emissionProbability.put(currentStatus,
                        getEstimateResult(currentStatus));
        }
    }

}
