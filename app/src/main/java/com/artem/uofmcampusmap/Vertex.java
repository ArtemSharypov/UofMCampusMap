package com.artem.uofmcampusmap;

import java.util.ArrayList;

/**
 * Created by Artem on 2017-04-28.
 */

public abstract class Vertex {
    private XYPoint xyPos;
    private ArrayList<Vertex> connections;
    private double g; //cost from the parent vertex
    private double h; //estimated cost from this to the destination
    private double f; //total cost (g+h)
    private Vertex parent;

    public Vertex(XYPoint xyPos) {
        this.xyPos = xyPos;
        connections = new ArrayList<>();
    }

    public Vertex(Vertex vertexToCopy) {
        this.xyPos = vertexToCopy.getXYPos();
        connections = new ArrayList<>(vertexToCopy.getConnections());
    }

    public void addConnection(Vertex vertexConnection) {
        connections.add(vertexConnection);
    }

    public XYPoint getXYPos() {
        return xyPos;
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

    public double getDistanceFrom(Vertex vertex)
    {
        double distance = xyPos.getDistanceFrom(vertex.getXYPos());

        return distance;
    }

    public double getF() {
        return f;
    }

    public Vertex getParent() {
        return parent;
    }

    public void setParent(Vertex parent) {
        this.parent = parent;
    }

    public boolean equals(Vertex vertex)
    {
        boolean areEqual = false;
        XYPoint posToCompare;

        if(vertex != null)
        {
            posToCompare = vertex.getXYPos();

            if(xyPos.getX() == posToCompare.getX() && xyPos.getY() == posToCompare.getY())
            {
                areEqual = true;
            }
        }

        return areEqual;
    }

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
