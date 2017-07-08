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

        return view;
    }

    //Default for a 1080x1920 resolution
    @Override
    protected int findXPixelFor(double xCoordinate)
    {
        int xPixel = 0;

        if(xCoordinate == 0)
        {
            xPixel = 73;
        }
        else if(xCoordinate == 30.24)
        {
            xPixel = 240;
        }
        else if(xCoordinate == 44.19)
        {
            xPixel = 320;
        }
        else if(xCoordinate == 49.43)
        {
            xPixel = 345;
        }
        else if(xCoordinate == 80.25)
        {
            xPixel = 519;
        }

        return xPixel;
    }

    //Default for a 1080x1920 resolution
    @Override
    protected int findYPixelFor(double yCoordinate)
    {
        int yPixel = 0;

        if(yCoordinate == 97.11)
        {
            yPixel = 1017;
        }
        else if(yCoordinate == 151.19)
        {
            yPixel = 1312;
        }
        else if(yCoordinate == 154.1)
        {
            yPixel = 1338;
        }
        else if(yCoordinate == 158.75)
        {
            yPixel = 1359;
        }
        else if(yCoordinate == 160.49)
        {
            yPixel = 1368;
        }
        else if(yCoordinate == 173.29)
        {
            yPixel = 1442;
        }
        else if(yCoordinate == 179.65)
        {
            yPixel = 1472;
        }
        else if(yCoordinate == 188.41)
        {
            yPixel = 1522;
        }
        return yPixel;
    }
}
