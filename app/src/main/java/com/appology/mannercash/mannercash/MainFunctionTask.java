package com.appology.mannercash.mannercash;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.skp.openplatform.android.sdk.api.APIRequest;
import com.skp.openplatform.android.sdk.common.PlanetXSDKConstants;
import com.skp.openplatform.android.sdk.common.PlanetXSDKException;
import com.skp.openplatform.android.sdk.common.RequestBundle;
import com.skp.openplatform.android.sdk.common.RequestListener;
import com.skp.openplatform.android.sdk.common.ResponseMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jeong on 2015-07-01.
 */
public class MainFunctionTask extends AsyncTask<Void, Integer, Void> {

    String id;
    String password;

    Context mContext;

    TextView pointText;
    TextView speedText;
    ImageView speedImage;
    ImageView speedImage2;
    ImageView speedExceed;

    TextView debugTextView;
    LocationManager locationManager;
    GpsManager locationListener;
    GpsManager speedListener;

    double prevLat = 0;
    double prevLon = 0;
    double curLat = 0;
    double curLon = 0;

    double roadStartLat = 0;
    double roadStartLon = 0;

    int[] directionData;
    int directionIndex = -1;
    int forwardCount = 0;
    int reverseCount = 0;
    int direction = 0;
    static final int forwardDirection = 1;
    static final int reverseDirection = 2;
    int lineIndex = 0;
    int changeRoadCheckCount =0;
    int speedLimit = 0;
    int tmpSpeedLimit = 0;
    int exitCount = 0;
    int speedCount = 6;

    float roadStartToCurDistance = 0.0f;
    float changePointDistance = 0.0f;
    float prevDistance = 0.0f;
    float curDistance = 0.0f;
    float accumulateDistance = 0.0f;

    boolean gpsOffFlag = false;
    boolean showGpsDialog = false;
    boolean requestComplete = false;
    boolean locationUpdateFlag = false;
    boolean isExpressWay = false;
    boolean isChangeRoadName = false;
    boolean isPointSave = false;
    boolean isSpeedExceed = false;
    boolean isSetImageExpressWay = false;

    Data[] data;
    LimitSpeed[] limitSpeed;
    JCT[] jct;

    APIRequest api;
    RequestBundle requestBundle;
    String tmapApiUrl = "";
    String hndResult = "";
    String roadName = "";
    String prevRoadName = "";

    SoundPool soundPool;

    CountThread countThread;
    Handler mHandler;

    int j = 0;


    public MainFunctionTask(Context mContext, LocationManager locationManager, TextView debugTextView, Data[] data, LimitSpeed[] limitSpeed, JCT[] jct,
                            TextView pointText, TextView speedText, ImageView speedImage,ImageView speedImage2, ImageView speedExceed) {
        this.mContext = mContext;
        this.locationManager = locationManager;
        this.debugTextView = debugTextView;
        this.pointText=pointText;
        this.speedText=speedText;
        this.speedImage=speedImage;
        this.speedImage2=speedImage2;
        this.data = data;
        this.limitSpeed = limitSpeed;
        this.jct=jct;
        this.speedExceed = speedExceed;

        directionData = new int[StaticVariable.directionDataCount];
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        mHandler = new Handler();

        SharedPreferences settings = mContext.getSharedPreferences("MannerCash", mContext.MODE_PRIVATE);
        id=settings.getString("email", "email");
        password=settings.getString("password", "password");
        //Toast.makeText(mContext.getApplicationContext(), "(회원정보) ID : "+id+" Password : "+password, Toast.LENGTH_LONG).show();
    }

    /*int whileCount = -1;   // test
    class MyLatLng {   // test
        double lat;   // test
        double lng;   // test

        MyLatLng(double lat, double lng) {   // test
            this.lat = lat;   // test
            this.lng = lng;   // test
        }   // test
    }   // test*/

    @Override
    protected Void doInBackground(Void... params) {
        while(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) { }

        /*MyLatLng[] m = new MyLatLng[5];   // test
        m[0] = new MyLatLng(37.515350, 127.015664);   // test
        m[1] = new MyLatLng(37.516541, 127.016093);   // test
        m[2] = new MyLatLng(37.518277, 127.016973);   // test
        m[3] = new MyLatLng(37.519316, 127.017939);   // test
        m[4] = new MyLatLng(37.519911, 127.018389);   // test*/

        while (isCancelled() == false) {
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                publishProgress();
                try {
                    Thread.sleep(1000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                /*if(whileCount >= 3) {   // test
                    break;   // test
                }   // test
                whileCount++;   // test
                prevLat = m[whileCount].lat;   // test
                prevLon = m[whileCount].lng;   // test
                curLat = m[whileCount+1].lat;   // test
                curLon = m[whileCount+1].lng;   // test*/

                setLatLon();

                if(!locationUpdateFlag) {
                    continue;
                }

                initVariable();

                requestRoadName();
                while(!requestComplete) { }

                if(roadName.contains("고속도로")) {
                    isExpressWay = true;
                } else {
                    exitCount++;
                    initDirectionData();
                }

                if(isExpressWay) {
                    getAccumulateDistance();
                    if(accumulateDistance >= StaticVariable.pointSaveStandardDistance) {
                        isPointSave = true;
                    }

                    if(isChangeRoadName) {
                        if(changeRoadCheckCount < StaticVariable.changeRoadStandByCount) {
                            changeRoadCheckCount++;
                            continue;
                        }
                        initDirectionData();
                    }

                    for(lineIndex = 0; lineIndex < StaticVariable.limitSpeedCount-1; lineIndex++) {
                        if(roadName.equals(limitSpeed[lineIndex].routeName)) {
                            break;
                        }
                    }

                    if(limitSpeed[lineIndex].code == 0) {
                        speedLimit = limitSpeed[lineIndex].speed1;
                    } else {
                        for(j = 0; j < limitSpeed[lineIndex].index; j++) {
                            roadStartLat = limitSpeed[lineIndex+j].latValue;
                            roadStartLon = limitSpeed[lineIndex+j].lonValue;

                            roadStartToCurDistance = calculDistance(roadStartLat, roadStartLon, curLat, curLon);
                            changePointDistance = calculDistance(roadStartLat, roadStartLon,
                                    limitSpeed[lineIndex + j].latValue2, limitSpeed[lineIndex + j].lonValue2);

                            //Log.i("mannercash", j + " " + roadStartToCurDistance + " " + changePointDistance);
                            if(roadStartToCurDistance <= changePointDistance) {
                                lineIndex += j;
                                break;
                            }
                        }

                        if(j == limitSpeed[lineIndex].index) {
                            Log.i("mannercash", String.valueOf(j));
                            continue;
                        }

                        if(limitSpeed[lineIndex].code == 1) {
                            speedLimit = limitSpeed[lineIndex].speed1;
                        } else {
                            prevDistance = calculDistance(roadStartLat, roadStartLon, prevLat, prevLon);
                            curDistance = calculDistance(roadStartLat, roadStartLon, curLat, curLon);

                            if(directionIndex != StaticVariable.directionDataCount-1) {
                                directionIndex++;
                            } else {
                                directionIndex = 0;
                            }

                            if(prevDistance <= curDistance) {
                                if(directionData[directionIndex] == forwardDirection) {
                                } else if(directionData[directionIndex] == reverseDirection) {
                                    forwardCount++;
                                    reverseCount--;
                                } else {
                                    forwardCount++;
                                }
                                directionData[directionIndex] = forwardDirection;
                            } else {
                                if(directionData[directionIndex] == forwardDirection) {
                                    forwardCount--;
                                    reverseCount++;
                                } else if(directionData[directionIndex] == reverseDirection) {
                                } else {
                                    reverseCount++;
                                }
                                directionData[directionIndex] = reverseDirection;
                            }

                            if((forwardCount + reverseCount) != 0) {
                                if((forwardCount / (forwardCount + reverseCount)) >= 0.5) {
                                    direction = forwardDirection;
                                    speedLimit = limitSpeed[lineIndex].speed1;
                                    Log.i("mannercash", "speedLimit = speed1");
                                } else {
                                    direction = reverseDirection;
                                    speedLimit = limitSpeed[lineIndex].speed2;
                                    Log.i("mannercash", "speedLimit = speed2");
                                }
                            }
                        }
                    }

                    if(speedListener.getMSpeed() > speedLimit) {
                        Log.i("mannercash", "속도 초과 - " + String.valueOf(speedCount));

                        if(!isSpeedExceed) {
                            isSpeedExceed = true;
                            countThread = new CountThread(true);
                            countThread.start();
                            Log.i("mannercash", "CountTask execute");
                        }
                    } else {
                        if(countThread != null) {
                            Log.i("mannercash", "카운트 중지");
                            countThread.stopThread(false);
                            countThread = null;
                            soundTurnOn(R.raw.decreasecomplete);
                        }
                        isSpeedExceed = false;
                    }

                    if(tmpSpeedLimit != speedLimit) {
                        Log.i("mannercash", "change speedLimit");
                        switch (speedLimit) {
                            case 70:
                                soundTurnOn(R.raw.seventy);
                                break;
                            case 80:
                                soundTurnOn(R.raw.eighty);
                                break;
                            case 90:
                                soundTurnOn(R.raw.ninety);
                                break;
                            case 100:
                                soundTurnOn(R.raw.hundred);
                                break;
                            case 110:
                                soundTurnOn(R.raw.hundredten);
                                break;
                            default:
                                break;
                        }
                        tmpSpeedLimit = speedLimit;
                    }
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
        if(isExpressWay) {
            enter(speedLimit);
            if(isPointSave) {
                savePoint(accumulateDistance, roadName);
                isPointSave = false;
                accumulateDistance = 0;
            }
            exitCount = 0;
        } else {
            Log.i("mannercash", "exit() called, " + "roadName:" + roadName +
                    " / " + prevLat + " / " + prevLon + " / " + curLat + " / " + curLon);

            if(exitCount > 1) {
                exit();
                if(accumulateDistance != 0) {
                    savePoint(accumulateDistance, prevRoadName);
                    accumulateDistance = 0;
                }
            }
        }

        if(!isSpeedExceed) {
            speedExceed.setVisibility(View.INVISIBLE);
        }

        sb.append("GPS Enabled:" + locationListener.isProviderEnabled() + "\n");
        //sb.append("GPS 수신 상태" + "\n" + locationListener.getGpsStatus() + "\n");
        sb.append("현재속도:" + String.valueOf(speedListener.getMSpeed()) + "\n");
        sb.append("speedLimit:" + String.valueOf(speedLimit) + "\n");
        sb.append("도로:" + roadName + "\n");
        sb.append("isExpressWay:" + isExpressWay + "\n");
        sb.append("lineIndex:" + String.valueOf(lineIndex) + "\n\n");
        sb.append("accumulateDistance:" + String.valueOf(accumulateDistance) + "\n\n");
        sb.append("changeRoadCheckCount:" + String.valueOf(changeRoadCheckCount) + "\n");
        sb.append("prevLat:" + prevLat + "\n");
        sb.append("prevLon:" + prevLon + "\n");
        sb.append("curLat:" + curLat + "\n");
        sb.append("curLon:" + curLon + "\n");
        sb.append("roadStartLat:" + String.valueOf(roadStartLat) + "\n");
        sb.append("roadStartLon:" + String.valueOf(roadStartLon) + "\n");
        sb.append("roadStartToCurDistance:" + String.valueOf(roadStartToCurDistance) + "\n");
        sb.append("changePointDistance:" + String.valueOf(changePointDistance) + "\n\n");
        sb.append("prevDistance:" + String.valueOf(prevDistance) + "\n");
        sb.append("curDistance:" + String.valueOf(curDistance) + "\n");
        sb.append("direction:" + String.valueOf(direction) + "\n");
        if((forwardCount + reverseCount) != 0) {
            sb.append("정방향%:" + String.valueOf((forwardCount / (forwardCount + reverseCount)) * 100) + "\n");
        } else {
            sb.append("정방향%:" + "\n");
        }
        sb.append("directionIndex:" + String.valueOf(directionIndex) + "\n");
        sb.append("forwardCount:" + String.valueOf(forwardCount) + "\n");
        sb.append("reverseCount:" + String.valueOf(reverseCount) + "\n");
        sb.append("directionData:" + "\n");
        for(int i = 0; i < StaticVariable.directionDataCount; i++) {
            sb.append(String.valueOf(directionData[i]) + " ");
            if(i % 5 == 4) {
                sb.append("/ ");
            }
            if(i % 20 == 19) {
                sb.append("\n");
            }
        }
        debugTextView.setText(sb.toString());
        sb.setLength(0);

        if(showGpsDialog) {
            showGpsDialog();
        }
    }

    class CountThread extends Thread {

        boolean isExecute;

        CountThread(boolean isExecute) {
            this.isExecute = isExecute;
        }

        void stopThread(boolean isExecute) {
            this.isExecute = isExecute;
        }

        @Override
        public void run() {
            super.run();
            speedCount = 5;
            try {
                Thread.sleep(100);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            soundTurnOn(R.raw.exceed);
            while(speedCount > 0 && isExecute) {
                try {
                    Thread.sleep(1000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                Log.i("mannercash", "speedCount = " + String.valueOf(speedCount));
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isSpeedExceed) {
                            speedExceed.setVisibility(View.VISIBLE);
                        } else {
                            speedExceed.setVisibility(View.INVISIBLE);
                        }
                    }
                });
                soundTurnOn(R.raw.exceedwarning);
                speedCount--;
            }
            isSpeedExceed = false;
        }
    }

    void initVariable() {
        roadStartLat = 0;
        roadStartLon = 0;
        roadStartToCurDistance = 0;
        changePointDistance = 0;
        lineIndex = 0;
        prevDistance = 0;
        curDistance = 0;
        locationUpdateFlag = false;
        isExpressWay = false;
    }

    void initDirectionData() {
        directionIndex = -1;
        for(int i = 0; i < StaticVariable.directionDataCount; i++) {
            directionData[i] = 0;
        }
        changeRoadCheckCount = 0;
        forwardCount = 0;
        reverseCount = 0;
        direction = 0;
        isChangeRoadName = false;
    }

    void requestRoadName() {
        requestComplete = false;

        setTmapApiUrl();

        api = new APIRequest();
        APIRequest.setAppKey("d269e7ae-48c6-3b25-b61d-0fbf564eb865");

        requestBundle = new RequestBundle();
        requestBundle.setUrl(tmapApiUrl);
        requestBundle.setHttpMethod(PlanetXSDKConstants.HttpMethod.GET);
        requestBundle.setRequestType(PlanetXSDKConstants.CONTENT_TYPE.JSON);
        requestBundle.setResponseType(PlanetXSDKConstants.CONTENT_TYPE.JSON);

        try {
            api.request(requestBundle, reqListener);
        } catch (PlanetXSDKException e) {
            e.printStackTrace();
        }
    }

    void setTmapApiUrl() {
        tmapApiUrl = "https://apis.skplanetx.com/tmap/multiViaPointRoute?" +
                    "startX=" + String.valueOf(prevLon) +
                    "&startY=" + String.valueOf(prevLat) +
                    "&endX=" + String.valueOf(curLon) +
                    "&endY=" + String.valueOf(curLat) +
                    "&passList=&reqCoordType=WGS84GEO" +
                    "&callback=&endPoiId=&bizAppId=1af75c43-93f7-4fd6-b8ad-6f1d7debc519&endRpFlag=&resCoordType=WGS84GEO&version=1";
    }

    Handler msgHandler = new Handler(){
        @Override
        public void dispatchMessage(Message msg) { requestComplete = true; }
    };

    RequestListener reqListener = new RequestListener() {
        @Override
        public void onPlanetSDKException(PlanetXSDKException e) {
            hndResult = e.toString();
            msgHandler.sendEmptyMessage(0);
        }

        @Override
        public void onComplete(ResponseMessage result) {
            hndResult = result.toString();
            parseJSON();
            msgHandler.sendEmptyMessage(0);
        }
    };

    void parseJSON() {
        try {
            JSONObject jsonObject = new JSONObject(hndResult);
            JSONArray features = jsonObject.getJSONArray("features");

            for (int i = 0; i < features.length(); i++) {
                JSONObject insideObj = features.getJSONObject(i);
                JSONObject properties = insideObj.getJSONObject("properties");
                if(properties.has("nextRoadName")) {
                    if(!roadName.equals(properties.getString("nextRoadName"))) {
                        prevRoadName = roadName;
                        roadName = properties.getString("nextRoadName");
                        changeRoadCheckCount = 0;
                        isChangeRoadName = true;
                    }
                    break;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void getAccumulateDistance() {
        float results = 0;
        results = calculDistance(prevLat, prevLon, curLat, curLon);
        accumulateDistance += results;
    }

    float calculDistance(double startLat, double startLon, double endLat, double endLon) {
        float[] results = new float[3];
        LatLng startPoint = new LatLng(startLat, startLon);
        LatLng endPoint = new LatLng(endLat, endLon);
        Location.distanceBetween(startPoint.latitude, startPoint.longitude, endPoint.latitude, endPoint.longitude, results);

        return (results[0] / 1000);
    }

    void enter(int speed){
        if(!isChangeRoadName) {
            if(!isSetImageExpressWay) {
                isSetImageExpressWay = true;
                speedImage.setVisibility(View.INVISIBLE);
                speedImage2.setVisibility(View.VISIBLE);
            }
            speedText.setText(String.valueOf(speed));
        }
    }

    void exit(){
        if(isSetImageExpressWay) {
            isSetImageExpressWay = false;
            speedImage.setVisibility(View.VISIBLE);
            speedImage2.setVisibility(View.INVISIBLE);
        }
        speedText.setText("");
        speedLimit = 0;
    }

    void savePoint(float distance, String RouteName){
        SharedPreferences settings = mContext.getSharedPreferences("MannerCash", mContext.MODE_PRIVATE);
        String id=settings.getString("email", "email");
        WordDBHelper mHelper = new WordDBHelper(MainActivity.mainActivity);
        SQLiteDatabase db=mHelper.getReadableDatabase();

        int curPoint;
        Cursor cursor;
        cursor = db.rawQuery("SELECT * FROM user where id='"+id+"'", null);
        if (cursor.moveToFirst()) {
            curPoint = cursor.getInt(2);
            Log.i("mannercash", String.valueOf(curPoint));
        }
        else{
            curPoint=0;
        }

        int savePoint;
        savePoint = (int)(distance*StaticVariable.pointSaveUnit);
        curPoint = curPoint + savePoint;

        db = mHelper.getWritableDatabase();
        db.execSQL("UPDATE user set Point=" + curPoint + " where ID='" + id + "';");

        pointText.setText(String.valueOf(curPoint));

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat CurTimeFormat = new SimpleDateFormat("HH:mm:ss");
        String strCurDate = CurDateFormat.format(date);
        String strCurTime = CurTimeFormat.format(date);

        db = mHelper.getWritableDatabase();
        db.execSQL("INSERT INTO point VALUES ('"+id+"',"+savePoint+",'"+RouteName+"','"+strCurDate+"','"+strCurTime+"');");
        mHelper.close();

        soundTurnOn(R.raw.pointsave);
    }

    void setLatLon() {
        if(locationListener.getLatitude() != curLat && locationListener.getLongitude() != curLon) {
            prevLat = curLat;
            prevLon = curLon;
            curLat = locationListener.getLatitude();
            curLon = locationListener.getLongitude();
            locationUpdateFlag = true;
        }
    }

    void soundTurnOn(int resId) {
        int soundId;
        soundId = soundPool.load(mContext, resId, 1);
        soundPool.play(soundId, 1, 1, 0, 0, 1);
    }

    private void gpsConfiguration() {
        locationListener = new GpsManager();
        speedListener = new GpsManager();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, StaticVariable.gpsUpdateDistance, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, speedListener);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationListener.setIsProviderEnabled(true);
        }
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

    @Override
    protected void onPreExecute() {
        gpsConfiguration();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
    }

    @Override
    protected void onCancelled() {
        if(locationListener!=null)
            locationManager.removeUpdates(locationListener);
        if(speedListener!=null)
            locationManager.removeUpdates(speedListener);
        if(countThread != null) {
            countThread.stopThread(false);
            countThread = null;
        }
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