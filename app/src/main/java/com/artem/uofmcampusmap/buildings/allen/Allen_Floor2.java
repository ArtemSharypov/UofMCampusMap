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
    protected int findXPixelFor(double xCoordinate) {
        int xPixel = 0;

        if(xCoordinate == 62.5)
        {
            xPixel = 305;
        }
        else if(xCoordinate == 65)
        {
            xPixel = 327;
        }
        else if(xCoordinate == 71.25)
        {
            xPixel = 357;
        }
        else if(xCoordinate == 153.75)
        {
            xPixel = 736;
        }
        else if(xCoordinate == 160)
        {
            xPixel = 756;
        }
        else if(xCoordinate == 162.5)
        {
            xPixel = 780;
        }
        else if(xCoordinate == 182.5)
        {
            xPixel = 876;
        }
        else if(xCoordinate == 200)
        {
            xPixel = 965;
        }

        return xPixel;
    }

    @Override
    protected int findYPixelFor(double yCoordinate) {
        int yPixel = 0;

        if(yCoordinate == 20)
        {
            yPixel = 1139;
        }
        else if(yCoordinate == 43.75)
        {
            yPixel = 1024;
        }
        else if(yCoordinate == 46.25)
        {
            yPixel = 1015;
        }
        else if(yCoordinate == 51.25)
        {
            yPixel = 1000;
        }
        else if(yCoordinate == 65)
        {
            yPixel = 921;
        }

        return yPixel;
    }
}
