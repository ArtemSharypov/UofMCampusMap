package com.artem.uofmcampusmap;

/**
 * Created by Artem on 2017-05-29.
 */

public class StairsElevatorVertex extends IndoorVertex {
    public StairsElevatorVertex(String buildingName, String id, XYPoint xyPos, int floor)
    {
        super(buildingName, id, xyPos, floor);
    }

    @Override
    public double getDistanceFrom(Vertex vertex) {
        //if different buildings, prefer going to a floor that has an exit / connection to other building
        //if same building then prefer going to the floor that the destination is at
        return super.getDistanceFrom(vertex);
    }
}
