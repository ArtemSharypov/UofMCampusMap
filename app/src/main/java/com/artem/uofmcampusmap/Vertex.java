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
    private double g; //cost from the parent vertex
    private double h; //estimated cost from this to the destination
    private double f; //total cost (g+h)
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

    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }

    public double getH() {
        return h;
    }

    public void calculateH(Vertex endPoint) {
        this.h = getDistanceFrom(endPoint);
    }

    //todo make it a valid distance in feet, but not important since stuff will be changed to include inside buildings
    //todo fix it properly
    public double getDistanceFrom(Vertex vertex)
    {
        double distance;
        double latDistance;
        double longDistance;

        latDistance = Math.abs(Math.abs(vertex.getPosition().latitude) - Math.abs(position.latitude));
        longDistance = Math.abs(Math.abs(vertex.getPosition().longitude) - Math.abs(position.longitude));
        //distance = Math.sqrt(Math.exp(latDistance) + Math.exp(longDistance));
        distance = latDistance + longDistance;

        return distance;
    }

    public void setH(double h)
    {
        this.h = h;
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