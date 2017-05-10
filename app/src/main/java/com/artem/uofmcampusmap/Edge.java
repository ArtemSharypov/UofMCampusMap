package com.artem.uofmcampusmap;

/**
 * Created by Artem on 2017-04-28.
 */

public class Edge {
    private Vertex source;
    private Vertex destination;
    private double weight;

    public Edge(Vertex source, Vertex destination)
    {
        this.source = source;
        this.destination = destination;
        calculateWeight();
    }

    public Edge(Vertex source, Vertex destination, double weight)
    {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    private void calculateWeight()
    {
        this.weight = source.getDistanceFrom(destination);
    }

    public double getWeight()
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
        return "" + weight;
    }
}
