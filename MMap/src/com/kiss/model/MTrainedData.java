package com.kiss.model;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

@SuppressLint("UseValueOf")
public class MTrainedData {
    public static final String READ_COMMON_FILE_URL = "http://192.168.30.17/php/readapi.php?file=ips_sensors_common";
    public static final String READ_USER_FILE_URL = "http://192.168.30.17/php/readapi.php?file=ips_sensors_user";
    public static final String WRITE_COMMON_FILE_URL = "http://192.168.30.17/php/writeapi.php?file=ips_sensors_common";
    public static final String WRITE_USER_FILE_URL = "http://192.168.30.17/php/writeapi.php?file=ips_sensors_user";
    public static final String DATA_RECORD_FORMAT = "%d %.3f %.3f %.3f %.3f\n";
    public static final String USER_CONST = "USER";
    public static final String COMMON_CONST = "COMMON";
    public static int maxLengthOfWinds = 15;

    public static Markov mkUser;
    public static Markov mkCommon;
    public static Markov mkInit;

    public String apiUserResponse = new String("");
    public String apiCommonResponse = new String("");
    public String responseUsed = apiUserResponse;

    public static void pushUserDataToServer(String data) {
        StaticHttpRequest task = new StaticHttpRequest();
        try {
            task.execute(new String[] {
                    "PUTDATA",
                    WRITE_USER_FILE_URL + "&p="
                            + URLEncoder.encode(data, "utf-8") });
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            Log.e("Error", e.getMessage(), e);
            e.printStackTrace();
        }
        // StaticHttpRequest task1 = new StaticHttpRequest();
        // task1.execute(new String[] { "PUTDATA", WRITE_COMMON_FILE_URL+ "&p="
        // + data });
    }

    private void _getApiResponse() {
        HttpRequest task = new HttpRequest();
        task.execute(new String[] { USER_CONST, READ_USER_FILE_URL });
        // HttpRequest task1 = new HttpRequest();
        // task1.execute(new String[] { "COMMON", READ_COMMON_FILE_URL });
    }

    public MTrainedData() {
        // _getApiResponse();
    }

    public void setMkUserAgain() {
        Log.d("StartEnd", "setMkUserAgain");
        _getApiResponse();
    }

    public MEObservedData getMEObservedData(MEStatus mes) {
        if (mkUser == null) {
            return null;
        }
        return mkUser.getMEObservedData(mes);
    }

    private void _trainningData() {

        if (apiUserResponse != "") {
            List<MEStatus> inputDataList = new ArrayList<MEStatus>();
            String[] records = apiUserResponse.trim().split("\n");
            for (int i = 0; i < records.length; i++) {
                String record = records[i];
                String[] data = record.split(" ");
                if (data.length > 0) {
                    long time = Long.parseLong(data[0]);
                    float a1 = Float.parseFloat(data[1]);
                    float a2 = Float.parseFloat(data[2]);
                    float a3 = Float.parseFloat(data[3]);
                    int a = Math.round(a1 * a1 + a2 * a2 + a3 * a3);
                    float azimuth = Float.parseFloat(data[4]);

                    MEStatus inputData = new MEStatus(a, azimuth, time);
                    inputDataList.add(inputData);
                }
            }
            List<MEStatus> simplifyDataList = getSimplifyDataList(inputDataList);
//            List<MEStatus> simplifyDataList = inputDataList;

            mkUser = new Markov();
            mkUser.setDataFromKmeanEstimation(inputDataList);
            mkUser.setStatusSet(simplifyDataList);
            mkUser.setObservedDataSet();
            mkUser.setStatusProbability(simplifyDataList);
            mkUser.setTransitionProbability(simplifyDataList);
            mkUser.setEmissionProbability(simplifyDataList);
        }

    }

    public static List<MEStatus> getContrastList(List<MEStatus> inputDataList) {

        List<MEStatus> tmpList = new ArrayList<MEStatus>(inputDataList);
        List<MEStatus> rList = new ArrayList<MEStatus>();

        boolean hadUp = false;

        rList.clear();
        int count = 0;
        for (int i = 0; i < tmpList.size() - 1; i++) {

            boolean nowUp = false;
            if (tmpList.get(i).a < tmpList.get(i + 1).a) {
                nowUp = true;
            }
            if (hadUp != nowUp) {
                MEStatus current = tmpList.get(i);
                current.before = count;
                if (rList.size() > 0) {
                    MEStatus tn = tmpList.get(rList.size() - 1);
                    tn.after = count;
                    rList.set(rList.size() - 1, tn);
                }
                rList.add(current);
                hadUp = nowUp;

                count = 0;

            } else {
                count++;
            }
        }
        return rList;
    }

    public static List<MEStatus> getSimplifyDataList(
            List<MEStatus> inputDataList) {
        return getContrastList(inputDataList);
    }

    public static void estimationClusterByKMean(List<MEStatus> inputDataList,
            Markov mk) {
        mk.kmeanGroupAUp = 108;
        mk.kmeanGroupADown = 88f;
        mk.kmeanGroupAOther = 98f;
        ArrayList<MEStatus> groupAUpList = new ArrayList<MEStatus>();
        ArrayList<MEStatus> groupADownList = new ArrayList<MEStatus>();
        ArrayList<MEStatus> groupAOtherList = new ArrayList<MEStatus>();

        ArrayList<MEStatus> groupBeUpList = new ArrayList<MEStatus>();
        ArrayList<MEStatus> groupBeDownList = new ArrayList<MEStatus>();
        ArrayList<MEStatus> groupBeOtherList = new ArrayList<MEStatus>();

        ArrayList<MEStatus> groupAfUpList = new ArrayList<MEStatus>();
        ArrayList<MEStatus> groupAfDownList = new ArrayList<MEStatus>();
        ArrayList<MEStatus> groupAfOtherList = new ArrayList<MEStatus>();

        float oldOne = -1, newOne = 0, count = 0;
        while (oldOne - newOne != 0.0f) {
            count++;
            Log.d("StartEnd1", "count estimationClusterByKMean: " + count);
            Log.d("StartEnd1", "size inputDataList: " + inputDataList.size());
            Log.d("StartEnd1", "mk.kmeanGroupAUp: " + mk.kmeanGroupAUp);
            Log.d("StartEnd1", "mk.kmeanGroupADown: " + mk.kmeanGroupADown);
            Log.d("StartEnd1", "mk.kmeanGroupAOther: " + mk.kmeanGroupAOther);
            oldOne = mk.kmeanGroupAUp * 1000000 + mk.kmeanGroupADown * 1000
                    + mk.kmeanGroupAOther;
            for (int i = 0; i < inputDataList.size(); i++) {
                float dAUp = Math
                        .abs(inputDataList.get(i).a - mk.kmeanGroupAUp);
                float dADown = Math.abs(inputDataList.get(i).a
                        - mk.kmeanGroupADown);
                float dAOther = Math.abs(inputDataList.get(i).a
                        - mk.kmeanGroupAOther);

                if (dAUp < dADown && dAUp < dAOther) {
                    groupAUpList.add(inputDataList.get(i));
                } else if (dAUp > dADown && dAOther > dADown) {
                    groupADownList.add(inputDataList.get(i));
                } else {
                    groupAOtherList.add(inputDataList.get(i));
                }

            }
            mk.kmeanGroupAUp = getAgvOfA(groupAUpList, mk.kmeanGroupAUp);
            mk.kmeanGroupADown = getAgvOfA(groupADownList, mk.kmeanGroupADown);
            mk.kmeanGroupAOther = getAgvOfA(groupAOtherList, mk.kmeanGroupAOther);
            newOne = mk.kmeanGroupAUp * 1000000 + mk.kmeanGroupADown * 1000
                    + mk.kmeanGroupAOther;
        }

        oldOne = -1;
        newOne = 0;
        while (oldOne - newOne == 0.0f) {
            oldOne = mk.kmeanGroupBeforeUp * 1000000 + mk.kmeanGroupBeforeDown
                    * 1000 + mk.kmeanGroupBeforeOther;
            for (int i = 0; i < inputDataList.size(); i++) {

                float dBeUp = Math.abs(inputDataList.get(i).a
                        - mk.kmeanGroupBeforeUp);
                float dBeDown = Math.abs(inputDataList.get(i).a
                        - mk.kmeanGroupBeforeDown);
                float dBeOther = Math.abs(inputDataList.get(i).a
                        - mk.kmeanGroupBeforeOther);

                if (dBeUp < dBeDown && dBeUp < dBeOther) {
                    groupBeUpList.add(inputDataList.get(i));
                } else if (dBeUp > dBeDown && dBeOther > dBeDown) {
                    groupBeDownList.add(inputDataList.get(i));
                } else {
                    groupBeOtherList.add(inputDataList.get(i));
                }

            }

            mk.kmeanGroupBeforeUp = getAgvOfBefore(groupAfUpList, mk.kmeanGroupBeforeUp);
            mk.kmeanGroupBeforeDown = getAgvOfBefore(groupAfDownList, mk.kmeanGroupBeforeDown);
            mk.kmeanGroupBeforeOther = getAgvOfBefore(groupAfOtherList, mk.kmeanGroupBeforeOther);
            newOne = mk.kmeanGroupBeforeUp * 1000000 + mk.kmeanGroupBeforeDown
                    * 1000 + mk.kmeanGroupBeforeOther;
        }

        oldOne = -1;
        newOne = 0;
        while (oldOne - newOne == 0.0f) {
            oldOne = mk.kmeanGroupAfterUp * 1000000 + mk.kmeanGroupAfterDown
                    * 1000 + mk.kmeanGroupAfterOther;
            for (int i = 0; i < inputDataList.size(); i++) {

                float dAfUp = Math.abs(inputDataList.get(i).a
                        - mk.kmeanGroupAfterUp);
                float dAfDown = Math.abs(inputDataList.get(i).a
                        - mk.kmeanGroupAfterDown);
                float dAfOther = Math.abs(inputDataList.get(i).a
                        - mk.kmeanGroupAfterOther);

                if (dAfUp < dAfDown && dAfUp < dAfOther) {
                    groupAfUpList.add(inputDataList.get(i));
                } else if (dAfUp > dAfDown && dAfOther > dAfDown) {
                    groupAfDownList.add(inputDataList.get(i));
                } else {
                    groupAfOtherList.add(inputDataList.get(i));
                }

            }

            mk.kmeanGroupAfterUp = getAgvOfAfter(groupAfUpList, mk.kmeanGroupAfterUp);
            mk.kmeanGroupAfterDown = getAgvOfAfter(groupAfDownList, mk.kmeanGroupAfterDown);
            mk.kmeanGroupAfterOther = getAgvOfAfter(groupAfOtherList, mk.kmeanGroupAfterOther);

            newOne = mk.kmeanGroupAfterUp * 1000000 + mk.kmeanGroupAfterDown
                    * 1000 + mk.kmeanGroupAfterOther;
        }
    }

    public static float getAgvOfA(List<MEStatus> inputDataList, float defaultValue) {
        if (inputDataList.size() == 0) {
            return defaultValue;
        }
        int tmp = 0;
        for (MEStatus mes : inputDataList) {
            tmp += mes.a;
        }
        return tmp / inputDataList.size();
    }

    public static float getAgvOfBefore(List<MEStatus> inputDataList, float defaultValue) {
        if (inputDataList.size() == 0) {
            return defaultValue;
        }
        int tmp = 0;
        for (MEStatus mes : inputDataList) {
            tmp += mes.before;
        }
        return tmp / inputDataList.size();
    }

    public static float getAgvOfAfter(List<MEStatus> inputDataList, float defaultValue) {
        if (inputDataList.size() == 0) {
            return defaultValue;
        }
        int tmp = 0;
        for (MEStatus mes : inputDataList) {
            tmp += mes.after;
        }
        return tmp / inputDataList.size();
    }

    class HttpRequest extends AsyncTask<String, Void, Boolean> {
        String responseConst = "OTHER";

        protected Boolean doInBackground(String... params) {
            if (params[0] == USER_CONST) {
                responseUsed = apiUserResponse;
            }
            if (params[0] == COMMON_CONST) {
                responseUsed = apiCommonResponse;
            }

            try {
                String url = params[1];
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);

                HttpResponse httpResponse = httpClient.execute(get);
                HttpEntity httpEntity = httpResponse.getEntity();
                if (params[0] == USER_CONST) {
                    apiUserResponse = EntityUtils.toString(httpEntity);
                }
                if (params[0] == COMMON_CONST) {
                    apiCommonResponse = EntityUtils.toString(httpEntity);
                }

            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }

            return true;
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
                _trainningData();
            }
        }
    }

    static class StaticHttpRequest extends AsyncTask<String, Void, Boolean> {
        protected Boolean doInBackground(String... params) {
            try {
                String url = params[1];
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);

                HttpResponse httpResponse = httpClient.execute(get);
                HttpEntity httpEntity = httpResponse.getEntity();
                String response = EntityUtils.toString(httpEntity);

            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }

            return true;
        }
    }

}
