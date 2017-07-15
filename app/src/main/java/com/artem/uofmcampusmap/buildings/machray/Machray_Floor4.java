package com.artem.uofmcampusmap.buildings.machray;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.artem.uofmcampusmap.R;
import com.artem.uofmcampusmap.buildings.DrawIndoorPathsFragment;
import com.artem.uofmcampusmap.buildings.DrawingPathView;

/**
 * Created by Artem on 2017-05-24.
 */

public class Machray_Floor4 extends DrawIndoorPathsFragment
{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_machray_floor4, container, false);

        DrawingPathView drawingPathView = (DrawingPathView) view.findViewById(R.id.lines_view);

        setDrawingPathView(drawingPathView);
        setBuilding(getResources().getString(R.string.machray));
        setFloor(4);

        displayRoute();

        return view;
    }

    //Default for a 1080x1920 resolution
    @Override
    protected int findXPixelFor(double xCoordinate)
    {
        int xPixel = 0;

        if(xCoordinate == 1011.25)
        {
            xPixel = 95;
        }
        else if(xCoordinate == 1018.75)
        {
            xPixel = 130;
        }
        else if(xCoordinate == 1028.75)
        {
            xPixel = 200;
        }
        else if(xCoordinate == 1033.75)
        {
            xPixel = 231;
        }
        else if(xCoordinate == 1035)
        {
            xPixel = 240;
        }
        else if(xCoordinate == 1047.5)
        {
            xPixel = 305;
        }
        else if(xCoordinate == 1059.38)
        {
            xPixel = 392;
        }
        else if(xCoordinate == 1084.38)
        {
            xPixel = 545;
        }

        scaleXPixelPos(xPixel);

        return xPixel;
    }

    //Default for a 1080x1920 resolution
    @Override
    protected int findYPixelFor(double yCoordinate)
    {
        int yPixel = 0;

        if(yCoordinate == 21.88)
        {
            yPixel = 1505;
        }
        else if(yCoordinate == 23.13)
        {
            yPixel = 1480;
        }
        else if(yCoordinate == 28.13)
        {
            yPixel = 1450;
        }
        else if(yCoordinate == 34.38)
        {
            yPixel = 1415;
        }
        else if(yCoordinate == 37.5)
        {
            yPixel = 1390;
        }
        else if(yCoordinate == 71.88)
        {
            yPixel = 1170;
        }

        scaleYPixelPos(yPixel);

        return yPixel;
    }
}
