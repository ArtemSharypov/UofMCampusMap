package com.artem.uofmcampusmap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

public class MapFragment extends Fragment implements DisplayRoute {

    private MapView mMapView;
    private ArrayList<Polyline> routeLines;
    private ArrayList<Marker> mapMarkers;
    private int lastPosInRoute; //Last position that was used to update the route
    private int currPosInLines;
    private Marker currLocation;
    private boolean currLocationVisible;
    private Route route;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        routeLines = new ArrayList<>();
        mapMarkers = new ArrayList<>();
        currLocationVisible = false;

        Context context = getActivity().getApplicationContext();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));

        mMapView = view.findViewById(R.id.mapView);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.setMultiTouchControls(true);
        mMapView.setBuiltInZoomControls(false);
        mMapView.setMaxZoomLevel(22.0);

        RotationGestureOverlay rotationGestureOverlay = new RotationGestureOverlay(context, mMapView);
        rotationGestureOverlay.setEnabled(true);
        mMapView.getOverlays().add(rotationGestureOverlay);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            addLocationOverlay();
        }

        centerMap();
        displayRoute();

        return view;
    }

    private void addLocationOverlay() {
        MyLocationNewOverlay locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getContext()), mMapView);
        locationOverlay.enableMyLocation();
//        locationOverlay.enableFollowLocation(); //todo maybe use this?
        mMapView.getOverlays().add(locationOverlay);
    }

    //Updates the PolyLines displayed dependent on what happened to the position in the route since last time the lines were drawn
    //If there is a positive change, that means the current path needs to be hidden
    //If there is a negative change, that means the previous path needs to be displayed
    @Override
    public void updateDisplayedRoute()
    {
        PassRouteData activity = (PassRouteData) getActivity();
        int currRoutePos = activity.getCurrInstructionPos();

        if(currRoutePos >= 0)
        {
            int linePos = currRoutePos - lastPosInRoute;
            lastPosInRoute = currRoutePos;

            if(linePos > 0)
            {
                //means that next was pressed, hide the current path/line
                if(currPosInLines < routeLines.size())
                {
                    routeLines.get(currPosInLines).setVisible(false);
                    mMapView.invalidate();

                    currPosInLines++;

                    Instruction currInstruc = route.getInstructionAt(currRoutePos);

                    //check where to move the marker
                    if(currInstruc != null)
                    {
                        OutdoorVertex source = (OutdoorVertex) currInstruc.getSource();

                        if(source != null)
                        {
                            if(currLocation == null)
                            {
                                currLocationVisible = true;
                                currLocation = createCurrLocationMarker(source.getPosition());
                                mMapView.getOverlays().add(currLocation);
                            }
                            else
                            {
                                /* todo: If it doesn't update position, 2 options:
                                    1) remove the point
                                    2) update the position then call invalidate on the map view
                                 */
                                currLocation.setPosition(source.getPosition());

                                //Re-display the marker on the map
                                if(!currLocationVisible)
                                {
                                    mMapView.getOverlays().add(currLocation);
                                    currLocationVisible = true;
                                }
                            }
                        }
                    }

                    if(currPosInLines >= routeLines.size())
                    {
                        mMapView.getOverlays().remove(currLocation);
                        currLocationVisible = false;
                    }
                }
            }
            else
            {
                //means that previous was pressed, show the previous path/line
                if(currPosInLines > 0)
                {
                    currPosInLines--;

                    routeLines.get(currPosInLines).setVisible(true);
                    mMapView.invalidate();

                    Instruction currInstruc = route.getInstructionAt(currRoutePos);

                    //check where to move the marker
                    if(currInstruc != null)
                    {
                        OutdoorVertex source = (OutdoorVertex) currInstruc.getSource();

                        if(currLocation == null)
                        {
                            currLocationVisible = true;
                            currLocation = createCurrLocationMarker(source.getPosition());
                            mMapView.getOverlays().add(currLocation);
                        }
                        else
                        {
                              /* todo: If it doesn't update position, 2 options:
                                    1) remove the point
                                    2) update the position then call invalidate on the map view
                                 */
                            currLocation.setPosition(source.getPosition());

                            //Re-display the marker on the map
                            if(!currLocationVisible)
                            {
                                mMapView.getOverlays().add(currLocation);
                                currLocationVisible = true;
                            }
                        }
                    }
                }
            }
        }
    }

    //Creates a list of Polylines that relate to the route, and the position it is in within the route
    //Any lines before the current position are not displayed, any after however are
    @Override
    public void displayRoute()
    {
        GeoPoint startPoint;
        GeoPoint endPoint;
        Instruction currInstruction;
        Polyline currPolyline;
        Vertex source;
        Vertex destination;
        PassRouteData activity = (PassRouteData) getActivity();
        int currRoutePos = activity.getCurrInstructionPos();
        route = activity.getRoute();

        if (route != null && route.getRouteLength() > 0)
        {
            lastPosInRoute = currRoutePos;
            currPosInLines = 0;

            currRoutePos--;

            //Path/Lines that are before the current position
            while (currRoutePos >= 0)
            {
                currInstruction = route.getInstructionAt(currRoutePos);
                source = currInstruction.getSource();
                destination = currInstruction.getDestination();

                //Create a path/line only if the instruction is an outdoors one
                if (currInstruction.isOutdoorInstruction())
                {
                    startPoint = ((OutdoorVertex) source).getPosition();
                    endPoint = ((OutdoorVertex) destination).getPosition();
                    currPolyline = createRedPolyline(startPoint, endPoint);

                    //Add the line to the start, since its to the LEFT of the current position (as in not shown)
                    routeLines.add(0, currPolyline);
                    routeLines.get(0).setVisible(false);

                    mMapView.getOverlayManager().add(currPolyline);

                    currPosInLines++; //Move up one position every time a non-visible polyline is added
                }
                else if (currInstruction.isIndoorInstruction())
                {
                    break;
                }

                currRoutePos--;
            }

            currRoutePos = lastPosInRoute;

            while (currRoutePos < route.getNumInstructions()) {
                currInstruction = route.getInstructionAt(currRoutePos);
                source = currInstruction.getSource();
                destination = currInstruction.getDestination();

                //Create a path/line only if the instruction is an outdoors one
                if (currInstruction.isOutdoorInstruction()) {
                    startPoint = ((OutdoorVertex) source).getPosition();
                    endPoint = ((OutdoorVertex) destination).getPosition();
                    currPolyline = createRedPolyline(startPoint, endPoint);

                    routeLines.add(currPolyline);
                    mMapView.getOverlayManager().add(currPolyline);

                    if (currLocation == null)
                    {
                        currLocationVisible = true;
                        currLocation = createCurrLocationMarker(startPoint);
                        mMapView.getOverlays().add(currLocation);
                    }
                }
                else if (currInstruction.isIndoorInstruction())
                {
                    break;
                }

                currRoutePos++;
            }
        }
    }

    // Creates a single Polyline from the 2 given positions, with a Red color and returns it
    private Polyline createRedPolyline(GeoPoint startPos, GeoPoint endPos)
    {
        Polyline polyline = null;
        ArrayList<GeoPoint> points = new ArrayList<>();

        if(startPos != null && endPos != null)
        {
            points.add(startPos);
            points.add(endPos);

            polyline = new Polyline();
            polyline.setPoints(points);
            polyline.setColor(Color.RED);
        }

        return polyline;
    }

    //Creates a Green Marker at the position specified and returned it, with a current location title
    private Marker createCurrLocationMarker(GeoPoint position)
    {
        Marker marker = null;

        if(position != null)
        {
            marker = new Marker(mMapView);
            marker.setPosition(position);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setTitle(getResources().getString(R.string.curr_location));
            marker.setTextLabelBackgroundColor(Color.GREEN);
        }

        return marker;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mMapView != null) {
            //remove all markers/lines to prevent a memory leak
            mapMarkers.clear();
            routeLines.clear();
        }
    }

    //Centers the camera around approx center of the campus
    private void centerMap()
    {
        GeoPoint centerOfCampus = new GeoPoint(49.809496, -97.133810);

        int zoomAmount = 19;
        MapController mapController = (MapController) mMapView.getController();
        mapController.setZoom(zoomAmount);
        mapController.setCenter(centerOfCampus);
    }
}
