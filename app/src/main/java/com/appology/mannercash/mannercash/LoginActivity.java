package com.appology.mannercash.mannercash;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class LoginActivity extends ActionBarActivity implements View.OnClickListener{
    WordDBHelper mHelper;
    SQLiteDatabase db;

    Button login;
    TextView join;
    int tutorial;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);

        mHelper = new WordDBHelper(this);

        intent = getIntent();
        tutorial=intent.getIntExtra("tutorial", 1);

        login=(Button)findViewById(R.id.login_login);
        login.setOnClickListener(this);

        join=(TextView)findViewById(R.id.login_join);
        join.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.login_login:
                if(tutorial==1){
                    db = mHelper.getWritableDatabase();
                    db.execSQL("UPDATE flags SET login = 0 WHERE login = 1;");
                    mHelper.close();

                    intent = new Intent(this, TutorialActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
                else if(tutorial==0) {
                    db = mHelper.getWritableDatabase();
                    db.execSQL("UPDATE flags SET login = 0 WHERE login = 1;");
                    mHelper.close();

                   finish();
                }
                break;

            case R.id.login_join:
                Toast.makeText(getApplicationContext(), "안돼 로그인만 해, 미안", Toast.LENGTH_SHORT).show();
                break;
        }
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
    }*/
}
