package de.gymnasium_lappersdorf.gymlapapp.Stundenplan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.gymnasium_lappersdorf.gymlapapp.R;

public class StundenplanerFragment extends Fragment {

    ViewPager vp;
    TabLayout tl;
    FloatingActionButton fab;
    int day;
    StundenplanerTabAdapter adapter;
    Toolbar tb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stundenplaner, container, false);

        fab = view.findViewById(R.id.stundenplan_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start Activity to add new hour
                Intent i = new Intent(getActivity(), AddHourActivity.class);
                i.putExtra("day", day);
                startActivityForResult(i, 1);
            }
        });

        vp = view.findViewById(R.id.pager);
        adapter = new StundenplanerTabAdapter(getChildFragmentManager());
        vp.setAdapter(adapter);

        tl = view.findViewById(R.id.tab_layout);
        tl.addTab(tl.newTab().setText("Mo"));
        tl.addTab(tl.newTab().setText("Di"));
        tl.addTab(tl.newTab().setText("Mi"));
        tl.addTab(tl.newTab().setText("Do"));
        tl.addTab(tl.newTab().setText("Fr"));
        vp.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tl));
        tl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp.setCurrentItem(tab.getPosition());
                day = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                day = tab.getPosition();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        tb = ((Activity)context).findViewById(R.id.toolbar_main);
        try {
            tb.setElevation(0);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            tb.setElevation(4);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }
}
