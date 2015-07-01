package com.appology.mannercash.mannercash;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends ActionBarActivity {

    private final long FINSH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    DrawerLayout dlDrawer;
    ActionBarDrawerToggle dtToggle;

    ListView lvDrawerList;
    ArrayAdapter<String> adtDrawerList;
    String[] menuItems = new String[]{"Home", "포인트 내역", "랭킹", "제휴사 안내", "보호구역 안내"};

    Intent intent;

    ToggleButton toggleButton;
    TextView textView;   // 네트워킹 테스트
    MainFunctionTask mainFunctionTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                dlDrawer.closeDrawer(lvDrawerList);
            }
        });

        dlDrawer = (DrawerLayout) findViewById(R.id.dl_activity_main);
        dtToggle = new ActionBarDrawerToggle(this, dlDrawer, R.string.app_name, R.string.app_name);
        dlDrawer.setDrawerListener(dtToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toggleButton = (ToggleButton) findViewById(R.id.toggle_button);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toggleButton.isChecked()) {
                    mainFunctionTask = new MainFunctionTask();
                    mainFunctionTask.execute(100);
                } else {
                    mainFunctionTask.cancel(true);
                }
            }
        });

        textView = (TextView) findViewById(R.id.text_view);   // 네트워킹 테스트
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
        getMenuInflater().inflate(R.menu.menu_point_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(dtToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class MainFunctionTask extends AsyncTask<Integer , Integer , Integer> {

        int value;   // 네트워킹 테스트
        int count;   // 네트워킹 테스트
        public String Url = "http://m.naver.com";   // 네트워킹 테스트

        public MainFunctionTask() {
            value = 0;   // 네트워킹 테스트
            count = 0;   // 네트워킹 테스트
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            while (isCancelled() == false) {

                value = request(Url);   // 네트워킹 테스트

                if (value >= 100) {   // 네트워킹 테스트
                    break;   // 네트워킹 테스트
                } else {   // 네트워킹 테스트
                    publishProgress(value);
                }

                try {
                    Thread.sleep(200);   // 네트워킹 테스트
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            return value;   // 네트워킹 테스트
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

        @Override
        protected void onProgressUpdate(Integer... values) {
            textView.setText(values[0].toString());   // 네트워킹 테스트
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Integer integer) {
        }

        @Override
        protected void onCancelled() {
        }
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
            Toast.makeText(getApplicationContext(), "'뒤로'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }
}