package com.appology.mannercash.mannercash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);

        SharedPreferences settings = getSharedPreferences("MannerCash", MODE_PRIVATE);
        if (settings.getString("logged", "").toString().equals("logged")) { // 로그인 기록이 있다면 바로 튜토리얼로 전환
            startTutorialActivity();
        }

        Button btn = (Button) findViewById(R.id.login_login);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email = (EditText) findViewById(R.id.login_email);
                EditText password = (EditText) findViewById(R.id.login_password);

                String emailString = email.getText().toString();
                String passwordString = password.getText().toString();

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
        });


        TextView signUp = (TextView) findViewById(R.id.login_join);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    void startTutorialActivity() {
        Intent intent = new Intent(LoginActivity.this, TutorialActivity.class);
        startActivity(intent);
        finish();
    }
}
