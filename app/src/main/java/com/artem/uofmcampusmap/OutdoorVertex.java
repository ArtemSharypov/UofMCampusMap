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

            distance = calculateDistanceFrom(outdoorVertex.getPosition());
        }

        return distance;
    }

    private int calculateDistanceFrom(LatLng location)
    {
        int distance = 0;
        final double AVG_RADIUS_EARTH_KM = 6373;
        final double KM_TO_M = 1000;
        double latDistance;
        double lngDistance;
        double a;
        double c;

        if(location != null)
        {
            latDistance = Math.toRadians(position.latitude - location.latitude);
            lngDistance = Math.toRadians(position.longitude - location.longitude);

            a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                    + Math.cos(Math.toRadians(position.latitude)) * Math.cos(Math.toRadians(location.latitude))
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

            if(calculateDistanceFrom(posToCompare) == 0)
            {
                areEqual = true;
            }
        }

        return areEqual;
    }

    @Override
    public int hashCode() {
        return (int) (position.latitude + position.longitude);
    }
}
