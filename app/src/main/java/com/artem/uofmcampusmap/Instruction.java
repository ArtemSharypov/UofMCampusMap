package com.artem.uofmcampusmap;

/**
 * Created by Artem on 2017-04-28.
 */

public class Instruction {
    private Vertex source;
    private Vertex destination;
    private int weight;
    private int direction;

    public Instruction(Vertex source, Vertex destination)
    {
        this.source = source;
        this.destination = destination;
        calculateWeight();
        findDirection();
    }

    private void findDirection()
    {
        if(source != null && destination != null)
        {
            int sourceToDestDirect = source.directionToVertexIs(destination);
            int destToSourceDirect = destination.directionToVertexIs(source);

            //todo check properly if it properly works
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

    private void calculateWeight()
    {
        this.weight = source.getDistanceFrom(destination);
    }

    public int getWeight()
    {
        return weight;
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
        String toString = "Go straight for " + weight;

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
