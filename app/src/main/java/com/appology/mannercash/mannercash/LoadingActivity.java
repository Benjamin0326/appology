package com.appology.mannercash.mannercash;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;


public class LoadingActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        getSupportActionBar().hide();

        Handler hd = new Handler();
        hd.postDelayed(new Runnable() {

            @Override
            public void run() {
                finish();
            }
        }, 3000);   // 3초
    }
}
