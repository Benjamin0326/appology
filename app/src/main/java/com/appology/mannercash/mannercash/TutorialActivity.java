package com.appology.mannercash.mannercash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;


public class TutorialActivity extends ActionBarActivity implements View.OnClickListener{

    ImageView[] dots = new ImageView[4];
    Button start;
    CheckBox chkbox;
    ImageView tutorial;

    int flag=0;
    int x;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        SharedPreferences settings = getSharedPreferences("MannerCash", MODE_PRIVATE);
        if (settings.getString("tutorialRead", "").toString().equals("tutorialRead")) { // 튜토리얼 다시보지않기 체크 시
            startMainActivity();
        }

        dots[0]=(ImageView)findViewById(R.id.tutorial_dot1);
        dots[1]=(ImageView)findViewById(R.id.tutorial_dot2);
        dots[2]=(ImageView)findViewById(R.id.tutorial_dot3);
        dots[3]=(ImageView)findViewById(R.id.tutorial_dot4);
        start = (Button)findViewById(R.id.tutorial_close);
        chkbox = (CheckBox)findViewById(R.id.tutorial_chkbox);
        tutorial = (ImageView)findViewById(R.id.tutorial_imgview);

        start.setOnClickListener(this);

        tutorial.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                Animation ani =new TranslateAnimation(100.0f, 0.0f, 0.0f, 0.0f);
                ani.setDuration(1500);
                ani.setInterpolator(new DecelerateInterpolator());
                Animation rani = new TranslateAnimation(-100.0f, 0.0f, 0.0f, 0.0f);
                rani.setInterpolator(new DecelerateInterpolator());
                rani.setDuration(1500);

                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        x=(int)event.getRawX();
                        break;
                    case MotionEvent.ACTION_MASK:
                        break;
                    case MotionEvent.ACTION_UP:
                        if(x>(int)event.getRawX()){
                            if(flag==0){
                                dots[0].setImageResource(R.drawable.tutorial_circle_light);
                                dots[1].setImageResource(R.drawable.tutorial_circle_dark);
                                tutorial.setImageResource(R.drawable.tutorial2);
                                tutorial.startAnimation(ani);
                            }
                            else if(flag==1){
                                dots[1].setImageResource(R.drawable.tutorial_circle_light);
                                dots[2].setImageResource(R.drawable.tutorial_circle_dark);
                                tutorial.setImageResource(R.drawable.tutorial3);
                                tutorial.startAnimation(ani);
                            }
                            else if(flag==2) {
                                dots[2].setImageResource(R.drawable.tutorial_circle_light);
                                dots[3].setImageResource(R.drawable.tutorial_circle_dark);
                                tutorial.setImageResource(R.drawable.tutorial4);
                                tutorial.startAnimation(ani);
                            }
                            flag++;
                        }
                        else if(x<(int)event.getRawX()){
                            if(flag==1){
                                dots[0].setImageResource(R.drawable.tutorial_circle_dark);
                                dots[1].setImageResource(R.drawable.tutorial_circle_light);
                                tutorial.setImageResource(R.drawable.tutorial1);
                                tutorial.startAnimation(rani);
                            }
                            else if(flag==2){
                                dots[1].setImageResource(R.drawable.tutorial_circle_dark);
                                dots[2].setImageResource(R.drawable.tutorial_circle_light);
                                tutorial.setImageResource(R.drawable.tutorial2);
                                tutorial.startAnimation(rani);
                            }
                            else if(flag==3){
                                dots[2].setImageResource(R.drawable.tutorial_circle_dark);
                                dots[3].setImageResource(R.drawable.tutorial_circle_light);
                                tutorial.setImageResource(R.drawable.tutorial3);
                                tutorial.startAnimation(rani);
                            }
                            flag--;
                        }
                        break;
                    default:
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
}
