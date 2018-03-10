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

public class Allen_Floor4 extends DrawIndoorPathsFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_allen_floor4, container, false);

        DrawingPathView drawingPathView = (DrawingPathView) view.findViewById(R.id.lines_view);

        setDrawingPathView(drawingPathView);
        setBuilding(getResources().getString(R.string.allen));
        setFloor(4);

        displayRoute();

        return view;
    }

    @Override
    protected int findXDPIPositionFor(double xCoordinate) {
        int xDPIPos = 0;

        if(xCoordinate == -62.5)
        {
            xDPIPos = 129;
        }
        else if(xCoordinate == -66.25)
        {
            xDPIPos = 140;
        }
        else if(xCoordinate == -71.25)
        {
            xDPIPos = 150;
        }
        else if(xCoordinate == -148.75)
        {
            xDPIPos = 279;
        }

        return xDPIPos;
    }

    @Override
    protected int findYDPIPositionFor(double yCoordinate) {
        int yDPIPos = 0;

        if(yCoordinate == 31.25)
        {
            yDPIPos = 258;
        }
        else if(yCoordinate == 36.25)
        {
            yDPIPos = 250;
        }
        else if(yCoordinate == 41.25)
        {
            yDPIPos = 243;
        }
        else if(yCoordinate == 43.75)
        {
            yDPIPos = 235;
        }

        return yDPIPos;
    }
}
