package com.artem.uofmcampusmap;

import android.os.Bundle;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Artem on 2017-04-22.
 */

public class RoutePlannerFragment extends Fragment {

    private Spinner fromBuilding;
    private EditText fromRoom;
    private Spinner toBuilding;
    private EditText toRoom;
    private Button cancelButton;
    private Button findRouteButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route_planner, container, false);

        final ArrayAdapter<CharSequence> fromBuildingAdapter =
                ArrayAdapter.createFromResource(this.getActivity(), R.array.building_options, android.R.layout.simple_spinner_item);
        fromBuildingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fromBuilding = (Spinner) view.findViewById(R.id.from_building);
        fromBuilding.setAdapter(fromBuildingAdapter);

        fromRoom = (EditText) view.findViewById(R.id.from_room);

        final ArrayAdapter<CharSequence> toBuildingAdapter =
                ArrayAdapter.createFromResource(this.getActivity(), R.array.building_options, android.R.layout.simple_spinner_item);
        toBuildingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        toBuilding = (Spinner) view.findViewById(R.id.to_building);
        toBuilding.setAdapter(toBuildingAdapter);

        toRoom = (EditText) view.findViewById(R.id.to_room);

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

                //todo implement a check if there is such a room for the building locations entered
                activity.passStartLocation(fromBuilding.getSelectedItem().toString());
                activity.passStartRoom(fromRoom.getText().toString().trim());

                activity.passDestinationLocation(toBuilding.getSelectedItem().toString());
                activity.passDestinationRoom(toRoom.getText().toString().trim());


                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        setHasOptionsMenu(true);

        return view;
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
