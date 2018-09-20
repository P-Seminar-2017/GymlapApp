package de.gymnasium_lappersdorf.gymlapapp;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import de.gymnasium_lappersdorf.gymlapapp.Navigation.NavDrawerOnclickHandler;
import de.gymnasium_lappersdorf.gymlapapp.Stundenplan.DatabaseHandler;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drl;
    Toolbar tb;
    NavDrawerOnclickHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing Stundenplan-DB at app start
        DatabaseHandler.INSTANCE.initialize(this);

        //setting Toolbar instead of Actionbar
        tb = findViewById(R.id.toolbar_main);
        setSupportActionBar(tb);

        //navigation Drawer
        drl = findViewById(R.id.drawer_layout_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, drl, tb, R.string.drawer_close, R.string.drawer_open);
        drl.addDrawerListener(toggle);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        getSupportActionBar().setHomeButtonEnabled(true);
        toggle.syncState();
        NavigationView nv = findViewById(R.id.navigation);

        handler = new NavDrawerOnclickHandler(MainActivity.this);
        nv.setNavigationItemSelectedListener(handler);
        //setting HomeFragment as default fragment when launching Activity
        handler.setHomeFragment();
    }


}
