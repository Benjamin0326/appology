package com.appology.mannercash.mannercash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class PointStatusActivity extends ActionBarActivity {

    DrawerLayout dlDrawer;
    ActionBarDrawerToggle dtToggle;

    ListView lvDrawerList;
    ArrayAdapter<String> adtDrawerList;
    LinearLayout dlLayout;
    String[] menuItems = new String[]{"Home", "포인트 내역", "랭킹", "제휴 안내"};

    Intent intent;

    ImageView userPhoto;
    TextView name;
    ImageButton infoModify;

    Context mContext;
    MainActivity mainActivityClass;

    CustomAdapter2 adp;

    ImageButton today;
    ImageButton week;
    ImageButton month;
    ImageButton month3;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_status);
        
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_point);
        mContext = this;

        dlLayout = (LinearLayout) findViewById(R.id.dlLayout);
        lvDrawerList = (ListView) findViewById(R.id.lv_activity_point_status);
        adtDrawerList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuItems);
        lvDrawerList.setAdapter(adtDrawerList);

        lvDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(getApplicationContext(), PointStatusActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(getApplicationContext(), RankingActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(getApplicationContext(), PartnerShipActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        break;
                }
                dlDrawer.closeDrawer(dlLayout);
            }
        });

        dlDrawer = (DrawerLayout) findViewById(R.id.dl_activity_point_status);
        dtToggle = new ActionBarDrawerToggle(this, dlDrawer, R.string.app_name, R.string.app_name);
        dlDrawer.setDrawerListener(dtToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        userPhoto = (ImageView) findViewById(R.id.userPhoto);

        name = (TextView) findViewById(R.id.name);

        mainActivityClass = (MainActivity) MainActivity.mainActivity;
        infoModify = (ImageButton) findViewById(R.id.infoModify);
        infoModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences("MannerCash", MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("logged", "");
                editor.putString("email", "");
                editor.putString("password", "");
                editor.putString("tutorialRead", "");
                editor.commit();

                intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                mainActivityClass.finish();
                finish();
            }
        });


        TextView nameText = (TextView)findViewById(R.id.point_name);
        TextView pointText = (TextView)findViewById(R.id.point_point);
        today=(ImageButton)findViewById(R.id.point_today);

        SharedPreferences settings = getSharedPreferences("MannerCash", MODE_PRIVATE);
        final String id=settings.getString("email", "");


        today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomAdapter2 adp=new CustomAdapter2();
                java.util.Calendar cal = java.util.Calendar.getInstance();
                java.text.DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                cal.add(cal.DATE,-0);
                String dateStr=format.format(cal.getTime());

                WordDBHelper mHelper = new WordDBHelper(mContext);
                SQLiteDatabase db=mHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM point where date>='"+dateStr+"' and id='"+id+"'order by date desc, time desc", null);
                cursor.moveToFirst();



                int index=cursor.getCount();
                while (index>0) {
                    int point = cursor.getInt(1);
                    String routeName = cursor.getString(2);
                    String date = cursor.getString(3);
                    point_item tmpItem=new point_item(date, routeName, point);
                    adp.add(tmpItem);

                    index--;
                    cursor.moveToNext();
                }
                listView = (ListView)findViewById(R.id.point_listView);
                listView.setAdapter(adp);

            }
        });
        week=(ImageButton)findViewById(R.id.point_week);
        week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomAdapter2 adp=new CustomAdapter2();
                java.util.Calendar cal = java.util.Calendar.getInstance();
                java.text.DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                cal.add(cal.DATE,-7);
                String dateStr=format.format(cal.getTime());

                WordDBHelper mHelper = new WordDBHelper(mContext);
                SQLiteDatabase db=mHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM point where date>='"+dateStr+"' and id='"+id+"' order by date desc, time desc", null);
                cursor.moveToFirst();
                int index=cursor.getCount();
                while (index>0) {
                    int point = cursor.getInt(1);
                    String routeName = cursor.getString(2);
                    String date = cursor.getString(3);
                    point_item tmpItem=new point_item(date, routeName, point);
                    adp.add(tmpItem);

                    index--;
                    cursor.moveToNext();
                }
                listView = (ListView)findViewById(R.id.point_listView);

                listView.setAdapter(adp);
            }
        });
        month=(ImageButton)findViewById(R.id.point_month);
        month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomAdapter2 adp=new CustomAdapter2();
                java.util.Calendar cal = java.util.Calendar.getInstance();
                java.text.DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                cal.add(cal.DATE,-30);
                String dateStr=format.format(cal.getTime());

                WordDBHelper mHelper = new WordDBHelper(mContext);
                SQLiteDatabase db=mHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM point where date>='"+dateStr+"' and id='"+id+"'order by date desc, time desc", null);


                cursor.moveToFirst();
                int index=cursor.getCount();
                while (index>0) {
                    int point = cursor.getInt(1);
                    String routeName = cursor.getString(2);
                    String date = cursor.getString(3);
                    point_item tmpItem=new point_item(date, routeName, point);
                    adp.add(tmpItem);

                    index--;
                    cursor.moveToNext();
                }
                listView = (ListView)findViewById(R.id.point_listView);
                listView.setAdapter(adp);
            }
        });
        month3=(ImageButton)findViewById(R.id.point_month_3);
        month3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomAdapter2 adp=new CustomAdapter2();
                java.util.Calendar cal = java.util.Calendar.getInstance();
                java.text.DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                cal.add(cal.DATE,-90);
                String dateStr=format.format(cal.getTime());

                WordDBHelper mHelper = new WordDBHelper(mContext);
                SQLiteDatabase db=mHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM point where date>='"+dateStr+"'and id='"+id+"' order by date desc, time desc", null);
                cursor.moveToFirst();
                int index=cursor.getCount();
                Log.i("mannercash", "1 : "+ index + ", " + adp.getCount());
                while (index>0) {
                    int point = cursor.getInt(1);
                    String routeName = cursor.getString(2);
                    String date = cursor.getString(3);
                    point_item tmpItem=new point_item(date, routeName, point);
                    adp.add(tmpItem);

                    index--;
                    cursor.moveToNext();
                }
                Log.i("mannercash", "1 : "+ adp.getCount());
                listView = (ListView)findViewById(R.id.point_listView);

                listView.setAdapter(adp);
            }
        });

        WordDBHelper mHelper = new WordDBHelper(this);
        SQLiteDatabase db=mHelper.getReadableDatabase();
        Cursor cursor;
        cursor = db.rawQuery("SELECT * FROM user where id='"+id+"'", null);
        if (cursor.moveToFirst()) {
            String nameStr = cursor.getString(3);
            nameText.setText(nameStr+"님");
            name.setText(nameStr);
            int myPoint=cursor.getInt(2);
            pointText.setText("총 잔액 "+Integer.toString(myPoint)+"원");
        }

        listView = (ListView)findViewById(R.id.point_listView);

        adp = new CustomAdapter2();

        cursor = db.rawQuery("SELECT * FROM point where id='"+id+"'order by date desc, time desc", null);

        cursor.moveToFirst();
        int index=cursor.getCount();
        while (index>0) {
            int point = cursor.getInt(1);
            String routeName = cursor.getString(2);
            String date = cursor.getString(3);
            point_item tmpItem=new point_item(date, routeName, point);
            adp.add(tmpItem);

            index--;
            cursor.moveToNext();
        }
        listView.deferNotifyDataSetChanged();
        listView.setAdapter(adp);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        dtToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        dtToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_point_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(dtToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

class CustomAdapter2 extends BaseAdapter {
    private ArrayList<point_item> m_List;

    public CustomAdapter2(){
        m_List=new ArrayList<point_item>();
    }

    @Override
    public int getCount(){
        return m_List.size();
    }

    @Override
    public Object getItem(int position){
        return m_List.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final int pos=position;
        final Context context=parent.getContext();

        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.point_item, parent, false);
            point_item tmpItem=m_List.get(pos);

            TextView t1 = (TextView)convertView.findViewById(R.id.point_item_date);
            t1.setText(tmpItem.date);
            TextView t2= (TextView)convertView.findViewById(R.id.point_item_route);
            t2.setText(tmpItem.routeName);
            TextView t3=(TextView)convertView.findViewById(R.id.point_item_point);
            t3.setText("적립 "+Integer.toString(tmpItem.point)+"원");
        }
        return convertView;
    }
    public void add(point_item addItem){
        m_List.add(addItem);
    }

    public void remove(int _position){
        m_List.remove(_position);
    }

}

class point_item{
    String date;
    String routeName;
    int point;

    public point_item(String _date, String _routeName, int _point){
        date=_date;
        routeName=_routeName;
        point=_point;
    }
}