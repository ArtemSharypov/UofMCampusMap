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
            direction = source.directionToVertexIs(destination);
        }
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
