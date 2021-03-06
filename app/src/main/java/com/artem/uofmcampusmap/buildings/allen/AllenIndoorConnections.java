package com.artem.uofmcampusmap.buildings.allen;

import android.content.res.Resources;

import com.artem.uofmcampusmap.IndoorVertex;
import com.artem.uofmcampusmap.R;
import com.artem.uofmcampusmap.XYPos;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Artem on 2017-07-12.
 */

public class AllenIndoorConnections {
    private HashMap<String, ArrayList<IndoorVertex>> rooms;
    private ArrayList<IndoorVertex> firstFloorStairsElevator;
    private ArrayList<IndoorVertex> secondFloorStairsElevator;
    private ArrayList<IndoorVertex> thirdFloorStairsElevator;
    private ArrayList<IndoorVertex> fourthFloorStairsElevator;
    private ArrayList<IndoorVertex> fifthFloorStairsElevator;
    private IndoorVertex armesTunnelConnection;
    private IndoorVertex armesNorthConnection; //Floor 2 connection
    private IndoorVertex armesSouthConnection; //Floor 2 connection
    private IndoorVertex bioSciTunnelConnection;
    private final String building;

    public AllenIndoorConnections(Resources resources)
    {
        rooms = new HashMap<>();
        firstFloorStairsElevator = new ArrayList<>();
        secondFloorStairsElevator = new ArrayList<>();
        thirdFloorStairsElevator = new ArrayList<>();
        fourthFloorStairsElevator = new ArrayList<>();
        fifthFloorStairsElevator = new ArrayList<>();
        building = resources.getString(R.string.allen);
        populateConnections();
    }

    public IndoorVertex getClosestStairsToRoom(IndoorVertex room)
    {
        IndoorVertex closestStairs = null;
        int bestDistance = Integer.MAX_VALUE;
        int tempDistance;
        ArrayList<IndoorVertex> allStairs = new ArrayList<>();

        if(room != null)
        {
            if(room.getFloor() == 1)
            {
                allStairs.addAll(firstFloorStairsElevator);
            }
            else if(room.getFloor() == 2)
            {
                allStairs.addAll(secondFloorStairsElevator);
            }
            if(room.getFloor() == 3)
            {
                allStairs.addAll(thirdFloorStairsElevator);
            }
            if(room.getFloor() == 4)
            {
                allStairs.addAll(fourthFloorStairsElevator);
            }
            if(room.getFloor() == 5)
            {
                allStairs.addAll(fifthFloorStairsElevator);
            }

            for(IndoorVertex stairs : allStairs)
            {
                tempDistance = room.getDistanceFrom(stairs);
                if(tempDistance < bestDistance)
                {
                    closestStairs = stairs;
                    bestDistance = tempDistance;
                }
            }

        }

        return closestStairs;
    }

    //todo make this better for rooms that have multi entrances or that have entrances on different floors
    //Finds a room based on its number
    public IndoorVertex findRoom(String roomNumb)
    {
        ArrayList<IndoorVertex> roomList;
        IndoorVertex room = null;

        if(rooms.containsKey(roomNumb))
        {
            roomList = rooms.get(roomNumb);

            if(roomList.size() > 0)
                room = roomList.get(0);
        }

        return room;
    }

    private void populateConnections()
    {
        //Stairs / Elevator creations
        IndoorVertex stairsLeftFloor1 = new IndoorVertex(building, new XYPos(-62.5, 68.75), 1);
        IndoorVertex stairsRightFloor1 = new IndoorVertex(building, new XYPos(-153.75, 68.75), 1);
        IndoorVertex elevatorFloor1 = new IndoorVertex(building, new XYPos(-72.5, 68.75), 1);

        firstFloorStairsElevator.add(stairsLeftFloor1);
        firstFloorStairsElevator.add(stairsRightFloor1);
        firstFloorStairsElevator.add(elevatorFloor1);

        IndoorVertex stairsLeftFloor2 = new IndoorVertex(building, new XYPos(-62.5, 43.75), 2);
        IndoorVertex stairsRightFloor2 = new IndoorVertex(building, new XYPos(-153.75, 43.75), 2);
        IndoorVertex elevatorFloor2 = new IndoorVertex(building, new XYPos(-71.25, 43.75), 2);

        secondFloorStairsElevator.add(stairsLeftFloor2);
        secondFloorStairsElevator.add(stairsRightFloor2);
        secondFloorStairsElevator.add(elevatorFloor2);

        IndoorVertex stairsLeftFloor3 = new IndoorVertex(building, new XYPos(-62.5, 43.75), 3);
        IndoorVertex stairsRightFloor3 = new IndoorVertex(building, new XYPos(-148.75, 41.25), 3);
        IndoorVertex elevatorFloor3 = new IndoorVertex(building, new XYPos(-71.25, 43.75), 3);

        thirdFloorStairsElevator.add(stairsLeftFloor3);
        thirdFloorStairsElevator.add(stairsRightFloor3);
        thirdFloorStairsElevator.add(elevatorFloor3);

        IndoorVertex stairsLeftFloor4 = new IndoorVertex(building, new XYPos(-62.5, 43.75), 4);
        IndoorVertex stairsRightFloor4 = new IndoorVertex(building, new XYPos(-148.75, 41.25), 4);
        IndoorVertex elevatorFloor4 = new IndoorVertex(building, new XYPos(-71.25, 43.75), 4);

        fourthFloorStairsElevator.add(stairsLeftFloor4);
        fourthFloorStairsElevator.add(stairsRightFloor4);
        fourthFloorStairsElevator.add(elevatorFloor4);

        IndoorVertex stairsLeftFloor5 = new IndoorVertex(building, new XYPos(-62.5, 43.75), 5);
        IndoorVertex stairsRightFloor5 = new IndoorVertex(building, new XYPos(-148.75, 41.25), 5);
        IndoorVertex elevatorFloor5 = new IndoorVertex(building, new XYPos(-71.25, 43.75), 5);

        fifthFloorStairsElevator.add(stairsLeftFloor5);
        fifthFloorStairsElevator.add(stairsRightFloor5);
        fifthFloorStairsElevator.add(elevatorFloor5);

        //Connections between floors
        //Stairs left
        stairsLeftFloor1.connectVertex(stairsLeftFloor2, true, false);
        stairsLeftFloor1.connectVertex(stairsLeftFloor3, true, false);
        stairsLeftFloor1.connectVertex(stairsLeftFloor4, true, false);
        stairsLeftFloor1.connectVertex(stairsLeftFloor5, true, false);
        stairsLeftFloor2.connectVertex(stairsLeftFloor3, true, false);
        stairsLeftFloor2.connectVertex(stairsLeftFloor4, true, false);
        stairsLeftFloor2.connectVertex(stairsLeftFloor5, true, false);
        stairsLeftFloor3.connectVertex(stairsLeftFloor4, true, false);
        stairsLeftFloor3.connectVertex(stairsLeftFloor5, true, false);
        stairsLeftFloor4.connectVertex(stairsLeftFloor5, true, false);

        //Stairs right
        stairsRightFloor1.connectVertex(stairsRightFloor2, true, false);
        stairsRightFloor1.connectVertex(stairsRightFloor3, true, false);
        stairsRightFloor1.connectVertex(stairsRightFloor4, true, false);
        stairsRightFloor1.connectVertex(stairsRightFloor5, true, false);
        stairsRightFloor2.connectVertex(stairsRightFloor3, true, false);
        stairsRightFloor2.connectVertex(stairsRightFloor4, true, false);
        stairsRightFloor2.connectVertex(stairsRightFloor5, true, false);
        stairsRightFloor3.connectVertex(stairsRightFloor4, true, false);
        stairsRightFloor3.connectVertex(stairsRightFloor5, true, false);
        stairsRightFloor4.connectVertex(stairsRightFloor5, true, false);

        //Elevator
        elevatorFloor1.connectVertex(elevatorFloor2, true, false);
        elevatorFloor1.connectVertex(elevatorFloor3, true, false);
        elevatorFloor1.connectVertex(elevatorFloor4, true, false);
        elevatorFloor1.connectVertex(elevatorFloor5, true, false);
        elevatorFloor2.connectVertex(elevatorFloor3, true, false);
        elevatorFloor2.connectVertex(elevatorFloor4, true, false);
        elevatorFloor2.connectVertex(elevatorFloor5, true, false);
        elevatorFloor3.connectVertex(elevatorFloor4, true, false);
        elevatorFloor3.connectVertex(elevatorFloor5, true, false);
        elevatorFloor4.connectVertex(elevatorFloor5, true, false);

        //Creations of each floors connections
        createFloor1Connections(stairsLeftFloor1, stairsRightFloor1, elevatorFloor1);
        createFloor2Connections(stairsLeftFloor2, stairsRightFloor2, elevatorFloor2);
        createFloor3Connections(stairsLeftFloor3, stairsRightFloor3, elevatorFloor3);
        createFloor4Connections(stairsLeftFloor4, stairsRightFloor4, elevatorFloor4);
        createFloor5Connections(stairsLeftFloor5, stairsRightFloor5, elevatorFloor5);
    }

    private void createFloor1Connections(IndoorVertex stairsLeft, IndoorVertex stairsRight, IndoorVertex elevator)
    {
        //Points on the floor
        armesTunnelConnection = new IndoorVertex(building, new XYPos(-181.25, 68.75), 1);
        IndoorVertex _155_31 = new IndoorVertex(building, new XYPos(-155, 31.25), 1);
        IndoorVertex _172_60 = new IndoorVertex(building, new XYPos(-172, 60), 1);
        IndoorVertex _157_60 = new IndoorVertex(building, new XYPos(-157.5, 60), 1);
        IndoorVertex _67_60 = new IndoorVertex(building, new XYPos(-67.5, 60), 1);
        IndoorVertex _157_101 = new IndoorVertex(building, new XYPos(-157.5, 101.25), 1); //rm114
        IndoorVertex _67_68 = new IndoorVertex(building, new XYPos(-67.5, 68.75), 1);
        IndoorVertex _157_68 = new IndoorVertex(building, new XYPos(-157.5, 68.75), 1);
        bioSciTunnelConnection = new IndoorVertex(building, new XYPos(-141, 0), 1);

        //Rooms
        ArrayList<IndoorVertex> rm114 = new ArrayList<>();
        rm114.add(_157_101);

        rooms.put("114", rm114);

        //Connections between points
        armesTunnelConnection.connectVertex(_172_60, true, false);
        armesTunnelConnection.connectVertex(_157_68, true, false);
        _172_60.connectVertex(_155_31, true, false);
        _172_60.connectVertex(_157_60, true, false);
        _155_31.connectVertex(bioSciTunnelConnection, true, false);
        _157_68.connectVertex(_157_101, true, false);
        _157_68.connectVertex(stairsRight, true, false);
        _157_68.connectVertex(_157_60, true, false);
        _157_60.connectVertex(_67_60, true, false);
        _67_60.connectVertex(_67_68, true, false);
        _67_68.connectVertex(stairsLeft, true, false);
        _67_68.connectVertex(elevator, true, false);

    }

    private void createFloor2Connections(IndoorVertex stairsLeft, IndoorVertex stairsRight, IndoorVertex elevator)
    {
        //Points on the floor
        armesSouthConnection = new IndoorVertex(building, new XYPos(-200, 43.75), 2);
        armesNorthConnection = new IndoorVertex(building, new XYPos(-200, 51.25), 2);
        IndoorVertex _182_43 = new IndoorVertex(building, new XYPos(-182.5, 43.75), 2);
        IndoorVertex _160_43 = new IndoorVertex(building, new XYPos(-160, 43.75), 2);
        IndoorVertex _160_20 = new IndoorVertex(building, new XYPos(-160, 20), 2);
        IndoorVertex _160_65 = new IndoorVertex(building, new XYPos(-160, 65), 2);
        IndoorVertex _65_65 = new IndoorVertex(building, new XYPos(-65, 65), 2);
        IndoorVertex _65_43 = new IndoorVertex(building, new XYPos(-65, 43.75), 2);
        IndoorVertex _65_20 = new IndoorVertex(building, new XYPos(-65, 20), 2);
        IndoorVertex _162_20 = new IndoorVertex(building, new XYPos(-162.5, 20), 2); //rm 201

        //Rooms
        ArrayList<IndoorVertex> rm201 = new ArrayList<>();
        rm201.add(_162_20);

        rooms.put("201", rm201);

        //Connections between points
        _182_43.connectVertex(armesNorthConnection, true, false);
        _182_43.connectVertex(armesSouthConnection, true, false);
        _182_43.connectVertex(_160_43, true, false);
        _160_43.connectVertex(stairsRight, true, false);
        _160_43.connectVertex(_160_20, true, false);
        _160_43.connectVertex(_160_65, true, false);
        _160_20.connectVertex(_162_20, true, false);
        _160_20.connectVertex(_65_20, true, false);
        _65_20.connectVertex(_65_43, true, false);
        _65_43.connectVertex(stairsLeft, true, false);
        _65_43.connectVertex(elevator, true, false);
        _65_43.connectVertex(_65_65, true, false);
        _65_65.connectVertex(_160_65, true, false);
    }

    private void createFloor3Connections(IndoorVertex stairsLeft, IndoorVertex stairsRight, IndoorVertex elevator)
    {
        //Points on the floor
        IndoorVertex _66_43 = new IndoorVertex(building, new XYPos(-66.25, 43.75), 3);
        IndoorVertex _148_36 = new IndoorVertex(building, new XYPos(-148.75, 36.25), 3);
        IndoorVertex _157_36 = new IndoorVertex(building, new XYPos(-157.5, 36.25), 3);
        IndoorVertex _157_65 = new IndoorVertex(building, new XYPos(-157.5, 65), 3);
        IndoorVertex _162_67 = new IndoorVertex(building, new XYPos(-162.5, 67.5), 3); //rm 316
        IndoorVertex _66_65 = new IndoorVertex(building, new XYPos(-66.25, 65), 3);
        IndoorVertex _21_65 = new IndoorVertex(building, new XYPos(-21.25, 65), 3); //rm 319
        IndoorVertex _66_36 = new IndoorVertex(building, new XYPos(-66.25, 36.25), 3);

        //Rooms
        ArrayList<IndoorVertex> rm319 = new ArrayList<>();
        rm319.add(_21_65);

        rooms.put("319", rm319);

        ArrayList<IndoorVertex> rm316 = new ArrayList<>();
        rm316.add(_162_67);

        rooms.put("316", rm316);

        //Connections between points
        _66_43.connectVertex(stairsLeft, true, false);
        _66_43.connectVertex(elevator, true, false);
        _66_43.connectVertex(_66_65, true, false);
        _66_43.connectVertex(_66_36, true, false);
        _148_36.connectVertex(stairsRight, true, false);
        _148_36.connectVertex(_157_36, true, false);
        _148_36.connectVertex(_66_36, true, false);
        _157_65.connectVertex(_157_36, true, false);
        _157_65.connectVertex(_162_67, true, false);
        _157_65.connectVertex(_66_65, true, false);
        _66_65.connectVertex(_21_65, true, false);
    }

    private void createFloor4Connections(IndoorVertex stairsLeft, IndoorVertex stairsRight, IndoorVertex elevator)
    {
        //Points on the floor
        IndoorVertex _66_43 = new IndoorVertex(building, new XYPos(-66.25, 43.75), 4);
        IndoorVertex _148_36 = new IndoorVertex(building, new XYPos(-148.75, 36.25), 4);
        IndoorVertex _66_36 = new IndoorVertex(building, new XYPos(-66.25, 36.25), 4);
        IndoorVertex _66_31 = new IndoorVertex(building, new XYPos(-66.25, 31.25), 4); //rm405
        IndoorVertex _148_31 = new IndoorVertex(building, new XYPos(-148.75, 31.25), 4); //rm403

        //Rooms
        ArrayList<IndoorVertex> rm405 = new ArrayList<>();
        rm405.add(_66_31);

        rooms.put("405", rm405);

        ArrayList<IndoorVertex> rm403 = new ArrayList<>();
        rm403.add(_148_31);

        rooms.put("403", rm403);

        //Connections between points
        _148_36.connectVertex(stairsRight, true, false);
        _148_36.connectVertex(_148_31, true, false);
        _148_36.connectVertex(_66_36, true, false);
        _66_36.connectVertex(_66_43, true, false);
        _66_36.connectVertex(_66_31, true, false);
        _66_31.connectVertex(stairsLeft, true, false);
        _66_31.connectVertex(elevator, true, false);
    }

    private void createFloor5Connections(IndoorVertex stairsLeft, IndoorVertex stairsRight, IndoorVertex elevator)
    {
        //Points on the floor
        IndoorVertex _66_43 = new IndoorVertex(building, new XYPos(-66.25, 43.75), 5);
        IndoorVertex _66_37 = new IndoorVertex(building, new XYPos(-66.25, 37.5), 5);
        IndoorVertex _16_37 = new IndoorVertex(building, new XYPos(-16.25, 37.5), 5);
        IndoorVertex _16_71 = new IndoorVertex(building, new XYPos(-16.25, 71.25), 5);
        IndoorVertex _66_71 = new IndoorVertex(building, new XYPos(-66.25, 71.25), 5);
        IndoorVertex _148_37 = new IndoorVertex(building, new XYPos(-148.75, 37.5), 5);
        IndoorVertex _152_37 = new IndoorVertex(building, new XYPos(-152.5, 37.5), 5); //rm 522

        //Rooms
        ArrayList<IndoorVertex> rm522 = new ArrayList<>();
        rm522.add(_152_37);

        rooms.put("522", rm522);

        //Connections between points
        _66_43.connectVertex(stairsLeft, true, false);
        _66_43.connectVertex(elevator, true, false);
        _66_43.connectVertex(_66_37, true, false);
        _66_43.connectVertex(_66_71, true, false);
        _16_71.connectVertex(_66_71, true, false);
        _16_71.connectVertex(_16_37, true, false);
        _66_37.connectVertex(_16_37, true, false);
        _148_37.connectVertex(_66_37, true, false);
        _148_37.connectVertex(stairsRight, true, false);
        _148_37.connectVertex(_152_37, true, false);
    }

    public IndoorVertex getArmesTunnelConnection() {
        return armesTunnelConnection;
    }

    public IndoorVertex getArmesNorthConnection() {
        return armesNorthConnection;
    }

    public IndoorVertex getArmesSouthConnection() {
        return armesSouthConnection;
    }

    public IndoorVertex getBioSciTunnelConnection() {
        return bioSciTunnelConnection;
    }
}
