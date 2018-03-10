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

public class Machray_Floor3 extends DrawIndoorPathsFragment
{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_machray_floor3, container, false);

        DrawingPathView drawingPathView = (DrawingPathView) view.findViewById(R.id.lines_view);

        setDrawingPathView(drawingPathView);
        setBuilding(getResources().getString(R.string.machray));
        setFloor(3);

        displayRoute();

        return view;
    }

    //Default for a 1080x1920 resolution
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
        else if(xCoordinate == 1035)
        {
            xDPIPos = 80;
        }
        else if(xCoordinate == 1047.75)
        {
            xDPIPos = 103;
        }
        else if(xCoordinate == 1072.5)
        {
            xDPIPos = 160;
        }
        else if(xCoordinate == 1085)
        {
            xDPIPos = 184;
        }

        return xDPIPos;
    }

    //Default for a 1080x1920 resolution
    @Override
    protected int findYDPIPositionFor(double yCoordinate)
    {
        int yDPIPos = 0;

        if(yCoordinate == 21.88)
        {
            yDPIPos = 413;
        }
        else if(yCoordinate == 26.88)
        {
            yDPIPos = 402;
        }
        else if(yCoordinate == 28.13)
        {
            yDPIPos = 398;
        }
        else if(yCoordinate == 34.38)
        {
            yDPIPos = 384;
        }
        else if(yCoordinate == 37.5)
        {
            yDPIPos = 377;
        }
        else if(yCoordinate == 71.88)
        {
            yDPIPos = 304;
        }

        return yDPIPos;
    }
}
