package com.artem.uofmcampusmap;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Artem on 2017-05-29.
 */

public class OutdoorVertex extends Vertex
{
    private LatLng position;

    OutdoorVertex(LatLng pos)
    {
        super();
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

    @Override
    public int getDistanceFrom(Vertex vertex) {
        int distance = 0;
        OutdoorVertex outdoorVertex;
        final double AVG_RADIUS_EARTH_KM = 6373;
        final double KM_TO_M = 1000;
        double latDistance;
        double lngDistance;
        double a;
        double c;

        if(vertex instanceof OutdoorVertex)
        {
            outdoorVertex = (OutdoorVertex) vertex;

            latDistance = Math.toRadians(position.latitude - outdoorVertex.getPosition().latitude);
            lngDistance = Math.toRadians(position.longitude - outdoorVertex.getPosition().longitude);

            a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                    + Math.cos(Math.toRadians(position.latitude)) * Math.cos(Math.toRadians(outdoorVertex.getPosition().latitude))
                    * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

            c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

            distance = (int) ((AVG_RADIUS_EARTH_KM * c) * KM_TO_M);
        }

        return distance;
    }

    public boolean equals(Vertex vertex)
    {
        boolean areEqual = false;
        LatLng posToCompare;

        if(vertex != null && vertex instanceof OutdoorVertex)
        {
            posToCompare = ((OutdoorVertex) vertex).getPosition();

            if(position.latitude == posToCompare.latitude && position.longitude == posToCompare.longitude)
            {
                areEqual = true;
            }
        }

        return areEqual;
    }
}
