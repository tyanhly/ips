package com.kiss.model.copy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.util.Log;
import android.widget.Toast;

import com.kiss.core.LimitedArray;

public class Markov {
    public static int maxLengthOfWinds = MTrainedData.maxLengthOfWinds;
    public static int maxLengthOfChain = 5;
    public static float kmeanADownEpsilon = 40f;
    public static float kmeanAUpEpsilon = 20f;
    public static float kmeanAOtherEpsilon = 40f;
    public static float kmeanBeforAfterMaxEpsilon = 5f;
    public static float kmeanBeforAfterMidEpsilon = 3f;
    public static float kmeanBeforAfterMinEpsilon = 5f;
    public float kmeanGroupAUp = 108f;
    public float kmeanGroupADown = 88f;
    public float kmeanGroupAOther = 98f;
    public float kmeanGroupBeforeUp = 9.0f;
    public float kmeanGroupBeforeDown = 4.0f;
    public float kmeanGroupBeforeOther = 0.5f;
    public float kmeanGroupAfterUp = 4.0f;
    public float kmeanGroupAfterDown = 9.0f;
    public float kmeanGroupAfterOther = 0.5f;

    public Set<MEStatus> statusSet = new HashSet<MEStatus>();
    public Set<MEObservedData> observedDataSet = new HashSet<MEObservedData>();
    public Map<MEStatus, Float> probabilityStatus = new HashMap<MEStatus, Float>();
    public Map<MEStatus, Map<MEStatus, Float>> probabilityTransition = new HashMap<MEStatus, Map<MEStatus, Float>>();
    public Map<MEStatus, Map<MEObservedData, Float>> probabilityEmission = new HashMap<MEStatus, Map<MEObservedData, Float>>();
    public LimitedArray<MEStatus> markovChain = new LimitedArray<MEStatus>(
            maxLengthOfChain);

    private void _init() {
        // SampleData sd = new SampleData();
        // this.statusSet = sd.statusSet;
        // this.observedDataSet = sd.observedDataSet;
        // this.statusProbability = sd.statusProbability;
        // this.transitionProbability = sd.transitionProbability;
        // this.emissionProbability = sd.emissionProbability;

    }

    public Markov() {
        _init();
    }

    public MEObservedData getMEObservedData(MEStatus mes) {
        Float tmpCmp = null;
        MEObservedData result = null;
        try {
            Map<MEObservedData, Float> tmpValue;
            if (this.probabilityEmission.containsKey(mes)) {
                // Log.i("Emission", "Emission:" + mes.toString());
                tmpValue = this.probabilityEmission.get(mes);
            } else {

                // Log.i("Emission", "Training:" + mes.toString());
                tmpValue = this.getRawEstimateResult(mes);
            }

            if (tmpValue == null) {
                return null;
            }
            MEObservedData up = new MEObservedData(MEObservedData.ACCEL_UP);
            MEObservedData down = new MEObservedData(MEObservedData.ACCEL_DOWN);
            MEObservedData other = new MEObservedData(
                    MEObservedData.ACCEL_OTHER);

            tmpCmp = tmpValue.get(other);

            result = new MEObservedData(MEObservedData.ACCEL_OTHER);
            if (tmpCmp.floatValue() < tmpValue.get(up).floatValue()) {
                tmpCmp = tmpValue.get(up);
                result = new MEObservedData(MEObservedData.ACCEL_UP);
            } else if (tmpCmp.floatValue() < tmpValue.get(down).floatValue()) {
                tmpCmp = tmpValue.get(down);
                result = new MEObservedData(MEObservedData.ACCEL_DOWN);
            }

            markovChain.add(mes);
            Log.d("result", result.toString());
        } catch (MarkovException e) {
            // TODO Auto-generated catch block
            Log.d("TungLog", e.getMessage());
            e.printStackTrace();
        }
        return result;

    }

    public Map<MEObservedData, Float> getMarkovEstimateResult(MEStatus mes) {
        Map<MEObservedData, Float> result = getRawEstimateResult(mes);
        // if(this.markovChain.size()>0){
        // MEStatus last = this.markovChain.get(this.markovChain.size());
        // if(this.probabilityTransition.containsKey(last)){
        // float probabilityTranstion =
        // this.probabilityTransition.get(last).get(mes);
        // }
        // }

        return result;
    }

    public Map<MEObservedData, Float> getRawEstimateResult(MEStatus mes) {
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
        result.put(
                up,
                _fUp(mes, this.kmeanGroupAUp, this.kmeanGroupBeforeUp,
                        this.kmeanGroupAfterUp));
        result.put(
                down,
                _fDown(mes, this.kmeanGroupADown, this.kmeanGroupBeforeDown,
                        this.kmeanGroupAfterDown));
        result.put(
                other,
                _fOther(mes, this.kmeanGroupAOther, this.kmeanGroupBeforeOther,
                        this.kmeanGroupAfterOther));

        return result;
    }

    private float _fUp(MEStatus mes, float groupA, float groupBefore,
            float groupAfter) {
        // @todo a
        float before = Math.abs(mes.before - groupBefore);

        if (before > this.kmeanBeforAfterMaxEpsilon) {
            if (mes.before > groupBefore) {
                before = 1;
            } else {
                before = 0;
            }

        } else {
            before = before / this.kmeanBeforAfterMaxEpsilon;
        }
        // @todo a
        float after = Math.abs(mes.after - groupAfter);
        if (after > this.kmeanBeforAfterMidEpsilon) {
            after = 0;
        } else {
            after = after / this.kmeanBeforAfterMidEpsilon;
        }

        // a
        float a = Math.abs(mes.a - groupA);

        if (a > this.kmeanAUpEpsilon) {
            if (mes.a > groupA) {
                a = 1;
            } else {
                a = 0;
            }
        } else {
            a = a / this.kmeanAUpEpsilon;
        }

        float tmp = (a + before + after) / 3;
        // return a;
        return tmp;
    }

    private float _fDown(MEStatus mes, float groupA, float groupBefore,
            float groupAfter) {
        // @todo a
        float before = Math.abs(mes.before - groupBefore);

        if (before > this.kmeanBeforAfterMidEpsilon) {
            if (mes.before > groupBefore) {
                before = 1;
            } else {
                before = 0;
            }
        } else {
            before = before / this.kmeanBeforAfterMidEpsilon;
        }
        // @todo a
        float after = Math.abs(mes.after - groupAfter);
        if (after > this.kmeanBeforAfterMaxEpsilon) {
            after = 0;
        } else {
            after = after / this.kmeanBeforAfterMaxEpsilon;
        }

        // a
        float a = Math.abs(mes.a - groupA);

        if (a > this.kmeanADownEpsilon) {
            if (mes.a < groupA) {
                a = 1;
            } else {
                a = 0;
            }
        } else {
            a = a / this.kmeanADownEpsilon;
        }

        float tmp = (a + before + after) / 3;
//       return a;
        return tmp;
    }

    private float _fOther(MEStatus mes, float groupA, float groupBefore,
            float groupAfter) {
        // @todo a
        float before = Math.abs(mes.before - groupBefore);

        if (before > this.kmeanBeforAfterMinEpsilon) {
            before = 0;

        } else {
            before = before / this.kmeanBeforAfterMinEpsilon;
        }
        // @todo a
        float after = Math.abs(mes.after - groupAfter);
        if (after > this.kmeanBeforAfterMinEpsilon) {
            after = 0;
        } else {
            after = after / this.kmeanBeforAfterMinEpsilon;
        }

        // a
        float a = Math.abs(mes.a - groupA);

        if (a > this.kmeanAOtherEpsilon) {
            a = 0;
        } else {
            a = a / this.kmeanAOtherEpsilon;
        }

         float tmp = (a + before + after) / 3;
//        return a;
         return tmp;
    }

    public void setStatusSet(List<MEStatus> simplifyDataList) {

        Log.d("StartEnd", "start setStatusSet");
        for (int i = 0; i < simplifyDataList.size(); i++) {
            statusSet.add(simplifyDataList.get(i));
        }
        Log.d("StartEnd", "stop setStatusSet");
    }

    public void setObservedDataSet() {
        Log.d("StartEnd", "start setObservedDataSet");
        try {

            observedDataSet.add(new MEObservedData(MEObservedData.ACCEL_DOWN));
            observedDataSet.add(new MEObservedData(MEObservedData.ACCEL_UP));
            observedDataSet.add(new MEObservedData(MEObservedData.ACCEL_OTHER));
        } catch (Exception e) {
            // TODO: handle exception
        }
        Log.d("StartEnd", "stop setObservedDataSet");
    }

    public void setStatusProbability(List<MEStatus> simplifyDataList) {

        Log.d("StartEnd", "start setStatusProbability");
        for (MEStatus es : statusSet) {
            this.probabilityStatus.put(es, 0.0f);
        }
        MEStatus tmpStatus = simplifyDataList.get(0);
        for (int i = 1; i < simplifyDataList.size() - 1; i++) {
            MEStatus tmpStatusI = simplifyDataList.get(i);
            if (tmpStatus.equals(tmpStatusI)) {
                this.probabilityStatus.put(tmpStatus, this.probabilityStatus
                        .get(tmpStatus).floatValue() + 1);

            }
            tmpStatus = simplifyDataList.get(i);
        }
        for (MEStatus es : statusSet) {
            this.probabilityStatus.put(es,
                    this.probabilityStatus.get(tmpStatus)
                            / this.probabilityStatus.size());
        }
        Log.d("StartEnd", "stop setStatusProbability");
    }

    public void setTransitionProbability(List<MEStatus> simplifyDataList) {
        Log.d("StartEnd", "start setTransitionProbability");
        Map<MEStatus, Float> initValue = new HashMap<MEStatus, Float>();
        for (MEStatus es : statusSet) {
            this.probabilityTransition.put(es, initValue);
        }

        MEStatus currentStatus = simplifyDataList.get(0);
        for (int i = 1; i < simplifyDataList.size() - 1; i++) {
            MEStatus tmpStatus = simplifyDataList.get(i);
            Map<MEStatus, Float> currentValue = this.probabilityTransition
                    .get(currentStatus);
            if (currentValue.containsKey(tmpStatus)) {
                currentValue.put(tmpStatus, currentValue.get(tmpStatus) + 1.0f);
            } else {
                currentValue.put(tmpStatus, 1.0f);
            }
            this.probabilityTransition.put(currentStatus, currentValue);
        }
        for (Map.Entry<MEStatus, Map<MEStatus, Float>> entry : this.probabilityTransition
                .entrySet()) {
            Map<MEStatus, Float> currentValue = entry.getValue();
            int numberOfElement = currentValue.size();
            for (Map.Entry<MEStatus, Float> es : currentValue.entrySet()) {
                es.setValue(es.getValue().floatValue() / numberOfElement);
            }
            entry.setValue(currentValue);
        }
        Log.d("StartEnd", "stop setTransitionProbability");
    }

    public void setEmissionProbability(List<MEStatus> simplifyDataList) {
        Log.d("StartEnd", "start setEmissionProbability");

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
            this.probabilityEmission.put(es, initValue);
        }

        for (int i = 0; i < simplifyDataList.size() - 1; i++) {
            MEStatus currentStatus = simplifyDataList.get(i);
            this.probabilityEmission.put(currentStatus,
                    getRawEstimateResult(currentStatus));
        }

        Log.d("StartEnd", "stop setEmissionProbability");
    }

    public void setDataFromKmeanEstimation(List<MEStatus> inputDataList) {

        Log.d("StartEnd", "start setDataFromKmeanEstimation");
        MTrainedData.estimationClusterByKMean(inputDataList, this);
        Log.d("StartEnd", "stop setDataFromKmeanEstimation");
    }

}
