package com.artem.uofmcampusmap;

/**
 * Created by Artem on 2017-05-29.
 */

public class IndoorVertex extends Vertex
{
    private int floor;
    private String building;
    //todo add variables for elevators / stairs that are used for getting distance from when they are on different floors

    public IndoorVertex(String buildingName, XYPos xyPos, int floor)
    {
        super(xyPos);
        this.building = buildingName;
        this.floor = floor;

    }

    public IndoorVertex(IndoorVertex vertex)
    {
        super(vertex);
        this.floor = vertex.getFloor();
        this.building = vertex.getBuilding();
    }


    @Override
    public double getDistanceFrom(Vertex vertex) {
        double distance = 0;

        if(vertex instanceof IndoorVertex)
        {
            //if they are diff buildings, then go to the lowest floor that has an exit unless they have a connection
            //then if lowest floor already use the super distance from
            //check if they are on diff floors, if so then use the reference to the closest stair/elevator to base distance on
        }
        else
        {
            distance = super.getDistanceFrom(vertex);
        }

        return distance;
    }

    public int getFloor() {
        return floor;
    }

    public String getBuilding() {
        return building;
    }
}
