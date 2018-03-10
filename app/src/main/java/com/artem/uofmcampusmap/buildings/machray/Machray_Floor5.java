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
    protected int findXDPIPositionFor(double xCoordinate)
    {
        int xDPIPos = 0;

        if(xCoordinate == 1011.25)
        {
            xDPIPos = 32;
        }
        else if(xCoordinate == 1018.75)
        {
            xDPIPos = 44;
        }
        else if(xCoordinate == 1028.75)
        {
            xDPIPos = 67;
        }
        else if(xCoordinate == 1033.75)
        {
            xDPIPos = 77;
        }
        else if(xCoordinate == 1042.5)
        {
            xDPIPos = 95;
        }

        return xDPIPos;
    }

    //default is 1080 x 1920
    @Override
    protected int findYDPIPositionFor(double yCoordinate)
    {
        int yDPIPos = 0;

        if(yCoordinate == 21.88)
        {
            yDPIPos = 416;
        }
        else if(yCoordinate == 28.13)
        {
            yDPIPos = 397;
        }
        else if(yCoordinate == 34.38)
        {
            yDPIPos = 385;
        }
        else if(yCoordinate == 37.5)
        {
            yDPIPos = 378;
        }
        else if(yCoordinate == 71.88)
        {
            yDPIPos = 304;
        }

        return yDPIPos;
    }
}
