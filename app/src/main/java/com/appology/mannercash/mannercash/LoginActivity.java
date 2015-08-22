package com.appology.mannercash.mannercash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);

        mContext = this;


        SharedPreferences settings = getSharedPreferences("MannerCash", MODE_PRIVATE);
        if (settings.getString("logged", "").toString().equals("logged")) { // 로그인 기록이 있다면 바로 튜토리얼로 전환
            startTutorialActivity();
        }

        final ImageButton signUp = (ImageButton) findViewById(R.id.login_join);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
                //finish();
            }
        });
        final FrameLayout mail=(FrameLayout)findViewById(R.id.emailFrame);
        final FrameLayout pw=(FrameLayout)findViewById(R.id.pwFrame);
        final ImageButton btn = (ImageButton) findViewById(R.id.login_login);
        final ImageButton toLogin = (ImageButton)findViewById(R.id.login_tologin);

        signUp.setVisibility(View.VISIBLE);
        toLogin.setVisibility(View.VISIBLE);
        btn.setVisibility(View.INVISIBLE);
        mail.setVisibility(View.INVISIBLE);
        pw.setVisibility(View.INVISIBLE);

        toLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                signUp.setVisibility(View.INVISIBLE);
                toLogin.setVisibility(View.INVISIBLE);
                btn.setVisibility(View.VISIBLE);
                mail.setVisibility(View.VISIBLE);
                pw.setVisibility(View.VISIBLE);
            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email = (EditText) findViewById(R.id.login_email);
                EditText password = (EditText) findViewById(R.id.login_password);

                String emailString = email.getText().toString();
                String passwordString = password.getText().toString();
                if(emailString.isEmpty()) {
                    Toast.makeText(mContext, "Email을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if(passwordString.isEmpty()) {
                    Toast.makeText(mContext, "Password를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    task = new BackgroundLogin();
                    task.execute(emailString, passwordString);
                    if (emailString.length() > 0 && passwordString.length() > 0) {
                        if (emailString.equals("1") && passwordString.equals("1")) {    // 테스트 (아이디 : 1, 비번 : 1)
                            SharedPreferences settings = getSharedPreferences("MannerCash", MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("logged", "logged");   // 자동 로그인을 위해 logged 기록
                            editor.putString("email", emailString);
                            editor.putString("password", passwordString);
                            editor.commit();


                            startTutorialActivity();
                        }
                    }
                }
            }
        });


    }

    void startTutorialActivity() {
        Intent intent = new Intent(LoginActivity.this, TutorialActivity.class);
        startActivity(intent);
        finish();
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
