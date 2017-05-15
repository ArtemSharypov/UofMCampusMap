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

public class MainActivity extends AppCompatActivity implements PassRouteData{
    private Toolbar toolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private String startLocation;
    private String destinationLocation;

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
    }

    //On click item for items in the nav drawer.
    private void selectDrawerItem(MenuItem menuItem) {

        switch(menuItem.getItemId())
        {
            case R.id.campus_map:
                //Map fragment should be at the very bottom of the stack if there is one
                if(getFragmentManager().getBackStackEntryCount() > 0)
                {
                    getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                break;
            case R.id.navigate:
                switchToRoutePlanner();
                break;
            case R.id.armes:
                BuildingLayoutFragment buildingLayoutFragment = new BuildingLayoutFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame_layout, buildingLayoutFragment);
                fragmentTransaction.addToBackStack("MapFragment");
                fragmentTransaction.commit();

        }
        menuItem.setChecked(true);
        mDrawerLayout.closeDrawers();
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
        else if(getFragmentManager().getBackStackEntryCount() > 0)
        {
            getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
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
}
