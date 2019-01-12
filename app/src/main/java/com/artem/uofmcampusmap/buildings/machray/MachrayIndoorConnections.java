package com.artem.uofmcampusmap.buildings.machray;

import android.content.res.Resources;

import com.artem.uofmcampusmap.IndoorVertex;
import com.artem.uofmcampusmap.R;
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
    private IndoorVertex armesConnectionTunnel;
    private IndoorVertex armesConnectionNorth;
    private IndoorVertex armesConnectionSouth;
    private IndoorVertex duffRoblinConnection;
    private final String building;

    public MachrayIndoorConnections(Resources resources)
    {
        rooms = new HashMap<>();
        firstFloorStairsElevator = new ArrayList<>();
        secondFloorStairsElevator = new ArrayList<>();
        thirdFloorStairsElevator = new ArrayList<>();
        fourthFloorStairsElevator = new ArrayList<>();
        fifthFloorStairsElevator = new ArrayList<>();
        building = resources.getString(R.string.machray);
        populateConnections();
    }

    //Finds the closest stairs on the floor that the specified room is on
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

    //Connects two vertex's, and adds them as North/South or East/West connections depending on their positions to eachother
    private void connectVertex(IndoorVertex indoorV1, IndoorVertex indoorV2)
    {
        XYPos firstPos = indoorV1.getPosition();
        XYPos secondPos = indoorV2.getPosition();

        //Change in only X position means that the vertex's are East/West of eachother
        //Change in only Y position means that the vertex's are North/South of eachother
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


        indoorV1.addConnection(indoorV2);
        indoorV2.addConnection(indoorV1);
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

    //Creates all of the points and exits within Machray, as well as connecting them to eachother
    private void populateConnections()
    {
        IndoorVertex elevatorFloor1 = new IndoorVertex(building, new XYPos(1050, 40), 1);
        IndoorVertex stairsFloor1 = new IndoorVertex(building, new XYPos(1056.25, 28.13), 1);

        firstFloorStairsElevator.add(stairsFloor1);
        firstFloorStairsElevator.add(elevatorFloor1);

        IndoorVertex stairsFloor2 = new IndoorVertex(building, new XYPos(1049.43, 160.49), 2);
        IndoorVertex elevatorFloor2 = new IndoorVertex(building, new XYPos(1044.19, 151.19), 2);

        secondFloorStairsElevator.add(stairsFloor2);
        secondFloorStairsElevator.add(elevatorFloor2);

        IndoorVertex stairsFloor3 = new IndoorVertex(building, new XYPos(1033.75, 28.13), 3);
        IndoorVertex elevatorFloor3 = new IndoorVertex(building, new XYPos(1028.75, 37.5), 3);

        thirdFloorStairsElevator.add(stairsFloor3);
        thirdFloorStairsElevator.add(elevatorFloor3);

        IndoorVertex stairsFloor4 = new IndoorVertex(building, new XYPos(1033.75, 28.13), 4);
        IndoorVertex elevatorFloor4 = new IndoorVertex(building, new XYPos(1028.75, 37.5), 4);

        fourthFloorStairsElevator.add(stairsFloor4);
        fourthFloorStairsElevator.add(elevatorFloor4);

        IndoorVertex stairsFloor5 = new IndoorVertex(building, new XYPos(1033.75, 28.13), 5);
        IndoorVertex elevatorFloor5 = new IndoorVertex(building, new XYPos(1028.75, 37.5), 5);

        firstFloorStairsElevator.add(stairsFloor5);
        fifthFloorStairsElevator.add(elevatorFloor5);

        //Stair connections to one another
        connectVertex(stairsFloor1, stairsFloor2);
        connectVertex(stairsFloor1, stairsFloor3);
        connectVertex(stairsFloor1, stairsFloor4);
        connectVertex(stairsFloor1, stairsFloor5);
        connectVertex(stairsFloor2, stairsFloor3);
        connectVertex(stairsFloor2, stairsFloor4);
        connectVertex(stairsFloor2, stairsFloor5);
        connectVertex(stairsFloor3, stairsFloor4);
        connectVertex(stairsFloor3, stairsFloor5);
        connectVertex(stairsFloor4, stairsFloor5);

        //Elevator connections to one another
        connectVertex(elevatorFloor1, elevatorFloor2);
        connectVertex(elevatorFloor1, elevatorFloor3);
        connectVertex(elevatorFloor1, elevatorFloor4);
        connectVertex(elevatorFloor1, elevatorFloor5);
        connectVertex(elevatorFloor2, elevatorFloor3);
        connectVertex(elevatorFloor2, elevatorFloor4);
        connectVertex(elevatorFloor2, elevatorFloor5);
        connectVertex(elevatorFloor3, elevatorFloor4);
        connectVertex(elevatorFloor3, elevatorFloor5);
        connectVertex(elevatorFloor4, elevatorFloor5);

        //Creation of each floors connections
        createFloor1Connections(stairsFloor1, elevatorFloor1);
        createFloor2Connections(stairsFloor2, elevatorFloor2);
        createFloor3Connections(stairsFloor3, elevatorFloor3);
        createFloor4Connections(stairsFloor4, elevatorFloor4);
        createFloor5Connections(stairsFloor5, elevatorFloor5);
    }

    private void createFloor1Connections(IndoorVertex stairs, IndoorVertex elevator)
    {
        armesConnectionTunnel = new IndoorVertex(building, new XYPos(1000, 31.25), 1);
        IndoorVertex _41_31 = new IndoorVertex(building, new XYPos(1041.25, 31.25), 1);
        IndoorVertex _50_28 = new IndoorVertex(building, new XYPos(1050, 28.13), 1);
        IndoorVertex _50_21 = new IndoorVertex(building, new XYPos(1050, 21.88), 1);
        IndoorVertex _105_21 = new IndoorVertex(building, new XYPos(1105, 21.88), 1);
        IndoorVertex _105_27 = new IndoorVertex(building, new XYPos(1105, 27.5), 1);
        duffRoblinConnection = new IndoorVertex(building, new XYPos(1105, -37.5), 1);
        IndoorVertex room_113_leftSide = new IndoorVertex(building, new XYPos(1110, 27.5), 1);
        IndoorVertex _105_77 = new IndoorVertex(building, new XYPos(1105, 77.5), 1);
        IndoorVertex room_108 = new IndoorVertex(building, new XYPos(1097.5, 87.5), 1);
        IndoorVertex room_111 = new IndoorVertex(building, new XYPos(1120, 81.25), 1);
        IndoorVertex room_112 = new IndoorVertex(building, new XYPos(1110, 75), 1);
        IndoorVertex _165_21 = new IndoorVertex(building, new XYPos(1165, 21.88), 1);
        IndoorVertex room_115 = new IndoorVertex(building, new XYPos(1168.75, 51.25), 1);
        IndoorVertex room_113_rightSide = new IndoorVertex(building, new XYPos(1162.5, 27.5), 1);
        IndoorVertex room_106 = new IndoorVertex(building, new XYPos(1090, 72.5), 1);
        IndoorVertex room_107 = new IndoorVertex(building, new XYPos(1090, 80), 1);
        IndoorVertex _93_72 = new IndoorVertex(building, new XYPos(1093.75, 72.5), 1);
        IndoorVertex _50_31 = new IndoorVertex(building, new XYPos(1050, 31.25), 1);
        IndoorVertex _105_72 = new IndoorVertex(building, new XYPos(1105, 72.5), 1);

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
        connectVertex(armesConnectionTunnel, _41_31);
        connectVertex(_41_31, _50_31);
        connectVertex(_50_31, elevator);
        connectVertex(_50_31, _50_28);
        connectVertex(_50_28, stairs);
        connectVertex(_50_28, _50_21);
        connectVertex(_50_21, _105_21);
        connectVertex(_105_21, _105_27);
        connectVertex(_105_21, _165_21);
        connectVertex(_105_21, duffRoblinConnection);
        connectVertex(_165_21, room_115);
        connectVertex(_165_21, room_113_rightSide);
        connectVertex(room_113_rightSide, room_115);
        connectVertex(_105_27, room_113_leftSide);
        connectVertex(_105_27, _105_72);
        connectVertex(_105_72, _93_72);
        connectVertex(_105_72, room_112);
        connectVertex(_105_72, _105_77);
        connectVertex(_105_77, room_108);
        connectVertex(_105_77, room_111);
        connectVertex(_105_77, room_112);
        connectVertex(room_108, room_111);
        connectVertex(room_106, room_107);
        connectVertex(room_106, _93_72);
    }

    private void createFloor2Connections(IndoorVertex stairs, IndoorVertex elevator)
    {
        //Points on the map
        armesConnectionNorth = new IndoorVertex(building, new XYPos(1000, 151.19), 2);
        armesConnectionSouth = new IndoorVertex(building, new XYPos(1000, 158.75), 2);
        IndoorVertex rm239_fac_science = new IndoorVertex(building, new XYPos(1030.24, 97.11), 2);
        IndoorVertex _30_151 = new IndoorVertex(building, new XYPos(1030.24, 151.19), 2);
        IndoorVertex _30_179 = new IndoorVertex(building, new XYPos(1030.24, 179.65), 2);
        exit = new IndoorVertex(building, new XYPos(1030.24, 188.41), 2);
        IndoorVertex rm211_library = new IndoorVertex(building, new XYPos(1080.25, 173.29), 2);
        IndoorVertex _44_160 = new IndoorVertex(building, new XYPos(1044.19, 160.49), 2);
        IndoorVertex _44_173 = new IndoorVertex(building, new XYPos(1044.19, 173.29), 2);
        IndoorVertex _30_173 = new IndoorVertex(building, new XYPos(1030.24, 173.29), 2);
        IndoorVertex _30_158 = new IndoorVertex(building, new XYPos(1030.24, 158.75), 2);
        IndoorVertex _44_154 = new IndoorVertex(building, new XYPos(1044.19, 154.1), 2);
        IndoorVertex _30_154 = new IndoorVertex(building, new XYPos(1030.24, 154.1), 2);

        //Rooms
        ArrayList<IndoorVertex> rm211 = new ArrayList<>();
        rm211.add(rm211_library);

        rooms.put("211", rm211);

        ArrayList<IndoorVertex> rm239 = new ArrayList<>();
        rm239.add(rm239_fac_science);

        rooms.put("239", rm239);

        //Connections to other points
        connectVertex(armesConnectionSouth, _30_158);
        connectVertex(armesConnectionNorth, _30_151);
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
        IndoorVertex _18_28 = new IndoorVertex(building, new XYPos(1018.75, 28.13), 3);
        IndoorVertex _18_71 = new IndoorVertex(building, new XYPos(1018.75, 71.88), 3);
        IndoorVertex _35_71 = new IndoorVertex(building, new XYPos(1035, 71.88), 3);
        IndoorVertex _11_71 = new IndoorVertex(building, new XYPos(1011.25, 71.88), 3);
        IndoorVertex _28_28 = new IndoorVertex(building, new XYPos(1028.75, 28.13), 3);
        IndoorVertex _28_21 = new IndoorVertex(building, new XYPos(1028.75, 21.88), 3);
        IndoorVertex _72_21 = new IndoorVertex(building, new XYPos(1072.5, 21.88), 3);
        IndoorVertex _47_71 = new IndoorVertex(building, new XYPos(1047.5, 71.88), 3);
        IndoorVertex _85_26 = new IndoorVertex(building, new XYPos(1085, 26.88), 3);
        IndoorVertex _28_34 = new IndoorVertex(building, new XYPos(1028.75, 34.38), 3);
        IndoorVertex _18_34 = new IndoorVertex(building, new XYPos(1018.75, 34.38), 3);

        //Rooms

        //Connections between points
        connectVertex(stairs1, _28_28);
        connectVertex(_28_28, _28_21);
        connectVertex(_28_28, _18_28);
        connectVertex(_28_28, _28_34);
        connectVertex(_18_28, _18_34);
        connectVertex(_18_34, _28_34);
        connectVertex(_18_34, _18_71);
        connectVertex(_18_71, _11_71);
        connectVertex(_18_71, _35_71);
        connectVertex(_35_71, _47_71);
        connectVertex(_28_21, _72_21);
        connectVertex(_72_21, _85_26);
        connectVertex(_28_34, elevator);
    }

    private void createFloor4Connections(IndoorVertex stairs1, IndoorVertex elevator)
    {
        //Points
        IndoorVertex _18_28 = new IndoorVertex(building, new XYPos(1018.75, 28.13), 4);
        IndoorVertex _18_71 = new IndoorVertex(building, new XYPos(1018.75, 71.88), 4);
        IndoorVertex _35_71 = new IndoorVertex(building, new XYPos(1035, 71.88), 4);
        IndoorVertex _11_71 = new IndoorVertex(building, new XYPos(1011.25, 71.88), 4);
        IndoorVertex _28_28 = new IndoorVertex(building, new XYPos(1028.75, 28.13), 4);
        IndoorVertex _28_21 = new IndoorVertex(building, new XYPos(1028.75, 21.88), 4);
        IndoorVertex _47_71 = new IndoorVertex(building, new XYPos(1047.5, 71.88), 4);
        IndoorVertex _28_34 = new IndoorVertex(building, new XYPos(1028.75, 34.38), 4);
        IndoorVertex _18_34 = new IndoorVertex(building, new XYPos(1018.75, 34.38), 4);
        IndoorVertex _84_23 = new IndoorVertex(building, new XYPos(1084.38, 23.13), 4);
        IndoorVertex _59_21 = new IndoorVertex(building, new XYPos(1059.38, 21.88), 4);

        //Rooms

        //Connections between points
        connectVertex(stairs1, _28_28);
        connectVertex(_28_28, _28_21);
        connectVertex(_28_28, _18_28);
        connectVertex(_28_28, _28_34);
        connectVertex(_18_28, _18_34);
        connectVertex(_18_34, _28_34);
        connectVertex(_18_34, _18_71);
        connectVertex(_18_71, _11_71);
        connectVertex(_18_71, _35_71);
        connectVertex(_35_71, _47_71);
        connectVertex(_28_34, elevator);
        connectVertex(_59_21, _84_23);
        connectVertex(_28_21, _59_21);
    }

    private void createFloor5Connections(IndoorVertex stairs1, IndoorVertex elevator)
    {
        //Points
        IndoorVertex _18_28 = new IndoorVertex(building, new XYPos(1018.75, 28.13), 5);
        IndoorVertex _18_71 = new IndoorVertex(building, new XYPos(1018.75, 71.88), 5);
        IndoorVertex _11_71 = new IndoorVertex(building, new XYPos(1011.25, 71.88), 5);
        IndoorVertex _28_28 = new IndoorVertex(building, new XYPos(1028.75, 28.13), 5);
        IndoorVertex _28_21 = new IndoorVertex(building, new XYPos(1028.75, 21.88), 5);
        IndoorVertex _28_34 = new IndoorVertex(building, new XYPos(1028.75, 34.38), 5);
        IndoorVertex _18_34 = new IndoorVertex(building, new XYPos(1018.75, 34.38), 5);
        IndoorVertex _42_71 = new IndoorVertex(building, new XYPos(1042.5, 71.88), 5);

        //Rooms

        //Connections between points
        connectVertex(stairs1, _28_28);
        connectVertex(_28_28, _28_21);
        connectVertex(_28_28, _18_28);
        connectVertex(_28_28, _28_34);
        connectVertex(_18_28, _18_34);
        connectVertex(_18_34, _28_34);
        connectVertex(_18_34, _18_71);
        connectVertex(_18_71, _11_71);
        connectVertex(_18_71, _42_71);
        connectVertex(_28_34, elevator);
    }

    public IndoorVertex getExit() {
        return exit;
    }

    public IndoorVertex getDuffRoblinConnection() {
        return duffRoblinConnection;
    }

    public IndoorVertex getArmesConnectionTunnel() {
        return armesConnectionTunnel;
    }

    public IndoorVertex getArmesConnectionNorth() {
        return armesConnectionNorth;
    }

    public IndoorVertex getArmesConnectionSouth() {
        return armesConnectionSouth;
    }
}
