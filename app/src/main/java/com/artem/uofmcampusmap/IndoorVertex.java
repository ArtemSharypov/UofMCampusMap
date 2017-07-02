package com.artem.uofmcampusmap;

/**
 * Created by Artem on 2017-05-29.
 */

public class IndoorVertex extends Vertex
{
    private int floor;
    private String building;
    private XYPos position;

    public IndoorVertex(String buildingName, XYPos xyPos, int floor)
    {
        super();
        this.building = buildingName;
        this.floor = floor;
        this.position = xyPos;

    }

    public IndoorVertex(IndoorVertex vertex)
    {
        super(vertex);
        this.floor = vertex.getFloor();
        this.building = vertex.getBuilding();
        this.position = vertex.getPosition();
    }

    @Override
    public int getDistanceFrom(Vertex destinationVertex) {
        int distance = 0;
        IndoorVertex indoorVertex;

        if(destinationVertex instanceof IndoorVertex)
        {
            indoorVertex = (IndoorVertex) destinationVertex;

            distance = position.getDistanceFrom(indoorVertex.getPosition());
        }

        return distance;
    }

    public boolean equals(Vertex vertex)
    {
        boolean areEqual = false;
        XYPos posToCompare;

        if(vertex != null && vertex instanceof IndoorVertex)
        {
            posToCompare = ((IndoorVertex) vertex).getPosition();

            if(position.getX() == posToCompare.getX() && position.getY() == posToCompare.getY())
            {
                areEqual = true;
            }
        }

        return areEqual;
    }

    @Override
    public int hashCode() {
        return (int) (position.getX() + position.getY());
    }

    public XYPos getPosition() {
        return position;
    }

    public int getFloor() {
        return floor;
    }

    public String getBuilding() {
        return building;
    }
}
