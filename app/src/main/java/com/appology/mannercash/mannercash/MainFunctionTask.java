package com.appology.mannercash.mannercash;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

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
    GpsManager speedListener;

    double prevLat = 0;
    double prevLon = 0;
    double curLat = 0;
    double curLon = 0;

    float totalDistance = 0.0f;

    boolean gpsOffFlag = false;
    boolean showGpsDialog = false;

    Data[] data = new Data[446];
    LimitSpeed[] limitSpeed = new LimitSpeed[36];
    JCT[] jct = new JCT[228];


    public MainFunctionTask(Context mContext, LocationManager locationManager, TextView debugTextView, Data[] data, LimitSpeed[] limitSpeed, JCT[] jct) {
        this.mContext = mContext;
        this.locationManager = locationManager;
        this.debugTextView = debugTextView;
        this.data = data;
        this.limitSpeed = limitSpeed;
        this.jct=jct;

        SharedPreferences settings = mContext.getSharedPreferences("MannerCash", mContext.MODE_PRIVATE);
        id=settings.getString("email", "email");
        password=settings.getString("password", "password");
        Toast.makeText(mContext.getApplicationContext(), "(회원정보) ID : "+id+" Password : "+password, Toast.LENGTH_LONG).show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        while(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        }

        while (isCancelled() == false) {
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                soundTurnOn(R.raw.test);    // 사운드 출력 예 -> raw 폴더에 출력할 사운드 파일 넣고 왼쪽과 같이 메소드 호출하면 됨.
                setLatLon();


                publishProgress();
                try {
                    Thread.sleep(500);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                if(!gpsOffFlag) {
                    showGpsDialog = true;
                    publishProgress();
                }
                gpsOffFlag = true;
            }
        }
        return null;
    }

    StringBuilder sb = new StringBuilder("");
    @Override
    protected void onProgressUpdate(Integer... values) {
        sb.append("GPS Enabled:" + locationListener.isProviderEnabled() + "\n");
        sb.append("GPS 수신 상태" + "\n" + locationListener.getGpsStatus() + "\n");
        sb.append("prevLat:" + prevLat + "\n");
        sb.append("prevLon:" + prevLon + "\n");
        sb.append("curLat:" + curLat + "\n");
        sb.append("curLon:" + curLon + "\n");
        sb.append("위도:" + String.valueOf(locationListener.getLatitude()) + "\n");
        sb.append("경도:" + String.valueOf(locationListener.getLongitude()) + "\n");
        sb.append("속도:" + String.valueOf(speedListener.getMSpeed()) + "\n");
        debugTextView.setText(sb.toString());
        sb.setLength(0);

        if(showGpsDialog) {
            showGpsDialog();
        }
    }

    boolean Enter(int code, Double x, Double y){    //code==0 : IC, code==1 : JCT
        char flag;
        if(code == 0)
            flag = 'C';
        else
            flag = 'T';

        for(int i = 0; i < 446; i++) {
            if(data[i].icName.charAt(data[i].icName.length() - 1) != flag)
                continue;
            if(data[i].Enter(x, y)) {
                return true;
            }
        }
        return false;
    }

    void getDistance() {
        float[] results = new float[3];
        LatLng Data_Point = new LatLng(prevLat, prevLon);
        LatLng Point = new LatLng(curLat, curLon);
        Location.distanceBetween(Data_Point.latitude, Data_Point.longitude, Point.latitude, Point.longitude, results);
        totalDistance += results[0];
    }

    void setLatLon() {
        if(locationListener.getLatitude() != curLat && locationListener.getLongitude() != curLon) {
            prevLat = curLat;
            prevLon = curLon;
            curLat = locationListener.getLatitude();
            curLon = locationListener.getLongitude();
        }
    }

    void soundTurnOn(int resId) {
        SoundPool soundPool;
        int soundId;

        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundId = soundPool.load(mContext, resId, 1);

        soundPool.play(soundId, 1, 1, 0, 0, 1);
    }

    public void showGpsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("'위치 서비스(GPS)' 사용 해제로 인하여 서비스를 종료합니다.").
                setCancelable(false).
                setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                ((Activity) mContext).finish();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.setTitle("위치 서비스 사용");
        alert.show();
    }

    private void gpsConfiguration() {
        locationListener = new GpsManager();
        speedListener = new GpsManager();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 300, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, speedListener);
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
        locationManager.removeUpdates(locationListener);
        locationManager.removeUpdates(speedListener);
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