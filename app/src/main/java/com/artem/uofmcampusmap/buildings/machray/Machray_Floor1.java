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

public class Machray_Floor1 extends DrawIndoorPathsFragment
{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_machray_floor1, container, false);

        DrawingPathView drawingPathView = (DrawingPathView) view.findViewById(R.id.lines_view);

        setDrawingPathView(drawingPathView);
        setBuilding(getResources().getString(R.string.machray));
        setFloor(1);

        return view;
    }

    @Override
    protected int findXPixelFor(double xCoordinate)
    {
        int xPixel = 0;

        if(xCoordinate == 0)
        {

        }
        else if(xCoordinate == 41.25)
        {

        }
        else if(xCoordinate == 50)
        {

        }
        else if(xCoordinate == 56.25)
        {

        }
        else if(xCoordinate == 90)
        {

        }
        else if(xCoordinate == 93.75)
        {

        }
        else if(xCoordinate == 97.5)
        {

        }
        else if(xCoordinate == 108)
        {

        }
        else if(xCoordinate == 110)
        {

        }
        else if(xCoordinate == 120)
        {

        }
        else if(xCoordinate == 162.5)
        {

        }
        else if(xCoordinate == 165)
        {

        }
        else if(xCoordinate == 168.75)
        {

        }


        return xPixel;
    }

    @Override
    protected int findYPixelFor(double yCoordinate)
    {
        int yPixel = 0;

        if(yCoordinate == -37.5)
        {

        }
        else if(yCoordinate == 21.88)
        {

        }
        else if(yCoordinate == 27.5)
        {

        }
        else if(yCoordinate == 28.13)
        {

        }
        else if(yCoordinate == 31.25)
        {

        }
        else if(yCoordinate == 40)
        {

        }
        else if(yCoordinate == 51.25)
        {

        }
        else if(yCoordinate == 72.5)
        {

        }
        else if(yCoordinate == 75)
        {

        }
        else if(yCoordinate == 80)
        {

        }
        else if(yCoordinate == 81.25)
        {

        }
        else if(yCoordinate == 87.5)
        {

        }

        return yPixel;
    }
}
