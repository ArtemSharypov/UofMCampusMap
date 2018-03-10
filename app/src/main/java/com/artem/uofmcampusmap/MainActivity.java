package com.artem.uofmcampusmap;

import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.artem.uofmcampusmap.buildings.BuildingLayoutFragment;
import com.artem.uofmcampusmap.buildings.tunnels.Agri_AnimalSci_Tunnel;
import com.artem.uofmcampusmap.buildings.tunnels.Archi2_ExtEduc_Tunnel;
import com.artem.uofmcampusmap.buildings.tunnels.Dafoe_DuffRoblin_UniCollege_Tunnel;
import com.artem.uofmcampusmap.buildings.tunnels.Robson_UniCollege_Tunnel;
import com.artem.uofmcampusmap.buildings.tunnels.Russel_Archi2_Tunnel;
import com.artem.uofmcampusmap.buildings.tunnels.Tier_Artlab_Tunnel;
import com.artem.uofmcampusmap.buildings.tunnels.Wallace_Parker_Tunnel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PassRouteData, PassBuildingData, ChangeToolbarTitleInterface{
    private Toolbar toolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ExpandableListView mExpandableListView;
    private CustomExpandableListAdapter expandableListAdapter;
    private String startLocation;
    private String startRoom;
    private String destinationLocation;
    private String destinationRoom;
    private String currBuilding;
    private int currInstructionPos;
    private Route currRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startLocation = "";
        startRoom = "";
        destinationLocation = "";
        destinationRoom = "";

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        replaceToolbarTitle("");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mExpandableListView = findViewById(R.id.nav_drawer_expandable_lv);

        List<String> listDataHeaders = Arrays.asList(getResources().getStringArray(R.array.drawer_group_options));
        HashMap<String, List<String>> listDataChilds = new HashMap<>();

        List<String> buildingOptions =  Arrays.asList(getResources().getStringArray(R.array.drawer_building_options));
        List<String> tunnelOptions =  Arrays.asList(getResources().getStringArray(R.array.drawer_tunnel_options));

        listDataChilds.put(getResources().getString(R.string.building_layouts), buildingOptions);
        listDataChilds.put(getResources().getString(R.string.tunnel_layouts), tunnelOptions);

        expandableListAdapter = new CustomExpandableListAdapter(this, listDataHeaders, listDataChilds);
        mExpandableListView.setAdapter(expandableListAdapter);

        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                TextView clickedTV = view.findViewById(R.id.tv_group_title);
                String data = clickedTV.getText().toString();

                if(data.equals(getResources().getString(R.string.campus_map)))
                {
                    //Collapses all groups in case they weren't collapsed
                    for(int i = 0; i < expandableListAdapter.getGroupCount(); i++) {
                        mExpandableListView.collapseGroup(i);
                    }

                    switchToNavigation();
                    mDrawerLayout.closeDrawers();
                }
                else if(data.equals(getResources().getString(R.string.navigate)))
                {
                    //Collapses all groups in case they weren't collapsed
                    for(int i = 0; i < expandableListAdapter.getGroupCount(); i++) {
                        mExpandableListView.collapseGroup(i);
                    }

                    replaceFragment(new RoutePlannerFragment());
                    mDrawerLayout.closeDrawers();
                }

                return false;
            }
        });

        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                TextView clickedTV = view.findViewById(R.id.tv_item_title);
                String data = clickedTV.getText().toString();

                //Collapses all groups in case they weren't collapsed
                for(int i = 0; i < expandableListAdapter.getGroupCount(); i++) {
                    mExpandableListView.collapseGroup(i);
                }

                selectDrawerItem(data);

                return false;
            }
        });

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if(savedInstanceState == null)
        {
            NavigationFragment navigationFragment = new NavigationFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.main_frame_layout, navigationFragment);
            fragmentTransaction.commit();
        }
        else
        {
            currBuilding = savedInstanceState.getString("currBuilding");
            startLocation = savedInstanceState.getString("startLocation");
            startRoom = savedInstanceState.getString("startRoom");
            destinationLocation = savedInstanceState.getString("destinationLocation");
            destinationRoom = savedInstanceState.getString("destinationRoom");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("currBuilding", currBuilding);
        outState.putString("startLocation", startLocation);
        outState.putString("startRoom", startRoom);
        outState.putString("destinationLocation", destinationLocation);
        outState.putString("destinationRoom", destinationRoom);
    }

    //On click item for items in the nav drawer.
    private void selectDrawerItem(String childItemName) {
        Resources resources = getResources();

        String agriculture = resources.getString(R.string.agriculture);
        String agri_engineer = resources.getString(R.string.agr_engineer);
        String allen = resources.getString(R.string.allen);
        String animalSci = resources.getString(R.string.animal_sci);
        String archi2 = resources.getString(R.string.archi_2);
        String armes = resources.getString(R.string.armes);
        String artlab = resources.getString(R.string.artlab);

        String bioSci = resources.getString(R.string.bio_sci);
        String buller = resources.getString(R.string.buller);

        String dairySci = resources.getString(R.string.dairy_science);
        String drake = resources.getString(R.string.drake_centre);
        String duffRoblin = resources.getString(R.string.duff_roblin);

        String e1_eitc = resources.getString(R.string.eitc_e1);
        String e2_eitc = resources.getString(R.string.eitc_e2);
        String e3_eitc = resources.getString(R.string.eitc_e3);
        String education = resources.getString(R.string.education);
        String eliDafoe = resources.getString(R.string.elizabeth_dafoe);
        String extEduc = resources.getString(R.string.ext_education);

        String fletcher = resources.getString(R.string.fletcher);

        String helenGlass = resources.getString(R.string.helen_glass);
        String humanEco = resources.getString(R.string.human_ecology);

        String isbister = resources.getString(R.string.isbister);

        String machray = resources.getString(R.string.machray);

        String parker = resources.getString(R.string.parker);
        String plantScience = resources.getString(R.string.plant_sci);

        String robertSchultz = resources.getString(R.string.robert_schultz);
        String robson = resources.getString(R.string.robson);
        String russel = resources.getString(R.string.russel);

        String stJohns = resources.getString(R.string.st_johns);
        String stPauls = resources.getString(R.string.st_pauls);

        String tacheArts = resources.getString(R.string.tache_arts);
        String tier = resources.getString(R.string.tier);

        String uniCentre = resources.getString(R.string.uni_centre);
        String uniCollege = resources.getString(R.string.uni_college);

        String wallace = resources.getString(R.string.wallace);

        String agriToAnimalSci = resources.getString(R.string.agri_to_animal_sci);
        String archi2ToExtEduc = resources.getString(R.string.archi2_to_ext_educ);
        String dafoeDuffRoblinUniCollege = resources.getString(R.string.dafoe_duff_uni_college);
        String uniCollegeToRobson = resources.getString(R.string.uni_college_to_robson);
        String russelToArchi2 = resources.getString(R.string.russel_to_archi2);
        String tierToArtlab = resources.getString(R.string.tier_to_artlab);
        String wallaceToParker = resources.getString(R.string.wallace_to_parker);

        if(childItemName.equals(agriculture))
        {
            this.currBuilding = agriculture;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(agri_engineer))
        {
            this.currBuilding = agri_engineer;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(allen))
        {
            this.currBuilding = allen;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(animalSci))
        {
            this.currBuilding = animalSci;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(archi2))
        {
            this.currBuilding = archi2;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(armes))
        {
            this.currBuilding = armes;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(artlab))
        {
            this.currBuilding = artlab;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(bioSci))
        {
            this.currBuilding = bioSci;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(buller))
        {
            this.currBuilding = buller;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(dairySci))
        {
            this.currBuilding = dairySci;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(drake))
        {
            this.currBuilding = drake;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(duffRoblin))
        {
            this.currBuilding = duffRoblin;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(education))
        {
            this.currBuilding = education;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(e1_eitc))
        {
            this.currBuilding = e1_eitc;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(e2_eitc))
        {
            this.currBuilding = e2_eitc;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(e3_eitc))
        {
            this.currBuilding = e3_eitc;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(eliDafoe))
        {
            this.currBuilding = eliDafoe;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(extEduc))
        {
            this.currBuilding = extEduc;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(fletcher))
        {
            this.currBuilding = fletcher;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(helenGlass))
        {
            this.currBuilding = helenGlass;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(humanEco))
        {
            this.currBuilding = humanEco;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(isbister))
        {
            this.currBuilding = isbister;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(machray))
        {
            this.currBuilding = machray;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(parker))
        {
            this.currBuilding = parker;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(plantScience))
        {
            this.currBuilding = plantScience;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(robertSchultz))
        {
            this.currBuilding = robertSchultz;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(robson))
        {
            this.currBuilding = robson;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(russel))
        {
            this.currBuilding = russel;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(stJohns))
        {
            this.currBuilding = stJohns;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(stPauls))
        {
            this.currBuilding = stPauls;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(tacheArts))
        {
            this.currBuilding = tacheArts;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(tier))
        {
            this.currBuilding = tier;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(uniCentre))
        {
            this.currBuilding = uniCentre;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(uniCollege))
        {
            this.currBuilding = uniCollege;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(wallace))
        {
            this.currBuilding = wallace;
            replaceFragment(new BuildingLayoutFragment());
        }
        else if(childItemName.equals(agriToAnimalSci))
        {
            replaceFragment(new Agri_AnimalSci_Tunnel());
        }
        else if(childItemName.equals(archi2ToExtEduc))
        {
            replaceFragment(new Archi2_ExtEduc_Tunnel());
        }
        else if(childItemName.equals(dafoeDuffRoblinUniCollege))
        {
            replaceFragment(new Dafoe_DuffRoblin_UniCollege_Tunnel());
        }
        else if(childItemName.equals(uniCollegeToRobson))
        {
            replaceFragment(new Robson_UniCollege_Tunnel());
        }
        else if(childItemName.equals(russelToArchi2))
        {
            replaceFragment(new Russel_Archi2_Tunnel());
        }
        else if(childItemName.equals(tierToArtlab))
        {
            replaceFragment(new Tier_Artlab_Tunnel());
        }
        else if(childItemName.equals(wallaceToParker))
        {
            replaceFragment(new Wallace_Parker_Tunnel());
        }

        mDrawerLayout.closeDrawers();
    }

    //Used to clear any route information when switching to a building layout
    private void resetFields()
    {
        startRoom = "";
        startLocation = "";
        destinationLocation = "";
        destinationRoom = "";
        currRoute = null;
        currInstructionPos = 0;
    }

    //Resets any route information, and switches to the desired fragment
    //If the fragment wasn't on the stack already, it'll use the one that is passed
    private void replaceFragment(Fragment fragment)
    {
        resetFields();
        replaceToolbarTitle("");

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame_layout, fragment);
        fragmentTransaction.commit();
    }

    //Switches the current view to the NavigationFragment, if its not already visible
    public void switchToNavigation()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currVisibleFragment = fragmentManager.findFragmentById(R.id.main_frame_layout);

        //If the current visible fragment isn't a NavigationFragment, then switch to it
        if(!(currVisibleFragment instanceof NavigationFragment))
        {
            NavigationFragment navigationFragment = new NavigationFragment();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_frame_layout, navigationFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void passStartLocation(String source) {
        startLocation = source;
    }

    @Override
    public String getStartLocation() {
        return startLocation;
    }

    @Override
    public void passStartRoom(String room) {
        this.startRoom = room;
    }

    @Override
    public String getStartRoom() {
        return startRoom;
    }

    @Override
    public void passDestinationLocation(String destination) {
        destinationLocation = destination;
    }

    @Override
    public String getDestinationLocation() {
        return destinationLocation;
    }

    @Override
    public void passDestinationRoom(String room) {
        this.destinationRoom = room;
    }

    @Override
    public String getDestinationRoom() {
        return destinationRoom;
    }

    @Override
    public int getCurrInstructionPos() {
        return currInstructionPos;
    }

    @Override
    public void setCurrInstructionPos(int currInstructionPos) {
        this.currInstructionPos = currInstructionPos;
    }

    public Route getRoute() {
        return currRoute;
    }

    public void passRoute(Route currRoute) {
        this.currRoute = currRoute;
    }

    @Override
    public void setCurrBuilding(String currBuilding) {
        this.currBuilding = currBuilding;
    }

    @Override
    public String getCurrBuilding() {
        return currBuilding;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Handles any clicks for the navigation drawer
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        //Handles any onClicks for the toolbar
        switch(item.getItemId())
        {
            case R.id.navigate_button:
                replaceFragment(new RoutePlannerFragment());

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currVisibleFragment = fragmentManager.findFragmentById(R.id.main_frame_layout);

        //Closes the app when it is displaying the map.
        //If it isn't currently on the map screen, it will then display the map
        if(this.mDrawerLayout.isDrawerOpen(GravityCompat.START))
        {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else if(!(currVisibleFragment instanceof NavigationFragment))
        {
            NavigationFragment navigationFragment = new NavigationFragment();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_frame_layout, navigationFragment);
            fragmentTransaction.commit();
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void replaceToolbarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}
