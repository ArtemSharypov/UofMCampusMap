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

public class Allen_Floor2 extends DrawIndoorPathsFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_allen_floor2, container, false);

        DrawingPathView drawingPathView = (DrawingPathView) view.findViewById(R.id.lines_view);

        setDrawingPathView(drawingPathView);
        setBuilding(getResources().getString(R.string.allen));
        setFloor(2);

        displayRoute();

        return view;
    }

    @Override
    protected int findXDPIPositionFor(double xCoordinate) {
        int xDPIPos = 0;

        if(xCoordinate == -62.5)
        {
            xDPIPos = 102;
        }
        else if(xCoordinate == -65)
        {
            xDPIPos = 109;
        }
        else if(xCoordinate == -71.25)
        {
            xDPIPos = 119;
        }
        else if(xCoordinate == -153.75)
        {
            xDPIPos = 245;
        }
        else if(xCoordinate == -160)
        {
            xDPIPos = 252;
        }
        else if(xCoordinate == -162.5)
        {
            xDPIPos = 260;
        }
        else if(xCoordinate == -182.5)
        {
            xDPIPos = 292;
        }
        else if(xCoordinate == -200)
        {
            xDPIPos = 322;
        }

        return xDPIPos;
    }

    @Override
    protected int findYDPIPositionFor(double yCoordinate) {
        int yDPIPos = 0;

        if(yCoordinate == 20)
        {
            yDPIPos = 292;
        }
        else if(yCoordinate == 43.75)
        {
            yDPIPos = 254;
        }
        else if(yCoordinate == 46.25)
        {
            yDPIPos = 251;
        }
        else if(yCoordinate == 51.25)
        {
            yDPIPos = 245;
        }
        else if(yCoordinate == 65)
        {
            yDPIPos = 220;
        }

        return yDPIPos;
    }
}
