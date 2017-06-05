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

    public WalkableZone(LatLng topLeft, LatLng topRight, LatLng bottomLeft, LatLng bottomRight)
    {
        this.topLeftCorner = new Vertex(topLeft);
        this.topRightCorner = new Vertex(topRight);
        this.bottomLeftCorner = new Vertex(bottomLeft);
        this.bottomRightCorner = new Vertex(bottomRight);
    }

    public void setConnectors(LatLng topMiddle, LatLng leftMiddle, LatLng rightMiddle, LatLng bottomMiddle)
    {
        if(topMiddle != null)
            this.topMiddle = new Vertex(topMiddle);

        if(leftMiddle != null)
            this.leftMiddle = new Vertex(leftMiddle);

        if(rightMiddle != null)
            this.rightMiddle = new Vertex(rightMiddle);

        if(bottomMiddle != null)
            this.bottomMiddle = new Vertex(bottomMiddle);

        topConnections();
        leftConnections();
        rightConnections();
        bottomConnections();
    }

    private void topConnections()
    {
        connectToTop(this.leftMiddle);
        connectToTop(this.rightMiddle);
        connectToTop(this.bottomMiddle);
    }

    private void leftConnections()
    {
        connectToLeft(this.topMiddle);
        connectToLeft(this.rightMiddle);
        connectToLeft(this.bottomMiddle);
    }

    private void rightConnections()
    {
        connectToRight(this.topMiddle);
        connectToRight(this.leftMiddle);
        connectToRight(this.bottomMiddle);
    }

    private void bottomConnections()
    {
        connectToBottom(this.topMiddle);
        connectToBottom(this.leftMiddle);
        connectToBottom(this.rightMiddle);
    }

    private void connectToRight(Vertex vertexToConnect)
    {
        if(vertexToConnect != null && rightMiddle != null)
        {
            vertexToConnect.addConnection(rightMiddle);
            rightMiddle.addConnection(vertexToConnect);
        }
    }

    private void connectToTop(Vertex vertexToConnect)
    {
        if(vertexToConnect != null && topMiddle != null)
        {
            vertexToConnect.addConnection(topMiddle);
            topMiddle.addConnection(vertexToConnect);
        }
    }

    private void connectToLeft(Vertex vertexToConnect)
    {
        if(vertexToConnect != null && leftMiddle != null)
        {
            vertexToConnect.addConnection(leftMiddle);
            leftMiddle.addConnection(vertexToConnect);
        }
    }

    private void connectToBottom(Vertex vertexToConnect)
    {
        if(vertexToConnect != null && bottomMiddle != null)
        {
            vertexToConnect.addConnection(bottomMiddle);
            bottomMiddle.addConnection(vertexToConnect);
        }
    }

    public void setTopMiddle(Vertex topMiddle) {
        this.topMiddle = topMiddle;
        topConnections();
    }

    public void setTopMiddle(LatLng topMiddle) {
        if(topMiddle != null)
        {
            this.topMiddle = new Vertex(topMiddle);
            topConnections();
        }
    }

    public void setLeftMiddle(Vertex leftMiddle) {
        this.leftMiddle = leftMiddle;
        leftConnections();
    }

    public void setLeftMiddle(LatLng leftMiddle)
    {
        if(leftMiddle != null) {
            this.leftMiddle = new Vertex(leftMiddle);
            leftConnections();
        }
    }

    public void setRightMiddle(Vertex rightMiddle) {
        this.rightMiddle = rightMiddle;
        rightConnections();
    }

    public void setRightMiddle(LatLng rightMiddle) {
        if(rightMiddle != null)
        {
            this.rightMiddle = new Vertex(rightMiddle);
            rightConnections();
        }
    }

    public void setBottomMiddle(Vertex bottomMiddle) {
        this.bottomMiddle = bottomMiddle;
        bottomConnections();
    }

    public void setBottomMiddle(LatLng bottomMiddle) {
        if(bottomMiddle != null)
        {
            this.bottomMiddle = new Vertex(bottomMiddle);
            bottomConnections();
        }
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
