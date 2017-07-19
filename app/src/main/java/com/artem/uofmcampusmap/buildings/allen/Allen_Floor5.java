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

public class Allen_Floor5 extends DrawIndoorPathsFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_allen_floor5, container, false);

        DrawingPathView drawingPathView = (DrawingPathView) view.findViewById(R.id.lines_view);

        setDrawingPathView(drawingPathView);
        setBuilding(getResources().getString(R.string.allen));
        setFloor(5);

        displayRoute();

        return view;
    }

    @Override
    protected int findXPixelFor(double xCoordinate) {
        int xPixel = 0;

        if(xCoordinate == -16.25)
        {
            xPixel = 150;
        }
        else if(xCoordinate == -62.5)
        {
            xPixel = 377;
        }
        else if(xCoordinate == -66.25)
        {
            xPixel = 407;
        }
        else if(xCoordinate == -71.25)
        {
            xPixel = 440;
        }
        else if(xCoordinate == -148.75)
        {
            xPixel = 843;
        }
        else if(xCoordinate == -152.5)
        {
            xPixel = 883;
        }

        return xPixel;
    }

    @Override
    protected int findYPixelFor(double yCoordinate) {
        int yPixel = 0;

        if(yCoordinate == 37.5)
        {
            yPixel = 744;
        }
        else if(yCoordinate == 41.25)
        {
            yPixel = 726;
        }
        else if(yCoordinate == 43.75)
        {
            yPixel = 703;
        }
        else if(yCoordinate == 71.25)
        {
            yPixel = 555;
        }

        return yPixel;
    }
}
