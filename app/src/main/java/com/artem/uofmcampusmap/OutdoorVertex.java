package com.artem.uofmcampusmap;


import org.osmdroid.util.GeoPoint;

public class OutdoorVertex extends Vertex
{
    private GeoPoint position;

    OutdoorVertex(GeoPoint pos)
    {
        super();
        this.position = pos;
    }

    OutdoorVertex(OutdoorVertex vertex)
    {
        super(vertex);
        this.position = vertex.getPosition();
    }

    public GeoPoint getPosition() {
        return position;
    }

    //Finds the distance between this Vertex and the specified one
    //Only works for when the passed Vertex is an Outdoor one
    @Override
    public int getDistanceFrom(Vertex vertex) {
        int distance = 0;
        OutdoorVertex outdoorVertex;

        if(vertex instanceof OutdoorVertex)
        {
            outdoorVertex = (OutdoorVertex) vertex;

            distance = calculateDistanceFrom(outdoorVertex.getPosition());
        }

        return distance;
    }

    //Calculates the distance in metres from this Vertex to the specified location
    private int calculateDistanceFrom(GeoPoint location)
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
            latDistance = Math.toRadians(position.getLatitude() - location.getLatitude());
            lngDistance = Math.toRadians(position.getLongitude()- location.getLongitude());

            a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                    + Math.cos(Math.toRadians(position.getLatitude())) * Math.cos(Math.toRadians(location.getLatitude()))
                    * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

            c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

            distance = (int) ((AVG_RADIUS_EARTH_KM * c) * KM_TO_M);
        }

        return distance;
    }

    //Find the closest (if there is one) indoor vertex that connects to the provided outdoor vertex
    public IndoorVertex findIndoorConnection()
    {
        IndoorVertex indoorVertex = null;

        //Find the indoor vertex (if there is one), set it and break the loop once its found
        for(Vertex vertex : getConnections())
        {
            if(vertex instanceof IndoorVertex)
            {
                indoorVertex = (IndoorVertex) vertex;
                break;
            }
        }

        return indoorVertex;
    }

    //Finds out if this and the passed Vertex are equal, used for the equal(Object obj) call
    //Must have the same LatLng position
    public boolean equals(Vertex vertex)
    {
        boolean areEqual = false;
        GeoPoint posToCompare;

        if(vertex != null && vertex instanceof OutdoorVertex)
        {
            posToCompare = ((OutdoorVertex) vertex).getPosition();

            //Since LatLng coords are double, the distance between themselves must be 0 to be the same point
            if(calculateDistanceFrom(posToCompare) == 0)
            {
                areEqual = true;
            }
        }

        return areEqual;
    }

    @Override
    public int hashCode() {
        return (int) (position.getLatitude() + position.getLongitude());
    }
}
