package com.appology.mannercash.mannercash;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Jeong on 2015-07-01.
 */
public class MainFunctionTask extends AsyncTask<Void, Integer, Void> {

    String id;
    String password;

    Context mContext;

    TextView debugTextView;
    LocationManager locationManager;
    GpsManager locationListener;

    double prevLat = 0;
    double prevLon = 0;
    double curLat = 0;
    double curLon = 0;

    boolean gpsOffFlag = false;

    Data[] data = new Data[446];
    IC[] ic = new IC[36];
    JCT[] jct = new JCT[228];


    public MainFunctionTask(Context mContext, LocationManager locationManager, TextView debugTextView, Data[] data, IC[] ic, JCT[] jct) {
        this.mContext = mContext;
        this.locationManager = locationManager;
        this.debugTextView = debugTextView;
        this.data = data;
        this.ic = ic;
        this.jct=jct;

        SharedPreferences settings = mContext.getSharedPreferences("MannerCash", mContext.MODE_PRIVATE);
        id=settings.getString("email", "email");
        password=settings.getString("password", "password");
        Toast.makeText(mContext.getApplicationContext(), "(회원정보) ID : "+id+" Password : "+password, Toast.LENGTH_LONG).show();
    }

    @Override
    protected Void doInBackground(Void... params) {

        while (isCancelled() == false) {

            soundTurnOn(R.raw.test);    // 사운드 출력 예 -> raw 폴더에 출력할 사운드 파일 넣고 왼쪽과 같이 메소드 호출하면 됨.
/*
            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                gpsOffFlag = true;
                cancel(true);
                ((Activity) mContext).finish();
            }*/

            if(locationListener.getLatitude() != curLat && locationListener.getLongitude() != curLon) {
                prevLat = curLat;
                prevLon = curLon;
                curLat = locationListener.getLatitude();
                curLon = locationListener.getLongitude();
            }


            publishProgress();
            try {
                Thread.sleep(500);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    StringBuilder sb = new StringBuilder("");
    @Override
    protected void onProgressUpdate(Integer... values) {
        sb.append("GPS Enabled" + "\n" + locationListener.isProviderEnabled() + "\n");
        sb.append("GPS 수신 상태" + "\n" + locationListener.getGpsStatus() + "\n");
        sb.append("prevLat:" + prevLat + "\n");
        sb.append("prevLon:" + prevLon + "\n");
        sb.append("curLat:" + curLat + "\n");
        sb.append("curLon:" + curLon + "\n");
        sb.append("위도:" + String.valueOf(locationListener.getLatitude()) + "\n");
        sb.append("경도:" + String.valueOf(locationListener.getLongitude()) + "\n");
        sb.append("속도:" + String.valueOf(locationListener.getMSpeed()) + "\n");
        debugTextView.setText(sb.toString());
        sb.setLength(0);
    }

    boolean Enter(int code, Double x, Double y){    //code==0 : IC, code==1 : JCT
        char flag;
        if(code==0)
            flag='C';
        else
            flag='T';

        for(int i=0;i<446;i++){
            if(data[i].icName.charAt(data[i].icName.length()-1) != flag)
                continue;
            if(data[i].Enter(x,y)){
                return true;
            }
        }
        return false;
    }

    void soundTurnOn(int resId) {
        SoundPool soundPool;
        int soundId;

        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundId = soundPool.load(mContext, resId, 1);

        soundPool.play(soundId, 1, 1, 0, 0, 1);
    }

    private void gpsConfiguration() {
        locationListener = new GpsManager();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 100, locationListener);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationListener.setIsProviderEnabled(true);
        }
    }

    @Override
    protected void onPreExecute() {
        gpsConfiguration();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
    }

    @Override
    protected void onCancelled() {
        if(gpsOffFlag) {
            Toast.makeText(mContext, "위치 서비스(GPS) 기능이 꺼졌습니다.\n사용 허용 후 다시 시작해주세요.", Toast.LENGTH_LONG).show();
        }
        locationManager.removeUpdates(locationListener);
    }

    /*private void request(String urlStr) {
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
                    conn.disconnect();
                }
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }*/
}