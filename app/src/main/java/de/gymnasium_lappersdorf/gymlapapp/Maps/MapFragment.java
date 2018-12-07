package de.gymnasium_lappersdorf.gymlapapp.Maps;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import de.gymnasium_lappersdorf.gymlapapp.R;

/**
 * 04.05.2018 | created by Lukas S
 */

public class MapFragment extends Fragment implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {

    private View v;
    private MapView mapView;
    private GoogleMap map;

    private Spinner optionSpinner;
    private final String[] OPTIONS = new String[]{"Gebäude", "Sekretariat"};

    private final LatLng GYMLAP = new LatLng(49.0453185, 12.0823808);
    private final LatLng GYMLAP_SEK = new LatLng(49.045420, 12.082991);


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_map, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Indoorkarte");

        //floor selection spinner

        optionSpinner = v.findViewById(R.id.option_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, OPTIONS);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        optionSpinner.setAdapter(adapter);
        optionSpinner.setOnItemSelectedListener(this);

        //map view

        mapView = v.findViewById(R.id.map);

        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());

        map = googleMap;

        map.clear();
        showBuilding();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

        switch (position) {
            case 0:
                // Gebäude
                map.clear();
                showBuilding();
                break;
            case 1:
                // Sekretariat
                map.clear();
                showSek();
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void showBuilding() {
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        map.addMarker(new MarkerOptions().position(GYMLAP).title("Gymnasium Lappersdorf"));
        CameraPosition gymlap = CameraPosition.builder().target(GYMLAP).zoom(17.25f).build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(gymlap));
    }

    private void showSek() {
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.addMarker(new MarkerOptions().position(GYMLAP_SEK).title("Sekretariat"));
        CameraPosition sek = CameraPosition.builder().target(GYMLAP_SEK).zoom(18.50f).build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(sek));
    }
}
