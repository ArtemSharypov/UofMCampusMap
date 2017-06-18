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
    public double getDistanceFrom(Vertex destinationVertex) {
        double distance = 0;

        if(destinationVertex instanceof IndoorVertex)
        {
            distance = getXYPos().getDistanceFrom(destinationVertex.getXYPos());
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
