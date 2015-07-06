package com.appology.mannercash.mannercash;

import android.content.Context;
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
    TextView textView3;   // 속도 테스트
    TextView textView4;   // 속도 테스트
    Integer requestCount;   // 네트워킹 테스트
    public String Url = "http://m.naver.com";   // 네트워킹 테스트

    LocationManager locationManager;   // 속도 테스트
    GpsManager locationListener;   // 속도 테스트

    Data[] data = new Data[408];

    int index; // index = 진입한 Data정보와 일치하는 index값 저장
    int flag=0; //flag는 현재 IC or JCT를 진입했는지 벗어났는지 chk

    public MainFunctionTask(Context mContext, LocationManager locationManager,
                            TextView textView, TextView textView2, TextView textView3, TextView textView4, Data[] data) {
        this.mContext = mContext;
        this.locationManager = locationManager;
        this.textView = textView;
        this.textView2 = textView2;
        this.textView3 = textView3;
        this.textView4 = textView4;
        this.data = data;

        requestCount = new Integer(0);   // 네트워킹 테스트
    }


    @Override
    protected Void doInBackground(Void... params) {
        while (isCancelled() == false) {

            request(Url);   // 네트워킹 테스트

            publishProgress();

            if(flag==0) {
                for (int i = 0; i < 407; i++) {
                    if (data[i].Enter(locationListener.getLatitude(), locationListener.getLongitude())) { // IC or JCT 에 진입했는지 알아봄
                        //Toast.makeText(mContext.getApplicationContext(), "Entered "+data[i].GetrouteName()+data[i].GetrouteNo()+data[i].GeticName()+data[i].GeticCode(), Toast.LENGTH_LONG).show();
                        index = i;
                        flag=1;
                        break;
                    }
                }
            }
            else if(flag==1) {
                for (int i = 0; i < 407; i++) {
                    if (i == index)
                        continue;
                    else if (data[i].Enter(locationListener.getLatitude(), locationListener.getLongitude())) { // 다른 IC or JCT 에 진입했는지 알아봄
                        //point+=50; //계속 속도를 잘 유지했으면 point 적립,

                        char chk=data[index].GeticName().charAt(data[index].GeticName().length());
                                // IcName 의 끝이 C일 경우 IC, T일 경우 JCT 이므로
                        if(chk=='C'){
                            index=999;  // IC통과 시 고속도로를 벗어나게 된 것이므로 index 초기화 및 flag=0으로 set
                            flag=0;
                        }
                        else if(chk=='T'){  // JCT 통과 시 고속도로를 다시 달리는 것이므로 index 재설정 및 flag 1로 유지
                            index=i;
                            flag=1;
                        }
                        break;
                    }
                }
            }
            try {
                Thread.sleep(2000);   // 네트워킹 테스트
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }

    private void request(String urlStr) {   // 네트워킹 테스트
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

                    requestCount++;   // 네트워킹 테스트

                    conn.disconnect();
                }
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void gpsConfiguration() {
        locationListener = new GpsManager();   // 속도 테스트
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);   // 속도 테스트
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationListener.setIsProviderEnabled(true);
        }
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        textView.setText(requestCount.toString());   // 네트워킹 테스트
        textView2.setText("현재속도 : " + locationListener.getMSpeed());   // 속도 테스트
        textView3.setText("위도 : " + locationListener.getLatitude() + "\n" + "경도 : " + locationListener.getLongitude());
        textView4.setText("GPS Enabled" + "\n" + locationListener.isProviderEnabled() + "\n\n" +
                            "GPS 수신 상태" + "\n" + locationListener.getGpsStatus());
/*        Toast.makeText(mContext.getApplicationContext(), "속도:" + locationListener.getMSpeed() +
                                                        "\n위도:" + locationListener.getLatitude() +
                                                        "\n경도:" + locationListener.getLongitude(), Toast.LENGTH_SHORT).show();*/
    }

    @Override
    protected void onPreExecute() {
        gpsConfiguration();   // 속도 테스트
    }

    @Override
    protected void onPostExecute(Void aVoid) {
    }

    @Override
    protected void onCancelled() {
        locationManager.removeUpdates(locationListener);
    }


}

