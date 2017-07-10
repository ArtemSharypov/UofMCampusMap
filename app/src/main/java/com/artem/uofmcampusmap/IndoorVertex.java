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

    //Distance between two IndoorVertex's is simply a^2 + b^2 = distance
    //Anything else can't be reasonably calculated
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

    //Find a vertex that connects to the stairs that is to the desired floor
    public IndoorVertex findStairsConnection(int destinationFloor)
    {
        IndoorVertex connection = null;
        IndoorVertex tempVertex;

        //Find the other stairs connection to the stairs, only have to check for floor
        //since nothing else should be cross floor connections that matter
        for(Vertex vertex : getConnections())
        {
            if(vertex instanceof IndoorVertex)
            {
                tempVertex = (IndoorVertex) vertex;

                if(tempVertex.getFloor() == destinationFloor)
                {
                    connection = tempVertex;
                    break;
                }

            }
        }

        return connection;
    }

    //Used for the main equals(Object obj) check
    //Two IndoorVertex's must have the same X and Y positions, and be within the same building
    public boolean equals(Vertex vertex)
    {
        boolean areEqual = false;
        XYPos posToCompare;
        IndoorVertex indoorVertex;

        if(vertex != null && vertex instanceof IndoorVertex)
        {
            indoorVertex = (IndoorVertex) vertex;
            posToCompare = indoorVertex.getPosition();

            if(building.equals(indoorVertex.getBuilding()) && position.getX() == posToCompare.getX()
                    && position.getY() == posToCompare.getY())
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
