package com.artem.uofmcampusmap.buildings;

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

import com.artem.uofmcampusmap.MainActivity;
import com.artem.uofmcampusmap.R;
import com.artem.uofmcampusmap.buildings.agri_engineer.*;
import com.artem.uofmcampusmap.buildings.agriculture.*;
import com.artem.uofmcampusmap.buildings.allen.*;
import com.artem.uofmcampusmap.buildings.animal_science.*;
import com.artem.uofmcampusmap.buildings.architecture_2.*;
import com.artem.uofmcampusmap.buildings.armes.*;
import com.artem.uofmcampusmap.buildings.artlab.*;
import com.artem.uofmcampusmap.buildings.bio_science.*;
import com.artem.uofmcampusmap.buildings.buller.*;
import com.artem.uofmcampusmap.buildings.dairy_science.*;
import com.artem.uofmcampusmap.buildings.drake.*;
import com.artem.uofmcampusmap.buildings.duff_roblin.*;
import com.artem.uofmcampusmap.buildings.education.*;
import com.artem.uofmcampusmap.buildings.eitc_e1.*;
import com.artem.uofmcampusmap.buildings.eitc_e2.*;
import com.artem.uofmcampusmap.buildings.eitc_e3.*;
import com.artem.uofmcampusmap.buildings.elizabeth_dafoe_lib.*;
import com.artem.uofmcampusmap.buildings.extended_education.Ext_Education_Floor1;
import com.artem.uofmcampusmap.buildings.fletcher.*;
import com.artem.uofmcampusmap.buildings.helen_glass.*;
import com.artem.uofmcampusmap.buildings.human_ecology.*;
import com.artem.uofmcampusmap.buildings.isbister.*;
import com.artem.uofmcampusmap.buildings.machray.*;
import com.artem.uofmcampusmap.buildings.parker.*;
import com.artem.uofmcampusmap.buildings.plant_science.*;
import com.artem.uofmcampusmap.buildings.robert_schultz.Robert_Schultz_Floor1;
import com.artem.uofmcampusmap.buildings.robson.*;
import com.artem.uofmcampusmap.buildings.russel.*;
import com.artem.uofmcampusmap.buildings.st_johns.*;
import com.artem.uofmcampusmap.buildings.st_pauls.*;
import com.artem.uofmcampusmap.buildings.tache_hall.*;
import com.artem.uofmcampusmap.buildings.tier.*;
import com.artem.uofmcampusmap.buildings.university_centre.*;
import com.artem.uofmcampusmap.buildings.university_college.*;
import com.artem.uofmcampusmap.buildings.wallace.*;

import java.util.ArrayList;

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

        //Creates/Returns a Fragment that is related to the building, and the position (floor) that is passed
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;

            if(buildingName.equals(getResources().getString(R.string.agr_engineer)))
            {
                if(position == 0)
                    fragment = new Agri_Engineer_Floor1();
                else if(position == 1)
                    fragment = new Agri_Engineer_Floor2();
            }
            else if(buildingName.equals(getResources().getString(R.string.agriculture)))
            {
                if(position == 0)
                    fragment = new Agriculture_Floor1();
                else if(position == 1)
                    fragment = new Agriculture_Floor2();
                else if(position == 2)
                    fragment = new Agriculture_Floor3();
            }
            else if(buildingName.equals(getResources().getString(R.string.allen)))
            {
                if(position == 0)
                    fragment = new Allen_Floor1();
                else if(position == 1)
                    fragment = new Allen_Floor2();
                else if(position == 2)
                    fragment = new Allen_Floor3();
                else if(position == 3)
                    fragment = new Allen_Floor4();
                else if(position == 4)
                    fragment = new Allen_Floor5();
            }
            else if(buildingName.equals(getResources().getString(R.string.animal_sci)))
            {
                if(position == 0)
                    fragment = new Animal_Sci_Floor0();
                else if(position == 1)
                    fragment = new Animal_Sci_Floor1();
                else if(position == 2)
                    fragment = new Animal_Sci_Floor2();
            }
            else if(buildingName.equals(getResources().getString(R.string.archi_2)))
            {
                if(position == 0)
                    fragment = new Architecture2_Floor1();
                else if(position == 1)
                    fragment = new Architecture2_Floor2();
                else if(position == 2)
                    fragment = new Architecture2_Floor3();
                else if(position == 3)
                    fragment = new Architecture2_Floor4();
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
                if(position == 0)
                    fragment = new Artlab_Floor1();
                else if(position == 1)
                    fragment = new Artlab_Floor2();
                else if(position == 2)
                    fragment = new Artlab_Floor3();
                else if(position == 3)
                    fragment = new Artlab_Floor4();
            }
            else if(buildingName.equals(getResources().getString(R.string.bio_sci)))
            {
                if(position == 0)
                    fragment = new Bio_Sci_Floor1();
                else if(position == 1)
                    fragment = new Bio_Sci_Floor2();
                else if(position == 2)
                    fragment = new Bio_Sci_Floor3();
                else if(position == 3)
                    fragment = new Bio_Sci_Floor4();
            }
            else if(buildingName.equals(getResources().getString(R.string.buller)))
            {
                if(position == 0)
                    fragment = new Buller_Floor1();
                else if(position == 1)
                    fragment = new Buller_Floor2();
                else if(position == 2)
                    fragment = new Buller_Floor3();
                else if(position == 3)
                    fragment = new Buller_Floor4();
                else if(position == 4)
                    fragment = new Buller_Floor5();
            }
            else if(buildingName.equals(getResources().getString(R.string.dairy_science)))
            {
                if(position == 0)
                    fragment = new Dairy_Sci_Floor0();
                else if(position == 1)
                    fragment = new Dairy_Sci_Floor1();
                else if(position == 2)
                    fragment = new Dairy_Sci_Floor2();
            }
            else if(buildingName.equals(getResources().getString(R.string.drake_centre)))
            {
                if(position == 0)
                    fragment = new Drake_Floor1();
                else if(position == 1)
                    fragment = new Drake_Floor2();
                else if(position == 2)
                    fragment = new Drake_Floor3();
                else if(position == 3)
                    fragment = new Drake_Floor4();
                else if(position == 4)
                    fragment = new Drake_Floor5();
                else if(position == 5)
                    fragment = new Drake_Floor6();
            }
            else if(buildingName.equals(getResources().getString(R.string.duff_roblin)))
            {
                if(position == 0)
                    fragment = new Duff_Roblin_Floor1();
                else if(position == 1)
                    fragment = new Duff_Roblin_Floor2();
                else if(position == 2)
                    fragment = new Duff_Roblin_Floor3();
                else if(position == 3)
                    fragment = new Duff_Roblin_Floor4();
            }
            else if(buildingName.equals(getResources().getString(R.string.education)))
            {
                if(position == 0)
                    fragment = new Education_Floor1();
                else if(position == 1)
                    fragment = new Education_Floor2();
                else if(position == 2)
                    fragment = new Education_Floor3();
                else if(position == 3)
                    fragment = new Education_Floor4();
            }
            else if(buildingName.equals(getResources().getString(R.string.eitc_e1)))
            {
                if(position == 0)
                    fragment = new EITC_E1_Floor2();
                else if(position == 1)
                    fragment = new EITC_E1_Floor3();
                else if(position == 2)
                    fragment = new EITC_E1_Floor4();
                else if(position == 3)
                    fragment = new EITC_E1_Floor5();
            }
            else if(buildingName.equals(getResources().getString(R.string.eitc_e2)))
            {
                if(position == 0)
                    fragment = new EITC_E2_Floor1();
                else if(position == 1)
                    fragment = new EITC_E2_Floor2();
                else if(position == 2)
                    fragment = new EITC_E2_Floor3();
                else if(position == 3)
                    fragment = new EITC_E2_Floor4();
                else if(position == 4)
                    fragment = new EITC_E2_Floor5();
                else if(position == 5)
                    fragment = new EITC_E2_Floor6();
            }
            else if(buildingName.equals(getResources().getString(R.string.eitc_e3)))
            {
                if(position == 0)
                    fragment = new EITC_E3_Floor1();
                else if(position == 1)
                    fragment = new EITC_E3_Floor2();
                else if(position == 2)
                    fragment = new EITC_E3_Floor3();
                else if(position == 3)
                    fragment = new EITC_E3_Floor4();
                else if(position == 4)
                    fragment = new EITC_E3_Floor5();
                else if(position == 5)
                    fragment = new EITC_E3_Floor6();
            }
            else if(buildingName.equals(getResources().getString(R.string.elizabeth_dafoe)))
            {
                if(position == 0)
                    fragment = new Eli_Dafoe_Floor0();
                else if(position == 1)
                    fragment = new Eli_Dafoe_Floor1();
                else if(position == 2)
                    fragment = new Eli_Dafoe_Floor2();
                else if(position == 3)
                    fragment = new Eli_Dafoe_Floor3();
            }
            else if(buildingName.equals(getResources().getString(R.string.ext_education)))
            {
                if(position == 0)
                    fragment = new Ext_Education_Floor1();
            }
            else if(buildingName.equals(getResources().getString(R.string.fletcher)))
            {
                if(position == 0)
                    fragment = new Fletcher_Floor1();
                else if(position == 1)
                    fragment = new Fletcher_Floor2();
                else if(position == 2)
                    fragment = new Fletcher_Floor3();
                else if(position == 3)
                    fragment = new Fletcher_Floor4();
                else if(position == 4)
                    fragment = new Fletcher_Floor5();
                else if(position == 5)
                    fragment = new Fletcher_Floor6();
            }
            else if(buildingName.equals(getResources().getString(R.string.helen_glass)))
            {
                if(position == 0)
                    fragment = new Helen_Glass_Floor1();
                else if(position == 1)
                    fragment = new Helen_Glass_Floor2();
                else if(position == 2)
                    fragment = new Helen_Glass_Floor3();
                else if(position == 3)
                    fragment = new Helen_Glass_Floor4();
            }
            else if(buildingName.equals(getResources().getString(R.string.human_ecology)))
            {
                if(position == 0)
                    fragment = new Human_Ecology_Floor1();
                else if(position == 1)
                    fragment = new Human_Ecology_Floor2();
                else if(position == 2)
                    fragment = new Human_Ecology_Floor3();
                else if(position == 3)
                    fragment = new Human_Ecology_Floor4();
            }
            else if(buildingName.equals(getResources().getString(R.string.isbister)))
            {
                if(position == 0)
                    fragment = new Isbister_Floor1();
                else if(position == 1)
                    fragment = new Isbister_Floor2();
                else if(position == 2)
                    fragment = new Isbister_Floor3();
                else if(position == 3)
                    fragment = new Isbister_Floor4();
            }
            else if(buildingName.equals(getResources().getString(R.string.machray)))
            {
                if(position == 0)
                    fragment = new Machray_Floor1();
                else if(position == 1)
                    fragment = new Machray_Floor2();
                else if(position == 2)
                    fragment = new Machray_Floor3();
                else if(position == 3)
                    fragment = new Machray_Floor4();
                else if(position == 4)
                    fragment = new Machray_Floor5();
            }
            else if(buildingName.equals(getResources().getString(R.string.parker)))
            {
                if(position == 0)
                    fragment = new Parker_Floor1();
                else if(position == 1)
                    fragment = new Parker_Floor2();
                else if(position == 2)
                    fragment = new Parker_Floor3();
                else if(position == 3)
                    fragment = new Parker_Floor4();
                else if(position == 4)
                    fragment = new Parker_Floor5();
            }
            else if(buildingName.equals(getResources().getString(R.string.plant_sci)))
            {
                if(position == 0)
                    fragment = new Plant_Sci_Floor0();
                else if(position == 1)
                    fragment = new Plant_Sci_Floor1();
                else if(position == 2)
                    fragment = new Plant_Sci_Floor2();
                else if(position == 3)
                    fragment = new Plant_Sci_Floor3();
            }
            else if(buildingName.equals(getResources().getString(R.string.robert_schultz)))
            {
                if(position == 0)
                    fragment = new Robert_Schultz_Floor1();
            }
            else if(buildingName.equals(getResources().getString(R.string.robson)))
            {
                if(position == 0)
                    fragment = new Robson_Floor1();
                else if(position == 1)
                    fragment = new Robson_Floor2();
                else if(position == 2)
                    fragment = new Robson_Floor3();
                else if(position == 3)
                    fragment = new Robson_Floor4();
            }
            else if(buildingName.equals(getResources().getString(R.string.russel)))
            {
                if(position == 0)
                    fragment = new Russel_Floor1();
                else if(position == 1)
                    fragment = new Russel_Floor2();
                else if(position == 2)
                    fragment = new Russel_Floor3();
            }
            else if(buildingName.equals(getResources().getString(R.string.st_johns)))
            {
                if(position == 0)
                    fragment = new St_Johns_Floor1();
                else if(position == 1)
                    fragment = new St_Johns_Floor2();
                else if(position == 2)
                    fragment = new St_Johns_Floor3();
            }
            else if(buildingName.equals(getResources().getString(R.string.st_pauls)))
            {
                if(position == 0)
                    fragment = new St_Pauls_Floor1();
                else if(position == 1)
                    fragment = new St_Pauls_Floor2();
                else if(position == 2)
                    fragment = new St_Pauls_Floor3();
            }
            else if(buildingName.equals(getResources().getString(R.string.tache_arts)))
            {
                if(position == 0)
                    fragment = new Tache_Hall_Floor1();
                else if(position == 1)
                    fragment = new Tache_Hall_Floor2();
                else if(position == 2)
                    fragment = new Tache_Hall_Floor3();
                else if(position == 3)
                    fragment = new Tache_Hall_Floor4();
                else if(position == 4)
                    fragment = new Tache_Hall_Floor5();
            }
            else if(buildingName.equals(getResources().getString(R.string.tier)))
            {
                if(position == 0)
                    fragment = new Tier_Floor1();
                else if(position == 1)
                    fragment = new Tier_Floor2();
                else if(position == 2)
                    fragment = new Tier_Floor3();
                else if(position == 3)
                    fragment = new Tier_Floor4();
                else if(position == 4)
                    fragment = new Tier_Floor5();
                else if(position == 5)
                    fragment = new Tier_Floor6();
            }
            else if(buildingName.equals(getResources().getString(R.string.uni_centre)))
            {
                if(position == 0)
                    fragment = new Uni_Centre_Floor1();
                else if(position == 1)
                    fragment = new Uni_Centre_Floor2();
                else if(position == 2)
                    fragment = new Uni_Centre_Floor3();
                else if(position == 3)
                    fragment = new Uni_Centre_Floor4();
                else if(position == 4)
                    fragment = new Uni_Centre_Floor5();
            }
            else if(buildingName.equals(getResources().getString(R.string.uni_college)))
            {
                if(position == 0)
                    fragment = new Uni_College_Floor1();
                else if(position == 1)
                    fragment = new Uni_College_Floor2();
                else if(position == 2)
                    fragment = new Uni_College_Floor3();
                else if(position == 3)
                    fragment = new Uni_College_Floor4();
            }
            else if(buildingName.equals(getResources().getString(R.string.wallace)))
            {
                if(position == 0)
                    fragment = new Wallace_Floor1();
                else if(position == 1)
                    fragment = new Wallace_Floor2();
                else if(position == 2)
                    fragment = new Wallace_Floor3();
                else if(position == 3)
                    fragment = new Wallace_Floor4();
                else if(position == 4)
                    fragment = new Wallace_Floor5();
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
        MainActivity activity = (MainActivity) getActivity();
        buildingName = activity.getCurrBuilding();
        ArrayList<String> buildingFloors = populateFloorNames();

        activity.replaceToolbarTitle(buildingName);

        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new BuildingFragmentPagerAdapter(getChildFragmentManager(), buildingFloors));

        tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    //Creates a List of floor names that are used as the titles of each tab
    private ArrayList<String> populateFloorNames()
    {
        ArrayList<String> floors = null;

        if(buildingName.equals(getResources().getString(R.string.agr_engineer)))
        {
            floors = createFloorNamesFrom(1, 2);
        }
        else if(buildingName.equals(getResources().getString(R.string.agriculture)))
        {
            floors = createFloorNamesFrom(1, 3);
        }
        else if(buildingName.equals(getResources().getString(R.string.allen)))
        {
            floors = createFloorNamesFrom(1, 5);
        }
        else if(buildingName.equals(getResources().getString(R.string.animal_sci)))
        {
            floors = createFloorNamesFrom(0, 2);
        }
        else if(buildingName.equals(getResources().getString(R.string.archi_2)))
        {
            floors = createFloorNamesFrom(1, 4);
        }
        else if(buildingName.equals(getResources().getString(R.string.armes)))
        {
            floors = createFloorNamesFrom(1, 2);
        }
        else if(buildingName.equals(getResources().getString(R.string.artlab)))
        {
            floors = createFloorNamesFrom(1, 4);
        }
        else if(buildingName.equals(getResources().getString(R.string.bio_sci)))
        {
            floors = createFloorNamesFrom(1, 4);
        }
        else if(buildingName.equals(getResources().getString(R.string.buller)))
        {
            floors = createFloorNamesFrom(1, 5);
        }
        else if(buildingName.equals(getResources().getString(R.string.dairy_science)))
        {
            floors = createFloorNamesFrom(0, 2);
        }
        else if(buildingName.equals(getResources().getString(R.string.drake_centre)))
        {
            floors = createFloorNamesFrom(1, 6);
        }
        else if(buildingName.equals(getResources().getString(R.string.duff_roblin)))
        {
            floors = createFloorNamesFrom(1, 4);
        }
        else if(buildingName.equals(getResources().getString(R.string.education)))
        {
            floors = createFloorNamesFrom(1, 4);
        }
        else if(buildingName.equals(getResources().getString(R.string.eitc_e1)))
        {
            floors = createFloorNamesFrom(2, 5);
        }
        else if(buildingName.equals(getResources().getString(R.string.eitc_e2)))
        {
            floors = createFloorNamesFrom(1, 6);
        }
        else if(buildingName.equals(getResources().getString(R.string.eitc_e3)))
        {
            floors = createFloorNamesFrom(1, 6);
        }
        else if(buildingName.equals(getResources().getString(R.string.elizabeth_dafoe)))
        {
            floors = createFloorNamesFrom(0, 3);
        }
        else if(buildingName.equals(getResources().getString(R.string.ext_education)))
        {
            floors = createFloorNamesFrom(1, 1);
        }
        else if(buildingName.equals(getResources().getString(R.string.fletcher)))
        {
            floors = createFloorNamesFrom(1, 6);
        }
        else if(buildingName.equals(getResources().getString(R.string.helen_glass)))
        {
            floors = createFloorNamesFrom(1, 4);
        }
        else if(buildingName.equals(getResources().getString(R.string.human_ecology)))
        {
            floors = createFloorNamesFrom(1, 4);
        }
        else if(buildingName.equals(getResources().getString(R.string.isbister)))
        {
            floors = createFloorNamesFrom(1, 4);
        }
        else if(buildingName.equals(getResources().getString(R.string.machray)))
        {
            floors = createFloorNamesFrom(1, 5);
        }
        else if(buildingName.equals(getResources().getString(R.string.parker)))
        {
            floors = createFloorNamesFrom(1, 5);
        }
        else if(buildingName.equals(getResources().getString(R.string.plant_sci)))
        {
            floors = createFloorNamesFrom(0, 3);
        }
        else if(buildingName.equals(getResources().getString(R.string.robert_schultz)))
        {
            floors = createFloorNamesFrom(1, 1);
        }
        else if(buildingName.equals(getResources().getString(R.string.robson)))
        {
            floors = createFloorNamesFrom(1, 4);
        }
        else if(buildingName.equals(getResources().getString(R.string.russel)))
        {
            floors = createFloorNamesFrom(1, 3);
        }
        else if(buildingName.equals(getResources().getString(R.string.st_johns)))
        {
            floors = createFloorNamesFrom(1, 3);
        }else if(buildingName.equals(getResources().getString(R.string.st_pauls)))
        {
            floors = createFloorNamesFrom(1, 3);
        }
        else if(buildingName.equals(getResources().getString(R.string.tache_arts)))
        {
            floors = createFloorNamesFrom(1, 5);
        }
        else if(buildingName.equals(getResources().getString(R.string.tier)))
        {
            floors = createFloorNamesFrom(1, 6);
        }
        else if(buildingName.equals(getResources().getString(R.string.uni_centre)))
        {
            floors = createFloorNamesFrom(1, 5);
        }
        else if(buildingName.equals(getResources().getString(R.string.uni_college)))
        {
            floors = createFloorNamesFrom(1, 4);
        }
        else if(buildingName.equals(getResources().getString(R.string.wallace)))
        {
            floors = createFloorNamesFrom(1, 5);
        }

        return floors;
    }

    private ArrayList<String> createFloorNamesFrom(int start, int end)
    {
        ArrayList<String> floors = new ArrayList<>();

        for(int i = start; i <= end; i++)
        {
            floors.add("Floor " + i);
        }

        return floors;
    }
}
