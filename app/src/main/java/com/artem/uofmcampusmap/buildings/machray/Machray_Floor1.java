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

public class Machray_Floor1 extends DrawIndoorPathsFragment
{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_machray_floor1, container, false);

        DrawingPathView drawingPathView = (DrawingPathView) view.findViewById(R.id.lines_view);

        setDrawingPathView(drawingPathView);
        setBuilding(getResources().getString(R.string.machray));
        setFloor(1);

        displayRoute();

        return view;
    }

    //Default for a 1080x1920 resolution
    @Override
    protected int findXPixelFor(double xCoordinate)
    {
        int xPixel = 0;

        if(xCoordinate == 1000)
        {
            xPixel = 108;
        }
        else if(xCoordinate == 1041.25)
        {
            xPixel = 294;
        }
        else if(xCoordinate == 1050)
        {
            xPixel = 343;
        }
        else if(xCoordinate == 1056.25)
        {
            xPixel = 370;
        }
        else if(xCoordinate == 1090)
        {
            xPixel = 533;
        }
        else if(xCoordinate == 1093.75)
        {
            xPixel = 555;
        }
        else if(xCoordinate == 1097.5)
        {
            xPixel = 568;
        }
        else if(xCoordinate == 1105)
        {
            xPixel = 594;
        }
        else if(xCoordinate == 1110)
        {
            xPixel = 632;
        }
        else if(xCoordinate == 1120)
        {
            xPixel = 680;
        }
        else if(xCoordinate == 1162.5)
        {
            xPixel = 873;
        }
        else if(xCoordinate == 1165)
        {
            xPixel = 897;
        }
        else if(xCoordinate == 1168.75)
        {
            xPixel = 908;
        }

        return xPixel;
    }

    //Default for a 1080x1920 resolution
    @Override
    protected int findYPixelFor(double yCoordinate)
    {
        int yPixel = 0;

        if(yCoordinate == -37.5)
        {
            yPixel = 1319;
        }
        else if(yCoordinate == 21.88)
        {
            yPixel = 1029;
        }
        else if(yCoordinate == 27.5)
        {
            yPixel = 998;
        }
        else if(yCoordinate == 28.13)
        {
            yPixel = 987;
        }
        else if(yCoordinate == 31.25)
        {
            yPixel = 971;
        }
        else if(yCoordinate == 40)
        {
            yPixel = 936;
        }
        else if(yCoordinate == 51.25)
        {
            yPixel = 890;
        }
        else if(yCoordinate == 72.5)
        {
            yPixel = 793;
        }
        else if(yCoordinate == 75)
        {
            yPixel = 780;
        }
        else if(yCoordinate == 77.5)
        {
            yPixel = 767;
        }
        else if(yCoordinate == 80)
        {
            yPixel = 756;
        }
        else if(yCoordinate == 81.25)
        {
            yPixel = 755;
        }
        else if(yCoordinate == 87.5)
        {
            yPixel = 730;
        }

        return yPixel;
    }
}
