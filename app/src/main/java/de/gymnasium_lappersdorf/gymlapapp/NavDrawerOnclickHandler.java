package de.gymnasium_lappersdorf.gymlapapp;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import de.gymnasium_lappersdorf.gymlapapp.Home.HomeFragment;
import de.gymnasium_lappersdorf.gymlapapp.Stundenplan.StundenplanerFragment;

/**
 * Created by leon on 08.12.17.
 */

public class NavDrawerOnclickHandler implements NavigationView.OnNavigationItemSelectedListener {

    Context c;
    DrawerLayout dl;
    Fragment h, s;
    FragmentManager fm;

    public NavDrawerOnclickHandler(Context c) {
        this.c = c;
        dl = ((Activity) c).findViewById(R.id.drawer_layout_main);
        fm = ((AppCompatActivity) c).getSupportFragmentManager();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id._home:
                item.setChecked(true);
                setHomeFragment();
                item.setChecked(false);
                return true;
            case R.id.stundenplaner:
                item.setChecked(true);
                setSPFragment();
                item.setChecked(false);
                return true;
        }
        return false;
    }

    public void setHomeFragment(){
        if (h==null) h = new HomeFragment();
        fm.beginTransaction()
                .replace(R.id.content_frame_main, h)
                .commit();
        dl.closeDrawers();
    }

    public void setSPFragment(){
        if (s==null) s = new StundenplanerFragment();
        fm.beginTransaction()
                .replace(R.id.content_frame_main, s)
                .commit();
        dl.closeDrawers();
    }
}
