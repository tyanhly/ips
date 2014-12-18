package com.kiss.markov;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static final String DATA_RECORD_FORMAT = "%d||%.3f||%.3f||%.3f||%.3f\n";
    public static final String USER_CONST= "USER";
    public static final String COMMON_CONST = "COMMON";
    public static int maxLengthOfWinds = 15;

    public static Markov mkUser ;
    public static Markov mkCommon ;
    public static Markov mkInit;

    public static void pushUserDataToServer(String data){
        StaticHttpRequest task = new StaticHttpRequest();
        try {
            task.execute(new String[] { "PUTDATA", WRITE_USER_FILE_URL + "&p=" + URLEncoder.encode(data, "utf-8") });
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            Log.e("Error", e.getMessage(),e);
            e.printStackTrace();
        }
//        StaticHttpRequest task1 = new StaticHttpRequest();
//        task1.execute(new String[] { "PUTDATA", WRITE_COMMON_FILE_URL+ "&p=" + data });
    }
    private void _getApiResponse() {
        HttpRequest task = new HttpRequest();
        task.execute(new String[] { "USER", READ_USER_FILE_URL });
        HttpRequest task1 = new HttpRequest();
        task1.execute(new String[] { "COMMON", READ_COMMON_FILE_URL });
    }

    public MTrainedData() {
//        _getApiResponse();
    }
    
    private void _trainningData(){
        
        if(apiUserResponse!=""){
            List<InputData> inputDataList = new ArrayList<InputData>();
            String[] records = apiUserResponse.split("\n");
            for(int i =0; i< records.length ; i++){
                String record = records[i];
                String[] data = record.split("||");
                long time = Long.parseLong(data[0]);
                float a1 = Float.parseFloat(data[1]);
                float a2 = Float.parseFloat(data[2]);
                float a3 = Float.parseFloat(data[3]);
                int a = Math.round(a1*a1+a2*a2+a3*a3);
                float azimuth = Float.parseFloat(data[4]);
                
                InputData inputData = new InputData(a, azimuth,time);
                inputDataList.add(inputData);
            }
            mkUser.setStatusSet(inputDataList);
            mkUser.setObservedDataSet() ;
            mkUser.setObservedDataSet() ;
            mkUser.setStatusProbability(inputDataList) ;
        }
        
    }




    

    String apiUserResponse = "";
    String apiCommonResponse= "";

    class HttpRequest extends AsyncTask<String, Void, Boolean> {
        String responseConst = "OTHER"; 
        protected Boolean doInBackground(String... params) {
            String response;
            if (params[0] == USER_CONST) {
                response = apiUserResponse;
            }
            if (params[0] == COMMON_CONST){
                response = apiCommonResponse;
            }

            try {
                String url = params[1];
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);

                HttpResponse httpResponse = httpClient.execute(get);
                HttpEntity httpEntity = httpResponse.getEntity();
                response = EntityUtils
                        .toString(httpEntity);
                
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
            if(result){
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
                String response = EntityUtils
                        .toString(httpEntity);
                
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
    }

}
