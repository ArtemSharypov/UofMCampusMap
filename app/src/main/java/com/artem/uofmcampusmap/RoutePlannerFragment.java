package com.artem.uofmcampusmap;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

/**
 * Created by Artem on 2017-04-22.
 */

public class RoutePlannerFragment extends Fragment {

    private Spinner fromBuilding;
    private Spinner toBuilding;
    private CheckBox preferTunnelsBox;
    private Button cancelButton;
    private Button findRouteButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route_planner, container, false);

        ArrayAdapter<CharSequence> fromBuildingAdapter =
                ArrayAdapter.createFromResource(this.getActivity(), R.array.building_options, android.R.layout.simple_spinner_item);
        fromBuildingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fromBuilding = (Spinner) view.findViewById(R.id.from_building);
        fromBuilding.setAdapter(fromBuildingAdapter);

        final ArrayAdapter<CharSequence> toBuildingAdapter =
                ArrayAdapter.createFromResource(this.getActivity(), R.array.building_options, android.R.layout.simple_spinner_item);
        toBuildingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        toBuilding = (Spinner) view.findViewById(R.id.to_building);
        toBuilding.setAdapter(toBuildingAdapter);

        preferTunnelsBox = (CheckBox) view.findViewById(R.id.prefer_tunnels_check);

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
                activity.passDestinationLocation(toBuilding.getSelectedItem().toString());

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
