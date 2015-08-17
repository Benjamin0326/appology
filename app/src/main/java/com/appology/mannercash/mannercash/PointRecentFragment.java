package com.appology.mannercash.mannercash;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Jeong on 2015-06-25.
 */
public class PointRecentFragment extends Fragment {

    SwipeRefreshLayout swipeView;
    TextView textView;

    String curDate;
    String endDate;

    PointRecentTask task;
    ArrayAdapter<String> adp;
    WordDBHelper mHelper;
    SQLiteDatabase db;

    public PointRecentFragment newInstance() {
        PointRecentFragment fragment = new PointRecentFragment();
        return fragment;
    }

    public PointRecentFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_point_recent, container, false);

        mHelper = new WordDBHelper(MainActivity.mainActivity);
        db=mHelper.getReadableDatabase();


        textView = (TextView) view.findViewById(R.id.textView);
        setDateTextView();


        ListView listView = (ListView) view.findViewById(R.id.listView);
        ArrayList<String> item = new ArrayList<String>();

        // 이부분을 수정하여 리스트뷰에 넣을 DB 데이터 불러오면 됨.
        task = new PointRecentTask();
        task.execute();
        // 이부분을 수정하여 리스트뷰에 넣을 DB 데이터 불러오면 됨.

        adp = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, item);
        listView.setAdapter(adp);

        swipeView = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        swipeView.setEnabled(false);

        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);

                // 여기에 새로고침 했을 시 동작하는 부분 삽입
                task = new PointRecentTask();
                task.execute();
                testPrint();
                // 여기에 새로고침 했을 시 동작하는 부분 삽입

                //swipeView.setRefreshing(false);

                Handler hd = new Handler();
                hd.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeView.setRefreshing(false);
                    }
                }, 3000);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0)
                    swipeView.setEnabled(true);
                else
                    swipeView.setEnabled(false);
            }
        });

        return view;
    }

    void testPrint(){
        SharedPreferences settings = MainActivity.mainActivity.getSharedPreferences("MannerCash", MainActivity.mainActivity.MODE_PRIVATE);
        String id=settings.getString("email", "email");
        Cursor cursor;
        cursor = db.rawQuery("SELECT * FROM point where id='"+id+"'", null);
        int point;
        while (true) {
            if(!cursor.moveToFirst())
                break;
            point = cursor.getInt(1);
            String routeName = cursor.getString(2);
            String date = cursor.getString(3);
            String time = cursor.getString(4);
            adp.add(point+" "+routeName+" "+date+" "+time);
            adp.notifyDataSetChanged();
            if(cursor.moveToNext()==false){
                break;
            }
        }
    }

    void setDateTextView() {
        Date date = new Date();

        SimpleDateFormat curDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        curDate = curDateFormat.format(date);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, -30);

        SimpleDateFormat endDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        endDate = endDateFormat.format(cal.getTime());

        textView.setText("조회기간 (최근 30일)     " + endDate + " ~ " + curDate);
    }


    private class PointRecentTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            testPrint();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
