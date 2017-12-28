package com.artem.uofmcampusmap;

import java.util.HashSet;

public abstract class Vertex {
    private HashSet<Vertex> connections;
    private HashSet<Vertex> connectionsNorth;
    private HashSet<Vertex> connectionsSouth;
    private HashSet<Vertex> connectionsEast;
    private HashSet<Vertex> connectionsWest;
    private HashSet<Vertex> connectionsNorthWest;
    private HashSet<Vertex> connectionsNorthEast;
    private HashSet<Vertex> connectionsSouthWest;
    private HashSet<Vertex> connectionsSouthEast;
    public final static int NORTH = 1;
    public final static int SOUTH = 2;
    public final static int EAST = 3;
    public final static int WEST = 4;
    public final static int NORTH_WEST = 5;
    public final static int NORTH_EAST = 6;
    public final static int SOUTH_WEST = 7;
    public final static int SOUTH_EAST = 8;
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
        connectionsNorthEast = new HashSet<>();
        connectionsNorthWest = new HashSet<>();
        connectionsSouthWest = new HashSet<>();
        connectionsSouthEast = new HashSet<>();
    }

    public Vertex(Vertex vertexToCopy) {
        connections = new HashSet<>(vertexToCopy.connections);
        connectionsNorth = new HashSet<>(vertexToCopy.connectionsNorth);
        connectionsSouth = new HashSet<>(vertexToCopy.connectionsSouth);
        connectionsWest = new HashSet<>(vertexToCopy.connectionsWest);
        connectionsEast = new HashSet<>(vertexToCopy.connectionsEast);
        connectionsNorthEast = new HashSet<>(vertexToCopy.connectionsNorthEast);
        connectionsNorthWest = new HashSet<>(vertexToCopy.connectionsNorthWest);
        connectionsSouthWest = new HashSet<>(vertexToCopy.connectionsSouthWest);
        connectionsSouthEast = new HashSet<>(vertexToCopy.connectionsSouthEast);
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

    //Used for HashMap / Set
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

    public void addNorthEastConnection(Vertex vertex)
    {
        connectionsNorthEast.add(vertex);
        connections.add(vertex);
    }

    public void addNorthWestConnection(Vertex vertex)
    {
        connectionsNorthWest.add(vertex);
        connections.add(vertex);
    }

    public void addSouthEastConnection(Vertex vertex)
    {
        connectionsSouthEast.add(vertex);
        connections.add(vertex);
    }

    public void addSouthWestConnection(Vertex vertex)
    {
        connectionsSouthWest.add(vertex);
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
        else if(connectionsNorthWest.contains(destination))
        {
            direction = NORTH_WEST;
        }
        if(connectionsNorthEast.contains(destination))
        {
            direction = NORTH_EAST;
        }
        if(connectionsSouthWest.contains(destination))
        {
            direction = SOUTH_WEST;
        }
        if(connectionsSouthEast.contains(destination))
        {
            direction = SOUTH_EAST;
        }

        return direction;
    }
}
