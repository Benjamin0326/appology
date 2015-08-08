package com.appology.mannercash.mannercash;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
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

import com.skp.openplatform.android.sdk.api.APIRequest;
import com.skp.openplatform.android.sdk.common.PlanetXSDKConstants;
import com.skp.openplatform.android.sdk.common.PlanetXSDKException;
import com.skp.openplatform.android.sdk.common.RequestBundle;
import com.skp.openplatform.android.sdk.common.RequestListener;
import com.skp.openplatform.android.sdk.common.ResponseMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class RankingActivity extends ActionBarActivity {

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

    TextView tvResult;
    Button btn;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        mContext = this;

        dlLayout = (LinearLayout) findViewById(R.id.dlLayout);
        lvDrawerList = (ListView) findViewById(R.id.lv_activity_ranking);
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

        dlDrawer = (DrawerLayout) findViewById(R.id.dl_activity_ranking);
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
            }
        });


        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test();
            }
        });

        tvResult = (TextView) findViewById(R.id.tv);
    }


    APIRequest api;
    RequestBundle requestBundle;
    String URL = "https://apis.skplanetx.com/tmap/multiViaPointRoute?" +
            "startX=" + "126.891398" +
            "&startY=" + "37.459036" +
            "&endX=" + "126.891794" +
            "&endY=" + "37.458387" +
            "&passList=&reqCoordType=WGS84GEO" +
            "&callback=&endPoiId=&bizAppId=1af75c43-93f7-4fd6-b8ad-6f1d7debc519&endRpFlag=&resCoordType=WGS84GEO&version=1";

    void test() {
        api = new APIRequest();
        APIRequest.setAppKey("d269e7ae-48c6-3b25-b61d-0fbf564eb865");

        requestBundle = new RequestBundle();
        requestBundle.setUrl(URL);
        requestBundle.setHttpMethod(PlanetXSDKConstants.HttpMethod.GET);
        requestBundle.setRequestType(PlanetXSDKConstants.CONTENT_TYPE.JSON);
        requestBundle.setResponseType(PlanetXSDKConstants.CONTENT_TYPE.JSON);

        try {
            api.request(requestBundle, reqListener);
        } catch (PlanetXSDKException e) {
            e.printStackTrace();
        }
    }

    String hndResult = "";
    String roadName = "";
    Handler msgHandler = new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            tvResult.setText(roadName);
        }
    };

    RequestListener reqListener = new RequestListener() {
        @Override
        public void onPlanetSDKException(PlanetXSDKException e) {
            hndResult = e.toString();
            msgHandler.sendEmptyMessage(0);
        }

        @Override
        public void onComplete(ResponseMessage result) {
            //hndResult = result.getStatusCode() + "\n" + result.toString();
            hndResult = result.toString();
            JSONparse();
            msgHandler.sendEmptyMessage(0);
        }
    };

    void JSONparse() {
        try {
            JSONObject jsonObject = new JSONObject(hndResult);
            JSONArray features = jsonObject.getJSONArray("features");

            //StringBuilder sb = new StringBuilder(String.valueOf(features.length()) + "\n");
            for (int i = 0; i < features.length(); i++) {
                JSONObject insideObj = features.getJSONObject(i);
                JSONObject properties = insideObj.getJSONObject("properties");
                if(properties.has("nextRoadName")) {
                    //sb.append(properties.getString("nextRoadName") + "\n");
                    roadName = String.valueOf(features.length()) + "\n" + properties.getString("nextRoadName");
                    break;
                }
            }
            //roadName = sb.toString();

        } catch (JSONException e) {
            e.printStackTrace();
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
        getMenuInflater().inflate(R.menu.menu_ranking, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(dtToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
