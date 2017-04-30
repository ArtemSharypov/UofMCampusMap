package com.artem.uofmcampusmap;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by Artem on 2017-04-21.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback{

    private MapView mMapView;
    private GoogleMap googleMap;
    private ImageView previousDirection;
    private ImageView nextDirection;
    private TextView currentDirections;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        previousDirection = (ImageView) view.findViewById(R.id.previous_direction);
        //set onClickListener

        nextDirection = (ImageView) view.findViewById(R.id.next_direction);
        //set onClickListener

        currentDirections = (TextView) view.findViewById(R.id.current_directions);

        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try
        {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }


    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        setUpMap();
    }

    public void setUpMap()
    {
        centerMap();
        addBuildingEntranceMarkers();
    }

    private void centerMap()
    {
        LatLng northEastCorner = new LatLng(49.814274, -97.131857);
        LatLng southWestCorner = new LatLng(49.798710, -97.152484);
        LatLngBounds uOfMCampus = new LatLngBounds(northEastCorner, southWestCorner);

        googleMap.setLatLngBoundsForCameraTarget(uOfMCampus);

        LatLng centerOfCampus = new LatLng(49.809496, -97.133810);

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(centerOfCampus));
    }

    private void addBuildingEntranceMarkers()
    {
        //adds building entrances on map
    }
}
