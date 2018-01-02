package com.artem.uofmcampusmap.buildings;

public class Line {
    private int sourceX;
    private int sourceY;
    private int destX;
    private int destY;

    public Line(int sourceX, int sourceY, int destX, int destY)
    {
        this.sourceX = sourceX;
        this.sourceY = sourceY;
        this.destX = destX;
        this.destY = destY;
    }

    public int getSourceX() {
        return sourceX;
    }

    public int getSourceY() {
        return sourceY;
    }

    public int getDestX() {
        return destX;
    }

    public int getDestY() {
        return destY;
    }
}
