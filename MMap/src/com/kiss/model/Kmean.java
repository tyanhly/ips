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

    private float groupLessUpCenter = 20f;
    private float groupLessDownCenter = 0f;
    private float groupLessOtherCenter = 5;

    private int numberOfGroupLessUpCenter = 0;
    private int numberOfGroupLessDownCenter = 0;
    private int numberOfGroupLessOtherCenter = 0;

    private float maxAUpDistance = 0;
    private float maxADownDistance = 0;
    private float maxAOtherDistance = 0;

    private float maxLessUpDistance = 0;
    private float maxLessDownDistance = 0;
    private float maxLessOtherDistance = 0;

    private int frequency; // number of item in a circle

    public Kmean(List<MEStatus> inputDataList) {
        _estimationAClusterByKMean(inputDataList);
        _estimationLessClusterByKMean(inputDataList);
        _setFrequency(inputDataList);
        _setMaxADistance(inputDataList);
        _setMaxLessDistance(inputDataList);
    }

    private void _setFrequency(List<MEStatus> inputDataList) {
        // MEObservedData current = getAGroup(inputDataList.get(0));
        // float currentDistance = getADistance(inputDataList.get(0));
        // int stepStatus = 1;
        // for(int i=1; i< inputDataList.size();i++){
        // MEObservedData tmp = getAGroup(inputDataList.get(i));
        // float distance = getADistance(inputDataList.get(i));
        // if(Math.abs(tmp.data-current.data)==1){
        // stepStatus++;
        // }
        // }
    }

    private void _setMaxADistance(List<MEStatus> inputDataList) {
        for (int i = 0; i < inputDataList.size(); i++) {
            float distance = getADistance(inputDataList.get(i));

            MEObservedData aGroup = getAGroup(inputDataList.get(i));
            switch (aGroup.data) {
            case MEObservedData.ACCEL_UP:
                if (distance > maxAUpDistance) {
                    maxADownDistance = distance;
                }
                break;
            case MEObservedData.ACCEL_DOWN:
                if (distance > maxADownDistance) {
                    maxADownDistance = distance;
                }
                break;
            case MEObservedData.ACCEL_OTHER:
                if (distance > maxAOtherDistance) {
                    maxADownDistance = distance;
                }
                break;

            default:
                break;
            }

        }
    }

    private void _setMaxLessDistance(List<MEStatus> inputDataList) {
        for (int i = 0; i < inputDataList.size(); i++) {
            float distance = getLessDistance(inputDataList.get(i));

            MEObservedData group = getLessGroup(inputDataList.get(i));
            switch (group.data) {
            case MEObservedData.ACCEL_UP:
                if (distance > maxAUpDistance) {
                    maxADownDistance = distance;
                }
                break;
            case MEObservedData.ACCEL_DOWN:
                if (distance > maxADownDistance) {
                    maxADownDistance = distance;
                }
                break;
            case MEObservedData.ACCEL_OTHER:
                if (distance > maxAOtherDistance) {
                    maxADownDistance = distance;
                }
                break;

            default:
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
        while (oldOne - newOne != 0.0f) {
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
        }

    }

    private void _estimationLessClusterByKMean(List<MEStatus> inputDataList) {

        ArrayList<MEStatus> groupUpList = new ArrayList<MEStatus>();
        ArrayList<MEStatus> groupDownList = new ArrayList<MEStatus>();
        ArrayList<MEStatus> groupOtherList = new ArrayList<MEStatus>();

        float oldOne = -1, newOne = 0;
        while (oldOne - newOne != 0.0f) {
            oldOne = _hashCodeGroupUnique((float) groupLessUpCenter,
                    (float) groupLessDownCenter, (float) groupLessOtherCenter);
            for (int i = 0; i < inputDataList.size(); i++) {
                float dUp = Math.abs(inputDataList.get(i).less
                        - groupLessUpCenter);

                float dDown = Math.abs(inputDataList.get(i).less
                        - groupLessDownCenter);
                float dOther = Math.abs(inputDataList.get(i).less
                        - groupLessOtherCenter);

                if (dUp < dDown && dUp < dOther) {
                    groupUpList.add(inputDataList.get(i));
                } else if (dUp > dDown && dOther > dDown) {
                    groupDownList.add(inputDataList.get(i));
                } else {
                    groupOtherList.add(inputDataList.get(i));
                }

            }
            groupLessUpCenter = getAgvLess(groupUpList, groupLessUpCenter);
            groupLessDownCenter = getAgvLess(groupDownList, groupLessDownCenter);
            groupLessOtherCenter = getAgvLess(groupOtherList, groupLessOtherCenter);

            numberOfGroupLessUpCenter = groupUpList.size();
            numberOfGroupLessDownCenter = groupDownList.size();
            numberOfGroupLessOtherCenter = groupOtherList.size();

            newOne = _hashCodeGroupUnique((float) groupLessUpCenter,
                    (float) groupLessDownCenter, (float) groupLessOtherCenter);
        }
    }

    public MEObservedData getLessGroup(MEStatus mes) {
        MEObservedData up = null;
        MEObservedData down = null;
        MEObservedData other = null;
        try {
            up = new MEObservedData(MEObservedData.ACCEL_UP);
            down = new MEObservedData(MEObservedData.ACCEL_DOWN);
            other = new MEObservedData(MEObservedData.ACCEL_OTHER);
        } catch (KissModelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        float t = Math.abs(mes.less - groupLessUpCenter);
        float t1 = Math.abs(mes.less - groupLessDownCenter);
        float t2 = Math.abs(mes.less - groupLessOtherCenter);

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
            // TODO Auto-generated catch block
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

    public float getLessDistance(MEStatus mes) {

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

        MEObservedData aGroup = getAGroup(mes);
        MEObservedData aLessGroup = getLessGroup(mes);

        if (aGroup.equals(aLessGroup)) {
            return aGroup;
        } else {
            switch (aGroup.data) {
            case MEObservedData.ACCEL_UP:
//                return aGroup;
                if (mes.less > this.groupLessUpCenter)
                    return aGroup;
                else
                    return aLessGroup;
            case MEObservedData.ACCEL_DOWN:
                if (mes.less < this.groupLessDownCenter)
                    return aGroup;
                else
                    return aLessGroup;

            case MEObservedData.ACCEL_OTHER:
                return aGroup;
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
    

    public static float getAgvLess(List<MEStatus> inputDataList, float defaultValue) {
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
