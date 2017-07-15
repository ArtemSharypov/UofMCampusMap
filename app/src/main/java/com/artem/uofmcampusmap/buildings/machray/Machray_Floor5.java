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

public class Machray_Floor5 extends DrawIndoorPathsFragment
{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_machray_floor5, container, false);

        DrawingPathView drawingPathView = (DrawingPathView) view.findViewById(R.id.lines_view);

        setDrawingPathView(drawingPathView);
        setBuilding(getResources().getString(R.string.machray));
        setFloor(5);

        displayRoute();

        return view;
    }

    //default is 1080 x 1920
    @Override
    protected int findXPixelFor(double xCoordinate)
    {
        int xPixel = 0;

        if(xCoordinate == 11.25)
        {
            xPixel = 95;
        }
        else if(xCoordinate == 18.75)
        {
            xPixel = 130;
        }
        else if(xCoordinate == 28.75)
        {
            xPixel = 200;
        }
        else if(xCoordinate == 33.75)
        {
            xPixel = 231;
        }
        else if(xCoordinate == 42.5)
        {
            xPixel = 285;
        }

        scaleXPixelPos(xPixel);

        return xPixel;
    }

    //default is 1080 x 1920
    @Override
    protected int findYPixelFor(double yCoordinate)
    {
        int yPixel = 0;

        if(yCoordinate == 21.88)
        {
            yPixel = 1505;
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
