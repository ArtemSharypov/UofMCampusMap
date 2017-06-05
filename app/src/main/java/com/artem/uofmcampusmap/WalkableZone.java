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

    /*
    //todo get rid of this in favour of the single ones
    public void setConnectors(LatLng top, LatLng left, LatLng right, LatLng bottom)
    {
        if(top != null)
            this.top = new Vertex(top);

        if(left != null)
            this.left = new Vertex(left);

        if(right != null)
            this.right = new Vertex(right);

        if(bottom != null)
            this.bottom = new Vertex(bottom);

        topConnections();
        leftConnections();
        rightConnections();
        bottomConnections();
    }
    */

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

    private void connectToRight(Vertex vertexToConnect)
    {
        if(vertexToConnect != null && right != null)
        {
            vertexToConnect.addConnection(right);
            right.addConnection(vertexToConnect);
        }
    }

    private void connectToTop(Vertex vertexToConnect)
    {
        if(vertexToConnect != null && top != null)
        {
            vertexToConnect.addConnection(top);
            top.addConnection(vertexToConnect);
        }
    }

    private void connectToLeft(Vertex vertexToConnect)
    {
        if(vertexToConnect != null && left != null)
        {
            vertexToConnect.addConnection(left);
            left.addConnection(vertexToConnect);
        }
    }

    private void connectToBottom(Vertex vertexToConnect)
    {
        if(vertexToConnect != null && bottom != null)
        {
            vertexToConnect.addConnection(bottom);
            bottom.addConnection(vertexToConnect);
        }
    }

    public void setTop(Vertex top) {
        this.top = top;
        topConnections();
    }

    public void setTop(LatLng top, XYPoint xyPos) {
        if(top != null)
        {
            this.top = new OutdoorVertex(top, xyPos);
            topConnections();
        }
    }

    public void setLeft(Vertex left) {
        this.left = left;
        leftConnections();
    }

    public void setLeft(LatLng pos, XYPoint xyPos)
    {
        if(pos != null) {
            this.left = new OutdoorVertex(pos, xyPos);
            leftConnections();
        }
    }

    public void setRight(Vertex right) {
        this.right = right;
        rightConnections();
    }

    public void setRight(LatLng pos, XYPoint xyPos) {
        if(pos != null)
        {
            this.right = new OutdoorVertex(pos, xyPos);
            rightConnections();
        }
    }

    public void setBottom(Vertex bottom) {
        this.bottom = bottom;
        bottomConnections();
    }

    public void setBottom(LatLng pos, XYPoint xyPos) {
        if(pos != null)
        {
            this.bottom = new OutdoorVertex(pos, xyPos);
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
