package com.appology.mannercash.mannercash;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Jeong on 2015-06-25.
 */
public class PointSearchFragment extends Fragment {

    Button startDate;
    Button endDate;
    Button inquiry;
    String startDateStr = "";
    String endDateStr = "";

    int initYear;
    int initMonth;
    int initDay;

    Calendar c1;
    Calendar c2;

    PointSearchTask task;


    public PointSearchFragment newInstance() {
        PointSearchFragment fragment = new PointSearchFragment();
        return fragment;
    }

    public PointSearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_point_search, container, false);

        startDate = (Button) view.findViewById(R.id.startDate);
        endDate = (Button) view.findViewById(R.id.endDate);
        inquiry = (Button) view.findViewById(R.id.inquiry);

        Date date = new Date();
        SimpleDateFormat curDateFormat = new SimpleDateFormat("yyyyMMdd");
        String curDate = curDateFormat.format(date);

        startDate.setText(curDate);
        endDate.setText(curDate);

        Calendar c = Calendar.getInstance();
        initYear = c.get(Calendar.YEAR);
        initMonth = c.get(Calendar.MONTH);
        initDay = c.get(Calendar.DAY_OF_MONTH);

        c1 = Calendar.getInstance();
        c2 = Calendar.getInstance();
        c1.set(initYear, initMonth, initDay);
        c2.set(initYear, initMonth, initDay);


        ListView listView = (ListView) view.findViewById(R.id.listView);
        ArrayList<String> item = new ArrayList<String>();

        ArrayAdapter<String> adp = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, item);
        listView.setAdapter(adp);


        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), startDateSetListener, initYear, initMonth, initDay);
                dialog.show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), endDateSetListener, initYear, initMonth, initDay);
                dialog.show();
            }
        });

        inquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(c1.before(c2) || c1.equals(c2)) {
                    task = new PointSearchTask();
                    task.execute();
                } else {
                    Toast.makeText(getActivity(), "시작일이 종료일보다 빠를 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    DatePickerDialog.OnDateSetListener startDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            startDateStr = dateStrArrange(year, monthOfYear, dayOfMonth);
            startDate.setText(startDateStr);
            c1.set(year, monthOfYear, dayOfMonth);
        }
    };

    DatePickerDialog.OnDateSetListener endDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            endDateStr = dateStrArrange(year, monthOfYear, dayOfMonth);
            endDate.setText(endDateStr);
            c2.set(year, monthOfYear, dayOfMonth);
        }
    };

    String dateStrArrange(int year, int monthOfYear, int dayOfMonth) {
        StringBuilder yearStr = new StringBuilder("");
        StringBuilder monthStr = new StringBuilder("");
        StringBuilder dayStr = new StringBuilder("");

        yearStr.append(String.valueOf(year));
        if(monthOfYear < 10) {
            monthStr.append("0").append(String.valueOf(monthOfYear+1));
        } else {
            monthStr.append(String.valueOf(monthOfYear+1));
        }
        if(dayOfMonth < 10) {
            dayStr.append("0").append(String.valueOf(dayOfMonth));
        } else {
            dayStr.append(String.valueOf(dayOfMonth));
        }

        StringBuilder date = new StringBuilder("");
        date.append(yearStr.toString()).append(monthStr.toString()).append(dayStr.toString());

        return date.toString();
    }


    private class PointSearchTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
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
