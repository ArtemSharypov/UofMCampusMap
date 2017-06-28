package com.artem.uofmcampusmap;

import java.util.ArrayList;

/**
 * Created by Artem on 2017-04-28.
 */

public abstract class Vertex {
    private ArrayList<Vertex> connections;
    private double g; //cost from the parent vertex
    private double h; //estimated cost from this to the destination
    private double f; //total cost (g+h)
    private Vertex parent;

    public Vertex() {
        connections = new ArrayList<>();
    }

    public Vertex(Vertex vertexToCopy) {
        connections = new ArrayList<>(vertexToCopy.getConnections());
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

}
