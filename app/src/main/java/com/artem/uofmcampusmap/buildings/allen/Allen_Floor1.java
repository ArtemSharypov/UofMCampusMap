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
    protected int findXPixelFor(double xCoordinate) {
        int xPixel = 0;

        if(xCoordinate == 62.5)
        {
            xPixel = 382;
        }
        else if(xCoordinate == 67.5)
        {
            xPixel = 413;
        }
        else if(xCoordinate == 72.5)
        {
            xPixel = 440;
        }
        else if(xCoordinate == 141)
        {
            xPixel = 792;
        }
        else if(xCoordinate == 153.75)
        {
            xPixel = 850;
        }
        else if(xCoordinate == 155)
        {
            xPixel = 862;
        }
        else if(xCoordinate == 157.5)
        {
            xPixel = 870;
        }
        else if(xCoordinate == 172)
        {
            xPixel = 954;
        }
        else if(xCoordinate == 181.25)
        {
            xPixel = 998;
        }

        return xPixel;
    }

    @Override
    protected int findYPixelFor(double yCoordinate) {
        int yPixel = 0;

        if(yCoordinate == 0)
        {
            yPixel = 1260;
        }
        else if(yCoordinate == 31.25)
        {
            yPixel = 1116;
        }
        else if(yCoordinate == 60)
        {
            yPixel = 955;
        }
        else if(yCoordinate == 68.75)
        {
            yPixel = 915;
        }
        else if(yCoordinate == 101.25)
        {
            yPixel = 750;
        }

        return yPixel;
    }
}
