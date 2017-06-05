package com.artem.uofmcampusmap;

/**
 * Created by Artem on 2017-06-05.
 */

public class XYPoint {
    private double x;
    private double y;

    public XYPoint(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public double getDistanceFrom(XYPoint otherPoint)
    {
        double dist = 0;

        double tempX = Math.abs(this.x - otherPoint.getX());
        double tempY = Math.abs(this.y = otherPoint.getY());

        dist = Math.exp(tempX) + Math.exp(tempY);
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
