package de.gymnasium_lappersdorf.gymlapapp.Info;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import de.gymnasium_lappersdorf.gymlapapp.R;

public class InfoFragment extends Fragment {

    RelativeLayout openLicense;
    ImageView toggleLicense;
    ScrollView txtLicense;

    public InfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_info, container, false);
        openLicense = v.findViewById(R.id.open_license);
        toggleLicense = v.findViewById(R.id.toggle_license);
        txtLicense = v.findViewById(R.id.txt_license);
        openLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtLicense.setVisibility(txtLicense.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                toggleLicense.setImageDrawable(getActivity().getDrawable(txtLicense.getVisibility()==View.VISIBLE ? R.drawable.arrowup : R.drawable.arrowdown));
            }
        });
        return v;
    }


}
