package com.appology.mannercash.mannercash;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends ActionBarActivity {

    Data[] data;
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

    ToggleButton toggleButton;
    Context mContext;
    MainFunctionTask mainFunctionTask;
    TextView textView;   // 네트워킹 테스트

    ImageView imageView;
    LocationManager locationManager;
    TextView textView2;   // 속도 테스트
    TextView textView3;   // 속도 테스트
    TextView textView4;

    public static Activity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        mainActivity = MainActivity.this;


///////////////////////// data /////////////////////////////////////////////////////////////////////
        assManager = getApplicationContext().getAssets();
        BufferedReader bufferReader;
        input = new String[6];
        data = new Data[408];
        try {
            is = assManager.open("data.txt");
            bufferReader = new BufferedReader(new InputStreamReader(is));
            while( (str = bufferReader.readLine()) != null ) {
                input = str.split(",");
                data[index]=new Data();
                data[index++].Reset(input[0], input[1], input[2], input[3], Double.parseDouble(input[4]), Double.parseDouble(input[5]));
            }
            data[407]=new Data();
            data[407].Reset("TestRouteName", "TestRouteNo", "TestIcCode", "TestIcName", 0.0, 0.0);
        }
        catch(IOException ex){
            Toast.makeText(getApplicationContext(), "No File", Toast.LENGTH_LONG).show();
        }


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

        userPhoto = (ImageView) findViewById(R.id.userPhoto);

        carNumber = (TextView) findViewById(R.id.carNumber);

        infoModify = (Button) findViewById(R.id.infoModify);
        infoModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(mContext, InfoModifyActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                finish();
            }
        });


        textView = (TextView) findViewById(R.id.text_view);   // 네트워킹 테스트

        textView.setOnClickListener(new View.OnClickListener(){    // 로그인 및 튜토리얼 정보 초기화
            @Override
            public void onClick(View v){
                SharedPreferences settings = getSharedPreferences("MannerCash", MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("logged", "");
                editor.putString("email", "");
                editor.putString("password", "");
                editor.putString("tutorialRead", "");
                editor.commit();
                Toast.makeText(getApplicationContext(), "로그인 및 튜토리얼 정보 초기화", Toast.LENGTH_LONG).show();
            }
        });

        textView2 = (TextView) findViewById(R.id.speed);   // 속도 테스트
        textView3 = (TextView) findViewById(R.id.textView3);   // 속도 테스트
        textView4 = (TextView) findViewById(R.id.textView4);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);   // 속도 테스트



        imageView = (ImageView) findViewById(R.id.gpsImage);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGpsSwitchDialog();
            }
        });

        toggleButton = (ToggleButton) findViewById(R.id.toggle_button);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    if (toggleButton.isChecked()) {
                        mainFunctionTask = new MainFunctionTask(mContext, locationManager,
                                                                textView, textView2, textView3, textView4,
                                                                toggleButton, data);
                        mainFunctionTask.execute();
                    } else {
                        mainFunctionTask.cancel(true);
                    }
                } else {
                    toggleButton.setChecked(false);
                    showGpsSwitchDialog();
                }

            }
        });
    }


    public void showGpsSwitchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        if(!(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))) {
            builder.setMessage("단말기의 설정에서 '위치 서비스(GPS)' 사용을 허용해 주세요.").
                    setCancelable(false).
                    setPositiveButton("설정하기",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(gpsOptionsIntent);
                                }
                            }).
                    setNegativeButton("취소",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.setTitle("위치 서비스 사용");
            alert.show();
        } else {
            builder.setMessage("현재 '위치 서비스(GPS)' 사용이 허용된 상태입니다.").
                    setCancelable(false).
                    setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
            AlertDialog alert = builder.create();
            alert.setTitle("위치 서비스 사용");
            alert.show();
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
}


class Data{
    String routeName;
    String routeNo;
    String icCode;
    String icName;
    Double xValue;
    Double yValue;

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

    boolean Enter(Double _xValue, Double _yValue){
        float[] results=new float[3];

        LatLng Data_Point = new LatLng(xValue,yValue);
        LatLng Point = new LatLng(_xValue, _yValue);
        Location.distanceBetween(Data_Point.latitude, Data_Point.longitude, Point.latitude, Point.longitude, results);

        if(results[0]<10) {
            return true;
        }
        else
            return false;
    }
}