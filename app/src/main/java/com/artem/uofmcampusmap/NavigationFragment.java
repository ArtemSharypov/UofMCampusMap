package com.artem.uofmcampusmap;

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

import com.artem.uofmcampusmap.buildings.armes.ArmesFloor1Fragment;
import com.artem.uofmcampusmap.buildings.armes.ArmesFloor2Fragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

/**
 * Created by Artem on 2017-04-21.
 */

public class NavigationFragment extends Fragment{

    private MapView mMapView;
    private FrameLayout fragHolder;
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
    private int currInstructionPos;
    private final String OUTSIDE_ID = "Outdside";

    //todo REALLY need to split up the map fragment and building layouts being shown, causes problems with re-doing navigation
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);

        campusMap = new MapNavigationMesh();
        currInstructionPos = 0;
        currFloor = 0;
        currLocation = "";

        fragHolder = (FrameLayout) view.findViewById(R.id.frag_holder);

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
                    instructionsTextView.setText(route.getDirectionsAt(currInstructionPos));

                    PassRouteData activity = (PassRouteData) getActivity();
                    activity.setCurrInstructionPos(currInstructionPos);

                    if(currInstruction.getSource() instanceof OutdoorVertex)
                    {
                        if(!currLocation.equals(OUTSIDE_ID))
                        {
                            currLocation = OUTSIDE_ID;
                            switchToMapFrag();

                        }
                        else
                        {
                            DisplayRoute childFrag = (DisplayRoute) getChildFragmentManager().findFragmentById(R.id.frag_holder);

                            if(childFrag != null)
                            {
                                childFrag.updateDisplayedRoute();
                            }
                        }
                    }
                    else if(currInstruction.getSource() instanceof IndoorVertex)
                    {
                        handleIndoorSource(currInstruction);
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
                    currInstructionPos++;

                    Instruction currInstruction = route.getInstructionAt(currInstructionPos);
                    instructionsTextView.setText(route.getDirectionsAt(currInstructionPos));

                    PassRouteData activity = (PassRouteData) getActivity();
                    activity.setCurrInstructionPos(currInstructionPos);

                    if(currInstruction.getSource() instanceof OutdoorVertex)
                    {
                        if(!currLocation.equals(OUTSIDE_ID) )
                        {
                            currLocation = OUTSIDE_ID;
                            switchToMapFrag();
                        }
                        else
                        {
                            DisplayRoute childFrag = (DisplayRoute) getChildFragmentManager().findFragmentById(R.id.frag_holder);

                            if(childFrag != null)
                            {
                                childFrag.updateDisplayedRoute();
                            }
                        }

                    }
                    else if(currInstruction.getSource() instanceof IndoorVertex)
                    {
                        handleIndoorSource(currInstruction);
                    }
                }
            }
        });

        PassRouteData activity = (PassRouteData) getActivity();
        startLocation = activity.getStartLocation();
        startRoom = activity.getStartRoom();
        destinationLocation = activity.getDestinationLocation();
        destinationRoom = activity.getDestinationRoom();


        if(!startLocation.equals("") && !destinationLocation.equals(""))
        {
            route = campusMap.findRoute(startLocation, startRoom, destinationLocation, destinationRoom);

            if(route != null)
            {
                Instruction firstInstruction = route.getInstructionAt(0);

                instructionsTextView.setText(route.getDirectionsAt(0));
                instructionsLinLayout.setVisibility(View.VISIBLE);

                activity.passRoute(route);
                activity.setCurrInstructionPos(currInstructionPos);

                if(firstInstruction.getSource() instanceof IndoorVertex)
                {
                    handleIndoorSource(firstInstruction);
                }
                else
                {
                    switchToMapFrag();
                    currLocation = OUTSIDE_ID;
                }

            }
        }
        else
        {
            switchToMapFrag();
            currLocation = OUTSIDE_ID;
        }

        return view;
    }

    private void switchToMapFrag()
    {
        MapFragment mapFragment = new MapFragment();
        FragmentManager childFragManager = getChildFragmentManager();

        FragmentTransaction childFragTrans = childFragManager.beginTransaction();
        childFragTrans.add(R.id.frag_holder, mapFragment);
        childFragTrans.addToBackStack("map");
        childFragTrans.commit();
    }

    private void handleIndoorSource(Instruction instructionWithIndoorV)
    {
        IndoorVertex indoorSource;

        if(instructionWithIndoorV.getSource() instanceof IndoorVertex)
        {
            indoorSource = (IndoorVertex) instructionWithIndoorV.getSource();
            String currBuilding = indoorSource.getBuilding();
            int currFloor = indoorSource.getFloor();
            DisplayRoute childFrag = (DisplayRoute) getChildFragmentManager().findFragmentById(R.id.frag_holder);
            PassRouteData activity = (PassRouteData) getActivity();

            //todo probably clean this logic up a little
            if(!currLocation.equals(OUTSIDE_ID))
            {
                if(currBuilding.equals(currLocation))
                {
                    if(currFloor != this.currFloor)
                    {
                        activity.passRoute(route);

                        switchViewToBuilding(currBuilding, currFloor);
                    }
                    else
                    {
                        if(childFrag != null)
                        {
                            childFrag.updateDisplayedRoute();
                        }
                    }
                }
                else
                {
                    activity.passRoute(route);

                    switchViewToBuilding(currBuilding, currFloor);
                }
            }
            else
            {
                activity.passRoute(route);

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
                childFragTrans.add(R.id.frag_holder, armesFloor1Fragment);
                childFragTrans.addToBackStack("Armes1");
                childFragTrans.commit();
            }
            else if(currFloor == 2)
            {
                ArmesFloor2Fragment armesFloor2Fragment = new ArmesFloor2Fragment();

                childFragTrans = childFragManager.beginTransaction();
                childFragTrans.add(R.id.frag_holder, armesFloor2Fragment);
                childFragTrans.addToBackStack("Armes1");
                childFragTrans.commit();
            }
        }
    }
}
