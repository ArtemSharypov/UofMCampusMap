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

public class Machray_Floor5 extends DrawIndoorPathsFragment
{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_machray_floor5, container, false);

        DrawingPathView drawingPathView = (DrawingPathView) view.findViewById(R.id.lines_view);

        setDrawingPathView(drawingPathView);
        setBuilding(getResources().getString(R.string.machray));
        setFloor(5);

        displayRoute();

        return view;
    }

    //default is 1080 x 1920
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
        else if(xCoordinate == 1042.5)
        {
            xPixel = 285;
        }

        return xPixel;
    }

    //default is 1080 x 1920
    @Override
    protected int findYPixelFor(double yCoordinate)
    {
        int yPixel = 0;

        if(yCoordinate == 21.88)
        {
            yPixel = 1246;
        }
        else if(yCoordinate == 28.13)
        {
            yPixel = 1189;
        }
        else if(yCoordinate == 34.38)
        {
            yPixel = 1153;
        }
        else if(yCoordinate == 37.5)
        {
            yPixel = 1132;
        }
        else if(yCoordinate == 71.88)
        {
            yPixel = 910;
        }

        return yPixel;
    }
}
