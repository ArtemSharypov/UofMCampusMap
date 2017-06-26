package com.artem.uofmcampusmap;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.artem.uofmcampusmap.buildings.DisplayIndoorRoutes;
import com.artem.uofmcampusmap.buildings.armes.ArmesFloor1Fragment;
import com.artem.uofmcampusmap.buildings.armes.ArmesFloor2Fragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

/**
 * Created by Artem on 2017-04-21.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback{

    private MapView mMapView;
    private FrameLayout indoorBuildingFragHolder;
    private GoogleMap googleMap;
    private ImageView prevInstruction;
    private ImageView nextInstruction;
    private TextView instructionsTextView;
    private LinearLayout instructionsLinLayout;
    private MapNavigationMesh campusMap;
    private String startLocation;
    private String startRoom;
    private String destinationLocation;
    private String destinationRoom;
    private String currLocation;
    private int currFloor;
    private Route route;
    private ArrayList<Polyline> routeLines;
    private int currInstructionPos;
    private int currOutsideLine; //The current position within routeLines for outside polylines

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        campusMap = new MapNavigationMesh();
        routeLines = new ArrayList<>();
        currInstructionPos = 0;
        currOutsideLine = 0;
        currFloor = 0;
        currLocation = "";

        indoorBuildingFragHolder = (FrameLayout) view.findViewById(R.id.indoor_building_frag_holder);

        instructionsTextView = (TextView) view.findViewById(R.id.current_instructions);
        instructionsLinLayout = (LinearLayout) view.findViewById(R.id.instructions_layout);

        prevInstruction = (ImageView) view.findViewById(R.id.prev_instruction);
        prevInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currInstructionPos > 0)
                {
                    currInstructionPos--;
                    Instruction currInstruction = route.getInstructionAt(currInstructionPos);
                    instructionsTextView.setText(currInstruction.getInstructions());

                    if(currInstruction.getSource() instanceof OutdoorVertex)
                    {
                        if(mMapView.getVisibility() == View.GONE)
                        {
                            mMapView.setVisibility(View.VISIBLE);
                            mMapView.onResume();
                            indoorBuildingFragHolder.setVisibility(View.GONE);
                            currLocation = "";

                        }
                        else if(currOutsideLine > 0)
                        {
                            currOutsideLine--;
                            routeLines.get(currOutsideLine).setVisible(true);
                        }
                    }
                    else if(currInstruction.getSource() instanceof IndoorVertex)
                    {
                        handleIndoorSource(currInstruction, false);
                    }

                }
            }
        });

        nextInstruction = (ImageView) view.findViewById(R.id.next_instruction);
        nextInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currInstructionPos + 1 < route.getNumInstructions())
                {
                    Instruction currInstruction = route.getInstructionAt(currInstructionPos);
                    currInstructionPos++;

                    instructionsTextView.setText(currInstruction.getInstructions());

                    if(currInstruction.getSource() instanceof OutdoorVertex)
                    {
                        if(mMapView.getVisibility() == View.GONE)
                        {
                            mMapView.setVisibility(View.VISIBLE);
                            mMapView.onResume();
                            indoorBuildingFragHolder.setVisibility(View.GONE);
                            currLocation = "";

                        }
                        else if(currOutsideLine < route.getNumInstructions() && currOutsideLine < routeLines.size())
                        {
                            routeLines.get(currOutsideLine).setVisible(false);
                            currOutsideLine++;
                        }

                    }
                    else if(currInstruction.getSource() instanceof IndoorVertex)
                    {
                        handleIndoorSource(currInstruction, true);
                    }
                }
            }
        });

        PassRouteData activity = (PassRouteData) getActivity();
        startLocation = activity.getStartLocation();
        startRoom = activity.getStartRoom();
        destinationLocation = activity.getDestinationLocation();
        destinationRoom = activity.getDestinationRoom();

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

        if(!startLocation.equals("") && !destinationLocation.equals(""))
        {
            route = campusMap.findRoute(startLocation, startRoom, destinationLocation, destinationRoom);

            if(route != null)
            {
                Instruction firstInstruction = route.getFirstInstruction();

                instructionsTextView.setText(firstInstruction.getInstructions());
                instructionsLinLayout.setVisibility(View.VISIBLE);

                activity.passRoute(route);
                activity.setCurrInstructionPos(currInstructionPos);

                if(firstInstruction.getSource() instanceof IndoorVertex)
                {
                    handleIndoorSource(firstInstruction, true);
                }

            }
        }

        return view;
    }

    //needs a true/false if its next or previous
    //also needs to be a edge so that the displaying works
    private void handleIndoorSource(Instruction instructionWithIndoorV, boolean isNextInstruc)
    {
        IndoorVertex indoorSource;

        if(instructionWithIndoorV.getSource() instanceof IndoorVertex)
        {
            indoorSource = (IndoorVertex) instructionWithIndoorV.getSource();
            String currBuilding = indoorSource.getBuilding();
            int currFloor = indoorSource.getFloor();
            DisplayIndoorRoutes childFrag = (DisplayIndoorRoutes) getChildFragmentManager().findFragmentById(R.id.indoor_building_frag_holder);

            //todo probably clean this logic up a little
            if(indoorBuildingFragHolder.getVisibility() == View.VISIBLE)
            {
                if(currBuilding.equals(currLocation))
                {
                    if(currFloor != this.currFloor)
                    {
                        switchViewToBuilding(currBuilding, currFloor);
                    }
                    else
                    {
                        if(childFrag != null)
                        {
                            PassRouteData activity = (PassRouteData) getActivity();
                            activity.setCurrInstructionPos(currInstructionPos);

                            childFrag.updateDisplayedRoute();
                        }
                    }
                }
                else
                {
                    switchViewToBuilding(currBuilding, currFloor);
                }
            }
            else
            {

                indoorBuildingFragHolder.setVisibility(View.VISIBLE);
                mMapView.setVisibility(View.GONE);
                mMapView.onPause();

                switchViewToBuilding(currBuilding, currFloor);
            }
        }
    }

    private void switchViewToBuilding(String buildingName, int floorNumber)
    {
        if(buildingName.equals(getResources().getString(R.string.armes)))
        {
            currLocation = buildingName;
            currFloor = floorNumber;
            FragmentManager childFragManager = getChildFragmentManager();
            FragmentTransaction childFragTrans;

            if(currFloor == 1)
            {
                ArmesFloor1Fragment armesFloor1Fragment = new ArmesFloor1Fragment();

                childFragTrans = childFragManager.beginTransaction();
                childFragTrans.add(R.id.indoor_building_frag_holder, armesFloor1Fragment);
                childFragTrans.addToBackStack("Armes1");
                childFragTrans.commit();
            }
            else if(currFloor == 2)
            {
                ArmesFloor2Fragment armesFloor2Fragment = new ArmesFloor2Fragment();

                childFragTrans = childFragManager.beginTransaction();
                childFragTrans.add(R.id.indoor_building_frag_holder, armesFloor2Fragment);
                childFragTrans.addToBackStack("Armes1");
                childFragTrans.commit();
            }
        }
    }

    private void drawRouteOnMap()
    {
        LatLng startPoint;
        LatLng endPoint;
        Instruction currInstruction;
        PolylineOptions currLine;
        Vertex source;
        Vertex destination;

        if(route != null && route.getRouteLength() > 0 && googleMap != null)
        {
            currInstruction = route.getFirstInstruction();

            while(currInstruction != null)
            {
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

                currInstruction = route.getNextInstruction();
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

        if(mMapView != null)
            mMapView.onDestroy();
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
        drawRouteOnMap();
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
        googleMap.addMarker(new MarkerOptions().position(agriculture).title(getResources().getString(R.string.agriculture)));

        LatLng agr_engineering = new LatLng(49.807355, -97.133847);
        googleMap.addMarker(new MarkerOptions().position(agr_engineering).title(getResources().getString(R.string.agr_engineer)));

        LatLng allen = new LatLng(49.810686, -97.134651);
        googleMap.addMarker(new MarkerOptions().position(allen).title(getResources().getString(R.string.allen)));

        LatLng animal_sci = new LatLng(49.806048, -97.137872);
        googleMap.addMarker(new MarkerOptions().position(animal_sci).title(getResources().getString(R.string.animal_sci)));

        LatLng archi_2 = new LatLng(49.807834, -97.136534);
        googleMap.addMarker(new MarkerOptions().position(archi_2).title(getResources().getString(R.string.archi_2)));

        LatLng armes = new LatLng(49.810900, -97.133801);
        googleMap.addMarker(new MarkerOptions().position(armes).title(getResources().getString(R.string.armes)));

        LatLng art_lab = new LatLng(49.808568, -97.130191);
        googleMap.addMarker(new MarkerOptions().position(art_lab).title(getResources().getString(R.string.artlab)));

        LatLng bio_sci = new LatLng(49.810222, -97.134779);
        googleMap.addMarker(new MarkerOptions().position(bio_sci).title(getResources().getString(R.string.bio_sci)));

        LatLng buller = new LatLng(49.810516, -97.133458);
        googleMap.addMarker(new MarkerOptions().position(buller).title(getResources().getString(R.string.buller)));

        LatLng dairy_sci = new LatLng(49.807553, -97.133297);
        googleMap.addMarker(new MarkerOptions().position(dairy_sci).title(getResources().getString(R.string.dairy_science)));

        LatLng drake_centre = new LatLng(49.808066, -97.130061);
        googleMap.addMarker(new MarkerOptions().position(drake_centre).title(getResources().getString(R.string.drake_centre)));

        LatLng duff_roblin = new LatLng(49.811029, -97.132555);
        googleMap.addMarker(new MarkerOptions().position(duff_roblin).title(getResources().getString(R.string.duff_roblin)));

        LatLng education = new LatLng(49.808682, -97.136705);
        googleMap.addMarker(new MarkerOptions().position(education).title(getResources().getString(R.string.education)));

        LatLng eitc_e1 = new LatLng(49.8082972, -97.1334759);
        googleMap.addMarker(new MarkerOptions().position(eitc_e1).title(getResources().getString(R.string.eitc_e1)));

        LatLng eitc_e2 = new LatLng(49.8086711, -97.1336891);
        googleMap.addMarker(new MarkerOptions().position(eitc_e2).title(getResources().getString(R.string.eitc_e2)));

        LatLng eitc_e3 = new LatLng(49.8083665, -97.1344375);
        googleMap.addMarker(new MarkerOptions().position(eitc_e3).title(getResources().getString(R.string.eitc_e3)));

        LatLng elizabeth_dafoe = new LatLng(49.8102360, -97.1316754);
        googleMap.addMarker(new MarkerOptions().position(elizabeth_dafoe).title(getResources().getString(R.string.elizabeth_dafoe)));

        LatLng ext_education = new LatLng(49.807408, -97.138650);
        googleMap.addMarker(new MarkerOptions().position(ext_education).title(getResources().getString(R.string.ext_education)));

        LatLng fac_music = new LatLng(49.807224, -97.135906);
        googleMap.addMarker(new MarkerOptions().position(fac_music).title(getResources().getString(R.string.fac_music)));

        LatLng fletcher = new LatLng(49.809714, -97.130965);
        googleMap.addMarker(new MarkerOptions().position(fletcher).title(getResources().getString(R.string.fletcher)));

        LatLng frank_kennedy = new LatLng(49.806954, -97.138744);
        googleMap.addMarker(new MarkerOptions().position(frank_kennedy).title(getResources().getString(R.string.frank_kennedy)));

        LatLng helen_glass = new LatLng(49.809063, -97.135516);
        googleMap.addMarker(new MarkerOptions().position(helen_glass).title(getResources().getString(R.string.helen_glass)));

        LatLng human_ecology = new LatLng(49.810734, -97.132233);
        googleMap.addMarker(new MarkerOptions().position(human_ecology).title(getResources().getString(R.string.human_ecology)));

        LatLng istbister = new LatLng(49.809384, -97.130538);
        googleMap.addMarker(new MarkerOptions().position(istbister).title(getResources().getString(R.string.isbister)));

        LatLng machray = new LatLng(49.811170, -97.133385);
        googleMap.addMarker(new MarkerOptions().position(machray).title(getResources().getString(R.string.machray)));

        LatLng music_2 = new LatLng(49.807759, -97.134333);
        googleMap.addMarker(new MarkerOptions().position(music_2).title(getResources().getString(R.string.music_annex)));

        LatLng parker = new LatLng(49.811239, -97.134531);
        googleMap.addMarker(new MarkerOptions().position(parker).title(getResources().getString(R.string.parker)));

        LatLng plant_sci = new LatLng(49.806870, -97.134641);
        googleMap.addMarker(new MarkerOptions().position(plant_sci).title(getResources().getString(R.string.plant_sci)));

        LatLng robert_schultz = new LatLng(49.810074, -97.136627);
        googleMap.addMarker(new MarkerOptions().position(robert_schultz).title(getResources().getString(R.string.robert_schultz)));

        LatLng robson = new LatLng(49.811844, -97.130639);
        googleMap.addMarker(new MarkerOptions().position(robson).title(getResources().getString(R.string.robson)));

        LatLng russel = new LatLng(49.808051, -97.135293);
        googleMap.addMarker(new MarkerOptions().position(russel).title(getResources().getString(R.string.russel)));

        LatLng st_johns = new LatLng(49.810565, -97.136832);
        googleMap.addMarker(new MarkerOptions().position(st_johns).title(getResources().getString(R.string.st_johns)));

        LatLng st_pauls = new LatLng(49.810266, -97.137926);
        googleMap.addMarker(new MarkerOptions().position(st_pauls).title(getResources().getString(R.string.st_pauls)));

        LatLng tier = new LatLng(49.809219, -97.130942);
        googleMap.addMarker(new MarkerOptions().position(tier).title(getResources().getString(R.string.tier)));

        LatLng uni_centre = new LatLng(49.8094187, -97.1347299);
        googleMap.addMarker(new MarkerOptions().position(uni_centre).title(getResources().getString(R.string.uni_centre)));

        LatLng uni_college = new LatLng(49.811203, -97.131089);
        googleMap.addMarker(new MarkerOptions().position(uni_college).title(getResources().getString(R.string.uni_college)));

        LatLng wallace = new LatLng(49.811757, -97.135920);
        googleMap.addMarker(new MarkerOptions().position(wallace).title(getResources().getString(R.string.wallace)));
    }
}
