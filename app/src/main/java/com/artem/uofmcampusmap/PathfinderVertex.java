package com.artem.uofmcampusmap;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Artem on 2017-04-28.
 */

public class PathfinderVertex extends Vertex {
    private long g; //cost from the parent vertex
    private long h; //estimated cost from this to the destination
    private long f; //total cost (g+h)
    private Vertex parent;

    public PathfinderVertex(String name, LatLng position)
    {
        super(name, position);
        initializeValues();
    }

    public PathfinderVertex(LatLng position)
    {
        super(position);
        initializeValues();
    }

    public PathfinderVertex(Vertex vertex)
    {
        super(vertex);
        initializeValues();
    }

    private void initializeValues()
    {
        g = -1;
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
}
