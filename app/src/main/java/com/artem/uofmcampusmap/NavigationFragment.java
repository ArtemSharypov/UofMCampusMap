package com.artem.uofmcampusmap;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.artem.uofmcampusmap.buildings.allen.Allen_Floor1;
import com.artem.uofmcampusmap.buildings.allen.Allen_Floor2;
import com.artem.uofmcampusmap.buildings.allen.Allen_Floor3;
import com.artem.uofmcampusmap.buildings.allen.Allen_Floor4;
import com.artem.uofmcampusmap.buildings.allen.Allen_Floor5;
import com.artem.uofmcampusmap.buildings.armes.ArmesFloor1Fragment;
import com.artem.uofmcampusmap.buildings.armes.ArmesFloor2Fragment;
import com.artem.uofmcampusmap.buildings.machray.Machray_Floor1;
import com.artem.uofmcampusmap.buildings.machray.Machray_Floor2;
import com.artem.uofmcampusmap.buildings.machray.Machray_Floor3;
import com.artem.uofmcampusmap.buildings.machray.Machray_Floor4;
import com.artem.uofmcampusmap.buildings.machray.Machray_Floor5;

public class NavigationFragment extends Fragment{

    //Used for finding a route based on startLocation, startRoom, destinationLocation, destinationRoom as a background task
    private class RouteCreationTask extends AsyncTask<Void, Void, Route>
    {
        @Override
        protected Route doInBackground(Void... voids) {
            Route routeCreated = null;

            if(!startLocation.equals("") && !destinationLocation.equals(""))
            {
                if(startLocation.equals(getResources().getString(R.string.curr_location)) ||
                        destinationLocation.equals(getResources().getString(R.string.curr_location)))
                {
                }
                else
                {
                    routeCreated = campusMap.getRoute(startLocation, startRoom, destinationLocation, destinationRoom);
                }
            }

            return routeCreated;
        }

        @Override
        protected void onPostExecute(Route routeCreated) {
            if(routeCreated != null)
            {
                route = routeCreated; //Update the route being displayed
                remainingDistance = routeCreated.getRouteLength();
                Instruction firstInstruction = routeCreated.getInstructionAt(0);

                updateDistanceTimeRemaining();
                updateShownInstruction();
                updateActivityRoutePos();
                updateActivityRoute(routeCreated);

                instructionsLinLayout.setVisibility(View.VISIBLE);

                if(firstInstruction.getSource() instanceof IndoorVertex)
                {
                    handleIndoorSource(firstInstruction);
                }
                else
                {
                    //Special case for only outdoor locations, since the display will already be the map and it won't have to change
                    //at all
                    if(currLocation.equals(OUTSIDE_ID))
                    {
                        DisplayRoute childFrag = (DisplayRoute) getChildFragmentManager().findFragmentById(R.id.frag_holder);

                        if(childFrag != null)
                        {
                            childFrag.displayRoute();
                        }
                    }
                }
            }
            else if(startLocation.equals(getResources().getString(R.string.curr_location)) ||
                    destinationLocation.equals(getResources().getString(R.string.curr_location)))
            {
                if(!isLocationServicesEnabled())
                {
                    Toast.makeText(getContext(),
                            "Couldn't find your location, make sure GPS is enabled", Toast.LENGTH_LONG)
                            .show();
                }
                else
                {
                    Toast.makeText(getContext(),
                            "Couldn't find a route from where you currently are", Toast.LENGTH_LONG)
                            .show();
                }
            }
            else
            {
                Toast.makeText(getContext(),
                        "Couldn't find a route from the desired locations", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    private FrameLayout fragHolder;
    private ImageView prevInstruction;
    private ImageView nextInstruction;
    private TextView instructionsTextView;
    private TextView distanceRemainingTV;
    private TextView estTimeRemainingTV;
    private LinearLayout instructionsLinLayout;
    private MapGraph campusMap;
    private String startLocation;
    private String startRoom;
    private String destinationLocation;
    private String destinationRoom;
    private String currLocation;
    private int currFloor;
    private Route route;
    private int currInstructionPos;
    private final String OUTSIDE_ID = "Outside";
    private double remainingDistance;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);

        campusMap = new MapGraph(getResources());
        currInstructionPos = 0;
        currFloor = 0;
        currLocation = "";

        fragHolder = (FrameLayout) view.findViewById(R.id.frag_holder);

        instructionsTextView = (TextView) view.findViewById(R.id.current_instructions);
        instructionsLinLayout = (LinearLayout) view.findViewById(R.id.instructions_layout);

        distanceRemainingTV = (TextView) view.findViewById(R.id.distance_remaining);
        estTimeRemainingTV = (TextView)  view.findViewById(R.id.time_remaining);

        prevInstruction = (ImageView) view.findViewById(R.id.prev_instruction);
        prevInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currInstructionPos > 0)
                {
                    currInstructionPos--;

                    Instruction currInstruction = route.getInstructionAt(currInstructionPos);
                    remainingDistance += currInstruction.getDistanceInMetres();

                    updateShownInstruction();
                    updateDistanceTimeRemaining();
                    updateActivityRoutePos();

                    if(currInstruction.getSource() instanceof OutdoorVertex)
                    {
                        if(currLocation.equals(OUTSIDE_ID))
                        {
                            updateCurrDisplayedRoute();
                        }
                        else
                        {
                            currLocation = OUTSIDE_ID;
                            switchToMapFrag();
                        }
                    }
                    else
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
                    remainingDistance -= route.getInstructionAt(currInstructionPos).getDistanceInMetres();

                    currInstructionPos++;

                    Instruction currInstruction = route.getInstructionAt(currInstructionPos);

                    updateShownInstruction();
                    updateDistanceTimeRemaining();
                    updateActivityRoutePos();

                    if(currInstruction.getSource() instanceof OutdoorVertex)
                    {
                        if(!currLocation.equals(OUTSIDE_ID) )
                        {
                            currLocation = OUTSIDE_ID;
                            switchToMapFrag();
                        }
                        else
                        {
                            updateCurrDisplayedRoute();
                        }

                    }
                    else if(currInstruction.getSource() instanceof IndoorVertex)
                    {
                        handleIndoorSource(currInstruction);
                    }
                }
                else
                {
                    if(currInstructionPos + 1 == route.getNumInstructions())
                    {
                        remainingDistance -= route.getInstructionAt(currInstructionPos).getDistanceInMetres();
                        currInstructionPos++;

                        updateShownInstruction();
                        updateDistanceTimeRemaining();
                        updateActivityRoutePos();
                        updateCurrDisplayedRoute();
                    }
                }
            }
        });

        PassRouteData activity = (PassRouteData) getActivity();
        startLocation = activity.getStartLocation();
        startRoom = activity.getStartRoom();
        destinationLocation = activity.getDestinationLocation();
        destinationRoom = activity.getDestinationRoom();

        ChangeToolbarTitleInterface toolbarActivity = (ChangeToolbarTitleInterface) getActivity();
        toolbarActivity.replaceToolbarTitle("");

        switchToMapFrag();
        currLocation = OUTSIDE_ID;

        if(!startLocation.equals("") && !destinationLocation.equals(""))
        {
            new RouteCreationTask().execute();
        }

        return view;
    }

    //Calls the child fragment (if there is one) to update the displayed path
    private void updateCurrDisplayedRoute()
    {
        DisplayRoute childFrag = (DisplayRoute) getChildFragmentManager().findFragmentById(R.id.frag_holder);

        if(childFrag != null)
        {
            childFrag.updateDisplayedRoute();
        }
    }

    //Updates the current route within the activity
    private void updateActivityRoute(Route route)
    {
        PassRouteData activity = (PassRouteData) getActivity();
        activity.passRoute(route);
    }

    //Updates the current instruction being shown
    private void updateShownInstruction()
    {
        if(route != null) {
            if (currInstructionPos < route.getNumInstructions()) {
                instructionsTextView.setText(route.getDirectionsAt(currInstructionPos));
            } else {
                instructionsTextView.setText(getResources().getString(R.string.arrived_msg));
            }
        }
    }

    private void updateDistanceTimeRemaining()
    {
        distanceRemainingTV.setText(" " + getResources().getString(R.string.distance_remaining) + remainingDistance + " m");
        estTimeRemainingTV.setText(" " + getResources().getString(R.string.est_time) + amountOfTime(remainingDistance) + " minute(s)");
    }

    private void updateActivityRoutePos()
    {
        PassRouteData activity = (PassRouteData) getActivity();
        activity.setCurrInstructionPos(currInstructionPos);
    }

    //Checks if location services ar enabled at all
    private boolean isLocationServicesEnabled()
    {
        boolean enabled = false;
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            enabled = true;
        }

        return enabled;
    }

    //Tries to estimate the amount of time needed to walk the distance remaining on the Route
    private int amountOfTime(double distance)
    {
        final double WALK_SPEED_METERS_PER_MIN = 66.67;

        return (int) Math.ceil(distance / WALK_SPEED_METERS_PER_MIN);
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

    //Potentially switches the child fragment being displayed, if the instructions building or floor has switched
    //If not then it will just update the path shown
    private void handleIndoorSource(Instruction instructionWithIndoorV)
    {
        IndoorVertex indoorSource;

        if(instructionWithIndoorV.getSource() instanceof IndoorVertex)
        {
            indoorSource = (IndoorVertex) instructionWithIndoorV.getSource();
            String currBuilding = indoorSource.getBuilding();
            int currFloor = indoorSource.getFloor();

            if(!currLocation.equals(OUTSIDE_ID) && currBuilding.equals(currLocation))
            {
                if(currFloor == this.currFloor)
                {
                    updateCurrDisplayedRoute();
                }
                else
                {
                    switchViewToBuilding(currBuilding, currFloor);
                }
            }
            else
            {
                switchViewToBuilding(currBuilding, currFloor);
            }
        }
    }

    //Checks if there is already the same fragment on the backstack, then use it. Else replace the current fragment with the one passed
    private void replaceFragment(Fragment fragment, String stateName)
    {
        FragmentManager childFragManager = getChildFragmentManager();
        boolean fragmentPopped = childFragManager.popBackStackImmediate(currLocation + currFloor, 0);

        if(!fragmentPopped)
        {
            FragmentTransaction childFragTrans = childFragManager.beginTransaction();
            childFragTrans.replace(R.id.frag_holder, fragment);
            childFragTrans.addToBackStack(stateName);
            childFragTrans.commit();
        }
    }

    //Switches to a specified building building or floor number and updated the current location / floor
    private void switchViewToBuilding(String buildingName, int floorNumber)
    {
        if(buildingName.equals(getResources().getString(R.string.armes)))
        {
            currLocation = buildingName;
            currFloor = floorNumber;

            if(currFloor == 1)
            {
                ArmesFloor1Fragment armesFloor1Fragment = new ArmesFloor1Fragment();

                replaceFragment(armesFloor1Fragment, currLocation + currFloor);
            }
            else if(currFloor == 2)
            {
                ArmesFloor2Fragment armesFloor2Fragment = new ArmesFloor2Fragment();

                replaceFragment(armesFloor2Fragment, currLocation + currFloor);
            }
        }
        else if(buildingName.equals(getResources().getString(R.string.machray)))
        {
            currLocation = buildingName;
            currFloor = floorNumber;

            if(currFloor == 1)
            {
                Machray_Floor1 machrayFloor1Frag = new Machray_Floor1();

                replaceFragment(machrayFloor1Frag, currLocation + currFloor);
            }
            else if(currFloor == 2)
            {
                Machray_Floor2 machrayFloor2Frag = new Machray_Floor2();

                replaceFragment(machrayFloor2Frag, currLocation + currFloor);
            }
            else if(currFloor == 3)
            {
                Machray_Floor3 machrayFloor3Frag = new Machray_Floor3();

                replaceFragment(machrayFloor3Frag, currLocation + currFloor);
            }
            else if(currFloor == 4)
            {
                Machray_Floor4 machrayFloor4Frag = new Machray_Floor4();

                replaceFragment(machrayFloor4Frag, currLocation + currFloor);
            }
            else if(currFloor == 5)
            {
                Machray_Floor5 machrayFloor5Frag = new Machray_Floor5();

                replaceFragment(machrayFloor5Frag, currLocation + currFloor);
            }
        }
        else if(buildingName.equals(getResources().getString(R.string.allen)))
        {
            currLocation = buildingName;
            currFloor = floorNumber;

            if(currFloor == 1)
            {
                Allen_Floor1 allenFloor1Frag = new Allen_Floor1();

                replaceFragment(allenFloor1Frag, currLocation + currFloor);
            }
            else if(currFloor == 2)
            {
                Allen_Floor2 allenFloor2Frag = new Allen_Floor2();

                replaceFragment(allenFloor2Frag, currLocation + currFloor);
            }
            else if(currFloor == 3)
            {
                Allen_Floor3 allenFloor3Frag = new Allen_Floor3();

                replaceFragment(allenFloor3Frag, currLocation + currFloor);
            }
            else if(currFloor == 4)
            {
                Allen_Floor4 allenFloor4Frag = new Allen_Floor4();

                replaceFragment(allenFloor4Frag, currLocation + currFloor);
            }
            else if(currFloor == 5)
            {
                Allen_Floor5 allenFloor5Frag = new Allen_Floor5();

                replaceFragment(allenFloor5Frag, currLocation + currFloor);
            }
        }
    }
}
