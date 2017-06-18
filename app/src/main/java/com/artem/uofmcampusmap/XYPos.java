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

    public double getDistanceFrom(XYPos otherPoint)
    {
        double dist = 0;

        double tempX = Math.abs(this.x - otherPoint.getX());
        double tempY = Math.abs(this.y - otherPoint.getY());

        dist = Math.pow(tempX, 2) + Math.pow(tempY, 2);
        dist = Math.sqrt(dist);

        return dist;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
