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

    boolean gpsOffFlag = false;

    Data[] data = new Data[446];
    IC[] ic = new IC[36];
    JCT[] jct = new JCT[228];

    int index; // index = 진입한 Data정보와 일치하는 index값 저장
    int flag=0; //flag는 현재 IC or JCT를 진입했는지 벗어났는지 chk

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

            publishProgress();
            /*
            if(flag==0) {
                if(index!=999) {
                    for (int i = 0; i < 407; i++) {
                        if (data[i].Enter(locationListener.getLatitude(), locationListener.getLongitude())) { // IC or JCT 에 진입했는지 알아봄
                            //Toast.makeText(mContext.getApplicationContext(), "Entered "+data[i].GetrouteName()+data[i].GetrouteNo()+data[i].GeticName()+data[i].GeticCode(), Toast.LENGTH_LONG).show();
                            index = i;
                            flag = 1;
                            break;
                        }
                    }
                }
                else{
                    for (int i = 0; i < 407; i++) {
                        if(i==index)
                            continue;
                        else {
                            if (data[i].Enter(locationListener.getLatitude(), locationListener.getLongitude())) { // IC or JCT 에 진입했는지 알아봄
                                //Toast.makeText(mContext.getApplicationContext(), "Entered "+data[i].GetrouteName()+data[i].GetrouteNo()+data[i].GeticName()+data[i].GeticCode(), Toast.LENGTH_LONG).show();
                                index = i;
                                flag = 1;
                                break;
                            }
                        }
                    }
                }
            }
            else if(flag==1) {
                if(locationListener.getMSpeed()>100){
                    //제한속도를 지키지 못한 경우 (임의로 기준 속도를 100km/h 로 둠)
                    Toast.makeText(mContext.getApplicationContext(), "제한속도 초과, 포인트 적립 실패", Toast.LENGTH_SHORT).show();
                    flag=0;     // flag 및 index 초기화
                    index=999;
                }
                for (int i = 0; i < 407; i++) {
                    if (i == index)
                        continue;
                    else if (data[i].Enter(locationListener.getLatitude(), locationListener.getLongitude())) { // 다른 IC or JCT 에 진입했는지 알아봄
                        Toast.makeText(mContext.getApplicationContext(), "포인트가 적립되었습니다.", Toast.LENGTH_SHORT).show();
                            //계속 속도를 잘 유지했으면 point 적립

                        /**************************************************************************************************************************************/
                       /*
                        try{
                            String link = "http://10.0.2.2/mannercash_server.php?code=3&id=" +
                                    URLEncoder.encode(id, "UTF-8")+"&password=" +
                                    URLEncoder.encode(password, "UTF-8") + "&phoneNum=" +
                                    URLEncoder.encode("", "UTF-8") + "&cardNum=" +
                                    URLEncoder.encode("", "UTF-8") +"&point=" +
                                    URLEncoder.encode("100", "UTF-8");
                            URL url = new URL(link);
                            HttpClient client = new DefaultHttpClient();
                            HttpGet request = new HttpGet();
                            request.setURI(new URI(link));
                            HttpResponse response = client.execute(request);
                        }
                        catch(Exception e){
                            Toast.makeText(mContext.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        /**************************************************************************************************************************************/
/*
                        char chk=data[index].GeticName().charAt(data[index].GeticName().length());
                                // IcName 의 끝이 C일 경우 IC, T일 경우 JCT 이므로
                        if(chk=='C'){  // IC통과 시 고속도로를 벗어나게 된 것이므로 index 초기화 및 flag=0으로 set
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
            */
            try {
                //Thread.sleep(2000);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

 /*   private void request(String urlStr) {
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

    private void gpsConfiguration() {
        locationListener = new GpsManager();   // 속도 테스트
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, locationListener);   // 속도 테스트
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationListener.setIsProviderEnabled(true);
        }
    }

    void soundTurnOn(int resId) {
        SoundPool soundPool;
        int soundId;

        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundId = soundPool.load(mContext, resId, 1);

        soundPool.play(soundId, 1, 1, 0, 0, 1);
    }

    StringBuilder sb = new StringBuilder("");
    @Override
    protected void onProgressUpdate(Integer... values) {
        sb.append("GPS Enabled" + "\n" + locationListener.isProviderEnabled() + "\n");
        sb.append("GPS 수신 상태" + "\n" + locationListener.getGpsStatus() + "\n");
        sb.append("위도:" + String.valueOf(locationListener.getLatitude()) + "\n");
        sb.append("경도:" + String.valueOf(locationListener.getLongitude()) + "\n");
        sb.append("속도:" + String.valueOf(locationListener.getMSpeed()) + "\n");
        debugTextView.setText(sb.toString());
        sb.setLength(0);
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
        if(gpsOffFlag) {
            Toast.makeText(mContext, "위치 서비스(GPS) 기능이 꺼졌습니다.\n사용 허용 후 다시 시작해주세요.", Toast.LENGTH_LONG).show();
        }
        locationManager.removeUpdates(locationListener);
    }


    boolean Enter(int code, Double x, Double y){    //code==0 : IC, code==1 : JCT
        char flag;
        if(code==0)
            flag='C';
        else
            flag='T';

        for(int i=0;i<446;i++){
            if(data[i].icName.charAt(data[i].icName.length()-1)!=flag)
                continue;
            if(data[i].Enter(x,y)){
                return true;
            }
        }
        return false;
    }
}