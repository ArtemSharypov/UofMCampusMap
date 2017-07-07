package com.artem.uofmcampusmap;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

/**
 * Created by Artem on 2017-06-28.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback, DisplayRoute {

    private GoogleMap googleMap;
    private MapView mMapView;
    private ArrayList<Polyline> routeLines;
    private ArrayList<Marker> mapMarkers;
    private int lastPosInRoute; //Last position that was used to update the route
    private int currPosInLines;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        routeLines = new ArrayList<>();
        mapMarkers = new ArrayList<>();

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
    public void updateDisplayedRoute()
    {
        PassRouteData activity = (PassRouteData) getActivity();
        int currRoutePos = activity.getCurrInstructionPos();

        if(currRoutePos >= 0)
        {
            int linePos = currRoutePos - lastPosInRoute;
            lastPosInRoute = currRoutePos;

            if(linePos > 0)
            {
                //means that next was pressed
                if(currPosInLines < routeLines.size())
                {
                    routeLines.get(currPosInLines).setVisible(false);
                    currPosInLines++;
                }
            }
            else
            {
                //means that previous was pressed
                if(currPosInLines > 0)
                {
                    currPosInLines--;
                    routeLines.get(currPosInLines).setVisible(true);
                }
            }
        }
    }

    @Override
    public void displayRoute()
    {
        LatLng startPoint;
        LatLng endPoint;
        Instruction currInstruction;
        PolylineOptions currLine;
        Vertex source;
        Vertex destination;
        PassRouteData activity = (PassRouteData) getActivity();
        int currRoutePos = activity.getCurrInstructionPos();
        Route route = activity.getRoute();

        if(route != null && route.getRouteLength() > 0 && googleMap != null)
        {
            lastPosInRoute = currRoutePos;
            currPosInLines = 0;

            while(currRoutePos < route.getNumInstructions())
            {
                currInstruction = route.getInstructionAt(currRoutePos);
                source = currInstruction.getSource();
                destination = currInstruction.getDestination();

                if(source instanceof OutdoorVertex && destination instanceof OutdoorVertex)
                {
                    startPoint = ((OutdoorVertex) source).getPosition();
                    endPoint = ((OutdoorVertex) destination).getPosition();
                    currLine = new PolylineOptions().add(startPoint)
                            .add(endPoint)
                            .color(Color.RED);

                    routeLines.add(googleMap.addPolyline(currLine));
                }
                else if(source instanceof IndoorVertex && destination instanceof IndoorVertex)
                {
                    break;
                }

                currRoutePos++;
            }
        }
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

        if(mMapView != null) {
            //remove all markers/lines to prevent a memory leak
            mapMarkers.clear();
            routeLines.clear();

            mMapView.onDestroy();
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        setUpMap();
    }

    public void setUpMap()
    {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        centerMap();
        addBuildingMarkers();
        displayRoute();
    }

    private void centerMap()
    {
        LatLng northEastCorner = new LatLng(49.815699, -97.129079);
        LatLng southWestCorner = new LatLng(49.798710, -97.152484);
        LatLngBounds uOfMCampus = new LatLngBounds(southWestCorner, northEastCorner);
        LatLng centerOfCampus = new LatLng(49.809496, -97.133810);
        float zoomAmount = 17f;

        googleMap.setLatLngBoundsForCameraTarget(uOfMCampus);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerOfCampus, zoomAmount));
    }

    private void addBuildingMarkers()
    {
        //todo make custom markers that just say the building name, over the building its meant to represent. maybe turn off onClicks for them

        LatLng agriculture = new LatLng(49.806950, -97.135443);
        Marker agri = googleMap.addMarker(new MarkerOptions().position(agriculture).title(getResources().getString(R.string.agriculture)));
        mapMarkers.add(agri);

        LatLng agr_engineering = new LatLng(49.807355, -97.133847);
        Marker agri_eng = googleMap.addMarker(new MarkerOptions().position(agr_engineering).title(getResources().getString(R.string.agr_engineer)));
        mapMarkers.add(agri_eng);

        LatLng allen = new LatLng(49.810686, -97.134651);
        Marker allenMarker = googleMap.addMarker(new MarkerOptions().position(allen).title(getResources().getString(R.string.allen)));
        mapMarkers.add(allenMarker);

        LatLng animal_sci = new LatLng(49.806048, -97.137872);
        Marker animalSci = googleMap.addMarker(new MarkerOptions().position(animal_sci).title(getResources().getString(R.string.animal_sci)));
        mapMarkers.add(animalSci);

        LatLng archi_2 = new LatLng(49.807834, -97.136534);
        Marker archi2M = googleMap.addMarker(new MarkerOptions().position(archi_2).title(getResources().getString(R.string.archi_2)));
        mapMarkers.add(archi2M);

        LatLng armes = new LatLng(49.810900, -97.133801);
        Marker armesM = googleMap.addMarker(new MarkerOptions().position(armes).title(getResources().getString(R.string.armes)));
        mapMarkers.add(armesM);

        LatLng art_lab = new LatLng(49.808568, -97.130191);
        Marker artlabM = googleMap.addMarker(new MarkerOptions().position(art_lab).title(getResources().getString(R.string.artlab)));
        mapMarkers.add(artlabM);

        LatLng bio_sci = new LatLng(49.810222, -97.134779);
        Marker bioSciM = googleMap.addMarker(new MarkerOptions().position(bio_sci).title(getResources().getString(R.string.bio_sci)));
        mapMarkers.add(bioSciM);

        LatLng buller = new LatLng(49.810516, -97.133458);
        Marker bullerM = googleMap.addMarker(new MarkerOptions().position(buller).title(getResources().getString(R.string.buller)));
        mapMarkers.add(bullerM);

        LatLng dairy_sci = new LatLng(49.807553, -97.133297);
        Marker dairySciM = googleMap.addMarker(new MarkerOptions().position(dairy_sci).title(getResources().getString(R.string.dairy_science)));
        mapMarkers.add(dairySciM);

        LatLng drake_centre = new LatLng(49.808066, -97.130061);
        Marker drakeCentreM = googleMap.addMarker(new MarkerOptions().position(drake_centre).title(getResources().getString(R.string.drake_centre)));
        mapMarkers.add(drakeCentreM);

        LatLng duff_roblin = new LatLng(49.811029, -97.132555);
        Marker duffRobM = googleMap.addMarker(new MarkerOptions().position(duff_roblin).title(getResources().getString(R.string.duff_roblin)));
        mapMarkers.add(duffRobM);

        LatLng education = new LatLng(49.808682, -97.136705);
        Marker educM = googleMap.addMarker(new MarkerOptions().position(education).title(getResources().getString(R.string.education)));
        mapMarkers.add(educM);

        LatLng eitc_e1 = new LatLng(49.8082972, -97.1334759);
        Marker eitcE1M= googleMap.addMarker(new MarkerOptions().position(eitc_e1).title(getResources().getString(R.string.eitc_e1)));
        mapMarkers.add(eitcE1M);

        LatLng eitc_e2 = new LatLng(49.8086711, -97.1336891);
        Marker eitcE2M = googleMap.addMarker(new MarkerOptions().position(eitc_e2).title(getResources().getString(R.string.eitc_e2)));
        mapMarkers.add(eitcE2M);

        LatLng eitc_e3 = new LatLng(49.8083665, -97.1344375);
        Marker eitcE3M= googleMap.addMarker(new MarkerOptions().position(eitc_e3).title(getResources().getString(R.string.eitc_e3)));
        mapMarkers.add(eitcE3M);

        LatLng elizabeth_dafoe = new LatLng(49.8102360, -97.1316754);
        Marker eliDafoeM= googleMap.addMarker(new MarkerOptions().position(elizabeth_dafoe).title(getResources().getString(R.string.elizabeth_dafoe)));
        mapMarkers.add(eliDafoeM);

        LatLng ext_education = new LatLng(49.807408, -97.138650);
        Marker extEducM= googleMap.addMarker(new MarkerOptions().position(ext_education).title(getResources().getString(R.string.ext_education)));
        mapMarkers.add(extEducM);

        LatLng fletcher = new LatLng(49.809714, -97.130965);
        Marker fletcherM = googleMap.addMarker(new MarkerOptions().position(fletcher).title(getResources().getString(R.string.fletcher)));
        mapMarkers.add(fletcherM);

        LatLng frank_kennedy = new LatLng(49.806954, -97.138744);
        Marker frankKenM= googleMap.addMarker(new MarkerOptions().position(frank_kennedy).title(getResources().getString(R.string.frank_kennedy)));
        mapMarkers.add(frankKenM);

        LatLng helen_glass = new LatLng(49.809063, -97.135516);
        Marker helenGlassM = googleMap.addMarker(new MarkerOptions().position(helen_glass).title(getResources().getString(R.string.helen_glass)));
        mapMarkers.add(helenGlassM);

        LatLng human_ecology = new LatLng(49.810734, -97.132233);
        Marker humanEcoM = googleMap.addMarker(new MarkerOptions().position(human_ecology).title(getResources().getString(R.string.human_ecology)));
        mapMarkers.add(humanEcoM);

        LatLng istbister = new LatLng(49.809384, -97.130538);
        Marker isbisterM =  googleMap.addMarker(new MarkerOptions().position(istbister).title(getResources().getString(R.string.isbister)));
        mapMarkers.add(isbisterM);

        LatLng machray = new LatLng(49.811170, -97.133385);
        Marker machrayM = googleMap.addMarker(new MarkerOptions().position(machray).title(getResources().getString(R.string.machray)));
        mapMarkers.add(machrayM);

        LatLng parker = new LatLng(49.811239, -97.134531);
        Marker parkerM = googleMap.addMarker(new MarkerOptions().position(parker).title(getResources().getString(R.string.parker)));
        mapMarkers.add(parkerM);

        LatLng plant_sci = new LatLng(49.806870, -97.134641);
        Marker plantSciM = googleMap.addMarker(new MarkerOptions().position(plant_sci).title(getResources().getString(R.string.plant_sci)));
        mapMarkers.add(plantSciM);

        LatLng robert_schultz = new LatLng(49.810074, -97.136627);
        Marker robSchultzM = googleMap.addMarker(new MarkerOptions().position(robert_schultz).title(getResources().getString(R.string.robert_schultz)));
        mapMarkers.add(robSchultzM);

        LatLng robson = new LatLng(49.811844, -97.130639);
        Marker robsonM = googleMap.addMarker(new MarkerOptions().position(robson).title(getResources().getString(R.string.robson)));
        mapMarkers.add(robsonM);

        LatLng russel = new LatLng(49.808051, -97.135293);
        Marker russelM= googleMap.addMarker(new MarkerOptions().position(russel).title(getResources().getString(R.string.russel)));
        mapMarkers.add(russelM);

        LatLng st_johns = new LatLng(49.810565, -97.136832);
        Marker stJohnsM = googleMap.addMarker(new MarkerOptions().position(st_johns).title(getResources().getString(R.string.st_johns)));
        mapMarkers.add(stJohnsM);

        LatLng st_pauls = new LatLng(49.810266, -97.137926);
        Marker stPaulsM = googleMap.addMarker(new MarkerOptions().position(st_pauls).title(getResources().getString(R.string.st_pauls)));
        mapMarkers.add(stPaulsM);

        LatLng tier = new LatLng(49.809219, -97.130942);
        Marker tierM = googleMap.addMarker(new MarkerOptions().position(tier).title(getResources().getString(R.string.tier)));
        mapMarkers.add(tierM);

        LatLng uni_centre = new LatLng(49.8094187, -97.1347299);
        Marker uniCentreM= googleMap.addMarker(new MarkerOptions().position(uni_centre).title(getResources().getString(R.string.uni_centre)));
        mapMarkers.add(uniCentreM);

        LatLng uni_college = new LatLng(49.811203, -97.131089);
        Marker uniCollegeM = googleMap.addMarker(new MarkerOptions().position(uni_college).title(getResources().getString(R.string.uni_college)));
        mapMarkers.add(uniCollegeM);

        LatLng wallace = new LatLng(49.811757, -97.135920);
        Marker wallaceM = googleMap.addMarker(new MarkerOptions().position(wallace).title(getResources().getString(R.string.wallace)));
        mapMarkers.add(wallaceM);
    }
}
