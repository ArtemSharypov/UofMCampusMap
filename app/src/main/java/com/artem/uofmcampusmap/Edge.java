package com.artem.uofmcampusmap;

/**
 * Created by Artem on 2017-04-28.
 */

public class Edge {
    private Vertex source;
    private Vertex destination;
    private long weight;

    public Edge(Vertex source, Vertex destination)
    {
        this.source = source;
        this.destination = destination;
    }

    public Edge(Vertex source, Vertex destination, long weight)
    {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }


    //todo: find weight of the edge based on distance from source to destination

    public long getWeight()
    {
        return weight;
    }

    public Vertex getSource() {
        return source;
    }

    public Vertex getDestination() {
        return destination;
    }

}
