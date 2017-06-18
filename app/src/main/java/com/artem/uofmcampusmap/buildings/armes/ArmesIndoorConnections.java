package com.artem.uofmcampusmap.buildings.armes;

import com.artem.uofmcampusmap.IndoorVertex;
import com.artem.uofmcampusmap.StairsElevatorVertex;
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

    public ArmesIndoorConnections()
    {
        rooms = new HashMap<>();
        allenConnections = new ArrayList<>();
        machrayConnections = new ArrayList<>();
    }

    //todo fix the connections that have ~51 as their Y cause its messed
    public void populateConnections()
    {
        //floor 2
        IndoorVertex _12_125 = new IndoorVertex(building, new XYPos(-54.8, 164.5), 2);
        IndoorVertex _131_112 = new IndoorVertex(building, new XYPos(-18.5, 160.7), 2); //room 208
        IndoorVertex _162_112 = new IndoorVertex(building, new XYPos(-8.9, 160.7), 2); //room 208
        StairsElevatorVertex _156_125 = new StairsElevatorVertex(building, new XYPos(-10.9 , 164.5), 2); //right stairs
        IndoorVertex _162_118 = new IndoorVertex(building, new XYPos(-8.9, 162.6), 2);
        IndoorVertex _131_118 = new IndoorVertex(building, new XYPos(-18.5, 162.6), 2);
        IndoorVertex _12_187 = new IndoorVertex(building, new XYPos(-54.8, 183.6), 2);
        IndoorVertex _12_37 = new IndoorVertex(building, new XYPos(-54.8, 126.5), 2);
        IndoorVertex _22_118 = new IndoorVertex(building, new XYPos(-51.6, 162.6), 2);
        IndoorVertex _31_118 = new IndoorVertex(building, new XYPos(-49, 162.6), 2);
        IndoorVertex _31_51 = new IndoorVertex(building, new XYPos(-49, 142), 2);
        IndoorVertex _31_137 = new IndoorVertex(building, new XYPos(-49, 168.4), 2);//rm 201
        StairsElevatorVertex _37_126 = new StairsElevatorVertex(building, new XYPos(-47, 164.9), 2); //left stairs
        IndoorVertex _50_51 = new IndoorVertex(building, new XYPos(-43.2, 142), 2);
        IndoorVertex _31_112 = new IndoorVertex(building, new XYPos(-49, 160.7), 2); //rm 200
        IndoorVertex _50_137 = new IndoorVertex(building, new XYPos(-43.2, 168.4), 2); //rm 201
        IndoorVertex _62_51 = new IndoorVertex(building, new XYPos(-39.5, 142), 2);
        IndoorVertex _62_118 = new IndoorVertex(building, new XYPos(-39.5, 162.6), 2);
        IndoorVertex _62_137 = new IndoorVertex(building, new XYPos(-39.5, 168.4), 2);//rm 205
        IndoorVertex _85_137 = new IndoorVertex(building, new XYPos(-32.5, 168.4), 2); //rm 205
        IndoorVertex _85_51 = new IndoorVertex(building, new XYPos(-32.5, 142), 2);
        IndoorVertex _62_112 = new IndoorVertex(building, new XYPos(-39.5, 160.7), 2); //rm200
        IndoorVertex _85_118 = new IndoorVertex(building, new XYPos(-32.5, 162.6), 2);
        IndoorVertex _85_112 = new IndoorVertex(building, new XYPos(-32.5, 160.7), 2); //rm 204
        IndoorVertex _108_118 = new IndoorVertex(building, new XYPos(-25.4, 162.6), 2);
        IndoorVertex _108_112 = new IndoorVertex(building, new XYPos(-25.4, 160.7), 2);//rm 204
        IndoorVertex _162_51 = new IndoorVertex(building, new XYPos(-8.9, 142), 2);
        IndoorVertex _22_51 = new IndoorVertex(building, new XYPos(-51.6, 142), 2);
        IndoorVertex _131_51 = new IndoorVertex(building, new XYPos(-18.5, 142), 2);

        northWestEntrance = new IndoorVertex(building, new XYPos(-58.4, 183.6), 2); //0, 187.5
        southWestEntrance = new IndoorVertex(building, new XYPos(-58.4, 164), 2); //0, 37.5

        IndoorVertex allen_connect_north = new IndoorVertex(building, new XYPos(-58.4, 142), 2); //0, 51.25
        IndoorVertex allen_connect_south = new IndoorVertex(building, new XYPos(-58.4, 162.6), 2); //0, 118.75

        allenConnections.add(allen_connect_north);
        allenConnections.add(allen_connect_south);

        IndoorVertex machray_connect_north = new IndoorVertex(building, new XYPos(-5.5 , 142), 2); //173, 51
        IndoorVertex machray_connect_south = new IndoorVertex(building, new XYPos(-5.5, 162.6), 2); //173, 118

        machrayConnections.add(machray_connect_north);
        machrayConnections.add(machray_connect_south);

        bullerConnection = new IndoorVertex(building, new XYPos(-53.9, 128.4), 2); //15, 6.25 todo check this
        parkerConnection = new IndoorVertex(building, new XYPos(-55, 189.3), 2); //11.25, 206.25

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
        connectVertex(_22_51, _37_126);
        connectVertex(_22_118, _37_126);
        connectVertex(_22_118, _31_118);
        connectVertex(_31_118, _31_112);
        connectVertex(_31_118, _37_126);
        connectVertex(_31_118, _62_118);
        connectVertex(_31_118, _31_51);
        connectVertex(_31_51, _37_126);
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
        connectVertex(_162_51, _156_125);
        connectVertex(_162_51, _162_118);
        connectVertex(machray_connect_north, _156_125);
        connectVertex(_162_118, _162_112);
        connectVertex(_162_118, _156_125);
        connectVertex(_162_118, machray_connect_south);
        connectVertex(machray_connect_south, _156_125);

        //todo all of theseeeeeeeeeeeeeeeeeeeeeee
        //floor 1
        IndoorVertex _163_120 = new IndoorVertex(building, new XYPos(), 1);
        IndoorVertex _10_126 = new IndoorVertex(building, new XYPos(), 1);
        IndoorVertex _18_130 = new IndoorVertex(building, new XYPos(), 1);
        IndoorVertex _18_156 = new IndoorVertex(building, new XYPos(), 1); //rm 201
        IndoorVertex _18_122 = new IndoorVertex(building, new XYPos(), 1);
        IndoorVertex _71_120 = new IndoorVertex(building, new XYPos(), 1);
        IndoorVertex _71_95 = new IndoorVertex(building, new XYPos(), 1); //rm 204
        IndoorVertex _56_132 = new IndoorVertex(building, new XYPos(), 1);
        StairsElevatorVertex _56_126 = new StairsElevatorVertex(building, new XYPos(), 1); //left stairs
        IndoorVertex _56_120 = new IndoorVertex(building, new XYPos(), 1);
        IndoorVertex _99_120 = new IndoorVertex(building, new XYPos(), 1);
        IndoorVertex _87_156 = new IndoorVertex(building, new XYPos(), 1); //rm 205
        IndoorVertex _92_156 = new IndoorVertex(building, new XYPos(), 1); //rm 111
        IndoorVertex _87_132 = new IndoorVertex(building, new XYPos(), 1);
        IndoorVertex _93_138 = new IndoorVertex(building, new XYPos(), 1); //rm 115
        IndoorVertex _130_156 = new IndoorVertex(building, new XYPos(), 1); //rm 111
        IndoorVertex _120_138 = new IndoorVertex(building, new XYPos(), 1); //rm 115
        StairsElevatorVertex _130_126 = new StairsElevatorVertex(building, new XYPos(), 1); //right stairs
        IndoorVertex _130_132 = new IndoorVertex(building, new XYPos(), 1);
        IndoorVertex _163_95 = new IndoorVertex(building, new XYPos(), 1); //rm 208

        machrayConnectionTunnel = new IndoorVertex(building, new XYPos(), 1); //168.75, 126.25
        parkerConnectionTunnel = new IndoorVertex(building, new XYPos(), 1); //10, 212.5
        allenConnectionTunnel = new IndoorVertex(building, new XYPos(), 1); //-22.5, 126.25

        //floor 1 rooms
        ArrayList<IndoorVertex> rm115 = new ArrayList<>();

        rooms.put("115", rm115);

        ArrayList<IndoorVertex> rm111 = new ArrayList<>();

        rooms.put("111", rm111);

        //floor 1 connections

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
}
