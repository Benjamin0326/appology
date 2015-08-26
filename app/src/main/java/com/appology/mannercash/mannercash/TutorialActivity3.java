package com.appology.mannercash.mannercash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class TutorialActivity3 extends ActionBarActivity implements View.OnClickListener{
    Button start;
    CheckBox chkbox;
    float pressedX;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_activity3);
        getSupportActionBar().hide();
        start = (Button)findViewById(R.id.tutorial_close);
        chkbox = (CheckBox)findViewById(R.id.tutorial_chkbox);
        start.setOnClickListener(this);
        RelativeLayout main = (RelativeLayout)findViewById(R.id.tutorial_main3);
        main.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pressedX = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (pressedX - event.getX() < 0) {
                            Intent intent = new Intent(TutorialActivity3.this, TutorialActivity2.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.right_in, R.anim.right_out);
                            finish();
                        }
                        break;
                }
                return true;
            }
        });

    }
    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.tutorial_close:
                if(chkbox.isChecked()) {
                    SharedPreferences settings = getSharedPreferences("MannerCash", MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("tutorialRead", "tutorialRead");   // 튜토리얼 다시보지않기 체크 시 tutorialRead 에 저장
                    editor.commit();
                    startMainActivity();
                    break;
                }
                else {
                    startMainActivity();
                    break;
                }
        }
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
