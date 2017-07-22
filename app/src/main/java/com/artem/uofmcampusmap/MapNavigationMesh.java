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
        populationMesh();
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
            //todo split this up into seperate sections (aka walkable zones) to speed up checking the location and then
            // just use LatLngBounds if the location is within the zone and then use a contains to make it do the check for me
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

        //Wait for the seperate route threads to finish, then combine the routes all together
        try
        {
            thread1.join();
            thread2.join();
            thread3.join();

            //Combine all the seperate routes into one single route
            if (firstRoutePart != null)
            {
                firstRoutePart.combineRoutes(secondRoutePart);
                route = firstRoutePart;
            }
            else
            {
                route = secondRoutePart;
            }

            route.combineRoutes(lastRoutePart);
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

        if(startRoomVertex != null && endRoomVertex != null)
        {
            //Find the closest stairs to the starting room, then find a route room to stairs
            startClosestStairs = closestStairsToRoom(startBuilding, startRoomVertex);

            //Start room -> closest stairs
            Thread thread1 = new Thread()
            {
                @Override
                public void run() {
                    firstRoutePart = routeFinder.findRoute(startRoomVertex, startClosestStairs);
                }
            };

            thread1.start();

            //Find the closest stairs to the destination room, create a route from the stairs to the room
            endClosestStairs = closestStairsToRoom(endBuilding, endRoomVertex);

            //Stairs on same floor as end room -> end room
            Thread thread2 = new Thread()
            {
                @Override
                public void run()
                {
                    lastRoutePart = routeFinder.findRoute(endClosestStairs, endRoomVertex);
                }
            };

            thread2.start();

            //Stairs that will connect to the closest stairs, that will now be on the same level as the tunnels
            startTunnelFloorStairs = startClosestStairs.findStairsConnection(TUNNELS_FLOOR);

            //Stairs that will connect from the tunnel floor, to the destination rooms floor
            endTunnelFloorStairs = endClosestStairs.findStairsConnection(TUNNELS_FLOOR);

            //Start building stairs at tunnel -> end building stairs at tunnel
            Thread thread3 = new Thread()
            {
                @Override
                public void run()
                {
                    secondRoutePart  = routeFinder.findRoute(startTunnelFloorStairs, endTunnelFloorStairs);
                }
            };

            thread3.start();

            //Wait for the seperate route threads to finish, then combine the routes all together
            try
            {
                thread1.join();
                thread2.join();
                thread3.join();

                //Combine the 3 parts of the route into one single Route
                route = firstRoutePart;
                route.combineRoutes(secondRoutePart);
                route.combineRoutes(lastRoutePart);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
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

    private void populateMesh()
    {
        //Bottom is the entrancce
        WalkableZone agri_engineer_north_ent = new WalkableZone(new LatLng(49.807551, -97.133870), new LatLng(49.807568, -97.133802), new LatLng(49.807471, -97.133811), new LatLng(49.807485, -97.133747));
        agri_engineer_north_ent.setTop(new LatLng(49.807573, -97.133832));
        agri_engineer_north_ent.setBottom(new LatLng(49.807491, -97.133790));

        addEntrance("Agricultural Engineering", agri_engineer_north_ent.getBottom());
        walkableZones.add(agri_engineer_north_ent);

        WalkableZone a = new WalkableZone(new LatLng(49.807610, -97.133883), new LatLng(49.807633, -97.133787), new LatLng(49.807551, -97.133870), new LatLng(49.807568, -97.133802));
        a.setLeft(new LatLng(49.807577, -97.133863));
        a.setRight(new LatLng(49.807589, -97.133770));
        a.setBottom(agri_engineer_north_ent.getTop());

        //Bottom is the entrance
        WalkableZone agri_engineer_south_ent = new WalkableZone(new LatLng(49.807450, -97.134213), new LatLng(49.807464, -97.134139), new LatLng(49.807396, -97.134163), new LatLng(49.807409, -97.134103));
        agri_engineer_south_ent.setTop(new LatLng(49.807460, -97.134169));
        agri_engineer_south_ent.setBottom(new LatLng(49.807406, -97.134132));

        addEntrance("Agricultural Engineering", agri_engineer_south_ent.getBottom());
        walkableZones.add(agri_engineer_south_ent);

        WalkableZone b = new WalkableZone(new LatLng(49.807475, -97.134234), new LatLng(49.807495, -97.134172), new LatLng(49.807450, -97.134213), new LatLng(49.807464, -97.134139));
        b.setLeft(new LatLng(49.807456, -97.134232));
        b.setRight(new LatLng(49.807472, -97.134153));
        b.setBottom(agri_engineer_south_ent.getTop());

        //Bottom is the entrance
        WalkableZone agriculture_north_ent = new WalkableZone(new LatLng(49.807129, -97.135141), new LatLng(49.807152, -97.135075 ), new LatLng(49.807080, -97.135103), new LatLng(49.807106, -97.135021));
        agriculture_north_ent.setBottom(new LatLng(49.807100, -97.135065));
        agriculture_north_ent.setTop(new LatLng(49.807138, -97.135099));

        addEntrance("Agriculture", agriculture_north_ent.getBottom());
        walkableZones.add(agriculture_north_ent);

        WalkableZone c = new WalkableZone(new LatLng(49.807164, -97.135187), new LatLng(49.807193, -97.135104), new LatLng(49.807129, -97.135141), new LatLng(49.807152, -97.135075));
        c.setLeft(new LatLng(49.807141, -97.135138));
        c.setRight(new LatLng(49.807172, -97.135082));
        c.setBottom(agriculture_north_ent.getTop());

        //Bottom is the entrance
        WalkableZone agriculture_south_ent = new WalkableZone(new LatLng(49.806989, -97.135595), new LatLng(49.807014, -97.135512), new LatLng(49.806936, -97.135529), new LatLng(49.806952, -97.135486));
        agriculture_south_ent.setTop(new LatLng(49.807007, -97.135541));
        agriculture_south_ent.setBottom(new LatLng(49.806958, -97.135498));

        addEntrance("Agriculture", agriculture_south_ent.getBottom());
        walkableZones.add(agriculture_south_ent);

        WalkableZone d = new WalkableZone(new LatLng(49.807019, -97.135584), new LatLng(49.807036, -97.135521), new LatLng(49.806989, -97.135595), new LatLng(49.807014, -97.135512));
        d.setLeft(new LatLng(49.806997, -97.135575));
        d.setRight(new LatLng(49.807014, -97.135511));
        d.setBottom(agriculture_south_ent.getTop());

        //Right is entrance
        WalkableZone allen_armes_parker = new WalkableZone(new LatLng(49.810959, -97.134638), new LatLng(49.811028, -97.134368), new LatLng(49.810859, -97.134555), new LatLng(49.810940, -97.134297));
        allen_armes_parker.setLeft(new LatLng(49.810950, -97.134629));
        allen_armes_parker.setRight(new LatLng(49.810993, -97.134347));

        addEntrance("Allen", allen_armes_parker.getRight());
        addEntrance("Armes", allen_armes_parker.getRight());
        addEntrance("Parker", allen_armes_parker.getRight());
        walkableZones.add(allen_armes_parker);

        //right is entrance
        WalkableZone allen_armes = new WalkableZone(new LatLng(49.810577, -97.134319), new LatLng(49.810651, -97.134069), new LatLng(49.810489, -97.134233), new LatLng(49.810576, -97.134008));
        allen_armes.setRight(new LatLng(49.810614, -97.134037));
        allen_armes.setLeft(new LatLng(49.810533, -97.134257));

        addEntrance("Allen", allen_armes.getRight());
        addEntrance("Armes", allen_armes.getRight());
        walkableZones.add(allen_armes);

        WalkableZone e = new WalkableZone(new LatLng(49.810533, -97.134452), new LatLng(49.810577, -97.134319), new LatLng(49.810454, -97.134363), new LatLng(49.810489, -97.134233));
        e.setBottom(new LatLng(49.810471, -97.134296));
        e.setLeft(new LatLng(49.810482, -97.134379));
        e.setRight(allen_armes.getLeft());

        //bottomMiddle is entrance
        WalkableZone animal_sci_north_ent = new WalkableZone(new LatLng(49.806251, -97.137677), new LatLng(49.806283, -97.137574), new LatLng(49.806171, -97.137650), new LatLng(49.806203, -97.137552));
        animal_sci_north_ent.setBottom(new LatLng(49.806214, -97.137599));
        animal_sci_north_ent.setTop(new LatLng(49.806275, -97.137608));

        addEntrance("Animal Science / Entomology", animal_sci_north_ent.getBottom());
        walkableZones.add(animal_sci_north_ent);

        WalkableZone f = new WalkableZone(new LatLng(49.806270, -97.137785), new LatLng(49.806324, -97.137599), new LatLng(49.806251, -97.137677), new LatLng(49.806283, -97.137574));
        f.setLeft(new LatLng(49.806251, -97.137814));
        f.setRight(new LatLng(49.806315, -97.137570));
        f.setTop(animal_sci_north_ent.getBottom());

        //bottomMiddle is entrance
        WalkableZone animal_sci_south_ent = new WalkableZone(new LatLng(49.806055, -97.138194), new LatLng(49.806088, -97.138104), new LatLng(49.806023, -97.138121), new LatLng(49.806044, -97.138058));
        animal_sci_south_ent.setTop(new LatLng(49.806080, -97.138134));
        animal_sci_south_ent.setBottom(new LatLng(49.806032, -97.138085));

        addEntrance("Animal Science / Entomology", animal_sci_south_ent.getBottom());
        walkableZones.add(animal_sci_south_ent);

        WalkableZone g = new WalkableZone(new LatLng(49.806110, -97.138247), new LatLng(49.806138, -97.138146), new LatLng(49.806055, -97.138194), new LatLng(49.806088, -97.138104));
        g.setLeft(new LatLng(49.806112, -97.138202));
        g.setRight(new LatLng(49.806138, -97.138101));
        g.setBottom(animal_sci_south_ent.getTop());

        //bottom is entrance
        WalkableZone archi2_north_ent = new WalkableZone(new LatLng(49.808076, -97.136340), new LatLng(49.808090, -97.136241), new LatLng(49.807958, -97.136240), new LatLng(49.807985, -97.136136));
        archi2_north_ent.setTop(new LatLng(49.808076, -97.136310));
        archi2_north_ent.setRight(new LatLng(49.808017, -97.136158));
        archi2_north_ent.setBottom(new LatLng(49.807970, -97.136214));

        addEntrance("Architecture 2", archi2_north_ent.getBottom());
        walkableZones.add(archi2_north_ent);

        //top is entrance
        WalkableZone archi2_south_ent = new WalkableZone(new LatLng(49.807778, -97.136417), new LatLng(49.807816, -97.136319), new LatLng(49.807708, -97.136326), new LatLng(49.807741, -97.136225));
        archi2_south_ent.setTop(new LatLng(49.807786, -97.136334));
        archi2_south_ent.setBottom(new LatLng(49.807725, -97.136278));

        addEntrance("Architecture 2", archi2_south_ent.getBottom());
        walkableZones.add(archi2_south_ent);

        //top is entrance
        WalkableZone armes_machray = new WalkableZone(new LatLng(49.810977, -97.133496), new LatLng(49.811021, -97.133344), new LatLng(49.810778, -97.133302), new LatLng(49.810819, -97.133178));
        armes_machray.setTop(new LatLng(49.810997, -97.133403));
        armes_machray.setRight(new LatLng(49.811005, -97.133292));

        addEntrance("Armes", armes_machray.getTop());
        addEntrance("Machray", armes_machray.getTop());
        walkableZones.add(armes_machray);

        WalkableZone h = new WalkableZone(new LatLng(49.811021, -97.133344), new LatLng(49.811063, -97.133218), new LatLng(49.810819, -97.133178), new LatLng(49.810857, -97.133063));
        h.setRight(new LatLng(49.811023, -97.133166));
        h.setLeft(armes_machray.getLeft());

        //right is entrance
        WalkableZone artlab = new WalkableZone(new LatLng(49.808630, -97.130596), new LatLng(49.808680, -97.130466), new LatLng(49.808323, -97.130325), new LatLng(49.808332, -97.130279));
        artlab.setTop(new LatLng(49.808661, -97.130542));
        artlab.setRight(new LatLng(49.808648, -97.130393));
        artlab.setBottom(new LatLng(49.808326, -97.130296));

        addEntrance("Artlab", artlab.getRight());
        walkableZones.add(artlab);

        //left is entrance
        WalkableZone bio_science_east_ent = new WalkableZone(new LatLng(49.810327, -97.134509), new LatLng(49.810361, -97.134390), new LatLng(49.810240, -97.134439), new LatLng(49.810274, -97.134310));
        bio_science_east_ent.setTop(new LatLng(49.810332, -97.134465));
        bio_science_east_ent.setLeft(new LatLng(49.810287, -97.134455));
        bio_science_east_ent.setRight(new LatLng(49.810312, -97.134349));

        addEntrance("Biological Science", bio_science_east_ent.getLeft());
        walkableZones.add(bio_science_east_ent);

        //right is entrance
        WalkableZone bio_science_west_ent = new WalkableZone(new LatLng(49.810106, -97.135083), new LatLng(49.810126, -97.135071), new LatLng(49.810045, -97.135043), new LatLng(49.810066, -97.135023));
        bio_science_west_ent.setTop(new LatLng(49.810110, -97.135071));
        bio_science_west_ent.setRight(new LatLng(49.810090, -97.135044));

        addEntrance("Biological Science", bio_science_west_ent.getRight());
        walkableZones.add(bio_science_west_ent);

        //top is entrance
        WalkableZone buller_west_ent = new WalkableZone(new LatLng(49.810325, -97.133594), new LatLng(49.810342, -97.133531), new LatLng(49.810283, -97.133552), new LatLng(49.810300, -97.133482));
        buller_west_ent.setTop(new LatLng(49.810327, -97.133548));
        buller_west_ent.setBottom(new LatLng(49.810288, -97.133519));

        addEntrance("Buller", buller_west_ent.getTop());
        walkableZones.add(buller_west_ent);

        //top is entrance
        WalkableZone buller_east_ent = new WalkableZone(new LatLng(49.810452, -97.133216), new LatLng(49.810471, -97.133141), new LatLng(49.810408, -97.133167), new LatLng(49.810427, -97.133103));
        buller_east_ent.setTop(new LatLng(49.810459, -97.133175));
        buller_east_ent.setBottom(new LatLng(49.810418, -97.133134));

        addEntrance("Buller", buller_east_ent.getTop());
        walkableZones.add(buller_east_ent);

        //bottom
        WalkableZone dairy_science = new WalkableZone(new LatLng(49.807686, -97.133389), new LatLng(49.807705, -97.133314), new LatLng(49.807632, -97.133349), new LatLng(49.807653, -97.133286));
        dairy_science.setBottom(new LatLng(49.807654, -97.133320));
        dairy_science.setTop(new LatLng(49.807702, -97.133360));

        addEntrance("Dairy Science", dairy_science.getBottom());
        walkableZones.add(dairy_science);

        WalkableZone j = new WalkableZone(new LatLng(49.807725, -97.133415), new LatLng(49.807743, -97.133356), new LatLng(49.807686, -97.133389), new LatLng(49.807705, -97.133314));
        j.setBottom(dairy_science.getTop());
        j.setLeft(new LatLng(49.807709, -97.133405));
        j.setRight(new LatLng(49.807729, -97.133342));

        //bottom
        WalkableZone drake = new WalkableZone(new LatLng(49.808328, -97.130346), new LatLng(49.808348, -97.130283), new LatLng(49.808202, -97.130248), new LatLng(49.808226, -97.130175));
        drake.setTop(artlab.getBottom());
        drake.setBottom(new LatLng(49.808213, -97.130217));

        addEntrance("Drake Centre", drake.getBottom());
        walkableZones.add(drake);

        //right
        WalkableZone duff_roblin_west_ent = new WalkableZone(new LatLng(49.810884, -97.133065), new LatLng(49.810902, -97.133002), new LatLng(49.810840, -97.133062), new LatLng(49.810861, -97.132970));
        duff_roblin_west_ent.setRight(new LatLng(49.810894, -97.132999));
        duff_roblin_west_ent.setBottom(new LatLng(49.810849, -97.132993));
        duff_roblin_west_ent.setTop(new LatLng(49.810888, -97.133021));

        addEntrance("Duff Roblin", duff_roblin_west_ent.getRight());
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

        addEntrance("Duff Roblin", duff_roblin_south_ent.getTop());
        walkableZones.add(duff_roblin_south_ent);

        //top
        WalkableZone education_south_ent = new WalkableZone(new LatLng(49.808603, -97.136485), new LatLng(49.808638, -97.136368), new LatLng(49.808546, -97.136412), new LatLng(49.808578, -97.136300));
        education_south_ent.setTop(new LatLng(49.808624, -97.136406));
        education_south_ent.setRight(new LatLng(49.808612, -97.136333));

        addEntrance("Education", education_south_ent.getTop());
        walkableZones.add(education_south_ent);

        //right
        WalkableZone education_west_ent = new WalkableZone(new LatLng(49.808459, -97.137405), new LatLng(49.808490, -97.137321), new LatLng(49.808391, -97.137367), new LatLng(49.808426, -97.137259));
        education_west_ent.setRight(new LatLng(49.808452, -97.137281));
        education_west_ent.setBottom(new LatLng(49.808409, -97.137312));

        addEntrance("Education", education_west_ent.getRight());
        walkableZones.add(education_west_ent);

        //left
        WalkableZone education_north_ent = new WalkableZone(new LatLng(49.809193, -97.136623), new LatLng(49.809237, -97.136483), new LatLng(49.809045, -97.136514), new LatLng(49.809091, -97.136383));
        education_north_ent.setLeft(new LatLng(49.809142, -97.136583));
        education_north_ent.setRight(new LatLng(49.809199, -97.136452));

        //todo turn this on later once inside navigation works
        //addEntrance("Education", education_north_ent.getLeft());
        walkableZones.add(education_north_ent);

        //left
        WalkableZone eitc_e1_east_ent = new WalkableZone(new LatLng(49.808477, -97.133103), new LatLng(49.808573, -97.132806), new LatLng(49.808452, -97.133072), new LatLng(49.808516, -97.132811));
        eitc_e1_east_ent.setLeft(new LatLng(49.808453, -97.133077));
        eitc_e1_east_ent.setRight(new LatLng(49.808540, -97.132805));

        addEntrance("EITC E1", eitc_e1_east_ent.getLeft());
        walkableZones.add(eitc_e1_east_ent);

        WalkableZone m = new WalkableZone(new LatLng(49.808573, -97.132806), new LatLng(49.808586, -97.132744), new LatLng(49.808516, -97.132811), new LatLng(49.808553, -97.132706));
        m.setTop(new LatLng(49.808571, -97.132774));
        m.setBottom(new LatLng(49.808541, -97.132743));
        m.setLeft(eitc_e1_east_ent.getRight());

        //top
        WalkableZone eitc_e1_e3 = new WalkableZone(new LatLng(49.808217, -97.134071), new LatLng(49.808257, -97.133959), new LatLng(49.807822, -97.133792), new LatLng(49.807866, -97.133607));
        eitc_e1_e3.setTop(new LatLng(49.808243, -97.133994));
        eitc_e1_e3.setBottom(new LatLng(49.807835, -97.133736));

        addEntrance("EITC E1", eitc_e1_e3.getTop());
        addEntrance("EITC E3", eitc_e1_e3.getTop());
        walkableZones.add(eitc_e1_e3);

        WalkableZone n = new WalkableZone(new LatLng(49.807822, -97.133792), new LatLng(49.807866, -97.133607), new LatLng(49.807773, -97.133753), new LatLng(49.807830, -97.133600));
        n.setRight(new LatLng(49.807840, -97.133618));
        n.setLeft(new LatLng(49.807797, -97.133749));
        n.setTop(eitc_e1_e3.getBottom());

        //left
        WalkableZone eitc_e1_e2 = new WalkableZone(new LatLng(49.808665, -97.133268), new LatLng(49.808765, -97.132963), new LatLng(49.808637, -97.133238), new LatLng(49.808729, -97.132932));
        eitc_e1_e2.setRight(new LatLng(49.808750, -97.132950));
        eitc_e1_e2.setLeft(new LatLng(49.808648, -97.133257));

        addEntrance("EITC E1", eitc_e1_e2.getLeft());
        addEntrance("EITC E2", eitc_e1_e2.getLeft());
        walkableZones.add(eitc_e1_e2);

        WalkableZone o = new WalkableZone(new LatLng(49.808765, -97.132963), new LatLng(49.808786, -97.132890), new LatLng(49.808729, -97.132932), new LatLng(49.808763, -97.132867));
        o.setBottom(new LatLng(49.808745, -97.132906));
        o.setTop(new LatLng(49.808772, -97.132921));
        o.setLeft(eitc_e1_e2.getRight());

        //bottom
        WalkableZone eitc_e2 = new WalkableZone(new LatLng(49.808984, -97.133486), new LatLng(49.809029, -97.133355), new LatLng(49.808951, -97.133464), new LatLng(49.808962, -97.133295));
        eitc_e2.setTop(new LatLng(49.809005, -97.133424));
        eitc_e2.setRight(new LatLng(49.808996, -97.133327));
        eitc_e2.setBottom(new LatLng(49.808937, -97.133421));

        addEntrance("EITC E2", eitc_e2.getBottom());
        walkableZones.add(eitc_e2);

        //bottom
        WalkableZone eitc_e3 = new WalkableZone(new LatLng(49.808693, -97.134597), new LatLng(49.808714, -97.134514), new LatLng(49.808622, -97.134579), new LatLng(49.808649, -97.134462));
        eitc_e3.setTop(new LatLng(49.808705, -97.134566));
        eitc_e3.setBottom(new LatLng(49.808643, -97.134489));

        addEntrance("EITC E3", eitc_e3.getBottom());
        walkableZones.add(eitc_e3);

        //top
        WalkableZone eli_dafoe = new WalkableZone(new LatLng(49.810011, -97.132205), new LatLng(49.809925, -97.131626), new LatLng(49.809633, -97.131902), new LatLng(49.809799, -97.131580));
        eli_dafoe.setTop(new LatLng(49.809993, -97.132140));
        eli_dafoe.setRight(new LatLng(49.809935, -97.131675));
        eli_dafoe.setBottom(new LatLng(49.809667, -97.131876));

        addEntrance("Elizabeth Dafoe", eli_dafoe.getRight());
        walkableZones.add(eli_dafoe);

        //right
        WalkableZone ext_education_west_ent = new WalkableZone(new LatLng(49.807054, -97.139374), new LatLng(49.807129, -97.139223), new LatLng(49.806972, -97.139307), new LatLng(49.807047, -97.139160));
        ext_education_west_ent.setLeft(new LatLng(49.807031, -97.139354));
        ext_education_west_ent.setRight(new LatLng(49.807073, -97.139269));

        addEntrance("Extended Education", ext_education_west_ent.getRight());
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

        addEntrance("Extended Education", ext_education_east_ent.getLeft());
        walkableZones.add(ext_education_east_ent);

        //right
        WalkableZone fletcher = new WalkableZone(new LatLng(49.809539, -97.131706), new LatLng(49.809621, -97.131453), new LatLng(49.809394, -97.131575), new LatLng(49.809488, -97.131327));
        fletcher.setLeft(new LatLng(49.809476, -97.131635));
        fletcher.setRight(new LatLng(49.809565, -97.131352));

        addEntrance("Fletcher", fletcher.getRight());
        walkableZones.add(fletcher);

        //top
        WalkableZone helen_glass = new WalkableZone(new LatLng(49.808812, -97.135812), new LatLng(49.808858, -97.135535), new LatLng(49.808681, -97.135794), new LatLng(49.808662, -97.135456));
        helen_glass.setTop(new LatLng(49.808810, -97.135583));
        helen_glass.setLeft(new LatLng(49.808760, -97.135817));
        helen_glass.setRight(new LatLng(49.808709, -97.135467));
        helen_glass.setBottom(new LatLng(49.808640, -97.135591));

        addEntrance("Helen Glass", helen_glass.getTop());
        walkableZones.add(helen_glass);

        //right
        WalkableZone human_eco = new WalkableZone(new LatLng(49.810717, -97.132430), new LatLng(49.810746, -97.132365), new LatLng(49.810622, -97.132354), new LatLng(49.810643, -97.132287));
        human_eco.setTop(new LatLng(49.810717, -97.132393));
        human_eco.setRight(new LatLng(49.810700, -97.132333));
        human_eco.setLeft(new LatLng(49.810673, -97.132386));

        addEntrance("Human Ecology", human_eco.getRight());
        walkableZones.add(human_eco);

        //top
        WalkableZone isbister = new WalkableZone(new LatLng(49.809111, -97.130311), new LatLng(49.809136, -97.130213), new LatLng(49.809044, -97.130238), new LatLng(49.809069, -97.130169));
        isbister.setTop(new LatLng(49.809124, -97.130257));
        isbister.setBottom(new LatLng(49.809062, -97.130209));
        isbister.setLeft(new LatLng(49.809061, -97.130252));

        addEntrance("Isbister", isbister.getTop());
        walkableZones.add(isbister);

        //bottom
        WalkableZone parker = new WalkableZone(new LatLng(49.811590, -97.134868), new LatLng(49.811697, -97.134544), new LatLng(49.811560, -97.134857), new LatLng(49.811656, -97.134506));
        parker.setLeft(new LatLng(49.811578, -97.134849));
        parker.setBottom(new LatLng(49.811591, -97.134773));

        addEntrance("Parker", parker.getBottom());
        walkableZones.add(parker);

        //right
        WalkableZone robert_schultz_west_ent = new WalkableZone(new LatLng(49.810117, -97.136969), new LatLng(49.810235, -97.136765), new LatLng(49.810086, -97.136956), new LatLng(49.810187, -97.136725));
        robert_schultz_west_ent.setTop(new LatLng(49.810215, -97.136786));
        robert_schultz_west_ent.setLeft(new LatLng(49.810097, -97.136940));
        robert_schultz_west_ent.setRight(new LatLng(49.810203, -97.136740));

        addEntrance("Robert Schultz Theatre", robert_schultz_west_ent.getRight());
        walkableZones.add(robert_schultz_west_ent);

        //top
        WalkableZone robert_schultz_south_ent = new WalkableZone(new LatLng(49.809909, -97.136491), new LatLng(49.809988, -97.136205), new LatLng(49.809813, -97.136425), new LatLng(49.809910, -97.136148));
        robert_schultz_south_ent.setTop(new LatLng(49.809943, -97.136340));
        robert_schultz_south_ent.setLeft(new LatLng(49.809833, -97.136435));
        robert_schultz_south_ent.setRight( new LatLng(49.809922, -97.136160));

        addEntrance("Robert Schultz Theatre", robert_schultz_south_ent.getTop());
        walkableZones.add(robert_schultz_south_ent);

        //right
        WalkableZone robson = new WalkableZone(new LatLng(49.811811, -97.131165), new LatLng(49.811827, -97.131042), new LatLng(49.811731, -97.131095), new LatLng(49.811738, -97.130961));
        robson.setTop(new LatLng(49.811825, -97.131126));
        robson.setRight(new LatLng(49.811758, -97.130976));

        addEntrance("Robson", robson.getRight());
        walkableZones.add(robson);

        //bot
        WalkableZone russel_north_ent = new WalkableZone(new LatLng(49.808270, -97.135577), new LatLng(49.808300, -97.135478), new LatLng(49.808178, -97.135498), new LatLng(49.808210, -97.135404));
        russel_north_ent.setTop(new LatLng(49.808286, -97.135528));
        russel_north_ent.setBottom(new LatLng(49.808192, -97.135455));

        addEntrance("Russel", russel_north_ent.getBottom());
        walkableZones.add(russel_north_ent);

        //top
        WalkableZone russel_south_ent = new WalkableZone(new LatLng(49.807892, -97.135264), new LatLng(49.807921, -97.135176), new LatLng(49.807418, -97.134878), new LatLng(49.807444, -97.134803));
        russel_south_ent.setTop(new LatLng(49.807905, -97.135216));
        russel_south_ent.setBottom(new LatLng(49.807431, -97.134837));

        addEntrance("Russel", russel_south_ent.getTop());
        walkableZones.add(russel_south_ent);

        //right
        WalkableZone st_johns_west_ent = new WalkableZone(new LatLng(49.810469, -97.137293), new LatLng(49.810568, -97.137053), new LatLng(49.810413, -97.137276), new LatLng(49.810545, -97.137009));
        st_johns_west_ent.setTop(new LatLng(49.810520, -97.137185));
        st_johns_west_ent.setLeft(new LatLng(49.810438, -97.137287));
        st_johns_west_ent.setRight(new LatLng(49.810557, -97.137024));
        st_johns_west_ent.setBottom(new LatLng(49.810486, -97.137138));

        addEntrance("St.Johns College", st_johns_west_ent.getRight());
        walkableZones.add(st_johns_west_ent);

        //bot
        WalkableZone st_johns_north_ent = new WalkableZone(new LatLng(49.810817, -97.137225), new LatLng(49.810850, -97.137101), new LatLng(49.810714, -97.137174), new LatLng(49.810759, -97.137040));
        st_johns_north_ent.setLeft(new LatLng(49.810760, -97.137195));
        st_johns_north_ent.setBottom(new LatLng(49.810749, -97.137065));

        addEntrance("St.Johns College", st_johns_north_ent.getBottom());
        walkableZones.add(st_johns_north_ent);

        //left
        WalkableZone st_johns_east_ent = new WalkableZone(new LatLng(49.811217, -97.136940), new LatLng(49.811264, -97.136789), new LatLng(49.810877, -97.136669), new LatLng(49.810910, -97.136532));
        st_johns_east_ent.setTop(new LatLng(49.811239, -97.136864));
        st_johns_east_ent.setBottom(new LatLng(49.810898, -97.136607));

        addEntrance("St.Johns College", st_johns_east_ent.getLeft());
        walkableZones.add(st_johns_east_ent);

        //bot
        WalkableZone st_pauls_north_ent = new WalkableZone(new LatLng(49.810576, -97.138317), new LatLng(49.810650, -97.138129), new LatLng(49.810397, -97.138124), new LatLng(49.810463, -97.137942));
        st_pauls_north_ent.setTop(new LatLng(49.810584, -97.138266));
        st_pauls_north_ent.setBottom(new LatLng(49.810430, -97.138044));

        addEntrance("St.Pauls College", st_pauls_north_ent.getBottom());
        walkableZones.add(st_pauls_north_ent);

        //left
        WalkableZone st_pauls_east_ent = new WalkableZone(new LatLng(49.809905, -97.137418), new LatLng(49.810040, -97.137095), new LatLng(49.809859, -97.137385), new LatLng(49.810001, -97.137022));
        st_pauls_east_ent.setLeft(new LatLng(49.809888, -97.137377));
        st_pauls_east_ent.setRight(new LatLng(49.810018, -97.137080));
        st_pauls_east_ent.setBottom(new LatLng(49.809914, -97.137222));

        addEntrance("St.Pauls College", st_pauls_east_ent.getLeft());
        walkableZones.add(st_pauls_east_ent);

        //left
        WalkableZone tier_tunnel = new WalkableZone(new LatLng(49.808856, -97.130641), new LatLng(49.808900, -97.130525), new LatLng(49.808780, -97.130590), new LatLng(49.808820, -97.130469));
        tier_tunnel.setRight(new LatLng(49.808851, -97.130514));
        tier_tunnel.setLeft(new LatLng(49.808811, -97.130605));
        tier_tunnel.setTop(new LatLng(49.808867, -97.130581));

        //todo toggle this back on when tier indoor navigation works
        //addEntrance("Tier", tier_tunnel.getTop());
        walkableZones.add(tier_tunnel);

        //right
        WalkableZone tier_west_ent = new WalkableZone(new LatLng(49.809150, -97.131390), new LatLng(49.809223, -97.131163), new LatLng(49.809084, -97.131322), new LatLng(49.809143, -97.131097));
        tier_west_ent.setTop(new LatLng(49.809159, -97.131304));
        tier_west_ent.setLeft(new LatLng(49.809095, -97.131370));
        tier_west_ent.setRight(new LatLng(49.809178, -97.131130));
        tier_west_ent.setBottom(new LatLng(49.809110, -97.131273));

        addEntrance("Tier", tier_west_ent.getRight());
        walkableZones.add(tier_west_ent);

        //top
        WalkableZone tier_south_ent = new WalkableZone(new LatLng(49.808947, -97.130789), new LatLng(49.808961, -97.130703), new LatLng(49.808839, -97.130715), new LatLng(49.808857, -97.130621));
        tier_south_ent.setTop(new LatLng(49.808940, -97.130745));
        tier_south_ent.setLeft(new LatLng(49.808867, -97.130741));
        tier_south_ent.setBottom(new LatLng(49.808847, -97.130678));

        addEntrance("Tier", tier_south_ent.getTop());
        walkableZones.add(tier_south_ent);

        //right
        WalkableZone uni_centre_west_ent = new WalkableZone(new LatLng(49.809210, -97.135372), new LatLng(49.809279, -97.135180), new LatLng(49.808874, -97.135086), new LatLng(49.808933, -97.134911));
        uni_centre_west_ent.setRight(new LatLng(49.809072, -97.135035));
        uni_centre_west_ent.setBottom(new LatLng(49.808910, -97.134955));

        addEntrance("University Centre", uni_centre_west_ent.getRight());
        walkableZones.add(uni_centre_west_ent);

        //left
        WalkableZone uni_centre_east_ent = new WalkableZone(new LatLng(49.809316, -97.133742), new LatLng(49.809422, -97.133583), new LatLng(49.809203, -97.133654), new LatLng(49.809302, -97.133481));
        uni_centre_east_ent.setLeft(new LatLng(49.809291, -97.133712));
        uni_centre_east_ent.setRight(new LatLng(49.809362, -97.133536));

        addEntrance("University Centre", uni_centre_east_ent.getLeft());
        walkableZones.add(uni_centre_east_ent);

        //left
        WalkableZone uni_centre_north_ent = new WalkableZone(new LatLng(49.809717, -97.134074), new LatLng(49.809757, -97.133985), new LatLng(49.809351, -97.133827), new LatLng(49.809370, -97.133664));
        uni_centre_north_ent.setLeft(new LatLng(49.809455, -97.133905));
        uni_centre_north_ent.setRight(new LatLng(49.809491, -97.133766));
        uni_centre_north_ent.setBottom(new LatLng(49.809349, -97.133790));

        addEntrance("University Centre", uni_centre_north_ent.getLeft());
        walkableZones.add(uni_centre_north_ent);

        WalkableZone uni_centre_south_west_ent = new WalkableZone(new LatLng(49.808773, -97.135190), new LatLng(49.808804, -97.135030), new LatLng(49.808773, -97.135073), new LatLng(49.808781, -97.135027));
        uni_centre_south_west_ent.setTop(new LatLng(49.808781, -97.135062));
        uni_centre_south_west_ent.setBottom(new LatLng(49.808778, -97.135052));

        walkableZones.add(uni_centre_south_west_ent);

        //top
        WalkableZone uni_college = new WalkableZone(new LatLng(49.811066, -97.131298), new LatLng(49.811142, -97.131053), new LatLng(49.810810, -97.131114), new LatLng(49.810893, -97.130871));
        uni_college.setTop(new LatLng(49.811055, -97.131105));
        uni_college.setLeft(new LatLng(49.810852, -97.131118));

        addEntrance("University College", uni_college.getTop());
        walkableZones.add(uni_college);

        //top
        WalkableZone wallace = new WalkableZone(new LatLng(49.811675, -97.135725), new LatLng(49.811666, -97.135649), new LatLng(49.811611, -97.135740), new LatLng(49.811599, -97.135649));
        wallace.setTop(new LatLng(49.811658, -97.135693));
        wallace.setBottom(new LatLng(49.811608, -97.135696));

        addEntrance("Wallace", wallace.getTop());
        walkableZones.add(wallace);

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
        aa.setBottom(artlab.getTop());

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
        af.setRight(fletcher.getLeft());
        af.setLeft(new LatLng(49.809454, -97.131758));
        af.setBottom(ad.getTop());

        walkableZones.add(af);

        WalkableZone ag = new WalkableZone(new LatLng(49.809729, -97.131988), new LatLng(49.809759, -97.131866), new LatLng(49.809529, -97.131830), new LatLng(49.809559, -97.131706));
        ag.setTop(eli_dafoe.getBottom());
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
        be.setBottom(eli_dafoe.getTop());

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

    }
    //WalkableZone  = new WalkableZone(new LatLng(), new LatLng(), new LatLng(), new LatLng());

    //Creates all of the WalkableZones sections of the mesh, that are used for finding where the GPS location is coming from
    //Also contains all of the Vertex's for navigation, along with their connections to one another
    private void populationMesh()
    {



        WalkableZone ai = new WalkableZone(new LatLng(49.808272, -97.132776), new LatLng(49.808341, -97.132528), new LatLng(49.808090, -97.132631), new LatLng(49.808176, -97.132409));
        ai.setTop(new LatLng(49.808329, -97.132564));
        ai.setLeft(new LatLng(49.808158, -97.132680));
        ai.setRight(ac.getLeft());
        ai.setBottom(new LatLng(49.808165, -97.132435));

        walkableZones.add(ai);

        WalkableZone aj = new WalkableZone(new LatLng(49.808544, -97.132795), new LatLng(49.808558, -97.132704), new LatLng(49.808312, -97.132602), new LatLng(49.808337, -97.132518));
        aj.setTop(eitc_e1_east_ent.getBottom());
        aj.setBottom(ai.getTop());

        walkableZones.add(aj);

        WalkableZone ak = new WalkableZone(new LatLng(49.808742, -97.132950), new LatLng(49.808766, -97.132859), new LatLng(49.808554, -97.132812), new LatLng(49.808587, -97.132714));
        ak.setTop(eitc_e1_e2.getBottom());
        ak.setBottom(eitc_e1_east_ent.getTop());

        walkableZones.add(ak);

        WalkableZone al = new WalkableZone(new LatLng(49.808168, -97.132468), new LatLng(49.808182, -97.132423), new LatLng(49.808093, -97.132407), new LatLng(49.808111, -97.132359));
        al.setTop(ai.getBottom());
        al.setBottom(new LatLng(49.808102, -97.132385));

        walkableZones.add(al);

        WalkableZone am = new WalkableZone(new LatLng(49.807741, -97.133363), new LatLng(49.808128, -97.132335), new LatLng(49.807706, -97.133334), new LatLng(49.808083, -97.132316));
        am.setLeft(dairy_science.getRight());
        am.setRight(al.getBottom());

        walkableZones.add(am);

        WalkableZone an = new WalkableZone(new LatLng(49.807624, -97.133819), new LatLng(49.807731, -97.133399), new LatLng(49.807581, -97.133805), new LatLng(49.807694, -97.133378));
        an.setLeft(agri_engineer_north_ent.getRight());
        an.setRight(dairy_science.getLeft());

        walkableZones.add(an);

        WalkableZone ao = new WalkableZone(new LatLng(49.807497, -97.134175), new LatLng(49.807590, -97.133892), new LatLng(49.807446, -97.134171), new LatLng(49.807549, -97.133870));
        ao.setLeft(agri_engineer_south_ent.getRight());
        ao.setRight(agri_engineer_north_ent.getLeft());

        walkableZones.add(ao);

        WalkableZone ap = new WalkableZone(new LatLng(49.807198, -97.135103), new LatLng(49.807483, -97.134205), new LatLng(49.807149, -97.135077), new LatLng(49.807446, -97.134198));
        ap.setTop(new LatLng(49.807312, -97.134700));
        ap.setRight(agri_engineer_south_ent.getLeft());
        ap.setLeft(agriculture_north_ent.getRight());

        walkableZones.add(ap);

        WalkableZone aq = new WalkableZone(new LatLng(49.807395, -97.134866), new LatLng(49.807428, -97.134760), new LatLng(49.807296, -97.134727), new LatLng(49.807322, -97.134668));
        aq.setBottom(ap.getTop());
        aq.setTop(new LatLng(49.807408, -97.134821));

        walkableZones.add(aq);

        WalkableZone ar = new WalkableZone(new LatLng(49.807035, -97.135521), new LatLng(49.807170, -97.135176), new LatLng(49.807000, -97.135508), new LatLng(49.807141, -97.135115));
        ar.setLeft(agriculture_south_ent.getRight());
        ar.setRight(agriculture_north_ent.getLeft());

        walkableZones.add(ar);

        WalkableZone as = new WalkableZone(new LatLng(49.806301, -97.137638), new LatLng(49.807022, -97.135584), new LatLng(49.806259, -97.137603), new LatLng(49.806986, -97.135561));
        as.setLeft(animal_sci_north_ent.getRight());
        as.setRight(agriculture_south_ent.getLeft());

        walkableZones.add(as);

        WalkableZone at = new WalkableZone(new LatLng(49.806143, -97.138145), new LatLng(49.806297, -97.137688), new LatLng(49.806125, -97.138120), new LatLng(49.806243, -97.137678));
        at.setLeft(animal_sci_south_ent.getRight());
        at.setRight(animal_sci_north_ent.getLeft());

        walkableZones.add(at);

        WalkableZone au = new WalkableZone(new LatLng(49.805983, -97.138623), new LatLng(49.806133, -97.138192), new LatLng(49.805933, -97.138572), new LatLng(49.806089, -97.138197));
        au.setRight(animal_sci_south_ent.getLeft());
        au.setTop(new LatLng(49.805973, -97.138556));

        walkableZones.add(au);

        WalkableZone aw = new WalkableZone(new LatLng(49.807856, -97.133648), new LatLng(49.808175, -97.132680), new LatLng(49.807816, -97.133633), new LatLng(49.808144, -97.132659));
        aw.setRight(ai.getLeft());
        aw.setLeft(eitc_e1_e3.getRight());

        walkableZones.add(aw);

        WalkableZone ax = new WalkableZone(new LatLng(49.807225, -97.135525), new LatLng(49.807874, -97.133808), new LatLng(49.807176, -97.135508), new LatLng(49.807779, -97.133750));
        ax.setRight(eitc_e1_e3.getLeft());
        ax.setLeft(new LatLng(49.807202, -97.135511));
        ax.setTop(russel_south_ent.getBottom());
        ax.setBottom(aq.getTop());

        walkableZones.add(ax);

        WalkableZone ay = new WalkableZone(new LatLng(49.807201, -97.135700), new LatLng(49.807252, -97.135570), new LatLng(49.807123, -97.135634), new LatLng(49.807180, -97.135489));
        ay.setRight(ax.getLeft());
        ay.setBottom(new LatLng(49.807156, -97.135650));
        ay.setLeft(fac_of_music_south_end.getRight());

        walkableZones.add(ay);

        WalkableZone az = new WalkableZone(new LatLng(49.806223, -97.138543), new LatLng(49.807193, -97.135656), new LatLng(49.806115, -97.138502), new LatLng(49.807135, -97.135619));
        az.setLeft(new LatLng(49.806191, -97.138524));
        az.setRight(ay.getBottom());
        az.setTop(new LatLng(49.807015, -97.136213));

        walkableZones.add(az);

        WalkableZone ba = new WalkableZone(new LatLng(49.806233, -97.138803), new LatLng(49.806306, -97.138689), new LatLng(49.806111, -97.138505), new LatLng(49.806229, -97.138530));
        ba.setRight(az.getLeft());
        ba.setLeft(new LatLng(49.806261, -97.138778));

        walkableZones.add(ba);

        WalkableZone bb = new WalkableZone(new LatLng(49.807059, -97.139510), new LatLng(49.807071, -97.139470), new LatLng(49.805933, -97.138566), new LatLng(49.805953, -97.138531));
        bb.setTop(new LatLng(49.807064, -97.139488));
        bb.setRight(ba.getLeft());
        bb.setBottom(au.getTop());

        walkableZones.add(bb);

        WalkableZone bc = new WalkableZone(new LatLng(49.807073, -97.139467), new LatLng(49.807088, -97.139412), new LatLng(49.807034, -97.139439), new LatLng(49.807053, -97.139378));
        bc.setBottom(ext_education_west_ent.getTop());
        bc.setTop(new LatLng(49.807078, -97.139433));

        walkableZones.add(bc);

        WalkableZone bd = new WalkableZone(new LatLng(49.807179, -97.139606), new LatLng(49.807206, -97.139503), new LatLng(49.807056, -97.139504), new LatLng(49.807085, -97.139402));
        bd.setTop(new LatLng(49.807186, -97.139560));
        bd.setBottom(bb.getTop());
        bd.setRight(bc.getTop());

        walkableZones.add(bd);

        WalkableZone be = new WalkableZone(new LatLng(49.807276, -97.139713), new LatLng(49.807902, -97.137921), new LatLng(49.807171, -97.139606), new LatLng(49.807768, -97.137840));
        be.setRight(new LatLng(49.807877, -97.137920));
        be.setTop(new LatLng(49.807288, -97.139629));
        be.setBottom(bd.getTop());

        walkableZones.add(be);

        WalkableZone bf = new WalkableZone(new LatLng(49.807283, -97.136411), new LatLng(49.807266, -97.136293), new LatLng(49.807003, -97.136300), new LatLng(49.807039, -97.136176));
        bf.setBottom(az.getTop());
        bf.setRight(new LatLng(49.807283, -97.136321));

        walkableZones.add(bf);

        WalkableZone bg = new WalkableZone(new LatLng(49.807359, -97.136397), new LatLng(49.807417, -97.136131), new LatLng(49.807223, -97.136401), new LatLng(49.807338, -97.136022));
        bg.setLeft(bf.getRight());
        bg.setRight(fac_of_music_north_end.getLeft());

        walkableZones.add(bg);

        WalkableZone bh = new WalkableZone(new LatLng(49.807983, -97.138078), new LatLng(49.808087, -97.137764), new LatLng(49.807694, -97.137839), new LatLng(49.807792, -97.137515));
        bh.setLeft(be.getRight());
        bh.setRight(new LatLng(49.807907, -97.137624));
        bh.setBottom(ext_education_east_ent.getTop());

        walkableZones.add(bh);

        WalkableZone bi = new WalkableZone(new LatLng(49.807986, -97.137703), new LatLng(49.808342, -97.136568), new LatLng(49.807756, -97.137598), new LatLng(49.808160, -97.136410));
        bi.setLeft(bh.getRight());
        bi.setRight(new LatLng(49.808271, -97.136575));
        bi.setTop(new LatLng(49.808241, -97.136807));
        bi.setBottom(new LatLng(49.808141, -97.136489));

        walkableZones.add(bi);

        WalkableZone bj = new WalkableZone(new LatLng(49.808373, -97.137459), new LatLng(49.808439, -97.137270), new LatLng(49.808218, -97.136837), new LatLng(49.808260, -97.136711));
        bj.setTop(education_west_ent.getBottom());
        bj.setBottom(bi.getTop());

        walkableZones.add(bj);

        WalkableZone bk = new WalkableZone(new LatLng(49.808318, -97.136638), new LatLng(49.808421, -97.136311), new LatLng(49.808214, -97.136558), new LatLng(49.808306, -97.136218));
        bk.setLeft(bi.getRight());
        bk.setRight(new LatLng(49.808361, -97.136284));

        walkableZones.add(bk);

        WalkableZone bl = new WalkableZone(new LatLng(49.808146, -97.136589), new LatLng(49.808189, -97.136455), new LatLng(49.808060, -97.136346), new LatLng(49.808091, -97.136224));
        bl.setBottom(archi2_north_ent.getTop());
        bl.setTop(bi.getBottom());

        walkableZones.add(bl);

        WalkableZone bm = new WalkableZone(new LatLng(49.808084, -97.136087), new LatLng(49.808084, -97.135914), new LatLng(49.807750, -97.136240), new LatLng(49.807679, -97.136123));
        bm.setTop(new LatLng(49.808059, -97.135999));
        bm.setLeft(archi2_north_ent.getRight());
        bm.setBottom(new LatLng(49.807745, -97.136164));

        walkableZones.add(bm);

        WalkableZone bn = new WalkableZone(new LatLng(49.807725, -97.136381), new LatLng(49.807799, -97.136184), new LatLng(49.807320, -97.136098), new LatLng(49.807386, -97.135893));
        bn.setTop(archi2_south_ent.getBottom());
        bn.setRight(bm.getBottom());
        bn.setBottom(fac_of_music_north_end.getTop());

        walkableZones.add(bn);

        WalkableZone bo = new WalkableZone(new LatLng(49.808272, -97.136020), new LatLng(49.808302, -97.135884), new LatLng(49.808042, -97.136102), new LatLng(49.808033, -97.135948));
        bo.setTop(new LatLng(49.808279, -97.135949));
        bo.setRight(new LatLng(49.808183, -97.135918));
        bo.setBottom(bm.getTop());

        walkableZones.add(bo);

        WalkableZone bp = new WalkableZone(new LatLng(49.808216, -97.135917), new LatLng(49.808318, -97.135581), new LatLng(49.808134, -97.135950), new LatLng(49.808273, -97.135551));
        bp.setLeft(bo.getRight());
        bp.setRight(russel_north_ent.getLeft());
        bp.setTop(new LatLng(49.808280, -97.135670));

        walkableZones.add(bp);

        WalkableZone bq = new WalkableZone(new LatLng(49.808334, -97.135829), new LatLng(49.808472, -97.135767), new LatLng(49.808264, -97.135693), new LatLng(49.808282, -97.135632));
        bq.setBottom(bp.getTop());
        bq.setLeft(new LatLng(49.808392, -97.135795));
        bq.setRight(new LatLng(49.808402, -97.135728));

        walkableZones.add(bq);

        WalkableZone br = new WalkableZone(new LatLng(49.808429, -97.135922), new LatLng(49.808445, -97.135787), new LatLng(49.808267, -97.136002), new LatLng(49.808294, -97.135882));
        br.setLeft(new LatLng(49.808306, -97.136012));
        br.setBottom(bo.getTop());
        br.setRight(bq.getLeft());
        br.setTop(new LatLng(49.808437, -97.135851));

        walkableZones.add(br);

        WalkableZone bs = new WalkableZone(new LatLng(49.808418, -97.136325), new LatLng(49.808433, -97.136190), new LatLng(49.808267, -97.135978), new LatLng(49.808354, -97.135987));
        bs.setRight(br.getLeft());
        bs.setLeft(bk.getRight());
        bs.setTop(new LatLng(49.808419, -97.136262));

        walkableZones.add(bs);

        WalkableZone bt = new WalkableZone(new LatLng(49.808641, -97.136126), new LatLng(49.808529, -97.136070), new LatLng(49.808400, -97.136326), new LatLng(49.808419, -97.136191));
        bt.setBottom(education_south_ent.getBottom());
        bt.setTop(bs.getTop());
        bt.setRight(new LatLng(49.808568, -97.136101));

        walkableZones.add(bt);

        WalkableZone bu = new WalkableZone(new LatLng(49.808591, -97.136154), new LatLng(49.808649, -97.136005), new LatLng(49.808427, -97.135911), new LatLng(49.808446, -97.135763));
        bu.setTop(new LatLng(49.808605, -97.136014));
        bu.setLeft(bt.getRight());
        bu.setBottom(br.getTop());

        walkableZones.add(bu);

        WalkableZone bv = new WalkableZone(new LatLng(49.808726, -97.136083), new LatLng(49.808729, -97.135940), new LatLng(49.808608, -97.136113), new LatLng(49.808614, -97.135913));
        bv.setTop(new LatLng(49.808721, -97.136014));
        bv.setBottom(bu.getTop());
        bv.setLeft(education_south_ent.getRight());

        walkableZones.add(bv);

        WalkableZone bw = new WalkableZone(new LatLng(49.808382, -97.135481), new LatLng(49.808445, -97.135284), new LatLng(49.808306, -97.135460), new LatLng(49.808378, -97.135242));
        bw.setTop(new LatLng(49.808397, -97.135377));
        bw.setLeft(russel_north_ent.getRight());

        walkableZones.add(bw);

        WalkableZone bx = new WalkableZone(new LatLng(49.808441, -97.135780), new LatLng(49.808541, -97.135535), new LatLng(49.808363, -97.135712), new LatLng(49.808432, -97.135495));
        bx.setLeft(bq.getRight());
        bx.setTop(new LatLng(49.808529, -97.135567));
        bx.setRight(new LatLng(49.808463, -97.135508));

        walkableZones.add(bx);

        WalkableZone by = new WalkableZone(new LatLng(49.808646, -97.135669), new LatLng(49.808644, -97.135568), new LatLng(49.808519, -97.135596), new LatLng(49.808533, -97.135545));
        by.setTop(helen_glass.getBottom());
        by.setBottom(bx.getTop());

        walkableZones.add(by);

        WalkableZone bz = new WalkableZone(new LatLng(49.808540, -97.135557), new LatLng(49.808670, -97.135079), new LatLng(49.808382, -97.135481), new LatLng(49.808532, -97.134996));
        bz.setLeft(bx.getRight());
        bz.setBottom(bw.getTop());
        bz.setRight(new LatLng(49.808617, -97.135022));

        walkableZones.add(bz);

        WalkableZone ca = new WalkableZone(new LatLng(49.808745, -97.135150), new LatLng(49.808777, -97.135021), new LatLng(49.808706, -97.135159), new LatLng(49.808745, -97.134995));
        ca.setLeft(new LatLng(49.808741, -97.135127));
        ca.setBottom(new LatLng(49.808738, -97.135035));
        ca.setTop(uni_centre_south_west_ent.getBottom());

        walkableZones.add(ca);

        WalkableZone cb = new WalkableZone(new LatLng(49.808711, -97.135147), new LatLng(49.808784, -97.134894), new LatLng(49.808524, -97.135020), new LatLng(49.808609, -97.134751));
        cb.setTop(ca.getBottom());
        cb.setRight(new LatLng(49.808748, -97.134871));
        cb.setLeft(bz.getRight());
        cb.setBottom(eitc_e3.getLeft());

        walkableZones.add(cb);

        WalkableZone cc = new WalkableZone(new LatLng(49.808747, -97.135500), new LatLng(49.808776, -97.135098), new LatLng(49.808661, -97.135457), new LatLng(49.808711, -97.135082));
        cc.setLeft(helen_glass.getRight());
        cc.setRight(ca.getLeft());

        walkableZones.add(cc);

        WalkableZone cd = new WalkableZone(new LatLng(49.808883, -97.135066), new LatLng(49.808994, -97.134727), new LatLng(49.808696, -97.134909), new LatLng(49.808811, -97.134602));
        cd.setLeft(cb.getRight());
        cd.setTop(uni_centre_west_ent.getBottom());
        cd.setRight(new LatLng(49.808953, -97.134693));

        walkableZones.add(cd);

        WalkableZone ce = new WalkableZone(new LatLng(49.809002, -97.134745), new LatLng(49.809182, -97.134225), new LatLng(49.808867, -97.134635), new LatLng(49.809019, -97.134121));
        ce.setLeft(cd.getRight());
        ce.setRight(new LatLng(49.809129, -97.134215));

        walkableZones.add(ce);

        WalkableZone cf = new WalkableZone(new LatLng(49.809198, -97.134243), new LatLng(49.809346, -97.133747), new LatLng(49.808991, -97.134100), new LatLng(49.809162, -97.133537));
        cf.setLeft(ce.getRight());
        cf.setBottom(new LatLng(49.809141, -97.133597));
        cf.setTop(uni_centre_north_ent.getBottom());

        walkableZones.add(cf);

        WalkableZone cg = new WalkableZone(new LatLng(49.809155, -97.133702), new LatLng(49.809196, -97.133509), new LatLng(49.808985, -97.133496), new LatLng(49.809029, -97.133355));
        cg.setTop(cf.getBottom());
        cg.setBottom(eitc_e2.getTop());
        cg.setLeft(eitc_e3.getRight());
        cg.setRight(new LatLng(49.809058, -97.133384));

        walkableZones.add(cg);

        WalkableZone ch = new WalkableZone(new LatLng(49.809004, -97.133166), new LatLng(49.809034, -97.133082), new LatLng(49.808760, -97.132960), new LatLng(49.808783, -97.132879));
        ch.setTop(new LatLng(49.809016, -97.133126));
        ch.setRight(new LatLng(49.808905, -97.132985));
        ch.setBottom(eitc_e1_e2.getTop());

        walkableZones.add(ch);

        WalkableZone ci = new WalkableZone(new LatLng(49.808929, -97.133014), new LatLng(49.808965, -97.132892), new LatLng(49.808883, -97.132978), new LatLng(49.808931, -97.132850));
        ci.setRight(ag.getLeft());
        ci.setLeft(ch.getRight());

        walkableZones.add(ci);

        WalkableZone cj = new WalkableZone(new LatLng(49.809029, -97.133360), new LatLng(49.809084, -97.133116), new LatLng(49.808952, -97.133304), new LatLng(49.809022, -97.133072));
        cj.setBottom(ch.getTop());
        cj.setLeft(eitc_e2.getRight());
        cj.setTop(new LatLng(49.809077, -97.133165));

        walkableZones.add(cj);

        WalkableZone ck = new WalkableZone(new LatLng(49.809077, -97.133430), new LatLng(49.809193, -97.133043), new LatLng(49.809012, -97.133397), new LatLng(49.809138, -97.132937));
        ck.setRight(p.getLeft());
        ck.setBottom(cj.getTop());
        ck.setLeft(cg.getRight());
        ck.setTop(new LatLng(49.809142, -97.133212));

        walkableZones.add(ck);

        WalkableZone cl = new WalkableZone(new LatLng(49.809464, -97.133621), new LatLng(49.809518, -97.133424), new LatLng(49.809102, -97.133344), new LatLng(49.809161, -97.133147));
        cl.setTop(new LatLng(49.809507, -97.133477));
        cl.setLeft(uni_centre_east_ent.getRight());
        cl.setBottom(ck.getTop());

        walkableZones.add(cl);

        WalkableZone cm = new WalkableZone(new LatLng(49.809806, -97.133977), new LatLng(49.809876, -97.133627), new LatLng(49.809452, -97.133705), new LatLng(49.809536, -97.133379));
        cm.setTop(new LatLng(49.809848, -97.133752));
        cm.setRight(new LatLng(49.809859, -97.133622));
        cm.setLeft(uni_centre_north_ent.getRight());
        cm.setBottom(cl.getTop());

        walkableZones.add(cm);

        WalkableZone cn = new WalkableZone(new LatLng(49.810146, -97.134445), new LatLng(49.810267, -97.133978), new LatLng(49.809733, -97.134127), new LatLng(49.809862, -97.133667));
        cn.setTop(new LatLng(49.810234, -97.134121));
        cn.setRight(new LatLng(49.810139, -97.133881));
        cn.setBottom(cm.getTop());

        walkableZones.add(cn);


        WalkableZone dd = new WalkableZone(new LatLng(49.810362, -97.134388), new LatLng(49.810430, -97.134156), new LatLng(49.810188, -97.134258), new LatLng(49.810251, -97.134033));
        dd.setTop(new LatLng(49.810405, -97.134235));
        dd.setLeft(bio_science_east_ent.getRight());
        dd.setBottom(cn.getTop());

        walkableZones.add(dd);

        WalkableZone de = new WalkableZone(new LatLng(49.810467, -97.134375), new LatLng(49.810512, -97.134234), new LatLng(49.810373, -97.134297), new LatLng(49.810410, -97.134163));
        de.setTop(allen_armes.getBottom());
        de.setBottom(dd.getTop());

        walkableZones.add(de);

        WalkableZone df = new WalkableZone(new LatLng(49.810479, -97.134623), new LatLng(49.810507, -97.134537), new LatLng(49.810315, -97.134509), new LatLng(49.810336, -97.134419));
        df.setLeft(new LatLng(49.810412, -97.134578));
        df.setRight(allen_armes.getLeft());
        df.setBottom(bio_science_east_ent.getTop());

        walkableZones.add(df);

        WalkableZone dg = new WalkableZone(new LatLng(49.810242, -97.135169), new LatLng(49.810480, -97.134612), new LatLng(49.810183, -97.135117), new LatLng(49.810377, -97.134508));
        dg.setRight(df.getLeft());
        dg.setLeft(new LatLng(49.810209, -97.135130));

        walkableZones.add(dg);

        WalkableZone dh = new WalkableZone(new LatLng(49.810204, -97.135301), new LatLng(49.810247, -97.135146), new LatLng(49.810047, -97.135304), new LatLng(49.810107, -97.135056));
        dh.setLeft(new LatLng(49.810131, -97.135299));
        dh.setBottom(bio_science_west_ent.getTop());
        dh.setRight(dg.getLeft());

        walkableZones.add(dh);

        WalkableZone di = new WalkableZone(new LatLng(49.810175, -97.135767), new LatLng(49.810229, -97.135289), new LatLng(49.810049, -97.135663), new LatLng(49.810111, -97.135292));
        di.setRight(dh.getLeft());
        di.setTop(new LatLng(49.810206, -97.135508));
        di.setLeft(new LatLng(49.810080, -97.135680));

        walkableZones.add(di);

        WalkableZone dj = new WalkableZone(new LatLng(49.809937, -97.136187), new LatLng(49.810107, -97.135687), new LatLng(49.809887, -97.136155), new LatLng(49.810053, -97.135647));
        dj.setLeft(robert_schultz_south_ent.getRight());
        dj.setRight(di.getLeft());

        walkableZones.add(dj);

        WalkableZone dk = new WalkableZone(new LatLng(49.810359, -97.135407), new LatLng(49.810391, -97.135261), new LatLng(49.810181, -97.135569), new LatLng(49.810184, -97.135413));
        dk.setBottom(di.getTop());
        dk.setTop(new LatLng(49.810354, -97.135384));

        walkableZones.add(dk);

        WalkableZone dl = new WalkableZone(new LatLng(49.811212, -97.136105), new LatLng(49.811227, -97.136027), new LatLng(49.810356, -97.135395), new LatLng(49.810381, -97.135279));
        dl.setTop(new LatLng(49.811221, -97.136078));
        dl.setBottom(dk.getTop());
        dl.setRight(new LatLng(49.810686, -97.135648));

        walkableZones.add(dl);

        WalkableZone dm = new WalkableZone(new LatLng(49.811294, -97.136101), new LatLng(49.811348, -97.135804), new LatLng(49.811261, -97.136075), new LatLng(49.811328, -97.135794));
        dm.setTop(new LatLng(49.811341, -97.135821));
        dm.setRight(parker.getLeft());
        dm.setLeft(new LatLng(49.811282, -97.136095));

        walkableZones.add(dm);

        WalkableZone dn = new WalkableZone(new LatLng(49.811406, -97.136256), new LatLng(49.811409, -97.136198), new LatLng(49.811229, -97.136118), new LatLng(49.811245, -97.136068));
        dn.setTop(new LatLng(49.811414, -97.136219));
        dn.setBottom(dl.getTop());
        dn.setRight(dm.getLeft());

        walkableZones.add(dn);

        WalkableZone dp = new WalkableZone(new LatLng(49.810697, -97.135739), new LatLng(49.810972, -97.134640), new LatLng(49.810647, -97.135700), new LatLng(49.810934, -97.134603));
        dp.setLeft(dl.getRight());
        dp.setRight(allen_armes_parker.getLeft());

        walkableZones.add(dp);

        WalkableZone dq = new WalkableZone(new LatLng(49.811258, -97.136906), new LatLng(49.811545, -97.135899), new LatLng(49.811228, -97.136883), new LatLng(49.811510, -97.135889));
        dq.setRight(new LatLng(49.811528, -97.135902));
        dq.setBottom(dn.getTop());
        dq.setLeft(st_johns_east_ent.getRight());

        walkableZones.add(dq);

        WalkableZone dr = new WalkableZone(new LatLng(49.811572, -97.135897), new LatLng(49.811566, -97.135773), new LatLng(49.811325, -97.135885), new LatLng(49.811348, -97.135793));
        dr.setBottom(dm.getTop());
        dr.setTop(new LatLng(49.811570, -97.135836));
        dr.setLeft(dq.getRight());

        walkableZones.add(dr);

        WalkableZone ds = new WalkableZone(new LatLng(49.811623, -97.135949), new LatLng(49.811559, -97.135283), new LatLng(49.811545, -97.135899), new LatLng(49.811501, -97.135308));
        ds.setTop(wallace.getBottom());
        ds.setBottom(dr.getTop());
        ds.setRight(new LatLng(49.811553, -97.135272));

        walkableZones.add(ds);

        WalkableZone dt = new WalkableZone(new LatLng(49.811662, -97.135257), new LatLng(49.811695, -97.135034), new LatLng(49.811542, -97.135286), new LatLng(49.811622, -97.135047));
        dt.setLeft(ds.getRight());
        dt.setBottom(new LatLng(49.811579, -97.135171));

        walkableZones.add(dt);

        WalkableZone du = new WalkableZone(new LatLng(49.811571, -97.135219), new LatLng(49.811598, -97.135135), new LatLng(49.811502, -97.135153), new LatLng(49.811530, -97.135078));
        du.setTop(dt.getBottom());
        du.setBottom(parker.getTop());

        walkableZones.add(du);

        WalkableZone dv = new WalkableZone(new LatLng(49.810962, -97.137900), new LatLng(49.811269, -97.136978), new LatLng(49.810900, -97.137855), new LatLng(49.811218, -97.136928));
        dv.setTop(new LatLng(49.811036, -97.137669));
        dv.setLeft(new LatLng(49.810917, -97.137856));
        dv.setRight(st_johns_east_ent.getLeft());
        dv.setBottom(new LatLng(49.810979, -97.137597));

        walkableZones.add(dv);

        WalkableZone dw = new WalkableZone(new LatLng(49.810963, -97.137684), new LatLng(49.811016, -97.137524), new LatLng(49.810754, -97.137487), new LatLng(49.810787, -97.137403));
        dw.setLeft(new LatLng(49.810832, -97.137564));
        dw.setTop(dv.getBottom());
        dw.setBottom(new LatLng(49.810791, -97.137452));

        walkableZones.add(dw);

        WalkableZone dx = new WalkableZone(new LatLng(49.810923, -97.137877), new LatLng(49.810853, -97.137518), new LatLng(49.810885, -97.137982), new LatLng(49.810807, -97.137499));
        dx.setLeft(new LatLng(49.810871, -97.137991));
        dx.setRight(dv.getLeft());
        dx.setBottom(dw.getLeft());

        walkableZones.add(dx);

        WalkableZone dy = new WalkableZone(new LatLng(49.810344, -97.139603), new LatLng(49.810908, -97.137951), new LatLng(49.810305, -97.139586), new LatLng(49.810870, -97.137942));
        dy.setRight(dx.getLeft());
        dy.setBottom(new LatLng(49.810719, -97.138378));

        walkableZones.add(dy);

        WalkableZone dz = new WalkableZone(new LatLng(49.810721, -97.138424), new LatLng(49.810745, -97.138348), new LatLng(49.810514, -97.138274), new LatLng(49.810547, -97.138193));
        dz.setTop(dy.getBottom());
        dz.setBottom(st_pauls_north_ent.getTop());

        walkableZones.add(dz);

        WalkableZone ea = new WalkableZone(new LatLng(49.810786, -97.137523), new LatLng(49.810853, -97.137223), new LatLng(49.810591, -97.137406), new LatLng(49.810668, -97.137168));
        ea.setBottom(new LatLng(49.810609, -97.137357));
        ea.setTop(dw.getBottom());
        ea.setRight(st_johns_north_ent.getLeft());

        walkableZones.add(ea);

        WalkableZone eb = new WalkableZone(new LatLng(49.810564, -97.137383), new LatLng(49.810610, -97.137415), new LatLng(49.810502, -97.137196), new LatLng(49.810536, -97.137151));
        eb.setTop(ea.getBottom());
        eb.setBottom(new LatLng(49.810545, -97.137339));
        eb.setRight(st_johns_west_ent.getTop());

        walkableZones.add(eb);

        WalkableZone ec = new WalkableZone(new LatLng(49.810583, -97.137403), new LatLng(49.810545, -97.137309), new LatLng(49.810087, -97.137195), new LatLng(49.810107, -97.137138));
        ec.setTop(eb.getBottom());
        ec.setRight(st_johns_west_ent.getLeft());
        ec.setBottom(new LatLng(49.810125, -97.137179));

        walkableZones.add(ec);

        WalkableZone ed = new WalkableZone(new LatLng(49.810120, -97.137207), new LatLng(49.810128, -97.136973), new LatLng(49.810029, -97.137103), new LatLng(49.810041, -97.136920));
        ed.setTop(ec.getBottom());
        ed.setRight(robert_schultz_west_ent.getLeft());
        ed.setLeft(st_pauls_east_ent.getRight());
        ed.setBottom(new LatLng(49.810038, -97.136942));

        walkableZones.add(ed);

        WalkableZone ee = new WalkableZone(new LatLng(49.810056, -97.137020), new LatLng(49.810078, -97.136918), new LatLng(49.809698, -97.136707), new LatLng(49.809721, -97.136650));
        ee.setTop(ed.getBottom());
        ee.setRight(new LatLng(49.809749, -97.136681));
        ee.setLeft(new LatLng(49.809737, -97.136764));

        walkableZones.add(ee);

        WalkableZone ef = new WalkableZone(new LatLng(49.809753, -97.136762), new LatLng(49.809855, -97.136444), new LatLng(49.809717, -97.136721), new LatLng(49.809819, -97.136405));
        ef.setRight(robert_schultz_south_ent.getLeft());
        ef.setLeft(ee.getRight());

        walkableZones.add(ef);

        WalkableZone eg = new WalkableZone(new LatLng(49.809907, -97.137265), new LatLng(49.809937, -97.137201), new LatLng(49.809742, -97.136922), new LatLng(49.809768, -97.136879));
        eg.setTop(st_pauls_east_ent.getBottom());
        eg.setBottom(new LatLng(49.809763, -97.136915));

        walkableZones.add(eg);

        WalkableZone eh = new WalkableZone(new LatLng(49.809753, -97.136946), new LatLng(49.809762, -97.136760), new LatLng(49.809242, -97.136546), new LatLng(49.809298, -97.136395));
        eh.setTop(eg.getBottom());
        eh.setRight(ee.getLeft());
        eh.setBottom(new LatLng(49.809265, -97.136474));

        walkableZones.add(eh);

        WalkableZone ei = new WalkableZone(new LatLng(49.809267, -97.136529), new LatLng(49.809297, -97.136386), new LatLng(49.808813, -97.136140), new LatLng(49.808852, -97.136002));
        ei.setTop(eh.getBottom());
        ei.setLeft(education_north_ent.getRight());
        ei.setBottom(new LatLng(49.808828, -97.136078));

        walkableZones.add(ei);

        WalkableZone ej = new WalkableZone(new LatLng(49.808794, -97.136136), new LatLng(49.808865, -97.135979), new LatLng(49.808712, -97.136087), new LatLng(49.808772, -97.135775));
        ej.setTop(ei.getBottom());
        ej.setBottom(bv.getTop());
        ej.setRight(helen_glass.getLeft());

        walkableZones.add(ej);

        armesIndoorConnections = new ArmesIndoorConnections();

        IndoorVertex nwArmesEnt = armesIndoorConnections.getNorthWestEntrance();
        allen_armes_parker.connectToRight(nwArmesEnt);

        IndoorVertex swArmesEnt = armesIndoorConnections.getSouthWestEntrance();
        allen_armes.connectToRight(swArmesEnt);

        machrayIndoorConnections = new MachrayIndoorConnections();

        IndoorVertex machrayEnt = machrayIndoorConnections.getExit();
        armes_machray.connectToTop(machrayEnt);

        allenIndoorConnections = new AllenIndoorConnections();

        connectBuildingsTogether();
    }

    private void connectBuildingsTogether()
    {
        allenIndoorConnections.getArmesTunnelConnection().connectVertex(armesIndoorConnections.getAllenConnectionTunnel());
        allenIndoorConnections.getArmesNorthConnection().connectVertex(armesIndoorConnections.getAllenConnectionNorth());
        allenIndoorConnections.getArmesSouthConnection().connectVertex(armesIndoorConnections.getAllenConnectionSouth());

        armesIndoorConnections.getMachrayConnectionTunnel().connectVertex(machrayIndoorConnections.getArmesConnectionTunnel());
        armesIndoorConnections.getMachrayConnectionNorth().connectVertex(machrayIndoorConnections.getArmesConnectionNorth());
        armesIndoorConnections.getMachrayConnectionSouth().connectVertex(machrayIndoorConnections.getArmesConnectionSouth());

    }
}
