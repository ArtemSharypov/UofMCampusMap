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
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by Artem on 2017-04-22.
 */

public class RoutePlannerFragment extends Fragment {

    private Spinner fromBuilding;
    private Spinner fromRoom;
    private TextView fromRoomTV;
    private Spinner toBuilding;
    private Spinner toRoom;
    private TextView toRoomTV;
    private Button cancelButton;
    private Button findRouteButton;
    private ArrayAdapter<CharSequence> fromRoomAdapter;
    private ArrayAdapter<CharSequence> toRoomAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route_planner, container, false);

        final ArrayAdapter<CharSequence> fromBuildingAdapter =
                ArrayAdapter.createFromResource(this.getActivity(), R.array.building_options, android.R.layout.simple_spinner_item);
        fromBuildingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fromBuilding = (Spinner) view.findViewById(R.id.from_building);
        fromBuilding.setAdapter(fromBuildingAdapter);
        fromBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                //todo probably switch to resources
                if(selectedItem.equals(R.string.armes))
                {
                    fromRoomTV.setVisibility(View.VISIBLE);
                    fromRoom.setVisibility(View.VISIBLE);
                    fromRoomAdapter.clear();
                    fromRoomAdapter.addAll(getResources().getStringArray(R.array.armes_rooms));
                    fromRoomAdapter.notifyDataSetChanged();
                }
                else
                {
                    fromRoomTV.setVisibility(View.GONE);
                    fromRoom.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //todo change the default array
        fromRoomAdapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.armes_rooms, android.R.layout.simple_spinner_item);
        fromRoomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fromRoom = (Spinner) view.findViewById(R.id.from_room);
        fromRoom.setAdapter(fromRoomAdapter);

        fromRoomTV = (TextView) view.findViewById(R.id.from_room_tv);

        final ArrayAdapter<CharSequence> toBuildingAdapter =
                ArrayAdapter.createFromResource(this.getActivity(), R.array.building_options, android.R.layout.simple_spinner_item);
        toBuildingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        toBuilding = (Spinner) view.findViewById(R.id.to_building);
        toBuilding.setAdapter(toBuildingAdapter);
        toBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                if(selectedItem.equals(R.string.armes))
                {
                    toRoomTV.setVisibility(View.VISIBLE);
                    toRoom.setVisibility(View.VISIBLE);
                    toRoomAdapter.clear();
                    toRoomAdapter.addAll(getResources().getStringArray(R.array.armes_rooms));
                    toRoomAdapter.notifyDataSetChanged();
                }
                else
                {
                    toRoomTV.setVisibility(View.GONE);
                    toRoom.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //todo change the default array
        toRoomAdapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.armes_rooms, android.R.layout.simple_spinner_item);
        toRoomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        toRoom = (Spinner) view.findViewById(R.id.to_room);
        toRoom.setAdapter(toRoomAdapter);

        toRoomTV = (TextView) view.findViewById(R.id.to_room_tv);

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

                activity.passStartLocation(fromBuilding.getSelectedItem().toString());

                if(fromRoom.getVisibility() == View.VISIBLE)
                {
                    activity.passStartRoom(fromRoom.getSelectedItem().toString());

                }

                activity.passDestinationLocation(toBuilding.getSelectedItem().toString());

                if(toRoom.getVisibility() == View.VISIBLE)
                {
                    activity.passDestinationRoom(toRoom.getSelectedItem().toString());
                }

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
