package com.artem.uofmcampusmap;

/**
 * Created by Artem on 2017-06-05.
 */

public class XYPos {
    private double x; //West/East distance from the 0 point, in METRES
    private double y; //North/South distance from the 0 point, in METRES

    public XYPos(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    //Calculates the distance between two XYPos's using a^2+c^2 = distance
    public int getDistanceFrom(XYPos otherPoint)
    {
        int dist;

        int tempX = (int) Math.abs(this.x - otherPoint.getX());
        int tempY = (int) Math.abs(this.y - otherPoint.getY());

        dist = (int) (Math.pow(tempX, 2) + Math.pow(tempY, 2));
        dist = (int) Math.sqrt(dist);

        return dist;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
