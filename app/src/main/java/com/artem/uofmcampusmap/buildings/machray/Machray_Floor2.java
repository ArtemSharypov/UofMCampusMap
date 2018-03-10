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

public class Machray_Floor2 extends DrawIndoorPathsFragment
{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_machray_floor2, container, false);

        DrawingPathView drawingPathView = (DrawingPathView) view.findViewById(R.id.lines_view);

        setDrawingPathView(drawingPathView);
        setBuilding(getResources().getString(R.string.machray));
        setFloor(2);

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
            xDPIPos = 24;
        }
        else if(xCoordinate == 1030.24)
        {
            xDPIPos = 80;
        }
        else if(xCoordinate == 1044.19)
        {
            xDPIPos = 106;
        }
        else if(xCoordinate == 1049.43)
        {
            xDPIPos = 115;
        }
        else if(xCoordinate == 1080.25)
        {
            xDPIPos = 173;
        }

        return xDPIPos;
    }

    //Default for a 1080x1920 resolution
    @Override
    protected int findYDPIPositionFor(double yCoordinate)
    {
        int yDPIPos = 0;

        if(yCoordinate == 97.11)
        {
            yDPIPos = 252;
        }
        else if(yCoordinate == 151.19)
        {
            yDPIPos = 351;
        }
        else if(yCoordinate == 154.1)
        {
            yDPIPos = 358;
        }
        else if(yCoordinate == 158.75)
        {
            yDPIPos = 366;
        }
        else if(yCoordinate == 160.49)
        {
            yDPIPos = 377;
        }
        else if(yCoordinate == 173.29)
        {
            yDPIPos = 393;
        }
        else if(yCoordinate == 179.65)
        {
            yDPIPos = 404;
        }
        else if(yCoordinate == 188.41)
        {
            yDPIPos = 425;
        }

        return yDPIPos;
    }
}
