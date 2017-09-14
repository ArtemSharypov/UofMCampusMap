package com.artem.uofmcampusmap;

import android.content.res.Resources;

import com.artem.uofmcampusmap.buildings.allen.AllenIndoorConnections;
import com.artem.uofmcampusmap.buildings.armes.ArmesIndoorConnections;
import com.artem.uofmcampusmap.buildings.machray.MachrayIndoorConnections;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Artem on 2017-05-30.
 */

public class MapNavigationMesh
{
    private ArrayList<WalkableZone> walkableZones;
    private HashMap<String, ArrayList<Vertex>> startEndLocations;
    private ArmesIndoorConnections armesIndoorConnections;
    private MachrayIndoorConnections machrayIndoorConnections;
    private AllenIndoorConnections allenIndoorConnections;
    private String[] tunnelConnectedBuildings;
    private Resources resources;
    private RouteFinder routeFinder;

    //Used for passing routes around from threads, normally is for each part ie going from a buildings room to an exit within it
    private Route firstRoutePart;
    private Route secondRoutePart;
    private Route lastRoutePart;

    public MapNavigationMesh(Resources resources)
    {
        walkableZones = new ArrayList<>();
        startEndLocations = new HashMap<>();
        routeFinder = new RouteFinder();
        this.resources = resources;
        tunnelConnectedBuildings = resources.getStringArray(R.array.tunnel_connected_buildings);
        populateMesh();
    }

    //Adds another entrance to a given building
    private void addEntrance(String keyName, Vertex entranceVertex)
    {
        ArrayList<Vertex> entrancesList;

        if(startEndLocations.containsKey(keyName))
        {
            entrancesList = startEndLocations.get(keyName);
        }
        else
        {
            entrancesList = new ArrayList<>();
        }

        entrancesList.add(entranceVertex);
        startEndLocations.put(keyName, entrancesList);
    }

    private boolean buildingsConnectViaTunnels(String building1, String building2)
    {
        List connectedBuildings = Arrays.asList(tunnelConnectedBuildings);

        return connectedBuildings.contains(building1) && connectedBuildings.contains(building2);
    }

    //Handles all of the cases of creating a Route between the start and end location.
    //uses RouteFinder to create any Routes between the locations as needed
    public Route getRoute(String startLocation, String startRoom, String endLocation, String endRoom)
    {
        Route route = null;
        Route indoorRoute = null;
        Route outdoorRoute = null;

        if(startEndLocations.containsKey(startLocation) && startEndLocations.containsKey(endLocation))
        {
            //Same buildings don't need a route, unless they're in different rooms
            if(startLocation.equals(endLocation))
            {
                if(!startRoom.equals(endRoom))
                {
                    route = routeSameBuildingRooms(startLocation, startRoom, endRoom);
                }
            }
            else
            {
                if(buildingsConnectViaTunnels(startLocation, endLocation) && !startRoom.equals("") && !endRoom.equals(""))
                {
                    indoorRoute = routeViaTunnels(startLocation, startRoom, endLocation, endRoom);
                }

                outdoorRoute = routeViaInToOut(startLocation, startRoom, endLocation, endRoom);

                //Check if its quicker to go through a tunnel / connection or by going outside and around
                if(indoorRoute != null)
                {
                    if(indoorRoute.getRouteLength() < outdoorRoute.getRouteLength())
                    {
                        route = indoorRoute;
                    }
                    else
                    {
                        route = outdoorRoute;
                    }
                }
                else
                {
                    route = outdoorRoute;
                }
            }
        }

        return route;
    }

    //Used for creation of a Route from a current GPS location, to a building or room within that building
    public Route getRoute(LatLng gpsStartLocation, String endLocation, String endRoom)
    {
        Route route = null;
        OutdoorVertex currLocation = null;
        OutdoorVertex endBuildingEntrance;
        Route entranceToEndRoom;

        if(startEndLocations.containsKey(endLocation))
        {
            //Tries to find where the GPS location is to route from
            for (WalkableZone currZone : walkableZones) {
                if (currZone.zoneContainsLatLngPos(gpsStartLocation)) {
                    currLocation = new OutdoorVertex(gpsStartLocation);
                    currZone.connectVertexToZone(currLocation);

                    break;
                }
            }

            if(currLocation != null)
            {
                endBuildingEntrance = findBestEntranceExitTo(currLocation, endLocation);

                //Create a route from the current location, to the destination building.
                //Then create a route from the entrance to the destination room and combine them
                route = routeFinder.findRoute(currLocation, endBuildingEntrance);
                entranceToEndRoom = routeFromBuildingEntranceToDest(endLocation, endBuildingEntrance, endRoom);

                if(route != null)
                {
                    route.combineRoutes(entranceToEndRoom);
                }
            }
        }

        return route;
    }

    //Used for creation of a Route from a building and/or room to a current GPS location
    public Route getRoute(String startLocation, String startRoom, LatLng gpsEndLocation)
    {
        Route route = null;
        OutdoorVertex currLocation = null;
        OutdoorVertex startBuildingExit;
        Route startToCurrLocationRoute;

        if(startEndLocations.containsKey(startLocation))
        {
            //todo split this up into seperate sections (aka walkable zones) to speed up checking the location
            //Tries to find where the GPS location is to route from
            for (WalkableZone currZone : walkableZones) {
                if (currZone.zoneContainsLatLngPos(gpsEndLocation)) {
                    currLocation = new OutdoorVertex(gpsEndLocation);
                    currZone.connectVertexToZone(currLocation);

                    break;
                }
            }

            if(currLocation != null)
            {
                //Shortest distance from the starting building to the current location
                startBuildingExit = findBestEntranceExitTo(currLocation, startLocation);

                //Creates a route from the starting room, to the exit and then a route from the exit to the current location
                route = routeFromStartRoomToExit(startLocation, startRoom, startBuildingExit);
                startToCurrLocationRoute = routeFinder.findRoute(startBuildingExit, currLocation);

                //Combines the two potential routes together
                if (route != null)
                {
                    route.combineRoutes(startToCurrLocationRoute);
                }
                else
                {
                    route = startToCurrLocationRoute;
                }
            }
        }

        return route;
    }

    //Creates a route for when both of the rooms are within the same building
    private Route routeSameBuildingRooms(String building, String startRoom, String endRoom)
    {
        Route route = null;
        final IndoorVertex startRoomVertex = findRoomVertex(startRoom, building);
        final IndoorVertex endRoomVertex = findRoomVertex(endRoom, building);
        final IndoorVertex closestStairs;
        final IndoorVertex destinationFloorStairs;

        if (startRoomVertex != null && endRoomVertex != null)
        {
            if(startRoomVertex.getFloor() == endRoomVertex.getFloor())
            {
                route = routeFinder.findRoute(startRoomVertex, endRoomVertex);
            }
            else //Different floors, find the closest stairs
            {
                //Find the closest stairs to the starting room, then find a route from it to them
                closestStairs = closestStairsToRoom(building, startRoomVertex);

                //Creates a route from the starting room to the closest stairs
                Thread thread1 = new Thread()
                {
                    @Override
                    public void run() {
                        firstRoutePart = routeFinder.findRoute(startRoomVertex, closestStairs);
                    }
                };

                thread1.start();

                //Stairs that will connect to the closest stairs, that will now be on the same level as the destination
                destinationFloorStairs = closestStairs.findStairsConnection(endRoomVertex.getFloor());

                //Creates a route from the last stairs to the destination room
                Thread thread2 = new Thread()
                {
                    @Override
                    public void run()
                    {
                        //From the stairs on the destination level to the destination itself
                        secondRoutePart = routeFinder.findRoute(destinationFloorStairs, endRoomVertex);
                    }
                };

                thread2.start();

                //Wait for the seperate route threads to finish, then combine the routes all together
                try
                {
                    thread1.join();
                    thread2.join();

                    route = firstRoutePart;

                    //Combine the seperate floor routes
                    route.combineRoutes(secondRoutePart);
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }

        return route;
    }

    //Creates a route for going from a building, to outdoors, then to enter the destination building
    private Route routeViaInToOut(final String startBuilding, final String startRoom, final String endBuilding, final String endRoom)
    {
        Route route = null;
        ArrayList<Vertex> startBuildingExits = startEndLocations.get(startBuilding);
        ArrayList<Vertex> endBuildingEntrances = startEndLocations.get(endBuilding);
        OutdoorVertex tempExit = null;
        OutdoorVertex tempEntrance = null;
        final OutdoorVertex startBuildingExit;
        final OutdoorVertex endBuildingEntrance;
        int tempDist;
        int bestDist = Integer.MAX_VALUE;

        //Find the best entrance/exit combination between all entrance/exits
        for (Vertex start : startBuildingExits)
        {
            for (Vertex destination : endBuildingEntrances)
            {
                tempDist = start.getDistanceFrom(destination);

                if (tempDist < bestDist)
                {
                    tempExit = (OutdoorVertex) start;
                    tempEntrance = (OutdoorVertex) destination;
                    bestDist = tempDist;
                }
            }
        }

        startBuildingExit = tempExit;
        endBuildingEntrance = tempEntrance;

        //Route from start room -> building exit
        Thread thread1 = new Thread()
        {
            @Override
            public void run() {
                //Route from the starting room, to the exit of the building
                firstRoutePart = routeFromStartRoomToExit(startBuilding, startRoom, startBuildingExit);
            }
        };

        thread1.start();

        //Route from start building exit -> end building entrance
        Thread thread2 = new Thread()
        {
            @Override
            public void run() {
                //Create a route from the start buildings exit to the destination building entrances
                secondRoutePart = routeFinder.findRoute(startBuildingExit, endBuildingEntrance);
            }
        };

        thread2.start();

        //Route from end building entrance -> end room
        Thread thread3 = new Thread()
        {
            @Override
            public void run()
            {
                //Route from the destination building entrance to the destination room
                lastRoutePart = routeFromBuildingEntranceToDest(endBuilding, endBuildingEntrance, endRoom);
            }
        };

        thread3.start();

        //Wait for the separate route threads to finish, then combine the routes all together
        try
        {
            thread1.join();
            thread2.join();
            thread3.join();

            //Combine all the separate routes into one single route
            if (firstRoutePart != null)
            {
                firstRoutePart.combineRoutes(secondRoutePart);
                route = firstRoutePart;
            }
            else
            {
                route = secondRoutePart;
            }

            if(route != null)
            {
                route.combineRoutes(lastRoutePart);
            }
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }

        return route;
    }

    // Used for creating a route between buildings that are connected by tunnels in some way
    private Route routeViaTunnels(String startBuilding, String startRoom, String endBuilding, String endRoom)
    {
        Route route = null;
        final int TUNNELS_FLOOR = 1;
        final IndoorVertex startRoomVertex = findRoomVertex(startRoom, startBuilding);
        final IndoorVertex endRoomVertex = findRoomVertex(endRoom, endBuilding);
        final IndoorVertex startClosestStairs;
        final IndoorVertex startTunnelFloorStairs;
        final IndoorVertex endClosestStairs;
        final IndoorVertex endTunnelFloorStairs;
        Route startToStairs;
        Route tunnelStairsRoute;
        Route stairsToEnd;

        if(startRoomVertex != null && endRoomVertex != null)
        {
            /* Four cases for the tunnel routing conditions
                1) they are both on the tunnel floor
                2) first room is on the tunnel floor, second room isnt
                3) second room is on the tunnel floor, first room isnt
                4) they are both not on the tunnel floor
             */
            if(startRoomVertex.getFloor() == endRoomVertex.getFloor() && startRoomVertex.getFloor() == TUNNELS_FLOOR)
            {
                route = routeFinder.findRoute(startRoomVertex, endRoomVertex);
            }
            else if(startRoomVertex.getFloor() == TUNNELS_FLOOR)
            {
                //Find the closest stairs to the destination room, create a route from the stairs to the room
                endClosestStairs = closestStairsToRoom(endBuilding, endRoomVertex);

                //Stairs that will connect from the tunnel floor, to the destination rooms floor
                endTunnelFloorStairs = endClosestStairs.findStairsConnection(TUNNELS_FLOOR);

                startToStairs = routeFinder.findRoute(startRoomVertex, endClosestStairs);
                stairsToEnd = routeFinder.findRoute(endTunnelFloorStairs, endRoomVertex);

                if(startToStairs != null && stairsToEnd != null)
                {
                    startToStairs.combineRoutes(stairsToEnd);
                    route = startToStairs;
                }
            }
            else if(endRoomVertex.getFloor() == TUNNELS_FLOOR)
            {
                //Find the closest stairs to the starting room, then find a route room to stairs
                startClosestStairs = closestStairsToRoom(startBuilding, startRoomVertex);

                //Stairs that will connect to the closest stairs, that will now be on the same level as the tunnels
                startTunnelFloorStairs = startClosestStairs.findStairsConnection(TUNNELS_FLOOR);

                startToStairs = routeFinder.findRoute(startRoomVertex, startClosestStairs);
                stairsToEnd = routeFinder.findRoute(startTunnelFloorStairs, endRoomVertex);

                if(startToStairs != null && stairsToEnd != null)
                {
                    startToStairs.combineRoutes(stairsToEnd);
                    route = startToStairs;
                }
            }
            else
            {
                //Find the closest stairs to the starting room, then find a route room to stairs
                startClosestStairs = closestStairsToRoom(startBuilding, startRoomVertex);

                //Find the closest stairs to the destination room, create a route from the stairs to the room
                endClosestStairs = closestStairsToRoom(endBuilding, endRoomVertex);

                //Stairs that will connect to the closest stairs, that will now be on the same level as the tunnels
                startTunnelFloorStairs = startClosestStairs.findStairsConnection(TUNNELS_FLOOR);

                //Stairs that will connect from the tunnel floor, to the destination rooms floor
                endTunnelFloorStairs = endClosestStairs.findStairsConnection(TUNNELS_FLOOR);

                startToStairs = routeFinder.findRoute(startRoomVertex, startClosestStairs);
                tunnelStairsRoute = routeFinder.findRoute(startTunnelFloorStairs, endTunnelFloorStairs);
                stairsToEnd = routeFinder.findRoute(endClosestStairs, endRoomVertex);

                if(startToStairs != null && tunnelStairsRoute != null && stairsToEnd != null)
                {
                    startToStairs.combineRoutes(tunnelStairsRoute);
                    startToStairs.combineRoutes(stairsToEnd);
                    route = startToStairs;
                }
            }
        }

        return route;
    }

    //Creates a route from a room within a building, to the specified exit of that building
    private Route routeFromStartRoomToExit(String building, String startRoom, OutdoorVertex exitVertex)
    {
        Route route = null;
        Route stairsToExit;
        IndoorVertex indoorExit;
        IndoorVertex startFloorStairs;
        IndoorVertex exitFloorStairs;
        IndoorVertex startRoomVertex = findRoomVertex(startRoom, building);

        //Finds a route from the starting room to the closest set of stairs, and then from those stairs to the exit
        if (startRoomVertex != null) {
            indoorExit = exitVertex.findIndoorConnection();

            if (indoorExit.getFloor() != startRoomVertex.getFloor())
            {
                startFloorStairs = closestStairsToRoom(building, startRoomVertex);
                exitFloorStairs = startFloorStairs.findStairsConnection(indoorExit.getFloor());
                stairsToExit = routeFinder.findRoute(exitFloorStairs, indoorExit);
                route = routeFinder.findRoute(startRoomVertex, startFloorStairs);

                route.combineRoutes(stairsToExit);
            }
            else
            {
                route = routeFinder.findRoute(startRoomVertex, indoorExit);
            }
        }

        return route;
    }

    //Creates a route from a building entrance, to a specified room within that building
    private Route routeFromBuildingEntranceToDest(String building, OutdoorVertex entranceVertex, String endRoom)
    {
        Route route = null;
        Route stairsToDestRoom;
        IndoorVertex indoorEntrance;
        IndoorVertex entranceFloorStairs;
        IndoorVertex destinationFloorStairs;
        IndoorVertex endRoomVertex = findRoomVertex(endRoom, building);

        //Finds a route from the entrance, a set of stairs, then from those stairs to the destination room
        if (endRoomVertex != null) {
            indoorEntrance = entranceVertex.findIndoorConnection();

            if (indoorEntrance.getFloor() != endRoomVertex.getFloor())
            {
                destinationFloorStairs = closestStairsToRoom(building, endRoomVertex);
                entranceFloorStairs = destinationFloorStairs.findStairsConnection(indoorEntrance.getFloor());
                route = routeFinder.findRoute(indoorEntrance, entranceFloorStairs);
                stairsToDestRoom = routeFinder.findRoute(destinationFloorStairs, endRoomVertex);

                route.combineRoutes(stairsToDestRoom);
            }
            else
            {
                route = routeFinder.findRoute(indoorEntrance, endRoomVertex);
            }
        }

        return route;
    }

    private IndoorVertex closestStairsToRoom(String building, IndoorVertex room)
    {
        IndoorVertex stairs = null;

        if(building.equals(resources.getString(R.string.armes)))
        {
            stairs = armesIndoorConnections.getClosestStairsToRoom(room);
        }
        else if(building.equals(resources.getString(R.string.machray)))
        {
            stairs = machrayIndoorConnections.getClosestStairsToRoom(room);
        }
        else if(building.equals(resources.getString(R.string.allen)))
        {
            stairs = allenIndoorConnections.getClosestStairsToRoom(room);
        }

        return stairs;
    }

    //Finds the shortest distance entrance / exit combination from the location to the building
    private OutdoorVertex findBestEntranceExitTo(OutdoorVertex location, String building)
    {
        OutdoorVertex bestEntExit = null;
        ArrayList<Vertex> endBuildingEntrances = startEndLocations.get(building);
        int tempDist;
        int bestDist = Integer.MAX_VALUE;

        //Find the best entrance from the current location
        for (Vertex destination : endBuildingEntrances) {
            tempDist = location.getDistanceFrom(destination);

            if (tempDist < bestDist) {
                bestEntExit = (OutdoorVertex) destination;
                bestDist = tempDist;
            }
        }

        return bestEntExit;
    }

    //Tries to find a specified room within a building
    private IndoorVertex findRoomVertex(String room, String building)
    {
        IndoorVertex roomVertex = null;

        if (building.equals(resources.getString(R.string.armes))) {
            roomVertex = armesIndoorConnections.findRoom(room);
        }
        else if(building.equals(resources.getString(R.string.machray)))
        {
            roomVertex = machrayIndoorConnections.findRoom(room);
        }
        else if(building.equals(resources.getString(R.string.allen)))
        {
            roomVertex = allenIndoorConnections.findRoom(room);
        }

        return roomVertex;
    }

    //Creates all of the WalkableZones sections of the mesh, that are used for finding where the GPS location is coming from
    //Also contains all of the Vertex's for navigation, along with their connections to one another
    private void populateMesh()
    {
        String agri_engineer = resources.getString(R.string.agr_engineer);

        //Bottom is the entrance
        WalkableZone agri_engineer_north_ent = new WalkableZone(new LatLng(49.807551, -97.133870), new LatLng(49.807568, -97.133802), new LatLng(49.807471, -97.133811), new LatLng(49.807485, -97.133747));
        agri_engineer_north_ent.setTop(new LatLng(49.807573, -97.133832));
        agri_engineer_north_ent.setBottom(new LatLng(49.807491, -97.133790));

        addEntrance(agri_engineer, agri_engineer_north_ent.getBottom());
        walkableZones.add(agri_engineer_north_ent);

        WalkableZone a = new WalkableZone(new LatLng(49.807610, -97.133883), new LatLng(49.807633, -97.133787), new LatLng(49.807551, -97.133870), new LatLng(49.807568, -97.133802));
        a.setLeft(new LatLng(49.807577, -97.133863));
        a.setRight(new LatLng(49.807589, -97.133770));
        a.setBottom(agri_engineer_north_ent.getTop());

        //Bottom is the entrance
        WalkableZone agri_engineer_south_ent = new WalkableZone(new LatLng(49.807450, -97.134213), new LatLng(49.807464, -97.134139), new LatLng(49.807396, -97.134163), new LatLng(49.807409, -97.134103));
        agri_engineer_south_ent.setTop(new LatLng(49.807460, -97.134169));
        agri_engineer_south_ent.setBottom(new LatLng(49.807406, -97.134132));

        addEntrance(agri_engineer, agri_engineer_south_ent.getBottom());
        walkableZones.add(agri_engineer_south_ent);

        WalkableZone b = new WalkableZone(new LatLng(49.807475, -97.134234), new LatLng(49.807495, -97.134172), new LatLng(49.807450, -97.134213), new LatLng(49.807464, -97.134139));
        b.setLeft(new LatLng(49.807456, -97.134232));
        b.setRight(new LatLng(49.807472, -97.134153));
        b.setBottom(agri_engineer_south_ent.getTop());

        String agriculture = resources.getString(R.string.agriculture);

        //Bottom is the entrance
        WalkableZone agriculture_north_ent = new WalkableZone(new LatLng(49.807129, -97.135141), new LatLng(49.807152, -97.135075 ), new LatLng(49.807080, -97.135103), new LatLng(49.807106, -97.135021));
        agriculture_north_ent.setBottom(new LatLng(49.807100, -97.135065));
        agriculture_north_ent.setTop(new LatLng(49.807138, -97.135099));

        addEntrance(agriculture, agriculture_north_ent.getBottom());
        walkableZones.add(agriculture_north_ent);

        WalkableZone c = new WalkableZone(new LatLng(49.807164, -97.135187), new LatLng(49.807193, -97.135104), new LatLng(49.807129, -97.135141), new LatLng(49.807152, -97.135075));
        c.setLeft(new LatLng(49.807141, -97.135138));
        c.setRight(new LatLng(49.807172, -97.135082));
        c.setBottom(agriculture_north_ent.getTop());

        //Bottom is the entrance
        WalkableZone agriculture_south_ent = new WalkableZone(new LatLng(49.806989, -97.135595), new LatLng(49.807014, -97.135512), new LatLng(49.806936, -97.135529), new LatLng(49.806952, -97.135486));
        agriculture_south_ent.setTop(new LatLng(49.807007, -97.135541));
        agriculture_south_ent.setBottom(new LatLng(49.806958, -97.135498));

        addEntrance(agriculture, agriculture_south_ent.getBottom());
        walkableZones.add(agriculture_south_ent);

        WalkableZone d = new WalkableZone(new LatLng(49.807019, -97.135584), new LatLng(49.807036, -97.135521), new LatLng(49.806989, -97.135595), new LatLng(49.807014, -97.135512));
        d.setLeft(new LatLng(49.806997, -97.135575));
        d.setRight(new LatLng(49.807014, -97.135511));
        d.setBottom(agriculture_south_ent.getTop());

        //Right
        WalkableZone agriculture_south_west_ent = new WalkableZone(new LatLng(49.806597, -97.136071), new LatLng(49.806609, -97.135974), new LatLng(49.806531, -97.136056), new LatLng(49.806552, -97.135955));
        agriculture_south_west_ent.setRight(new LatLng(49.806573, -97.135970));
        agriculture_south_west_ent.setLeft(new LatLng(49.806546, -97.136050));
        agriculture_south_west_ent.setTop(new LatLng(49.806585, -97.136020));

        walkableZones.add(agriculture_south_west_ent);
        addEntrance(agriculture, agriculture_south_west_ent.getRight());

        String allen = resources.getString(R.string.allen);
        String armes = resources.getString(R.string.armes);
        String parker = resources.getString(R.string.parker);

        //Right is entrance
        WalkableZone allen_armes_parker = new WalkableZone(new LatLng(49.810959, -97.134638), new LatLng(49.811028, -97.134368), new LatLng(49.810859, -97.134555), new LatLng(49.810940, -97.134297));
        allen_armes_parker.setLeft(new LatLng(49.810950, -97.134629));
        allen_armes_parker.setRight(new LatLng(49.810993, -97.134347));

        addEntrance(allen, allen_armes_parker.getRight());
        addEntrance(armes, allen_armes_parker.getRight());
        addEntrance(parker, allen_armes_parker.getRight());
        walkableZones.add(allen_armes_parker);

        //right is entrance
        WalkableZone allen_armes = new WalkableZone(new LatLng(49.810577, -97.134319), new LatLng(49.810651, -97.134069), new LatLng(49.810489, -97.134233), new LatLng(49.810576, -97.134008));
        allen_armes.setRight(new LatLng(49.810614, -97.134037));
        allen_armes.setLeft(new LatLng(49.810533, -97.134257));

        addEntrance(allen, allen_armes.getRight());
        addEntrance(armes, allen_armes.getRight());
        walkableZones.add(allen_armes);

        WalkableZone e = new WalkableZone(new LatLng(49.810533, -97.134452), new LatLng(49.810577, -97.134319), new LatLng(49.810454, -97.134363), new LatLng(49.810489, -97.134233));
        e.setBottom(new LatLng(49.810471, -97.134296));
        e.setLeft(new LatLng(49.810482, -97.134379));
        e.setRight(allen_armes.getLeft());

        String animalSci = resources.getString(R.string.animal_sci);

        //bottomMiddle is entrance
        WalkableZone animal_sci_north_ent = new WalkableZone(new LatLng(49.806251, -97.137677), new LatLng(49.806283, -97.137574), new LatLng(49.806171, -97.137650), new LatLng(49.806203, -97.137552));
        animal_sci_north_ent.setBottom(new LatLng(49.806214, -97.137599));
        animal_sci_north_ent.setTop(new LatLng(49.806275, -97.137608));

        addEntrance(animalSci, animal_sci_north_ent.getBottom());
        walkableZones.add(animal_sci_north_ent);

        WalkableZone f = new WalkableZone(new LatLng(49.806270, -97.137785), new LatLng(49.806324, -97.137599), new LatLng(49.806251, -97.137677), new LatLng(49.806283, -97.137574));
        f.setLeft(new LatLng(49.806251, -97.137814));
        f.setRight(new LatLng(49.806315, -97.137570));
        f.setTop(animal_sci_north_ent.getBottom());

        //bottomMiddle is entrance
        WalkableZone animal_sci_south_ent = new WalkableZone(new LatLng(49.806055, -97.138194), new LatLng(49.806088, -97.138104), new LatLng(49.806023, -97.138121), new LatLng(49.806044, -97.138058));
        animal_sci_south_ent.setTop(new LatLng(49.806080, -97.138134));
        animal_sci_south_ent.setBottom(new LatLng(49.806032, -97.138085));

        addEntrance(animalSci, animal_sci_south_ent.getBottom());
        walkableZones.add(animal_sci_south_ent);

        WalkableZone g = new WalkableZone(new LatLng(49.806110, -97.138247), new LatLng(49.806138, -97.138146), new LatLng(49.806055, -97.138194), new LatLng(49.806088, -97.138104));
        g.setLeft(new LatLng(49.806112, -97.138202));
        g.setRight(new LatLng(49.806138, -97.138101));
        g.setBottom(animal_sci_south_ent.getTop());

        String archi2 = resources.getString(R.string.archi_2);

        //bottom is entrance
        WalkableZone archi2_north_ent = new WalkableZone(new LatLng(49.808076, -97.136340), new LatLng(49.808090, -97.136241), new LatLng(49.807958, -97.136240), new LatLng(49.807985, -97.136136));
        archi2_north_ent.setTop(new LatLng(49.808076, -97.136310));
        archi2_north_ent.setRight(new LatLng(49.808017, -97.136158));
        archi2_north_ent.setBottom(new LatLng(49.807970, -97.136214));

        addEntrance(archi2, archi2_north_ent.getBottom());
        walkableZones.add(archi2_north_ent);

        //top is entrance
        WalkableZone archi2_south_ent = new WalkableZone(new LatLng(49.807778, -97.136417), new LatLng(49.807816, -97.136319), new LatLng(49.807708, -97.136326), new LatLng(49.807741, -97.136225));
        archi2_south_ent.setTop(new LatLng(49.807786, -97.136334));
        archi2_south_ent.setBottom(new LatLng(49.807725, -97.136278));

        addEntrance(archi2, archi2_south_ent.getBottom());
        walkableZones.add(archi2_south_ent);

        //right
        WalkableZone archi2_west_ent = new WalkableZone(new LatLng(49.807778, -97.137130), new LatLng(49.807834, -97.136870), new LatLng(49.807675, -97.137019), new LatLng(49.807759, -97.136834));
        archi2_west_ent.setRight(new LatLng(49.807761, -97.136842));
        archi2_west_ent.setLeft(new LatLng(49.807692, -97.137034));
        archi2_west_ent.setTop(new LatLng(49.807831, -97.136946));

        addEntrance(archi2, archi2_west_ent.getRight());
        walkableZones.add(archi2_west_ent);

        String machray = resources.getString(R.string.machray);

        //top is entrance
        WalkableZone armes_machray = new WalkableZone(new LatLng(49.810977, -97.133496), new LatLng(49.811021, -97.133344), new LatLng(49.810778, -97.133302), new LatLng(49.810819, -97.133178));
        armes_machray.setTop(new LatLng(49.810997, -97.133403));
        armes_machray.setRight(new LatLng(49.811005, -97.133292));

        addEntrance(armes, armes_machray.getTop());
        addEntrance(machray, armes_machray.getTop());
        walkableZones.add(armes_machray);

        WalkableZone h = new WalkableZone(new LatLng(49.811021, -97.133344), new LatLng(49.811063, -97.133218), new LatLng(49.810819, -97.133178), new LatLng(49.810857, -97.133063));
        h.setRight(new LatLng(49.811023, -97.133166));
        h.setLeft(armes_machray.getLeft());

        String artlab = resources.getString(R.string.artlab);

        //right is entrance
        WalkableZone artlab_ent = new WalkableZone(new LatLng(49.808630, -97.130596), new LatLng(49.808680, -97.130466), new LatLng(49.808323, -97.130325), new LatLng(49.808332, -97.130279));
        artlab_ent.setTop(new LatLng(49.808661, -97.130542));
        artlab_ent.setRight(new LatLng(49.808648, -97.130393));
        artlab_ent.setBottom(new LatLng(49.808326, -97.130296));
        artlab_ent.setLeft(new LatLng(49.808612, -97.130553));

        addEntrance(artlab, artlab_ent.getRight());
        walkableZones.add(artlab_ent);

        String bioSci = resources.getString(R.string.bio_sci);

        //left is entrance
        WalkableZone bio_science_east_ent = new WalkableZone(new LatLng(49.810327, -97.134509), new LatLng(49.810361, -97.134390), new LatLng(49.810240, -97.134439), new LatLng(49.810274, -97.134310));
        bio_science_east_ent.setTop(new LatLng(49.810332, -97.134465));
        bio_science_east_ent.setLeft(new LatLng(49.810287, -97.134455));
        bio_science_east_ent.setRight(new LatLng(49.810312, -97.134349));

        addEntrance(bioSci, bio_science_east_ent.getLeft());
        walkableZones.add(bio_science_east_ent);

        //right is entrance
        WalkableZone bio_science_west_ent = new WalkableZone(new LatLng(49.810106, -97.135083), new LatLng(49.810126, -97.135071), new LatLng(49.810045, -97.135043), new LatLng(49.810066, -97.135023));
        bio_science_west_ent.setTop(new LatLng(49.810110, -97.135071));
        bio_science_west_ent.setRight(new LatLng(49.810090, -97.135044));

        addEntrance(bioSci, bio_science_west_ent.getRight());
        walkableZones.add(bio_science_west_ent);

        String buller = resources.getString(R.string.buller);

        //top is entrance
        WalkableZone buller_west_ent = new WalkableZone(new LatLng(49.810325, -97.133594), new LatLng(49.810342, -97.133531), new LatLng(49.810283, -97.133552), new LatLng(49.810300, -97.133482));
        buller_west_ent.setTop(new LatLng(49.810327, -97.133548));
        buller_west_ent.setBottom(new LatLng(49.810288, -97.133519));

        addEntrance(buller, buller_west_ent.getTop());
        walkableZones.add(buller_west_ent);

        //top is entrance
        WalkableZone buller_east_ent = new WalkableZone(new LatLng(49.810452, -97.133216), new LatLng(49.810471, -97.133141), new LatLng(49.810408, -97.133167), new LatLng(49.810427, -97.133103));
        buller_east_ent.setTop(new LatLng(49.810459, -97.133175));
        buller_east_ent.setBottom(new LatLng(49.810418, -97.133134));

        addEntrance(buller, buller_east_ent.getTop());
        walkableZones.add(buller_east_ent);

        String dairySci = resources.getString(R.string.dairy_science);

        //bottom
        WalkableZone dairy_science_ent = new WalkableZone(new LatLng(49.807686, -97.133389), new LatLng(49.807705, -97.133314), new LatLng(49.807632, -97.133349), new LatLng(49.807653, -97.133286));
        dairy_science_ent.setBottom(new LatLng(49.807654, -97.133320));
        dairy_science_ent.setTop(new LatLng(49.807702, -97.133360));

        addEntrance(dairySci, dairy_science_ent.getBottom());
        walkableZones.add(dairy_science_ent);

        WalkableZone j = new WalkableZone(new LatLng(49.807725, -97.133415), new LatLng(49.807743, -97.133356), new LatLng(49.807686, -97.133389), new LatLng(49.807705, -97.133314));
        j.setBottom(dairy_science_ent.getTop());
        j.setLeft(new LatLng(49.807709, -97.133405));
        j.setRight(new LatLng(49.807729, -97.133342));

        String drake = resources.getString(R.string.drake_centre);

        //bottom
        WalkableZone drake_ent = new WalkableZone(new LatLng(49.808328, -97.130346), new LatLng(49.808348, -97.130283), new LatLng(49.808202, -97.130248), new LatLng(49.808226, -97.130175));
        drake_ent.setTop(artlab_ent.getBottom());
        drake_ent.setBottom(new LatLng(49.808213, -97.130217));

        addEntrance(drake, drake_ent.getBottom());
        walkableZones.add(drake_ent);

        String duffRoblin = resources.getString(R.string.duff_roblin);

        //right
        WalkableZone duff_roblin_west_ent = new WalkableZone(new LatLng(49.810884, -97.133065), new LatLng(49.810902, -97.133002), new LatLng(49.810840, -97.133062), new LatLng(49.810861, -97.132970));
        duff_roblin_west_ent.setRight(new LatLng(49.810894, -97.132999));
        duff_roblin_west_ent.setBottom(new LatLng(49.810849, -97.132993));
        duff_roblin_west_ent.setTop(new LatLng(49.810888, -97.133021));

        addEntrance(duffRoblin, duff_roblin_west_ent.getRight());
        walkableZones.add(duff_roblin_west_ent);

        WalkableZone k = new WalkableZone(new LatLng(49.810975, -97.133121), new LatLng(49.810993, -97.133068), new LatLng(49.810884, -97.133065), new LatLng(49.810902, -97.133002));
        k.setTop(new LatLng(49.810981, -97.133100));
        k.setBottom(duff_roblin_west_ent.getTop());

        WalkableZone l = new WalkableZone(new LatLng(49.810840, -97.133062), new LatLng(49.810861, -97.132970), new LatLng(49.810749, -97.132958), new LatLng(49.810776, -97.132903));
        l.setBottom(new LatLng(49.810765, -97.132917));
        l.setTop(duff_roblin_west_ent.getBottom());

        //top
        WalkableZone duff_roblin_south_ent = new WalkableZone(new LatLng(49.810942, -97.132367), new LatLng(49.810993, -97.132212), new LatLng(49.810878, -97.132348), new LatLng(49.810933, -97.132188));
        duff_roblin_south_ent.setTop(new LatLng(49.810961, -97.132287));
        duff_roblin_south_ent.setLeft(new LatLng(49.810923, -97.132351));

        addEntrance(duffRoblin, duff_roblin_south_ent.getTop());
        walkableZones.add(duff_roblin_south_ent);

        //bottom is entrance
        WalkableZone duff_roblin_north_ent = new WalkableZone(new LatLng(49.811263, -97.132699), new LatLng(49.811324, -97.132479), new LatLng(49.811143, -97.132621), new LatLng(49.811264, -97.132260));
        duff_roblin_north_ent.setBottom(new LatLng(49.811189, -97.132487));
        duff_roblin_north_ent.setLeft(new LatLng(49.811177, -97.132604));
        duff_roblin_north_ent.setRight(new LatLng(49.811308, -97.132463));

        addEntrance(duffRoblin, duff_roblin_north_ent.getBottom());
        walkableZones.add(duff_roblin_north_ent);

        String education = resources.getString(R.string.education);

        //top
        WalkableZone education_south_ent = new WalkableZone(new LatLng(49.808603, -97.136485), new LatLng(49.808638, -97.136368), new LatLng(49.808546, -97.136412), new LatLng(49.808578, -97.136300));
        education_south_ent.setTop(new LatLng(49.808624, -97.136406));
        education_south_ent.setRight(new LatLng(49.808612, -97.136333));

        addEntrance(education, education_south_ent.getTop());
        walkableZones.add(education_south_ent);

        //right
        WalkableZone education_west_ent = new WalkableZone(new LatLng(49.808459, -97.137405), new LatLng(49.808490, -97.137321), new LatLng(49.808391, -97.137367), new LatLng(49.808426, -97.137259));
        education_west_ent.setRight(new LatLng(49.808452, -97.137281));
        education_west_ent.setBottom(new LatLng(49.808409, -97.137312));

        addEntrance(education, education_west_ent.getRight());
        walkableZones.add(education_west_ent);

        //left
        WalkableZone education_north_ent = new WalkableZone(new LatLng(49.809193, -97.136623), new LatLng(49.809237, -97.136483), new LatLng(49.809045, -97.136514), new LatLng(49.809091, -97.136383));
        education_north_ent.setLeft(new LatLng(49.809142, -97.136583));
        education_north_ent.setRight(new LatLng(49.809199, -97.136452));

        //todo turn this on later once inside navigation works
        //addEntrance(education, education_north_ent.getLeft());
        walkableZones.add(education_north_ent);

        String e1_eitc = resources.getString(R.string.eitc_e1);

        //left
        WalkableZone eitc_e1_east_ent = new WalkableZone(new LatLng(49.808477, -97.133103), new LatLng(49.808573, -97.132806), new LatLng(49.808452, -97.133072), new LatLng(49.808516, -97.132811));
        eitc_e1_east_ent.setLeft(new LatLng(49.808453, -97.133077));
        eitc_e1_east_ent.setRight(new LatLng(49.808540, -97.132805));

        addEntrance(e1_eitc, eitc_e1_east_ent.getLeft());
        walkableZones.add(eitc_e1_east_ent);

        WalkableZone m = new WalkableZone(new LatLng(49.808573, -97.132806), new LatLng(49.808586, -97.132744), new LatLng(49.808516, -97.132811), new LatLng(49.808553, -97.132706));
        m.setTop(new LatLng(49.808571, -97.132774));
        m.setBottom(new LatLng(49.808541, -97.132743));
        m.setLeft(eitc_e1_east_ent.getRight());

        String e3_eitc = resources.getString(R.string.eitc_e3);

        //top
        WalkableZone eitc_e1_e3 = new WalkableZone(new LatLng(49.808217, -97.134071), new LatLng(49.808257, -97.133959), new LatLng(49.807822, -97.133792), new LatLng(49.807866, -97.133607));
        eitc_e1_e3.setTop(new LatLng(49.808243, -97.133994));
        eitc_e1_e3.setBottom(new LatLng(49.807835, -97.133736));

        addEntrance(e1_eitc, eitc_e1_e3.getTop());
        addEntrance(e3_eitc, eitc_e1_e3.getTop());
        walkableZones.add(eitc_e1_e3);

        WalkableZone n = new WalkableZone(new LatLng(49.807822, -97.133792), new LatLng(49.807866, -97.133607), new LatLng(49.807773, -97.133753), new LatLng(49.807830, -97.133600));
        n.setRight(new LatLng(49.807840, -97.133618));
        n.setLeft(new LatLng(49.807797, -97.133749));
        n.setTop(eitc_e1_e3.getBottom());

        String e2_eitc = resources.getString(R.string.eitc_e2);

        //left
        WalkableZone eitc_e1_e2 = new WalkableZone(new LatLng(49.808665, -97.133268), new LatLng(49.808765, -97.132963), new LatLng(49.808637, -97.133238), new LatLng(49.808729, -97.132932));
        eitc_e1_e2.setRight(new LatLng(49.808750, -97.132950));
        eitc_e1_e2.setLeft(new LatLng(49.808648, -97.133257));

        addEntrance(e1_eitc, eitc_e1_e2.getLeft());
        addEntrance(e2_eitc, eitc_e1_e2.getLeft());
        walkableZones.add(eitc_e1_e2);

        WalkableZone o = new WalkableZone(new LatLng(49.808765, -97.132963), new LatLng(49.808786, -97.132890), new LatLng(49.808729, -97.132932), new LatLng(49.808763, -97.132867));
        o.setBottom(new LatLng(49.808745, -97.132906));
        o.setTop(new LatLng(49.808772, -97.132921));
        o.setLeft(eitc_e1_e2.getRight());

        //bottom
        WalkableZone eitc_e2_ent = new WalkableZone(new LatLng(49.808984, -97.133486), new LatLng(49.809029, -97.133355), new LatLng(49.808951, -97.133464), new LatLng(49.808962, -97.133295));
        eitc_e2_ent.setTop(new LatLng(49.809005, -97.133424));
        eitc_e2_ent.setRight(new LatLng(49.808996, -97.133327));
        eitc_e2_ent.setBottom(new LatLng(49.808937, -97.133421));

        addEntrance(e2_eitc, eitc_e2_ent.getBottom());
        walkableZones.add(eitc_e2_ent);

        //bottom
        WalkableZone eitc_e3_ent = new WalkableZone(new LatLng(49.808693, -97.134597), new LatLng(49.808714, -97.134514), new LatLng(49.808622, -97.134579), new LatLng(49.808649, -97.134462));
        eitc_e3_ent.setTop(new LatLng(49.808705, -97.134566));
        eitc_e3_ent.setBottom(new LatLng(49.808643, -97.134489));

        addEntrance(e3_eitc, eitc_e3_ent.getBottom());
        walkableZones.add(eitc_e3_ent);

        String eliDafoe = resources.getString(R.string.elizabeth_dafoe);

        //top
        WalkableZone eli_dafoe_ent = new WalkableZone(new LatLng(49.810011, -97.132205), new LatLng(49.809925, -97.131626), new LatLng(49.809633, -97.131902), new LatLng(49.809799, -97.131580));
        eli_dafoe_ent.setTop(new LatLng(49.809993, -97.132140));
        eli_dafoe_ent.setRight(new LatLng(49.809935, -97.131675));
        eli_dafoe_ent.setBottom(new LatLng(49.809667, -97.131876));

        addEntrance(eliDafoe, eli_dafoe_ent.getRight());
        walkableZones.add(eli_dafoe_ent);

        String extEduc = resources.getString(R.string.ext_education);

        //right
        WalkableZone ext_education_west_ent = new WalkableZone(new LatLng(49.807054, -97.139374), new LatLng(49.807129, -97.139223), new LatLng(49.806972, -97.139307), new LatLng(49.807047, -97.139160));
        ext_education_west_ent.setLeft(new LatLng(49.807031, -97.139354));
        ext_education_west_ent.setRight(new LatLng(49.807073, -97.139269));

        addEntrance(extEduc, ext_education_west_ent.getRight());
        walkableZones.add(ext_education_west_ent);

        WalkableZone p = new WalkableZone(new LatLng(49.807035, -97.139448), new LatLng(49.807054, -97.139374), new LatLng(49.806952, -97.139383), new LatLng(49.806972, -97.139307));
        p.setTop(new LatLng(49.807047, -97.139411));
        p.setBottom(new LatLng(49.806955, -97.139350));
        p.setRight(ext_education_west_ent.getLeft());

        //left
        WalkableZone ext_education_east_ent = new WalkableZone(new LatLng(49.807610, -97.137967), new LatLng(49.807655, -97.137753), new LatLng(49.807457, -97.137887), new LatLng(49.807535, -97.137650));
        ext_education_east_ent.setRight(new LatLng(49.807557, -97.137671));
        ext_education_east_ent.setLeft(new LatLng(49.807553, -97.137825));
        ext_education_east_ent.setTop(new LatLng(49.807641, -97.137787));

        addEntrance(extEduc, ext_education_east_ent.getLeft());
        walkableZones.add(ext_education_east_ent);

        String fletcher = resources.getString(R.string.fletcher);

        //right
        WalkableZone fletcher_ent = new WalkableZone(new LatLng(49.809539, -97.131706), new LatLng(49.809621, -97.131453), new LatLng(49.809394, -97.131575), new LatLng(49.809488, -97.131327));
        fletcher_ent.setLeft(new LatLng(49.809476, -97.131635));
        fletcher_ent.setRight(new LatLng(49.809565, -97.131352));

        addEntrance(fletcher, fletcher_ent.getRight());
        walkableZones.add(fletcher_ent);

        //top
        WalkableZone helen_glass = new WalkableZone(new LatLng(49.808812, -97.135812), new LatLng(49.808858, -97.135535), new LatLng(49.808681, -97.135794), new LatLng(49.808662, -97.135456));
        helen_glass.setTop(new LatLng(49.808810, -97.135583));
        helen_glass.setLeft(new LatLng(49.808760, -97.135817));
        helen_glass.setRight(new LatLng(49.808709, -97.135467));
        helen_glass.setBottom(new LatLng(49.808640, -97.135591));

        addEntrance(resources.getString(R.string.helen_glass), helen_glass.getTop());
        walkableZones.add(helen_glass);

        //right
        WalkableZone human_eco = new WalkableZone(new LatLng(49.810717, -97.132430), new LatLng(49.810746, -97.132365), new LatLng(49.810622, -97.132354), new LatLng(49.810643, -97.132287));
        human_eco.setTop(new LatLng(49.810717, -97.132393));
        human_eco.setRight(new LatLng(49.810700, -97.132333));
        human_eco.setLeft(new LatLng(49.810673, -97.132386));

        addEntrance(resources.getString(R.string.human_ecology), human_eco.getRight());
        walkableZones.add(human_eco);

        //top
        WalkableZone isbister = new WalkableZone(new LatLng(49.809111, -97.130311), new LatLng(49.809136, -97.130213), new LatLng(49.809044, -97.130238), new LatLng(49.809069, -97.130169));
        isbister.setTop(new LatLng(49.809124, -97.130257));
        isbister.setBottom(new LatLng(49.809062, -97.130209));
        isbister.setLeft(new LatLng(49.809061, -97.130252));

        addEntrance(resources.getString(R.string.isbister), isbister.getTop());
        walkableZones.add(isbister);

        //bottom
        WalkableZone parker_ent = new WalkableZone(new LatLng(49.811590, -97.134868), new LatLng(49.811697, -97.134544), new LatLng(49.811560, -97.134857), new LatLng(49.811656, -97.134506));
        parker_ent.setLeft(new LatLng(49.811578, -97.134849));
        parker_ent.setBottom(new LatLng(49.811591, -97.134773));

        addEntrance(parker, parker_ent.getBottom());
        walkableZones.add(parker_ent);

        String robertSchultz = resources.getString(R.string.robert_schultz);

        //right
        WalkableZone robert_schultz_west_ent = new WalkableZone(new LatLng(49.810117, -97.136969), new LatLng(49.810235, -97.136765), new LatLng(49.810086, -97.136956), new LatLng(49.810187, -97.136725));
        robert_schultz_west_ent.setTop(new LatLng(49.810215, -97.136786));
        robert_schultz_west_ent.setLeft(new LatLng(49.810097, -97.136940));
        robert_schultz_west_ent.setRight(new LatLng(49.810203, -97.136740));

        addEntrance(robertSchultz, robert_schultz_west_ent.getRight());
        walkableZones.add(robert_schultz_west_ent);

        //top
        WalkableZone robert_schultz_south_ent = new WalkableZone(new LatLng(49.809909, -97.136491), new LatLng(49.809988, -97.136205), new LatLng(49.809813, -97.136425), new LatLng(49.809910, -97.136148));
        robert_schultz_south_ent.setTop(new LatLng(49.809943, -97.136340));
        robert_schultz_south_ent.setLeft(new LatLng(49.809833, -97.136435));
        robert_schultz_south_ent.setRight( new LatLng(49.809922, -97.136160));

        addEntrance(robertSchultz, robert_schultz_south_ent.getTop());
        walkableZones.add(robert_schultz_south_ent);

        //right
        WalkableZone robson = new WalkableZone(new LatLng(49.811811, -97.131165), new LatLng(49.811827, -97.131042), new LatLng(49.811731, -97.131095), new LatLng(49.811738, -97.130961));
        robson.setTop(new LatLng(49.811825, -97.131126));
        robson.setRight(new LatLng(49.811758, -97.130976));
        robson.setLeft(new LatLng(49.811797, -97.131148));

        addEntrance(resources.getString(R.string.robson), robson.getRight());
        walkableZones.add(robson);

        String russel = resources.getString(R.string.russel);

        //bot
        WalkableZone russel_north_ent = new WalkableZone(new LatLng(49.808270, -97.135577), new LatLng(49.808300, -97.135478), new LatLng(49.808178, -97.135498), new LatLng(49.808210, -97.135404));
        russel_north_ent.setTop(new LatLng(49.808286, -97.135528));
        russel_north_ent.setBottom(new LatLng(49.808192, -97.135455));

        addEntrance(russel, russel_north_ent.getBottom());
        walkableZones.add(russel_north_ent);

        //top
        WalkableZone russel_south_ent = new WalkableZone(new LatLng(49.807892, -97.135264), new LatLng(49.807921, -97.135176), new LatLng(49.807418, -97.134878), new LatLng(49.807444, -97.134803));
        russel_south_ent.setTop(new LatLng(49.807905, -97.135216));
        russel_south_ent.setBottom(new LatLng(49.807431, -97.134837));

        addEntrance(russel, russel_south_ent.getTop());
        walkableZones.add(russel_south_ent);

        String stJohns = resources.getString(R.string.st_johns);

        //right
        WalkableZone st_johns_west_ent = new WalkableZone(new LatLng(49.810469, -97.137293), new LatLng(49.810568, -97.137053), new LatLng(49.810413, -97.137276), new LatLng(49.810545, -97.137009));
        st_johns_west_ent.setTop(new LatLng(49.810520, -97.137185));
        st_johns_west_ent.setLeft(new LatLng(49.810438, -97.137287));
        st_johns_west_ent.setRight(new LatLng(49.810557, -97.137024));
        st_johns_west_ent.setBottom(new LatLng(49.810486, -97.137138));

        addEntrance(stJohns, st_johns_west_ent.getRight());
        walkableZones.add(st_johns_west_ent);

        //bot
        WalkableZone st_johns_north_ent = new WalkableZone(new LatLng(49.810817, -97.137225), new LatLng(49.810850, -97.137101), new LatLng(49.810714, -97.137174), new LatLng(49.810759, -97.137040));
        st_johns_north_ent.setLeft(new LatLng(49.810760, -97.137195));
        st_johns_north_ent.setBottom(new LatLng(49.810749, -97.137065));

        addEntrance(stJohns, st_johns_north_ent.getBottom());
        walkableZones.add(st_johns_north_ent);

        //left
        WalkableZone st_johns_east_ent = new WalkableZone(new LatLng(49.811217, -97.136940), new LatLng(49.811264, -97.136789), new LatLng(49.810877, -97.136669), new LatLng(49.810910, -97.136532));
        st_johns_east_ent.setTop(new LatLng(49.811239, -97.136864));
        st_johns_east_ent.setBottom(new LatLng(49.810898, -97.136607));

        addEntrance(stJohns, st_johns_east_ent.getLeft());
        walkableZones.add(st_johns_east_ent);

        String stPauls = resources.getString(R.string.st_pauls);

        //bot
        WalkableZone st_pauls_north_ent = new WalkableZone(new LatLng(49.810576, -97.138317), new LatLng(49.810650, -97.138129), new LatLng(49.810397, -97.138124), new LatLng(49.810463, -97.137942));
        st_pauls_north_ent.setTop(new LatLng(49.810584, -97.138266));
        st_pauls_north_ent.setBottom(new LatLng(49.810430, -97.138044));

        addEntrance(stPauls, st_pauls_north_ent.getBottom());
        walkableZones.add(st_pauls_north_ent);

        //left
        WalkableZone st_pauls_east_ent = new WalkableZone(new LatLng(49.809905, -97.137418), new LatLng(49.810040, -97.137095), new LatLng(49.809859, -97.137385), new LatLng(49.810001, -97.137022));
        st_pauls_east_ent.setLeft(new LatLng(49.809888, -97.137377));
        st_pauls_east_ent.setRight(new LatLng(49.810018, -97.137080));
        st_pauls_east_ent.setBottom(new LatLng(49.809914, -97.137222));

        addEntrance(stPauls, st_pauls_east_ent.getLeft());
        walkableZones.add(st_pauls_east_ent);

        String tacheArts = resources.getString(R.string.tache_arts);

        //bottom
        WalkableZone tache_arts_east_ent = new WalkableZone(new LatLng(49.808460, -97.131075), new LatLng(49.808497, -97.130925), new LatLng(49.808366, -97.131007), new LatLng(49.808402, -97.130871));
        tache_arts_east_ent.setBottom(new LatLng(49.808391, -97.130937));
        tache_arts_east_ent.setRight(new LatLng(49.808466, -97.130921));
        tache_arts_east_ent.setTop(new LatLng(49.808483, -97.131013));

        addEntrance(tacheArts, tache_arts_east_ent.getBottom());
        walkableZones.add(tache_arts_east_ent);

        //bottom
        WalkableZone tache_arts_mid_west_ent = new WalkableZone(new LatLng(49.808036, -97.132293), new LatLng(49.808060, -97.132226), new LatLng(49.807934, -97.132253), new LatLng(49.807977, -97.132147));
        tache_arts_mid_west_ent.setTop(new LatLng(49.808048, -97.132261));
        tache_arts_mid_west_ent.setBottom(new LatLng(49.807962, -97.132190));

        addEntrance(tacheArts, tache_arts_mid_west_ent.getBottom());
        walkableZones.add(tache_arts_mid_west_ent);

        //right
        WalkableZone tache_arts_west_ent = new WalkableZone(new LatLng(49.807839, -97.132685), new LatLng(49.807823, -97.132539), new LatLng(49.807798, -97.132663), new LatLng(49.807783, -97.132571));
        tache_arts_west_ent.setRight(new LatLng(49.807803, -97.132558));
        tache_arts_west_ent.setLeft(new LatLng(49.807821, -97.132675));

        addEntrance(tacheArts, tache_arts_west_ent.getRight());
        walkableZones.add(tache_arts_west_ent);

        String tier = resources.getString(R.string.tier);

        //left
        WalkableZone tier_tunnel = new WalkableZone(new LatLng(49.808856, -97.130641), new LatLng(49.808900, -97.130525), new LatLng(49.808780, -97.130590), new LatLng(49.808820, -97.130469));
        tier_tunnel.setRight(new LatLng(49.808851, -97.130514));
        tier_tunnel.setLeft(new LatLng(49.808811, -97.130605));
        tier_tunnel.setTop(new LatLng(49.808867, -97.130581));

        //todo toggle this back on when tier indoor navigation works
        //addEntrance(tier, tier_tunnel.getTop());
        walkableZones.add(tier_tunnel);

        //right
        WalkableZone tier_west_ent = new WalkableZone(new LatLng(49.809150, -97.131390), new LatLng(49.809223, -97.131163), new LatLng(49.809084, -97.131322), new LatLng(49.809143, -97.131097));
        tier_west_ent.setTop(new LatLng(49.809159, -97.131304));
        tier_west_ent.setLeft(new LatLng(49.809095, -97.131370));
        tier_west_ent.setRight(new LatLng(49.809178, -97.131130));
        tier_west_ent.setBottom(new LatLng(49.809110, -97.131273));

        addEntrance(tier, tier_west_ent.getRight());
        walkableZones.add(tier_west_ent);

        //top
        WalkableZone tier_south_ent = new WalkableZone(new LatLng(49.808947, -97.130789), new LatLng(49.808961, -97.130703), new LatLng(49.808839, -97.130715), new LatLng(49.808857, -97.130621));
        tier_south_ent.setTop(new LatLng(49.808940, -97.130745));
        tier_south_ent.setLeft(new LatLng(49.808867, -97.130741));
        tier_south_ent.setBottom(new LatLng(49.808847, -97.130678));

        addEntrance(tier, tier_south_ent.getTop());
        walkableZones.add(tier_south_ent);

        String uniCentre = resources.getString(R.string.uni_centre);

        //right
        WalkableZone uni_centre_west_ent = new WalkableZone(new LatLng(49.809210, -97.135372), new LatLng(49.809279, -97.135180), new LatLng(49.808874, -97.135086), new LatLng(49.808933, -97.134911));
        uni_centre_west_ent.setRight(new LatLng(49.809072, -97.135035));
        uni_centre_west_ent.setBottom(new LatLng(49.808910, -97.134955));

        addEntrance(uniCentre, uni_centre_west_ent.getRight());
        walkableZones.add(uni_centre_west_ent);

        //left
        WalkableZone uni_centre_east_ent = new WalkableZone(new LatLng(49.809316, -97.133742), new LatLng(49.809422, -97.133583), new LatLng(49.809203, -97.133654), new LatLng(49.809302, -97.133481));
        uni_centre_east_ent.setLeft(new LatLng(49.809291, -97.133712));
        uni_centre_east_ent.setRight(new LatLng(49.809362, -97.133536));

        addEntrance(uniCentre, uni_centre_east_ent.getLeft());
        walkableZones.add(uni_centre_east_ent);

        //left
        WalkableZone uni_centre_north_ent = new WalkableZone(new LatLng(49.809717, -97.134074), new LatLng(49.809757, -97.133985), new LatLng(49.809351, -97.133827), new LatLng(49.809370, -97.133664));
        uni_centre_north_ent.setLeft(new LatLng(49.809455, -97.133905));
        uni_centre_north_ent.setRight(new LatLng(49.809491, -97.133766));
        uni_centre_north_ent.setBottom(new LatLng(49.809349, -97.133790));

        addEntrance(uniCentre, uni_centre_north_ent.getLeft());
        walkableZones.add(uni_centre_north_ent);

        //top
        WalkableZone uni_centre_south_west_ent = new WalkableZone(new LatLng(49.808773, -97.135190), new LatLng(49.808804, -97.135030), new LatLng(49.808773, -97.135073), new LatLng(49.808781, -97.135027));
        uni_centre_south_west_ent.setTop(new LatLng(49.808781, -97.135062));
        uni_centre_south_west_ent.setBottom(new LatLng(49.808778, -97.135052));

        addEntrance(uniCentre, uni_centre_south_west_ent.getTop());
        walkableZones.add(uni_centre_south_west_ent);

        //top
        WalkableZone uni_college = new WalkableZone(new LatLng(49.811066, -97.131298), new LatLng(49.811142, -97.131053), new LatLng(49.810810, -97.131114), new LatLng(49.810893, -97.130871));
        uni_college.setTop(new LatLng(49.811055, -97.131105));
        uni_college.setLeft(new LatLng(49.810852, -97.131118));

        addEntrance(resources.getString(R.string.uni_college), uni_college.getTop());
        walkableZones.add(uni_college);

        //top
        WalkableZone wallace = new WalkableZone(new LatLng(49.811675, -97.135725), new LatLng(49.811666, -97.135649), new LatLng(49.811611, -97.135740), new LatLng(49.811599, -97.135649));
        wallace.setTop(new LatLng(49.811658, -97.135693));
        wallace.setBottom(new LatLng(49.811608, -97.135696));

        addEntrance(resources.getString(R.string.wallace), wallace.getTop());
        walkableZones.add(wallace);

        //bottom is entrance
        WalkableZone welcome_centre_ent = new WalkableZone(new LatLng(49.806274, -97.139653), new LatLng(49.806258, -97.139530), new LatLng(49.806235, -97.139658), new LatLng(49.806215, -97.139568));
        welcome_centre_ent.setLeft(new LatLng(49.806239, -97.139653));
        welcome_centre_ent.setRight(new LatLng(49.806207, -97.139565));
        welcome_centre_ent.setBottom(new LatLng(49.806228, -97.139611));

        walkableZones.add(welcome_centre_ent);
        addEntrance(resources.getString(R.string.welcome_centre), welcome_centre_ent.getBottom());

        WalkableZone q = new WalkableZone(new LatLng(49.809053, -97.130248), new LatLng(49.809074, -97.130167), new LatLng(49.808961, -97.130183), new LatLng(49.808984, -97.130097));
        q.setTop(isbister.getBottom());
        q.setBottom(new LatLng(49.808979, -97.130159));

        walkableZones.add(q);

        WalkableZone r = new WalkableZone(new LatLng(49.808961, -97.130183), new LatLng(49.808984, -97.130097), new LatLng(49.808933, -97.130170), new LatLng(49.808966, -97.130065));
        r.setTop(q.getBottom());
        r.setLeft(new LatLng(49.808944, -97.130198));

        walkableZones.add(r);

        WalkableZone s = new WalkableZone(new LatLng(49.808889, -97.130385), new LatLng(49.808961, -97.130183), new LatLng(49.808867, -97.130377), new LatLng(49.808933, -97.130170));
        s.setLeft(new LatLng(49.808876, -97.130384));
        s.setRight(r.getLeft());

        walkableZones.add(s);

        WalkableZone t = new WalkableZone(new LatLng(49.808889, -97.130549), new LatLng(49.808917, -97.130417), new LatLng(49.808816, -97.130490), new LatLng(49.808857, -97.130376));
        t.setLeft(tier_tunnel.getRight());
        t.setRight(s.getLeft());
        t.setTop(new LatLng(49.808904, -97.130478));

        walkableZones.add(t);

        WalkableZone u = new WalkableZone(new LatLng(49.809000, -97.130608), new LatLng(49.809095, -97.130290), new LatLng(49.808890, -97.130532), new LatLng(49.808981, -97.130248));
        u.setBottom(t.getTop());
        u.setRight(isbister.getLeft());

        walkableZones.add(u);

        WalkableZone v = new WalkableZone(new LatLng(49.808839, -97.130715), new LatLng(49.808857, -97.130621), new LatLng(49.808766, -97.130649), new LatLng(49.808792, -97.130572));
        v.setTop(tier_south_ent.getBottom());
        v.setRight(tier_tunnel.getLeft());
        v.setLeft(new LatLng(49.808804, -97.130670));

        walkableZones.add(d);

        WalkableZone w = new WalkableZone(new LatLng(49.808795, -97.130915), new LatLng(49.808839, -97.130715), new LatLng(49.808698, -97.130856), new LatLng(49.808766, -97.130649));
        w.setBottom(new LatLng(49.808758, -97.130674));
        w.setRight(v.getLeft());
        w.setLeft(new LatLng(49.808720, -97.130867));
        w.setTop(new LatLng(49.808804, -97.130796));

        walkableZones.add(e);

        WalkableZone x = new WalkableZone(new LatLng(49.808850, -97.130968), new LatLng(49.808916, -97.130771), new LatLng(49.808795, -97.130915), new LatLng(49.808839, -97.130715));
        x.setBottom(w.getTop());
        x.setTop(new LatLng(49.808855, -97.130921));
        x.setRight(tier_south_ent.getLeft());

        walkableZones.add(f);

        WalkableZone y = new WalkableZone(new LatLng(49.809096, -97.131293), new LatLng(49.809113, -97.131221), new LatLng(49.808836, -97.130941), new LatLng(49.808870, -97.130892));
        y.setTop(tier_west_ent.getBottom());
        y.setBottom(x.getTop());

        walkableZones.add(y);

        WalkableZone z = new WalkableZone(new LatLng(49.808720, -97.131075), new LatLng(49.808792, -97.130897), new LatLng(49.808630, -97.131024), new LatLng(49.808699, -97.130837));
        z.setRight(w.getLeft());
        z.setLeft(new LatLng(49.808679, -97.131043));

        walkableZones.add(z);

        WalkableZone aa = new WalkableZone(new LatLng(49.808749, -97.130731), new LatLng(49.808776, -97.130608), new LatLng(49.808626, -97.130625), new LatLng(49.808666, -97.130509));
        aa.setTop(w.getBottom());
        aa.setBottom(artlab_ent.getTop());

        walkableZones.add(aa);

        WalkableZone ab = new WalkableZone(new LatLng(49.809368, -97.131550), new LatLng(49.809367, -97.131519), new LatLng(49.809131, -97.131328), new LatLng(49.809156, -97.131268));
        ab.setTop(new LatLng(49.809317, -97.131483));
        ab.setBottom(tier_west_ent.getTop());

        walkableZones.add(ab);

        WalkableZone ac = new WalkableZone(new LatLng(49.809393, -97.131592), new LatLng(49.809405, -97.131512), new LatLng(49.809311, -97.131535), new LatLng(49.809333, -97.131430));
        ac.setBottom(ab.getTop());
        ac.setLeft(new LatLng(49.809348, -97.131537));

        walkableZones.add(ac);

        WalkableZone ad = new WalkableZone(new LatLng(49.809374, -97.131675), new LatLng(49.809400, -97.131586), new LatLng(49.809305, -97.131618), new LatLng(49.809335, -97.131521));
        ad.setLeft(new LatLng(49.809343, -97.131669));
        ad.setTop(new LatLng(49.809384, -97.131631));
        ad.setRight(ac.getLeft());
        ad.setBottom(new LatLng(49.809321, -97.131602));

        walkableZones.add(ad);

        WalkableZone ae = new WalkableZone(new LatLng(49.809307, -97.131886), new LatLng(49.809372, -97.131680), new LatLng(49.809275, -97.131873), new LatLng(49.809311, -97.131631));
        ae.setBottom(new LatLng(49.809297, -97.131784));
        ae.setTop(new LatLng(49.809337, -97.131786));
        ae.setRight(ad.getLeft());

        walkableZones.add(ae);

        WalkableZone af = new WalkableZone(new LatLng(49.809529, -97.131830), new LatLng(49.809559, -97.131706), new LatLng(49.809356, -97.131694), new LatLng(49.809393, -97.131578));
        af.setTop(new LatLng(49.809548, -97.131778));
        af.setRight(fletcher_ent.getLeft());
        af.setLeft(new LatLng(49.809454, -97.131758));
        af.setBottom(ad.getTop());

        walkableZones.add(af);

        WalkableZone ag = new WalkableZone(new LatLng(49.809729, -97.131988), new LatLng(49.809759, -97.131866), new LatLng(49.809529, -97.131830), new LatLng(49.809559, -97.131706));
        ag.setTop(eli_dafoe_ent.getBottom());
        ag.setBottom(af.getTop());

        walkableZones.add(ag);

        WalkableZone ah = new WalkableZone(new LatLng(49.809475, -97.131959), new LatLng(49.809537, -97.131816), new LatLng(49.809306, -97.131885), new LatLng(49.809369, -97.131681));
        ah.setRight(af.getLeft());
        ah.setBottom(ae.getTop());
        ah.setLeft(new LatLng(49.809414, -97.131894));

        walkableZones.add(ah);

        WalkableZone ai = new WalkableZone(new LatLng(49.809453, -97.132051), new LatLng(49.809439, -97.131880), new LatLng(49.809406, -97.132022), new LatLng(49.809384, -97.131902));
        ai.setLeft(new LatLng(49.809437, -97.132016));
        ai.setBottom(ah.getLeft());

        walkableZones.add(ai);

        WalkableZone aj = new WalkableZone(new LatLng(49.809322, -97.132436), new LatLng(49.809454, -97.132039), new LatLng(49.809270, -97.132397), new LatLng(49.809402, -97.131994));
        aj.setLeft(new LatLng(49.809299, -97.132413));
        aj.setRight(ai.getLeft());

        walkableZones.add(aj);

        WalkableZone ak = new WalkableZone(new LatLng(49.809331, -97.132564), new LatLng(49.809380, -97.132450), new LatLng(49.809238, -97.132501), new LatLng(49.809277, -97.132381));
        ak.setLeft(new LatLng(49.809260, -97.132509));
        ak.setRight(aj.getLeft());
        ak.setBottom(new LatLng(49.809257, -97.132445));

        walkableZones.add(ak);

        WalkableZone al = new WalkableZone(new LatLng(49.809158, -97.132906), new LatLng(49.809331, -97.132564), new LatLng(49.809119, -97.132877), new LatLng(49.809238, -97.132501));
        al.setLeft(new LatLng(49.809140, -97.132895));
        al.setRight(ak.getLeft());

        walkableZones.add(al);

        WalkableZone am = new WalkableZone(new LatLng(49.809298, -97.131625), new LatLng(49.809333, -97.131560), new LatLng(49.809083, -97.131436), new LatLng(49.809103, -97.131378));
        am.setTop(ad.getBottom());
        am.setBottom(new LatLng(49.809098, -97.131423));

        walkableZones.add(am);

        WalkableZone an = new WalkableZone(new LatLng(49.809083, -97.131436), new LatLng(49.809103, -97.131378), new LatLng(49.808982, -97.131379), new LatLng(49.808998, -97.131296));
        an.setLeft(new LatLng(49.809013, -97.131396));
        an.setTop(am.getBottom());
        an.setBottom(new LatLng(49.808993, -97.131339));
        an.setRight(tier_west_ent.getLeft());

        walkableZones.add(an);

        WalkableZone ao = new WalkableZone(new LatLng(49.808996, -97.131400), new LatLng(49.809008, -97.131280), new LatLng(49.808695, -97.131158), new LatLng(49.808716, -97.131070));
        ao.setBottom(new LatLng(49.808711, -97.131107));
        ao.setTop(an.getBottom());

        walkableZones.add(ao);

        WalkableZone ap = new WalkableZone(new LatLng(49.808751, -97.131194), new LatLng(49.808763, -97.131079), new LatLng(49.808614, -97.131094), new LatLng(49.808639, -97.131023));
        ap.setTop(ao.getBottom());
        ap.setRight(z.getLeft());
        ap.setLeft(new LatLng(49.808662, -97.131126));

        walkableZones.add(ap);

        WalkableZone aq = new WalkableZone(new LatLng(49.811059, -97.133230), new LatLng(49.811081, -97.133173), new LatLng(49.810958, -97.133122), new LatLng(49.810981, -97.133065));
        aq.setLeft(h.getRight());
        aq.setBottom(k.getTop());

        walkableZones.add(aq);

        WalkableZone ar = new WalkableZone(new LatLng(49.810436, -97.132360), new LatLng(49.810937, -97.131046), new LatLng(49.810395, -97.132365), new LatLng(49.810804, -97.130952));
        ar.setLeft(new LatLng(49.810417, -97.132356));
        ar.setRight(uni_college.getLeft());

        walkableZones.add(ar);

        WalkableZone as = new WalkableZone(new LatLng(49.810418, -97.132463), new LatLng(49.810436, -97.132360), new LatLng(49.810375, -97.132416), new LatLng(49.810395, -97.132365));
        as.setTop(new LatLng(49.810424, -97.132399));
        as.setRight(ar.getLeft());
        as.setLeft(new LatLng(49.810397, -97.132421));

        walkableZones.add(as);

        WalkableZone at = new WalkableZone(new LatLng(49.810696, -97.132424), new LatLng(49.810672, -97.132329), new LatLng(49.810410, -97.132430), new LatLng(49.810426, -97.132356));
        at.setBottom(as.getTop());
        at.setTop(human_eco.getLeft());

        walkableZones.add(at);

        WalkableZone au = new WalkableZone(new LatLng(49.810853, -97.132527), new LatLng(49.810853, -97.132444), new LatLng(49.810710, -97.132419), new LatLng(49.810723, -97.132348));
        au.setTop(new LatLng(49.810868, -97.132463));
        au.setBottom(human_eco.getTop());

        walkableZones.add(au);

        WalkableZone av = new WalkableZone(new LatLng(49.810908, -97.132452), new LatLng(49.810942, -97.132363), new LatLng(49.810881, -97.132445), new LatLng(49.810904, -97.132352));
        av.setLeft(new LatLng(49.810895, -97.132448));
        av.setRight(duff_roblin_south_ent.getLeft());

        walkableZones.add(av);

        WalkableZone aw = new WalkableZone(new LatLng(49.810787, -97.132837), new LatLng(49.810882, -97.132556), new LatLng(49.810729, -97.132765), new LatLng(49.810817, -97.132489));
        aw.setLeft(new LatLng(49.810773, -97.132774));
        aw.setRight(av.getLeft());

        walkableZones.add(aw);

        WalkableZone ax = new WalkableZone(new LatLng(49.810755, -97.132960), new LatLng(49.810801, -97.132790), new LatLng(49.810686, -97.132908), new LatLng(49.810730, -97.132731));
        ax.setRight(aw.getLeft());
        ax.setTop(l.getBottom());
        ax.setBottom(new LatLng(49.810713, -97.132832));

        walkableZones.add(ax);

        WalkableZone ay = new WalkableZone(new LatLng(49.810686, -97.132908), new LatLng(49.810730, -97.132731), new LatLng(49.810543, -97.132789), new LatLng(49.810578, -97.132646));
        ay.setTop(ax.getBottom());
        ay.setBottom(new LatLng(49.810556, -97.132738));

        walkableZones.add(ay);

        WalkableZone az = new WalkableZone(new LatLng(49.810543, -97.132789), new LatLng(49.810578, -97.132646), new LatLng(49.810460, -97.132734), new LatLng(49.810494, -97.132623));
        az.setLeft(new LatLng(49.810501, -97.132754));
        az.setTop(ay.getBottom());
        az.setBottom(new LatLng(49.810480, -97.132685));

        walkableZones.add(az);

        WalkableZone ba = new WalkableZone(new LatLng(49.810473, -97.132753), new LatLng(49.810517, -97.132602), new LatLng(49.810360, -97.132643), new LatLng(49.810393, -97.132518));
        ba.setTop(az.getBottom());
        ba.setBottom(new LatLng(49.810383, -97.132568));

        walkableZones.add(ba);

        WalkableZone bb = new WalkableZone(new LatLng(49.810375, -97.132623), new LatLng(49.810431, -97.132430), new LatLng(49.810329, -97.132581), new LatLng(49.810375, -97.132374));
        bb.setTop(ba.getBottom());
        bb.setRight(as.getLeft());
        bb.setBottom(new LatLng(49.810353, -97.132491));

        walkableZones.add(bb);

        WalkableZone bc = new WalkableZone(new LatLng(49.810329, -97.132581), new LatLng(49.810375, -97.132374), new LatLng(49.810247, -97.132535), new LatLng(49.810292, -97.132322));
        bc.setTop(bb.getBottom());
        bc.setBottom(new LatLng(49.810271, -97.132448));

        walkableZones.add(bc);

        WalkableZone bd = new WalkableZone(new LatLng(49.810247, -97.132535), new LatLng(49.810292, -97.132322), new LatLng(49.810197, -97.132524), new LatLng(49.810254, -97.132291));
        bd.setLeft(new LatLng(49.810227, -97.132536));
        bd.setBottom(new LatLng(49.810231, -97.132411));
        bd.setTop(bc.getBottom());

        walkableZones.add(bd);

        WalkableZone be = new WalkableZone(new LatLng(49.810197, -97.132524), new LatLng(49.810254, -97.132291), new LatLng(49.809969, -97.132338), new LatLng(49.810003, -97.132069));
        be.setTop(bd.getBottom());
        be.setBottom(eli_dafoe_ent.getTop());

        walkableZones.add(be);

        WalkableZone bf = new WalkableZone(new LatLng(49.810224, -97.132706), new LatLng(49.810260, -97.132539), new LatLng(49.810165, -97.132647), new LatLng(49.810215, -97.132481));
        bf.setLeft(new LatLng(49.810185, -97.132659));
        bf.setRight(bd.getLeft());
        bf.setTop(new LatLng(49.810229, -97.132611));

        walkableZones.add(bf);

        WalkableZone bg = new WalkableZone(new LatLng(49.810301, -97.133067), new LatLng(49.810350, -97.133026), new LatLng(49.810201, -97.132675), new LatLng(49.810230, -97.132535));
        bg.setBottom(bf.getTop());
        bg.setLeft(new LatLng(49.810316, -97.133019));

        walkableZones.add(bg);

        WalkableZone bh = new WalkableZone(new LatLng(49.810371, -97.133132), new LatLng(49.810388, -97.133057), new LatLng(49.810285, -97.133075), new LatLng(49.810296, -97.133002));
        bh.setTop(new LatLng(49.810374, -97.133096));
        bh.setLeft(new LatLng(49.810330, -97.133117));
        bh.setRight(bg.getLeft());

        walkableZones.add(bh);

        WalkableZone bi = new WalkableZone(new LatLng(49.810411, -97.133172), new LatLng(49.810443, -97.133085), new LatLng(49.810371, -97.133132), new LatLng(49.810388, -97.133057));
        bi.setTop(buller_east_ent.getBottom());
        bi.setRight(new LatLng(49.810404, -97.133084));
        bi.setLeft(new LatLng(49.810384, -97.133145));
        bi.setBottom(bh.getTop());

        walkableZones.add(bi);

        WalkableZone bj = new WalkableZone(new LatLng(49.810443, -97.133085), new LatLng(49.810551, -97.132790), new LatLng(49.810388, -97.133057), new LatLng(49.810467, -97.132720));
        bj.setLeft(bi.getRight());
        bj.setRight(az.getLeft());

        walkableZones.add(bj);

        WalkableZone bk = new WalkableZone(new LatLng(49.810299, -97.133496), new LatLng(49.810416, -97.133163), new LatLng(49.810244, -97.133450), new LatLng(49.810358, -97.133125));
        bk.setRight(bi.getLeft());
        bk.setLeft(new LatLng(49.810280, -97.133476));
        bk.setBottom(new LatLng(49.810309, -97.133285));

        walkableZones.add(bk);

        WalkableZone bl = new WalkableZone(new LatLng(49.810244, -97.133450), new LatLng(49.810358, -97.133125), new LatLng(49.810174, -97.133407), new LatLng(49.810289, -97.133064));
        bl.setTop(bk.getBottom());
        bk.setLeft(new LatLng(49.810221, -97.133422));
        bk.setRight(bh.getLeft());

        walkableZones.add(bl);

        WalkableZone bm = new WalkableZone(new LatLng(49.810286, -97.133572), new LatLng(49.810307, -97.133489), new LatLng(49.810224, -97.133515), new LatLng(49.810243, -97.133439));
        bm.setTop(buller_west_ent.getBottom());
        bm.setLeft(new LatLng(49.810252, -97.133532));
        bm.setRight(bk.getLeft());
        bm.setBottom(new LatLng(49.810235, -97.133477));

        walkableZones.add(bm);

        WalkableZone bn = new WalkableZone(new LatLng(49.810224, -97.133515), new LatLng(49.810243, -97.133439), new LatLng(49.810142, -97.133446), new LatLng(49.810161, -97.133388));
        bn.setTop(bm.getBottom());
        bn.setRight(bl.getLeft());
        bn.setLeft(new LatLng(49.810178, -97.133464));

        walkableZones.add(bn);

        WalkableZone bo = new WalkableZone(new LatLng(49.810155, -97.133922), new LatLng(49.810285, -97.133550), new LatLng(49.810106, -97.133866), new LatLng(49.810228, -97.133504));
        bo.setLeft(new LatLng(49.810132, -97.133894));
        bo.setRight(bm.getLeft());

        walkableZones.add(bo);

        WalkableZone bp = new WalkableZone(new LatLng(49.809926, -97.133515), new LatLng(49.810205, -97.132665), new LatLng(49.809884, -97.133500), new LatLng(49.810178, -97.132621));
        bp.setLeft(new LatLng(49.809891, -97.133519));
        bp.setRight(bf.getLeft());

        walkableZones.add(bp);

        WalkableZone bq = new WalkableZone(new LatLng(49.809860, -97.133670), new LatLng(49.809920, -97.133507), new LatLng(49.809825, -97.133649), new LatLng(49.809887, -97.133490));
        bq.setRight(bp.getLeft());
        bq.setLeft(new LatLng(49.809846, -97.133635));
        bq.setTop(new LatLng(49.809900, -97.133571));

        walkableZones.add(bq);

        WalkableZone br = new WalkableZone(new LatLng(49.810191, -97.133507), new LatLng(49.810209, -97.133397), new LatLng(49.809866, -97.133643), new LatLng(49.809915, -97.133501));
        br.setBottom(bq.getTop());
        br.setRight(bn.getLeft());

        walkableZones.add(br);

        WalkableZone bs = new WalkableZone(new LatLng(49.809798, -97.133902), new LatLng(49.809878, -97.133641), new LatLng(49.809771, -97.133873), new LatLng(49.809829, -97.133602));
        bs.setRight(bq.getLeft());
        bs.setBottom(new LatLng(49.809815, -97.133708));
        bs.setTop(new LatLng(49.809836, -97.133761));

        walkableZones.add(bs);

        WalkableZone bt = new WalkableZone(new LatLng(49.810000, -97.133983), new LatLng(49.810053, -97.133804), new LatLng(49.809798, -97.133902), new LatLng(49.809878, -97.133641));
        bt.setTop(new LatLng(49.810009, -97.133896));
        bt.setBottom(bs.getTop());

        walkableZones.add(bt);

        WalkableZone bu = new WalkableZone(new LatLng(49.810053, -97.134035), new LatLng(49.810097, -97.133847), new LatLng(49.810000, -97.133983), new LatLng(49.810053, -97.133804));
        bu.setLeft(new LatLng(49.810024, -97.134002));
        bu.setBottom(bt.getTop());
        bu.setTop(new LatLng(49.810076, -97.133945));

        walkableZones.add(bu);

        WalkableZone bv = new WalkableZone(new LatLng(49.810076, -97.134399), new LatLng(49.810170, -97.134135), new LatLng(49.809702, -97.134224), new LatLng(49.809818, -97.133818));
        bv.setRight(bu.getLeft());
        bv.setTop(new LatLng(49.810126, -97.134269));

        walkableZones.add(bv);

        WalkableZone bw = new WalkableZone(new LatLng(49.810108, -97.134080), new LatLng(49.810164, -97.133896), new LatLng(49.810045, -97.134033), new LatLng(49.810088, -97.133837));
        bw.setRight(bo.getLeft());
        bw.setTop(new LatLng(49.810128, -97.134015));
        bw.setBottom(bu.getTop());

        walkableZones.add(bw);

        WalkableZone bx = new WalkableZone(new LatLng(49.810172, -97.134141), new LatLng(49.810207, -97.134004), new LatLng(49.810108, -97.134080), new LatLng(49.810164, -97.133896));
        bx.setBottom(bw.getTop());
        bx.setTop(new LatLng(49.810185, -97.134074));

        walkableZones.add(bx);

        WalkableZone by = new WalkableZone(new LatLng(49.810212, -97.134195), new LatLng(49.810256, -97.134047), new LatLng(49.810164, -97.134153), new LatLng(49.810207, -97.134012));
        by.setTop(new LatLng(49.810238, -97.134108));
        by.setBottom(bx.getTop());
        by.setLeft(new LatLng(49.810207, -97.134140));

        walkableZones.add(by);

        WalkableZone bz = new WalkableZone(new LatLng(49.810150, -97.134431), new LatLng(49.810212, -97.134195), new LatLng(49.810096, -97.134408), new LatLng(49.810164, -97.134153));
        bz.setBottom(bv.getTop());
        bz.setRight(by.getLeft());
        bz.setTop(new LatLng(49.810201, -97.134214));

        walkableZones.add(bz);

        WalkableZone ca = new WalkableZone(new LatLng(49.810276, -97.134331), new LatLng(49.810293, -97.134226), new LatLng(49.810187, -97.134255), new LatLng(49.810219, -97.134157));
        ca.setRight(new LatLng(49.810268, -97.134205));
        ca.setBottom(bz.getTop());
        ca.setTop(new LatLng(49.810285, -97.134272));

        walkableZones.add(ca);

        WalkableZone cb = new WalkableZone(new LatLng(49.810358, -97.134377), new LatLng(49.810388, -97.134295), new LatLng(49.810276, -97.134331), new LatLng(49.810293, -97.134226));
        cb.setLeft(bio_science_east_ent.getRight());
        cb.setRight(new LatLng(49.810341, -97.134265));
        cb.setTop(ca.getTop());

        walkableZones.add(cb);

        WalkableZone cc = new WalkableZone(new LatLng(49.810293, -97.134226), new LatLng(49.810328, -97.134087), new LatLng(49.810219, -97.134157), new LatLng(49.810251, -97.134032));
        cc.setLeft(ca.getRight());
        cc.setBottom(by.getTop());
        cc.setTop(new LatLng(49.810308, -97.134166));

        walkableZones.add(cc);

        WalkableZone cd = new WalkableZone(new LatLng(49.810383, -97.134303), new LatLng(49.810418, -97.134176), new LatLng(49.810293, -97.134226), new LatLng(49.810328, -97.134087));
        cd.setTop(new LatLng(49.810398, -97.134241));
        cd.setLeft(cb.getRight());
        cd.setBottom(cc.getTop());

        walkableZones.add(cd);

        WalkableZone ce = new WalkableZone(new LatLng(49.810459, -97.134375), new LatLng(49.810502, -97.134222), new LatLng(49.810383, -97.134303), new LatLng(49.810418, -97.134176));
        ce.setTop(e.getBottom());
        ce.setBottom(cd.getTop());

        walkableZones.add(ce);

        WalkableZone cf = new WalkableZone(new LatLng(49.810392, -97.134548), new LatLng(49.810408, -97.134480), new LatLng(49.810321, -97.134523), new LatLng(49.810338, -97.134432));
        cf.setBottom(bio_science_east_ent.getTop());
        cf.setTop(new LatLng(49.810400, -97.134519));

        walkableZones.add(cf);

        WalkableZone cg = new WalkableZone(new LatLng(49.810503, -97.134562), new LatLng(49.810541, -97.134435), new LatLng(49.810413, -97.134488), new LatLng(49.810449, -97.134351));
        cg.setLeft(new LatLng(49.810457, -97.134531));
        cg.setRight(e.getLeft());

        walkableZones.add(cg);

        WalkableZone ch = new WalkableZone(new LatLng(49.810473, -97.134620), new LatLng(49.810503, -97.134532), new LatLng(49.810372, -97.134553), new LatLng(49.810403, -97.134481));
        ch.setRight(cg.getLeft());
        ch.setBottom(cf.getTop());
        ch.setLeft(new LatLng(49.810405, -97.134584));

        walkableZones.add(ch);

        WalkableZone ci = new WalkableZone(new LatLng(49.810243, -97.135158), new LatLng(49.810466, -97.134604), new LatLng(49.810178, -97.135114), new LatLng(49.810375, -97.134537));
        ci.setRight(ch.getLeft());
        ci.setLeft(new LatLng(49.810212, -97.135132));
        walkableZones.add(ci);

        WalkableZone cj = new WalkableZone(new LatLng(49.810205, -97.135267), new LatLng(49.810246, -97.135138), new LatLng(49.810143, -97.135224), new LatLng(49.810177, -97.135113));
        cj.setLeft(new LatLng(49.810168, -97.135241));
        cj.setBottom(new LatLng(49.810172, -97.135130));
        cj.setRight(ci.getLeft());

        walkableZones.add(cj);

        WalkableZone ck = new WalkableZone(new LatLng(49.810164, -97.135162), new LatLng(49.810174, -97.135111), new LatLng(49.810091, -97.135100), new LatLng(49.810109, -97.135058));
        ck.setTop(cj.getBottom());
        ck.setBottom(bio_science_west_ent.getTop());
        ck.setLeft(new LatLng(49.810106, -97.135109));

        walkableZones.add(ck);

        WalkableZone cl = new WalkableZone(new LatLng(49.810077, -97.135302), new LatLng(49.810119, -97.135120), new LatLng(49.810046, -97.135286), new LatLng(49.810093, -97.135097));
        cl.setTop(new LatLng(49.810088, -97.135235));
        cl.setRight(ck.getLeft());
        walkableZones.add(cl);

        WalkableZone cm = new WalkableZone(new LatLng(49.810187, -97.135319), new LatLng(49.810208, -97.135246), new LatLng(49.810071, -97.135293), new LatLng(49.810099, -97.135186));
        cm.setLeft(new LatLng(49.810130, -97.135293));
        cm.setBottom(cl.getTop());
        cm.setRight(cj.getLeft());

        walkableZones.add(cm);

        WalkableZone cn = new WalkableZone(new LatLng(49.810163, -97.135463), new LatLng(49.810151, -97.135302), new LatLng(49.810106, -97.135503), new LatLng(49.810106, -97.135303));
        cn.setLeft(new LatLng(49.810144, -97.135461));
        cn.setRight(cm.getLeft());

        walkableZones.add(cn);

        WalkableZone co = new WalkableZone(new LatLng(49.810214, -97.135596), new LatLng(49.810210, -97.135412), new LatLng(49.810083, -97.135543), new LatLng(49.810105, -97.135416));
        co.setLeft(new LatLng(49.810138, -97.135569));
        co.setTop(new LatLng(49.810187, -97.135514));
        co.setRight(cn.getLeft());

        walkableZones.add(co);

        WalkableZone cp = new WalkableZone(new LatLng(49.809949, -97.136185), new LatLng(49.810180, -97.135563), new LatLng(49.809882, -97.136181), new LatLng(49.810104, -97.135502));
        cp.setLeft(robert_schultz_south_ent.getRight());
        cp.setRight(co.getLeft());

        walkableZones.add(cp);

        WalkableZone cq = new WalkableZone(new LatLng(49.810348, -97.135414), new LatLng(49.810382, -97.135268), new LatLng(49.810185, -97.135552), new LatLng(49.810188, -97.135464));
        cq.setLeft(co.getLeft());
        cq.setTop(new LatLng(49.810354, -97.135368));

        walkableZones.add(cq);

        WalkableZone cr = new WalkableZone(new LatLng(49.810534, -97.135555), new LatLng(49.810550, -97.135493), new LatLng(49.810346, -97.135430), new LatLng(49.810362, -97.135339));
        cr.setTop(new LatLng(49.810536, -97.135528));
        cr.setBottom(cq.getTop());

        walkableZones.add(cr);

        WalkableZone cs = new WalkableZone(new LatLng(49.810584, -97.135604), new LatLng(49.810603, -97.135530), new LatLng(49.810534, -97.135555), new LatLng(49.810550, -97.135493));
        cs.setBottom(cr.getTop());
        cs.setTop(new LatLng(49.810594, -97.135587));
        cs.setRight(new LatLng(49.810576, -97.135542));

        walkableZones.add(cs);

        WalkableZone ct = new WalkableZone(new LatLng(49.810603, -97.135530), new LatLng(49.810997, -97.134538), new LatLng(49.810550, -97.135493), new LatLng(49.810874, -97.134511));
        ct.setLeft(cs.getRight());
        ct.setRight(allen_armes_parker.getLeft());

        walkableZones.add(ct);

        WalkableZone cu = new WalkableZone(new LatLng(49.811224, -97.136121), new LatLng(49.811244, -97.136048), new LatLng(49.810584, -97.135604), new LatLng(49.810603, -97.135530));
        cu.setTop(new LatLng(49.811234, -97.136100));
        cu.setBottom(cs.getTop());

        walkableZones.add(cu);

        WalkableZone cv = new WalkableZone(new LatLng(49.811268, -97.136132), new LatLng(49.811278, -97.136091), new LatLng(49.811224, -97.136121), new LatLng(49.811244, -97.136048));
        cv.setTop(new LatLng(49.811280, -97.136113));
        cv.setRight(new LatLng(49.811264, -97.136079));
        cv.setBottom(cu.getTop());

        walkableZones.add(cv);

        WalkableZone cw = new WalkableZone(new LatLng(49.811403, -97.136255), new LatLng(49.811410, -97.136182), new LatLng(49.811268, -97.136132), new LatLng(49.811278, -97.136091));
        cw.setTop(new LatLng(49.811405, -97.136219));
        cw.setBottom(cv.getTop());

        walkableZones.add(cw);

        WalkableZone cx = new WalkableZone(new LatLng(49.811291, -97.136099), new LatLng(49.811356, -97.135864), new LatLng(49.811240, -97.136052), new LatLng(49.811315, -97.135853));
        cx.setLeft(cv.getRight());
        cx.setRight(new LatLng(49.811324, -97.135879));
        walkableZones.add(cx);

        WalkableZone cy = new WalkableZone(new LatLng(49.811365, -97.135884), new LatLng(49.811382, -97.135798), new LatLng(49.811310, -97.135874), new LatLng(49.811324, -97.135797));
        cy.setLeft(cx.getRight());
        cy.setRight(new LatLng(49.811337, -97.135806));
        cy.setTop(new LatLng(49.811375, -97.135818));

        walkableZones.add(cy);

        WalkableZone cz = new WalkableZone(new LatLng(49.811515, -97.135879), new LatLng(49.811519, -97.135823), new LatLng(49.811367, -97.135851), new LatLng(49.811378, -97.135795));
        cz.setTop(new LatLng(49.811495, -97.135846));
        cz.setBottom(cy.getTop());

        walkableZones.add(cz);

        WalkableZone da = new WalkableZone(new LatLng(49.811557, -97.135895), new LatLng(49.811567, -97.135781), new LatLng(49.811497, -97.135908), new LatLng(49.811496, -97.135791));
        da.setLeft(new LatLng(49.811528, -97.135899));
        da.setBottom(cz.getTop());
        da.setTop(new LatLng(49.811570, -97.135821));

        walkableZones.add(da);

        WalkableZone db = new WalkableZone(new LatLng(49.811610, -97.135916), new LatLng(49.811605, -97.135752), new LatLng(49.811545, -97.135894), new LatLng(49.811551, -97.135774));
        db.setBottom(da.getTop());
        db.setRight(new LatLng(49.811590, -97.135757));

        walkableZones.add(db);

        WalkableZone dc = new WalkableZone(new LatLng(49.811608, -97.135756), new LatLng(49.811601, -97.135646), new LatLng(49.811562, -97.135762), new LatLng(49.811551, -97.135662));
        dc.setLeft(db.getRight());
        dc.setTop(wallace.getBottom());
        dc.setRight(new LatLng(49.811574, -97.135638));

        walkableZones.add(dc);

        WalkableZone dd = new WalkableZone(new LatLng(49.811604, -97.135658), new LatLng(49.811565, -97.135287), new LatLng(49.811545, -97.135671), new LatLng(49.811512, -97.135310));
        dd.setLeft(dc.getRight());
        dd.setRight(new LatLng(49.811553, -97.135283));

        walkableZones.add(dd);

        WalkableZone de = new WalkableZone(new LatLng(49.811668, -97.135269), new LatLng(49.811683, -97.135030), new LatLng(49.811538, -97.135305), new LatLng(49.811625, -97.135036));
        de.setLeft(dd.getRight());
        de.setBottom(new LatLng(49.811576, -97.135173));

        walkableZones.add(de);

        WalkableZone df = new WalkableZone(new LatLng(49.811563, -97.135217), new LatLng(49.811597, -97.135134), new LatLng(49.811504, -97.135152), new LatLng(49.811529, -97.135080));
        df.setTop(de.getBottom());
        df.setBottom(new LatLng(49.811519, -97.135130));

        walkableZones.add(df);

        WalkableZone dg = new WalkableZone(new LatLng(49.811501, -97.135161), new LatLng(49.811529, -97.135078), new LatLng(49.811465, -97.135135), new LatLng(49.811490, -97.135052));
        dg.setTop(df.getBottom());
        dg.setLeft(new LatLng(49.811491, -97.135151));
        dg.setRight(new LatLng(49.811516, -97.135073));

        walkableZones.add(dg);

        WalkableZone dh = new WalkableZone(new LatLng(49.811528, -97.135089), new LatLng(49.811613, -97.134800), new LatLng(49.811489, -97.135061), new LatLng(49.811593, -97.134756));
        dh.setLeft(dg.getRight());
        dh.setRight(parker_ent.getLeft());

        walkableZones.add(dh);

        WalkableZone di = new WalkableZone(new LatLng(49.811344, -97.135811), new LatLng(49.811501, -97.135153), new LatLng(49.811323, -97.135811), new LatLng(49.811473, -97.135129));
        di.setLeft(cy.getRight());
        di.setRight(dg.getLeft());

        walkableZones.add(di);

        WalkableZone dj = new WalkableZone(new LatLng(49.811441, -97.136322), new LatLng(49.811449, -97.136226), new LatLng(49.811401, -97.136282), new LatLng(49.811418, -97.136178));
        dj.setLeft(new LatLng(49.811415, -97.136281));
        dj.setBottom(cw.getTop());
        dj.setRight(new LatLng(49.811431, -97.136206));

        walkableZones.add(dj);

        WalkableZone dk = new WalkableZone(new LatLng(49.811444, -97.136259), new LatLng(49.811542, -97.135890), new LatLng(49.811421, -97.136195), new LatLng(49.811502, -97.135888));
        dk.setLeft(dj.getRight());
        dk.setTop(db.getBottom());

        walkableZones.add(dk);

        WalkableZone dl = new WalkableZone(new LatLng(49.811263, -97.136898), new LatLng(49.811439, -97.136274), new LatLng(49.811237, -97.136873), new LatLng(49.811403, -97.136245));
        dl.setRight(dj.getLeft());
        dl.setLeft(new LatLng(49.811263, -97.136838));
        walkableZones.add(dl);

        WalkableZone dm = new WalkableZone(new LatLng(49.811257, -97.136899), new LatLng(49.811274, -97.136837), new LatLng(49.811230, -97.136880), new LatLng(49.811252, -97.136825));
        dm.setLeft(new LatLng(49.811251, -97.136882));
        dm.setRight(dl.getLeft());
        dm.setBottom(st_johns_east_ent.getTop());

        walkableZones.add(dm);

        WalkableZone dn = new WalkableZone(new LatLng(49.811064, -97.137590), new LatLng(49.811266, -97.136890), new LatLng(49.811002, -97.137561), new LatLng(49.811232, -97.136873));
        dn.setLeft(new LatLng(49.811029, -97.137547));
        dn.setRight(dm.getLeft());

        walkableZones.add(dn);

        WalkableZone dp = new WalkableZone(new LatLng(49.811019, -97.137728), new LatLng(49.811079, -97.137574), new LatLng(49.810952, -97.137648), new LatLng(49.810999, -97.137528));
        dp.setRight(dn.getLeft());
        dp.setBottom(new LatLng(49.810986, -97.137589));
        dp.setLeft(new LatLng(49.810978, -97.137666));
        dp.setTop(new LatLng(49.811039, -97.137662));

        walkableZones.add(dp);

        WalkableZone dq = new WalkableZone(new LatLng(49.810926, -97.137890), new LatLng(49.810994, -97.137674), new LatLng(49.810900, -97.137852), new LatLng(49.810958, -97.137633));
        dq.setRight(dp.getLeft());
        dq.setLeft(new LatLng(49.810913, -97.137865));

        walkableZones.add(dq);

        WalkableZone dr = new WalkableZone(new LatLng(49.810897, -97.137966), new LatLng(49.810928, -97.137876), new LatLng(49.810867, -97.137940), new LatLng(49.810893, -97.137844));
        dr.setLeft(new LatLng(49.810886, -97.137936));
        dr.setRight(dq.getLeft());
        dr.setBottom(new LatLng(49.810892, -97.137881));

        walkableZones.add(dr);

        WalkableZone ds = new WalkableZone(new LatLng(49.810754, -97.138382), new LatLng(49.810910, -97.137928), new LatLng(49.810725, -97.138356), new LatLng(49.810867, -97.137929));
        ds.setLeft(new LatLng(49.810738, -97.138363));
        ds.setRight(dr.getLeft());

        walkableZones.add(ds);

        WalkableZone dt = new WalkableZone(new LatLng(49.810731, -97.138451), new LatLng(49.810754, -97.138382), new LatLng(49.810707, -97.138413), new LatLng(49.810725, -97.138356));
        dt.setLeft(new LatLng(49.810719, -97.138435));
        dr.setRight(ds.getLeft());
        dr.setBottom(new LatLng(49.810719, -97.138375));

        walkableZones.add(dt);

        WalkableZone du = new WalkableZone(new LatLng(49.810715, -97.138412), new LatLng(49.810730, -97.138342), new LatLng(49.810576, -97.138287), new LatLng(49.810585, -97.138244));
        du.setTop(dt.getBottom());
        du.setBottom(st_pauls_north_ent.getTop());

        walkableZones.add(du);

        WalkableZone dv = new WalkableZone(new LatLng(49.810960, -97.137676), new LatLng(49.811006, -97.137527), new LatLng(49.810841, -97.137532), new LatLng(49.810853, -97.137462));
        dv.setTop(dp.getBottom());
        dv.setBottom(new LatLng(49.810839, -97.137492));

        walkableZones.add(dv);

        WalkableZone dw = new WalkableZone(new LatLng(49.810841, -97.137532), new LatLng(49.810853, -97.137462), new LatLng(49.810767, -97.137480), new LatLng(49.810787, -97.137407));
        dw.setBottom(new LatLng(49.810776, -97.137458));
        dw.setTop(dv.getBottom());
        dw.setLeft(new LatLng(49.810820, -97.137518));

        walkableZones.add(dw);

        WalkableZone dx = new WalkableZone(new LatLng(49.810903, -97.137857), new LatLng(49.810843, -97.137514), new LatLng(49.810865, -97.137948), new LatLng(49.810806, -97.137537));
        dx.setBottom(dw.getLeft());
        dx.setTop(dr.getBottom());

        walkableZones.add(dx);

        WalkableZone dy = new WalkableZone(new LatLng(49.810786, -97.137523), new LatLng(49.810853, -97.137223), new LatLng(49.810591, -97.137406), new LatLng(49.810668, -97.137168));
        dy.setBottom(new LatLng(49.810609, -97.137357));
        dy.setTop(dw.getBottom());
        dy.setRight(st_johns_north_ent.getLeft());

        walkableZones.add(dy);

        WalkableZone dz = new WalkableZone(new LatLng(49.810631, -97.137424), new LatLng(49.810641, -97.137316), new LatLng(49.810531, -97.137369), new LatLng(49.810541, -97.137314));
        dz.setTop(dy.getBottom());
        dz.setRight(new LatLng(49.810563, -97.137314));
        dz.setBottom(new LatLng(49.810540, -97.137336));

        walkableZones.add(dz);

        WalkableZone ea = new WalkableZone(new LatLng(49.810590, -97.137324), new LatLng(49.810533, -97.137166), new LatLng(49.810546, -97.137325), new LatLng(49.810500, -97.137198));
        ea.setLeft(dz.getRight());
        ea.setBottom(st_johns_west_ent.getTop());

        walkableZones.add(ea);

        WalkableZone eb = new WalkableZone(new LatLng(49.810547, -97.137376), new LatLng(49.810545, -97.137314), new LatLng(49.810437, -97.137350), new LatLng(49.810459, -97.137286));
        eb.setTop(dz.getBottom());
        eb.setBottom(new LatLng(49.810456, -97.137313));

        walkableZones.add(eb);

        WalkableZone ec = new WalkableZone(new LatLng(49.810437, -97.137350), new LatLng(49.810459, -97.137286), new LatLng(49.810405, -97.137344), new LatLng(49.810413, -97.137265));
        ec.setTop(eb.getBottom());
        ec.setRight(st_johns_west_ent.getLeft());
        ec.setBottom(new LatLng(49.810411, -97.137299));

        walkableZones.add(ec);

        WalkableZone ed = new WalkableZone(new LatLng(49.810400, -97.137336), new LatLng(49.810408, -97.137261), new LatLng(49.810105, -97.137219), new LatLng(49.810124, -97.137139));
        ed.setTop(ec.getBottom());
        ed.setBottom(new LatLng(49.810117, -97.137177));

        walkableZones.add(ed);

        WalkableZone ee = new WalkableZone(new LatLng(49.810130, -97.137229), new LatLng(49.810148, -97.137136), new LatLng(49.810040, -97.137156), new LatLng(49.810076, -97.137058));
        ee.setBottom(ed.getBottom());
        ee.setRight(new LatLng(49.810075, -97.137105));

        walkableZones.add(ee);

        WalkableZone ef = new WalkableZone(new LatLng(49.810109, -97.137125), new LatLng(49.810111, -97.136973), new LatLng(49.810043, -97.137115), new LatLng(49.810049, -97.136941));
        ef.setRight(robert_schultz_west_ent.getLeft());
        ef.setTop(ee.getRight());
        ef.setLeft(st_pauls_east_ent.getRight());
        ef.setBottom(new LatLng(49.810038, -97.136994));

        walkableZones.add(ef);

        WalkableZone eg = new WalkableZone(new LatLng(49.810476, -97.137168), new LatLng(49.810506, -97.137115), new LatLng(49.810206, -97.136852), new LatLng(49.810230, -97.136766));
        eg.setTop(st_johns_west_ent.getBottom());
        eg.setBottom(robert_schultz_west_ent.getTop());

        walkableZones.add(eg);

        WalkableZone eh = new WalkableZone(new LatLng(49.809901, -97.137256), new LatLng(49.809926, -97.137178), new LatLng(49.809753, -97.136936), new LatLng(49.809772, -97.136879));
        eh.setTop(st_pauls_east_ent.getBottom());
        eh.setBottom(new LatLng(49.809767, -97.136912));

        walkableZones.add(eh);

        WalkableZone ei = new WalkableZone(new LatLng(49.810055, -97.136989), new LatLng(49.810077, -97.136892), new LatLng(49.809771, -97.136787), new LatLng(49.809789, -97.136697));
        ei.setTop(ef.getBottom());
        ei.setBottom(new LatLng(49.809775, -97.136730));

        walkableZones.add(ei);

        WalkableZone ej = new WalkableZone(new LatLng(49.809770, -97.136827), new LatLng(49.809793, -97.136689), new LatLng(49.809683, -97.136752), new LatLng(49.809719, -97.136642));
        ej.setTop(ei.getBottom());
        ej.setRight(new LatLng(49.809748, -97.136676));
        ej.setBottom(new LatLng(49.809682, -97.136770));
        ej.setLeft(new LatLng(49.809719, -97.136800));
        walkableZones.add(ej);

        WalkableZone ek = new WalkableZone(new LatLng(49.809755, -97.136946), new LatLng(49.809770, -97.136827), new LatLng(49.809647, -97.136860), new LatLng(49.809683, -97.136752));
        ek.setLeft(new LatLng(49.809685, -97.136878));
        ek.setRight(ej.getLeft());
        ek.setBottom(ej.getBottom());
        ek.setTop(eh.getBottom());

        walkableZones.add(ek);

        WalkableZone el = new WalkableZone(new LatLng(49.809767, -97.136700), new LatLng(49.809853, -97.136450), new LatLng(49.809716, -97.136659), new LatLng(49.809808, -97.136399));
        el.setLeft(ej.getRight());
        el.setRight(robert_schultz_south_ent.getLeft());

        walkableZones.add(el);

        WalkableZone em = new WalkableZone(new LatLng(49.809659, -97.136870), new LatLng(49.809701, -97.136701), new LatLng(49.809220, -97.136541), new LatLng(49.809276, -97.136389));
        em.setTop(ek.getBottom());
        em.setBottom(new LatLng(49.809256, -97.136456));

        walkableZones.add(em);

        WalkableZone en = new WalkableZone(new LatLng(49.809263, -97.136548), new LatLng(49.809270, -97.136349), new LatLng(49.809139, -97.136417), new LatLng(49.809181, -97.136256));
        en.setTop(em.getBottom());
        en.setBottom(new LatLng(49.809164, -97.136369));
        en.setLeft(education_north_ent.getRight());

        walkableZones.add(en);

        WalkableZone eo = new WalkableZone(new LatLng(49.809073, -97.136399), new LatLng(49.809168, -97.136307), new LatLng(49.808988, -97.136348), new LatLng(49.809017, -97.136179));
        eo.setTop(en.getBottom());
        eo.setBottom(new LatLng(49.809014, -97.136245));

        walkableZones.add(eo);

        WalkableZone ep = new WalkableZone(new LatLng(49.808998, -97.136361), new LatLng(49.809080, -97.136045), new LatLng(49.808936, -97.136315), new LatLng(49.809014, -97.135998));
        ep.setTop(eo.getBottom());
        ep.setBottom(new LatLng(49.808970, -97.136200));

        walkableZones.add(ep);

        WalkableZone eq = new WalkableZone(new LatLng(49.808949, -97.136297), new LatLng(49.808992, -97.136099), new LatLng(49.808809, -97.136162), new LatLng(49.808854, -97.135986));
        eq.setTop(ep.getBottom());
        eq.setBottom(new LatLng(49.808839, -97.136086));

        walkableZones.add(eq);

        WalkableZone er = new WalkableZone(new LatLng(49.808820, -97.136239), new LatLng(49.808888, -97.135912), new LatLng(49.808710, -97.136105), new LatLng(49.808721, -97.135869));
        er.setTop(eq.getBottom());
        er.setRight(helen_glass.getLeft());
        er.setBottom(new LatLng(49.808727, -97.136011));

        walkableZones.add(er);

        WalkableZone es = new WalkableZone(new LatLng(49.808733, -97.132954), new LatLng(49.808759, -97.132863), new LatLng(49.808554, -97.132815), new LatLng(49.808570, -97.132710));
        es.setTop(o.getBottom());
        es.setBottom(m.getTop());

        walkableZones.add(es);

        WalkableZone et = new WalkableZone(new LatLng(49.808514, -97.132804), new LatLng(49.808541, -97.132698), new LatLng(49.808243, -97.132637), new LatLng(49.808281, -97.132474));
        et.setTop(m.getBottom());
        et.setBottom(new LatLng(49.808268, -97.132535));

        walkableZones.add(et);

        WalkableZone eu = new WalkableZone(new LatLng(49.808241, -97.132647), new LatLng(49.808285, -97.132479), new LatLng(49.808120, -97.132552), new LatLng(49.808176, -97.132407));
        eu.setTop(et.getBottom());
        eu.setLeft(new LatLng(49.808183, -97.132588));
        eu.setRight(new LatLng(49.808188, -97.132429));
        eu.setBottom(new LatLng(49.808168, -97.132441));

        walkableZones.add(eu);

        WalkableZone ev = new WalkableZone(new LatLng(49.808213, -97.132736), new LatLng(49.808244, -97.132628), new LatLng(49.808089, -97.132639), new LatLng(49.808120, -97.132539));
        ev.setLeft(new LatLng(49.808158, -97.132679));
        ev.setRight(eu.getLeft());

        walkableZones.add(ev);

        WalkableZone ew = new WalkableZone(new LatLng(49.807949, -97.133715), new LatLng(49.808242, -97.132778), new LatLng(49.807794, -97.133697), new LatLng(49.808134, -97.132658));
        ew.setRight(ev.getLeft());
        ew.setLeft(n.getRight());

        walkableZones.add(ew);

        WalkableZone ex = new WalkableZone(new LatLng(49.808162, -97.132460), new LatLng(49.808178, -97.132416), new LatLng(49.808095, -97.132411), new LatLng(49.808109, -97.132361));
        ex.setTop(eu.getBottom());
        ex.setBottom(new LatLng(49.808102, -97.132384));

        walkableZones.add(ex);

        WalkableZone ey = new WalkableZone(new LatLng(49.808094, -97.132413), new LatLng(49.808119, -97.132347), new LatLng(49.808055, -97.132383), new LatLng(49.808076, -97.132325));
        ey.setTop(ex.getBottom());
        ey.setLeft(new LatLng(49.808078, -97.132401));
        ey.setRight(new LatLng(49.808093, -97.132346));

        walkableZones.add(ey);

        WalkableZone ez = new WalkableZone(new LatLng(49.807980, -97.132753), new LatLng(49.808099, -97.132397), new LatLng(49.807939, -97.132723), new LatLng(49.808059, -97.132374));
        ez.setRight(ey.getLeft());
        ez.setLeft(new LatLng(49.807960, -97.132734));

        walkableZones.add(ez);

        WalkableZone fa = new WalkableZone(new LatLng(49.807959, -97.132809), new LatLng(49.807980, -97.132749), new LatLng(49.807897, -97.132784), new LatLng(49.807920, -97.132712));
        fa.setRight(ez.getLeft());
        fa.setBottom(new LatLng(49.807910, -97.132761));
        fa.setLeft(new LatLng(49.807914, -97.132790));

        walkableZones.add(fa);

        WalkableZone fb = new WalkableZone(new LatLng(49.807860, -97.132979), new LatLng(49.807926, -97.132793), new LatLng(49.807850, -97.132946), new LatLng(49.807902, -97.132784));
        fb.setRight(fa.getLeft());
        fb.setLeft(new LatLng(49.807861, -97.132945));

        walkableZones.add(fb);

        WalkableZone fc = new WalkableZone(new LatLng(49.807856, -97.133010), new LatLng(49.807857, -97.132926), new LatLng(49.807803, -97.133002), new LatLng(49.807826, -97.132853));
        fc.setBottom(new LatLng(49.807818, -97.132910));
        fc.setLeft(new LatLng(49.807834, -97.133010));
        fc.setRight(fb.getLeft());

        walkableZones.add(fc);

        WalkableZone fd = new WalkableZone(new LatLng(49.807744, -97.133359), new LatLng(49.807855, -97.133028), new LatLng(49.807705, -97.133325), new LatLng(49.807801, -97.133033));
        fd.setLeft(j.getRight());
        fd.setRight(fc.getLeft());

        walkableZones.add(fd);

        WalkableZone fe = new WalkableZone(new LatLng(49.807606, -97.133793), new LatLng(49.807731, -97.133404), new LatLng(49.807560, -97.133803), new LatLng(49.807690, -97.133382));
        fe.setRight(j.getLeft());
        fe.setLeft(a.getRight());

        walkableZones.add(fe);

        WalkableZone ff = new WalkableZone(new LatLng(49.807494, -97.134172), new LatLng(49.807583, -97.133896), new LatLng(49.807461, -97.134138), new LatLng(49.807530, -97.133882));
        ff.setLeft(b.getRight());
        ff.setRight(a.getLeft());

        walkableZones.add(ff);

        WalkableZone fg = new WalkableZone(new LatLng(49.807322, -97.134665), new LatLng(49.807478, -97.134213), new LatLng(49.807300, -97.134664), new LatLng(49.807438, -97.134230));
        fg.setRight(b.getLeft());
        fg.setLeft(new LatLng(49.807310, -97.134668));

        walkableZones.add(fg);

        WalkableZone fh = new WalkableZone(new LatLng(49.807300, -97.134732), new LatLng(49.807331, -97.134650), new LatLng(49.807270, -97.134706), new LatLng(49.807291, -97.134671));
        fh.setRight(fg.getLeft());
        fh.setLeft(new LatLng(49.807287, -97.134714));
        fh.setTop(new LatLng(49.807312, -97.134702));

        walkableZones.add(fh);

        WalkableZone fi = new WalkableZone(new LatLng(49.807188, -97.135089), new LatLng(49.807300, -97.134708), new LatLng(49.807163, -97.135044), new LatLng(49.807271, -97.134703));
        fi.setLeft(c.getRight());
        fi.setRight(fh.getLeft());

        walkableZones.add(fi);

        WalkableZone fj = new WalkableZone(new LatLng(49.807034, -97.135514), new LatLng(49.807150, -97.135169), new LatLng(49.807012, -97.135482), new LatLng(49.807129, -97.135137));
        fj.setLeft(d.getRight());
        fj.setRight(c.getLeft());

        walkableZones.add(fj);

        WalkableZone fk = new WalkableZone(new LatLng(49.806936, -97.135820), new LatLng(49.807001, -97.135611), new LatLng(49.806908, -97.135793), new LatLng(49.806976, -97.135593));
        fk.setRight(d.getLeft());
        fk.setLeft(new LatLng(49.806915, -97.135807));

        walkableZones.add(fk);

        WalkableZone fl = new WalkableZone(new LatLng(49.806883, -97.135999), new LatLng(49.806936, -97.135820), new LatLng(49.806838, -97.135959), new LatLng(49.806908, -97.135793));
        fl.setLeft(new LatLng(49.806842, -97.135970));
        fl.setRight(fk.getLeft());
        fl.setBottom(new LatLng(49.806843, -97.135923));

        walkableZones.add(fl);

        WalkableZone fm = new WalkableZone(new LatLng(49.806737, -97.136385), new LatLng(49.806883, -97.135999), new LatLng(49.806713, -97.136357), new LatLng(49.806838, -97.135959));
        fm.setLeft(new LatLng(49.806720, -97.136354));
        fm.setRight(fl.getLeft());

        walkableZones.add(fm);

        WalkableZone fn = new WalkableZone(new LatLng(49.806711, -97.136468), new LatLng(49.806883, -97.135999), new LatLng(49.806673, -97.136461), new LatLng(49.806713, -97.136357));
        fn.setLeft(new LatLng(49.806681, -97.136484));
        fn.setRight(fm.getLeft());
        fn.setBottom(new LatLng(49.806703, -97.136381));

        walkableZones.add(fn);

        WalkableZone fo = new WalkableZone(new LatLng(49.806311, -97.137613), new LatLng(49.806711, -97.136468), new LatLng(49.806295, -97.137586), new LatLng(49.806673, -97.136461));
        fo.setLeft(f.getRight());
        fo.setRight(fn.getLeft());

        walkableZones.add(fo);

        WalkableZone fp = new WalkableZone(new LatLng(49.806618, -97.136098), new LatLng(49.806649, -97.135965), new LatLng(49.806597, -97.136071), new LatLng(49.806609, -97.135974));
        fp.setLeft(new LatLng(49.806625, -97.136082));
        fp.setRight(new LatLng(49.806638, -97.135989));
        fp.setBottom(agriculture_south_west_ent.getTop());

        walkableZones.add(fp);

        WalkableZone fq = new WalkableZone(new LatLng(49.806833, -97.135955), new LatLng(49.806885, -97.135835), new LatLng(49.806646, -97.136026), new LatLng(49.806641, -97.135965));
        fq.setLeft(fp.getRight());
        fq.setTop(fl.getBottom());

        walkableZones.add(fq);

        WalkableZone fr = new WalkableZone(new LatLng(49.806679, -97.136420), new LatLng(49.806709, -97.136345), new LatLng(49.806597, -97.136067), new LatLng(49.806636, -97.136033));
        fr.setRight(fp.getLeft());
        fr.setTop(fn.getBottom());

        walkableZones.add(fr);

        WalkableZone fs = new WalkableZone(new LatLng(49.806137, -97.138107), new LatLng(49.806276, -97.137752), new LatLng(49.806110, -97.138086), new LatLng(49.806219, -97.137722));
        fs.setLeft(g.getRight());
        fs.setRight(f.getLeft());

        walkableZones.add(fs);

        WalkableZone ft = new WalkableZone(new LatLng(49.806009, -97.138555), new LatLng(49.806094, -97.138227), new LatLng(49.805965, -97.138515), new LatLng(49.806066, -97.138202));
        ft.setLeft(new LatLng(49.805977, -97.138521));
        ft.setRight(g.getLeft());

        walkableZones.add(ft);

        WalkableZone fu = new WalkableZone(new LatLng(49.805973, -97.138593), new LatLng(49.805994, -97.138533), new LatLng(49.805936, -97.138563), new LatLng(49.805957, -97.138514));
        fu.setLeft(new LatLng(49.805957, -97.138581));
        fu.setRight(ft.getLeft());
        fu.setBottom(new LatLng(49.805948, -97.138532));
        fu.setTop(new LatLng(49.805979, -97.138568));

        walkableZones.add(fu);

        WalkableZone fv = new WalkableZone(new LatLng(49.806051, -97.138680), new LatLng(49.806067, -97.138603), new LatLng(49.805970, -97.138591), new LatLng(49.805985, -97.138539));
        fv.setTop(new LatLng(49.806060, -97.138641));
        fv.setBottom(fu.getTop());

        walkableZones.add(fv);

        WalkableZone fw = new WalkableZone(new LatLng(49.806107, -97.138730), new LatLng(49.806122, -97.138611), new LatLng(49.806051, -97.138680), new LatLng(49.806067, -97.138603));
        fw.setLeft(new LatLng(49.806062, -97.138692));
        fw.setRight(new LatLng(49.806090, -97.138642));
        fw.setBottom(fv.getTop());

        walkableZones.add(fw);

        WalkableZone fx = new WalkableZone(new LatLng(49.806171, -97.138640), new LatLng(49.806167, -97.138577), new LatLng(49.806099, -97.138675), new LatLng(49.806087, -97.138632));
        fx.setBottom(fw.getRight());
        fx.setTop(new LatLng(49.806166, -97.138620));

        walkableZones.add(fx);

        WalkableZone fy = new WalkableZone(new LatLng(49.806236, -97.138625), new LatLng(49.806219, -97.138477), new LatLng(49.806152, -97.138632), new LatLng(49.806143, -97.138476));
        fy.setBottom(fx.getTop());
        fy.setRight(new LatLng(49.806199, -97.138518));
        fy.setTop(new LatLng(49.806201, -97.138583));

        walkableZones.add(fy);

        WalkableZone fz = new WalkableZone(new LatLng(49.806720, -97.139232), new LatLng(49.806741, -97.139156), new LatLng(49.806184, -97.138699), new LatLng(49.806209, -97.138547));
        fz.setBottom(fy.getTop());
        fz.setTop(new LatLng(49.806725, -97.139191));

        walkableZones.add(fz);

        WalkableZone ga = new WalkableZone(new LatLng(49.806750, -97.139263), new LatLng(49.806760, -97.139208), new LatLng(49.806718, -97.139237), new LatLng(49.806732, -97.139188));
        ga.setTop(new LatLng(49.806746, -97.139231));
        ga.setBottom(fz.getTop());
        ga.setRight(new LatLng(49.806747, -97.139193));

        walkableZones.add(ga);

        WalkableZone gb = new WalkableZone(new LatLng(49.806980, -97.139393), new LatLng(49.806992, -97.139341), new LatLng(49.806729, -97.139185), new LatLng(49.806742, -97.139161));
        gb.setBottom(ga.getRight());
        gb.setTop(p.getBottom());

        walkableZones.add(gb);

        WalkableZone gc = new WalkableZone(new LatLng(49.807060, -97.139511), new LatLng(49.807071, -97.139469), new LatLng(49.806734, -97.139245), new LatLng(49.806747, -97.139207));
        gc.setTop(new LatLng(49.807064, -97.139489));
        gc.setBottom(ga.getTop());

        walkableZones.add(gc);

        WalkableZone gd = new WalkableZone(new LatLng(49.807065, -97.139469), new LatLng(49.807094, -97.139403), new LatLng(49.807032, -97.139431), new LatLng(49.807056, -97.139377));
        gd.setTop(new LatLng(49.807082, -97.139433));
        gd.setBottom(p.getTop());

        walkableZones.add(gd);

        WalkableZone ge = new WalkableZone(new LatLng(49.807112, -97.139493), new LatLng(49.807140, -97.139434), new LatLng(49.807068, -97.139470), new LatLng(49.807086, -97.139407));
        ge.setBottom(gd.getTop());
        ge.setLeft(new LatLng(49.807098, -97.139486));

        walkableZones.add(ge);

        WalkableZone gf = new WalkableZone(new LatLng(49.807138, -97.139577), new LatLng(49.807161, -97.139518), new LatLng(49.807059, -97.139506), new LatLng(49.807068, -97.139470));
        gf.setBottom(gc.getTop());
        gf.setRight(ge.getLeft());
        gf.setTop(new LatLng(49.807149, -97.139542));

        addEntrance(resources.getString(R.string.nb_uni_chancellor), gf.getTop());
        walkableZones.add(gf);

        WalkableZone gg = new WalkableZone(new LatLng(49.807174, -97.139611), new LatLng(49.807203, -97.139496), new LatLng(49.807139, -97.139574), new LatLng(49.807167, -97.139477));
        gg.setBottom(gf.getTop());
        gg.setLeft(new LatLng(49.807159, -97.139590));
        gg.setTop(new LatLng(49.807184, -97.139551));

        walkableZones.add(gg);

        WalkableZone gh = new WalkableZone(new LatLng(49.807276, -97.139694), new LatLng(49.807303, -97.139576), new LatLng(49.807176, -97.139602), new LatLng(49.807204, -97.139482));
        gh.setTop(new LatLng(49.807291, -97.139634));
        gh.setRight(new LatLng(49.807244, -97.139548));
        gh.setBottom(gg.getTop());

        walkableZones.add(gh);

        WalkableZone gi = new WalkableZone(new LatLng(49.807302, -97.139589), new LatLng(49.807875, -97.137969), new LatLng(49.807194, -97.139507), new LatLng(49.807753, -97.137892));
        gi.setLeft(gh.getRight());
        gi.setRight(new LatLng(49.807859, -97.137962));

        walkableZones.add(gi);

        WalkableZone gj = new WalkableZone(new LatLng(49.807758, -97.138015), new LatLng(49.807811, -97.137807), new LatLng(49.807609, -97.137891), new LatLng(49.807668, -97.137689));
        gj.setBottom(ext_education_east_ent.getTop());
        gj.setRight(new LatLng(49.807711, -97.137728));
        gj.setTop(new LatLng(49.807787, -97.137875));

        walkableZones.add(gj);

        WalkableZone gk = new WalkableZone(new LatLng(49.807747, -97.137761), new LatLng(49.807768, -97.137600), new LatLng(49.807668, -97.137702), new LatLng(49.807707, -97.137553));
        gk.setLeft(gj.getRight());
        gk.setBottom(new LatLng(49.807693, -97.137604));
        gk.setTop(new LatLng(49.807760, -97.137688));

        walkableZones.add(gk);

        WalkableZone gl = new WalkableZone(new LatLng(49.807651, -97.137755), new LatLng(49.807709, -97.137559), new LatLng(49.807533, -97.137673), new LatLng(49.807602, -97.137463));
        gl.setTop(gk.getBottom());
        gl.setLeft(ext_education_west_ent.getRight());
        gl.setRight(new LatLng(49.807613, -97.137461));
        gl.setBottom(new LatLng(49.807596, -97.137463));

        walkableZones.add(gl);

        WalkableZone gm = new WalkableZone(new LatLng(49.807983, -97.138084), new LatLng(49.808025, -97.137915), new LatLng(49.807746, -97.137889), new LatLng(49.807775, -97.137772));
        gm.setLeft(gi.getRight());
        gm.setBottom(gj.getTop());
        gm.setRight(new LatLng(49.807887, -97.137852));

        walkableZones.add(gm);

        WalkableZone gn = new WalkableZone(new LatLng(49.808023, -97.137913), new LatLng(49.808073, -97.137765), new LatLng(49.807746, -97.137763), new LatLng(49.807773, -97.137591));
        gn.setLeft(gm.getRight());
        gn.setRight(new LatLng(49.807918, -97.137662));
        gn.setTop(new LatLng(49.808056, -97.137815));
        gn.setBottom(gk.getTop());

        walkableZones.add(gn);

        WalkableZone go = new WalkableZone(new LatLng(49.807978, -97.137690), new LatLng(49.808059, -97.137344), new LatLng(49.807773, -97.137582), new LatLng(49.807872, -97.137258));
        gn.setLeft(gn.getRight());
        gn.setRight(new LatLng(49.807987, -97.137359));

        walkableZones.add(go);

        WalkableZone gp = new WalkableZone(new LatLng(49.807637, -97.137483), new LatLng(49.807676, -97.137159), new LatLng(49.807600, -97.137451), new LatLng(49.807631, -97.137173));
        gp.setLeft(gl.getRight());
        gp.setRight(new LatLng(49.807650, -97.137158));

        walkableZones.add(gp);

        WalkableZone gq = new WalkableZone(new LatLng(49.807672, -97.137194), new LatLng(49.807714, -97.137053), new LatLng(49.807573, -97.137138), new LatLng(49.807614, -97.137007));
        gq.setLeft(gp.getRight());
        gq.setRight(archi2_west_ent.getLeft());
        gq.setBottom(new LatLng(49.807599, -97.137059));

        walkableZones.add(gq);

        WalkableZone gr = new WalkableZone(new LatLng(49.807862, -97.137037), new LatLng(49.807883, -97.136920), new LatLng(49.807802, -97.136984), new LatLng(49.807827, -97.136872));
        gr.setBottom(archi2_west_ent.getTop());
        gr.setLeft(new LatLng(49.807843, -97.136996));
        gr.setRight(new LatLng(49.807865, -97.136919));

        walkableZones.add(gr);

        WalkableZone gs = new WalkableZone(new LatLng(49.807874, -97.137293), new LatLng(49.807897, -97.137200), new LatLng(49.807827, -97.137028), new LatLng(49.807860, -97.136972));
        gs.setTop(new LatLng(49.807894, -97.137231));
        gs.setRight(gr.getLeft());

        walkableZones.add(gs);

        WalkableZone gt = new WalkableZone(new LatLng(49.808045, -97.137401), new LatLng(49.808064, -97.137301), new LatLng(49.807876, -97.137261), new LatLng(49.807899, -97.137195));
        gt.setLeft(go.getRight());
        gt.setBottom(gs.getTop());
        gt.setRight(new LatLng(49.808016, -97.137268));

        walkableZones.add(gt);

        WalkableZone gu = new WalkableZone(new LatLng(49.808072, -97.137347), new LatLng(49.808210, -97.136892), new LatLng(49.807905, -97.137207), new LatLng(49.808037, -97.136800));
        gu.setLeft(gt.getRight());
        gu.setRight(new LatLng(49.808161, -97.136890));

        walkableZones.add(gu);

        WalkableZone gv = new WalkableZone(new LatLng(49.808217, -97.136855), new LatLng(49.808253, -97.136735), new LatLng(49.808063, -97.136742), new LatLng(49.808100, -97.136687));
        gv.setLeft(new LatLng(49.808180, -97.136807));
        gv.setTop(new LatLng(49.808231, -97.136804));
        gv.setRight(new LatLng(49.808226, -97.136697));

        walkableZones.add(gv);

        WalkableZone gw = new WalkableZone(new LatLng(49.808253, -97.136735), new LatLng(49.808335, -97.136547), new LatLng(49.808100, -97.136687), new LatLng(49.808152, -97.136439));
        gw.setLeft(gv.getRight());
        gw.setRight(new LatLng(49.808284, -97.136539));
        gw.setBottom(new LatLng(49.808154, -97.136511));

        walkableZones.add(gw);

        WalkableZone gx = new WalkableZone(new LatLng(49.808332, -97.136579), new LatLng(49.808407, -97.136315), new LatLng(49.808240, -97.136504), new LatLng(49.808315, -97.136249));
        gx.setLeft(gw.getRight());
        gx.setRight(new LatLng(49.808366, -97.136293));

        walkableZones.add(gx);

        WalkableZone gy = new WalkableZone(new LatLng(49.808198, -97.136915), new LatLng(49.808213, -97.136832), new LatLng(49.808032, -97.136813), new LatLng(49.808060, -97.136734));
        gy.setBottom(new LatLng(49.808045, -97.136778));
        gy.setLeft(gu.getRight());
        gy.setRight(gv.getLeft());

        walkableZones.add(gy);

        WalkableZone gz = new WalkableZone(new LatLng(49.808034, -97.136840), new LatLng(49.808056, -97.136738), new LatLng(49.807860, -97.136966), new LatLng(49.807855, -97.136899));
        gz.setTop(gy.getBottom());
        gz.setLeft(gr.getRight());

        walkableZones.add(gz);

        WalkableZone ha = new WalkableZone(new LatLng(49.808373, -97.137459), new LatLng(49.808439, -97.137270), new LatLng(49.808218, -97.136837), new LatLng(49.808260, -97.136711));
        ha.setTop(education_west_ent.getBottom());
        ha.setBottom(gv.getTop());

        walkableZones.add(ha);

        WalkableZone hb = new WalkableZone(new LatLng(49.808131, -97.136533), new LatLng(49.808171, -97.136455), new LatLng(49.808048, -97.136324), new LatLng(49.808074, -97.136225));
        hb.setTop(gw.getBottom());
        hb.setBottom(archi2_north_ent.getTop());

        walkableZones.add(hb);

        WalkableZone hc = new WalkableZone(new LatLng(49.807582, -97.137139), new LatLng(49.807639, -97.136943), new LatLng(49.807506, -97.137096), new LatLng(49.807563, -97.136908));
        hc.setTop(gq.getBottom());
        hc.setRight(new LatLng(49.807582, -97.136929));

        walkableZones.add(hc);

        WalkableZone hd = new WalkableZone(new LatLng(49.807603, -97.136954), new LatLng(49.807730, -97.136334), new LatLng(49.807552, -97.136926), new LatLng(49.807668, -97.136312));
        hd.setLeft(hc.getRight());
        hd.setRight(new LatLng(49.807689, -97.136309));

        walkableZones.add(hd);

        WalkableZone he = new WalkableZone(new LatLng(49.807713, -97.136350), new LatLng(49.807767, -97.136189), new LatLng(49.807655, -97.136330), new LatLng(49.807689, -97.136130));
        he.setLeft(hd.getRight());
        he.setTop(archi2_south_ent.getBottom());
        he.setRight(new LatLng(49.807738, -97.136187));
        he.setBottom(new LatLng(49.807681, -97.136191));

        walkableZones.add(he);

        WalkableZone hf = new WalkableZone(new LatLng(49.807668, -97.136286), new LatLng(49.807694, -97.136117), new LatLng(49.807436, -97.136165), new LatLng(49.807487, -97.135980));
        hf.setTop(he.getBottom());
        hf.setBottom(new LatLng(49.807471, -97.136059));

        walkableZones.add(hf);

        WalkableZone hg = new WalkableZone(new LatLng(49.807410, -97.136204), new LatLng(49.807491, -97.135937), new LatLng(49.807320, -97.136142), new LatLng(49.807415, -97.135869));
        hg.setTop(hf.getBottom());
        hg.setLeft(new LatLng(49.807387, -97.136112));
        hg.setRight(new LatLng(49.807441, -97.135932));

        walkableZones.add(hg);

        WalkableZone hh = new WalkableZone(new LatLng(49.807366, -97.136318), new LatLng(49.807408, -97.136168), new LatLng(49.807277, -97.136275), new LatLng(49.807344, -97.136069));
        hh.setLeft(new LatLng(49.807318, -97.136300));
        hh.setRight(hg.getLeft());

        walkableZones.add(hh);

        WalkableZone hi = new WalkableZone(new LatLng(49.807309, -97.136461), new LatLng(49.807347, -97.136332), new LatLng(49.807232, -97.136390), new LatLng(49.807260, -97.136286));
        hi.setRight(hh.getLeft());
        hi.setBottom(new LatLng(49.807241, -97.136322));

        walkableZones.add(hi);

        WalkableZone hj = new WalkableZone(new LatLng(49.807237, -97.136412), new LatLng(49.807252, -97.136290), new LatLng(49.806996, -97.136302), new LatLng(49.807040, -97.136174));
        hj.setTop(hi.getBottom());
        hj.setBottom(new LatLng(49.807028, -97.136229));

        walkableZones.add(hj);

        WalkableZone hk = new WalkableZone(new LatLng(49.807014, -97.136321), new LatLng(49.807051, -97.136183), new LatLng(49.806942, -97.136258), new LatLng(49.806960, -97.136137));
        hk.setTop(hj.getBottom());
        hk.setLeft(new LatLng(49.806979, -97.136249));
        hk.setRight(new LatLng(49.807007, -97.136153));

        walkableZones.add(hk);

        WalkableZone hl = new WalkableZone(new LatLng(49.806920, -97.136462), new LatLng(49.807023, -97.136277), new LatLng(49.806818, -97.136401), new LatLng(49.806949, -97.136239));
        hl.setRight(hk.getLeft());
        hl.setLeft(new LatLng(49.806882, -97.136424));

        walkableZones.add(hl);

        WalkableZone hm = new WalkableZone(new LatLng(49.806844, -97.136649), new LatLng(49.806900, -97.136478), new LatLng(49.806746, -97.136616), new LatLng(49.806820, -97.136384));
        hm.setLeft(new LatLng(49.806823, -97.136629));
        hm.setRight(hl.getLeft());

        addEntrance(resources.getString(R.string.wb_dafoe_uofm), hm.getLeft());
        walkableZones.add(hm);

        WalkableZone hn = new WalkableZone(new LatLng(49.806248, -97.138602), new LatLng(49.806834, -97.136656), new LatLng(49.806090, -97.138574), new LatLng(49.806752, -97.136622));
        hn.setLeft(fy.getRight());
        hn.setRight(hm.getLeft());

        walkableZones.add(hn);

        WalkableZone ho = new WalkableZone(new LatLng(49.807023, -97.136127), new LatLng(49.807235, -97.135607), new LatLng(49.806969, -97.136050), new LatLng(49.807157, -97.135510));
        ho.setLeft(hk.getRight());
        ho.setRight(new LatLng(49.807187, -97.135548));

        walkableZones.add(ho);

        WalkableZone hp = new WalkableZone(new LatLng(49.807206, -97.135572), new LatLng(49.807227, -97.135514), new LatLng(49.807168, -97.135534), new LatLng(49.807185, -97.135489));
        hp.setLeft(ho.getRight());
        hp.setRight(new LatLng(49.807203, -97.135501));
        hp.setTop(new LatLng(49.807211, -97.135550));

        walkableZones.add(hp);

        WalkableZone hq = new WalkableZone(new LatLng(49.807428, -97.135760), new LatLng(49.807446, -97.135657), new LatLng(49.807202, -97.135564), new LatLng(49.807222, -97.135517));
        hq.setTop(new LatLng(49.807438, -97.135711));
        hq.setBottom(hp.getTop());

        walkableZones.add(hq);

        WalkableZone hr = new WalkableZone(new LatLng(49.807480, -97.135781), new LatLng(49.807482, -97.135697), new LatLng(49.807428, -97.135760), new LatLng(49.807446, -97.135657));
        hr.setLeft(new LatLng(49.807452, -97.135756));
        hr.setBottom(hq.getTop());

        walkableZones.add(hr);

        WalkableZone hs = new WalkableZone(new LatLng(49.807478, -97.136006), new LatLng(49.807482, -97.135775), new LatLng(49.807383, -97.135937), new LatLng(49.807436, -97.135747));
        hs.setRight(hr.getLeft());
        hs.setLeft(hg.getRight());

        walkableZones.add(hs);

        WalkableZone ht = new WalkableZone(new LatLng(49.807225, -97.135530), new LatLng(49.807422, -97.134878), new LatLng(49.807145, -97.135479), new LatLng(49.807358, -97.134834));
        ht.setRight(new LatLng(49.807405, -97.134870));
        ht.setLeft(hp.getRight());

        walkableZones.add(ht);

        WalkableZone hu = new WalkableZone(new LatLng(49.807422, -97.134878), new LatLng(49.807446, -97.134792), new LatLng(49.807358, -97.134834), new LatLng(49.807424, -97.134768));
        hu.setLeft(ht.getRight());
        hu.setRight(new LatLng(49.807436, -97.134784));
        hu.setTop(russel_south_ent.getBottom());
        hu.setBottom(new LatLng(49.807407, -97.134837));

        walkableZones.add(hu);

        WalkableZone hv = new WalkableZone(new LatLng(49.807394, -97.134873), new LatLng(49.807422, -97.134765), new LatLng(49.807292, -97.134742), new LatLng(49.807308, -97.134675));
        hv.setTop(hu.getBottom());
        hv.setBottom(fh.getTop());

        walkableZones.add(hv);

        WalkableZone hw = new WalkableZone(new LatLng(49.807454, -97.134803), new LatLng(49.807874, -97.133806), new LatLng(49.807422, -97.134765), new LatLng(49.807780, -97.133697));
        hw.setLeft(hu.getRight());
        hw.setRight(n.getLeft());

        walkableZones.add(hw);

        WalkableZone hx = new WalkableZone(new LatLng(49.809585, -97.133746), new LatLng(49.809625, -97.133530), new LatLng(49.809770, -97.133934), new LatLng(49.809844, -97.133606));
        hx.setTop(bs.getTop());
        hx.setBottom(new LatLng(49.809610, -97.133596));

        walkableZones.add(hx);

        WalkableZone hy = new WalkableZone(new LatLng(49.809541, -97.133789), new LatLng(49.809629, -97.133521), new LatLng(49.809444, -97.133714), new LatLng(49.809507, -97.133438));
        hy.setTop(hx.getBottom());
        hy.setBottom(new LatLng(49.809514, -97.133526));
        hy.setLeft(uni_centre_north_ent.getRight());

        walkableZones.add(hy);

        WalkableZone hz = new WalkableZone(new LatLng(49.809479, -97.133558), new LatLng(49.809511, -97.133435), new LatLng(49.809155, -97.133304), new LatLng(49.809185, -97.133183));
        hz.setTop(hy.getBottom());
        hz.setLeft(uni_centre_east_ent.getRight());
        hz.setBottom(new LatLng(49.809183, -97.133267));

        walkableZones.add(hz);

        WalkableZone ia = new WalkableZone(new LatLng(49.809194, -97.133180), new LatLng(49.809258, -97.133002), new LatLng(49.809079, -97.133115), new LatLng(49.809127, -97.132889));
        ia.setRight(al.getLeft());
        ia.setLeft(new LatLng(49.809136, -97.133148));

        walkableZones.add(ia);

        WalkableZone ib = new WalkableZone(new LatLng(49.809164, -97.133324), new LatLng(49.809195, -97.133185), new LatLng(49.809057, -97.133225), new LatLng(49.809083, -97.133115));
        ib.setRight(ia.getLeft());
        ib.setTop(hz.getBottom());
        ib.setLeft(new LatLng(49.809091, -97.133237));
        ib.setBottom(new LatLng(49.809068, -97.133162));

        walkableZones.add(ib);

        WalkableZone ic = new WalkableZone(new LatLng(49.809057, -97.133225), new LatLng(49.809086, -97.133122), new LatLng(49.808996, -97.133183), new LatLng(49.809021, -97.133084));
        ic.setTop(ib.getBottom());
        ic.setBottom(new LatLng(49.809016, -97.133126));
        ic.setLeft(new LatLng(49.809040, -97.133177));

        walkableZones.add(ic);

        WalkableZone id = new WalkableZone(new LatLng(49.808994, -97.133431), new LatLng(49.809070, -97.133199), new LatLng(49.808949, -97.133367), new LatLng(49.809007, -97.133151));
        id.setRight(ic.getLeft());
        id.setLeft(eitc_e2_ent.getRight());
        id.setTop(new LatLng(49.809063, -97.133278));

        walkableZones.add(id);

        WalkableZone ig = new WalkableZone(new LatLng(49.809065, -97.133434), new LatLng(49.809126, -97.133256), new LatLng(49.809005, -97.133403), new LatLng(49.809070, -97.133199));
        ig.setLeft(new LatLng(49.809043, -97.133412));
        ig.setRight(ib.getLeft());
        ig.setBottom(id.getTop());

        walkableZones.add(ig);

        WalkableZone ih = new WalkableZone(new LatLng(49.809075, -97.133582), new LatLng(49.809115, -97.133454), new LatLng(49.808982, -97.133504), new LatLng(49.809014, -97.133378));
        ih.setLeft(new LatLng(49.809033, -97.133534));
        ih.setTop(new LatLng(49.809099, -97.133523));
        ih.setBottom(eitc_e2_ent.getTop());
        ih.setRight(ig.getLeft());

        walkableZones.add(ih);

        WalkableZone ii = new WalkableZone(new LatLng(49.809002, -97.133168), new LatLng(49.809027, -97.133079), new LatLng(49.808902, -97.133076), new LatLng(49.808925, -97.133001));
        ii.setTop(ic.getBottom());
        ii.setBottom(new LatLng(49.808926, -97.133034));

        walkableZones.add(ii);

        WalkableZone ij = new WalkableZone(new LatLng(49.808902, -97.133078), new LatLng(49.808925, -97.133000), new LatLng(49.808858, -97.133042), new LatLng(49.808883, -97.132969));
        ij.setBottom(new LatLng(49.808873, -97.132998));
        ij.setTop(ii.getBottom());
        ij.setRight(new LatLng(49.808905, -97.132982));

        walkableZones.add(ij);

        WalkableZone ik = new WalkableZone(new LatLng(49.808834, -97.133304), new LatLng(49.808891, -97.132972), new LatLng(49.808498, -97.133017), new LatLng(49.808580, -97.132743));
        ik.setTop(ij.getBottom());
        ik.setBottom(o.getTop());

        walkableZones.add(ik);

        WalkableZone il = new WalkableZone(new LatLng(49.809370, -97.133901), new LatLng(49.809432, -97.133733), new LatLng(49.809295, -97.133866), new LatLng(49.809357, -97.133642));
        il.setBottom(new LatLng(49.809315, -97.133807));
        il.setTop(uni_centre_north_ent.getBottom());

        walkableZones.add(il);

        WalkableZone im = new WalkableZone(new LatLng(49.809187, -97.133802), new LatLng(49.809227, -97.133658), new LatLng(49.809053, -97.133682), new LatLng(49.809110, -97.133454));
        im.setTop(new LatLng(49.809204, -97.133726));
        im.setBottom(ih.getTop());

        walkableZones.add(im);

        WalkableZone in = new WalkableZone(new LatLng(49.809295, -97.133864), new LatLng(49.809324, -97.133737), new LatLng(49.809185, -97.133802), new LatLng(49.809218, -97.133655));
        in.setBottom(im.getTop());
        in.setTop(il.getBottom());
        in.setLeft(new LatLng(49.809236, -97.133827));

        walkableZones.add(in);

        WalkableZone io = new WalkableZone(new LatLng(49.808969, -97.134733), new LatLng(49.809294, -97.133837), new LatLng(49.808925, -97.134694), new LatLng(49.809102, -97.133740));
        io.setRight(in.getLeft());
        io.setLeft(new LatLng(49.808951, -97.134699));

        walkableZones.add(io);

        WalkableZone ip = new WalkableZone(new LatLng(49.808927, -97.134904), new LatLng(49.808973, -97.134718), new LatLng(49.808803, -97.134861), new LatLng(49.808857, -97.134656));
        ip.setRight(io.getLeft());
        ip.setBottom(new LatLng(49.808816, -97.134808));
        ip.setLeft(new LatLng(49.808896, -97.134874));

        walkableZones.add(ip);

        WalkableZone iq = new WalkableZone(new LatLng(49.808873, -97.135073), new LatLng(49.808927, -97.134904), new LatLng(49.808771, -97.134989), new LatLng(49.808803, -97.134861));
        iq.setTop(uni_centre_west_ent.getBottom());
        iq.setRight(ip.getLeft());

        walkableZones.add(iq);

        WalkableZone ir = new WalkableZone(new LatLng(49.808780, -97.134891), new LatLng(49.808859, -97.134634), new LatLng(49.808721, -97.134849), new LatLng(49.808803, -97.134616));
        ir.setTop(ip.getBottom());
        ir.setLeft(new LatLng(49.808750, -97.134865));

        walkableZones.add(ir);

        WalkableZone is = new WalkableZone(new LatLng(49.808784, -97.134577), new LatLng(49.809078, -97.133558), new LatLng(49.808663, -97.134458), new LatLng(49.808987, -97.133491));
        is.setLeft(new LatLng(49.808742, -97.134541));
        is.setRight(ih.getLeft());

        walkableZones.add(is);

        WalkableZone it = new WalkableZone(new LatLng(49.808756, -97.134647), new LatLng(49.808770, -97.134525), new LatLng(49.808699, -97.134605), new LatLng(49.808719, -97.134527));
        it.setBottom(eitc_e3_ent.getTop());
        it.setLeft(new LatLng(49.808712, -97.134615));
        it.setRight(is.getLeft());

        walkableZones.add(it);

        WalkableZone iu = new WalkableZone(new LatLng(49.808675, -97.134844), new LatLng(49.808741, -97.134654), new LatLng(49.808564, -97.134740), new LatLng(49.808634, -97.134531));
        iu.setRight(it.getLeft());
        iu.setLeft(new LatLng(49.808649, -97.134794));

        walkableZones.add(iu);

        WalkableZone iv = new WalkableZone(new LatLng(49.808652, -97.134934), new LatLng(49.808672, -97.134821), new LatLng(49.808536, -97.134845), new LatLng(49.808568, -97.134743));
        iv.setRight(iu.getLeft());
        iv.setLeft(new LatLng(49.808600, -97.134911));
        iv.setTop(new LatLng(49.808681, -97.134869));

        walkableZones.add(iv);

        WalkableZone iw = new WalkableZone(new LatLng(49.808745, -97.134999), new LatLng(49.808783, -97.134882), new LatLng(49.808652, -97.134934), new LatLng(49.808672, -97.134821));
        iw.setLeft(new LatLng(49.808711, -97.134971));
        iw.setRight(ir.getLeft());
        iw.setBottom(iv.getTop());

        walkableZones.add(iw);

        WalkableZone ix = new WalkableZone(new LatLng(49.808766, -97.135097), new LatLng(49.808786, -97.135026), new LatLng(49.808732, -97.135052), new LatLng(49.808744, -97.134995));
        ix.setTop(uni_centre_south_west_ent.getBottom());
        ix.setLeft(new LatLng(49.808749, -97.135069));
        ix.setBottom(new LatLng(49.808737, -97.135029));

        walkableZones.add(ix);

        WalkableZone iy = new WalkableZone(new LatLng(49.808748, -97.135195), new LatLng(49.808766, -97.135097), new LatLng(49.808699, -97.135173), new LatLng(49.808732, -97.135052));
        iy.setRight(ix.getLeft());
        iy.setBottom(new LatLng(49.808703, -97.135102));
        iy.setLeft(new LatLng(49.808736, -97.135156));

        walkableZones.add(iy);

        WalkableZone iz = new WalkableZone(new LatLng(49.808748, -97.135499), new LatLng(49.808759, -97.135183), new LatLng(49.808669, -97.135534), new LatLng(49.808719, -97.135134));
        iz.setRight(iy.getLeft());
        iz.setLeft(helen_glass.getRight());

        walkableZones.add(iz);

        WalkableZone ja = new WalkableZone(new LatLng(49.808730, -97.135058), new LatLng(49.808744, -97.134998), new LatLng(49.808683, -97.135035), new LatLng(49.808695, -97.134968));
        ja.setLeft(new LatLng(49.808712, -97.135043));
        ja.setRight(iw.getLeft());
        ja.setBottom(new LatLng(49.808687, -97.135011));
        ja.setTop(ix.getBottom());

        walkableZones.add(ja);

        WalkableZone jb = new WalkableZone(new LatLng(49.808683, -97.135035), new LatLng(49.808695, -97.134968), new LatLng(49.808519, -97.134949), new LatLng(49.808549, -97.134877));
        jb.setTop(ja.getBottom());
        jb.setLeft(new LatLng(49.808587, -97.134958));
        jb.setRight(iv.getLeft());

        walkableZones.add(jb);

        WalkableZone jc = new WalkableZone(new LatLng(49.808707, -97.135152), new LatLng(49.808726, -97.135063), new LatLng(49.808651, -97.135136), new LatLng(49.808679, -97.135039));
        jc.setRight(ja.getLeft());
        jc.setBottom(new LatLng(49.808653, -97.135098));
        jc.setTop(iy.getBottom());

        walkableZones.add(jc);

        WalkableZone jd = new WalkableZone(new LatLng(49.808651, -97.135136), new LatLng(49.808679, -97.135039), new LatLng(49.808480, -97.135063), new LatLng(49.808510, -97.134935));
        jd.setTop(jc.getBottom());
        jd.setRight(jb.getLeft());
        jd.setLeft(new LatLng(49.808548, -97.135068));

        walkableZones.add(jd);

        WalkableZone je = new WalkableZone(new LatLng(49.808513, -97.135542), new LatLng(49.808638, -97.135130), new LatLng(49.808396, -97.135477), new LatLng(49.808516, -97.135050));
        je.setRight(jd.getLeft());
        je.setBottom(new LatLng(49.808401, -97.135387));
        je.setLeft(new LatLng(49.808481, -97.135518));

        walkableZones.add(je);

        WalkableZone jf = new WalkableZone(new LatLng(49.808384, -97.135509), new LatLng(49.808431, -97.135284), new LatLng(49.808315, -97.135441), new LatLng(49.808378, -97.135250));
        jf.setTop(je.getBottom());
        jf.setLeft(new LatLng(49.808324, -97.135465));

        walkableZones.add(jf);

        WalkableZone jg = new WalkableZone(new LatLng(49.808287, -97.135649), new LatLng(49.808340, -97.135508), new LatLng(49.808254, -97.135623), new LatLng(49.808315, -97.135441));
        jg.setRight(jf.getLeft());
        jg.setLeft(new LatLng(49.808270, -97.135634));
        jg.setBottom(russel_north_ent.getTop());

        walkableZones.add(jg);

        WalkableZone jh = new WalkableZone(new LatLng(49.808518, -97.135642), new LatLng(49.808520, -97.135566), new LatLng(49.808415, -97.135582), new LatLng(49.808429, -97.135495));
        jh.setLeft(new LatLng(49.808455, -97.135582));
        jh.setRight(je.getLeft());
        jh.setTop(new LatLng(49.808524, -97.135571));

        walkableZones.add(jh);

        WalkableZone ji = new WalkableZone(new LatLng(49.808688, -97.135687), new LatLng(49.808690, -97.135578), new LatLng(49.808519, -97.135616), new LatLng(49.808528, -97.135561));
        ji.setTop(helen_glass.getBottom());
        ji.setBottom(jh.getTop());

        walkableZones.add(ji);

        WalkableZone jj = new WalkableZone(new LatLng(49.808450, -97.135752), new LatLng(49.808509, -97.135600), new LatLng(49.808361, -97.135707), new LatLng(49.808410, -97.135577));
        jj.setRight(jh.getLeft());
        jj.setLeft(new LatLng(49.808404, -97.135723));

        walkableZones.add(jj);

        WalkableZone jk = new WalkableZone(new LatLng(49.808432, -97.135911), new LatLng(49.808439, -97.135784), new LatLng(49.808325, -97.135862), new LatLng(49.808339, -97.135830));
        jk.setTop(new LatLng(49.808434, -97.135839));
        jk.setLeft(new LatLng(49.808335, -97.135915));
        jk.setRight(new LatLng(49.808393, -97.135794));

        walkableZones.add(jk);

        WalkableZone jl = new WalkableZone(new LatLng(49.808274, -97.135700), new LatLng(49.808291, -97.135646), new LatLng(49.808233, -97.135669), new LatLng(49.808253, -97.135614));
        jl.setTop(new LatLng(49.808278, -97.135671));
        jl.setLeft(new LatLng(49.808258, -97.135681));
        jl.setRight(jg.getLeft());

        walkableZones.add(jl);

        WalkableZone jm = new WalkableZone(new LatLng(49.808339, -97.135826), new LatLng(49.808362, -97.135702), new LatLng(49.808274, -97.135700), new LatLng(49.808291, -97.135646));
        jm.setTop(new LatLng(49.808353, -97.135734));
        jm.setBottom(jl.getTop());

        walkableZones.add(jm);

        WalkableZone jn = new WalkableZone(new LatLng(49.808212, -97.135899), new LatLng(49.808274, -97.135685), new LatLng(49.808141, -97.135929), new LatLng(49.808239, -97.135655));
        jn.setLeft(new LatLng(49.808190, -97.135903));
        jn.setRight(jl.getLeft());

        walkableZones.add(jn);

        WalkableZone jo = new WalkableZone(new LatLng(49.808262, -97.136000), new LatLng(49.808288, -97.135899), new LatLng(49.808101, -97.136032), new LatLng(49.808111, -97.135948));
        jo.setRight(jn.getLeft());
        jo.setTop(new LatLng(49.808205, -97.135957));
        jo.setBottom(new LatLng(49.808132, -97.135969));

        walkableZones.add(jo);

        WalkableZone jp = new WalkableZone(new LatLng(49.808125, -97.136074), new LatLng(49.808132, -97.135937), new LatLng(49.807911, -97.136115), new LatLng(49.807860, -97.135904));
        jp.setTop(jo.getBottom());
        jp.setLeft(archi2_north_ent.getRight());
        jp.setBottom(new LatLng(49.807948, -97.136049));

        walkableZones.add(jp);

        WalkableZone jq = new WalkableZone(new LatLng(49.807953, -97.136089), new LatLng(49.807953, -97.136008), new LatLng(49.807747, -97.136211), new LatLng(49.807719, -97.136124));
        jq.setTop(jp.getBottom());
        jq.setBottom(he.getRight());

        walkableZones.add(jq);

        WalkableZone jr = new WalkableZone(new LatLng(49.808268, -97.135988), new LatLng(49.808289, -97.135912), new LatLng(49.808197, -97.136025), new LatLng(49.808211, -97.135896));
        jr.setTop(new LatLng(49.808278, -97.135950));
        jr.setBottom(jo.getTop());

        walkableZones.add(jr);

        WalkableZone js = new WalkableZone(new LatLng(49.808342, -97.136014), new LatLng(49.808366, -97.135941), new LatLng(49.808268, -97.135988), new LatLng(49.808289, -97.135912));
        js.setLeft(new LatLng(49.808311, -97.135997));
        js.setRight(jk.getLeft());
        js.setBottom(jr.getTop());

        walkableZones.add(js);

        WalkableZone jt = new WalkableZone(new LatLng(49.808366, -97.136164), new LatLng(49.808342, -97.136006), new LatLng(49.808314, -97.136261), new LatLng(49.808267, -97.135992));
        jt.setRight(js.getLeft());
        jt.setTop(new LatLng(49.808347, -97.136203));

        walkableZones.add(jt);

        WalkableZone ju = new WalkableZone(new LatLng(49.808405, -97.136316), new LatLng(49.808428, -97.136199), new LatLng(49.808313, -97.136245), new LatLng(49.808354, -97.136139));
        ju.setLeft(gx.getRight());
        ju.setBottom(jt.getTop());
        ju.setTop(new LatLng(49.808426, -97.136251));

        walkableZones.add(ju);

        WalkableZone jv = new WalkableZone(new LatLng(49.808568, -97.136322), new LatLng(49.808515, -97.136140), new LatLng(49.808400, -97.136311), new LatLng(49.808422, -97.136198));
        jv.setBottom(ju.getTop());
        jv.setRight(new LatLng(49.808536, -97.136200));

        walkableZones.add(jv);

        WalkableZone jw = new WalkableZone(new LatLng(49.808568, -97.136235), new LatLng(49.808588, -97.136173), new LatLng(49.808505, -97.136171), new LatLng(49.808515, -97.136125));
        jw.setTop(new LatLng(49.808577, -97.136208));
        jw.setLeft(jv.getRight());
        jw.setRight(new LatLng(49.808552, -97.136155));
        walkableZones.add(jw);

        WalkableZone jx = new WalkableZone(new LatLng(49.808592, -97.136187), new LatLng(49.808586, -97.135921), new LatLng(49.808520, -97.136130), new LatLng(49.808511, -97.135955));
        jx.setLeft(jw.getRight());
        jx.setTop(new LatLng(49.808602, -97.136014));
        jx.setRight(new LatLng(49.808529, -97.135912));

        walkableZones.add(jx);

        WalkableZone jy = new WalkableZone(new LatLng(49.808528, -97.136012), new LatLng(49.808576, -97.135871), new LatLng(49.808413, -97.135915), new LatLng(49.808442, -97.135783));
        jy.setTop(jx.getRight());
        jy.setBottom(jk.getTop());

        walkableZones.add(jy);

        WalkableZone jz = new WalkableZone(new LatLng(49.808761, -97.136115), new LatLng(49.808768, -97.135939), new LatLng(49.808601, -97.136093), new LatLng(49.808587, -97.135937));
        jz.setLeft(new LatLng(49.808696, -97.136073));
        jz.setBottom(jx.getTop());
        jz.setTop(er.getBottom());

        walkableZones.add(jz);

        WalkableZone ka = new WalkableZone(new LatLng(49.808639, -97.136348), new LatLng(49.808668, -97.136238), new LatLng(49.808547, -97.136294), new LatLng(49.808584, -97.136188));
        ka.setBottom(jw.getTop());
        ka.setRight(new LatLng(49.808646, -97.136227));
        ka.setLeft(education_south_ent.getRight());

        walkableZones.add(ka);

        WalkableZone kb = new WalkableZone(new LatLng(49.808656, -97.136242), new LatLng(49.808718, -97.136077), new LatLng(49.808630, -97.136217), new LatLng(49.808672, -97.136060));
        kb.setLeft(ka.getRight());
        kb.setRight(jz.getLeft());

        walkableZones.add(kb);

        WalkableZone kc = new WalkableZone(new LatLng(49.808925, -97.133014), new LatLng(49.808969, -97.132893), new LatLng(49.808886, -97.132981), new LatLng(49.808932, -97.132848));
        kc.setLeft(ij.getRight());
        kc.setRight(new LatLng(49.808950, -97.132876));

        walkableZones.add(kc);

        WalkableZone kd = new WalkableZone(new LatLng(49.809006, -97.132926), new LatLng(49.809040, -97.132840), new LatLng(49.808926, -97.132861), new LatLng(49.808952, -97.132778));
        kd.setLeft(kc.getRight());
        kd.setBottom(new LatLng(49.808956, -97.132822));

        walkableZones.add(kd);

        WalkableZone ke = new WalkableZone(new LatLng(49.808995, -97.132807), new LatLng(49.808919, -97.132534), new LatLng(49.808931, -97.132852), new LatLng(49.808876, -97.132600));
        ke.setTop(kd.getBottom());
        ke.setBottom(new LatLng(49.808897, -97.132560));

        walkableZones.add(ke);

        WalkableZone kf = new WalkableZone(new LatLng(49.808919, -97.132534), new LatLng(49.808907, -97.132494), new LatLng(49.808876, -97.132600), new LatLng(49.808859, -97.132500));
        kf.setLeft(ke.getBottom());
        kf.setTop(new LatLng(49.808914, -97.132500));
        kf.setBottom(new LatLng(49.808869, -97.132534));

        walkableZones.add(kf);

        WalkableZone kg = new WalkableZone(new LatLng(49.809060, -97.132374), new LatLng(49.809037, -97.132316), new LatLng(49.808920, -97.132534), new LatLng(49.808907, -97.132479));
        kg.setRight(new LatLng(49.809048, -97.132343));
        kg.setBottom(kf.getTop());

        walkableZones.add(kg);

        WalkableZone kh = new WalkableZone(new LatLng(49.809060, -97.132374), new LatLng(49.809098, -97.132248), new LatLng(49.809033, -97.132334), new LatLng(49.809059, -97.132242));
        kh.setLeft(kg.getRight());
        kh.setRight(new LatLng(49.809081, -97.132241));
        kh.setTop(new LatLng(49.809085, -97.132296));

        walkableZones.add(kh);

        WalkableZone ki = new WalkableZone(new LatLng(49.809247, -97.132490), new LatLng(49.809284, -97.132373), new LatLng(49.809072, -97.132380), new LatLng(49.809095, -97.132254));
        ki.setTop(ak.getBottom());
        ki.setBottom(kh.getTop());

        walkableZones.add(ki);

        WalkableZone kj = new WalkableZone(new LatLng(49.809105, -97.132251), new LatLng(49.809111, -97.131937), new LatLng(49.809057, -97.132242), new LatLng(49.809068, -97.131978));
        kj.setLeft(kh.getRight());
        kj.setRight(new LatLng(49.809094, -97.131941));

        walkableZones.add(kj);

        WalkableZone kk = new WalkableZone(new LatLng(49.809109, -97.131939), new LatLng(49.809091, -97.131844), new LatLng(49.809064, -97.131954), new LatLng(49.809057, -97.131859));
        kk.setLeft(kj.getRight());
        kk.setRight(new LatLng(49.809071, -97.131855));
        kk.setTop(new LatLng(49.809097, -97.131889));

        walkableZones.add(kk);

        WalkableZone kl = new WalkableZone(new LatLng(49.809310, -97.131867), new LatLng(49.809305, -97.131714), new LatLng(49.809109, -97.131939), new LatLng(49.809091, -97.131844));
        kl.setBottom(kk.getTop());
        kl.setTop(ae.getBottom());

        walkableZones.add(kl);

        WalkableZone km = new WalkableZone(new LatLng(49.809048, -97.131890), new LatLng(49.809091, -97.131842), new LatLng(49.808942, -97.131705), new LatLng(49.808969, -97.131617));
        km.setLeft(kk.getRight());
        km.setBottom(new LatLng(49.808965, -97.131667));

        walkableZones.add(km);

        WalkableZone kn = new WalkableZone(new LatLng(49.808950, -97.131730), new LatLng(49.808984, -97.131617), new LatLng(49.808885, -97.131669), new LatLng(49.808903, -97.131590));
        kn.setTop(km.getBottom());
        kn.setBottom(new LatLng(49.808899, -97.131645));
        kn.setRight(new LatLng(49.808945, -97.131607));

        walkableZones.add(kn);

        WalkableZone ko = new WalkableZone(new LatLng(49.808976, -97.131650), new LatLng(49.809056, -97.131420), new LatLng(49.808904, -97.131595), new LatLng(49.808974, -97.131357));
        ko.setLeft(kn.getRight());
        ko.setRight(an.getLeft());

        walkableZones.add(ko);

        WalkableZone kp = new WalkableZone(new LatLng(49.808908, -97.131673), new LatLng(49.808921, -97.131577), new LatLng(49.808744, -97.131656), new LatLng(49.808730, -97.131559));
        kp.setTop(kn.getBottom());
        kp.setBottom(new LatLng(49.808748, -97.131616));

        walkableZones.add(kp);

        WalkableZone kq = new WalkableZone(new LatLng(49.808686, -97.131637), new LatLng(49.808747, -97.131552), new LatLng(49.808691, -97.131683), new LatLng(49.808677, -97.131587));
        kq.setTop(kp.getBottom());
        kq.setBottom(new LatLng(49.808683, -97.131646));
        kq.setRight(new LatLng(49.808707, -97.131568));

        walkableZones.add(kq);

        WalkableZone kr = new WalkableZone(new LatLng(49.808739, -97.131563), new LatLng(49.808684, -97.131191), new LatLng(49.808683, -97.131594), new LatLng(49.808627, -97.131232));
        kr.setLeft(kq.getRight());
        kr.setRight(new LatLng(49.808652, -97.131214));

        walkableZones.add(kr);

        WalkableZone ks = new WalkableZone(new LatLng(49.808691, -97.131234), new LatLng(49.808741, -97.131166), new LatLng(49.808575, -97.131211), new LatLng(49.808614, -97.131091));
        ks.setLeft(new LatLng(49.808605, -97.131195));
        ks.setRight(ap.getLeft());
        ks.setTop(kr.getRight());

        walkableZones.add(ks);

        WalkableZone kt = new WalkableZone(new LatLng(49.808691, -97.131664), new LatLng(49.808684, -97.131583), new LatLng(49.808605, -97.131733), new LatLng(49.808598, -97.131648));
        kt.setRight(kq.getBottom());
        kt.setBottom(new LatLng(49.808600, -97.131681));

        walkableZones.add(kt);

        WalkableZone ku = new WalkableZone(new LatLng(49.808591, -97.131747), new LatLng(49.808596, -97.131656), new LatLng(49.808542, -97.131725), new LatLng(49.808557, -97.131666));
        ku.setLeft(new LatLng(49.808558, -97.131745));
        ku.setBottom(new LatLng(49.808549, -97.131690));
        ku.setTop(kt.getBottom());

        walkableZones.add(ku);

        WalkableZone kv = new WalkableZone(new LatLng(49.808510, -97.131978), new LatLng(49.808582, -97.131769), new LatLng(49.808468, -97.131963), new LatLng(49.808547, -97.131726));
        kv.setLeft(new LatLng(49.808488, -97.131966));
        kv.setRight(ku.getLeft());

        walkableZones.add(kv);

        WalkableZone kw = new WalkableZone(new LatLng(49.808514, -97.132132), new LatLng(49.808514, -97.131975), new LatLng(49.808468, -97.132126), new LatLng(49.808472, -97.131963));
        kw.setLeft(new LatLng(49.808484, -97.132128));
        kw.setRight(kv.getLeft());
        kw.setBottom(new LatLng(49.808470, -97.132012));

        walkableZones.add(kw);

        WalkableZone kx = new WalkableZone(new LatLng(49.808541, -97.132239), new LatLng(49.808501, -97.132151), new LatLng(49.808482, -97.132266), new LatLng(49.808469, -97.132128));
        kx.setRight(kw.getLeft());
        kx.setBottom(new LatLng(49.808469, -97.132213));
        kx.setLeft(new LatLng(49.808502, -97.132243));

        walkableZones.add(kx);

        WalkableZone ky = new WalkableZone(new LatLng(49.808654, -97.132582), new LatLng(49.808675, -97.132450), new LatLng(49.808478, -97.132441), new LatLng(49.808488, -97.132242));
        ky.setRight(kx.getLeft());
        ky.setTop(new LatLng(49.808642, -97.132497));

        walkableZones.add(ky);

        WalkableZone kz = new WalkableZone(new LatLng(49.808867, -97.132617), new LatLng(49.808853, -97.132499), new LatLng(49.808654, -97.132582), new LatLng(49.808675, -97.132450));
        kz.setBottom(ky.getTop());
        kz.setRight(kf.getBottom());

        walkableZones.add(kz);

        WalkableZone la = new WalkableZone(new LatLng(49.808452, -97.131682), new LatLng(49.808617, -97.131215), new LatLng(49.808413, -97.131683), new LatLng(49.808583, -97.131180));
        la.setRight(ks.getLeft());
        la.setLeft(new LatLng(49.808439, -97.131677));

        walkableZones.add(la);

        WalkableZone lb = new WalkableZone(new LatLng(49.808426, -97.131765), new LatLng(49.808446, -97.131702), new LatLng(49.808393, -97.131752), new LatLng(49.808421, -97.131685));
        lb.setLeft(new LatLng(49.808401, -97.131757));
        lb.setRight(la.getLeft());
        lb.setTop(new LatLng(49.808436, -97.131730));

        walkableZones.add(lb);

        WalkableZone lc = new WalkableZone(new LatLng(49.808544, -97.131731), new LatLng(49.808598, -97.131646), new LatLng(49.808426, -97.131765), new LatLng(49.808446, -97.131702));
        lc.setTop(ku.getBottom());
        lc.setBottom(lb.getTop());

        walkableZones.add(lc);

        WalkableZone ld = new WalkableZone(new LatLng(49.808386, -97.131903), new LatLng(49.808426, -97.131765), new LatLng(49.808346, -97.131879), new LatLng(49.808393, -97.131752));
        ld.setLeft(new LatLng(49.808369, -97.131877));
        ld.setTop(new LatLng(49.808396, -97.131849));
        ld.setRight(lb.getLeft());

        walkableZones.add(ld);

        WalkableZone le = new WalkableZone(new LatLng(49.808464, -97.132102), new LatLng(49.808472, -97.131969), new LatLng(49.808386, -97.131903), new LatLng(49.808408, -97.131814));
        le.setBottom(ld.getTop());
        le.setTop(kw.getBottom());

        walkableZones.add(le);

        WalkableZone lf = new WalkableZone(new LatLng(49.808263, -97.132243), new LatLng(49.808387, -97.131892), new LatLng(49.808236, -97.132222), new LatLng(49.808358, -97.131858));
        lf.setRight(ld.getLeft());
        lf.setLeft(new LatLng(49.808249, -97.132228));

        walkableZones.add(lf);

        WalkableZone lg = new WalkableZone(new LatLng(49.808278, -97.132366), new LatLng(49.808294, -97.132253), new LatLng(49.808213, -97.132301), new LatLng(49.808234, -97.132216));
        lg.setLeft(new LatLng(49.808227, -97.132306));
        lg.setTop(new LatLng(49.808276, -97.132284));
        lg.setRight(lf.getLeft());

        walkableZones.add(lg);

        WalkableZone lh = new WalkableZone(new LatLng(49.808482, -97.132259), new LatLng(49.808475, -97.132159), new LatLng(49.808287, -97.132348), new LatLng(49.808275, -97.132248));
        lh.setTop(kx.getBottom());
        lh.setBottom(lg.getTop());

        walkableZones.add(lh);

        WalkableZone li = new WalkableZone(new LatLng(49.808194, -97.132426), new LatLng(49.808243, -97.132299), new LatLng(49.808173, -97.132423), new LatLng(49.808220, -97.132272));
        li.setRight(lg.getLeft());
        li.setLeft(eu.getRight());

        walkableZones.add(li);

        WalkableZone lj = new WalkableZone(new LatLng(49.808439, -97.135784), new LatLng(49.808446, -97.135748), new LatLng(49.808339, -97.135830), new LatLng(49.808368, -97.135704));
        lj.setRight(jj.getLeft());
        lj.setBottom(jm.getTop());
        lj.setLeft(jk.getRight());

        walkableZones.add(lj);

        WalkableZone lk = new WalkableZone(new LatLng(49.808506, -97.130960), new LatLng(49.808624, -97.130624), new LatLng(49.808405, -97.130885), new LatLng(49.808514, -97.130424));
        lk.setLeft(tache_arts_east_ent.getRight());
        lk.setRight(artlab_ent.getLeft());

        walkableZones.add(lk);

        WalkableZone ll = new WalkableZone(new LatLng(49.808526, -97.131163), new LatLng(49.808569, -97.130985), new LatLng(49.808460, -97.131075), new LatLng(49.808497, -97.130925));
        ll.setLeft(new LatLng(49.808492, -97.131126));
        ll.setBottom(tache_arts_east_ent.getTop());

        walkableZones.add(ll);

        WalkableZone lm = new WalkableZone(new LatLng(49.808128, -97.132291), new LatLng(49.808526, -97.131163), new LatLng(49.808046, -97.132235), new LatLng(49.808460, -97.131075));
        lm.setLeft(new LatLng(49.808099, -97.132259));
        lm.setRight(ll.getLeft());

        walkableZones.add(lm);

        WalkableZone ln = new WalkableZone(new LatLng(49.808101, -97.132402), new LatLng(49.808128, -97.132291), new LatLng(49.808013, -97.132347), new LatLng(49.808046, -97.132235));
        ln.setRight(lm.getLeft());
        ln.setLeft(ey.getRight());
        ln.setBottom(tache_arts_mid_west_ent.getTop());

        walkableZones.add(ln);

        WalkableZone lo = new WalkableZone(new LatLng(49.807900, -97.132794), new LatLng(49.807920, -97.132724), new LatLng(49.807815, -97.132722), new LatLng(49.807837, -97.132664));
        lo.setTop(fa.getBottom());
        lo.setBottom(new LatLng(49.807827, -97.132706));

        walkableZones.add(lo);

        WalkableZone lp = new WalkableZone(new LatLng(49.807823, -97.132725), new LatLng(49.807844, -97.132670), new LatLng(49.807785, -97.132696), new LatLng(49.807792, -97.132654));
        lp.setTop(lo.getBottom());
        lp.setRight(tache_arts_west_ent.getLeft());
        lp.setBottom(new LatLng(49.807800, -97.132686));

        addEntrance(resources.getString(R.string.nb_maclean_uofm), lp.getBottom());
        walkableZones.add(lp);

        WalkableZone lq = new WalkableZone(new LatLng(49.807790, -97.132701), new LatLng(49.807799, -97.132665), new LatLng(49.807673, -97.132597), new LatLng(49.807696, -97.132580));
        lq.setTop(lp.getBottom());
        lq.setBottom(new LatLng(49.807674, -97.132578));

        walkableZones.add(lq);

        WalkableZone lr = new WalkableZone(new LatLng(49.811095, -97.137751), new LatLng(49.811111, -97.137692), new LatLng(49.811030, -97.137694), new LatLng(49.811050, -97.137640));
        lr.setBottom(dp.getTop());
        lr.setTop(new LatLng(49.811101, -97.137721));

        walkableZones.add(lr);

        WalkableZone ls = new WalkableZone(new LatLng(49.811119, -97.137793), new LatLng(49.811138, -97.137710), new LatLng(49.811095, -97.137751), new LatLng(49.811111, -97.137692));
        ls.setBottom(lr.getTop());
        ls.setTop(new LatLng(49.811128, -97.137763));
        ls.setRight(new LatLng(49.811125, -97.137706));

        walkableZones.add(ls);

        WalkableZone lt = new WalkableZone(new LatLng(49.811145, -97.137826), new LatLng(49.811165, -97.137777), new LatLng(49.811114, -97.137797), new LatLng(49.811134, -97.137736));
        lt.setBottom(ls.getTop());
        lt.setTop(new LatLng(49.811145, -97.137826));

        walkableZones.add(lt);

        //Q lot
        WalkableZone lu = new WalkableZone(new LatLng(49.811529, -97.140758), new LatLng(49.812239, -97.138603), new LatLng(49.810447, -97.139838), new LatLng(49.811173, -97.137742));
        lu.setBottom(lt.getTop());

        walkableZones.add(lu);

        WalkableZone lv = new WalkableZone(new LatLng(49.811139, -97.137730), new LatLng(49.811185, -97.137597), new LatLng(49.811109, -97.137696), new LatLng(49.811168, -97.137529));
        lv.setLeft(ls.getRight());
        lv.setRight(new LatLng(49.811169, -97.137582));

        walkableZones.add(lv);

        WalkableZone lw = new WalkableZone(new LatLng(49.811196, -97.137610), new LatLng(49.811210, -97.137547), new LatLng(49.811146, -97.137572), new LatLng(49.811166, -97.137536));
        lw.setLeft(lv.getRight());
        lw.setTop(new LatLng(49.811201, -97.137577));

        walkableZones.add(lw);

        //K lot
        WalkableZone lx = new WalkableZone(new LatLng(49.812224, -97.138470), new LatLng(49.812047, -97.137238), new LatLng(49.811204, -97.137644), new LatLng(49.811518, -97.136726));
        lx.setBottom(lw.getTop());

        walkableZones.add(lx);

        WalkableZone ly = new WalkableZone(new LatLng(49.810273, -97.139754), new LatLng(49.810749, -97.138429), new LatLng(49.810249, -97.139732), new LatLng(49.810711, -97.138403));
        ly.setLeft(new LatLng(49.810268, -97.139747));
        ly.setRight(dt.getLeft());

        walkableZones.add(ly);

        WalkableZone lz = new WalkableZone(new LatLng(49.810259, -97.139845), new LatLng(49.810282, -97.139752), new LatLng(49.810218, -97.139822), new LatLng(49.810241, -97.139723));
        lz.setRight(ly.getLeft());
        lz.setLeft(new LatLng(49.810243, -97.139823));
        lz.setBottom(new LatLng(49.810243, -97.139758));

        walkableZones.add(lz);

        WalkableZone ma = new WalkableZone(new LatLng(49.809798, -97.141210), new LatLng(49.810257, -97.139840), new LatLng(49.809766, -97.141177), new LatLng(49.810226, -97.139814));
        ma.setLeft(new LatLng(49.809784, -97.141186));
        ma.setRight(lz.getLeft());

        walkableZones.add(ma);

        WalkableZone mb = new WalkableZone(new LatLng(49.809781, -97.141260), new LatLng(49.809800, -97.141200), new LatLng(49.809747, -97.141256), new LatLng(49.809766, -97.141205));
        mb.setLeft(new LatLng(49.809771, -97.141233));
        mb.setBottom(new LatLng(49.809761, -97.141219));
        mb.setRight(ma.getLeft());

        walkableZones.add(mb);

        WalkableZone mc = new WalkableZone(new LatLng(49.809674, -97.141525), new LatLng(49.809785, -97.141213), new LatLng(49.809651, -97.141531), new LatLng(49.809754, -97.141270));
        mc.setLeft(new LatLng(49.809669, -97.141523));
        mc.setRight(mb.getLeft());

        walkableZones.add(mc);

        //northbound uni
        WalkableZone md = new WalkableZone(new LatLng(49.809732, -97.141681), new LatLng(49.809750, -97.141584), new LatLng(49.809610, -97.141571), new LatLng(49.809640, -97.141507));
        md.setRight(mc.getLeft());
        md.setBottom(new LatLng(49.809624, -97.141531));

        addEntrance(resources.getString(R.string.sb_uni_at_dysart), md.getBottom());
        walkableZones.add(md);

        WalkableZone me = new WalkableZone(new LatLng(49.809620, -97.141543), new LatLng(49.809627, -97.141512), new LatLng(49.809445, -97.141396), new LatLng(49.809456, -97.141374));
        me.setTop(md.getBottom());
        me.setBottom(new LatLng(49.809434, -97.141377));

        walkableZones.add(me);

        WalkableZone mf = new WalkableZone(new LatLng(49.809445, -97.141396), new LatLng(49.809456, -97.141374), new LatLng(49.809380, -97.141345), new LatLng(49.809394, -97.141322));
        mf.setTop(me.getBottom());
        mf.setBottom(new LatLng(49.809385, -97.141329));
        mf.setRight(new LatLng(49.809445, -97.141353));

        walkableZones.add(mf);

        WalkableZone mg = new WalkableZone(new LatLng(49.809754, -97.141272), new LatLng(49.809759, -97.141200), new LatLng(49.809446, -97.141371), new LatLng(49.809420, -97.141320));
        mg.setLeft(mf.getRight());
        mg.setTop(mb.getBottom());

        walkableZones.add(mg);

        WalkableZone mh = new WalkableZone(new LatLng(49.809416, -97.141376), new LatLng(49.809412, -97.141315), new LatLng(49.809217, -97.141251), new LatLng(49.809243, -97.141182));
        mh.setTop(mf.getBottom());
        mh.setBottom(new LatLng(49.809228, -97.141222));

        walkableZones.add(mh);

        WalkableZone mi = new WalkableZone(new LatLng(49.809221, -97.141259), new LatLng(49.809249, -97.141176), new LatLng(49.809180, -97.141228), new LatLng(49.809207, -97.141155));
        mi.setTop(mh.getBottom());
        mi.setLeft(new LatLng(49.809202, -97.141240));
        mi.setBottom(new LatLng(49.809200, -97.141190));

        walkableZones.add(mi);

        WalkableZone mj = new WalkableZone(new LatLng(49.809085, -97.141677), new LatLng(49.809220, -97.141251), new LatLng(49.809046, -97.141643), new LatLng(49.809190, -97.141228));
        mj.setRight(mi.getLeft());
        mj.setLeft(new LatLng(49.809065, -97.141658));

        walkableZones.add(mj);

        WalkableZone mk = new WalkableZone(new LatLng(49.809066, -97.141767), new LatLng(49.809100, -97.141685), new LatLng(49.809019, -97.141760), new LatLng(49.809043, -97.141642));
        mk.setRight(mj.getLeft());
        mk.setBottom(new LatLng(49.809043, -97.141690));

        walkableZones.add(mk);

        WalkableZone ml = new WalkableZone(new LatLng(49.809019, -97.141760), new LatLng(49.809043, -97.141642), new LatLng(49.808544, -97.141359), new LatLng(49.808572, -97.141253));
        ml.setTop(mk.getBottom());
        ml.setBottom(new LatLng(49.808576, -97.141313));

        walkableZones.add(ml);

        WalkableZone mm = new WalkableZone(new LatLng(49.808544, -97.141359), new LatLng(49.808572, -97.141253), new LatLng(49.808465, -97.141285), new LatLng(49.808486, -97.141209));
        mm.setTop(ml.getBottom());
        mm.setBottom(new LatLng(49.808472, -97.141241));

        walkableZones.add(mm);

        WalkableZone mn = new WalkableZone(new LatLng(49.808465, -97.141285), new LatLng(49.808486, -97.141209), new LatLng(49.808287, -97.141158), new LatLng(49.808308, -97.141058));
        mn.setTop(mm.getBottom());
        mn.setBottom(new LatLng(49.808308, -97.141107));

        addEntrance(resources.getString(R.string.sb_uni_at_dysart), mn.getBottom());

        walkableZones.add(mn);

        WalkableZone mo = new WalkableZone(new LatLng(49.808287, -97.141158), new LatLng(49.808308, -97.141058), new LatLng(49.808249, -97.141107), new LatLng(49.808272, -97.141024));
        mo.setTop(mn.getBottom());
        mo.setBottom(new LatLng(49.808259, -97.141061));
        mo.setRight(new LatLng(49.808292, -97.141035));

        walkableZones.add(mo);

        WalkableZone mp = new WalkableZone(new LatLng(49.808309, -97.141062), new LatLng(49.808464, -97.140590), new LatLng(49.808283, -97.141033), new LatLng(49.808441, -97.140566));
        mp.setLeft(mo.getRight());
        mp.setRight(new LatLng(49.808453, -97.140575));

        walkableZones.add(mp);

        WalkableZone mq = new WalkableZone(new LatLng(49.808470, -97.140600), new LatLng(49.808485, -97.140544), new LatLng(49.808431, -97.140573), new LatLng(49.808447, -97.140528));
        mq.setLeft(mp.getRight());
        mq.setBottom(new LatLng(49.808448, -97.140574));
        mq.setTop(new LatLng(49.808467, -97.140573));

        walkableZones.add(mq);

        WalkableZone mr = new WalkableZone(new LatLng(49.809193, -97.141246), new LatLng(49.809210, -97.141155), new LatLng(49.808470, -97.140600), new LatLng(49.808485, -97.140544));
        mr.setTop(mi.getBottom());
        mr.setBottom(mq.getTop());

        walkableZones.add(mr);

        WalkableZone ms = new WalkableZone(new LatLng(49.808435, -97.140581), new LatLng(49.808454, -97.140535), new LatLng(49.807370, -97.139773), new LatLng(49.807372, -97.139694));
        ms.setTop(mq.getBottom());
        ms.setBottom(new LatLng(49.807369, -97.139737));

        walkableZones.add(ms);

        WalkableZone mt = new WalkableZone(new LatLng(49.807370, -97.139773), new LatLng(49.807372, -97.139694), new LatLng(49.807332, -97.139735), new LatLng(49.807348, -97.139657));
        mt.setTop(ms.getBottom());
        mt.setLeft(new LatLng(49.807350, -97.139741));
        mt.setBottom(new LatLng(49.807343, -97.139702));

        walkableZones.add(mt);

        WalkableZone mu = new WalkableZone(new LatLng(49.807332, -97.139735), new LatLng(49.807365, -97.139626), new LatLng(49.807266, -97.139687), new LatLng(49.807299, -97.139565));
        mu.setTop(mt.getBottom());
        mu.setBottom(gh.getTop());

        walkableZones.add(mu);

        WalkableZone mv = new WalkableZone(new LatLng(49.807300, -97.140240), new LatLng(49.807366, -97.139750), new LatLng(49.807264, -97.140217), new LatLng(49.807329, -97.139722));
        mv.setRight(mt.getLeft());
        mv.setLeft(new LatLng(49.807289, -97.140227));

        walkableZones.add(mv);

        WalkableZone mw = new WalkableZone(new LatLng(49.807336, -97.140459), new LatLng(49.807383, -97.140313), new LatLng(49.807198, -97.140392), new LatLng(49.807264, -97.140217));
        mw.setRight(mv.getLeft());
        mw.setBottom(new LatLng(49.807242, -97.140251));
        mw.setTop(new LatLng(49.807301, -97.140341));

        walkableZones.add(mw);

        WalkableZone mx = new WalkableZone(new LatLng(49.808270, -97.141125), new LatLng(49.808289, -97.141029), new LatLng(49.807192, -97.140751), new LatLng(49.807297, -97.140307));
        mx.setBottom(mw.getTop());
        mx.setTop(mo.getBottom());

        walkableZones.add(mx);

        WalkableZone my = new WalkableZone(new LatLng(49.807238, -97.140276), new LatLng(49.807257, -97.140220), new LatLng(49.807039, -97.140116), new LatLng(49.807055, -97.140073));
        my.setTop(mw.getBottom());
        my.setBottom(new LatLng(49.807045, -97.140097));

        walkableZones.add(my);

        WalkableZone mz = new WalkableZone(new LatLng(49.807023, -97.140165), new LatLng(49.807067, -97.140046), new LatLng(49.806971, -97.140072), new LatLng(49.806974, -97.139971));
        mz.setTop(my.getBottom());
        mz.setBottom(new LatLng(49.807001, -97.140071));
        mz.setRight(new LatLng(49.807019, -97.140016));

        walkableZones.add(mz);

        WalkableZone na = new WalkableZone(new LatLng(49.807039, -97.140032), new LatLng(49.807182, -97.139595), new LatLng(49.807002, -97.140004), new LatLng(49.807144, -97.139572));
        na.setLeft(mz.getRight());
        na.setRight(gg.getLeft());

        walkableZones.add(na);

        WalkableZone nb = new WalkableZone(new LatLng(49.806971, -97.140270), new LatLng(49.807016, -97.140054), new LatLng(49.806834, -97.140136), new LatLng(49.806879, -97.139939));
        nb.setTop(mz.getBottom());
        nb.setLeft(new LatLng(49.806838, -97.140092));

        walkableZones.add(nb);

        WalkableZone nc = new WalkableZone(new LatLng(49.806828, -97.140143), new LatLng(49.806859, -97.140047), new LatLng(49.806572, -97.139930), new LatLng(49.806588, -97.139871));
        nc.setRight(nb.getLeft());
        nc.setBottom(new LatLng(49.806577, -97.139928));
        nc.setLeft(new LatLng(49.806686, -97.140031));

        walkableZones.add(nc);

        WalkableZone nd = new WalkableZone(new LatLng(49.806535, -97.140045), new LatLng(49.806580, -97.139891), new LatLng(49.806514, -97.140024), new LatLng(49.806554, -97.139885));
        nd.setTop(nc.getBottom());
        nd.setLeft(new LatLng(49.806525, -97.140033));
        nd.setBottom(new LatLng(49.806543, -97.139936));

        walkableZones.add(nd);

        WalkableZone ne = new WalkableZone(new LatLng(49.806527, -97.140086), new LatLng(49.806543, -97.140029), new LatLng(49.806283, -97.139905), new LatLng(49.806306, -97.139846));
        ne.setRight(nd.getLeft());
        ne.setBottom(new LatLng(49.806303, -97.139877));

        walkableZones.add(ne);

        WalkableZone nf = new WalkableZone(new LatLng(49.806335, -97.139855), new LatLng(49.806257, -97.139642), new LatLng(49.806289, -97.139926), new LatLng(49.806172, -97.139687));
        nf.setTop(ne.getBottom());
        nf.setRight(welcome_centre_ent.getLeft());

        walkableZones.add(nf);

        //First half student parking
        WalkableZone ng = new WalkableZone(new LatLng(49.805581, -97.143818), new LatLng(49.806833, -97.140140), new LatLng(49.805411, -97.143679), new LatLng(49.806571, -97.139928));
        ng.setRight(nc.getLeft());

        walkableZones.add(ng);

        WalkableZone nh = new WalkableZone(new LatLng(49.806238, -97.139541), new LatLng(49.806217, -97.139437), new LatLng(49.806182, -97.139562), new LatLng(49.806160, -97.139393));
        nh.setLeft(welcome_centre_ent.getRight());
        nh.setRight(new LatLng(49.806189, -97.139416));

        walkableZones.add(nh);

        WalkableZone ni = new WalkableZone(new LatLng(49.806217, -97.139437), new LatLng(49.806226, -97.139410), new LatLng(49.806160, -97.139393), new LatLng(49.806166, -97.139361));
        ni.setTop(new LatLng(49.806220, -97.139421));
        ni.setLeft(nh.getRight());
        ni.setBottom(new LatLng(49.806163, -97.139376));

        walkableZones.add(ni);

        WalkableZone nj = new WalkableZone(new LatLng(49.806159, -97.139387), new LatLng(49.806165, -97.139362), new LatLng(49.805959, -97.139229), new LatLng(49.805973, -97.139150));
        nj.setTop(ni.getBottom());
        nj.setBottom(new LatLng(49.805972, -97.139203));

        walkableZones.add(nj);

        WalkableZone nk = new WalkableZone(new LatLng(49.805964, -97.139236), new LatLng(49.805973, -97.139150), new LatLng(49.805876, -97.139184), new LatLng(49.805908, -97.139117));
        nk.setTop(nj.getBottom());
        nk.setRight(new LatLng(49.805959, -97.139146));
        nk.setBottom(new LatLng(49.805896, -97.139161));
        nk.setLeft(new LatLng(49.805907, -97.139193));

        walkableZones.add(nk);

        WalkableZone nl = new WalkableZone(new LatLng(49.805810, -97.139522), new LatLng(49.805916, -97.139223), new LatLng(49.805768, -97.139490), new LatLng(49.805876, -97.139184));
        nl.setRight(nk.getLeft());
        nl.setLeft(new LatLng(49.805799, -97.139512));

        walkableZones.add(nl);

        WalkableZone nm = new WalkableZone(new LatLng(49.805773, -97.139664), new LatLng(49.805814, -97.139522), new LatLng(49.805722, -97.139621), new LatLng(49.805771, -97.139489));
        nm.setRight(nl.getLeft());

        addEntrance(resources.getString(R.string.wb_dafoe_at_uni), nm.getRight());
        walkableZones.add(nm);

        //Student parking other half
        WalkableZone nn = new WalkableZone(new LatLng(49.805364, -97.143656), new LatLng(49.806539, -97.140053), new LatLng(49.804643, -97.143086), new LatLng(49.805826, -97.139527));
        nn.setRight(welcome_centre_ent.getLeft());

        walkableZones.add(nn);

        WalkableZone no = new WalkableZone(new LatLng(49.806455, -97.139634), new LatLng(49.806464, -97.139600), new LatLng(49.806216, -97.139439), new LatLng(49.806225, -97.139410));
        no.setBottom(ni.getTop());
        no.setTop(new LatLng(49.806460, -97.139616));

        walkableZones.add(no);

        WalkableZone np = new WalkableZone(new LatLng(49.806463, -97.139655), new LatLng(49.806488, -97.139616), new LatLng(49.806455, -97.139634), new LatLng(49.806464, -97.139600));
        np.setBottom(no.getTop());
        np.setLeft(new LatLng(49.806475, -97.139646));

        walkableZones.add(np);

        WalkableZone nq = new WalkableZone(new LatLng(49.806551, -97.139905), new LatLng(49.806491, -97.139636), new LatLng(49.806523, -97.139972), new LatLng(49.806461, -97.139643));
        nq.setRight(np.getLeft());
        nq.setLeft(nd.getBottom());

        walkableZones.add(nq);

        WalkableZone nr = new WalkableZone(new LatLng(49.805973, -97.139165), new LatLng(49.806076, -97.138695), new LatLng(49.805946, -97.139141), new LatLng(49.806049, -97.138677));
        nr.setLeft(nk.getRight());
        nr.setRight(fw.getLeft());

        walkableZones.add(nr);

        WalkableZone ns = new WalkableZone(new LatLng(49.805805, -97.139135), new LatLng(49.805815, -97.139066), new LatLng(49.805776, -97.139094), new LatLng(49.805788, -97.139023));
        ns.setLeft(new LatLng(49.805793, -97.139116));
        ns.setTop(new LatLng(49.805805, -97.139096));
        ns.setRight(new LatLng(49.805800, -97.139047));

        walkableZones.add(ns);

        WalkableZone nt = new WalkableZone(new LatLng(49.805877, -97.139184), new LatLng(49.805907, -97.139138), new LatLng(49.805803, -97.139130), new LatLng(49.805815, -97.139066));
        nt.setBottom(ns.getTop());
        nt.setTop(nk.getBottom());

        walkableZones.add(nt);

        WalkableZone nu = new WalkableZone(new LatLng(49.805720, -97.139413), new LatLng(49.805811, -97.139117), new LatLng(49.805695, -97.139384), new LatLng(49.805780, -97.139088));
        nu.setRight(ns.getLeft());

        addEntrance(resources.getString(R.string.eb_dafoe_at_uni), nu.getRight());
        walkableZones.add(nu);

        WalkableZone nv = new WalkableZone(new LatLng(49.805812, -97.139074), new LatLng(49.805978, -97.138583), new LatLng(49.805785, -97.139032), new LatLng(49.805945, -97.138546));
        nv.setLeft(ns.getRight());
        nv.setRight(fu.getLeft());

        walkableZones.add(nv);

        WalkableZone nw = new WalkableZone(new LatLng(49.811083, -97.133150), new LatLng(49.811263, -97.132652), new LatLng(49.810990, -97.133071), new LatLng(49.811163, -97.132573));
        nw.setRight(duff_roblin_north_ent.getLeft());
        nw.setLeft(aq.getRight());

        walkableZones.add(nw);

        WalkableZone nx = new WalkableZone(new LatLng(49.811323, -97.132494), new LatLng(49.811812, -97.131159), new LatLng(49.811292, -97.132453), new LatLng(49.811738, -97.131176));
        nx.setLeft(nw.getRight());
        nx.setRight(robson.getLeft());

        walkableZones.add(nx);

        armesIndoorConnections = new ArmesIndoorConnections(resources);

        IndoorVertex nwArmesEnt = armesIndoorConnections.getNorthWestEntrance();
        allen_armes_parker.connectToRight(nwArmesEnt);

        IndoorVertex swArmesEnt = armesIndoorConnections.getSouthWestEntrance();
        allen_armes.connectToRight(swArmesEnt);

        machrayIndoorConnections = new MachrayIndoorConnections(resources);

        IndoorVertex machrayEnt = machrayIndoorConnections.getExit();
        armes_machray.connectToTop(machrayEnt);

        allenIndoorConnections = new AllenIndoorConnections(resources);

        connectBuildingsTogether();
    }

    private void connectBuildingsTogether()
    {
        //Allen to Armes
        allenIndoorConnections.getArmesTunnelConnection().connectVertex(armesIndoorConnections.getAllenConnectionTunnel());
        allenIndoorConnections.getArmesNorthConnection().connectVertex(armesIndoorConnections.getAllenConnectionNorth());
        allenIndoorConnections.getArmesSouthConnection().connectVertex(armesIndoorConnections.getAllenConnectionSouth());

        //Armes to Machray
        armesIndoorConnections.getMachrayConnectionTunnel().connectVertex(machrayIndoorConnections.getArmesConnectionTunnel());
        armesIndoorConnections.getMachrayConnectionNorth().connectVertex(machrayIndoorConnections.getArmesConnectionNorth());
        armesIndoorConnections.getMachrayConnectionSouth().connectVertex(machrayIndoorConnections.getArmesConnectionSouth());

    }
}
