package com.artem.uofmcampusmap.building_layouts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.artem.uofmcampusmap.R;
import com.artem.uofmcampusmap.building_layouts.armes.ArmesFloor1Fragment;
import com.artem.uofmcampusmap.building_layouts.armes.ArmesFloor2Fragment;

import java.util.ArrayList;

/**
 * Created by Artem on 2017-05-15.
 */

public class BuildingLayoutFragment extends Fragment{

    class BuildingFragmentPagerAdapter extends FragmentPagerAdapter
    {
        private ArrayList<String> tabTitles;
        private int numPages;

        public BuildingFragmentPagerAdapter(FragmentManager fm, ArrayList<String> titles)
        {
            super(fm);
            this.tabTitles = titles;
            this.numPages = tabTitles.size();
        }

        @Override
        public int getCount() {
            return numPages;
        }

        @Override
        public Fragment getItem(int position) {
            //so here is where each fragment has the selection that depend on the level
            //todo add fragments that can be changed depending on the floor selected
            //this would be the call to the building files depending on what is clicked?
            //would need to remember the building that is selected for it to work
            //ie if(building.equals(SomeBuilding) {SomeBuilding.getFloor(position);}

            if(buildingName.equals("armes"))
            {
                if(position == 0)
                   return new ArmesFloor1Fragment();
                else if(position == 1)
                    return new ArmesFloor2Fragment();
            }

            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles.get(position);
        }
    }

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private String buildingName;

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_building_layout, container, false);

        ArrayList<String> buildingFloors = new ArrayList<>(); //todo make this have the proper floors (could just have this added in adapter already)
        buildingFloors.add("Floor 1");
        buildingFloors.add("Floor 2");

        buildingName = "armes";

        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new BuildingFragmentPagerAdapter(getChildFragmentManager(), buildingFloors));

        tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
}
