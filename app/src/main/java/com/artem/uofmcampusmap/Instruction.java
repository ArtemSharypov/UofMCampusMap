package com.artem.uofmcampusmap;

/**
 * Created by Artem on 2017-04-28.
 */

public class Instruction {
    private Vertex source;
    private Vertex destination;
    private int distance;
    private int direction;

    public Instruction(Vertex source, Vertex destination)
    {
        this.source = source;
        this.destination = destination;
        calculateDistance();
        findDirection();
    }

    //Used for special case of route optimization, since the vertex's dont directly connect to eachother
    public Instruction(Vertex source, Vertex destination, int direction)
    {
        this.source = source;
        this.destination = destination;
        this.direction = direction;
        calculateDistance();
    }

    private void findDirection()
    {
        if(source != null && destination != null)
        {
            int sourceToDestDirect = source.directionToVertexIs(destination);
            int destToSourceDirect = destination.directionToVertexIs(source);

            //Direction only matters for when the instruction goes north/south or east/west
            if(checkIfEastWest(sourceToDestDirect, destToSourceDirect) || checkIfNorthSouth(sourceToDestDirect, destToSourceDirect))
            {
                direction = sourceToDestDirect;
            }
            else
            {
                direction = 0;
            }
        }
    }

    private boolean checkIfEastWest(int firstDirection, int secDirection)
    {
        boolean eastWest = false;

        if(firstDirection == Vertex.EAST && secDirection == Vertex.WEST)
        {
            eastWest = true;
        }
        else if(firstDirection == Vertex.WEST && secDirection == Vertex.EAST)
        {
            eastWest = true;
        }

        return eastWest;
    }

    private boolean checkIfNorthSouth(int firstDirection, int secDirection)
    {
        boolean northSouth = false;

        if(firstDirection == Vertex.NORTH && secDirection == Vertex.SOUTH)
        {
            northSouth = true;
        }
        else if(firstDirection == Vertex.SOUTH && secDirection == Vertex.NORTH)
        {
            northSouth = true;
        }

        return northSouth;
    }

    private void calculateDistance()
    {
        this.distance = source.getDistanceFrom(destination);
    }

    public int getDistanceInMetres()
    {
        int metreDistance = distance;
        final double METERS_PER_FEET = 0.3048;

        if(source instanceof IndoorVertex && destination instanceof IndoorVertex)
        {
            metreDistance = (int) (distance * METERS_PER_FEET);
        }

        return metreDistance;
    }

    public int getDistance()
    {
        return distance;
    }

    public Vertex getSource() {
        return source;
    }

    public Vertex getDestination() {
        return destination;
    }

    public int getCurrDirection()
    {
        return direction;
    }

    public String getTextDirections()
    {
        String toString = "Go straight for " + distance;

        if(source instanceof IndoorVertex)
        {
            if(destination instanceof OutdoorVertex)
            {
                toString = "Exit the building";
            }
            else
            {
                IndoorVertex sourceIndoor = (IndoorVertex) source;
                IndoorVertex destIndoor = (IndoorVertex) destination;
                if(sourceIndoor.getFloor() == destIndoor.getFloor())
                {
                    toString += " feet";
                }
                else
                {
                    if(sourceIndoor.getFloor() > destIndoor.getFloor())
                    {
                        toString = "Go up ";
                    }
                    else
                    {
                        toString = "Go down ";
                    }
                    toString += Math.max(((IndoorVertex) source).getFloor(), ((IndoorVertex) destination).getFloor());
                    toString += " floors.";
                }
            }
        }
        else
        {
            if(destination instanceof IndoorVertex)
            {
                toString = "Enter " + ((IndoorVertex) destination).getBuilding();
            }
            else
            {
                toString += "m";
            }
        }

        return toString;
    }

}
