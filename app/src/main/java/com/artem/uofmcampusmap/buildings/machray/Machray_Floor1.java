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
    protected int findXDPIPositionFor(double xCoordinate)
    {
        int xDPIPos = 0;

        if(xCoordinate == 1000)
        {
            xDPIPos = 36;
        }
        else if(xCoordinate == 1041.25)
        {
            xDPIPos = 98;
        }
        else if(xCoordinate == 1050)
        {
            xDPIPos = 115;
        }
        else if(xCoordinate == 1056.25)
        {
            xDPIPos = 124;
        }
        else if(xCoordinate == 1090)
        {
            xDPIPos = 178;
        }
        else if(xCoordinate == 1093.75)
        {
            xDPIPos = 185;
        }
        else if(xCoordinate == 1097.5)
        {
            xDPIPos = 190;
        }
        else if(xCoordinate == 1105)
        {
            xDPIPos = 198;
        }
        else if(xCoordinate == 1110)
        {
            xDPIPos = 211;
        }
        else if(xCoordinate == 1120)
        {
            xDPIPos = 227;
        }
        else if(xCoordinate == 1162.5)
        {
            xDPIPos = 291;
        }
        else if(xCoordinate == 1165)
        {
            xDPIPos = 299;
        }
        else if(xCoordinate == 1168.75)
        {
            xDPIPos = 303;
        }

        return xDPIPos;
    }

    //Default for a 1080x1920 resolution
    @Override
    protected int findYDPIPositionFor(double yCoordinate)
    {
        int yDPIPos = 0;

        if(yCoordinate == -37.5)
        {
            yDPIPos = 440;
        }
        else if(yCoordinate == 21.88)
        {
            yDPIPos = 343;
        }
        else if(yCoordinate == 27.5)
        {
            yDPIPos = 333;
        }
        else if(yCoordinate == 28.13)
        {
            yDPIPos = 329;
        }
        else if(yCoordinate == 31.25)
        {
            yDPIPos = 324;
        }
        else if(yCoordinate == 40)
        {
            yDPIPos = 312;
        }
        else if(yCoordinate == 51.25)
        {
            yDPIPos = 297;
        }
        else if(yCoordinate == 72.5)
        {
            yDPIPos = 265;
        }
        else if(yCoordinate == 75)
        {
            yDPIPos = 260;
        }
        else if(yCoordinate == 77.5)
        {
            yDPIPos = 266;
        }
        else if(yCoordinate == 80)
        {
            yDPIPos = 252;
        }
        else if(yCoordinate == 81.25)
        {
            yDPIPos = 251;
        }
        else if(yCoordinate == 87.5)
        {
            yDPIPos = 244;
        }

        return yDPIPos;
    }
}
