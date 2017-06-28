package com.artem.uofmcampusmap;

/**
 * Created by Artem on 2017-04-28.
 */

public class Instruction {
    private Vertex source;
    private Vertex destination;
    private int weight;

    public Instruction(Vertex source, Vertex destination)
    {
        this.source = source;
        this.destination = destination;
        calculateWeight();
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

    //todo implement this fully (as in turn left etc)
    public String getInstructions()
    {
        String toString = "" + weight;

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
