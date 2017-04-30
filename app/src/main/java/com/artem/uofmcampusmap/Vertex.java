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

    public void addEdge(Edge edgeToAdd)
    {
        connections.add(edgeToAdd);
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
