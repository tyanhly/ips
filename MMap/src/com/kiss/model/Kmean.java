package com.kiss.model;

import java.util.ArrayList;
import java.util.List;

public class Kmean {

    private float groupAUpCenter = 108f;
    private float groupADownCenter = 88f;
    private float groupAOtherCenter = 98f;

    private int numberOfGroupAUpCenter = 0;
    private int numberOfGroupADownCenter = 0;
    private int numberOfGroupAOtherCenter = 0;

    private float grouplessUpCenter = 20f;
    private float grouplessDownCenter = 0f;
    private float grouplessOtherCenter = 5;
    
    private int numberOfGrouplessUpCenter = 0;
    private int numberOfGrouplessDownCenter = 0;
    private int numberOfGrouplessOtherCenter = 0;

    private float maxAUpDistance = 0;
    private float maxADownDistance = 0;
    private float maxAOtherDistance = 0;

    private float maxlessUpDistance = 0;
    private float maxlessDownDistance = 0;
    private float maxlessOtherDistance = 0;

    private int maxFrequency; // number of item in a circle

    public Kmean(List<MEStatus> inputDataList) {
        _estimationAClusterByKMean(inputDataList);
        _estimationlessClusterByKMean(inputDataList);
        _setMaxADistance(inputDataList);
        _setMaxlessDistance(inputDataList);
        _setFrequency(inputDataList);
    }

    private void _setFrequency(List<MEStatus> inputDataList) {
        // int size = inputDataList.size();
        // maxFrequency = size / numberOfGroupAUpCenter;
    }

    private void _setMaxADistance(List<MEStatus> inputDataList) {
        for (int i = 0; i < inputDataList.size(); i++) {
            float distance = getADistance(inputDataList.get(i));

            MEObservedData aGroup = getAGroup(inputDataList.get(i));
            switch (aGroup.data) {
            case MEObservedData.ACCEL_UP:
                if (distance > maxAUpDistance) {
                    maxAUpDistance = distance;
                }
                break;
            case MEObservedData.ACCEL_DOWN:
                if (distance > maxADownDistance) {
                    maxADownDistance = distance;
                }
                break;
            case MEObservedData.ACCEL_OTHER:
                if (distance > maxAOtherDistance) {
                    maxAOtherDistance = distance;
                }
                break;
            }

        }
    }

    private void _setMaxlessDistance(List<MEStatus> inputDataList) {
        for (int i = 0; i < inputDataList.size(); i++) {
            float distance = getlessDistance(inputDataList.get(i));

            MEObservedData group = getlessGroup(inputDataList.get(i));
            switch (group.data) {
            case MEObservedData.ACCEL_UP:
                if (distance > maxlessUpDistance) {
                    maxlessUpDistance = distance;
                }
                break;
            case MEObservedData.ACCEL_DOWN:
                if (distance > maxlessDownDistance) {
                    maxlessDownDistance = distance;
                }
                break;
            case MEObservedData.ACCEL_OTHER:
                if (distance > maxlessOtherDistance) {
                    maxlessOtherDistance = distance;
                }
                break;

            }

        }
    }

    private float _hashCodeGroupUnique(float a, float b, float c) {
        return a * 1000 * 1000 + b * 1000 + c;
    }

    private void _estimationAClusterByKMean(List<MEStatus> inputDataList) {

        ArrayList<MEStatus> groupUpList = new ArrayList<MEStatus>();
        ArrayList<MEStatus> groupDownList = new ArrayList<MEStatus>();
        ArrayList<MEStatus> groupOtherList = new ArrayList<MEStatus>();

        float oldOne = -1, newOne = 0;
        do {
            oldOne = _hashCodeGroupUnique(groupAUpCenter, groupADownCenter,
                    groupAOtherCenter);
            for (int i = 0; i < inputDataList.size(); i++) {
                float dAUp = Math.abs(inputDataList.get(i).a - groupAUpCenter);
                float dADown = Math.abs(inputDataList.get(i).a
                        - groupADownCenter);
                float dAOther = Math.abs(inputDataList.get(i).a
                        - groupAOtherCenter);

                if (dAUp < dADown && dAUp < dAOther) {
                    groupUpList.add(inputDataList.get(i));
                } else if (dAUp > dADown && dAOther > dADown) {
                    groupDownList.add(inputDataList.get(i));
                } else {
                    groupOtherList.add(inputDataList.get(i));
                }

            }
            groupAUpCenter = getAgv(groupUpList, groupAUpCenter);
            groupADownCenter = getAgv(groupDownList, groupADownCenter);
            groupAOtherCenter = getAgv(groupOtherList, groupAOtherCenter);
            numberOfGroupAUpCenter = groupUpList.size();
            numberOfGroupADownCenter = groupDownList.size();
            numberOfGroupAOtherCenter = groupOtherList.size();

            newOne = _hashCodeGroupUnique(groupAUpCenter, groupADownCenter,
                    groupAOtherCenter);
        } while (oldOne - newOne != 0.0f);

    }

    private void _estimationlessClusterByKMean(List<MEStatus> inputDataList) {

        ArrayList<MEStatus> groupUpList = new ArrayList<MEStatus>();
        ArrayList<MEStatus> groupDownList = new ArrayList<MEStatus>();
        ArrayList<MEStatus> groupOtherList = new ArrayList<MEStatus>();

        float oldOne = -1, newOne = 0;
        do {
            oldOne = _hashCodeGroupUnique((float) grouplessUpCenter,
                    (float) grouplessDownCenter,
                    (float) grouplessOtherCenter);
            for (int i = 0; i < inputDataList.size(); i++) {
                float dUp = Math.abs(inputDataList.get(i).less
                        - grouplessUpCenter);

                float dDown = Math.abs(inputDataList.get(i).less
                        - grouplessDownCenter);
                float dOther = Math.abs(inputDataList.get(i).less
                        - grouplessOtherCenter);

                if (dUp < dDown && dUp < dOther) {
                    groupUpList.add(inputDataList.get(i));
                } else if (dUp > dDown && dOther > dDown) {
                    groupDownList.add(inputDataList.get(i));
                } else {
                    groupOtherList.add(inputDataList.get(i));
                }

            }
            grouplessUpCenter = getAgvless(groupUpList, grouplessUpCenter);
            grouplessDownCenter = getAgvless(groupDownList,
                    grouplessDownCenter);
            grouplessOtherCenter = getAgvless(groupOtherList,
                    grouplessOtherCenter);
            numberOfGrouplessUpCenter = groupUpList.size();
            numberOfGrouplessDownCenter = groupDownList.size();
            numberOfGrouplessOtherCenter = groupOtherList.size();

            newOne = _hashCodeGroupUnique((float) grouplessUpCenter,
                    (float) grouplessDownCenter,
                    (float) grouplessOtherCenter);
        } while (oldOne - newOne != 0.0f);
    }

    public MEObservedData getlessGroup(MEStatus mes) {
        MEObservedData up = null;
        MEObservedData down = null;
        MEObservedData other = null;
        try {
            up = new MEObservedData(MEObservedData.ACCEL_UP);
            down = new MEObservedData(MEObservedData.ACCEL_DOWN);
            other = new MEObservedData(MEObservedData.ACCEL_OTHER);
        } catch (KissModelException e) {
            e.printStackTrace();
        }

        float t = Math.abs(mes.less - grouplessUpCenter);
        float t1 = Math.abs(mes.less - grouplessDownCenter);
        float t2 = Math.abs(mes.less - grouplessOtherCenter);

        if (t < t1 && t < t2) {
            return up;
        } else if (t1 < t && t1 < t2) {
            return down;
        } else {
            return other;
        }

    }

    public MEObservedData getAGroup(MEStatus mes) {
        MEObservedData up = null;
        MEObservedData down = null;
        MEObservedData other = null;
        try {
            up = new MEObservedData(MEObservedData.ACCEL_UP);
            down = new MEObservedData(MEObservedData.ACCEL_DOWN);
            other = new MEObservedData(MEObservedData.ACCEL_OTHER);
        } catch (KissModelException e) {
            e.printStackTrace();
        }

        float t = Math.abs(mes.a - groupAUpCenter);
        float t1 = Math.abs(mes.a - groupADownCenter);
        float t2 = Math.abs(mes.a - groupAOtherCenter);

        if (t < t1 && t < t2) {
            return up;
        } else if (t1 < t && t1 < t2) {
            return down;
        } else {
            return other;
        }

    }

    public float getlessDistance(MEStatus mes) {

        float t = Math.abs(mes.less - groupAUpCenter);
        float t1 = Math.abs(mes.less - groupADownCenter);
        float t2 = Math.abs(mes.less - groupAOtherCenter);

        if (t < t1 && t < t2) {
            return t;
        } else if (t1 < t && t1 < t2) {
            return t1;
        } else {
            return t2;
        }

    }

    public float getADistance(MEStatus mes) {

        float t = Math.abs(mes.a - groupAUpCenter);
        float t1 = Math.abs(mes.a - groupADownCenter);
        float t2 = Math.abs(mes.a - groupAOtherCenter);

        if (t < t1 && t < t2) {
            return t;
        } else if (t1 < t && t1 < t2) {
            return t1;
        } else {
            return t2;
        }

    }

    public MEObservedData estimateResult(MEStatus mes) {
        MEObservedData up = null;
        MEObservedData down = null;
        MEObservedData other = null;
        try {
            up = new MEObservedData(MEObservedData.ACCEL_UP);
            down = new MEObservedData(MEObservedData.ACCEL_DOWN);
            other = new MEObservedData(MEObservedData.ACCEL_OTHER);
        } catch (KissModelException e) {
            e.printStackTrace();
        }

        MEObservedData aGroup = getAGroup(mes);
        MEObservedData alessGroup = getlessGroup(mes);
        // int tmp = (maxFrequency >
        // MTrainedData.maxLengthOfWinds)?MTrainedData.maxLengthOfWinds:
        // maxFrequency;

        if (aGroup.equals(alessGroup)) {
            if (aGroup.data == MEObservedData.ACCEL_UP) {
                if (mes.greater == 0 && Math.abs(mes.less - 2*MTrainedData.maxLengthOfWinds) < 5) {
                    return up;
                } else {
                    return other;
                }
            }
            return aGroup;
        } else {
            switch (aGroup.data) {
            case MEObservedData.ACCEL_UP:
                if (mes.less < this.grouplessOtherCenter)
                    return down;
                else
                    return other;
            case MEObservedData.ACCEL_DOWN:
                if (mes.less > this.grouplessDownCenter)
                    return other;
                else
                    return down;

            case MEObservedData.ACCEL_OTHER:
                return other;
            }
        }
        return null;
    }

    public static float getAgv(List<MEStatus> inputDataList, float defaultValue) {
        if (inputDataList.size() == 0) {
            return defaultValue;
        }
        int tmp = 0;
        for (MEStatus mes : inputDataList) {
            tmp += mes.a;
        }
        return tmp / inputDataList.size();
    }

    public static float getAgvless(List<MEStatus> inputDataList,
            float defaultValue) {
        if (inputDataList.size() == 0) {
            return defaultValue;
        }
        int tmp = 0;
        for (MEStatus mes : inputDataList) {
            tmp += mes.less;
        }
        return tmp / inputDataList.size();
    }
}
