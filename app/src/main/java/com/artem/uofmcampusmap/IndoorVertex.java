package com.artem.uofmcampusmap;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Artem on 2017-05-29.
 */

public class IndoorVertex extends Vertex
{
    private int floor;

    IndoorVertex(String name, LatLng pos)
    {
        super(name, pos);
    }

    @Override
    public double getDistanceFrom(Vertex vertex) {
        return super.getDistanceFrom(vertex);
    }
}
