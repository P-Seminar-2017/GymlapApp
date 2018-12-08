package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * 07.12.2018 | created by Lukas S
 */
public class HomeworkTabAdapter extends FragmentPagerAdapter {

    private static final int NUM_TABS = 2;
    private HausaufgabenTabFragment[] fragments;

    public HomeworkTabAdapter(FragmentManager fm) {
        super(fm);
        fragments = new HausaufgabenTabFragment[2];
        fragments[0] = new HausaufgabenOnlineFragment();
        fragments[1] = new HausaufgabenLokalFragment();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return NUM_TABS;
    }
}
