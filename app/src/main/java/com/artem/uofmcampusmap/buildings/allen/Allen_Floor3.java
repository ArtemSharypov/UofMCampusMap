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

public class Allen_Floor3 extends DrawIndoorPathsFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_allen_floor3, container, false);
        DrawingPathView drawingPathView = (DrawingPathView) view.findViewById(R.id.lines_view);

        setDrawingPathView(drawingPathView);
        setBuilding(getResources().getString(R.string.allen));
        setFloor(3);

        displayRoute();

        return view;
    }

    @Override
    protected int findXPixelFor(double xCoordinate) {
        int xPixel = 0;

        if(xCoordinate == 21.25)
        {
            xPixel = 150;
        }
        else if(xCoordinate == 62.5)
        {
            xPixel = 375;
        }
        else if(xCoordinate == 66.25)
        {
            xPixel = 408;
        }
        else if(xCoordinate == 71.25)
        {
            xPixel = 438;
        }
        else if(xCoordinate == 148.75)
        {
            xPixel = 850;
        }
        else if(xCoordinate == 157.5)
        {
            xPixel = 905;
        }
        else if(xCoordinate == 162.5)
        {
            xPixel = 930;
        }

        return xPixel;
    }

    @Override
    protected int findYPixelFor(double yCoordinate) {
        int yPixel = 0;

        if(yCoordinate == 36.25)
        {
            yPixel = 1020;
        }
        else if(yCoordinate == 41.25)
        {
            yPixel = 995;
        }
        else if(yCoordinate == 43.75)
        {
            yPixel = 976;
        }
        else if(yCoordinate == 65)
        {
            yPixel = 852;
        }
        else if(yCoordinate == 67.5)
        {
            yPixel = 844;
        }

        return yPixel;
    }
}
