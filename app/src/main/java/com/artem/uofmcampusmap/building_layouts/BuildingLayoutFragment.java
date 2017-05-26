package com.artem.uofmcampusmap.building_layouts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.artem.uofmcampusmap.MainActivity;
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
        private int numFloors;

        public BuildingFragmentPagerAdapter(FragmentManager fm, ArrayList<String> titles)
        {
            super(fm);
            this.tabTitles = titles;
            this.numFloors = tabTitles.size();
        }

        @Override
        public int getCount() {
            return numFloors;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;

            if(buildingName.equals(getResources().getString(R.string.agriculture)))
            {

            }
            else if(buildingName.equals(getResources().getString(R.string.agr_engineer)))
            {

            }
            else if(buildingName.equals(getResources().getString(R.string.allen)))
            {

            }
            else if(buildingName.equals(getResources().getString(R.string.animal_sci)))
            {

            }
            else if(buildingName.equals(getResources().getString(R.string.archi_2)))
            {

            }
            else if(buildingName.equals(getResources().getString(R.string.armes)))
            {
                if(position == 0)
                    fragment = new ArmesFloor1Fragment();
                else if(position == 1)
                    fragment = new ArmesFloor2Fragment();
            }
            else if(buildingName.equals(getResources().getString(R.string.artlab)))
            {

            }
            else if(buildingName.equals(getResources().getString(R.string.bio_sci)))
            {

            }
            else if(buildingName.equals(getResources().getString(R.string.buller)))
            {

            }
            else if(buildingName.equals(getResources().getString(R.string.dairy_science)))
            {

            }else if(buildingName.equals(getResources().getString(R.string.drake_centre)))
            {

            }
            else if(buildingName.equals(getResources().getString(R.string.duff_roblin)))
            {

            }
            else if(buildingName.equals(getResources().getString(R.string.education)))
            {

            }
            else if(buildingName.equals(getResources().getString(R.string.eitc_e1)))
            {

            }
            else if(buildingName.equals(getResources().getString(R.string.eitc_e2)))
            {

            }
            else if(buildingName.equals(getResources().getString(R.string.eitc_e3)))
            {

            }
            else if(buildingName.equals(getResources().getString(R.string.elizabeth_dafoe)))
            {

            }
            else if(buildingName.equals(getResources().getString(R.string.ext_education)))
            {

            }
            else if(buildingName.equals(getResources().getString(R.string.fac_music)))
            {

            }
            else if(buildingName.equals(getResources().getString(R.string.fac_music)))
            {

            }
            else if(buildingName.equals(getResources().getString(R.string.fletcher)))
            {

            }else if(buildingName.equals(getResources().getString(R.string.helen_glass)))
            {

            }else if(buildingName.equals(getResources().getString(R.string.human_ecology)))
            {

            }
            else if(buildingName.equals(getResources().getString(R.string.isbister)))
            {

            }
            else if(buildingName.equals(getResources().getString(R.string.machray)))
            {

            }
            else if(buildingName.equals(getResources().getString(R.string.music_annex)))
            {

            }
            else if(buildingName.equals(getResources().getString(R.string.parker)))
            {

            }
            else if(buildingName.equals(getResources().getString(R.string.plant_sci)))
            {

            }
            else if(buildingName.equals(getResources().getString(R.string.robert_schultz)))
            {

            }
            else if(buildingName.equals(getResources().getString(R.string.robson)))
            {

            }
            else if(buildingName.equals(getResources().getString(R.string.russel)))
            {

            }
            else if(buildingName.equals(getResources().getString(R.string.st_johns)))
            {

            }else if(buildingName.equals(getResources().getString(R.string.st_pauls)))
            {

            }
            else if(buildingName.equals(getResources().getString(R.string.tier)))
            {

            }
            else if(buildingName.equals(getResources().getString(R.string.uni_centre)))
            {

            }
            else if(buildingName.equals(getResources().getString(R.string.uni_college)))
            {

            }
            else if(buildingName.equals(getResources().getString(R.string.wallace)))
            {

            }

            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles.get(position);
        }
    }

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private String buildingName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_building_layout, container, false);

        ArrayList<String> buildingFloors = populateFloorNames();

        MainActivity activity = (MainActivity) getActivity();
        buildingName = activity.getCurrBuilding();

        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new BuildingFragmentPagerAdapter(getChildFragmentManager(), buildingFloors));

        tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    private ArrayList<String> populateFloorNames()
    {
        ArrayList<String> floors = null;

        if(buildingName.equals(getResources().getString(R.string.agriculture)))
        {
            floors = createFloorNames(1, 2);
        }
        else if(buildingName.equals(getResources().getString(R.string.agr_engineer)))
        {
            floors = createFloorNames(1, 3);
        }
        else if(buildingName.equals(getResources().getString(R.string.allen)))
        {
            floors = createFloorNames(1, 5);
        }
        else if(buildingName.equals(getResources().getString(R.string.animal_sci)))
        {
            floors = createFloorNames(0, 2);
        }
        else if(buildingName.equals(getResources().getString(R.string.archi_2)))
        {
            floors = createFloorNames(1, 4);
        }
        else if(buildingName.equals(getResources().getString(R.string.armes)))
        {
            floors = createFloorNames(1, 2);
        }
        else if(buildingName.equals(getResources().getString(R.string.artlab)))
        {
            floors = createFloorNames(1, 4);
        }
        else if(buildingName.equals(getResources().getString(R.string.bio_sci)))
        {
            floors = createFloorNames(1, 4);
        }
        else if(buildingName.equals(getResources().getString(R.string.buller)))
        {
            floors = createFloorNames(1, 5);
        }
        else if(buildingName.equals(getResources().getString(R.string.dairy_science)))
        {
            floors = createFloorNames(0, 2);
        }
        else if(buildingName.equals(getResources().getString(R.string.drake_centre)))
        {
            floors = createFloorNames(1, 6);
        }
        else if(buildingName.equals(getResources().getString(R.string.duff_roblin)))
        {
            floors = createFloorNames(1, 4);
        }
        else if(buildingName.equals(getResources().getString(R.string.education)))
        {
            floors = createFloorNames(1, 4);
        }
        else if(buildingName.equals(getResources().getString(R.string.eitc_e1)))
        {
            floors = createFloorNames(2, 5);
        }
        else if(buildingName.equals(getResources().getString(R.string.eitc_e2)))
        {
            floors = createFloorNames(1, 6);
        }
        else if(buildingName.equals(getResources().getString(R.string.eitc_e3)))
        {
            floors = createFloorNames(1, 6);
        }
        else if(buildingName.equals(getResources().getString(R.string.elizabeth_dafoe)))
        {
            floors = createFloorNames(0, 3);
        }
        else if(buildingName.equals(getResources().getString(R.string.ext_education)))
        {
            floors = createFloorNames(1, 1);
        }
        else if(buildingName.equals(getResources().getString(R.string.fac_music)))
        {
            floors = createFloorNames(1, 3);
        }
        else if(buildingName.equals(getResources().getString(R.string.fletcher)))
        {
            floors = createFloorNames(1, 6);
        }
        else if(buildingName.equals(getResources().getString(R.string.helen_glass)))
        {
            floors = createFloorNames(1, 4);
        }
        else if(buildingName.equals(getResources().getString(R.string.human_ecology)))
        {
            //todo add in human ecology
        }
        else if(buildingName.equals(getResources().getString(R.string.isbister)))
        {
            floors = createFloorNames(1, 4);
        }
        else if(buildingName.equals(getResources().getString(R.string.machray)))
        {
            floors = createFloorNames(1, 5);
        }
        else if(buildingName.equals(getResources().getString(R.string.music_annex)))
        {
            floors = createFloorNames(1, 1);
        }
        else if(buildingName.equals(getResources().getString(R.string.parker)))
        {
            floors = createFloorNames(1, 5);
        }
        else if(buildingName.equals(getResources().getString(R.string.plant_sci)))
        {
            floors = createFloorNames(0, 3);
        }
        else if(buildingName.equals(getResources().getString(R.string.robert_schultz)))
        {
            floors = createFloorNames(1, 1);
        }
        else if(buildingName.equals(getResources().getString(R.string.robson)))
        {
            floors = createFloorNames(1, 4);
        }
        else if(buildingName.equals(getResources().getString(R.string.russel)))
        {
            floors = createFloorNames(1, 3);
        }
        else if(buildingName.equals(getResources().getString(R.string.st_johns)))
        {
            floors = createFloorNames(1, 3);
        }else if(buildingName.equals(getResources().getString(R.string.st_pauls)))
        {
            floors = createFloorNames(1, 3);
        }
        else if(buildingName.equals(getResources().getString(R.string.tier)))
        {
            floors = createFloorNames(1, 6);
        }
        else if(buildingName.equals(getResources().getString(R.string.uni_centre)))
        {
            floors = createFloorNames(1, 5);
        }
        else if(buildingName.equals(getResources().getString(R.string.uni_college)))
        {
            floors = createFloorNames(1, 4);
        }
        else if(buildingName.equals(getResources().getString(R.string.wallace)))
        {
            floors = createFloorNames(1, 5);
        }

        return floors;
    }

    private ArrayList<String> createFloorNames(int start, int end)
    {
        ArrayList<String> floors = new ArrayList<>();

        for(int i = start; i <= end; i++)
        {
            floors.add("Floor " + i);
        }

        return floors;
    }
}
