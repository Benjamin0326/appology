package com.appology.mannercash.mannercash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;


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


}
