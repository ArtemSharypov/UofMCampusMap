package com.artem.uofmcampusmap;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Artem on 2017-04-28.
 */

public class Vertex {
    private String name;
    private LatLng position;
    private ArrayList<Vertex> connections;
    private long g; //cost from the parent vertex
    private long h; //estimated cost from this to the destination
    private long f; //total cost (g+h)
    private Vertex parent;

    public Vertex(String name, LatLng position) {
        this.name = name;
        this.position = position;
        connections = new ArrayList<>();
    }

    public Vertex(LatLng position) {
        this.name = "";
        this.position = position;
        connections = new ArrayList<>();
    }

    public Vertex(Vertex vertexToCopy) {
        this.name = vertexToCopy.getName();
        this.position = vertexToCopy.getPosition();
        connections = new ArrayList<>(vertexToCopy.getConnections());
    }

    public void addConnection(Vertex vertexConnection) {
        connections.add(vertexConnection);
    }

    public boolean hasConnection(Vertex vertex)
    {
        boolean connects = false;

        for (Vertex currConnection : connections) {
            if (currConnection.equals(vertex)) {
                connects = true;
                break;
            }
        }

        return connects;
    }

    public void calculateF() {
        this.f = this.g + this.h;
    }

    public long getG() {
        return g;
    }

    public void setG(long g) {
        this.g = g;
    }

    public long getH() {
        return h;
    }

    public void calculateH(Vertex endPoint) {
        this.h = getDistanceFrom(endPoint);
    }

    public long getDistanceFrom(Vertex vertex)
    {
        long distance = 0;

        //todo: calculate distance from this to the specified vertex and return it

        return distance;
    }

    public void setH(long h)
    {
        this.h = h;
    }

    public long getF() {
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
        LatLng posToCompare;

        if(vertex != null)
        {
            posToCompare = vertex.getPosition();

            if(position.latitude == posToCompare.latitude && position.longitude == posToCompare.longitude)
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getPosition() {
        return position;
    }
}
