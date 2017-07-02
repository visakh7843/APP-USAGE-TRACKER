package com.example.acer.appusagetracker;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseOptions;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.internal.InterfaceAudience;
import com.couchbase.lite.replicator.Replication;
import com.example.acer.appusagetracker.lockscreen.UnlockCountService;
import com.example.acer.appusagetracker.usagetracker.appUsage.PagerAdapter;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements Replication.ChangeListener {

    public static String TAG = "GrocerySync";
    public static int flag=0;
    public static int unlock=0;

    public static final String DATABASE_NAME = "sync_gateway";
    public static final String designDocName = "grocery-local";
    public static final String byDateViewName = "byDate";

    public static final String SYNC_URL = "http://172.16.143.150:4985/sync_gateway";

    protected static Manager manager;
    public static Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences preferences=getSharedPreferences("anyname", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt("unlock",unlock);
        editor.commit();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Detailed View"));
        tabLayout.addTab(tabLayout.newTab().setText("Pie-Chart View"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        Intent myIntent = new Intent(this,UnlockCountService.class);
        this.startService(myIntent);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        try {
            startCBLite();
          } catch (Exception e) {

            Toast.makeText(getApplicationContext(), "Error Initializing CBLIte, see logs for details", Toast.LENGTH_LONG).show();
            com.couchbase.lite.util.Log.e("TAG", "Error initializing CBLite", e);
        }
    }
    protected void onDestroy() {
        if(manager != null) {
            manager.close();
        }
        super.onDestroy();
    }

    public void startCBLite() throws Exception {

        manager = new Manager(new AndroidContext(getApplicationContext()), Manager.DEFAULT_OPTIONS);
        DatabaseOptions options = new DatabaseOptions();
        options.setCreate(true);
        database = manager.openDatabase(DATABASE_NAME, options);
        com.couchbase.lite.View viewItemsByDate = database.getView(String.format("%s/%s", designDocName, byDateViewName));
        viewItemsByDate.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                Object createdAt = document.get("created_at");
                if (createdAt != null) {
                    emitter.emit(createdAt.toString(), null);
                }
            }
        }, "1.0");
        startSync();
    }

    public  void startSync() {
        URL syncUrl;
        try {
            syncUrl = new URL(SYNC_URL);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        Replication pullReplication = database.createPullReplication(syncUrl);
        pullReplication.setContinuous(true);

        Replication pushReplication = database.createPushReplication(syncUrl);
        pushReplication.setContinuous(true);

        pullReplication.start();
        pushReplication.start();

        pullReplication.addChangeListener(this);
        pushReplication.addChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void changed(Replication.ChangeEvent event) {


    }

}

