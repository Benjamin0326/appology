package com.appology.mannercash.mannercash;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.widget.TextView;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jeong on 2015-07-01.
 */
public class MainFunctionTask extends AsyncTask<Void, Integer, Void> {

    Context mContext;

    TextView textView;   // 네트워킹 테스트
    TextView textView2;   // 속도 테스트
    int value;   // 네트워킹 테스트
    int count;   // 네트워킹 테스트
    int mSpeed;   // 속도 테스트
    public String Url = "http://m.naver.com";   // 네트워킹 테스트

    LocationManager locationManager;   // 속도 테스트
    LocationListener locationListener;   // 속도 테스트


    public MainFunctionTask(Context mContext, TextView textView, TextView textView2) {
        this.mContext = mContext;
        this.textView = textView;
        this.textView2 = textView2;

        value = 0;   // 네트워킹 테스트
        count = 0;   // 네트워킹 테스트
        mSpeed = 0;   // 속도 테스트
    }

    @Override
    protected Void doInBackground(Void... params) {
        while (isCancelled() == false) {

            value = request(Url);   // 네트워킹 테스트

            publishProgress(value, mSpeed);

            try {
                Thread.sleep(200);   // 네트워킹 테스트
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }

    private int request(String urlStr) {   // 네트워킹 테스트
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            if (conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                int resCode = conn.getResponseCode();
                if (resCode == HttpURLConnection.HTTP_OK) {

                    count++;   // 네트워킹 테스트

                    conn.disconnect();
                }
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        return count;
    }

    private void gpsSpeedRequest() {
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);   // 속도 테스트
        locationListener = new GpsManager(mSpeed);   // 속도 테스트
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);   // 속도 테스트
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        textView.setText(values[0].toString());   // 네트워킹 테스트
        textView2.setText("현재속도 : " + values[1].toString());   // 속도 테스트
    }

    @Override
    protected void onPreExecute() {
        gpsSpeedRequest();   // 속도 테스트
    }

    @Override
    protected void onPostExecute(Void aVoid) {
    }

    @Override
    protected void onCancelled() {
    }
}
