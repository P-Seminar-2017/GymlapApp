package de.gymnasium_lappersdorf.gymlapapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class StundenplanerTabAdapter extends FragmentPagerAdapter {

    //number of Tabs
    private static int NUM_ITEMS = 5;

    public StundenplanerTabAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        StundenplanerDayFragment s = new StundenplanerDayFragment();
        s.setDay(position);
        return s;
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}
