package com.artem.uofmcampusmap;

import java.util.ArrayList;

/**
 * Created by Artem on 2017-04-28.
 */

public abstract class Vertex {
    private ArrayList<Vertex> connections;
    private ArrayList<Vertex> connectionsNorth;
    private ArrayList<Vertex> connectionsSouth;
    private ArrayList<Vertex> connectionsEast;
    private ArrayList<Vertex> connectionsWest;
    public static int NORTH = 1;
    public static int SOUTH = 2;
    public static int EAST = 3;
    public static int WEST = 4;
    private double g; //cost from the parent vertex
    private double h; //estimated cost from this to the destination
    private double f; //total cost (g+h)
    private Vertex parent;

    public Vertex() {
        connections = new ArrayList<>();
        connectionsNorth = new ArrayList<>();
        connectionsSouth = new ArrayList<>();
        connectionsWest = new ArrayList<>();
        connectionsEast = new ArrayList<>();
    }

    public Vertex(Vertex vertexToCopy) {
        connections = new ArrayList<>(vertexToCopy.getConnections());
        connectionsNorth = new ArrayList<>(vertexToCopy.connectionsNorth);
        connectionsSouth = new ArrayList<>(vertexToCopy.connectionsSouth);
        connectionsWest = new ArrayList<>(vertexToCopy.connectionsWest);
        connectionsEast = new ArrayList<>(vertexToCopy.connectionsEast);
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
        boolean result = false;

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

    public ArrayList<Vertex> getConnections() {
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

    public int directionToVertexIs(Vertex vertex)
    {
        int direction = 0;

        if(connectionsNorth.contains(vertex))
        {
            direction = NORTH;
        }
        else if(connectionsSouth.contains(vertex))
        {
            direction = SOUTH;
        }
        else if(connectionsEast.contains(vertex))
        {
            direction = EAST;
        }
        else if(connectionsWest.contains(vertex))
        {
            direction = WEST;
        }

        return direction;
    }
}
