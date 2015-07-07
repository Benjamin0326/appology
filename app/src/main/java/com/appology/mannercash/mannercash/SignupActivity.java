package com.appology.mannercash.mannercash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class SignupActivity extends ActionBarActivity {

    private static SignUpFragment1 signUpFrag1;
    private static SignUpFragment2 signUpFrag2;
    private static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mContext = this;

        if (savedInstanceState == null) {

            signUpFrag1 = new SignUpFragment1();
            signUpFrag2 = new SignUpFragment2();

            getSupportFragmentManager().beginTransaction()
                                        .add(R.id.container, signUpFrag1)
                                        .commit();
        }
    }


    // 회원 가입 첫번째 페이지
    public static class SignUpFragment1 extends Fragment {

        EditText email;
        EditText passWord;
        EditText phoneNum;
        EditText authNum;
        Button btnOk;
        Button btnCancel;

        public SignUpFragment1() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_signup_1, container, false);

            email = (EditText) rootView.findViewById(R.id.signup_email);
            passWord = (EditText) rootView.findViewById(R.id.signup_password);
            phoneNum = (EditText) rootView.findViewById(R.id.signup_phone_num);
            authNum = (EditText) rootView.findViewById(R.id.signup_auth_num);

            btnOk = (Button) rootView.findViewById(R.id.signup_ok);
            btnCancel = (Button) rootView.findViewById(R.id.signup_cancel);

            btnOk.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, signUpFrag2).commit();
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });

            return rootView;
        }

        public EditText getEmail() {
            return email;
        }

        public EditText getPasswd() {
            return passWord;
        }
    }


    // 회원 가입 두번째 페이지
    public static class SignUpFragment2 extends Fragment {

        Button btnOk;
        EditText cardNum1;
        EditText cardNum2;
        EditText cardNum3;
        EditText cardNum4;
        EditText passWord;

        public SignUpFragment2() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_signup_2, container, false);

            cardNum1 = (EditText) rootView.findViewById(R.id.signup2_point_num1);
            cardNum2 = (EditText) rootView.findViewById(R.id.signup2_point_num2);
            cardNum3 = (EditText) rootView.findViewById(R.id.signup2_point_num3);
            cardNum4 = (EditText) rootView.findViewById(R.id.signup2_point_num4);
            passWord = (EditText) rootView.findViewById(R.id.signup2_passwd);

            btnOk = (Button) rootView.findViewById(R.id.signup2_btn_ok);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText email = signUpFrag1.getEmail();
                    EditText password = signUpFrag1.getPasswd();

                    String emailString = email.getText().toString();
                    String passwordString = password.getText().toString();

                    SharedPreferences settings = mContext.getSharedPreferences("MannerCash", MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("logged", "logged");   // 자동 로그인을 위해 logged 기록
                    editor.putString("email", emailString);
                    editor.putString("password", passwordString);
                    editor.commit();

                    Intent intent = new Intent(mContext, TutorialActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });

            return rootView;
        }
    }
}
