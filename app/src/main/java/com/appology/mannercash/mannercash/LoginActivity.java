package com.appology.mannercash.mannercash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

public class LoginActivity extends ActionBarActivity {

    BackgroundLogin task;
    Context mContext;

    InputMethodManager imm;

    EditText email;
    EditText password;

    FrameLayout mail;
    FrameLayout pw;
    ImageButton btn;
    ImageButton toLogin;
    LinearLayout layout;
    ImageButton signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);
        getSupportActionBar().hide();
        mContext = this;
        SharedPreferences settings = getSharedPreferences("MannerCash", MODE_PRIVATE);
        if (settings.getString("logged", "").toString().equals("logged")) { // 로그인 기록이 있다면 바로 튜토리얼로 전환
            startTutorialActivity();
        }

        WordDBHelper mHelper = new WordDBHelper(this);
        SQLiteDatabase db=mHelper.getReadableDatabase();
        Cursor cursor;
        cursor = db.rawQuery("SELECT * FROM user where id='" + "admin" + "'", null);
        if (cursor.moveToFirst()) {

        } else {
            db = mHelper.getWritableDatabase();
            db.execSQL("INSERT INTO user VALUES ('admin','1234', 240, 'admin');");
            db = mHelper.getWritableDatabase();
            db.execSQL("INSERT INTO point VALUES ('"+"admin"+"',"+60+",'"+"경부 고속도로"+"','"+"2015-07-18"+"','"+"21:34:18"+"');");
            db = mHelper.getWritableDatabase();
            db.execSQL("INSERT INTO point VALUES ('"+"admin"+"',"+60+",'"+"서울외곽순환 고속도로"+"','"+"2015-07-22"+"','"+"23:15:21"+"');");
            db = mHelper.getWritableDatabase();
            db.execSQL("INSERT INTO point VALUES ('"+"admin"+"',"+60+",'"+"서울외곽순환 고속도로"+"','"+"2015-08-22"+"','"+"16:53:35"+"');");
            db = mHelper.getWritableDatabase();
            db.execSQL("INSERT INTO point VALUES ('"+"admin"+"',"+60+",'"+"서해안 고속도로"+"','"+"2015-08-23"+"','"+"22:22:44"+"');");
        }

        signUp = (ImageButton) findViewById(R.id.login_join);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mail=(FrameLayout)findViewById(R.id.emailFrame);
        pw=(FrameLayout)findViewById(R.id.pwFrame);
        btn = (ImageButton) findViewById(R.id.login_login);
        toLogin = (ImageButton)findViewById(R.id.login_tologin);
        layout = (LinearLayout) findViewById(R.id.layout);

        signUp.setVisibility(View.VISIBLE);
        toLogin.setVisibility(View.VISIBLE);
        btn.setVisibility(View.INVISIBLE);
        mail.setVisibility(View.INVISIBLE);
        pw.setVisibility(View.INVISIBLE);

        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp.setVisibility(View.INVISIBLE);
                toLogin.setVisibility(View.INVISIBLE);
                btn.setVisibility(View.VISIBLE);
                mail.setVisibility(View.VISIBLE);
                pw.setVisibility(View.VISIBLE);
            }
        });

        email = (EditText) findViewById(R.id.login_email);
        password = (EditText) findViewById(R.id.login_password);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailString = email.getText().toString();
                String passwordString = password.getText().toString();
                if(emailString.isEmpty()) {
                    Toast.makeText(mContext, "Email을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if(passwordString.isEmpty()) {
                    Toast.makeText(mContext, "Password를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {

                    WordDBHelper mHelper = new WordDBHelper(mContext);
                    SQLiteDatabase db=mHelper.getReadableDatabase();
                    Cursor cursor;
                    cursor = db.rawQuery("SELECT * FROM user where id='"+emailString+"' and password='"+passwordString+"'", null);
                    if (cursor.moveToFirst()) {
                        SharedPreferences settings = getSharedPreferences("MannerCash", MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("logged", "logged");   // 자동 로그인을 위해 logged 기록
                        editor.putString("email", emailString);
                        editor.putString("password", passwordString);
                        editor.commit();

                        startTutorialActivity();
                    }
                    else{
                        Toast.makeText(mContext, "회원정보가 잘못 되었습니다.", Toast.LENGTH_LONG).show();
                    }

                    //task = new BackgroundLogin();
                    //task.execute(emailString, passwordString);
                    /*if (emailString.length() > 0 && passwordString.length() > 0) {
                        if (emailString.equals("1") && passwordString.equals("1")) {    // 테스트 (아이디 : 1, 비번 : 1)
                            SharedPreferences settings = getSharedPreferences("MannerCash", MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("logged", "logged");   // 자동 로그인을 위해 logged 기록
                            editor.putString("email", emailString);
                            editor.putString("password", passwordString);
                            editor.commit();


                            startTutorialActivity();
                        }
                    }*/
                }
            }
        });

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(email.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
            }
        });
    }

    void startTutorialActivity() {
        Intent intent = new Intent(LoginActivity.this, TutorialActivity.class);
        startActivity(intent);
        finish();
    }

    private final long FINSH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    @Override
    public void onBackPressed() {
        if(btn.getVisibility() == View.VISIBLE) {
            signUp.setVisibility(View.VISIBLE);
            toLogin.setVisibility(View.VISIBLE);
            btn.setVisibility(View.INVISIBLE);
            mail.setVisibility(View.INVISIBLE);
            pw.setVisibility(View.INVISIBLE);
        } else {
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

    private class BackgroundLogin extends AsyncTask<String, Void, String>{

        String id;
        String password;

        @Override
        protected String doInBackground(String... arg) {
            try{
                id = (String) arg[0];
                password = (String) arg[1];
                String link = "http://10.0.2.2/mannercash_server.php?code=2&id=" +
                        URLEncoder.encode(id, "UTF-8") + "&password=" +
                        URLEncoder.encode(password, "UTF-8") + "&phoneNum=" +
                        URLEncoder.encode("", "UTF-8") + "&cardNum=" +
                        URLEncoder.encode("", "UTF-8") + "&point=" +
                        URLEncoder.encode("", "UTF-8");
                URL url = new URL(link);
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));
                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                in.close();
                return sb.toString();
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result){
            if(result.equals("OK")){
                SharedPreferences settings = getSharedPreferences("MannerCash", MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("logged", "logged");   // 자동 로그인을 위해 logged 기록
                editor.putString("email", id);
                editor.putString("password", password);
                editor.commit();
                Toast.makeText(getApplicationContext(), "로그인 되었습니다.", Toast.LENGTH_LONG).show();
                startTutorialActivity();
            }
            else{
                Toast.makeText(getApplicationContext(), "로그인에 실패했습니다..", Toast.LENGTH_LONG).show();
            }
        }
    }
}

