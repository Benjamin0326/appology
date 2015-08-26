package com.appology.mannercash.mannercash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class TutorialActivity2 extends ActionBarActivity {
    float pressedX;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_activity2);
        getSupportActionBar().hide();
        RelativeLayout main = (RelativeLayout)findViewById(R.id.tutorial_main2);
        main.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pressedX = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (pressedX - event.getX() > 0) {
                            Intent intent = new Intent(TutorialActivity2.this, TutorialActivity3.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.left_in, R.anim.left_out);
                            finish();
                        }
                        else if(pressedX - event.getX() < 0){
                            Intent intent = new Intent(TutorialActivity2.this, TutorialActivity.class);
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
