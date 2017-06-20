package com.artem.uofmcampusmap.buildings.armes;

import com.artem.uofmcampusmap.IndoorVertex;
import com.artem.uofmcampusmap.Vertex;
import com.artem.uofmcampusmap.XYPos;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Artem on 2017-06-08.
 */

public class ArmesIndoorConnections {
    private HashMap<String, ArrayList<IndoorVertex>> rooms;
    private ArrayList<IndoorVertex> allenConnections;
    private IndoorVertex allenConnectionTunnel;
    private ArrayList<IndoorVertex> machrayConnections;
    private IndoorVertex machrayConnectionTunnel;
    private IndoorVertex bullerConnection;
    private IndoorVertex parkerConnection;
    private IndoorVertex parkerConnectionTunnel;
    private IndoorVertex southWestEntrance;
    private IndoorVertex northWestEntrance;
    private final String building = "Armes";
    private ArrayList<IndoorVertex> firstFloorStairs;
    private ArrayList<IndoorVertex> secondFloorStairs;

    public ArmesIndoorConnections()
    {
        rooms = new HashMap<>();
        allenConnections = new ArrayList<>();
        machrayConnections = new ArrayList<>();
        firstFloorStairs = new ArrayList<>();
        secondFloorStairs = new ArrayList<>();
        populateConnections();
    }

    //todo fix the connections that have ~51 as their Y cause its messed
    public void populateConnections()
    {
        //floor 2
        IndoorVertex _12_125 = new IndoorVertex(building, new XYPos(12.5, 125), 2);
        IndoorVertex _131_112 = new IndoorVertex(building, new XYPos(131.25, 112.5), 2); //room 208
        IndoorVertex _162_112 = new IndoorVertex(building, new XYPos(162.5, 112.5), 2); //room 208
        IndoorVertex rightStairsFloor2 = new IndoorVertex(building, new XYPos(156.25, 125), 2);
        IndoorVertex _162_118 = new IndoorVertex(building, new XYPos(162.5, 118.75), 2);
        IndoorVertex _131_118 = new IndoorVertex(building, new XYPos(131.25, 118.75), 2);
        IndoorVertex _12_187 = new IndoorVertex(building, new XYPos(12.5, 187.5), 2);
        IndoorVertex _12_37 = new IndoorVertex(building, new XYPos(12.5, 37.5), 2);
        IndoorVertex _22_118 = new IndoorVertex(building, new XYPos(22.5, 118.75), 2);
        IndoorVertex _31_118 = new IndoorVertex(building, new XYPos(31.25, 118.75), 2);
        IndoorVertex _31_51 = new IndoorVertex(building, new XYPos(31.25, 51.25), 2);
        IndoorVertex _31_137 = new IndoorVertex(building, new XYPos(31.25, 137.5), 2);//rm 201
        IndoorVertex leftStairsFloor2 = new IndoorVertex(building, new XYPos(37.5, 126.25), 2);
        IndoorVertex _50_51 = new IndoorVertex(building, new XYPos(50, 51.25), 2);
        IndoorVertex _31_112 = new IndoorVertex(building, new XYPos(31.25, 112.5), 2); //rm 200
        IndoorVertex _50_137 = new IndoorVertex(building, new XYPos(50, 137.5), 2); //rm 201
        IndoorVertex _62_51 = new IndoorVertex(building, new XYPos(62.5, 51.25), 2);
        IndoorVertex _62_118 = new IndoorVertex(building, new XYPos(62.5, 118.75), 2);
        IndoorVertex _62_137 = new IndoorVertex(building, new XYPos(62.5, 137.5), 2);//rm 205
        IndoorVertex _85_137 = new IndoorVertex(building, new XYPos(85, 137.5), 2); //rm 205
        IndoorVertex _85_51 = new IndoorVertex(building, new XYPos(85, 51.25), 2);
        IndoorVertex _62_112 = new IndoorVertex(building, new XYPos(62.5, 112.5), 2); //rm200
        IndoorVertex _85_118 = new IndoorVertex(building, new XYPos(85, 118.75), 2);
        IndoorVertex _85_112 = new IndoorVertex(building, new XYPos(85, 112.5), 2); //rm 204
        IndoorVertex _108_118 = new IndoorVertex(building, new XYPos(108.5, 118.75), 2);
        IndoorVertex _108_112 = new IndoorVertex(building, new XYPos(108.5, 112.5), 2);//rm 204
        IndoorVertex _162_51 = new IndoorVertex(building, new XYPos(162.5, 51.25), 2);
        IndoorVertex _22_51 = new IndoorVertex(building, new XYPos(22.5, 51.25), 2);
        IndoorVertex _131_51 = new IndoorVertex(building, new XYPos(131.25, 51.25), 2);

        northWestEntrance = new IndoorVertex(building, new XYPos(0, 187.5), 2);
        southWestEntrance = new IndoorVertex(building, new XYPos(0, 37.5), 2);

        IndoorVertex allen_connect_north = new IndoorVertex(building, new XYPos(0, 51.25), 2);
        IndoorVertex allen_connect_south = new IndoorVertex(building, new XYPos(0, 118.75), 2);

        allenConnections.add(allen_connect_north);
        allenConnections.add(allen_connect_south);

        IndoorVertex machray_connect_north = new IndoorVertex(building, new XYPos(173, 51), 2);
        IndoorVertex machray_connect_south = new IndoorVertex(building, new XYPos(173, 118), 2);

        machrayConnections.add(machray_connect_north);
        machrayConnections.add(machray_connect_south);

        bullerConnection = new IndoorVertex(building, new XYPos(15, 6.25), 2); //todo check this
        parkerConnection = new IndoorVertex(building, new XYPos(11.25, 206.25), 2);

        //floor 2 connections
        connectVertex(bullerConnection, southWestEntrance);
        connectVertex(southWestEntrance, _12_37);
        connectVertex(_12_37, _12_125);
        connectVertex(northWestEntrance, parkerConnection);
        connectVertex(parkerConnection, _12_187);
        connectVertex(northWestEntrance, _12_187);
        connectVertex(_12_125, _12_187);
        connectVertex(allen_connect_north, _12_125);
        connectVertex(allen_connect_south, _12_125);
        connectVertex(_12_125, _22_118);
        connectVertex(_12_125, _22_51);
        connectVertex(_22_51, _31_51);
        connectVertex(_22_51, leftStairsFloor2);
        connectVertex(_22_118, leftStairsFloor2);
        connectVertex(_22_118, _31_118);
        connectVertex(_31_118, _31_112);
        connectVertex(_31_118, leftStairsFloor2);
        connectVertex(_31_118, _62_118);
        connectVertex(_31_118, _31_51);
        connectVertex(_31_51, leftStairsFloor2);
        connectVertex(_31_51, _31_137);
        connectVertex(_31_51, _50_51);
        connectVertex(_50_51, _50_137);
        connectVertex(_50_51, _62_51);
        connectVertex(_62_51, _62_118);
        connectVertex(_62_51, _62_137);
        connectVertex(_62_51, _85_51);
        connectVertex(_62_118, _62_112);
        connectVertex(_62_118, _85_118);
        connectVertex(_85_118, _85_112);
        connectVertex(_85_118, _85_51);
        connectVertex(_85_118, _108_118);
        connectVertex(_108_118, _108_112);
        connectVertex(_108_118, _131_118);
        connectVertex(_131_118, _131_112);
        connectVertex(_131_118, _162_118);
        connectVertex(_131_118, _131_51);
        connectVertex(_131_51, _162_51);
        connectVertex(_131_51, _85_51);
        connectVertex(_85_51, _85_137);
        connectVertex(_162_51, machray_connect_north);
        connectVertex(_162_51, rightStairsFloor2);
        connectVertex(_162_51, _162_118);
        connectVertex(machray_connect_north, rightStairsFloor2);
        connectVertex(_162_118, _162_112);
        connectVertex(_162_118, rightStairsFloor2);
        connectVertex(_162_118, machray_connect_south);
        connectVertex(machray_connect_south, rightStairsFloor2);

        //floor 1
        IndoorVertex _163_120 = new IndoorVertex(building, new XYPos(163.75, 120), 1);
        IndoorVertex _10_126 = new IndoorVertex(building, new XYPos(10, 126.25), 1);
        IndoorVertex _18_130 = new IndoorVertex(building, new XYPos(18.75, 130), 1);
        IndoorVertex _18_156 = new IndoorVertex(building, new XYPos(18.75, 156.25), 1); //rm 201
        IndoorVertex _18_122 = new IndoorVertex(building, new XYPos(18.75, 122.5), 1);
        IndoorVertex _18_95 = new IndoorVertex(building, new XYPos(18.75, 95), 1);
        IndoorVertex _71_120 = new IndoorVertex(building, new XYPos(71.25, 120), 1);
        IndoorVertex _71_95 = new IndoorVertex(building, new XYPos(71.25, 95), 1); //rm 204
        IndoorVertex _56_132 = new IndoorVertex(building, new XYPos(56.25, 132.5), 1);
        IndoorVertex leftStairsFloor1 = new IndoorVertex(building, new XYPos(56.25, 126.25), 1);
        IndoorVertex _56_120 = new IndoorVertex(building, new XYPos(56.25, 120), 1);
        IndoorVertex _99_120 = new IndoorVertex(building, new XYPos(99.4, 120), 1);
        IndoorVertex _87_156 = new IndoorVertex(building, new XYPos(87.5, 156.25), 1); //rm 205
        IndoorVertex _92_156 = new IndoorVertex(building, new XYPos(92.5, 156.25), 1); //rm 111
        IndoorVertex _87_132 = new IndoorVertex(building, new XYPos(87.5, 132.5), 1);
        IndoorVertex _93_138 = new IndoorVertex(building, new XYPos(93.75, 138.75), 1); //rm 115
        IndoorVertex _130_156 = new IndoorVertex(building, new XYPos(130, 156.25), 1); //rm 111
        IndoorVertex _120_138 = new IndoorVertex(building, new XYPos(120, 138.75), 1); //rm 115
        IndoorVertex rightStairsFloor1 = new IndoorVertex(building, new XYPos(130, 126.25), 1);
        IndoorVertex _130_132 = new IndoorVertex(building, new XYPos(130, 132.5), 1);
        IndoorVertex _163_95 = new IndoorVertex(building, new XYPos(163.75, 95), 1); //rm 208

        machrayConnectionTunnel = new IndoorVertex(building, new XYPos(168.75, 126.25), 1);
        parkerConnectionTunnel = new IndoorVertex(building, new XYPos(10, 212.5), 1);
        allenConnectionTunnel = new IndoorVertex(building, new XYPos(-22.5, 126.25), 1);

        //floor 1 rooms/stairs
        ArrayList<IndoorVertex> rm115 = new ArrayList<>();
        rm115.add(_93_138);

        rooms.put("115", rm115);

        ArrayList<IndoorVertex> rm111 = new ArrayList<>();
        rm111.add(_130_156);

        rooms.put("111", rm111);

        firstFloorStairs.add(leftStairsFloor1);
        firstFloorStairs.add(rightStairsFloor1);

        //floor 1 connections
        //todo add connections
        connectVertex(parkerConnectionTunnel, _10_126);
        connectVertex(allenConnectionTunnel, _10_126);
        connectVertex(_10_126, _18_130);
        connectVertex(_10_126, _18_122);
        connectVertex(_18_130, _18_122);
        connectVertex(_18_130, _18_156);
        connectVertex(_18_130, _56_132);
        connectVertex(_18_122, _18_95);
        connectVertex(_18_122, _56_120);
        connectVertex(machrayConnectionTunnel, _163_120);
        connectVertex(machrayConnectionTunnel, _130_132);
        connectVertex(_163_120, _163_95);
        connectVertex(_163_120, _99_120);
        connectVertex(_56_132, leftStairsFloor1);
        connectVertex(_56_132, _71_120);
        connectVertex(_56_132, _87_132);
        connectVertex(leftStairsFloor1, _56_120);
        connectVertex(leftStairsFloor1, _71_120);
        connectVertex(leftStairsFloor1, _87_132);
        connectVertex(_56_120, _71_120);
        connectVertex(_56_120, _99_120);
        connectVertex(_71_120, _71_95);
        connectVertex(_71_120, _99_120);
        connectVertex(_71_120, _87_132);
        connectVertex(_87_132, _87_156);
        connectVertex(_87_132, _92_156);
        connectVertex(_87_132, _93_138);
        connectVertex(_87_132, _130_132);
        connectVertex(_87_132, rightStairsFloor1);
        connectVertex(_87_156, _92_156);
        connectVertex(_87_156, _93_138);
        connectVertex(_93_138, _92_156);
        connectVertex(_130_156, _120_138);
        connectVertex(_130_156, _130_132);
        connectVertex(_120_138, _130_132);
        connectVertex(_130_132, rightStairsFloor1);
        connectVertex(rightStairsFloor1, _99_120);

        //remaining rooms
        ArrayList<IndoorVertex> rm200 = new ArrayList<>();
        rm200.add(_31_112);
        rm200.add(_62_112);

        rooms.put("200", rm200);

        ArrayList<IndoorVertex> rm201 = new ArrayList<>();
        rm201.add(_31_137);
        rm201.add(_50_137);

        rooms.put("201", rm201);

        ArrayList<IndoorVertex> rm204 = new ArrayList<>();
        rm204.add(_85_112);
        rm204.add(_108_112);

        rooms.put("204", rm204);

        ArrayList<IndoorVertex> rm205 = new ArrayList<>();
        rm205.add(_62_137);
        rm205.add(_85_137);

        rooms.put("205", rm205);

        ArrayList<IndoorVertex> rm208 = new ArrayList<>();
        rm208.add(_131_118);
        rm208.add(_162_118);

        rooms.put("208", rm208);

        //floor1 to floor2 stair connections
        connectVertex(leftStairsFloor1, leftStairsFloor2);
        connectVertex(rightStairsFloor1, rightStairsFloor2);
    }

    private void connectVertex(Vertex vertex1, Vertex vertex2)
    {
        vertex1.addConnection(vertex2);
        vertex2.addConnection(vertex1);
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

    public IndoorVertex getAllenConnectionTunnel() {
        return allenConnectionTunnel;
    }

    public IndoorVertex getMachrayConnectionTunnel() {
        return machrayConnectionTunnel;
    }

    public IndoorVertex getBullerConnection() {
        return bullerConnection;
    }

    public IndoorVertex getParkerConnection() {
        return parkerConnection;
    }

    public IndoorVertex getParkerConnectionTunnel() {
        return parkerConnectionTunnel;
    }

    public IndoorVertex getSouthWestEntrance() {
        return southWestEntrance;
    }

    public IndoorVertex getNorthWestEntrance() {
        return northWestEntrance;
    }

    public IndoorVertex getClosestStairsToRoom(IndoorVertex room)
    {
        IndoorVertex closestStairs = null;
        int bestDistance = Integer.MAX_VALUE;
        int tempDistance;
        ArrayList<IndoorVertex> allStairs = new ArrayList<>();
        allStairs.addAll(firstFloorStairs);
        allStairs.addAll(secondFloorStairs);

        if(room != null)
        {
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
}
