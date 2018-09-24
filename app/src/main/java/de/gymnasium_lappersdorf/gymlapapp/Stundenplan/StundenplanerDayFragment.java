package de.gymnasium_lappersdorf.gymlapapp.Stundenplan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.gymnasium_lappersdorf.gymlapapp.R;

public class StundenplanerDayFragment extends Fragment {

    RecyclerView rv;
    long day;
    StundenplanRvAdapter rvAdapter;

    public void setDay(int d) {
        day = d;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_stundenplaner_day, container, false);
        rv = v.findViewById(R.id.stundenplaner_rv);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);
        rvAdapter = new StundenplanRvAdapter(DatabaseHandler.INSTANCE.getDay(this.day).lessons, this.day, getActivity());
        rv.setAdapter(rvAdapter);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshRV();
    }

    public void refreshRV() {
        rvAdapter.setDataset(DatabaseHandler.INSTANCE.getDay(this.day).lessons);
        rvAdapter.notifyDataSetChanged();
    }
}
