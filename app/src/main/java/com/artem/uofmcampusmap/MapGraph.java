package com.artem.uofmcampusmap;

import android.content.res.Resources;

import com.artem.uofmcampusmap.buildings.allen.AllenIndoorConnections;
import com.artem.uofmcampusmap.buildings.armes.ArmesIndoorConnections;
import com.artem.uofmcampusmap.buildings.machray.MachrayIndoorConnections;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class MapGraph
{
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

    public MapGraph(Resources resources)
    {
        startEndLocations = new HashMap<>();
        routeFinder = new RouteFinder();
        this.resources = resources;
        tunnelConnectedBuildings = resources.getStringArray(R.array.tunnel_connected_buildings);
        populateGraph();
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

    //Gets the closest stairs to a specified room within a building
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

    /* Creates all of the points for the outdoor section of the map, also connects them to any indoor connections
        includes all buildings, parking lots, and bus stops */
    private void populateGraph()
    {
        /*****************************************************************************************
                                             Location Names
         ****************************************************************************************/
        String activeLivingCentre = resources.getString(R.string.active_living_centre);
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
        String welcomeCentre = resources.getString(R.string.welcome_centre);

        //Bus stops
        String nbUniAtDysart = resources.getString(R.string.nb_uni_at_dysart);
        String nbUniChancellor = resources.getString(R.string.nb_uni_chancellor);
        String sbUniAtDysart = resources.getString(R.string.sb_uni_at_dysart);
        String wbDafoeAtUni = resources.getString(R.string.wb_dafoe_at_uni);
        String wbDafoeAtAgri = resources.getString(R.string.wb_dafoe_at_agriculture);
        String ebDafoeAtUni = resources.getString(R.string.eb_dafoe_at_uni);
        String ebDafoeAtAgri = resources.getString(R.string.eb_dafoe_at_agri);

        //Parking Lots
        String studentLot = resources.getString(R.string.student_lot);
        String qLot = resources.getString(R.string.q_lot);
        String kLot = resources.getString(R.string.k_lot);
        String iLot = resources.getString(R.string.i_lot);

        /*****************************************************************************************
                                            Location Points
         ****************************************************************************************/
        //active living centre
        OutdoorVertex active_living_west_ent = new OutdoorVertex(new GeoPoint(49.806411, -97.138719));
        addEntrance(activeLivingCentre, active_living_west_ent);

        OutdoorVertex active_living_east_ent = new OutdoorVertex(new GeoPoint(49.806441, -97.138033));
        addEntrance(activeLivingCentre, active_living_east_ent);

        OutdoorVertex active_living_south_west_ent = new OutdoorVertex(new GeoPoint(49.806326, -97.138460));
        addEntrance(activeLivingCentre, active_living_south_west_ent);

        //agriculture
        OutdoorVertex agriculture_north_ent = new OutdoorVertex(new GeoPoint(49.807100, -97.135065));
        addEntrance(agriculture, agriculture_north_ent);

        OutdoorVertex agriculture_south_ent  = new OutdoorVertex(new GeoPoint(49.806958, -97.135498));
        addEntrance(agriculture, agriculture_south_ent);

        OutdoorVertex agriculture_south_west_ent = new OutdoorVertex(new GeoPoint(49.806573, -97.135970));
        addEntrance(agriculture, agriculture_south_west_ent);

        //agri_engineer
        OutdoorVertex agri_engineer_north_ent = new OutdoorVertex(new GeoPoint(49.807491, -97.133790));
        addEntrance(agri_engineer, agri_engineer_north_ent);

        OutdoorVertex agri_engineer_south_ent = new OutdoorVertex(new GeoPoint(49.807406, -97.134132));
        addEntrance(agri_engineer, agri_engineer_south_ent);

        //allen = new OutdoorVertex(new GeoPoint());
        OutdoorVertex allen_armes_parker = new OutdoorVertex(new GeoPoint(49.810993, -97.134347));
        addEntrance(allen, allen_armes_parker);
        addEntrance(armes, allen_armes_parker);
        addEntrance(parker, allen_armes_parker);

        OutdoorVertex allen_armes = new OutdoorVertex(new GeoPoint(49.810614, -97.134037));
        addEntrance(allen, allen_armes);
        addEntrance(armes, allen_armes);

        //animal sci
        OutdoorVertex animal_sci_north_ent = new OutdoorVertex(new GeoPoint(49.806214, -97.137599 ));
        addEntrance(animalSci, animal_sci_north_ent);

        OutdoorVertex animal_sci_south_ent = new OutdoorVertex(new GeoPoint(49.806032, -97.138085));
        addEntrance(animalSci, animal_sci_south_ent);

        //archi2 OutdoorVertex = new OutdoorVertex(new GeoPoint());
        OutdoorVertex archi2_east_ent = new OutdoorVertex(new GeoPoint(49.807970, -97.136214));
        addEntrance(archi2, archi2_east_ent);

        OutdoorVertex archi2_south_ent = new OutdoorVertex(new GeoPoint( 49.807786, -97.136334));
        addEntrance(archi2, archi2_south_ent);

        OutdoorVertex archi2_west_ent = new OutdoorVertex(new GeoPoint( 49.807761, -97.136842));
        addEntrance(archi2, archi2_west_ent);

        //armes
        OutdoorVertex armes_machray = new OutdoorVertex(new GeoPoint(49.810997, -97.133403));
        addEntrance(armes, armes_machray);
        addEntrance(machray, armes_machray);

        //artlab
        OutdoorVertex artlab_ent = new OutdoorVertex(new GeoPoint(49.808648, -97.130393));
        addEntrance(artlab, artlab_ent);

        //biosci
        OutdoorVertex bio_science_east_ent = new OutdoorVertex(new GeoPoint(49.810287, -97.134455));
        addEntrance(bioSci, bio_science_east_ent);

        OutdoorVertex bio_science_west_ent = new OutdoorVertex(new GeoPoint(49.810090, -97.135044));
        addEntrance(bioSci, bio_science_west_ent);

        //buller
        OutdoorVertex buller_west_ent = new OutdoorVertex(new GeoPoint(49.810327, -97.133548));
        addEntrance(buller, buller_west_ent);

        OutdoorVertex buller_east_ent = new OutdoorVertex(new GeoPoint(49.810459, -97.133175));
        addEntrance(buller, buller_east_ent);

        //dairy sci
        OutdoorVertex dairy_science_ent  = new OutdoorVertex(new GeoPoint(49.807654, -97.133320));
        addEntrance(dairySci, dairy_science_ent);

        //drake
        OutdoorVertex drake_ent = new OutdoorVertex(new GeoPoint(49.808213, -97.130217 ));
        addEntrance(drake, drake_ent);

        //duff roblin
        OutdoorVertex duff_roblin_west_ent = new OutdoorVertex(new GeoPoint(49.810894, -97.132999));
        addEntrance(duffRoblin, duff_roblin_west_ent);

        OutdoorVertex duff_roblin_south_ent = new OutdoorVertex(new GeoPoint(49.810961, -97.132287));
        addEntrance(duffRoblin, duff_roblin_south_ent);

        OutdoorVertex duff_roblin_north_ent = new OutdoorVertex(new GeoPoint(49.811189, -97.132487));
        addEntrance(duffRoblin, duff_roblin_north_ent   );

        //eitc e1/e2/e3
        OutdoorVertex eitc_e1_east_ent = new OutdoorVertex(new GeoPoint(49.808453, -97.133077));
        addEntrance(e1_eitc, eitc_e1_east_ent);

        OutdoorVertex eitc_e1_e3_ent = new OutdoorVertex(new GeoPoint(49.808243, -97.133994));
        addEntrance(e1_eitc, eitc_e1_e3_ent);
        addEntrance(e3_eitc, eitc_e1_e3_ent);

        OutdoorVertex eitc_e1_e2_ent = new OutdoorVertex(new GeoPoint(49.808648, -97.133257));
        addEntrance(e1_eitc, eitc_e1_e2_ent);
        addEntrance(e2_eitc, eitc_e1_e2_ent);

        OutdoorVertex eitc_e2_ent = new OutdoorVertex(new GeoPoint(49.808937, -97.133421));
        addEntrance(e2_eitc, eitc_e2_ent);

        OutdoorVertex eitc_e3_ent = new OutdoorVertex(new GeoPoint(49.808643, -97.134489));
        addEntrance(e3_eitc, eitc_e3_ent);

        //education
        OutdoorVertex education_south_ent = new OutdoorVertex(new GeoPoint(49.808624, -97.136406));
        addEntrance(education, education_south_ent);

        OutdoorVertex education_west_ent = new OutdoorVertex(new GeoPoint(49.808452, -97.137281));
        addEntrance(education, education_west_ent);

        OutdoorVertex education_north_ent = new OutdoorVertex(new GeoPoint(49.809142, -97.136583));
        //addEntrance(education, education_north_ent); todo toggle back on when indoor points are added to education

        //eli dafoe
        OutdoorVertex eli_dafoe_ent  = new OutdoorVertex(new GeoPoint(49.809993, -97.132140));
        addEntrance(eliDafoe, eli_dafoe_ent );

        //ext education
        OutdoorVertex ext_education_west_ent = new OutdoorVertex(new GeoPoint(49.807073, -97.139269));
        addEntrance(extEduc, ext_education_west_ent);

        OutdoorVertex ext_education_east_ent = new OutdoorVertex(new GeoPoint(49.807553, -97.137825));
        addEntrance(extEduc, ext_education_east_ent);

        //fletcher
        OutdoorVertex fletcher_ent = new OutdoorVertex(new GeoPoint(49.809565, -97.131352));
        addEntrance(fletcher, fletcher_ent);

        //helen glass
        OutdoorVertex helen_glass_ent = new OutdoorVertex(new GeoPoint(49.808810, -97.135583));
        addEntrance(helenGlass, helen_glass_ent);

        //human eco
        OutdoorVertex human_eco_west_ent = new OutdoorVertex(new GeoPoint(49.810700, -97.132333));
        addEntrance(humanEco, human_eco_west_ent);

        OutdoorVertex human_eco_south_east_ent = new OutdoorVertex(new GeoPoint(49.810818, -97.131628));
        addEntrance(humanEco, human_eco_south_east_ent);

        //isbister
        OutdoorVertex isbister_ent = new OutdoorVertex(new GeoPoint(49.809124, -97.130257));
        addEntrance(isbister, isbister_ent);

        //parker
        OutdoorVertex parker_ent = new OutdoorVertex(new GeoPoint(49.811591, -97.134773));
        addEntrance(parker, parker_ent);

        //robert schultz
        OutdoorVertex robert_schultz_west_ent = new OutdoorVertex(new GeoPoint(49.810203, -97.136740));
        addEntrance(robertSchultz, robert_schultz_west_ent);

        OutdoorVertex robert_schultz_south_ent = new OutdoorVertex(new GeoPoint(49.809943, -97.136340));
        addEntrance(robertSchultz, robert_schultz_south_ent);

        //robson
        OutdoorVertex robson_ent = new OutdoorVertex(new GeoPoint(49.811758, -97.130976));
        addEntrance(robson, robson_ent);

        //russel
        OutdoorVertex russel_north_ent = new OutdoorVertex(new GeoPoint(49.808192, -97.135455));
        addEntrance(russel, russel_north_ent);

        OutdoorVertex russel_south_ent = new OutdoorVertex(new GeoPoint(49.807905, -97.135216));
        addEntrance(russel, russel_south_ent);

        //st johns
        OutdoorVertex st_johns_west_ent = new OutdoorVertex(new GeoPoint(49.810557, -97.137024));
        addEntrance(stJohns, st_johns_west_ent);

        OutdoorVertex st_johns_north_ent = new OutdoorVertex(new GeoPoint(49.810749, -97.137065));
        addEntrance(stJohns, st_johns_north_ent);

        OutdoorVertex st_johns_east_ent= new OutdoorVertex(new GeoPoint(49.810898, -97.136607));
        addEntrance(stJohns, st_johns_east_ent);

        //st pauls
        OutdoorVertex st_pauls_north_ent = new OutdoorVertex(new GeoPoint(49.810430, -97.138044));
        addEntrance(stPauls, st_pauls_north_ent);

        OutdoorVertex st_pauls_east_ent = new OutdoorVertex(new GeoPoint(49.809888, -97.137377));
        addEntrance(stPauls, st_pauls_east_ent);

        //tache arts
        OutdoorVertex tache_arts_east_ent = new OutdoorVertex(new GeoPoint(49.808391, -97.130937));
        addEntrance(tacheArts, tache_arts_east_ent);

        OutdoorVertex tache_arts_mid_west_ent = new OutdoorVertex(new GeoPoint(49.807962, -97.132190));
        addEntrance(tacheArts, tache_arts_mid_west_ent);

        OutdoorVertex tache_arts_west_ent = new OutdoorVertex(new GeoPoint(49.807803, -97.132558));
        addEntrance(tacheArts, tache_arts_west_ent);

        //tier
        OutdoorVertex tier_tunnel = new OutdoorVertex(new GeoPoint(49.808811, -97.130605));
        //addEntrance(tier, tier_tunnel); todo toggle on when tier indoor works / tunnels

        OutdoorVertex tier_west_ent = new OutdoorVertex(new GeoPoint(49.809178, -97.131130));
        addEntrance(tier, tier_west_ent);

        OutdoorVertex tier_south_ent = new OutdoorVertex(new GeoPoint(49.808940, -97.130745));
        addEntrance(tier, tier_south_ent);

        //uni centre
        OutdoorVertex uni_centre_west_ent = new OutdoorVertex(new GeoPoint(49.809072, -97.135035));
        addEntrance(uniCentre, uni_centre_west_ent);

        OutdoorVertex uni_centre_east_ent = new OutdoorVertex(new GeoPoint(49.809291, -97.133712));
        addEntrance(uniCentre, uni_centre_east_ent);

        OutdoorVertex uni_centre_north_ent = new OutdoorVertex(new GeoPoint(49.809455, -97.133905));
        addEntrance(uniCentre, uni_centre_north_ent);

        OutdoorVertex uni_centre_south_west_ent = new OutdoorVertex(new GeoPoint(49.808781, -97.135062));
        addEntrance(uniCentre, uni_centre_south_west_ent);

        //uni college
        OutdoorVertex uni_college_south_ent = new OutdoorVertex(new GeoPoint(49.811055, -97.131105));
        addEntrance(uniCollege, uni_college_south_ent);

        OutdoorVertex uni_college_north_ent = new OutdoorVertex(new GeoPoint(49.811559, -97.131540));
        addEntrance(uniCollege, uni_college_north_ent);

        //wallace
        OutdoorVertex wallace_ent = new OutdoorVertex(new GeoPoint(49.811658, -97.135693));
        addEntrance(wallace, wallace_ent);

        //welcome centre
        OutdoorVertex welcome_centre_ent = new OutdoorVertex(new GeoPoint(49.806228, -97.139611));
        addEntrance(welcomeCentre, welcome_centre_ent);

        /* todo add missing parking lots / bus stop points
         OutdoorVertex = new OutdoorVertex(new GeoPoint());
        addEntrance();

         Will need to add (so far):
            i_lot
         */

        OutdoorVertex eb_dafoe_at_agri = new OutdoorVertex(new GeoPoint(49.806118, -97.138156));
        addEntrance(ebDafoeAtAgri, eb_dafoe_at_agri);
        connectVertexesWithDirection(Vertex.SOUTH, eb_dafoe_at_agri, animal_sci_south_ent);

        OutdoorVertex eb_dafoe_at_uni = new OutdoorVertex(new GeoPoint(49.805770, -97.139203));
        addEntrance(ebDafoeAtUni, eb_dafoe_at_uni);

        OutdoorVertex wb_dafoe_at_uni = new OutdoorVertex(new GeoPoint(49.805771, -97.139564));
        addEntrance(wbDafoeAtUni, wb_dafoe_at_uni);

        OutdoorVertex student_lot_north = new OutdoorVertex(new GeoPoint(49.806810, -97.140113));
        addEntrance(studentLot, student_lot_north);

        OutdoorVertex student_lot_south = new OutdoorVertex(new GeoPoint(49.806250, -97.139753));
        addEntrance(studentLot, student_lot_south);

        OutdoorVertex student_lot_mid = new OutdoorVertex(new GeoPoint(49.806546, -97.139944));
        addEntrance(studentLot, student_lot_mid);
        connectVertexesWithDirection(Vertex.NORTH, student_lot_mid, student_lot_north);

        OutdoorVertex sb_uni_at_dysart = new OutdoorVertex(new GeoPoint(49.808526, -97.141261));
        addEntrance(sbUniAtDysart, sb_uni_at_dysart);

        OutdoorVertex nb_uni_at_dysart = new OutdoorVertex(new GeoPoint(49.809658, -97.141567));
        addEntrance(nbUniAtDysart, nb_uni_at_dysart);

        OutdoorVertex q_lot_west = new OutdoorVertex(new GeoPoint(49.810521, -97.139791));
        addEntrance(qLot, q_lot_west);

        OutdoorVertex q_lot_mid = new OutdoorVertex(new GeoPoint(49.810906, -97.138556));
        addEntrance(qLot, q_lot_mid);

        OutdoorVertex q_lot_east = new OutdoorVertex(new GeoPoint(49.811158, -97.137833));
        addEntrance(qLot, q_lot_east);

        OutdoorVertex k_lot_south = new OutdoorVertex(new GeoPoint(49.811520, -97.136801));
        addEntrance(kLot, k_lot_south);

        OutdoorVertex k_lot_east = new OutdoorVertex(new GeoPoint(49.811741, -97.136826));
        //addEntrance(kLot, k_lot_east); todo re-enable when wallace has 100% other entrances that work

        OutdoorVertex nb_uni_chancellor  = new OutdoorVertex(new GeoPoint(49.807102, -97.139505));
        addEntrance(nbUniChancellor, nb_uni_chancellor );

        OutdoorVertex wb_dafoe_at_agri = new OutdoorVertex(new GeoPoint(49.806850, -97.136522));
        addEntrance(wbDafoeAtAgri, wb_dafoe_at_agri);

        //todo maybe split it up into different buses?
        OutdoorVertex wb_dafoe_uofm = new OutdoorVertex(new GeoPoint(49.808387, -97.131837));
        addEntrance(wbDafoeAtUni, wb_dafoe_uofm);

        /*****************************************************************************************
                                            Outdoor to Indoor Connections
         ****************************************************************************************/

        armesIndoorConnections = new ArmesIndoorConnections(resources);

        IndoorVertex nwArmesEnt = armesIndoorConnections.getNorthWestEntrance();
        connectVertexesNoDirection(nwArmesEnt, allen_armes_parker);

        IndoorVertex swArmesEnt = armesIndoorConnections.getSouthWestEntrance();
        connectVertexesNoDirection(allen_armes, swArmesEnt);

        machrayIndoorConnections = new MachrayIndoorConnections(resources);

        IndoorVertex machrayEnt = machrayIndoorConnections.getExit();
        connectVertexesNoDirection(armes_machray, machrayEnt);

        allenIndoorConnections = new AllenIndoorConnections(resources);

        connectBuildingsTogether();

        /*****************************************************************************************
                                            Remaining Points
         ****************************************************************************************/
        OutdoorVertex a = new OutdoorVertex(new GeoPoint(49.808616, -97.130490));
        connectVertexesWithDirection(Vertex.SOUTH, a, drake_ent);
        connectVertexesWithDirection(Vertex.EAST, a, artlab_ent);

        OutdoorVertex b = new OutdoorVertex(new GeoPoint(49.808616, -97.130490));
        connectVertexesWithDirection(Vertex.SOUTH, b, a);

        OutdoorVertex c = new OutdoorVertex(new GeoPoint(49.808391, -97.130937));
        connectVertexesWithDirection(Vertex.EAST, c, b);
        connectVertexesWithDirection(Vertex.SOUTH, c, tache_arts_east_ent);

        OutdoorVertex d = new OutdoorVertex(new GeoPoint(49.808109, -97.132285));
        connectVertexesWithDirection(Vertex.EAST, d, c);
        connectVertexesWithDirection(Vertex.SOUTH, d, tache_arts_mid_west_ent);

        OutdoorVertex e = new OutdoorVertex(new GeoPoint(49.808087, -97.132375));
        connectVertexesWithDirection(Vertex.EAST, e, d);

        OutdoorVertex f = new OutdoorVertex(new GeoPoint(49.807943, -97.132763));

        OutdoorVertex g = new OutdoorVertex(new GeoPoint(49.807808, -97.132590));
        connectVertexesWithDirection(Vertex.SOUTH_EAST, g, tache_arts_west_ent);

        OutdoorVertex h = new OutdoorVertex(new GeoPoint(49.807829, -97.132709));
        connectVertexesWithDirection(Vertex.SOUTH_EAST, h, g);
        connectVertexesWithDirection(Vertex.NORTH, h, f);

        OutdoorVertex i = new OutdoorVertex(new GeoPoint(49.808039, -97.132501));
        connectVertexesWithDirection(Vertex.SOUTH_WEST, i, g);
        connectVertexesWithDirection(Vertex.EAST, i, e);
        connectVertexesWithDirection(Vertex.WEST, i, f);

        OutdoorVertex j = new OutdoorVertex(new GeoPoint(49.807720, -97.133372));
        connectVertexesWithDirection(Vertex.EAST, j, f);
        connectVertexesWithDirection(Vertex.SOUTH_WEST, j, dairy_science_ent);

        OutdoorVertex k = new OutdoorVertex(new GeoPoint(49.807570, -97.133815));
        connectVertexesWithDirection(Vertex.EAST, k, j);
        connectVertexesWithDirection(Vertex.SOUTH, k, agri_engineer_north_ent);

        OutdoorVertex l = new OutdoorVertex(new GeoPoint(49.807467, -97.134181));
        connectVertexesWithDirection(Vertex.EAST, l, k);
        connectVertexesWithDirection(Vertex.SOUTH, l, agri_engineer_south_ent);

        OutdoorVertex m = new OutdoorVertex(new GeoPoint(49.807295, -97.134688));
        connectVertexesWithDirection(Vertex.EAST, m, l);

        OutdoorVertex n = new OutdoorVertex(new GeoPoint(49.807155, -97.135105));
        connectVertexesWithDirection(Vertex.EAST, n, m);
        connectVertexesWithDirection(Vertex.SOUTH, n, agriculture_north_ent);

        OutdoorVertex o = new OutdoorVertex(new GeoPoint(49.807006, -97.135536));
        connectVertexesWithDirection(Vertex.EAST, o, n);
        connectVertexesWithDirection(Vertex.SOUTH, o, agriculture_south_ent);

        OutdoorVertex p = new OutdoorVertex(new GeoPoint(49.806887, -97.135891));
        connectVertexesWithDirection(Vertex.EAST, p, o);

        OutdoorVertex q = new OutdoorVertex(new GeoPoint(49.806576, -97.135981));
        connectVertexesWithDirection(Vertex.EAST, q, agriculture_south_west_ent);

        OutdoorVertex r = new OutdoorVertex(new GeoPoint(49.806639, -97.135990));
        connectVertexesWithDirection(Vertex.SOUTH, r, q);

        OutdoorVertex s = new OutdoorVertex(new GeoPoint(49.806707, -97.136394));
        connectVertexesWithDirection(Vertex.SOUTH_EAST, s, r);

        OutdoorVertex t = new OutdoorVertex(new GeoPoint(49.806750, -97.135923));
        connectVertexesWithDirection(Vertex.SOUTH_WEST, t, r);
        connectVertexesWithDirection(Vertex.NORTH_EAST, t, p);

        OutdoorVertex u = new OutdoorVertex(new GeoPoint(49.806873, -97.135959));
        connectVertexesWithDirection(Vertex.SOUTH_EAST, u, t);
        connectVertexesWithDirection(Vertex.EAST, u, p);
        connectVertexesWithDirection(Vertex.WEST, u, s);

        OutdoorVertex v = new OutdoorVertex(new GeoPoint(49.806402, -97.137327));
        connectVertexesWithDirection(Vertex.EAST, v, s);

        OutdoorVertex w = new OutdoorVertex(new GeoPoint(49.806287, -97.137624));
        connectVertexesWithDirection(Vertex.EAST, w, v);
        connectVertexesWithDirection(Vertex.SOUTH, w, animal_sci_north_ent);
        connectVertexesWithDirection(Vertex.WEST, w, eb_dafoe_at_agri);

        OutdoorVertex x = new OutdoorVertex(new GeoPoint(49.805965, -97.138555));
        connectVertexesWithDirection(Vertex.EAST, x, eb_dafoe_at_agri);

        OutdoorVertex y = new OutdoorVertex(new GeoPoint(49.805793, -97.139094));
        connectVertexesWithDirection(Vertex.EAST, y, x);
        connectVertexesWithDirection(Vertex.WEST, y, eb_dafoe_at_uni);

        OutdoorVertex z = new OutdoorVertex(new GeoPoint(49.805915, -97.139174));
        connectVertexesWithDirection(Vertex.WEST, z, wb_dafoe_at_uni);
        connectVertexesWithDirection(Vertex.SOUTH, z, y);

        OutdoorVertex aa = new OutdoorVertex(new GeoPoint(49.805952, -97.139181));
        connectVertexesWithDirection(Vertex.SOUTH, aa, z);

        OutdoorVertex ab = new OutdoorVertex(new GeoPoint(49.806184, -97.139398));
        connectVertexesWithDirection(Vertex.SOUTH, ab, aa);

        OutdoorVertex ac = new OutdoorVertex(new GeoPoint(49.806234, -97.139608));
        connectVertexesWithDirection(Vertex.SOUTH_WEST, ac, welcome_centre_ent);
        connectVertexesWithDirection(Vertex.NORTH_WEST, ac, student_lot_south);
        connectVertexesWithDirection(Vertex.SOUTH_EAST, ac, ab);

        OutdoorVertex ad = new OutdoorVertex(new GeoPoint(49.806470, -97.139625));
        connectVertexesWithDirection(Vertex.SOUTH, ad, ab);
        connectVertexesWithDirection(Vertex.NORTH_WEST, ad, student_lot_mid);

        OutdoorVertex ae = new OutdoorVertex(new GeoPoint(49.807016, -97.140066));
        connectVertexesWithDirection(Vertex.SOUTH_WEST, ae, student_lot_north);

        OutdoorVertex af = new OutdoorVertex(new GeoPoint(49.807282, -97.140274));
        connectVertexesWithDirection(Vertex.SOUTH, af, ae);

        OutdoorVertex ag = new OutdoorVertex(new GeoPoint(49.807292, -97.140465));
        connectVertexesWithDirection(Vertex.SOUTH_EAST, ag, af);

        OutdoorVertex ah = new OutdoorVertex(new GeoPoint(49.808288, -97.141064));
        connectVertexesWithDirection(Vertex.SOUTH, ah, ag);
        connectVertexesWithDirection(Vertex.NORTH, ah, sb_uni_at_dysart);

        OutdoorVertex ai = new OutdoorVertex(new GeoPoint(49.809060, -97.141681));
        connectVertexesWithDirection(Vertex.SOUTH, ai, sb_uni_at_dysart);

        OutdoorVertex aj = new OutdoorVertex(new GeoPoint(49.809219, -97.141210));
        connectVertexesWithDirection(Vertex.WEST, aj, ai);

        OutdoorVertex ak = new OutdoorVertex(new GeoPoint(49.809449, -97.141357));
        connectVertexesWithDirection(Vertex.SOUTH, ak, aj);
        connectVertexesWithDirection(Vertex.NORTH, ak, nb_uni_at_dysart);

        OutdoorVertex al = new OutdoorVertex(new GeoPoint(49.809771, -97.141212));
        connectVertexesWithDirection(Vertex.WEST, al, nb_uni_at_dysart);
        connectVertexesWithDirection(Vertex.SOUTH_WEST, al, ak);

        OutdoorVertex am = new OutdoorVertex(new GeoPoint(49.810249, -97.139795));
        connectVertexesWithDirection(Vertex.WEST, am, al);

        OutdoorVertex an = new OutdoorVertex(new GeoPoint(49.810396, -97.139881));
        connectVertexesWithDirection(Vertex.SOUTH, an, am);
        connectVertexesWithDirection(Vertex.EAST, an, q_lot_west);

        OutdoorVertex ao = new OutdoorVertex(new GeoPoint(49.810733, -97.138391));
        connectVertexesWithDirection(Vertex.WEST, ao, am);
        connectVertexesWithDirection(Vertex.NORTH, ao, q_lot_mid);

        OutdoorVertex ap = new OutdoorVertex(new GeoPoint(49.810581, -97.138267));
        connectVertexesWithDirection(Vertex.NORTH, ap, ao);
        connectVertexesWithDirection(Vertex.SOUTH_EAST, ap, st_pauls_north_ent);

        OutdoorVertex aq = new OutdoorVertex(new GeoPoint(49.810897, -97.137906));
        connectVertexesWithDirection(Vertex.WEST, aq, ao);

        OutdoorVertex ar = new OutdoorVertex(new GeoPoint(49.810993, -97.137608));
        connectVertexesWithDirection(Vertex.WEST, ar, aq);
        connectVertexesWithDirection(Vertex.NORTH, ar, q_lot_east);

        OutdoorVertex as = new OutdoorVertex(new GeoPoint(49.810832, -97.137497));
        connectVertexesWithDirection(Vertex.NORTH, as, ar);

        OutdoorVertex at = new OutdoorVertex(new GeoPoint(49.810761, -97.137220));
        connectVertexesWithDirection(Vertex.NORTH_WEST, at, as);
        connectVertexesWithDirection(Vertex.SOUTH_EAST, at, st_johns_north_ent);

        OutdoorVertex au = new OutdoorVertex(new GeoPoint(49.810619, -97.137358));
        connectVertexesWithDirection(Vertex.NORTH, au, as);
        connectVertexesWithDirection(Vertex.NORTH_EAST, au, at);

        OutdoorVertex av = new OutdoorVertex(new GeoPoint(49.811264, -97.136832));
        connectVertexesWithDirection(Vertex.WEST, av, ar);
        connectVertexesWithDirection(Vertex.SOUTH, av, st_johns_east_ent);

        OutdoorVertex aw = new OutdoorVertex(new GeoPoint(49.811342, -97.136668));
        connectVertexesWithDirection(Vertex.WEST, aw, av);
        connectVertexesWithDirection(Vertex.NORTH, aw, k_lot_south);

        OutdoorVertex ax = new OutdoorVertex(new GeoPoint(49.811420, -97.136233));
        connectVertexesWithDirection(Vertex.WEST, ax, aw);

        OutdoorVertex ay = new OutdoorVertex(new GeoPoint(49.811573, -97.135848));
        connectVertexesWithDirection(Vertex.SOUTH_WEST, ay, ax);

        OutdoorVertex az = new OutdoorVertex(new GeoPoint(49.811583, -97.135702));
        connectVertexesWithDirection(Vertex.WEST, az, ay);
        connectVertexesWithDirection(Vertex.NORTH, az, wallace_ent);

        OutdoorVertex ba = new OutdoorVertex(new GeoPoint(49.807354, -97.139717));
        connectVertexesWithDirection(Vertex.NORTH, ba, aj);
        connectVertexesWithDirection(Vertex.WEST, ba, af);

        OutdoorVertex bb = new OutdoorVertex(new GeoPoint(49.807229, -97.139597));
        connectVertexesWithDirection(Vertex.NORTH, bb, ba);

        OutdoorVertex bc = new OutdoorVertex(new GeoPoint(49.807167, -97.139562));
        connectVertexesWithDirection(Vertex.NORTH, bc, bb);
        connectVertexesWithDirection(Vertex.WEST, bc, ae);
        connectVertexesWithDirection(Vertex.SOUTH, bc, nb_uni_chancellor);

        OutdoorVertex bd = new OutdoorVertex(new GeoPoint(49.807078, -97.139433));
        connectVertexesWithDirection(Vertex.NORTH_WEST, bd, nb_uni_chancellor);

        OutdoorVertex be = new OutdoorVertex(new GeoPoint(49.807026, -97.139390));
        connectVertexesWithDirection(Vertex.NORTH, be, bd);
        connectVertexesWithDirection(Vertex.EAST, be, ext_education_west_ent);

        OutdoorVertex bf = new OutdoorVertex(new GeoPoint(49.806758, -97.139193));
        connectVertexesWithDirection(Vertex.NORTH, bf, be);

        OutdoorVertex bg = new OutdoorVertex(new GeoPoint(49.806617, -97.139131));
        connectVertexesWithDirection(Vertex.NORTH_EAST, bg, bf);
        connectVertexesWithDirection(Vertex.NORTH, bg, nb_uni_chancellor);

        OutdoorVertex bh = new OutdoorVertex(new GeoPoint(49.806387, -97.138722));
        connectVertexesWithDirection(Vertex.NORTH_WEST, bh, bg);
        connectVertexesWithDirection(Vertex.EAST, bh, active_living_west_ent);

        OutdoorVertex bi = new OutdoorVertex(new GeoPoint(49.806179, -97.138511));
        connectVertexesWithDirection(Vertex.NORTH_WEST, bi, bh);
        connectVertexesWithDirection(Vertex.NORTH_EAST, bi, active_living_south_west_ent);

        OutdoorVertex bj = new OutdoorVertex(new GeoPoint(49.806074, -97.138655));
        connectVertexesWithDirection(Vertex.NORTH_EAST, bj, bi);
        connectVertexesWithDirection(Vertex.WEST, bj, aa);
        connectVertexesWithDirection(Vertex.SOUTH, bj, x);

        OutdoorVertex bk = new OutdoorVertex(new GeoPoint(49.806404, -97.138007));
        connectVertexesWithDirection(Vertex.WEST, bk, bj);
        connectVertexesWithDirection(Vertex.NORTH, bk, active_living_east_ent);

        OutdoorVertex bl = new OutdoorVertex(new GeoPoint(49.806533, -97.137406));
        connectVertexesWithDirection(Vertex.WEST, bl, bk);
        connectVertexesWithDirection(Vertex.SOUTH, bl, v);
        connectVertexesWithDirection(Vertex.EAST, bl, wb_dafoe_at_agri);

        OutdoorVertex bm = new OutdoorVertex(new GeoPoint(49.806995, -97.136195));
        connectVertexesWithDirection(Vertex.WEST, bm, wb_dafoe_at_agri);
        connectVertexesWithDirection(Vertex.SOUTH_EAST, bm, u);

        OutdoorVertex bn = new OutdoorVertex(new GeoPoint(49.807262, -97.136336));
        connectVertexesWithDirection(Vertex.SOUTH, bn, bm);

        OutdoorVertex bo = new OutdoorVertex(new GeoPoint(49.807472, -97.136055));
        connectVertexesWithDirection(Vertex.WEST, bo, bn);

        OutdoorVertex bp = new OutdoorVertex(new GeoPoint(49.807194, -97.135528));
        connectVertexesWithDirection(Vertex.WEST, bp, bm);

        OutdoorVertex bq = new OutdoorVertex(new GeoPoint(49.807455, -97.135734));
        connectVertexesWithDirection(Vertex.SOUTH, bq, bp);
        connectVertexesWithDirection(Vertex.WEST, bq, bo);

        OutdoorVertex br = new OutdoorVertex(new GeoPoint(49.807427, -97.134809));
        connectVertexesWithDirection(Vertex.WEST, br, bp);
        connectVertexesWithDirection(Vertex.SOUTH, br, m);
        connectVertexesWithDirection(Vertex.NORTH, br, russel_south_ent);

        OutdoorVertex bs = new OutdoorVertex(new GeoPoint(49.807794, -97.133751));
        connectVertexesWithDirection(Vertex.WEST, bs, br);

        OutdoorVertex bt = new OutdoorVertex(new GeoPoint(49.807843, -97.133611));
        connectVertexesWithDirection(Vertex.WEST, bt, bs);

        OutdoorVertex bu = new OutdoorVertex(new GeoPoint(49.807942, -97.133788));
        connectVertexesWithDirection(Vertex.SOUTH_WEST, bu, bs);
        connectVertexesWithDirection(Vertex.SOUTH_EAST, bu, bt);
        connectVertexesWithDirection(Vertex.NORTH, bu, eitc_e1_e3_ent);

        OutdoorVertex bv = new OutdoorVertex(new GeoPoint(49.808184, -97.132593));
        connectVertexesWithDirection(Vertex.WEST, bv, bt);

        OutdoorVertex bw = new OutdoorVertex(new GeoPoint(49.808205, -97.132456));
        connectVertexesWithDirection(Vertex.WEST, bw, bv);
        connectVertexesWithDirection(Vertex.SOUTH, bw, e);

        OutdoorVertex bx = new OutdoorVertex(new GeoPoint(49.808343, -97.132567));
        connectVertexesWithDirection(Vertex.SOUTH, bx, bw);

        OutdoorVertex by = new OutdoorVertex(new GeoPoint(49.808248, -97.132312));
        connectVertexesWithDirection(Vertex.WEST, by, bw);
        connectVertexesWithDirection(Vertex.EAST, by, wb_dafoe_uofm);

        OutdoorVertex bz = new OutdoorVertex(new GeoPoint(49.808422, -97.131735));
        connectVertexesWithDirection(Vertex.WEST, bz, wb_dafoe_uofm);

        OutdoorVertex ca = new OutdoorVertex(new GeoPoint(49.808640, -97.131079));
        connectVertexesWithDirection(Vertex.WEST, ca, bz);

        OutdoorVertex cb = new OutdoorVertex(new GeoPoint(49.808767, -97.130686));
        connectVertexesWithDirection(Vertex.SOUTH, cb, b);

        OutdoorVertex cc = new OutdoorVertex(new GeoPoint(49.808825, -97.130533));
        connectVertexesWithDirection(Vertex.WEST, cc, cb);
        connectVertexesWithDirection(Vertex.NORTH, cc, tier_tunnel);

        OutdoorVertex cd = new OutdoorVertex(new GeoPoint(49.808967, -97.130131));
        connectVertexesWithDirection(Vertex.WEST, cd, cc);

        OutdoorVertex ce = new OutdoorVertex(new GeoPoint(49.809080, -97.130223));
        connectVertexesWithDirection(Vertex.SOUTH, ce, cd);
        connectVertexesWithDirection(Vertex.NORTH, ce, isbister_ent);
        connectVertexesWithDirection(Vertex.SOUTH_WEST, ce, cc);

        OutdoorVertex cf = new OutdoorVertex(new GeoPoint(49.808895, -97.130710));
        connectVertexesWithDirection(Vertex.SOUTH_EAST, cf, cc);
        connectVertexesWithDirection(Vertex.NORTH, cf, tier_south_ent);
        connectVertexesWithDirection(Vertex.SOUTH_WEST, cf, cb);

        OutdoorVertex cg = new OutdoorVertex(new GeoPoint(49.808837, -97.130926));
        connectVertexesWithDirection(Vertex.EAST, cg, cf);
        connectVertexesWithDirection(Vertex.SOUTH_EAST, cg, cb);

        OutdoorVertex ch = new OutdoorVertex(new GeoPoint(49.808767, -97.130686));
        connectVertexesWithDirection(Vertex.WEST, ch, ca);
        connectVertexesWithDirection(Vertex.EAST, ch, cb);
        connectVertexesWithDirection(Vertex.NORTH, ch, cg);

        OutdoorVertex ci = new OutdoorVertex(new GeoPoint(49.809023, -97.131366));
        connectVertexesWithDirection(Vertex.SOUTH, ci, ca);

        OutdoorVertex cj = new OutdoorVertex(new GeoPoint(49.809119, -97.131285));
        connectVertexesWithDirection(Vertex.SOUTH, cj, cg);
        connectVertexesWithDirection(Vertex.EAST, cj, tier_west_ent);
        connectVertexesWithDirection(Vertex.SOUTH_WEST, cj, ci);

        OutdoorVertex ck = new OutdoorVertex(new GeoPoint(49.809372, -97.131616));
        connectVertexesWithDirection(Vertex.SOUTH_EAST, ck, cj);
        connectVertexesWithDirection(Vertex.SOUTH, ck, ci);

        OutdoorVertex cl = new OutdoorVertex(new GeoPoint(49.809303, -97.131776));
        connectVertexesWithDirection(Vertex.EAST, cl, ck);

        OutdoorVertex cm = new OutdoorVertex(new GeoPoint(49.809417, -97.131889));
        connectVertexesWithDirection(Vertex.SOUTH, cm, cl);
        connectVertexesWithDirection(Vertex.SOUTH_EAST, cm, ck);

        OutdoorVertex cn = new OutdoorVertex(new GeoPoint(49.809455, -97.131700));
        connectVertexesWithDirection(Vertex.SOUTH, cn, ck);
        connectVertexesWithDirection(Vertex.SOUTH_WEST, cn, cl);
        connectVertexesWithDirection(Vertex.WEST, cn, cm);

        OutdoorVertex co = new OutdoorVertex(new GeoPoint(49.809455, -97.131700));
        connectVertexesWithDirection(Vertex.WEST, co, cn);
        connectVertexesWithDirection(Vertex.EAST, co, fletcher_ent);

        OutdoorVertex cp = new OutdoorVertex(new GeoPoint(49.809544, -97.131782));
        connectVertexesWithDirection(Vertex.SOUTH, cp, cn);
        connectVertexesWithDirection(Vertex.SOUTH_WEST, cp, cm);
        connectVertexesWithDirection(Vertex.SOUTH_EAST, cp, co);

        OutdoorVertex cq = new OutdoorVertex(new GeoPoint(49.809729, -97.131921));
        connectVertexesWithDirection(Vertex.SOUTH, cq, cp);
        connectVertexesWithDirection(Vertex.NORTH_EAST, cq, eli_dafoe_ent);

        OutdoorVertex cr = new OutdoorVertex(new GeoPoint(49.810027, -97.132191));
        connectVertexesWithDirection(Vertex.SOUTH, cr, cq);
        connectVertexesWithDirection(Vertex.SOUTH_EAST, cr, eli_dafoe_ent);

        OutdoorVertex cs = new OutdoorVertex(new GeoPoint(49.810250, -97.132423));
        connectVertexesWithDirection(Vertex.SOUTH, cs, cr);

        OutdoorVertex ct = new OutdoorVertex(new GeoPoint(49.810222, -97.132560));
        connectVertexesWithDirection(Vertex.EAST, ct, cs);

        OutdoorVertex cu = new OutdoorVertex(new GeoPoint(49.810396, -97.132412));
        connectVertexesWithDirection(Vertex.SOUTH_WEST, cu, cs);

        OutdoorVertex cv = new OutdoorVertex(new GeoPoint(49.810379, -97.132565));
        connectVertexesWithDirection(Vertex.EAST, cv, cu);
        connectVertexesWithDirection(Vertex.SOUTH, cv, cs);

        OutdoorVertex cw = new OutdoorVertex(new GeoPoint(49.810522, -97.132715));
        connectVertexesWithDirection(Vertex.SOUTH, cw, cv);

        OutdoorVertex cx = new OutdoorVertex(new GeoPoint(49.810695, -97.132842));
        connectVertexesWithDirection(Vertex.SOUTH, cx, cw);

        OutdoorVertex cy = new OutdoorVertex(new GeoPoint(49.810776, -97.132800));
        connectVertexesWithDirection(Vertex.SOUTH_WEST, cy, cx);

        OutdoorVertex cz = new OutdoorVertex(new GeoPoint(49.810756, -97.132917));
        connectVertexesWithDirection(Vertex.SOUTH, cz, cx);
        connectVertexesWithDirection(Vertex.EAST, cz, cy);

        OutdoorVertex da = new OutdoorVertex(new GeoPoint(49.810864, -97.133007));
        connectVertexesWithDirection(Vertex.SOUTH, da, cz);
        connectVertexesWithDirection(Vertex.EAST, da, duff_roblin_west_ent);

        OutdoorVertex db = new OutdoorVertex(new GeoPoint(49.811030, -97.133126));
        connectVertexesWithDirection(Vertex.SOUTH, db, da);

        OutdoorVertex dc = new OutdoorVertex(new GeoPoint(49.810973, -97.133447));
        connectVertexesWithDirection(Vertex.EAST, dc, db);
        connectVertexesWithDirection(Vertex.NORTH, dc, armes_machray);

        OutdoorVertex dd = new OutdoorVertex(new GeoPoint(49.811239, -97.132505));
        connectVertexesWithDirection(Vertex.WEST, dd, db);
        connectVertexesWithDirection(Vertex.SOUTH, dd, duff_roblin_north_ent);

        OutdoorVertex de = new OutdoorVertex(new GeoPoint(49.811304, -97.132477));
        connectVertexesWithDirection(Vertex.NORTH_WEST, de, dd);

        OutdoorVertex df = new OutdoorVertex(new GeoPoint(49.811616, -97.131583));
        connectVertexesWithDirection(Vertex.WEST, df, de);
        connectVertexesWithDirection(Vertex.SOUTH, df, uni_college_north_ent);

        OutdoorVertex dg = new OutdoorVertex(new GeoPoint(49.811784, -97.131088));
        connectVertexesWithDirection(Vertex.EAST, dg, df);

        OutdoorVertex dh = new OutdoorVertex(new GeoPoint(49.811758, -97.130976));
        connectVertexesWithDirection(Vertex.NORTH_WEST, dh, dg);
        connectVertexesWithDirection(Vertex.EAST, dh, robson_ent);

        OutdoorVertex di = new OutdoorVertex(new GeoPoint(49.811218, -97.130472));
        connectVertexesWithDirection(Vertex.NORTH, di, dh);

        OutdoorVertex dj = new OutdoorVertex(new GeoPoint(49.810910, -97.130998));
        connectVertexesWithDirection(Vertex.NORTH_EAST, dj, di);
        connectVertexesWithDirection(Vertex.NORTH, dj, uni_college_south_ent);

        OutdoorVertex dk = new OutdoorVertex(new GeoPoint(49.810690, -97.131575));
        connectVertexesWithDirection(Vertex.EAST, dk, dj);
        connectVertexesWithDirection(Vertex.NORTH, dk, human_eco_south_east_ent);
        connectVertexesWithDirection(Vertex.WEST, dk, cu);

        OutdoorVertex dl = new OutdoorVertex(new GeoPoint(49.808633, -97.134835));
        connectVertexesWithDirection(Vertex.SOUTH_WEST, dl, cu);
        connectVertexesWithDirection(Vertex.EAST, dl, human_eco_west_ent);

        OutdoorVertex dm = new OutdoorVertex(new GeoPoint(49.810813, -97.132468));
        connectVertexesWithDirection(Vertex.SOUTH, dm, dl);

        OutdoorVertex dn = new OutdoorVertex(new GeoPoint(49.810881, -97.132456));
        connectVertexesWithDirection(Vertex.SOUTH_WEST, dn, dm);
        connectVertexesWithDirection(Vertex.WEST, dn, cy);

        OutdoorVertex dp = new OutdoorVertex(new GeoPoint(49.810950, -97.132275));
        connectVertexesWithDirection(Vertex.WEST, dp, dn);
        connectVertexesWithDirection(Vertex.NORTH, dp, duff_roblin_south_ent);

        OutdoorVertex dq = new OutdoorVertex(new GeoPoint(49.811603, -97.134784));
        connectVertexesWithDirection(Vertex.SOUTH, dq, parker_ent);

        OutdoorVertex dr = new OutdoorVertex(new GeoPoint(49.811499, -97.135103));
        connectVertexesWithDirection(Vertex.EAST, dr, dq);

        OutdoorVertex ds = new OutdoorVertex(new GeoPoint(49.811585, -97.135175));
        connectVertexesWithDirection(Vertex.SOUTH, ds, dr);

        OutdoorVertex dt = new OutdoorVertex(new GeoPoint(49.811551, -97.135292));
        connectVertexesWithDirection(Vertex.EAST, dt, ds);
        connectVertexesWithDirection(Vertex.NORTH_WEST, dt, az);

        OutdoorVertex du = new OutdoorVertex(new GeoPoint(49.811332, -97.135826));
        connectVertexesWithDirection(Vertex.EAST, du, dr);
        connectVertexesWithDirection(Vertex.NORTH_EAST, du, az);

        OutdoorVertex dv = new OutdoorVertex(new GeoPoint(49.811269, -97.136102));
        connectVertexesWithDirection(Vertex.EAST, dv, du);
        connectVertexesWithDirection(Vertex.NORTH, dv, ax);

        OutdoorVertex dw = new OutdoorVertex(new GeoPoint(49.810693, -97.135637));
        connectVertexesWithDirection(Vertex.NORTH, dw, dv);
        connectVertexesWithDirection(Vertex.EAST, dw, allen_armes_parker);

        OutdoorVertex dx = new OutdoorVertex(new GeoPoint(49.810365, -97.135373));
        connectVertexesWithDirection(Vertex.NORTH, dx, dw);

        OutdoorVertex dy = new OutdoorVertex(new GeoPoint(49.810145, -97.135518));
        connectVertexesWithDirection(Vertex.NORTH_EAST, dy, dx);

        OutdoorVertex dz = new OutdoorVertex(new GeoPoint(49.810143, -97.135289));
        connectVertexesWithDirection(Vertex.WEST, dz, dy);

        OutdoorVertex ea = new OutdoorVertex(new GeoPoint(49.809882, -97.136287));
        connectVertexesWithDirection(Vertex.EAST, ea, dy);
        connectVertexesWithDirection(Vertex.NORTH, ea, robert_schultz_south_ent);

        OutdoorVertex eb = new OutdoorVertex(new GeoPoint(49.809741, -97.136712));
        connectVertexesWithDirection(Vertex.EAST, eb, ea);

        OutdoorVertex ec = new OutdoorVertex(new GeoPoint(49.809711, -97.136783));
        connectVertexesWithDirection(Vertex.EAST, ec, eb);

        OutdoorVertex ed = new OutdoorVertex(new GeoPoint(49.809689, -97.136859));
        connectVertexesWithDirection(Vertex.EAST, ed, ec);

        OutdoorVertex ee = new OutdoorVertex(new GeoPoint(49.809491, -97.136643));
        connectVertexesWithDirection(Vertex.NORTH, ee, ec);

        OutdoorVertex ef = new OutdoorVertex(new GeoPoint(49.809926, -97.137251));
        connectVertexesWithDirection(Vertex.WEST, ef, st_pauls_east_ent);
        connectVertexesWithDirection(Vertex.SOUTH_EAST, ef, ec);

        OutdoorVertex eg = new OutdoorVertex(new GeoPoint(49.809975, -97.136876));
        connectVertexesWithDirection(Vertex.SOUTH, eg, eb);

        OutdoorVertex eh = new OutdoorVertex(new GeoPoint(49.810060, -97.137003));
        connectVertexesWithDirection(Vertex.SOUTH_WEST, eh, ef);
        connectVertexesWithDirection(Vertex.SOUTH_EAST, eh, eg);
        connectVertexesWithDirection(Vertex.NORTH_EAST, eh, robert_schultz_west_ent);

        OutdoorVertex ei = new OutdoorVertex(new GeoPoint(49.810109, -97.137174));
        connectVertexesWithDirection(Vertex.SOUTH_EAST, ei, eh);

        OutdoorVertex ej = new OutdoorVertex(new GeoPoint(49.810435, -97.137311));
        connectVertexesWithDirection(Vertex.SOUTH, ej, ei);
        connectVertexesWithDirection(Vertex.NORTH, ej, au);

        OutdoorVertex ek = new OutdoorVertex(new GeoPoint(49.810507, -97.137157));
        connectVertexesWithDirection(Vertex.WEST, ek, ej);
        connectVertexesWithDirection(Vertex.EAST, ek, st_johns_west_ent);
        connectVertexesWithDirection(Vertex.SOUTH_EAST, ek, robert_schultz_west_ent);

        OutdoorVertex el = new OutdoorVertex(new GeoPoint(49.810201, -97.135155));
        connectVertexesWithDirection(Vertex.WEST, el, dz);

        OutdoorVertex em = new OutdoorVertex(new GeoPoint(49.810087, -97.135058));
        connectVertexesWithDirection(Vertex.NORTH, em, el);
        connectVertexesWithDirection(Vertex.EAST, em, bio_science_west_ent);

        OutdoorVertex en = new OutdoorVertex(new GeoPoint(49.810428, -97.134544));
        connectVertexesWithDirection(Vertex.WEST, en, el);

        OutdoorVertex eo = new OutdoorVertex(new GeoPoint(49.810503, -97.134314));
        connectVertexesWithDirection(Vertex.WEST, eo, en);
        connectVertexesWithDirection(Vertex.EAST, eo, allen_armes);

        OutdoorVertex ep = new OutdoorVertex(new GeoPoint(49.810292, -97.134439));
        connectVertexesWithDirection(Vertex.WEST, ep, bio_science_east_ent);
        connectVertexesWithDirection(Vertex.NORTH, ep, en);

        OutdoorVertex eq = new OutdoorVertex(new GeoPoint(49.810319, -97.134327));
        connectVertexesWithDirection(Vertex.WEST, eq, ep);

        OutdoorVertex er = new OutdoorVertex(new GeoPoint(49.810352, -97.134199));
        connectVertexesWithDirection(Vertex.WEST, er, eq);
        connectVertexesWithDirection(Vertex.NORTH, er, eo);

        OutdoorVertex es = new OutdoorVertex(new GeoPoint(49.810104, -97.133998));
        connectVertexesWithDirection(Vertex.NORTH_WEST, es, eq);
        connectVertexesWithDirection(Vertex.NORTH, es, er);

        OutdoorVertex et = new OutdoorVertex(new GeoPoint(49.809812, -97.133753));
        connectVertexesWithDirection(Vertex.NORTH, et, es);

        OutdoorVertex eu = new OutdoorVertex(new GeoPoint(49.809630, -97.133706));
        connectVertexesWithDirection(Vertex.NORTH_EAST, eu, et);

        OutdoorVertex ev = new OutdoorVertex(new GeoPoint(49.809674, -97.133615));
        connectVertexesWithDirection(Vertex.WEST, ev, eu);
        connectVertexesWithDirection(Vertex.NORTH, ev, et);

        OutdoorVertex ew = new OutdoorVertex(new GeoPoint(49.809514, -97.133726));
        connectVertexesWithDirection(Vertex.NORTH, ew, eu);
        connectVertexesWithDirection(Vertex.NORTH_EAST, ew, ev);

        OutdoorVertex ex = new OutdoorVertex(new GeoPoint(49.809329, -97.133360));
        connectVertexesWithDirection(Vertex.NORTH, ex, ev);
        connectVertexesWithDirection(Vertex.NORTH_WEST, ex, ew);
        connectVertexesWithDirection(Vertex.SOUTH_WEST, ex, uni_centre_east_ent);

        OutdoorVertex ey = new OutdoorVertex(new GeoPoint(49.809329, -97.133360));
        connectVertexesWithDirection(Vertex.NORTH, ey, ex);
        connectVertexesWithDirection(Vertex.WEST, ey, uni_centre_east_ent);

        OutdoorVertex ez = new OutdoorVertex(new GeoPoint(49.809111, -97.133192));
        connectVertexesWithDirection(Vertex.NORTH, ez, ey);

        OutdoorVertex fa = new OutdoorVertex(new GeoPoint(49.809344, -97.133798));
        connectVertexesWithDirection(Vertex.NORTH_EAST, fa, ew);

        OutdoorVertex fb = new OutdoorVertex(new GeoPoint(49.809455, -97.133901));
        connectVertexesWithDirection(Vertex.WEST, fb, uni_centre_north_ent);
        connectVertexesWithDirection(Vertex.EAST, fb, ew);
        connectVertexesWithDirection(Vertex.SOUTH, fb, fa);

        OutdoorVertex fc = new OutdoorVertex(new GeoPoint(49.809036, -97.133131));
        connectVertexesWithDirection(Vertex.NORTH, fc, ez);

        OutdoorVertex fd = new OutdoorVertex(new GeoPoint(49.809048, -97.133398));
        connectVertexesWithDirection(Vertex.EAST, fd, ez);
        connectVertexesWithDirection(Vertex.SOUTH_EAST, fd, fc);

        OutdoorVertex fe = new OutdoorVertex(new GeoPoint(49.808997, -97.133328));
        connectVertexesWithDirection(Vertex.EAST, fe, fc);
        connectVertexesWithDirection(Vertex.NORTH, fe, fd);
        connectVertexesWithDirection(Vertex.WEST, fe, eitc_e2_ent);

        OutdoorVertex ff = new OutdoorVertex(new GeoPoint(49.809025, -97.133489));
        connectVertexesWithDirection(Vertex.SOUTH, ff, eitc_e2_ent);
        connectVertexesWithDirection(Vertex.EAST, ff, fd);

        OutdoorVertex fg = new OutdoorVertex(new GeoPoint(49.809157, -97.133619));
        connectVertexesWithDirection(Vertex.SOUTH_EAST, fg, ff);
        connectVertexesWithDirection(Vertex.NORTH, fg, fa);

        OutdoorVertex fh = new OutdoorVertex(new GeoPoint(49.809228, -97.133893));
        connectVertexesWithDirection(Vertex.NORTH_EAST, fh, fa);
        connectVertexesWithDirection(Vertex.SOUTH_EAST, fh, fg);

        OutdoorVertex fi = new OutdoorVertex(new GeoPoint(49.808946, -97.134713));
        connectVertexesWithDirection(Vertex.EAST, fi, fh);

        OutdoorVertex fj = new OutdoorVertex(new GeoPoint(49.808887, -97.134933));
        connectVertexesWithDirection(Vertex.EAST, fj, fi);

        OutdoorVertex fk = new OutdoorVertex(new GeoPoint(49.809050, -97.135067));
        connectVertexesWithDirection(Vertex.SOUTH, fk, fj);
        connectVertexesWithDirection(Vertex.EAST, fk, uni_centre_west_ent);

        OutdoorVertex fl = new OutdoorVertex(new GeoPoint(49.808804, -97.134824));
        connectVertexesWithDirection(Vertex.NORTH, fl, fj);
        connectVertexesWithDirection(Vertex.NORTH_EAST, fl, fi);

        OutdoorVertex fm = new OutdoorVertex(new GeoPoint(49.808729, -97.134899));
        connectVertexesWithDirection(Vertex.NORTH_EAST, fm, fl);

        OutdoorVertex fn = new OutdoorVertex(new GeoPoint(49.808633, -97.134835));
        connectVertexesWithDirection(Vertex.NORTH, fn, fm);

        OutdoorVertex fo = new OutdoorVertex(new GeoPoint(49.808715, -97.134554));
        connectVertexesWithDirection(Vertex.WEST, fo, fn);
        connectVertexesWithDirection(Vertex.EAST, fo, ff);
        connectVertexesWithDirection(Vertex.SOUTH, fo, eitc_e3_ent);

        OutdoorVertex fp = new OutdoorVertex(new GeoPoint(49.809036, -97.133131));
        connectVertexesWithDirection(Vertex.NORTH, fp, fc);

        OutdoorVertex fq = new OutdoorVertex(new GeoPoint(49.808765, -97.132921));
        connectVertexesWithDirection(Vertex.NORTH, fq, fp);
        connectVertexesWithDirection(Vertex.WEST, fq, eitc_e1_e2_ent);

        OutdoorVertex fr = new OutdoorVertex(new GeoPoint(49.808561, -97.132757));
        connectVertexesWithDirection(Vertex.NORTH, fr, fq);
        connectVertexesWithDirection(Vertex.WEST, fr, eitc_e1_east_ent);
        connectVertexesWithDirection(Vertex.SOUTH, fr, bx);

        OutdoorVertex fs = new OutdoorVertex(new GeoPoint(49.808957, -97.132845));
        connectVertexesWithDirection(Vertex.WEST, fs, fp);

        OutdoorVertex ft = new OutdoorVertex(new GeoPoint(49.809283, -97.132469));
        connectVertexesWithDirection(Vertex.WEST, ft, ez);

        OutdoorVertex fu = new OutdoorVertex(new GeoPoint(49.809432, -97.132016));
        connectVertexesWithDirection(Vertex.WEST, fu, ft);
        connectVertexesWithDirection(Vertex.SOUTH_EAST, fu, cm);

        OutdoorVertex fv = new OutdoorVertex(new GeoPoint(49.809880, -97.133564));
        connectVertexesWithDirection(Vertex.WEST, fv, et);
        connectVertexesWithDirection(Vertex.EAST, fv, ct);

        OutdoorVertex fw = new OutdoorVertex(new GeoPoint(49.810262, -97.133501));
        connectVertexesWithDirection(Vertex.WEST, fw, es);
        connectVertexesWithDirection(Vertex.NORTH, fw, buller_west_ent);

        OutdoorVertex fx = new OutdoorVertex(new GeoPoint(49.810195, -97.133443));
        connectVertexesWithDirection(Vertex.NORTH, fx, fw);
        connectVertexesWithDirection(Vertex.SOUTH_WEST, fx, fv);

        OutdoorVertex fy = new OutdoorVertex(new GeoPoint(49.810396, -97.133120));
        connectVertexesWithDirection(Vertex.NORTH, fy, buller_east_ent);
        connectVertexesWithDirection(Vertex.WEST, fy, fw);
        connectVertexesWithDirection(Vertex.SOUTH_WEST, fy, fx);
        connectVertexesWithDirection(Vertex.EAST, fy, cw);

        OutdoorVertex fz = new OutdoorVertex(new GeoPoint(49.810326, -97.133067));
        connectVertexesWithDirection(Vertex.NORTH, fz, fy);
        connectVertexesWithDirection(Vertex.SOUTH_EAST, fz, ct);
        connectVertexesWithDirection(Vertex.WEST, fz, fx);
        connectVertexesWithDirection(Vertex.NORTH_WEST, fz, fw);

        OutdoorVertex ga = new OutdoorVertex(new GeoPoint(49.808892, -97.132523));
        connectVertexesWithDirection(Vertex.NORTH_WEST, ga, fs);

        OutdoorVertex gb = new OutdoorVertex(new GeoPoint(49.809070, -97.132286));
        connectVertexesWithDirection(Vertex.SOUTH_WEST, gb, ga);
        connectVertexesWithDirection(Vertex.NORTH, gb, ft);

        OutdoorVertex gc = new OutdoorVertex(new GeoPoint(49.809088, -97.131900));
        connectVertexesWithDirection(Vertex.NORTH_WEST, gc, gb);
        connectVertexesWithDirection(Vertex.NORTH_EAST, gc, cl);

        OutdoorVertex gd = new OutdoorVertex(new GeoPoint(49.808939, -97.131629));
        connectVertexesWithDirection(Vertex.NORTH_WEST, gd, gc);
        connectVertexesWithDirection(Vertex.EAST, gd, ci);

        OutdoorVertex ge = new OutdoorVertex(new GeoPoint(49.808718, -97.131607));
        connectVertexesWithDirection(Vertex.NORTH_EAST, ge, gd);
        connectVertexesWithDirection(Vertex.SOUTH_EAST, ge, ca);

        OutdoorVertex gf = new OutdoorVertex(new GeoPoint(49.808570, -97.131707));
        connectVertexesWithDirection(Vertex.NORTH_EAST, gf, ge);
        connectVertexesWithDirection(Vertex.SOUTH_WEST, gf, bz);

        OutdoorVertex gg = new OutdoorVertex(new GeoPoint(49.808480, -97.132023));
        connectVertexesWithDirection(Vertex.EAST, gg, gf);
        connectVertexesWithDirection(Vertex.SOUTH_EAST, gg, wb_dafoe_uofm);

        OutdoorVertex gh = new OutdoorVertex(new GeoPoint(49.808492, -97.132219));
        connectVertexesWithDirection(Vertex.SOUTH_EAST, gh, gg);
        connectVertexesWithDirection(Vertex.SOUTH_WEST, gh, by);

        OutdoorVertex gi = new OutdoorVertex(new GeoPoint(49.808640, -97.132510));
        connectVertexesWithDirection(Vertex.SOUTH_EAST, gi, gh);
        connectVertexesWithDirection(Vertex.NORTH_EAST, gi, ga);

        OutdoorVertex gj = new OutdoorVertex(new GeoPoint(49.808759, -97.135044));
        connectVertexesWithDirection(Vertex.NORTH, gj, uni_centre_south_west_ent);

        OutdoorVertex gk = new OutdoorVertex(new GeoPoint(49.808696, -97.134996));
        connectVertexesWithDirection(Vertex.NORTH, gk, gj);
        connectVertexesWithDirection(Vertex.EAST, gk, fm);
        connectVertexesWithDirection(Vertex.SOUTH_EAST, gk, fn);

        OutdoorVertex gl = new OutdoorVertex(new GeoPoint(49.808591, -97.134935));
        connectVertexesWithDirection(Vertex.NORTH, gl, gk);
        connectVertexesWithDirection(Vertex.EAST, gl, fn);

        OutdoorVertex gm = new OutdoorVertex(new GeoPoint(49.808735, -97.135130));
        connectVertexesWithDirection(Vertex.EAST, gm, gj);
        connectVertexesWithDirection(Vertex.SOUTH_WEST, gm, gl);

        OutdoorVertex gn = new OutdoorVertex(new GeoPoint(49.808726, -97.135545));
        connectVertexesWithDirection(Vertex.SOUTH_EAST, gn, gm);

        OutdoorVertex go = new OutdoorVertex(new GeoPoint(49.808780, -97.135572));
        connectVertexesWithDirection(Vertex.NORTH, go, helen_glass_ent);
        connectVertexesWithDirection(Vertex.SOUTH, go, gn);

        OutdoorVertex gp = new OutdoorVertex(new GeoPoint(49.808695, -97.135616));
        connectVertexesWithDirection(Vertex.NORTH_EAST, gp, go);

        OutdoorVertex gq = new OutdoorVertex(new GeoPoint(49.808799, -97.136042));
        connectVertexesWithDirection(Vertex.SOUTH_EAST, gq, gn);

        OutdoorVertex gr = new OutdoorVertex(new GeoPoint(49.809201, -97.136406));
        connectVertexesWithDirection(Vertex.SOUTH, gr, gq);
        connectVertexesWithDirection(Vertex.NORTH, gr, ee);
        connectVertexesWithDirection(Vertex.WEST, gr, education_north_ent);

        OutdoorVertex gs = new OutdoorVertex(new GeoPoint(49.808463, -97.135552));
        connectVertexesWithDirection(Vertex.NORTH, gs, gp);
        connectVertexesWithDirection(Vertex.EAST, gs, gk);

        OutdoorVertex gt = new OutdoorVertex(new GeoPoint(49.808410, -97.135443));
        connectVertexesWithDirection(Vertex.NORTH_WEST, gt, gs);
        connectVertexesWithDirection(Vertex.EAST, gt, gl);

        OutdoorVertex gu = new OutdoorVertex(new GeoPoint(49.808346, -97.135423));
        connectVertexesWithDirection(Vertex.NORTH, gu, gt  );

        OutdoorVertex gv = new OutdoorVertex(new GeoPoint(49.808298, -97.135538));
        connectVertexesWithDirection(Vertex.EAST, gv, gu);
        connectVertexesWithDirection(Vertex.SOUTH, gv, russel_north_ent);

        OutdoorVertex gw = new OutdoorVertex(new GeoPoint(49.808261, -97.135656));
        connectVertexesWithDirection(Vertex.EAST, gw, gv);

        OutdoorVertex gx = new OutdoorVertex(new GeoPoint(49.808393, -97.135768));
        connectVertexesWithDirection(Vertex.SOUTH, gx, gw);
        connectVertexesWithDirection(Vertex.EAST, gx, gs);

        OutdoorVertex gy = new OutdoorVertex(new GeoPoint(49.808316, -97.135959));
        connectVertexesWithDirection(Vertex.SOUTH_EAST, gy, gx);
        connectVertexesWithDirection(Vertex.EAST, gy, gs);

        OutdoorVertex gz = new OutdoorVertex(new GeoPoint(49.808316, -97.135959));
        connectVertexesWithDirection(Vertex.NORTH_EAST, gz, gy);

        OutdoorVertex ha = new OutdoorVertex(new GeoPoint(49.808372, -97.136237));
        connectVertexesWithDirection(Vertex.SOUTH_EAST, ha, gz);

        OutdoorVertex hb = new OutdoorVertex(new GeoPoint(49.808543, -97.136176));
        connectVertexesWithDirection(Vertex.SOUTH_WEST, hb, ha);

        OutdoorVertex hc = new OutdoorVertex(new GeoPoint(49.808632, -97.136243));
        connectVertexesWithDirection(Vertex.SOUTH, hc, hb);

        OutdoorVertex hd = new OutdoorVertex(new GeoPoint(49.808594, -97.136381));
        connectVertexesWithDirection(Vertex.EAST, hd, hc);
        connectVertexesWithDirection(Vertex.NORTH, hd, education_south_ent);

        OutdoorVertex he = new OutdoorVertex(new GeoPoint(49.808584, -97.136018));
        connectVertexesWithDirection(Vertex.WEST, he, hb);

        OutdoorVertex hf = new OutdoorVertex(new GeoPoint(49.808479, -97.135872));
        connectVertexesWithDirection(Vertex.NORTH_WEST, hf, he);
        connectVertexesWithDirection(Vertex.SOUTH_WEST, hf, gy);

        OutdoorVertex hg = new OutdoorVertex(new GeoPoint(49.808700, -97.136016));
        connectVertexesWithDirection(Vertex.SOUTH_WEST, hg, he);
        connectVertexesWithDirection(Vertex.NORTH, hg, gq);
        connectVertexesWithDirection(Vertex.WEST, hg, hc);

        OutdoorVertex hh = new OutdoorVertex(new GeoPoint(49.808173, -97.135980));
        connectVertexesWithDirection(Vertex.NORTH_EAST, hh, gz);
        connectVertexesWithDirection(Vertex.EAST, hh, gw);

        OutdoorVertex hi = new OutdoorVertex(new GeoPoint(49.808003, -97.136058));
        connectVertexesWithDirection(Vertex.NORTH_EAST, hi, hh);

        OutdoorVertex hj = new OutdoorVertex(new GeoPoint(49.807744, -97.136167));
        connectVertexesWithDirection(Vertex.NORTH_EAST, hj, hi);

        OutdoorVertex hk = new OutdoorVertex(new GeoPoint(49.807702, -97.136259));
        connectVertexesWithDirection(Vertex.EAST, hk, hj);
        connectVertexesWithDirection(Vertex.NORTH, hk, archi2_south_ent);

        OutdoorVertex hl = new OutdoorVertex(new GeoPoint(49.807614, -97.136163));
        connectVertexesWithDirection(Vertex.NORTH, hl, hk);
        connectVertexesWithDirection(Vertex.NORTH_EAST, hl, hj);
        connectVertexesWithDirection(Vertex.SOUTH, hl, bo);

        OutdoorVertex hm = new OutdoorVertex(new GeoPoint(49.807578, -97.136917));
        connectVertexesWithDirection(Vertex.EAST, hm, hk);

        OutdoorVertex hn = new OutdoorVertex(new GeoPoint(49.807637, -97.137089));
        connectVertexesWithDirection(Vertex.SOUTH_EAST, hn, hm);

        OutdoorVertex ho = new OutdoorVertex(new GeoPoint(49.807767, -97.136857));
        connectVertexesWithDirection(Vertex.SOUTH, ho, archi2_west_ent);
        connectVertexesWithDirection(Vertex.SOUTH_WEST, ho, hn);

        OutdoorVertex hp = new OutdoorVertex(new GeoPoint(49.807842, -97.136958));
        connectVertexesWithDirection(Vertex.SOUTH, hp, ho);
        connectVertexesWithDirection(Vertex.SOUTH_WEST, hp, hn);

        OutdoorVertex hq = new OutdoorVertex(new GeoPoint(49.807897, -97.137278));
        connectVertexesWithDirection(Vertex.SOUTH_EAST, hq, hp);

        OutdoorVertex hr = new OutdoorVertex(new GeoPoint(49.808089, -97.136716));
        connectVertexesWithDirection(Vertex.SOUTH_WEST, hr, hp);

        OutdoorVertex hs = new OutdoorVertex(new GeoPoint(49.808150, -97.136517));
        connectVertexesWithDirection(Vertex.WEST, hs, hr);

        OutdoorVertex ht = new OutdoorVertex(new GeoPoint(49.808247, -97.136618));
        connectVertexesWithDirection(Vertex.SOUTH, ht, hs);
        connectVertexesWithDirection(Vertex.EAST, ht, ha);

        OutdoorVertex hu = new OutdoorVertex(new GeoPoint(49.808206, -97.136766));
        connectVertexesWithDirection(Vertex.EAST, hu, ht);
        connectVertexesWithDirection(Vertex.SOUTH, hu, hr);

        OutdoorVertex hv = new OutdoorVertex(new GeoPoint(49.808063, -97.136276));
        connectVertexesWithDirection(Vertex.NORTH_WEST, hv, hs);
        connectVertexesWithDirection(Vertex.SOUTH_EAST, hv, hi);

        OutdoorVertex hw = new OutdoorVertex(new GeoPoint(49.807988, -97.136225));
        connectVertexesWithDirection(Vertex.NORTH, hw, hv);
        connectVertexesWithDirection(Vertex.EAST, hw, hi);
        connectVertexesWithDirection(Vertex.SOUTH, hw, archi2_east_ent);

        OutdoorVertex hx = new OutdoorVertex(new GeoPoint(49.808408, -97.137315));
        connectVertexesWithDirection(Vertex.SOUTH_EAST, hx, hu);

        OutdoorVertex hy = new OutdoorVertex(new GeoPoint(49.808449, -97.137303));
        connectVertexesWithDirection(Vertex.SOUTH_WEST, hy, hx);
        connectVertexesWithDirection(Vertex.EAST, hy, education_west_ent);

        OutdoorVertex hz = new OutdoorVertex(new GeoPoint(49.807896, -97.137743));

        OutdoorVertex ia = new OutdoorVertex(new GeoPoint(49.807896, -97.137743));
        connectVertexesWithDirection(Vertex.WEST, ia, hz);
        connectVertexesWithDirection(Vertex.EAST, ia, hu);
        connectVertexesWithDirection(Vertex.SOUTH, ia, hq);

        OutdoorVertex ib = new OutdoorVertex(new GeoPoint(49.807568, -97.137825));
        connectVertexesWithDirection(Vertex.NORTH_EAST, ib, hz);
        connectVertexesWithDirection(Vertex.WEST, ib, ext_education_east_ent);

        OutdoorVertex ic = new OutdoorVertex(new GeoPoint(49.807560, -97.137677));
        connectVertexesWithDirection(Vertex.WEST, ic, ext_education_east_ent);

        OutdoorVertex id = new OutdoorVertex(new GeoPoint(49.807607, -97.137533));
        connectVertexesWithDirection(Vertex.WEST, id, ic);
        connectVertexesWithDirection(Vertex.SOUTH_EAST, id, hn);
        connectVertexesWithDirection(Vertex.NORTH, id, hz);

        OutdoorVertex ie = new OutdoorVertex(new GeoPoint(49.807805, -97.138122));
        connectVertexesWithDirection(Vertex.EAST, ie, hz);

        OutdoorVertex ig = new OutdoorVertex(new GeoPoint(49.807705, -97.138202));
        connectVertexesWithDirection(Vertex.NORTH_EAST, ig, ie);
        connectVertexesWithDirection(Vertex.WEST, ig, bb);
    }

    //Connects 2 vertex's with directions, the direction that is taken is from vertex1 -> vertex2
    //and connection from vertex2 is opposite of directionV1toV2
    private void connectVertexesWithDirection(int directionV1toV2, Vertex vertex1, Vertex vertex2)
    {
        if(vertex1 != null && vertex2 != null){
            switch(directionV1toV2)
            {
                case Vertex.NORTH:
                    vertex1.addNorthConnection(vertex2);
                    vertex2.addSouthConnection(vertex1);
                    break;

                case Vertex.SOUTH:
                    vertex1.addSouthConnection(vertex2);
                    vertex2.addNorthConnection(vertex1);
                    break;

                case Vertex.EAST:
                    vertex1.addEastConnection(vertex2);
                    vertex2.addWestConnection(vertex1);
                    break;

                case Vertex.WEST:
                    vertex1.addWestConnection(vertex2);
                    vertex2.addEastConnection(vertex1);
                    break;

                case Vertex.NORTH_EAST:
                    vertex1.addNorthEastConnection(vertex2);
                    vertex2.addSouthWestConnection(vertex1);
                    break;

                case Vertex.NORTH_WEST:
                    vertex1.addNorthWestConnection(vertex2);
                    vertex2.addSouthEastConnection(vertex1);
                    break;

                case Vertex.SOUTH_EAST:
                    vertex1.addSouthEastConnection(vertex2);
                    vertex2.addNorthWestConnection(vertex1);
                    break;

                case Vertex.SOUTH_WEST:
                    vertex1.addSouthWestConnection(vertex2);
                    vertex2.addNorthEastConnection(vertex1);
                    break;
            }
        }
    }

    //Connect the vertex's with no specific direction
    private void connectVertexesNoDirection(Vertex vertex1, Vertex vertex2)
    {
        if(vertex1 != null && vertex2 != null)
        {
            vertex1.addConnection(vertex2);
            vertex2.addConnection(vertex1);
        }
    }

    //Connects buildings together that are directly connected in some way (ie tunnels)
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
