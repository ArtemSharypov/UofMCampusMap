package com.artem.uofmcampusmap;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Artem on 2017-05-29.
 */

public class OutdoorVertex extends Vertex
{
    private LatLng position;

    OutdoorVertex(LatLng pos, XYPoint xyPos)
    {
        super(xyPos);
        this.position = pos;
    }

    OutdoorVertex(OutdoorVertex vertex)
    {
        super(vertex);
        this.position = vertex.getPosition();
    }

    public LatLng getPosition() {
        return position;
    }
}
