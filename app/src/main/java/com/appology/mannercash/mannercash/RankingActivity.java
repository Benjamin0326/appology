package com.appology.mannercash.mannercash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class RankingActivity extends ActionBarActivity {

    DrawerLayout dlDrawer;
    ActionBarDrawerToggle dtToggle;

    ListView lvDrawerList;
    ArrayAdapter<String> adtDrawerList;
    LinearLayout dlLayout;
    String[] menuItems = new String[]{"Home", "포인트 내역", "랭킹", "제휴사 안내", "보호구역 안내"};
    Intent intent;

    ImageView userPhoto;
    TextView name;
    Button infoModify;

    WordDBHelper mHelper;
    SQLiteDatabase db;

    Context mContext;
    MainActivity mainActivityClass;
    CustomAdapter adp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        mContext = this;

        dlLayout = (LinearLayout) findViewById(R.id.dlLayout);
        lvDrawerList = (ListView) findViewById(R.id.lv_activity_ranking);
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
                        break;
                    case 4:
                        break;
                }
                dlDrawer.closeDrawer(dlLayout);
            }
        });

        dlDrawer = (DrawerLayout) findViewById(R.id.dl_activity_ranking);
        dtToggle = new ActionBarDrawerToggle(this, dlDrawer, R.string.app_name, R.string.app_name);
        dlDrawer.setDrawerListener(dtToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        userPhoto = (ImageView) findViewById(R.id.userPhoto);

        name = (TextView) findViewById(R.id.name);


        mainActivityClass = (MainActivity) MainActivity.mainActivity;
        infoModify = (Button) findViewById(R.id.infoModify);
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



        ListView listView = (ListView)findViewById(R.id.rank_listView);
        adp = new CustomAdapter();

        mHelper = new WordDBHelper(this);
        db=mHelper.getReadableDatabase();
        TextView nameText = (TextView)findViewById(R.id.rank_name);
        TextView pointText = (TextView)findViewById(R.id.rank_point);
        SharedPreferences settings = getSharedPreferences("MannerCash", MODE_PRIVATE);
        String id=settings.getString("email","");
        WordDBHelper mHelper = new WordDBHelper(MainActivity.mainActivity);
        SQLiteDatabase db=mHelper.getReadableDatabase();
        Cursor cursor;
        cursor = db.rawQuery("SELECT * FROM user where id='"+id+"'", null);
        if (cursor.moveToFirst()) {
            String nameStr = cursor.getString(3);
            name.setText(nameStr);
            nameText.setText(nameStr+" 님");
            int myPoint=cursor.getInt(2);
            pointText.setText(Integer.toString(myPoint)+" 원");
        }

        cursor = db.rawQuery("SELECT * FROM user order by point desc", null);

        cursor.moveToFirst();
        int index=cursor.getCount();
        int rank=1;
        while (index>0) {
            int point = cursor.getInt(2);
            String name = cursor.getString(3);
            item tmpItem=new item(rank, name, point);
            adp.add(tmpItem);

            index--;
            cursor.moveToNext();
        }

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ranking, menu);
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

class CustomAdapter extends BaseAdapter{
    private ArrayList<item> m_List;

    public CustomAdapter(){
        m_List=new ArrayList<item>();
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
            convertView=inflater.inflate(R.layout.rank_item, parent, false);
            item tmpItem=m_List.get(pos);

            TextView t1 = (TextView)convertView.findViewById(R.id.rank_item_num);
            if(tmpItem.item_rank==1){
                t1.setBackgroundResource(R.drawable.first);
            }
            else {
                t1.setText(Integer.toString(tmpItem.item_rank));
            }
            TextView t2= (TextView)convertView.findViewById(R.id.rank_item_name);
            t2.setText(tmpItem.item_name);
            TextView t3=(TextView)convertView.findViewById(R.id.rank_item_point);
            t3.setText(Integer.toString(tmpItem.item_point));
        }
        return convertView;
    }
    public void add(item addItem){
        m_List.add(addItem);
    }

    public void remove(int _position){
        m_List.remove(_position);
    }
}

class item{
    int item_rank;
    String item_name;
    int item_point;

    public item(int _rank, String _name, int _point){
        item_rank=_rank;
        item_name=_name;
        item_point=_point;
    }
}