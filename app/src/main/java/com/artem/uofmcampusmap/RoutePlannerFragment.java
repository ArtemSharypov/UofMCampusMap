package com.artem.uofmcampusmap;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RoutePlannerFragment extends Fragment {
    private Button cancelButton;
    private Button findRouteButton;
    private AutoCompleteTextView toLocationAutoComplete;
    private AutoCompleteTextView fromLocationAutoComplete;
    private String[] locations;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route_planner, container, false);

        ChangeToolbarTitleInterface toolbarActivity = (ChangeToolbarTitleInterface) getActivity();
        toolbarActivity.replaceToolbarTitle("");

        String[] buildings = getResources().getStringArray(R.array.building_options);
        String[] rooms = getResources().getStringArray(R.array.building_rooms);
        String[] parkingLotsAndBuses = getResources().getStringArray(R.array.lots_bus_stops);
        String[] tempLocations = new String[buildings.length + rooms.length];
        locations = new String[tempLocations.length + parkingLotsAndBuses.length];

        //Combine all of the buildings and rooms into a single array
        System.arraycopy(buildings, 0, tempLocations, 0, buildings.length);
        System.arraycopy(rooms, 0, tempLocations, buildings.length, rooms.length);

        //Combine buildings/rooms array with all bus stops and parking lots
        System.arraycopy(tempLocations, 0, locations, 0, tempLocations.length);
        System.arraycopy(parkingLotsAndBuses, 0, locations, tempLocations.length, parkingLotsAndBuses.length);

        cancelButton = (Button) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //returns to the map fragment / screen
                MainActivity mainActivity = (MainActivity) getActivity();

                if(mainActivity != null)
                {
                    mainActivity.switchToNavigation();
                }
            }
        });

        findRouteButton = (Button) view.findViewById(R.id.find_route_button);
        findRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findRoute();
            }
        });

        toLocationAutoComplete = (AutoCompleteTextView) view.findViewById(R.id.to_autocomplete);
        ArrayAdapter<String> toAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, locations);
        toLocationAutoComplete.setAdapter(toAdapter);

        fromLocationAutoComplete = (AutoCompleteTextView) view.findViewById(R.id.from_autocomplete);
        ArrayAdapter<String> fromAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, locations);
        fromLocationAutoComplete.setAdapter(fromAdapter);

        toLocationAutoComplete.setOnEditorActionListener(new AutoCompleteTextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    findRoute();

                    //Hides the keyboard
                    InputMethodManager inputMethodManager = (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

                    return true;
                }
                return false;
            }
        });

        setHasOptionsMenu(true);

        return view;
    }

    private void findRoute()
    {
        MainActivity activity = (MainActivity) getActivity();
        String toLocation = "";
        String toRoom = "";
        String toText = "";
        String[] splitToLocation;
        String fromLocation = "";
        String fromRoom = "";
        String fromText = "";
        String[] splitFromLocation;
        boolean fromLocationValid;
        boolean toLocationValid;

        //Parses the destination location into room / building, bus stop, or parking lot
        toText = toLocationAutoComplete.getText().toString();
        splitToLocation = toText.split(" ");
        toLocationValid = validLocation(toText);

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

        //Parses the destination location into room / building, bus stop, or parking lot
        fromText = fromLocationAutoComplete.getText().toString();
        splitFromLocation = fromText.split(" ");
        fromLocationValid = validLocation(fromText);

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

        //Check if both locations entered are considered to be valid
        if(fromLocationValid && toLocationValid)
        {
            activity.passStartLocation(fromLocation.trim());
            activity.passStartRoom(fromRoom.trim());

            activity.passDestinationLocation(toLocation.trim());
            activity.passDestinationRoom(toRoom.trim());

            activity.switchToNavigation();

        }
        else if(!fromLocationValid && !toLocationValid)
        {
            //Both of the locations is invalid
            Toast.makeText(getContext(),
                    "Couldn't find either of the two locations on campus", Toast.LENGTH_LONG)
                    .show();
        }
        else if(!toLocationValid)
        {
            //Only the destination is invalid
            Toast.makeText(getContext(),
                    "Couldn't find the destination within the campus", Toast.LENGTH_LONG)
                    .show();

        }
        else if(!fromLocationValid)
        {
            //Only the starting location is invalid
            Toast.makeText(getContext(),
                    "Couldn't find the starting location within the campus", Toast.LENGTH_LONG)
                    .show();
        }
    }

    //Checks if the passed String is a valid location on campus
    private boolean validLocation(String currLocation)
    {
        boolean valid = false;
        List<String> listOfLocations = new ArrayList<>(Arrays.asList(locations));

        for(String location : listOfLocations) {
            if(location.equalsIgnoreCase(currLocation.trim())){
                valid = true;
            }
        }

        return valid;
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
        MenuItem navigateOption = menu.findItem(R.id.navigate_button);

        if(navigateOption != null) {
            navigateOption.setVisible(false);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //check what button on the menu was clicked and deal with it

        return super.onOptionsItemSelected(item);
    }
}
