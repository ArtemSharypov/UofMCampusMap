package com.artem.uofmcampusmap;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Artem on 2017-04-28.
 */

public class Vertex {
    private String name;
    private LatLng position;
    private ArrayList<Edge> connections;
    private long g; //cost from the parent vertex
    private long h; //estimated cost from this to the destination
    private long f; //total cost (g+h)
    private Vertex parent;

    public Vertex(String name, LatLng position)
    {
        this.name = name;
        this.position = position;
        connections = new ArrayList<>();
    }

    public Vertex(LatLng position)
    {
        this.name = "";
        this.position = position;
        connections = new ArrayList<>();
    }

    public Vertex(Vertex vertexToCopy)
    {
        this.name = vertexToCopy.getName();
        this.position = vertexToCopy.getPosition();
        connections = new ArrayList<>(vertexToCopy.getConnections());
    }

    public void addConnection(Edge edgeToAdd)
    {
        connections.add(edgeToAdd);
    }

    public void addConnection(Vertex vertexToConnect)
    {
        connections.add(new Edge(this, vertexToConnect));
    }

    public Edge findConnection(Vertex vertex)
    {
        Edge edge = null;

        for(Edge currEdge: connections)
        {
            if(currEdge.getDestination().equals(vertex))
            {
                edge = currEdge;
                break;
            }
        }

        return edge;
    }

    public void calculateF()
    {
        this.f = g + h;
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
        //todo: calculate and set h to the weight from this to the end vertex
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

    public ArrayList<Edge> getConnections() {
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
