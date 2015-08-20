package com.appology.mannercash.mannercash;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends ActionBarActivity {

    WordDBHelper mHelper;
    SQLiteDatabase db;

    Data[] data;
    LimitSpeed[] limitSpeed;
    JCT[] jct;
    AssetManager assManager;
    InputStream is;
    String str;
    String[] input;
    int index=0;

    private final long FINSH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    DrawerLayout dlDrawer;
    ActionBarDrawerToggle dtToggle;

    ListView lvDrawerList;
    ArrayAdapter<String> adtDrawerList;
    LinearLayout dlLayout;
    String[] menuItems = new String[]{"Home", "포인트 내역", "랭킹", "제휴사 안내", "보호구역 안내"};

    Intent intent;

    ImageView userPhoto;
    TextView carNumber;
    Button infoModify;

    Context mContext;
    MainFunctionTask mainFunctionTask;
    TextView debugTextView;
    TextView countTextView;
    LocationManager locationManager;

    Animation animAppear;
    Animation animDisAppear;

    public static Activity mainActivity;

    TextView speedText;
    TextView pointText;
    ImageView speedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHelper = new WordDBHelper(this);
        db=mHelper.getReadableDatabase();

        mContext = this;
        mainActivity = MainActivity.this;

        getData();  // IC, JCT, LimitSpeed Data 불러와서 저장

        int point = getPoint(); //point 불러옴. (error ㅠㅠ)
        speedText=(TextView)findViewById(R.id.limitspeed);
        speedImage=(ImageView)findViewById(R.id.limitspeedBackground);
        //animAppear = AnimationUtils.loadAnimation(this, R.anim.alpha);
        //animAppear.setFillAfter(true);
        //animDisAppear = AnimationUtils.loadAnimation(this, R.anim.alpha2);
        //animDisAppear.setFillAfter(true);
        //speedImage.startAnimation(animDisAppear);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        pointText=(TextView)findViewById(R.id.point);
        pointText.setText(Integer.toString(point));

        dlLayout = (LinearLayout) findViewById(R.id.dlLayout);
        lvDrawerList = (ListView) findViewById(R.id.lv_activity_main);
        adtDrawerList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuItems);
        lvDrawerList.setAdapter(adtDrawerList);

        lvDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(getApplicationContext(), PointStatusActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(getApplicationContext(), RankingActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                }
                dlDrawer.closeDrawer(dlLayout);
            }
        });

        dlDrawer = (DrawerLayout) findViewById(R.id.dl_activity_main);
        dtToggle = new ActionBarDrawerToggle(this, dlDrawer, R.string.app_name, R.string.app_name);
        dlDrawer.setDrawerListener(dtToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //Drawable actionbarDrawable = getResources().getDrawable(R.drawable.actionbar);
        //getSupportActionBar().setBackgroundDrawable(actionbarDrawable);



        userPhoto = (ImageView) findViewById(R.id.userPhoto);

        carNumber = (TextView) findViewById(R.id.carNumber);

        infoModify = (Button) findViewById(R.id.infoModify);
        infoModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(mContext, InfoModifyActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });


        debugTextView = (TextView) findViewById(R.id.debugTextView);
        countTextView = (TextView) findViewById(R.id.count);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        mainFunctionTask = new MainFunctionTask(mContext, locationManager, debugTextView, data, limitSpeed, jct,
                                                pointText, speedText, speedImage, countTextView, animAppear, animDisAppear);
        mainFunctionTask.execute();
    }

    public void showGpsSwitchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("단말기의 설정에서 '위치 서비스(GPS)' 사용을 허용해 주세요.").
                setCancelable(false).
                setPositiveButton("설정",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(gpsOptionsIntent);
                            }
                        }).
                setNegativeButton("종료",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                finish();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.setTitle("위치 서비스 사용");
        alert.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGpsSwitchDialog();
            Log.i("1111111111", "showGpsSwitchDialog() called");
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        dtToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        dtToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_settings:
                break;
        }

        if(dtToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if( intervalTime >= 0 && intervalTime <= FINSH_INTERVAL_TIME ) {
            super.onBackPressed();
        }
        else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mainFunctionTask != null) {
            mainFunctionTask.cancel(true);
        }
    }

    int getPoint(){

        SharedPreferences settings = mContext.getSharedPreferences("MannerCash", mContext.MODE_PRIVATE);
        int point;
        String id=settings.getString("email", "email");
        String pw=settings.getString("password","password");
        //SharedPreferences.Editor editor = settings.edit();
        //editor.putInt("point", point+1);
        //editor.commit();
        //point=settings.getInt("point", 0);

        db=mHelper.getReadableDatabase();

        Cursor cursor;
        cursor = db.rawQuery("SELECT * FROM user where id='"+id+"'", null);
        if (cursor.moveToFirst()) {
            point = cursor.getInt(2);
        } else {
            db = mHelper.getWritableDatabase();
            db.execSQL("INSERT INTO user VALUES ('"+id+"', '"+pw+"',0);");
            mHelper.close();
            point=0;
        }
        return point;

    }

    void getData(){
        assManager = getApplicationContext().getAssets();
        BufferedReader bufferReader;
        input = new String[6];
        data = new Data[446];
        try {
            is = assManager.open("data.txt");
            bufferReader = new BufferedReader(new InputStreamReader(is));
            while( (str = bufferReader.readLine()) != null ) {
                input = str.split(",");
                data[index]=new Data();
                data[index++].Reset(input[0], input[1], input[2], input[3], Double.parseDouble(input[4]), Double.parseDouble(input[5]));
            }
            is.close();
        }
        catch(IOException ex){
            Toast.makeText(getApplicationContext(), "No File", Toast.LENGTH_LONG).show();
        }

        index=0;
        assManager = getApplicationContext().getAssets();
        input = new String[10];
        limitSpeed = new LimitSpeed[StaticVariable.limitSpeedCount];
        try {
            is = assManager.open("speed.txt");
            bufferReader = new BufferedReader(new InputStreamReader(is));
            while( (str = bufferReader.readLine()) != null ) {
                input = str.split(",");
                limitSpeed[index]=new LimitSpeed();
                limitSpeed[index++].Reset(Integer.parseInt(input[0]),Integer.parseInt(input[1]),input[2],input[3],Double.parseDouble(input[4]),Double.parseDouble(input[5]),Double.parseDouble(input[6]),Double.parseDouble(input[7]), Integer.parseInt(input[8]), Integer.parseInt(input[9]));
            }
            is.close();
        }
        catch(IOException ex){
            Toast.makeText(getApplicationContext(), "(IC)No File", Toast.LENGTH_LONG).show();
        }


        index=0;
        assManager = getApplicationContext().getAssets();
        input = new String[8];
        jct = new JCT[228];
        try {
            is = assManager.open("jct.txt");
            bufferReader = new BufferedReader(new InputStreamReader(is));
            while( (str = bufferReader.readLine()) != null ) {
                input = str.split(",");
                jct[index]=new JCT();
                jct[index++].Reset(Integer.parseInt(input[0]), input[1], input[2], Double.parseDouble(input[3]), Double.parseDouble(input[4]), input[5],input[6], Integer.parseInt(input[7]));
            }
            is.close();
            //Toast.makeText(MainActivity.this,jct[227].getRouteName()+" "+jct[227].getIcCode()+" "+jct[227].getLimitSpeed(),Toast.LENGTH_LONG).show();
        }
        catch(IOException ex){
            Toast.makeText(getApplicationContext(), "(JCT)No File", Toast.LENGTH_LONG).show();
        }
    }
}



class Data{
    String routeName;
    String routeNo;
    String icCode;
    String icName;
    double xValue;
    double yValue;

    void Reset(String _routeName, String _routeNo, String _icCode, String _icName, Double _xValue, Double _yValue){
        routeName=_routeName;
        routeNo = _routeNo;
        icCode=_icCode;
        icName = _icName;
        xValue=_xValue;
        yValue=_yValue;
    }

    String GetrouteName(){
        return routeName;
    }

    String GeticName(){
        return icName;
    }

    String GetrouteNo(){
        return routeNo;
    }

    String GeticCode(){
        return icCode;
    }

    boolean Enter(double _yValue, double _xValue){
        float[] results=new float[3];

        LatLng Data_Point = new LatLng(yValue,xValue);
        LatLng Point = new LatLng(_xValue, _yValue);
        Location.distanceBetween(Data_Point.latitude, Data_Point.longitude, Point.latitude, Point.longitude,results);
        if(icName.charAt(icName.length()-1)=='T') {
            if (results[0] < 350.0f) {
                return true;
            } else
                return false;
        }
        else{
            if (results[0] < 30.0f) {
                return true;
            } else
                return false;
        }
    }
}

class LimitSpeed{
    int code;
    int index;
    String routeName;
    String icName;
    double latValue;
    double lonValue;
    double latValue2;
    double lonValue2;
    int speed1=100;
    int speed2=100;
    void Reset(int _code, int _index, String _routeName, String _icName, double _latValue, double _lonValue, double _latValue2, double _lonValue2, int _speed1, int _speed2){
        code=_code;
        index=_index;
        routeName=_routeName;
        icName=_icName;
        latValue=_latValue;
        lonValue=_lonValue;
        latValue2=_latValue2;
        lonValue2=_lonValue2;
        speed1=_speed1;
        speed2=_speed2;
    }

    float curDistance(double curLat, double curLon){
        float[] results=new float[3];

        LatLng Data_Point = new LatLng(latValue,lonValue);
        LatLng Point = new LatLng(curLat, curLon);
        Location.distanceBetween(Data_Point.latitude, Data_Point.longitude, Point.latitude, Point.longitude, results);

        return results[0];
    }

    float pointDistance(){
        float[] results=new float[3];

        LatLng Data_Point = new LatLng(latValue,lonValue);
        LatLng Point = new LatLng(latValue2, lonValue2);
        Location.distanceBetween(Data_Point.latitude, Data_Point.longitude, Point.latitude, Point.longitude, results);

        return results[0];
    }

}

class JCT{
    int num;
    String icCode;
    String icName;
    Double xValue;
    Double yValue;
    String routeName;
    String routeNo;
    int limitSpeed;

    void Reset(int _num, String _icCode, String _icName, Double _xValue, Double _yValue, String _routeName, String _routeNo, int _limitSpeed){
        num=_num;
        icCode=_icCode;
        icName=_icName;
        xValue=_xValue;
        yValue=_yValue;
        routeName=_routeName;
        routeNo=_routeNo;
        limitSpeed=_limitSpeed;
    }

    int getNum(){   return num;}
    String getIcCode(){ return icCode;}
    String getRouteName(){    return routeName;}
    String getRouteNo(){  return routeNo;}
    int getLimitSpeed(){    return limitSpeed;}

    boolean Enter(Double _yValue, Double _xValue){
        float[] results=new float[3];

        LatLng Data_Point = new LatLng(yValue,xValue);
        LatLng Point = new LatLng(_yValue, _xValue);
        Location.distanceBetween(Data_Point.latitude, Data_Point.longitude, Point.latitude, Point.longitude, results);

        if(results[0]<30) {
            return true;
        }
        else
            return false;
    }
}

class WordDBHelper extends SQLiteOpenHelper {
    public WordDBHelper(Context context){
        super(context, "mannercash.db", null, 3);
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE user (id VARCHAR PRIMARY KEY not null, password VARCHAR not null, point INTEGER);");
        db.execSQL("CREATE TABLE point (id VARCHAR not null, point INTEGER not null, routeName VARCHAR, date DATE, time TIME);");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS point");

        onCreate(db);
    }

}