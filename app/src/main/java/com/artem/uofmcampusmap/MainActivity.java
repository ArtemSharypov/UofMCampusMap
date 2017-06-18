package com.artem.uofmcampusmap;

import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
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

import com.artem.uofmcampusmap.buildings.BuildingLayoutFragment;
import com.artem.uofmcampusmap.buildings.tunnels.Agri_AnimalSci_Tunnel;
import com.artem.uofmcampusmap.buildings.tunnels.Archi2_ExtEduc_Tunnel;
import com.artem.uofmcampusmap.buildings.tunnels.Dafoe_DuffRoblin_UniCollege_Tunnel;
import com.artem.uofmcampusmap.buildings.tunnels.Robson_UniCollege_Tunnel;
import com.artem.uofmcampusmap.buildings.tunnels.Russel_Archi2_Tunnel;
import com.artem.uofmcampusmap.buildings.tunnels.Tier_Artlab_Tunnel;
import com.artem.uofmcampusmap.buildings.tunnels.Wallace_Parker_Tunnel;

public class MainActivity extends AppCompatActivity implements PassRouteData, PassBuildingData{
    private Toolbar toolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private String startLocation;
    private String destinationLocation;
    private String currBuilding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startLocation = "";
        destinationLocation = "";

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                selectDrawerItem(menuItem);
                return true;
            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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
            MapFragment mapFragment = new MapFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.main_frame_layout, mapFragment);
            fragmentTransaction.commit();
        }
        else
        {
            currBuilding = savedInstanceState.getString("currBuilding");
            startLocation = savedInstanceState.getString("startLocation");
            destinationLocation = savedInstanceState.getString("destinationLocation");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("currBuilding", currBuilding);
        outState.putString("startLocation", startLocation);
        outState.putString("destinationLocation", destinationLocation);
    }



    //On click item for items in the nav drawer.
    private void selectDrawerItem(MenuItem menuItem) {
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;

        switch(menuItem.getItemId())
        {
            case R.id.campus_map:
                //Map fragment should be at the very bottom of the stack if there is one
                if(getSupportFragmentManager().getBackStackEntryCount() > 0)
                {
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }

                break;
            case R.id.navigate:
                switchToRoutePlanner();
                break;
            case R.id.agriculture:
                this.currBuilding = getResources().getString(R.string.agriculture);
                switchToBuildingLayouts();
                break;
            case R.id.agri_engineer:
                this.currBuilding = getResources().getString(R.string.agr_engineer);
                switchToBuildingLayouts();
                break;
            case R.id.allen:
                this.currBuilding = getResources().getString(R.string.allen);
                switchToBuildingLayouts();
                break;
            case R.id.animal_sci:
                this.currBuilding = getResources().getString(R.string.animal_sci);
                switchToBuildingLayouts();
                break;
            case R.id.architecture:
                this.currBuilding = getResources().getString(R.string.archi_2);
                switchToBuildingLayouts();
                break;
            case R.id.armes:
                this.currBuilding = getResources().getString(R.string.armes);
                switchToBuildingLayouts();
                break;
            case R.id.artlab:
                this.currBuilding = getResources().getString(R.string.artlab);
                switchToBuildingLayouts();
                break;
            case R.id.bio_sci:
                this.currBuilding = getResources().getString(R.string.bio_sci);
                switchToBuildingLayouts();
                break;
            case R.id.buller:
                this.currBuilding = getResources().getString(R.string.buller);
                switchToBuildingLayouts();
                break;
            case R.id.dairy_sci:
                this.currBuilding = getResources().getString(R.string.dairy_science);
                switchToBuildingLayouts();
                break;
            case R.id.drake_centre:
                this.currBuilding = getResources().getString(R.string.drake_centre);
                switchToBuildingLayouts();
                break;
            case R.id.duff_roblin:
                this.currBuilding = getResources().getString(R.string.duff_roblin);
                switchToBuildingLayouts();
                break;
            case R.id.education:
                this.currBuilding = getResources().getString(R.string.education);
                switchToBuildingLayouts();
                break;
            case R.id.eitc_e1:
                this.currBuilding = getResources().getString(R.string.eitc_e1);
                switchToBuildingLayouts();
                break;
            case R.id.eitc_e2:
                this.currBuilding = getResources().getString(R.string.eitc_e2);
                switchToBuildingLayouts();
                break;
            case R.id.eitc_e3:
                this.currBuilding = getResources().getString(R.string.eitc_e3);
                switchToBuildingLayouts();
                break;
            case R.id.elizabeth_dafoe:
                this.currBuilding = getResources().getString(R.string.elizabeth_dafoe);
                switchToBuildingLayouts();
                break;
            case R.id.ext_education:
                this.currBuilding = getResources().getString(R.string.ext_education);
                switchToBuildingLayouts();
                break;
            case R.id.fac_music:
                this.currBuilding = getResources().getString(R.string.fac_music);
                switchToBuildingLayouts();
                break;
            case R.id.fletcher:
                this.currBuilding = getResources().getString(R.string.fletcher);
                switchToBuildingLayouts();
                break;
            case R.id.helen_glass:
                this.currBuilding = getResources().getString(R.string.helen_glass);
                switchToBuildingLayouts();
                break;
            case R.id.human_eco:
                this.currBuilding = getResources().getString(R.string.human_ecology);
                switchToBuildingLayouts();
                break;
            case R.id.isbister:
                this.currBuilding = getResources().getString(R.string.isbister);
                switchToBuildingLayouts();
                break;
            case R.id.machray:
                this.currBuilding = getResources().getString(R.string.machray);
                switchToBuildingLayouts();
                break;
            case R.id.music_annex:
                this.currBuilding = getResources().getString(R.string.music_annex);
                switchToBuildingLayouts();
                break;
            case R.id.parker:
                this.currBuilding = getResources().getString(R.string.parker);
                switchToBuildingLayouts();
                break;
            case R.id.plant_sci:
                this.currBuilding = getResources().getString(R.string.plant_sci);
                switchToBuildingLayouts();
                break;
            case R.id.robert_schultz:
                this.currBuilding = getResources().getString(R.string.robert_schultz);
                switchToBuildingLayouts();
                break;
            case R.id.robson:
                this.currBuilding = getResources().getString(R.string.robson);
                switchToBuildingLayouts();
                break;
            case R.id.russel:
                this.currBuilding = getResources().getString(R.string.russel);
                switchToBuildingLayouts();
                break;
            case R.id.st_johns:
                this.currBuilding = getResources().getString(R.string.st_johns);
                switchToBuildingLayouts();
                break;
            case R.id.st_pauls:
                this.currBuilding = getResources().getString(R.string.st_pauls);
                switchToBuildingLayouts();
                break;
            case R.id.tier:
                this.currBuilding = getResources().getString(R.string.tier);
                switchToBuildingLayouts();
                break;
            case R.id.uni_centre:
                this.currBuilding = getResources().getString(R.string.uni_centre);
                switchToBuildingLayouts();
                break;
            case R.id.uni_college:
                this.currBuilding = getResources().getString(R.string.uni_college);
                switchToBuildingLayouts();
                break;
            case R.id.wallace:
                this.currBuilding = getResources().getString(R.string.wallace);
                switchToBuildingLayouts();
                break;
            case R.id.agri_animal_sci:
                Agri_AnimalSci_Tunnel agri_animalSci_tunnel = new Agri_AnimalSci_Tunnel();

                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame_layout, agri_animalSci_tunnel);
                fragmentTransaction.addToBackStack("MapFragment");
                fragmentTransaction.commit();

                break;
            case R.id.archi2_ext_educ:
                Archi2_ExtEduc_Tunnel archi2_extEduc_tunnel = new Archi2_ExtEduc_Tunnel();

                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame_layout, archi2_extEduc_tunnel);
                fragmentTransaction.addToBackStack("MapFragment");
                fragmentTransaction.commit();

                break;
            case R.id.dafoe_duff_roblin_uni_college:
                Dafoe_DuffRoblin_UniCollege_Tunnel dafoe_duffRoblin_uniCollege_tunnel = new Dafoe_DuffRoblin_UniCollege_Tunnel();

                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame_layout, dafoe_duffRoblin_uniCollege_tunnel);
                fragmentTransaction.addToBackStack("MapFragment");
                fragmentTransaction.commit();

                break;
            case R.id.robson_uni_college:
                Robson_UniCollege_Tunnel robson_uniCollege_tunnel = new Robson_UniCollege_Tunnel();

                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame_layout, robson_uniCollege_tunnel);
                fragmentTransaction.addToBackStack("MapFragment");
                fragmentTransaction.commit();

                break;
            case R.id.russel_archi2:
                Russel_Archi2_Tunnel russel_archi2_tunnel = new Russel_Archi2_Tunnel();

                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame_layout, russel_archi2_tunnel);
                fragmentTransaction.addToBackStack("MapFragment");
                fragmentTransaction.commit();

                break;
            case R.id.tier_artlab:
                Tier_Artlab_Tunnel tier_artlab_tunnel = new Tier_Artlab_Tunnel();

                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame_layout, tier_artlab_tunnel);
                fragmentTransaction.addToBackStack("MapFragment");
                fragmentTransaction.commit();

                break;
            case R.id.wallace_parker:
                Wallace_Parker_Tunnel wallace_parker_tunnel = new Wallace_Parker_Tunnel();

                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame_layout, wallace_parker_tunnel);
                fragmentTransaction.addToBackStack("MapFragment");
                fragmentTransaction.commit();

                break;
        }
        menuItem.setChecked(true);
        mDrawerLayout.closeDrawers();
    }

    private void switchToBuildingLayouts()
    {
        BuildingLayoutFragment buildingLayoutFragment = new BuildingLayoutFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame_layout, buildingLayoutFragment);
        fragmentTransaction.addToBackStack("MapFragment");
        fragmentTransaction.commit();
    }

    //Switch current fragment to the RoutePlannerFragment
    private void switchToRoutePlanner()
    {
        RoutePlannerFragment routePlannerFragment = new RoutePlannerFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame_layout, routePlannerFragment);
        fragmentTransaction.addToBackStack("MapFragment");
        fragmentTransaction.commit();
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
    public void passDestinationLocation(String destination) {
        destinationLocation = destination;
    }

    @Override
    public String getDestinationLocation() {
        return destinationLocation;
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
                switchToRoutePlanner();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if(this.mDrawerLayout.isDrawerOpen(GravityCompat.START))
        {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else if(getSupportFragmentManager().getBackStackEntryCount() > 0)
        {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
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
    public void setCurrBuilding(String currBuilding) {
        this.currBuilding = currBuilding;
    }

    @Override
    public String getCurrBuilding() {
        return currBuilding;
    }
}
