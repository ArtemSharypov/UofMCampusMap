package com.artem.uofmcampusmap;

import java.util.HashSet;

/**
 * Created by Artem on 2017-04-28.
 */

public abstract class Vertex {
    private HashSet<Vertex> connections;
    private HashSet<Vertex> connectionsNorth;
    private HashSet<Vertex> connectionsSouth;
    private HashSet<Vertex> connectionsEast;
    private HashSet<Vertex> connectionsWest;
    public static int NORTH = 1;
    public static int SOUTH = 2;
    public static int EAST = 3;
    public static int WEST = 4;
    private double g; //cost from the parent vertex
    private double h; //estimated cost from this to the destination
    private double f; //total cost (g+h)
    private Vertex parent;

    public Vertex() {
        connections = new HashSet<>();
        connectionsNorth = new HashSet<>();
        connectionsSouth = new HashSet<>();
        connectionsWest = new HashSet<>();
        connectionsEast = new HashSet<>();
    }

    public Vertex(Vertex vertexToCopy) {
        connections = new HashSet<>(vertexToCopy.connections);
        connectionsNorth = new HashSet<>(vertexToCopy.connectionsNorth);
        connectionsSouth = new HashSet<>(vertexToCopy.connectionsSouth);
        connectionsWest = new HashSet<>(vertexToCopy.connectionsWest);
        connectionsEast = new HashSet<>(vertexToCopy.connectionsEast);
    }

    public void addConnection(Vertex vertexConnection) {
        connections.add(vertexConnection);
    }

    public void calculateF() {
        this.f = this.g + this.h;
    }

    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }

    public void calculateH(Vertex endPoint) {
        this.h = getDistanceFrom(endPoint);
    }

    public abstract int getDistanceFrom(Vertex vertex);

    public double getF() {
        return f;
    }

    public Vertex getParent() {
        return parent;
    }

    public void setParent(Vertex parent) {
        this.parent = parent;
    }

    public abstract boolean equals(Vertex vertex);

    @Override
    public boolean equals(Object obj) {
        boolean result;

        if(this == obj)
        {
            result = true;
        }
        else if(obj == null)
        {
            result = false;
        }
        else if(getClass() != obj.getClass())
        {
            result = false;
        }
        else
        {
            result = equals((Vertex) obj);
        }

        return result;
    }

    public HashSet<Vertex> getConnections() {
        return connections;
    }

    public void addNorthConnection(Vertex vertex)
    {
        connectionsNorth.add(vertex);
        connections.add(vertex);
    }

    public void addSouthConnection(Vertex vertex)
    {
        connectionsSouth.add(vertex);
        connections.add(vertex);
    }

    public void addEastConnection(Vertex vertex)
    {
        connectionsEast.add(vertex);
        connections.add(vertex);
    }

    public void addWestConnection(Vertex vertex)
    {
        connectionsWest.add(vertex);
        connections.add(vertex);
    }

    //used as source -> destination
    //returns the direction that must be taken to get from this to the destination
    public int directionToVertexIs(Vertex destination)
    {
        int direction = 0;

        if(connectionsNorth.contains(destination))
        {
            direction = NORTH;
        }
        else if(connectionsSouth.contains(destination))
        {
            direction = SOUTH;
        }
        else if(connectionsEast.contains(destination))
        {
            direction = EAST;
        }
        else if(connectionsWest.contains(destination))
        {
            direction = WEST;
        }

        return direction;
    }
}
