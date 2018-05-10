package de.gymnasium_lappersdorf.gymlapapp.Maps;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import de.gymnasium_lappersdorf.gymlapapp.R;
import io.mapwize.mapwizeformapbox.MapOptions;
import io.mapwize.mapwizeformapbox.MapwizePlugin;
import io.mapwize.mapwizeformapbox.Marker;
import io.mapwize.mapwizeformapbox.api.Api;
import io.mapwize.mapwizeformapbox.api.ApiCallback;
import io.mapwize.mapwizeformapbox.model.Direction;
import io.mapwize.mapwizeformapbox.model.Place;
import io.mapwize.mapwizeformapbox.model.Venue;

/**
 * 10.05.2018 | created by Lukas S
 */

public class MapwizeFragment extends Fragment {

    private View v;
    private MapView mapView;
    private MapwizePlugin mapwizePlugin;
    private Venue venue_gymlap;
    private MapboxMap map;

    private Place start, target;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_mapwize, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Mapbox.getInstance(v.getContext(), getResources().getString(R.string.mapbox_access_token));

        mapView = v.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        MapOptions options = new MapOptions.Builder()
                .language("de")
                .build();

        mapwizePlugin = new MapwizePlugin(mapView, options);

        mapwizePlugin.setOnDidLoadListener(new MapwizePlugin.OnDidLoadListener() {
            @Override
            public void didLoad(MapwizePlugin mapwizePlugin) {
                // Mapwize is ready to use
                MapwizeFragment.this.mapwizePlugin = mapwizePlugin;
                initApi();
            }
        });

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                // Mapbox is ready to use
                map = mapboxMap;
            }
        });
    }

    //get access to api
    private void initApi() {

        mapwizePlugin.grantAccess(getResources().getString(R.string.mapwize_gymlap_access_key), new ApiCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                //Load venue
                Api.getVenue(getResources().getString(R.string.mapwize_venue_gymlap_id), new ApiCallback<Venue>() {
                    @Override
                    public void onSuccess(Venue venue) {
                        venue_gymlap = venue;
                        CameraPosition position = new CameraPosition.Builder()
                                .target(venue_gymlap.getMarker())
                                .zoom(18)
                                .tilt(0)
                                .build();
                        map.setCameraPosition(position);

                        buildInterface();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        //TODO
                    }
                });
            }

            @Override
            public void onFailure(Throwable throwable) {
                //TODO
            }
        });

    }

    //show building and implement listeners
    private void buildInterface() {

        Api.getPlace(getResources().getString(R.string.mapwize_gymlap_entrance_id), new ApiCallback<Place>() {
            @Override
            public void onSuccess(Place place) {
                start = place;
            }

            @Override
            public void onFailure(Throwable throwable) {
                //TODO
            }
        });

        mapwizePlugin.setOnPlaceClickListener(new MapwizePlugin.OnPlaceClickListener() {
            @Override
            public boolean onPlaceClick(Place place) {

                mapwizePlugin.removeMarkers();
                mapwizePlugin.addMarker(place);
                target = place;

                displayRoute();

                return false;
            }
        });

        mapwizePlugin.setOnMarkerClickListener(new MapwizePlugin.OnMarkerClickListener() {
            @Override
            public void onMarkerClick(Marker marker) {
                mapwizePlugin.removeMarkers();
            }
        });

    }

    //displays route between start and target
    private void displayRoute() {
        Api.getDirection(start, target, true, new ApiCallback<Direction>() {
            @Override
            public void onSuccess(Direction direction) {
                mapwizePlugin.removeDirection();
                mapwizePlugin.setDirection(direction);
            }

            @Override
            public void onFailure(Throwable throwable) {
                //TODO
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
