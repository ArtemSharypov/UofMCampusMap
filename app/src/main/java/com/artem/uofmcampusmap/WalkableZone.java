package com.artem.uofmcampusmap;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Artem on 2017-05-30.
 */

public class WalkableZone
{
    //Used to define the shape of the zone, any can be null
    private LatLng topLeftCorner;
    private LatLng topRightCorner;
    private LatLng bottomLeftCorner;
    private LatLng bottomRightCorner;

    //Used as part of the connections
    private Vertex top;
    private Vertex left;
    private Vertex right;
    private Vertex bottom;

    public WalkableZone(LatLng topLeft, LatLng topRight, LatLng bottomLeft, LatLng bottomRight)
    {
        this.topLeftCorner = topLeft;
        this.topRightCorner = topRight;
        this.bottomLeftCorner = bottomLeft;
        this.bottomRightCorner = bottomRight;
    }

    private void topConnections()
    {
        connectToTop(this.left);
        connectToTop(this.right);
        connectToTop(this.bottom);
    }

    private void leftConnections()
    {
        connectToLeft(this.top);
        connectToLeft(this.right);
        connectToLeft(this.bottom);
    }

    private void rightConnections()
    {
        connectToRight(this.top);
        connectToRight(this.left);
        connectToRight(this.bottom);
    }

    private void bottomConnections()
    {
        connectToBottom(this.top);
        connectToBottom(this.left);
        connectToBottom(this.right);
    }

    public void connectToRight(Vertex vertexToConnect)
    {
        if(vertexToConnect != null && right != null)
        {
            vertexToConnect.addEastConnection(right);
            right.addWestConnection(vertexToConnect);
        }
    }

    public void connectToTop(Vertex vertexToConnect)
    {
        if(vertexToConnect != null && top != null)
        {
            vertexToConnect.addNorthConnection(top);
            top.addSouthConnection(vertexToConnect);
        }
    }

    public void connectToLeft(Vertex vertexToConnect)
    {
        if(vertexToConnect != null && left != null)
        {
            vertexToConnect.addWestConnection(left);
            left.addEastConnection(vertexToConnect);
        }
    }

    public void connectToBottom(Vertex vertexToConnect)
    {
        if(vertexToConnect != null && bottom != null)
        {
            vertexToConnect.addSouthConnection(bottom);
            bottom.addNorthConnection(vertexToConnect);
        }
    }

    public void setTop(Vertex top) {
        this.top = top;
        topConnections();
    }

    public void setTop(LatLng top) {
        if(top != null)
        {
            this.top = new OutdoorVertex(top);
            topConnections();
        }
    }

    public void setLeft(Vertex left) {
        this.left = left;
        leftConnections();
    }

    public void setLeft(LatLng pos)
    {
        if(pos != null) {
            this.left = new OutdoorVertex(pos);
            leftConnections();
        }
    }

    public void setRight(Vertex right) {
        this.right = right;
        rightConnections();
    }

    public void setRight(LatLng pos) {
        if(pos != null)
        {
            this.right = new OutdoorVertex(pos);
            rightConnections();
        }
    }

    public void setBottom(Vertex bottom) {
        this.bottom = bottom;
        bottomConnections();
    }

    public void setBottom(LatLng pos) {
        if(pos != null)
        {
            this.bottom = new OutdoorVertex(pos);
            bottomConnections();
        }
    }

    public Vertex getTop() {
        return top;
    }

    public Vertex getLeft() {
        return left;
    }

    public Vertex getRight() {
        return right;
    }

    public Vertex getBottom() {
        return bottom;
    }

    public LatLng getTopLeftCorner() {
        return topLeftCorner;
    }

    public LatLng getTopRightCorner() {
        return topRightCorner;
    }

    public LatLng getBottomLeftCorner() {
        return bottomLeftCorner;
    }

    public LatLng getBottomRightCorner() {
        return bottomRightCorner;
    }
}
