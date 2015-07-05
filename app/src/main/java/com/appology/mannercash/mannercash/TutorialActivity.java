package com.appology.mannercash.mannercash;

import android.database.sqlite.SQLiteDatabase;
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

    WordDBHelper mHelper;
    SQLiteDatabase db;

    int flag=0;
    int x;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        mHelper = new WordDBHelper(this);

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
                Animation ani =new TranslateAnimation(200.0f, 0.0f, 0.0f, 0.0f);
                ani.setDuration(2500);
                ani.setInterpolator(new DecelerateInterpolator());
                Animation rani = new TranslateAnimation(-200.0f, 0.0f, 0.0f, 0.0f);
                rani.setInterpolator(new DecelerateInterpolator());
                rani.setDuration(2500);

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
                if(chkbox.isChecked()){
                    db = mHelper.getWritableDatabase();
                    db.execSQL("UPDATE flags SET tutorial = 0 WHERE tutorial = 1;");
                    mHelper.close();
                    finish();
                    break;
                }
                else{
                    finish();
                    break;
                }
        }
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tutorial, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */
}
