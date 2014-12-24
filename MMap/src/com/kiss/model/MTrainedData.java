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
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.kiss.core.FileUtil;

@SuppressLint("UseValueOf")
public class MTrainedData {
    public static final String READ_COMMON_FILE_URL = "http://192.168.30.17/php/readapi.php?file=ips_sensors_common";
    public static final String READ_USER_FILE_URL = "http://192.168.30.17/php/readapi.php?file=ips_sensors_user";
    public static final String WRITE_COMMON_FILE_URL = "http://192.168.30.17/php/writeapi.php?file=ips_sensors_common";
    public static final String WRITE_USER_FILE_URL = "http://192.168.30.17/php/writeapi.php?file=ips_sensors_user";
    public static final String DATA_RECORD_FORMAT = "%d %.3f %.3f %.3f %.3f;";
    public static final String USER_CONST = "USER";
    public static final String COMMON_CONST = "COMMON";
    public static int maxLengthOfWinds = 10;

    private Kmean kmean;

    public String apiUserResponse = new String("");
    public String apiCommonResponse = new String("");
    public String responseUsed = apiUserResponse;

    private Context c;

    public static void pushUserDataToServer(Context c, String data) {
        FileUtil.appendFile(c, data, "abc");
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
        GetData task = new GetData();
        task.execute(new String[] {});

        Log.d("StartEnd", "_getApiResponse");
    }

    public MTrainedData(Context c) {
        // _getApiResponse();
        this.c = c;
    }

    public void setMkUserAgain() {
        Log.d("StartEnd", "setMkUserAgain");
        _getApiResponse();
    }

    public MEObservedData getMEObservedData(MEStatus mes) {
        if (kmean == null) {
            return null;
        }
        return kmean.estimateResult(mes);
    }

    private void _trainningData() {
        Log.d("TEST", "TrainningData");
        if (apiUserResponse != "") {
            List<MEStatus> inputDataList = new ArrayList<MEStatus>();
            String[] records = apiUserResponse.trim().split(";");
            for (int i = 0; i < records.length; i++) {
                String record = records[i];
                String[] data = record.split(" ");
                if (data.length > 0) {
                    long time = Long.parseLong(data[0]);
                    float a1 = Float.parseFloat(data[1]);
                    float a2 = Float.parseFloat(data[2]);
                    float a3 = Float.parseFloat(data[3]);
                    float azimuth = Float.parseFloat(data[4]);
                    MEStatus inputData = createMEStatus(a1, a2, a3, time,
                            azimuth);
                    inputDataList.add(inputData);
                }
            }
            kmean = new Kmean(getSimplifyList(inputDataList));
        }
    }

    private List<MEStatus> getSimplifyList(List<MEStatus> inputDataList) {

        for (int i = 0; i < inputDataList.size() - 1; i++) {
            MEStatus mes = new MEStatus(i, inputDataList);
            inputDataList.set(i, mes);
        }
        return inputDataList;
    }

    public static MEStatus createMEStatus(float a1, float a2, float a3,
            long time, float azimuth) {
        int a = Math.round(a1 * a1 + a2 * a2 + a3 * a3);
        return new MEStatus(a, time, azimuth);
    }

    class GetData extends AsyncTask<String, Void, Boolean> {
        String responseConst = "OTHER";

        protected Boolean doInBackground(String... params) {

            apiUserResponse = FileUtil.readFile(MTrainedData.this.c, "abc");

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

    public Kmean getKmean() {
        return kmean;
    }

}
