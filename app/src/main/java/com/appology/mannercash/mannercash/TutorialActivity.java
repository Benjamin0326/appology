package com.appology.mannercash.mannercash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class TutorialActivity extends ActionBarActivity{
    float pressedX;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        getSupportActionBar().hide();
        SharedPreferences settings = getSharedPreferences("MannerCash", MODE_PRIVATE);
        if (settings.getString("tutorialRead", "").toString().equals("tutorialRead")) { // 튜토리얼 다시보지않기 체크 시
            startMainActivity();
        }

        RelativeLayout main = (RelativeLayout)findViewById(R.id.tutorial_main1);
        main.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pressedX = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (pressedX - event.getX() > 0) {
                            Intent intent = new Intent(TutorialActivity.this, TutorialActivity2.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.left_in, R.anim.left_out);
                            finish();
                        }
                        break;
                }
                return true;
            }
        });


    }

    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private final long FINSH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
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
}
