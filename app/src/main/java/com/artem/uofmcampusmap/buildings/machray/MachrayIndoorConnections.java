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

        //Stair connections to one another

        //Creation of each floors connections
        createFloor1Connections(stairsFloor1, elevatorFloor1);


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
        IndoorVertex _110_27 = new IndoorVertex(building, new XYPos(110, 27.5), 1);
        IndoorVertex _137_77 = new IndoorVertex(building, new XYPos(137.5, 77.5), 1);
        IndoorVertex _97_87 = new IndoorVertex(building, new XYPos(97.5, 87.5), 1);
        IndoorVertex _120_81 = new IndoorVertex(building, new XYPos(120, 81.25), 1);
        IndoorVertex _108_75 = new IndoorVertex(building, new XYPos(108.75, 75), 1);
        IndoorVertex _165_21 = new IndoorVertex(building, new XYPos(165, 21.88), 1);
        IndoorVertex _168_51 = new IndoorVertex(building, new XYPos(168.75, 51.25), 1);
        IndoorVertex _162_27 = new IndoorVertex(building, new XYPos(162.5, 27.5), 1);
        IndoorVertex _90_72 = new IndoorVertex(building, new XYPos(90, 72.5), 1);
        IndoorVertex _90_80 = new IndoorVertex(building, new XYPos(90, 80), 1);
        IndoorVertex _93_72 = new IndoorVertex(building, new XYPos(93.75, 72.5), 1);
        IndoorVertex _50_31 = new IndoorVertex(building, new XYPos(50, 31.25), 1);
        IndoorVertex _137_72 = new IndoorVertex(building, new XYPos(137.5, 72.5), 1);

        //Other building connections
        armesConnections.add(armesConnection);

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
        connectVertex(_165_21, _168_51);
        connectVertex(_165_21, _162_27);
        connectVertex(_162_27, _168_51);
        connectVertex(_137_27, _110_27);
        connectVertex(_137_27, _137_72);
        connectVertex(_137_72, _93_72);
        connectVertex(_137_72, _108_75);
        connectVertex(_137_72, _137_77);
        connectVertex(_137_77, _97_87);
        connectVertex(_137_77, _120_81);
        connectVertex(_137_77, _108_75);
        connectVertex(_97_87, _120_81);
        connectVertex(_90_72, _90_80);
        connectVertex(_90_72, _93_72);
    }

    private void createFloor2Connections(IndoorVertex stairs1, IndoorVertex elevator)
    {
        //IndoorVertex _ = new IndoorVertex(building, new XYPos(), 2);
    }

    private void createFloor3Connections(IndoorVertex stairs1, IndoorVertex elevator)
    {
        //IndoorVertex _ = new IndoorVertex(building, new XYPos(), 3);
    }

    private void createFloor4Connections(IndoorVertex stairs1, IndoorVertex elevator)
    {
        //IndoorVertex _ = new IndoorVertex(building, new XYPos(), 4);
    }

    private void createFloor5Connections(IndoorVertex stairs1, IndoorVertex elevator)
    {
        //IndoorVertex _ = new IndoorVertex(building, new XYPos(), 5);
    }

    public IndoorVertex getExit() {
        return exit;
    }

    public IndoorVertex getDuffRoblinConnection() {
        return duffRoblinConnection;
    }
}
