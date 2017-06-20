package com.artem.uofmcampusmap;

/**
 * Created by Artem on 2017-04-28.
 */

public class Edge {
    private Vertex source;
    private Vertex destination;
    private int weight;

    public Edge(Vertex source, Vertex destination)
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

    //todo implement this fully
    public String getInstructions()
    {
        String toString = "" + weight;

        if(source instanceof IndoorVertex)
        {
            toString += " feet";
        }
        else
        {
            toString += "m";
        }

        return toString;
    }
}
