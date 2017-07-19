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
    protected int findXPixelFor(double xCoordinate)
    {
        int xPixel = 0;

        if(xCoordinate == 1011.25)
        {
            xPixel = 95;
        }
        else if(xCoordinate == 1018.75)
        {
            xPixel = 130;
        }
        else if(xCoordinate == 1028.75)
        {
            xPixel = 200;
        }
        else if(xCoordinate == 1033.75)
        {
            xPixel = 231;
        }
        else if(xCoordinate == 1035)
        {
            xPixel = 240;
        }
        else if(xCoordinate == 1047.75)
        {
            xPixel = 307;
        }
        else if(xCoordinate == 1072.5)
        {
            xPixel = 480;
        }
        else if(xCoordinate == 1085)
        {
            xPixel = 550;
        }

        return xPixel;
    }

    //Default for a 1080x1920 resolution
    @Override
    protected int findYPixelFor(double yCoordinate)
    {
        int yPixel = 0;

        if(yCoordinate == 21.88)
        {
            yPixel = 1238;
        }
        else if(yCoordinate == 26.88)
        {
            yPixel = 1205;
        }
        else if(yCoordinate == 28.13)
        {
            yPixel = 1192;
        }
        else if(yCoordinate == 34.38)
        {
            yPixel = 1151;
        }
        else if(yCoordinate == 37.5)
        {
            yPixel = 1131;
        }
        else if(yCoordinate == 71.88)
        {
            yPixel = 910;
        }

        return yPixel;
    }
}
