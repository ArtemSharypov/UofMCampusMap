package com.artem.uofmcampusmap;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Artem on 2017-05-30.
 */

public class WalkableZone
{
    //Used to define the shape of the zone, any can be null
    private Vertex topLeftCorner;
    private Vertex topRightCorner;
    private Vertex bottomLeftCorner;
    private Vertex bottomRightCorner;

    //Used as part of the connections
    private Vertex topMiddle;
    private Vertex leftMiddle;
    private Vertex rightMiddle;
    private Vertex bottomMiddle;

    public WalkableZone(Vertex topLeft, Vertex topRight, Vertex bottomLeft, Vertex bottomRight)
    {
        this.topLeftCorner = topLeft;
        this.topRightCorner = topRight;
        this.bottomLeftCorner = bottomLeft;
        this.bottomRightCorner = bottomRight;
    }

    public WalkableZone(LatLng topLeft, LatLng topRight, LatLng bottomLeft, LatLng bottomRight)
    {
        this.topLeftCorner = new Vertex(topLeft);
        this.topRightCorner = new Vertex(topRight);
        this.bottomLeftCorner = new Vertex(bottomLeft);
        this.bottomRightCorner = new Vertex(bottomRight);
    }

    public void setConnectors(LatLng topMiddle, LatLng leftMiddle, LatLng rightMiddle, LatLng bottomMiddle)
    {
        this.topMiddle = new Vertex(topMiddle);
        this.leftMiddle = new Vertex(leftMiddle);
        this.rightMiddle = new Vertex(rightMiddle);
        this.bottomMiddle = new Vertex(bottomMiddle);

        //todo make any non null vertex's afterwards connect
    }

    public void setConnectors(Vertex topMiddle, Vertex leftMiddle, Vertex rightMiddle, Vertex bottomMiddle)
    {
        this.topMiddle = topMiddle;
        this.leftMiddle = leftMiddle;
        this.rightMiddle = rightMiddle;
        this.bottomMiddle = bottomMiddle;

        //todo make any non null vertex's afterwards connect
    }

    public void setTopMiddle(Vertex topMiddle) {
        this.topMiddle = topMiddle;
    }

    public void setTopMiddle(LatLng topMiddle) {
        this.topMiddle = new Vertex(topMiddle);
    }

    public void setLeftMiddle(Vertex leftMiddle) {
        this.leftMiddle = leftMiddle;
    }

    public void setLeftMiddle(LatLng leftMiddle) {
        this.leftMiddle = new Vertex(leftMiddle);
    }

    public void setRightMiddle(Vertex rightMiddle) {
        this.rightMiddle = rightMiddle;
    }

    public void setRightMiddle(LatLng rightMiddle) {
        this.rightMiddle = new Vertex(rightMiddle);
    }

    public void setBottomMiddle(Vertex bottomMiddle) {
        this.bottomMiddle = bottomMiddle;
    }

    public void setBottomMiddle(LatLng bottomMiddle) {
        this.bottomMiddle = new Vertex(bottomMiddle);
    }

    public Vertex getTopMiddle() {
        return topMiddle;
    }

    public Vertex getLeftMiddle() {
        return leftMiddle;
    }

    public Vertex getRightMiddle() {
        return rightMiddle;
    }

    public Vertex getBottomMiddle() {
        return bottomMiddle;
    }
}
