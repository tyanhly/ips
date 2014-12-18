package com.kiss.markov;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.util.Log;

@SuppressLint("UseValueOf")
public class SampleData {
    public Set<MEStatus> statusSet = new HashSet<MEStatus>();
    public Set<MEObservedData> observedDataSet = new HashSet<MEObservedData>();
    public Map<MEStatus, Float> statusProbability = new HashMap<MEStatus, Float>();
    public Map<MEStatus, Map<MEStatus, Float>> transitionProbability = new HashMap<MEStatus, Map<MEStatus, Float>>();
    public Map<MEStatus, Map<MEObservedData, Float>> emissionProbability = new HashMap<MEStatus, Map<MEObservedData, Float>>();

    public SampleData() throws MarkovException {
        _init();
    }

    private void _init() throws MarkovException {
        int arrIntData[] = { 98, 98, 98, 97, 95, 94, 94, 94, 93, 93, 94, 95,
                95, 97, 99, 101, 103, 104, 103, 102, 102, 101, 105, 106, 107,
                107, 105, 102, 99, 98, 100, 103, 103, 98, 95, 95, 94, 93, 93,
                89, 85, 85, 87, 92, 97, 102, 102, 99, 95, 92, 92, 94, 98, 100,
                99, 99, 99, 98, 98, 100, 104, 104, 103, 101, 99, 99, 101, 104,
                104, 102, 102, 100, 100, 105, 114, 120, 118, 108, 99, 91, 86,
                84, 87, 91, 94, 95, 96, 96, 95, 92, 88, 85, 84, 88, 91, 94,
                100, 103, 103, 104, 103, 102, 104, 106, 109, 110, 108, 105,
                101, 100, 100, 102, 108, 112, 107, 96, 87, 85, 86, 88, 90, 91,
                92, 95, 98, 98, 95, 91, 89, 87, 89, 90, 92, 95, 102, 103, 99,
                98, 100, 103, 107, 108, 104, 102, 101, 100, 105, 114, 122, 126,
                122, 111, 99, 89, 84, 86, 90, 94, 98, 100, 99, 95, 92, 88, 87,
                88, 90, 91, 92, 93, 96, 98, 100, 102, 101, 100, 100, 100, 100,
                102, 105, 107, 104, 103, 103, 103, 105, 108, 110, 107, 100, 96,
                92, 90, 89, 88, 90, 91, 91, 90, 89, 88, 88, 87, 88, 89, 91, 93,
                96, 98, 99, 102, 103, 104, 105, 107, 110, 112, 109, 106, 105,
                105, 106, 109, 109, 107, 104, 101, 96, 91, 88, 89, 90, 92, 94,
                95, 95, 93, 92, 91, 90, 90, 94, 96, 96, 97, 96, 95, 95, 97, 99,
                101, 102, 103, 103, 105, 107, 107, 107, 107, 109, 110, 110,
                109, 108, 105, 97, 90, 87, 87, 87, 89, 91, 92, 92, 89, 89, 89,
                89, 88, 88, 89, 89, 91, 94, 99, 102, 104, 103, 102, 103, 107,
                110, 110, 107, 103, 102, 105, 110, 113, 112, 106, 101, 95, 91,
                88, 88, 88, 91, 94, 95, 97, 97, 96, 93, 88, 88, 88, 90, 92, 92,
                94, 96, 97, 97, 99, 102, 103, 103, 103, 102, 102, 103, 107,
                109, 110, 113, 112, 110, 109, 107, 102, 94, 88, 87, 89, 91, 94,
                96, 97, 96, 91, 87, 83, 82, 83, 84, 87, 92, 96, 99, 100, 101,
                102, 102, 102, 102, 103, 103, 102, 102, 103, 104, 111, 115,
                116, 114, 109, 107, 104, 97, 90, 86, 85, 87, 91, 96, 97, 96,
                94, 91, 88, 86, 87, 89, 91, 93, 94, 97, 98, 101, 104, 106, 105,
                104, 103, 103, 103, 104, 106, 104, 101, 104, 107, 110, 111,
                109, 104, 100, 98, 96, 94, 94, 93, 93, 96, 98, 99, 96, 94, 92,
                91, 93, 97, 98, 100, 101, 101, 103, 105, 104, 100, 99, 99, 98,
                97, 98, 97, 95, 97, 102, 105, 110, 114, 110, 101, 94, 90, 90,
                93, 96, 97, 95, 92, 89, 88, 87, 86, 85, 86, 91, 93, 95, 96, 99,
                101, 103, 104, 104, 105, 106, 107, 106, 107, 109, 113, 115,
                112, 106, 104, 104, 108, 110, 107, 101, 95, 91, 88, 88, 88, 89,
                89, 89, 88, 88, 87, 87, 87, 88, 89, 92, 95, 100, 105, 104, 101,
                98, 99, 100, 100, 99, 98, 99, 99, 100, 99, 102, 108, 110, 106,
                101, 100, 100, 99, 97, 95, 94, 94, 96, 98, 97, 93, 88, 86, 87,
                89, 90, 91, 92, 93, 95, 98, 101, 102, 102, 101, 100, 102, 105,
                106, 106, 107, 108, 107, 103, 103, 105, 109, 113, 113, 107, 98,
                92, 88, 86, 86, 88, 89, 91, 92, 92, 91, 89, 87, 87, 88, 89, 90,
                93, 95, 96, 96, 94, 93, 93, 98, 104, 106, 106, 104, 104, 105,
                106, 107, 109, 113, 113, 112, 106, 99, 92, 86, 85, 88, 91, 94,
                97, 98, 98, 97, 95, 92, 91, 94, 96, 98, 97, 97, 97, 97, 99,
                101, 102, 101, 101, 102, 103, 106, 109, 105, 102, 101, 104,
                106, 107, 107, 105, 99, 96, 94, 96, 97, 96, 95, 94, 92, 89, 87,
                85, 84, 84, 84, 86, 89, 90, 91, 92, 95, 96, 98, 99, 101, 106,
                107, 107, 104, 101, 104, 111, 116, 116, 112, 105, 98, 90, 86,
                84, 86, 90, 96, 99, 103, 103, 100, 96, 93, 91, 91, 93, 95, 96,
                97, 97, 98, 101, 103, 104, 103, 103, 103, 104, 106, 109, 108,
                105, 105, 106, 107, 110, 109, 105, 100, 95, 91, 91, 91, 92, 94,
                95, 90, 87, 86, 88, 88, 87, 86, 86, 88, 91, 94, 97, 98, 96, 96,
                96, 99, 100, 102, 102, 100, 101, 105, 110, 115, 115, 112, 103,
                96, 91, 87, 87, 89, 94, 97, 97, 97, 98, 97, 94, 91, 88, 89, 91,
                93, 95, 94, 93, 95, 98, 101, 105, 108, 108, 107, 107, 107, 106,
                103, 105, 110, 114, 114, 111, 107, 103, 98, 95, 95, 96, 99, 99,
                99, 97, 94, 91, 88, 85, 84, 83, 83, 85, 87, 90, 93, 93, 92, 93,
                94, 99, 104, 107, 109, 107, 105, 102, 104, 110, 113, 114, 112,
                106, 100, 94, 89, 85, 86, 88, 91, 92, 94, 97, 99, 101, 101, 98,
                93, 89, 88, 92, 96, 97, 96, 95, 96, 97, 100, 102, 103, 103,
                103, 104, 104, 106, 104, 104, 106, 109, 111, 110, 106, 102, 98,
                94, 91, 91, 91, 92, 94, 96, 93, 89, 86, 84, 85, 86, 90, 93, 95,
                98, 101, 102, 101, 99, 98, 99, 102, 103, 102, 100, 101, 105,
                106, 109, 113, 118, 121, 121, 114, 103, 92, 83, 79, 79, 81, 84,
                89, 92, 93, 93, 92, 92, 91, 91, 91, 93, 95, 96, 98, 99, 99, 99,
                100, 101, 102, 102, 102, 103, 106, 106, 103, 107, 110, 113,
                115, 112, 105, 98, 93, 89, 88, 90, 91, 92, 95, 98, 99, 96, 91,
                87, 86, 86, 89, 91, 93, 94, 98, 101, 101, 100, 100, 101, 102,
                102, 103, 104, 106, 104, 108, 115, 120, 122, 116, 103, 92, 86,
                82, 82, 85, 88, 91, 94, 94, 96, 96, 93, 90, 89, 88, 89, 91, 94,
                97, 99, 99, 100, 101, 103, 105, 106, 107, 109, 109, 109, 108,
                105, 106, 109, 110, 114, 113, 107, 100, 95, 91, 89, 88, 88, 89,
                90, 91, 92, 93, 94, 96, 97, 97, 95, 92, 92, 94, 97, 99, 99, 98,
                94, 94, 98, 103, 103, 101, 100, 101, 105, 109, 113, 116, 117,
                108, 99, 94, 90, 90, 92, 93, 94, 95, 94, 92, 92, 92, 89, 89,
                91, 93, 96, 96, 97, 97, 99, 99, 99, 98, 99, 99, 99, 99, 100,
                101, 103, 104, 104, 106, 107, 111, 116, 117, 116, 101, 94, 90,
                87, 86, 85, 86, 88, 89, 91, 92, 92, 91, 91, 91, 91, 93, 96, 98,
                97, 97, 99, 99, 100, 100, 101, 102, 103, 102, 103, 100, 100,
                102, 106, 113, 121, 123, 116, 103, 93, 88, 85, 86, 87, 88, 90,
                93, 94, 95, 96, 94, 91, 89, 89, 89, 91, 92, 95, 96, 96, 96, 96,
                97, 100, 102, 104, 108, 111, 112, 110, 108, 108, 113, 117, 118,
                115, 110, 105, 100, 95, 92, 90, 88, 89, 91, 92, 92, 91, 90, 89,
                88, 86, 85, 85, 87, 90, 92, 91, 90, 91, 93, 95, 99, 103, 104,
                105, 105, 107, 109, 110, 112, 114, 115, 114, 111, 105, 98, 90,
                87, 88, 92, 94, 93, 94, 97, 101, 102, 102, 97, 92, 88, 89, 93,
                96, 99, 100, 100, 99, 98, 97, 97, 99, 100, 102, 102, 102, 103,
                105, 106, 109, 114, 116, 116, 113, 107, 102, 99, 96, 92, 90,
                91, 94, 94, 93, 91, 90, 90, 90, 89, 88, 87, 89, 91, 93, 94, 97,
                99, 100, 100, 100, 100, 100, 99, 98, 99, 101, 103, 107, 110,
                111, 110, 110, 107, 99, 91, 86, 86, 89, 93, 97, 98, 97, 96, 95,
                95, 94, 93, 92, 93, 94, 94, 95, 95, 96, 97, 98, 98, 98, 99, 98,
                97, 96, 97, 100, 103, 105, 105, 103, 104, 108, 113, 118, 118,
                109, 98, 91, 88, 89, 92, 97, 99, 100, 100, 100, 99, 97, 92, 89,
                86, 84, 84, 85, 86, 87, 88, 91, 93, 94, 96, 100, 102, 103, 105,
                104, 102, 101, 105, 111, 114, 117, 115, 109, 101, 98, 96, 91,
                85, 88, 90, 92, 95, 98, 98, 95, 91, 87, 88, 90, 93, 95, 95, 93,
                93, 95, 99, 102, 103, 106, 106, 107, 108, 110, 110, 111, 113,
                113, 110, 106, 96, 92, 89, 88, 87, 89, 91, 95, 98, 98, 97, 93,
                91, 90, 90, 92, 93, 95, 96, 98, 98, 97, 96, 94, 94, 95, 95, 98,
                99, 99, 98, 101, 103, 101, 105, 113, 121, 122, 114, 101, 95,
                93, 90, 88, 89, 91, 95, 100, 102, 100, 95, 91, 89, 89, 91, 94,
                95, 95, 95, 94, 95, 96, 99, 102, 104, 105, 106, 107, 110, 110,
                106, 108, 105, 107, 110, 108, 102, 95, 91, 89, 92, 97, 102,
                104, 103, 98, 92, 88, 85, 83, 83, 86, 90, 92, 92, 94, 93, 92,
                92, 94, 97, 101, 102, 101, 99, 99, 102, 101, 104, 111, 115,
                116, 111, 103, 98, 95, 92, 89, 87, 88, 90, 95, 99, 102, 101,
                95, 91, 87, 86, 88, 94, 96, 96, 95, 95, 96, 98, 99, 103, 106,
                105, 105, 105, 105, 105, 105, 107, 108, 110, 110, 108, 107,
                105, 102, 100, 99, 97, 96, 96, 97, 98, 99, 96, 90, 86, 85, 86,
                88, 89, 88, 88, 90, 93, 95, 99, 100, 100, 100, 103, 105, 105,
                102, 100, 97, 97, 99, 103, 109, 114, 112, 104, 95, 90, 87, 84,
                83, 84, 87, 92, 98, 106, 108, 103, 95, 91, 88, 87, 89, 92, 94,
                96, 98, 99, 100, 101, 102, 103, 105, 105, 105, 105, 104, 101,
                101, 105, 111, 114, 111, 103, 100, 98, 93, 92, 93, 98, 105,
                108, 105, 97, 94, 93, 92, 90, 87, 85, 87, 91, 95, 96, 95, 93,
                92, 92, 93, 97, 99, 100, 100, 101, 100, 100, 105, 110, 113,
                114, 111, 108, 105, 99, 89, 82, 82, 86, 93, 100, 106, 108, 103,
                93, 88, 88, 88, 87, 87, 87, 88, 90, 95, 100, 103, 106, 107,
                109, 112, 114, 115, 112, 113, 117, 120, 120, 117, 110, 103, 97,
                92, 88, 85, 84, 86, 88, 92, 96, 98, 98, 96, 94, 94, 93, 91, 91,
                93, 94, 95, 96, 98, 99, 98, 99, 102, 107, 111, 109, 107, 111,
                114, 114, 115, 114, 110, 104, 95, 90, 88, 91, 96, 100, 105,
                109, 107, 99, 90, 84, 78, 75, 78, 84, 88, 93, 95, 97, 99, 100,
                101, 103, 105, 107, 109, 113, 116, 116, 113, 106, 104, 105,
                110, 113, 111, 101, 93, 89, 88, 89, 92, 93, 95, 96, 96, 96, 94,
                90, 87, 88, 89, 92, 94, 96, 99, 102, 101, 99, 98, 98, 100, 101,
                101, 101, 99, 98, 97, 98, 103, 106, 108, 112, 114, 109, 101,
                97, 94, 93, 94, 97, 99, 100, 98, 89, 87, 86, 86, 87, 89, 90,
                92, 95, 98, 99, 99, 97, 98, 100, 103, 105, 106, 105, 105, 105,
                104, 106, 110, 112, 110, 103, 98, 94, 93, 92, 93, 93, 93, 94,
                96, 98, 99, 99, 98, 96, 93, 92, 93, 96, 96, 95, 95, 97, 98, 98,
                97, 99, 102, 102, 100, 97, 99, 106, 111, 117, 120, 117, 106,
                99, 95, 93, 90, 88, 90, 94, 96, 97, 94, 90, 87, 85, 84, 84, 88,
                91, 93, 94, 96, 96, 97, 98, 100, 102, 102, 101, 100, 102, 104,
                105, 108, 107, 107, 112, 117, 120, 120, 115, 107, 99, 96, 93,
                92, 94, 96, 98, 98, 97, 94, 90, 87, 86, 86, 86, 85, 87, 92, 94,
                96, 95, 93, 91, 91, 92, 94, 96, 97, 98, 99, 101, 104, 110, 119,
                126, 128, 122, 111, 101, 93, 89, 88, 90, 93, 98, 102, 107, 111,
                108, 97, 89, 85, 82, 81, 82, 85, 89, 92, 95, 99, 103, 105, 107,
                107, 107, 108, 108, 110, 112, 110, 107, 107, 108, 113, 116,
                114, 109, 101, 95, 90, 88, 89, 90, 92, 94, 93, 91, 87, 85, 84,
                83, 82, 82, 83, 84, 87, 88, 92, 95, 97, 97, 99, 101, 102, 103,
                102, 106, 115, 127, 134, 133, 122, 109, 101, 94, 88, 87, 88,
                93, 99, 103, 103, 95, 84, 78, 75, 74, 76, 78, 82, 86, 91, 95,
                99, 103, 106, 106, 108, 111, 113, 114, 113, 112, 111, 110, 112,
                114, 122, 127, 127, 116, 101, 91, 85, 84, 87, 93, 98, 102, 105,
                96, 90, 88, 86, 83, 81, 80, 79, 79, 81, 81, 80, 78, 79, 82, 87,
                93, 96, 102, 107, 110, 118, 129, 141, 148, 147, 139, 124, 106,
                91, 80, 76, 79, 86, 97, 105, 111, 110, 99, 85, 76, 70, 68, 72,
                77, 83, 87, 90, 94, 97, 98, 99, 101, 103, 106, 110, 114, 117,
                115, 116, 119, 127, 135, 141, 135, 121, 107, 97, 88, 84, 85,
                88, 93, 95, 97, 97, 95, 89, 84, 82, 81, 82, 82, 83, 85, 87, 89,
                90, 88, 89, 89, 91, 94, 94, 96, 100, 101, 105, 112, 122, 132,
                135, 131, 122, 109, 97, 87, 79, 78, 81, 86, 93, 96, 98, 97, 93,
                87, 85, 84, 85, 89, 93, 95, 96, 97, 99, 101, 101, 102, 103,
                104, 107, 110, 113, 113, 109, 109, 112, 119, 125, 127, 120,
                106, 94, 89, 87, 89, 93, 99, 102, 102, 97, 89, 88, 87, 86, 84,
                82, 82, 82, 85, 90, 93, 95, 95, 95, 97, 99, 99, 99, 99, 101,
                102, 105, 108, 111, 120, 128, 130, 118, 101, 88, 81, 82, 85,
                91, 97, 101, 101, 96, 91, 90, 90, 89, 90, 91, 93, 93, 93, 94,
                97, 97, 97, 98, 99, 101, 104, 104, 104, 102, 105, 109, 115,
                119, 120, 115, 106, 94, 91, 89, 91, 92, 94, 96, 94, 90, 85, 83,
                84, 86, 85, 86, 87, 89, 92, 93, 94, 92, 90, 92, 96, 102, 105,
                105, 104, 106, 112, 114, 117, 121, 122, 119, 114, 109, 103, 94,
                85, 78, 76, 79, 86, 94, 101, 106, 106, 98, 87, 83, 81, 84, 87,
                93, 96, 97, 97, 97, 98, 98, 98, 100, 100, 101, 102, 103, 106,
                109, 106, 103, 107, 108, 112, 117, 116, 109, 99, 93, 89, 88,
                88, 91, 98, 106, 112, 111, 100, 90, 88, 87, 85, 83, 84, 88, 91,
                93, 94, 94, 93, 92, 91, 92, 94, 98, 100, 100, 103, 104, 106,
                112, 120, 129, 135, 130, 116, 99, 90, 87, 86, 88, 93, 99, 102,
                105, 103, 98, 92, 87, 84, 84, 87, 91, 95, 97, 102, 102};
        setStatusSet(arrIntData);
        setObservedDataSet();
        setStatusProbability(arrIntData);
        setTransitionProbability(arrIntData);
        setEmissionProbability(arrIntData);
    }

    private void setStatusSet(int arrIntData[]) {

        for (int i = 0; i < arrIntData.length; i++) {
            statusSet.add(new MEStatus(i, arrIntData,0.0d));
        }
    }

    private void setObservedDataSet() throws MarkovException {
        observedDataSet.add(new MEObservedData(MEObservedData.ACCEL_DOWN));
        observedDataSet.add(new MEObservedData(MEObservedData.ACCEL_UP));
        observedDataSet.add(new MEObservedData(MEObservedData.ACCEL_OTHER));
    }

    private void setStatusProbability(int arrIntData[]) {
        MEStatus tmpStatus = new MEStatus(0, arrIntData,0.0d);
        Float initValue = new Float(0.0f);
        for (MEStatus es : statusSet) {

            this.statusProbability.put(es, initValue);
        }
        for (int i = 1; i < arrIntData.length - 1; i++) {
            MEStatus tmpStatus1 = new MEStatus(i, arrIntData,0.0d);
            if (tmpStatus.equals(tmpStatus1)) {
                this.statusProbability
                        .put(tmpStatus,
                                new Float(this.statusProbability.get(tmpStatus)
                                        .floatValue() + 1));
            }
            tmpStatus = new MEStatus(i, arrIntData,0.0d);
        }
        for (MEStatus es : statusSet) {
            this.statusProbability.put(es,
                    new Float(this.statusProbability.get(tmpStatus)
                            .floatValue() / this.statusProbability.size()));
        }
    }

    private void setTransitionProbability(int arrIntData[]) {

        Map<MEStatus, Float> initValue = new HashMap<MEStatus, Float>();
        for (MEStatus es : statusSet) {
            this.transitionProbability.put(es, initValue);
        }

        MEStatus currentStatus = new MEStatus(0, arrIntData,0.0d);
        for (int i = 1; i < arrIntData.length - 1; i++) {
            MEStatus tmpStatus = new MEStatus(i, arrIntData,0.0d);
            Map<MEStatus, Float> currentValue = this.transitionProbability
                    .get(currentStatus);
            if (currentValue.containsKey(tmpStatus)) {
                currentValue
                        .put(tmpStatus, new Float(currentValue.get(tmpStatus)
                                .floatValue() + 1.0f));
            } else {
                currentValue.put(tmpStatus, new Float(1.0f));
            }
            this.transitionProbability.put(currentStatus, currentValue);
        }
        for (Map.Entry<MEStatus, Map<MEStatus, Float>> entry : this.transitionProbability
                .entrySet()) {
            Map<MEStatus, Float> currentValue = entry.getValue();
            int numberOfElement = currentValue.size();
            for (Map.Entry<MEStatus, Float> es : currentValue.entrySet()) {
                es.setValue(new Float(es.getValue().floatValue()
                        / numberOfElement));
            }
            entry.setValue(currentValue);
        }
    }

    private void setEmissionProbability(int arrIntData[])
            throws MarkovException {

        Map<MEObservedData, Float> initValue = new HashMap<MEObservedData, Float>();
        initValue.put(new MEObservedData(MEObservedData.ACCEL_UP), new Float(
                0.0f));
        initValue.put(new MEObservedData(MEObservedData.ACCEL_DOWN), new Float(
                0.0f));
        initValue.put(new MEObservedData(MEObservedData.ACCEL_OTHER),
                new Float(0.0f));

        for (MEStatus es : statusSet) {
            this.emissionProbability.put(es, initValue);
        }

        for (int i = 0; i < arrIntData.length - 1; i++) {
            MEStatus currentStatus = new MEStatus(i, arrIntData,0.0d);
            this.emissionProbability.put(currentStatus,
                    getEstimateResult(currentStatus));
        }
    }

    public Map<MEObservedData, Float> getEstimateResult(MEStatus mes)
            throws MarkovException {
        int MAX_LENGTH_OF_WINDS = 10;
        Map<MEObservedData, Float> result = new HashMap<MEObservedData, Float>();
        MEObservedData up = new MEObservedData(MEObservedData.ACCEL_UP);
        MEObservedData down = new MEObservedData(MEObservedData.ACCEL_DOWN);
        MEObservedData other = new MEObservedData(MEObservedData.ACCEL_OTHER);
        result.put(up, new Float(0.0f));
        result.put(down, new Float(0.0f));
        result.put(other, new Float(1.0f));

        // Filter it not top or bottom
        if (mes.before * mes.after > 0) {
            // it is top
            if (mes.before > 0) {
                // it is up
                if (mes.id > 105) {
                    float rateUp = 0.0f, rateOther = 0.0f;
                    if (mes.before + mes.after > 15 && mes.before > 1 && mes.after>1) {
                        Log.d("OutputData", "4");
                        rateUp = 1.0f;
                        rateOther = 0.0f;
                    } else {
                        rateUp = 0.0f;
                        rateOther = 1.0f;
                    }
                    result.put(up, new Float(rateUp));
                    result.put(other, new Float(rateOther));
                }
            }
            // it is bot
            else {
                // it is down
                if (mes.id < 90) {
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
}
