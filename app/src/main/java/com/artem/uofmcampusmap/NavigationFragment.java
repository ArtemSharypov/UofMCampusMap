package com.artem.uofmcampusmap;

import android.location.Location;
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

import com.artem.uofmcampusmap.buildings.armes.ArmesFloor1Fragment;
import com.artem.uofmcampusmap.buildings.armes.ArmesFloor2Fragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * Created by Artem on 2017-04-21.
 */

public class NavigationFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private FrameLayout fragHolder;
    private ImageView prevInstruction;
    private ImageView nextInstruction;
    private TextView instructionsTextView;
    private TextView distanceRemainingTV;
    private TextView estTimeRemainingTV;
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
    private final String OUTSIDE_ID = "Outside";
    private double remainingDistance;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private GoogleApiClient googleApiClient;
    private Location lastLocation = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);

        campusMap = new MapNavigationMesh(getResources());
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
                    instructionsTextView.setText(route.getDirectionsAt(currInstructionPos));

                    remainingDistance += currInstruction.getDistanceInMetres();

                    //todo make this a method to make it cleaner?
                    distanceRemainingTV.setText(getResources().getString(R.string.distance_remaining) + remainingDistance + " m");
                    estTimeRemainingTV.setText(getResources().getString(R.string.est_time) + amountOfTime(remainingDistance) + " minutes");

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
                    remainingDistance -= route.getInstructionAt(currInstructionPos).getDistanceInMetres();

                    currInstructionPos++;

                    Instruction currInstruction = route.getInstructionAt(currInstructionPos);
                    instructionsTextView.setText(route.getDirectionsAt(currInstructionPos));

                    //todo make this a method to make it cleaner?
                    distanceRemainingTV.setText(getResources().getString(R.string.distance_remaining) + remainingDistance + " m");
                    estTimeRemainingTV.setText(getResources().getString(R.string.est_time) + amountOfTime(remainingDistance) + " minutes");

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

        if(checkPlayServices())
        {
            buildGoogleApiClient();
        }

        switchToMapFrag();
        currLocation = OUTSIDE_ID;

        if(!startLocation.equals("") && !destinationLocation.equals(""))
        {
            if(startLocation.equals(getResources().getString(R.string.curr_location)) ||
                    destinationLocation.equals(getResources().getString(R.string.curr_location)))
            {
                findLocation();
            }
            else
            {
                route = campusMap.findRoute(startLocation, startRoom, destinationLocation, destinationRoom);
            }

            if(route != null)
            {
                remainingDistance = route.getRouteLength();

                //todo make this a method to make it cleaner?
                distanceRemainingTV.setText(getResources().getString(R.string.distance_remaining) + remainingDistance + " m");
                estTimeRemainingTV.setText(getResources().getString(R.string.est_time) + amountOfTime(remainingDistance) + " minutes");

                Instruction firstInstruction = route.getInstructionAt(0);

                instructionsTextView.setText(route.getDirectionsAt(0));
                instructionsLinLayout.setVisibility(View.VISIBLE);

                activity.passRoute(route);
                activity.setCurrInstructionPos(currInstructionPos);

                if(firstInstruction.getSource() instanceof IndoorVertex)
                {
                    handleIndoorSource(firstInstruction);
                }
            }
        }

        return view;
    }

    private boolean checkPlayServices()
    {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());
        boolean works = true;

        //Checks that it worked
        if(resultCode != ConnectionResult.SUCCESS)
        {
            if(GoogleApiAvailability.getInstance().isUserResolvableError(resultCode))
            {
                GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), resultCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else
            {
                getActivity().finish();
            }

            works = false;
        }

        return works;
    }

    private synchronized void buildGoogleApiClient()
    {
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        googleApiClient.connect();
    }

    private void findLocation()
    {
        try
        {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }
        catch(SecurityException error)
        {
            Toast.makeText(getContext(),
                "Couldn't find the location, make sure GPS is enabled", Toast.LENGTH_LONG)
                .show();
        }

        //Checks if there is a location that can be queried
        if(lastLocation != null)
        {
            LatLng gpsLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

            if(startLocation.equals(getResources().getString(R.string.curr_location)))
            {
                route = campusMap.findRoute(gpsLocation, destinationLocation, destinationRoom);
            }
            else
            {
                route = campusMap.findRoute(startLocation, startRoom, gpsLocation);
            }

            if(route != null)
            {
                remainingDistance = route.getRouteLength();

                //todo make this a method to make it cleaner?
                distanceRemainingTV.setText(getResources().getString(R.string.distance_remaining) + remainingDistance + " m");
                estTimeRemainingTV.setText(getResources().getString(R.string.est_time) + amountOfTime(remainingDistance) + " minutes");

                Instruction firstInstruction = route.getInstructionAt(0);

                instructionsTextView.setText(route.getDirectionsAt(0));
                instructionsLinLayout.setVisibility(View.VISIBLE);

                PassRouteData activity = (PassRouteData) getActivity();
                activity.passRoute(route);
                activity.setCurrInstructionPos(currInstructionPos);

                if(firstInstruction.getSource() instanceof IndoorVertex)
                {
                    handleIndoorSource(firstInstruction);
                }
                else if(!currLocation.equals(OUTSIDE_ID))
                {
                    switchToMapFrag();
                    currLocation = OUTSIDE_ID;
                }
            }
        }
        else
        {
            Toast.makeText(getContext(),
                    "Couldn't find the location, make sure GPS is enabled", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private int amountOfTime(double distance)
    {
        final double WALK_SPEED_METERS_PER_MIN = 66.67;

        return (int) (distance / WALK_SPEED_METERS_PER_MIN) + 1;
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try
        {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }
        catch(SecurityException error)
        {
            Toast.makeText(getContext(),
                    "Couldn't find the location, make sure GPS is enabled", Toast.LENGTH_LONG)
                    .show();
        }

        findLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getContext(),
                "Failed to connect", Toast.LENGTH_LONG)
                .show();
    }
}
