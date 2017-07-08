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

    @Override
    protected int findXPixelFor(double xCoordinate)
    {
        int xPixel = 0;

        if(xCoordinate == 0)
        {

        }
        else if(xCoordinate == 30.24)
        {

        }
        else if(xCoordinate == 44.19)
        {

        }
        else if(xCoordinate == 49.43)
        {

        }
        else if(xCoordinate == 80.25)
        {

        }

        return xPixel;
    }

    @Override
    protected int findYPixelFor(double yCoordinate)
    {
        int yPixel = 0;

        if(yCoordinate == 97.11)
        {

        }
        else if(yCoordinate == 151.19)
        {

        }
        else if(yCoordinate == 154.1)
        {

        }
        else if(yCoordinate == 158.75)
        {

        }
        else if(yCoordinate == 160.49)
        {

        }
        else if(yCoordinate == 173.29)
        {

        }
        else if(yCoordinate == 179.65)
        {

        }

        return yPixel;
    }
}
