package com.artem.uofmcampusmap;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Artem on 2017-04-22.
 */

public class RoutePlannerFragment extends Fragment {
    private Button cancelButton;
    private Button findRouteButton;
    private RadioGroup toRadioGroup;
    private RadioGroup fromRadioGroup;
    private AutoCompleteTextView toLocationAutoComplete;
    private AutoCompleteTextView fromLocationAutoComplete;
    private boolean toLocationIsGps;
    private boolean fromLocationIsGps;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route_planner, container, false);

        cancelButton = (Button) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //returns to the map fragment / screen
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        findRouteButton = (Button) view.findViewById(R.id.find_route_button);
        findRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();
                String toLocation = "";
                String toRoom = "";
                String toText = "";
                String[] splitToLocation;
                String fromLocation = "";
                String fromRoom = "";
                String fromText = "";
                String[] splitFromLocation;

                if(toLocationIsGps)
                {
                    toLocation = getResources().getString(R.string.curr_location);
                }
                else
                {
                    //check for building, bus stop, parking lot
                    toText = toLocationAutoComplete.getText().toString();
                    splitToLocation = toText.split(" ");

                    //Length 1 means more than likely it is a building
                    if(splitToLocation.length == 1)
                    {
                        toLocation = splitToLocation[0];
                    }
                    else if(splitToLocation.length > 1)
                    {
                        //Essentially check if the the first entry is a room, if not then handle the other cases
                        try
                        {
                            Integer.parseInt(splitToLocation[0]);

                            //If it succeeds in casting the String to a number, then it HAS to be a room
                            toRoom = splitToLocation[0];
                            toLocation = combineSplitWords(1, splitToLocation);

                        }
                        catch(NumberFormatException e)
                        {
                            //If it failed to make the String into a number, it has to be a parking lot, bus stop, or building
                            //Therefore just use the entire entered String from before
                            toLocation = toText;
                        }
                    }
                }

                if(fromLocationIsGps)
                {
                    fromLocation = getResources().getString(R.string.curr_location);
                }
                else
                {
                    //check for building, bus stop, parking lot
                    fromText = fromLocationAutoComplete.getText().toString();
                    splitFromLocation = fromText.split(" ");

                    //Length 1 means more than likely it is a building
                    if(splitFromLocation.length == 1)
                    {
                        fromLocation = splitFromLocation[0];
                    }
                    else if(splitFromLocation.length > 1)
                    {
                        //Essentially check if the the first entry is a room, if not then handle the other cases
                        try
                        {
                            Integer.parseInt(splitFromLocation[0]);

                            //If it succeeds in casting the String to a number, then it HAS to be a room
                            fromRoom = splitFromLocation[0];
                            fromLocation = combineSplitWords(1, splitFromLocation);

                        }
                        catch(NumberFormatException e)
                        {
                            //If it failed to make the String into a number, it has to be a parking lot, bus stop, or building
                            //Therefore just use the entire entered String from before
                            fromLocation = fromText;
                        }
                    }
                }

                //todo implement a check if there is such a room for the building locations entered, if not make a toast
                activity.passStartLocation(fromLocation.trim());
                activity.passStartRoom(fromRoom.trim());

                activity.passDestinationLocation(toLocation.trim());
                activity.passDestinationRoom(toRoom.trim());


                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        String[] buildings = getResources().getStringArray(R.array.building_options);
        String[] rooms = getResources().getStringArray(R.array.building_rooms);
        String[] parkingLotsAndBuses = getResources().getStringArray(R.array.lots_bus_stops);
        String[] tempLocations = new String[buildings.length + rooms.length];
        String[] locations = new String[tempLocations.length + parkingLotsAndBuses.length];

        //Combine all of the buildings and rooms into a single array
        System.arraycopy(buildings, 0, tempLocations, 0, buildings.length);
        System.arraycopy(rooms, 0, tempLocations, buildings.length, rooms.length);

        //Combine buildings/rooms array with all bus stops and parking lots
        System.arraycopy(tempLocations, 0, locations, 0, tempLocations.length);
        System.arraycopy(parkingLotsAndBuses, 0, locations, tempLocations.length, parkingLotsAndBuses.length);

        toLocationAutoComplete = (AutoCompleteTextView) view.findViewById(R.id.to_autocomplete);
        ArrayAdapter<String> toAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, locations);
        toLocationAutoComplete.setAdapter(toAdapter);

        fromLocationAutoComplete = (AutoCompleteTextView) view.findViewById(R.id.from_autocomplete);
        ArrayAdapter<String> fromAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, locations);
        fromLocationAutoComplete.setAdapter(fromAdapter);

        toRadioGroup = (RadioGroup) view.findViewById(R.id.to_radio_group);
        toRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId)
            {
                switch(checkedId)
                {
                    case R.id.to_curr_location:

                        if(toLocationAutoComplete.getVisibility() == View.VISIBLE)
                        {
                            toLocationAutoComplete.setVisibility(View.INVISIBLE);
                        }

                        toLocationIsGps = true;
                        break;

                    case R.id.to_location:

                        if(toLocationAutoComplete.getVisibility() == View.INVISIBLE)
                        {
                            toLocationAutoComplete.setText("");
                            toLocationAutoComplete.setVisibility(View.VISIBLE);
                        }

                        toLocationIsGps = false;
                        break;
                }
            }
        });

        fromRadioGroup = (RadioGroup) view.findViewById(R.id.from_radio_group);
        fromRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {

                switch(checkedId)
                {
                    case R.id.from_curr_location:

                        if(fromLocationAutoComplete.getVisibility() == View.VISIBLE)
                        {
                            fromLocationAutoComplete.setVisibility(View.INVISIBLE);
                        }

                        fromLocationIsGps = true;
                        break;

                    case R.id.from_location:

                        if(fromLocationAutoComplete.getVisibility() == View.INVISIBLE)
                        {
                            fromLocationAutoComplete.setText("");
                            fromLocationAutoComplete.setVisibility(View.VISIBLE);
                        }

                        fromLocationIsGps = false;
                        break;
                }
            }
        });

        setHasOptionsMenu(true);

        return view;
    }

    //Combines the words in an array starting at a position passed
    private String combineSplitWords(int startPos, String[] wordsToCombine)
    {
        String combined = "";

        for(int i = startPos; i < wordsToCombine.length; i++)
        {
            combined += wordsToCombine[startPos] + " ";
        }

        return combined;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_route_planner, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //check what button on the menu was clicked and deal with it

        return super.onOptionsItemSelected(item);
    }
}
