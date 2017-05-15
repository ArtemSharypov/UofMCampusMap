package com.artem.uofmcampusmap;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
                return new ArmesFloor1Fragment();
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

        ArrayList<String> buildingFloors = new ArrayList<>(); //todo make this have the proper floors
        buildingFloors.add("ayo 1");
        buildingFloors.add("ayo 2");
        buildingFloors.add("THE MADMAN 3");

        buildingName = "armes";

        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new BuildingFragmentPagerAdapter(getChildFragmentManager(), buildingFloors));

        tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        
        return view;
    }
}
