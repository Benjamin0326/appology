package com.appology.mannercash.mannercash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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


public class SignupActivity extends ActionBarActivity {

    private static SignUpFragment1 signUpFrag1;
    private static SignUpFragment2 signUpFrag2;
    private static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();
        mContext = this;

        if (savedInstanceState == null) {

            signUpFrag1 = new SignUpFragment1();
            signUpFrag2 = new SignUpFragment2();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, signUpFrag1)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    // 회원 가입 첫번째 페이지
    public static class SignUpFragment1 extends Fragment {

        EditText email;
        EditText passWord;
        EditText passWord2;
        EditText name;
        ImageButton btnOk;
        ImageButton btnCancel;
        LinearLayout layout;

        InputMethodManager imm;

        public SignUpFragment1() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_signup_1, container, false);

            email = (EditText) rootView.findViewById(R.id.signup_email);
            passWord = (EditText) rootView.findViewById(R.id.signup_password);
            passWord2 = (EditText) rootView.findViewById(R.id.signup_password2);
            name = (EditText) rootView.findViewById(R.id.signup_name);
            layout = (LinearLayout) rootView.findViewById(R.id.layout);

            btnOk = (ImageButton) rootView.findViewById(R.id.signup_ok);
            btnCancel = (ImageButton) rootView.findViewById(R.id.signup_cancel);
            btnOk.setImageResource(R.drawable.signup_ok);
            btnCancel.setImageResource(R.drawable.signup_cancel);
            btnOk.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    btnOk.setImageResource(R.drawable.signup_ok_pressed);
                    btnCancel.setImageResource(R.drawable.signup_cancel);
                    if(!isValidEmail(email.getText().toString()))
                        Toast.makeText(mContext.getApplicationContext(), "메일주소를 정확히 입력해주세요.", Toast.LENGTH_LONG).show();
                    else if(!isValidPasswordLength(passWord.getText().toString()))
                        Toast.makeText(mContext.getApplicationContext(), "비밀번호는 8자리 이상이여야 합니다.", Toast.LENGTH_LONG).show();
                    else if(!isValidPasswordConfirm(passWord.getText().toString(), passWord2.getText().toString()))
                        Toast.makeText(mContext.getApplicationContext(), "비밀번호가 같지 않습니다.", Toast.LENGTH_LONG).show();
                    else if(name.getText().toString().isEmpty())
                        Toast.makeText(mContext.getApplicationContext(), "이름을 입력해주세요.", Toast.LENGTH_LONG).show();
                    else{
                        WordDBHelper mHelper = new WordDBHelper(mContext);
                        SQLiteDatabase db=mHelper.getReadableDatabase();
                        Cursor cursor;
                        cursor = db.rawQuery("SELECT * FROM user where id='"+email.getText().toString()+"'", null);
                        if (cursor.moveToFirst()) {
                            Toast.makeText(mContext.getApplicationContext(), "이미 회원가입 되신 E-Mail 주소입니다.", Toast.LENGTH_LONG).show();
                        }
                        else {
                            SharedPreferences settings = getActivity().getSharedPreferences("MannerCash", MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("logged", "logged");   // 자동 로그인을 위해 logged 기록
                            editor.putString("email", email.getText().toString());
                            editor.putString("password", passWord.getText().toString());
                            editor.commit();

                            db = mHelper.getWritableDatabase();
                            db.execSQL("INSERT INTO user VALUES ('" + email.getText().toString() + "','" + passWord.getText().toString() + "'," + 0 + ",'"+name.getText().toString()+"');");
                            Intent intent = new Intent(mContext, TutorialActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    }
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    btnOk.setImageResource(R.drawable.signup_ok);
                    btnCancel.setImageResource(R.drawable.signup_cancel_pressed);
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(email.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(passWord.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(passWord2.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(name.getWindowToken(), 0);
                }
            });

            return rootView;
        }

        boolean isValidEmail(CharSequence target) {
            if (target == null)
                return false;

            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }

        boolean isValidPasswordLength(String pass) {
            if (pass != null && pass.length() > 7)
                return true;

            return false;
        }

        boolean isValidPasswordConfirm(String password, String confirmPassword)
        {
            if (confirmPassword != null && password != null) {
                if (password.equals(confirmPassword)) {
                    return true;
                }
            }
            return false;
        }

        public EditText getEmail() {
            return email;
        }

        public EditText getName() {
            return name;
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

        Signup task;

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
                    EditText name = signUpFrag1.getName();

                    String emailString = email.getText().toString();
                    String passwordString = password.getText().toString();
                    String nameString = name.getText().toString();
                    String cardNumString = cardNum1.getText().toString() +
                            cardNum2.getText().toString() +
                            cardNum3.getText().toString() +
                            cardNum4.getText().toString();

                    if(cardNum1.getText().toString().isEmpty() ||
                            cardNum2.getText().toString().isEmpty() ||
                            cardNum3.getText().toString().isEmpty() ||
                            cardNum4.getText().toString().isEmpty())
                        Toast.makeText(mContext.getApplicationContext(),  "카드번호를 제대로 입력해주세요.", Toast.LENGTH_LONG).show();
                    else {
                        task = new Signup();
                        task.execute(emailString, passwordString, nameString, cardNumString);
                        getActivity().finish();
                    }
                    /*SharedPreferences settings = mContext.getSharedPreferences("MannerCash", MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("logged", "logged");   // 자동 로그인을 위해 logged 기록
                    editor.putString("email", emailString);
                    editor.putString("password", passwordString);
                    editor.commit();
                    */
                    //Intent intent = new Intent(mContext, TutorialActivity.class);
                    //startActivity(intent);

                }
            });
            return rootView;
        }

    }

    // BackGround Test
    private static class Signup extends AsyncTask<String, Void, String> {

        protected void onPreExcute() {

        }

        @Override
        protected String doInBackground(String... arg) {
            try {
                String id = (String) arg[0];
                String password = (String) arg[1];
                String name = (String) arg[2];
                String cardNum = (String) arg[3];
                String link = "http://10.0.2.2/mannercash_server.php?code=1&id=" +
                        URLEncoder.encode(id, "UTF-8") + "&password=" +
                        URLEncoder.encode(password, "UTF-8") + "&name=" +
                        URLEncoder.encode(name, "UTF-8") + "&cardNum=" +
                        URLEncoder.encode(cardNum, "UTF-8") + "&point=" +
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
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result){
            if(result.equals("OK"))
                Toast.makeText(mContext.getApplicationContext(),  "회원가입에 성공하였습니다.", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(mContext.getApplicationContext(),  "회원가입에 실패하였습니다.", Toast.LENGTH_LONG).show();

        }
    }
}




