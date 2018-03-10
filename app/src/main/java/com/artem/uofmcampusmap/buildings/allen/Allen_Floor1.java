package com.artem.uofmcampusmap.buildings.allen;

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
 * Created by Artem on 2017-05-23.
 */

public class Allen_Floor1 extends DrawIndoorPathsFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_allen_floor1, container, false);

        DrawingPathView drawingPathView = (DrawingPathView) view.findViewById(R.id.lines_view);

        setDrawingPathView(drawingPathView);
        setBuilding(getResources().getString(R.string.allen));
        setFloor(1);

        displayRoute();

        return view;
    }

    @Override
    protected int findXDPIPositionFor(double xCoordinate) {
        int xDPIPos = 0;

        if(xCoordinate == -62.5)
        {
            xDPIPos = 127;
        }
        else if(xCoordinate == -67.5)
        {
            xDPIPos = 138;
        }
        else if(xCoordinate == -72.5)
        {
            xDPIPos = 147;
        }
        else if(xCoordinate == -141)
        {
            xDPIPos = 264;
        }
        else if(xCoordinate == -153.75)
        {
            xDPIPos = 283;
        }
        else if(xCoordinate == -155)
        {
            xDPIPos = 288;
        }
        else if(xCoordinate == -157.5)
        {
            xDPIPos = 290;
        }
        else if(xCoordinate == -172)
        {
            xDPIPos = 318;
        }
        else if(xCoordinate == -181.25)
        {
            xDPIPos = 333;
        }

        return xDPIPos;
    }

    @Override
    protected int findYDPIPositionFor(double yCoordinate) {
        int yDPIPos = 0;

        if(yCoordinate == 0)
        {
            yDPIPos = 334;
        }
        else if(yCoordinate == 31.25)
        {
            yDPIPos = 287;
        }
        else if(yCoordinate == 60)
        {
            yDPIPos = 232;
        }
        else if(yCoordinate == 68.75)
        {
            yDPIPos = 218;
        }
        else if(yCoordinate == 101.25)
        {
            yDPIPos = 164;
        }

        return yDPIPos;
    }
}
