package com.artem.uofmcampusmap.buildings.machray;

import com.artem.uofmcampusmap.IndoorVertex;
import com.artem.uofmcampusmap.Vertex;
import com.artem.uofmcampusmap.XYPos;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Artem on 2017-07-07.
 */

public class MachrayIndoorConnections
{
    private HashMap<String, ArrayList<IndoorVertex>> rooms;
    private ArrayList<IndoorVertex> firstFloorStairsElevator;
    private ArrayList<IndoorVertex> secondFloorStairsElevator;
    private ArrayList<IndoorVertex> thirdFloorStairsElevator;
    private ArrayList<IndoorVertex> fourthFloorStairsElevator;
    private ArrayList<IndoorVertex> fifthFloorStairsElevator;
    private IndoorVertex exit;
    private ArrayList<IndoorVertex> armesConnections;
    private IndoorVertex duffRoblinConnection;
    private final String building = "Machray"; //todo switch this to resources?

    public MachrayIndoorConnections()
    {
        rooms = new HashMap<>();
        firstFloorStairsElevator = new ArrayList<>();
        secondFloorStairsElevator = new ArrayList<>();
        thirdFloorStairsElevator = new ArrayList<>();
        fourthFloorStairsElevator = new ArrayList<>();
        fifthFloorStairsElevator = new ArrayList<>();
        armesConnections = new ArrayList<>();
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

    private void connectVertex(Vertex vertex1, Vertex vertex2)
    {
        if(vertex1 instanceof IndoorVertex && vertex2 instanceof IndoorVertex)
        {
            IndoorVertex indoorV1 = (IndoorVertex) vertex1;
            XYPos firstPos = indoorV1.getPosition();
            IndoorVertex indoorV2 = (IndoorVertex) vertex2;
            XYPos secondPos = indoorV2.getPosition();

            if(firstPos.getX() > secondPos.getX() && Math.floor(firstPos.getY() - secondPos.getY()) == 0.0)
            {
                indoorV1.addWestConnection(indoorV2);
                indoorV2.addEastConnection(indoorV1);
            }
            else if(firstPos.getX() < secondPos.getX() && Math.floor(firstPos.getY() - secondPos.getY()) == 0.0)
            {
                indoorV1.addEastConnection(indoorV2);
                indoorV2.addWestConnection(indoorV1);
            }
            else if(firstPos.getY() > secondPos.getY() && Math.floor(firstPos.getX() - secondPos.getX()) == 0.0)
            {
                indoorV1.addSouthConnection(indoorV2);
                indoorV2.addNorthConnection(indoorV1);
            }
            else if(firstPos.getY() < secondPos.getY() && Math.floor(firstPos.getX() - secondPos.getX()) == 0.0)
            {
                indoorV1.addNorthConnection(indoorV2);
                indoorV2.addSouthConnection(indoorV1);
            }
            else
            {
                vertex1.addConnection(vertex2);
                vertex2.addConnection(vertex1);
            }

            vertex1.addConnection(vertex2);
            vertex2.addConnection(vertex1);

        }
        else
        {
            vertex1.addConnection(vertex2);
            vertex2.addConnection(vertex1);
        }
    }

    private void populateConnections()
    {
        IndoorVertex elevatorFloor1 = new IndoorVertex(building, new XYPos(50, 40), 1);
        IndoorVertex stairsFloor1 = new IndoorVertex(building, new XYPos(56.25, 28.13), 1);

        IndoorVertex stairsFloor2 = new IndoorVertex(building, new XYPos(49.43, 160.49), 2);
        IndoorVertex elevatorFloor2 = new IndoorVertex(building, new XYPos(44.19, 151.19), 2);

        //Stair connections to one another
        connectVertex(stairsFloor1, stairsFloor2);

        //Elevator connections to one another
        connectVertex(elevatorFloor1, elevatorFloor2);

        //Creation of each floors connections
        createFloor1Connections(stairsFloor1, elevatorFloor1);
        createFloor2Connections(stairsFloor2, elevatorFloor2);

    }

    private void createFloor1Connections(IndoorVertex stairs, IndoorVertex elevator)
    {
        IndoorVertex armesConnection = new IndoorVertex(building, new XYPos(0, 31.25), 1);
        IndoorVertex _41_31 = new IndoorVertex(building, new XYPos(41.25, 31.25), 1);
        IndoorVertex _50_28 = new IndoorVertex(building, new XYPos(50, 28.13), 1);
        IndoorVertex _50_21 = new IndoorVertex(building, new XYPos(50, 21.88), 1);
        IndoorVertex _137_21 = new IndoorVertex(building, new XYPos(137.5, 21.88), 1);
        IndoorVertex _137_27 = new IndoorVertex(building, new XYPos(137.5, 27.5), 1);
        duffRoblinConnection = new IndoorVertex(building, new XYPos(137.5, -37.5), 1);
        IndoorVertex room_113_leftSide = new IndoorVertex(building, new XYPos(110, 27.5), 1);
        IndoorVertex _137_77 = new IndoorVertex(building, new XYPos(137.5, 77.5), 1);
        IndoorVertex room_108 = new IndoorVertex(building, new XYPos(97.5, 87.5), 1);
        IndoorVertex room_111 = new IndoorVertex(building, new XYPos(120, 81.25), 1);
        IndoorVertex room_112 = new IndoorVertex(building, new XYPos(108.75, 75), 1);
        IndoorVertex _165_21 = new IndoorVertex(building, new XYPos(165, 21.88), 1);
        IndoorVertex room_115 = new IndoorVertex(building, new XYPos(168.75, 51.25), 1);
        IndoorVertex room_113_rightSide = new IndoorVertex(building, new XYPos(162.5, 27.5), 1);
        IndoorVertex room_106 = new IndoorVertex(building, new XYPos(90, 72.5), 1);
        IndoorVertex room_107 = new IndoorVertex(building, new XYPos(90, 80), 1);
        IndoorVertex _93_72 = new IndoorVertex(building, new XYPos(93.75, 72.5), 1);
        IndoorVertex _50_31 = new IndoorVertex(building, new XYPos(50, 31.25), 1);
        IndoorVertex _137_72 = new IndoorVertex(building, new XYPos(137.5, 72.5), 1);

        //Other building connections
        armesConnections.add(armesConnection);

        //rooms
        ArrayList<IndoorVertex> rm106 = new ArrayList<>();
        rm106.add(room_106);

        rooms.put("106", rm106);

        ArrayList<IndoorVertex> rm107 = new ArrayList<>();
        rm107.add(room_107);

        rooms.put("107", rm107);

        ArrayList<IndoorVertex> rm108 = new ArrayList<>();
        rm108.add(room_108);

        rooms.put("108", rm108);

        ArrayList<IndoorVertex> rm111 = new ArrayList<>();
        rm111.add(room_111);

        rooms.put("111", rm111);

        ArrayList<IndoorVertex> rm112= new ArrayList<>();
        rm112.add(room_112);

        rooms.put("112", rm112);

        ArrayList<IndoorVertex> rm115 = new ArrayList<>();
        rm115.add(room_115);

        rooms.put("115", rm115);

        ArrayList<IndoorVertex> rm113 = new ArrayList<>();
        rm113.add(room_113_rightSide);
        rm113.add(room_113_leftSide);

        rooms.put("113", rm113);

        //Connections between this floor
        connectVertex(armesConnection, _41_31);
        connectVertex(_41_31, _50_31);
        connectVertex(_50_31, elevator);
        connectVertex(_50_31, _50_28);
        connectVertex(_50_28, stairs);
        connectVertex(_50_28, _50_21);
        connectVertex(_50_21, _137_21);
        connectVertex(_137_21, _137_27);
        connectVertex(_137_21, _165_21);
        connectVertex(_137_21, duffRoblinConnection);
        connectVertex(_165_21, room_115);
        connectVertex(_165_21, room_113_rightSide);
        connectVertex(room_113_rightSide, room_115);
        connectVertex(_137_27, room_113_leftSide);
        connectVertex(_137_27, _137_72);
        connectVertex(_137_72, _93_72);
        connectVertex(_137_72, room_112);
        connectVertex(_137_72, _137_77);
        connectVertex(_137_77, room_108);
        connectVertex(_137_77, room_111);
        connectVertex(_137_77, room_112);
        connectVertex(room_108, room_111);
        connectVertex(room_106, room_107);
        connectVertex(room_106, _93_72);
    }

    private void createFloor2Connections(IndoorVertex stairs, IndoorVertex elevator)
    {
        //Points on the map
        IndoorVertex armes_connection_top = new IndoorVertex(building, new XYPos(0, 151.19), 2);
        IndoorVertex armes_connection_bottom = new IndoorVertex(building, new XYPos(0, 158.75), 2);
        IndoorVertex rm239_fac_science = new IndoorVertex(building, new XYPos(30.24, 97.11), 2);
        IndoorVertex _30_151 = new IndoorVertex(building, new XYPos(30.24, 151.19), 2);
        IndoorVertex _30_179 = new IndoorVertex(building, new XYPos(30.24, 179.65), 2);
        exit = new IndoorVertex(building, new XYPos(30.24, 188.41), 2);
        IndoorVertex rm211_library = new IndoorVertex(building, new XYPos(80.25, 173.29), 2);
        IndoorVertex _44_160 = new IndoorVertex(building, new XYPos(44.19, 160.49), 2);
        IndoorVertex _44_173 = new IndoorVertex(building, new XYPos(44.19, 173.29), 2);
        IndoorVertex _30_173 = new IndoorVertex(building, new XYPos(30.24, 173.29), 2);
        IndoorVertex _30_158 = new IndoorVertex(building, new XYPos(30.24, 158.75), 2);
        IndoorVertex _44_154 = new IndoorVertex(building, new XYPos(44.19, 154.1), 2);
        IndoorVertex _30_154 = new IndoorVertex(building, new XYPos(30.24, 154.1), 2);

        //Rooms
        ArrayList<IndoorVertex> rm211 = new ArrayList<>();
        rm211.add(rm211_library);

        rooms.put("211", rm211);

        ArrayList<IndoorVertex> rm239 = new ArrayList<>();
        rm239.add(rm239_fac_science);

        rooms.put("239", rm239);

        //Connections to other buildings
        armesConnections.add(armes_connection_bottom);
        armesConnections.add(armes_connection_top);

        //Connections to other points
        connectVertex(armes_connection_bottom, _30_158);
        connectVertex(armes_connection_top, _30_151);
        connectVertex(_30_151, rm239_fac_science);
        connectVertex(_30_151, _30_154);
        connectVertex(_30_154, _44_154);
        connectVertex(_30_154, _30_158);
        connectVertex(_30_158, _44_154);
        connectVertex(_30_158, _44_160);
        connectVertex(_30_158, _30_173);
        connectVertex(_30_173, _44_173);
        connectVertex(_30_173, _30_179);
        connectVertex(_30_179, exit);
        connectVertex(_44_154, elevator);
        connectVertex(_44_154, _44_160);
        connectVertex(_44_160, stairs);
        connectVertex(_44_160, _44_173);
        connectVertex(_44_173, rm211_library);

    }

    private void createFloor3Connections(IndoorVertex stairs1, IndoorVertex elevator)
    {
        //Points
        IndoorVertex _ = new IndoorVertex(building, new XYPos(), 3);
        IndoorVertex _ = new IndoorVertex(building, new XYPos(), 3);
        IndoorVertex _ = new IndoorVertex(building, new XYPos(), 3);
        IndoorVertex _ = new IndoorVertex(building, new XYPos(), 3);
        IndoorVertex _ = new IndoorVertex(building, new XYPos(), 3);
        IndoorVertex _ = new IndoorVertex(building, new XYPos(), 3);
        IndoorVertex _ = new IndoorVertex(building, new XYPos(), 3);
        IndoorVertex _ = new IndoorVertex(building, new XYPos(), 3);
        IndoorVertex _ = new IndoorVertex(building, new XYPos(), 3);
        IndoorVertex _ = new IndoorVertex(building, new XYPos(), 3);
        IndoorVertex _ = new IndoorVertex(building, new XYPos(), 3);
        IndoorVertex _ = new IndoorVertex(building, new XYPos(), 3);
        IndoorVertex _ = new IndoorVertex(building, new XYPos(), 3);

        //Rooms

        //Connections between points

    }

    private void createFloor4Connections(IndoorVertex stairs1, IndoorVertex elevator)
    {
        //IndoorVertex _ = new IndoorVertex(building, new XYPos(), 4);

        //Points

        //Rooms

        //Connections between points
    }

    private void createFloor5Connections(IndoorVertex stairs1, IndoorVertex elevator)
    {
        //IndoorVertex _ = new IndoorVertex(building, new XYPos(), 5);

        //Points

        //Rooms

        //Connections between points
    }

    public IndoorVertex getExit() {
        return exit;
    }

    public IndoorVertex getDuffRoblinConnection() {
        return duffRoblinConnection;
    }
}
