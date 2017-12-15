package de.gymnasium_lappersdorf.gymlapapp;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

/**
 * Created by leon on 08.12.17.
 */

public class NavDrawerOnclickHandler implements NavigationView.OnNavigationItemSelectedListener {

    Context c;
    DrawerLayout dl;

    public NavDrawerOnclickHandler(Context c) {
        this.c = c;
        dl = ((Activity) c).findViewById(R.id.drawer_layout_main);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id._home:
                android.app.Fragment h = new HomeFragment();
                FragmentManager fm = ((Activity) c).getFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.content_frame_main, h)
                        .commit();
                item.setChecked(true);
                dl.closeDrawers();
                item.setChecked(false);
                return true;
            case R.id.stundenplaner:
                //todo add stundenplaner fragment
                return true;

        }
        return false;
    }
}
