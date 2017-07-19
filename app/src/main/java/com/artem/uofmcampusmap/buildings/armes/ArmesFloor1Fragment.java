package com.artem.uofmcampusmap.buildings.armes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.artem.uofmcampusmap.IndoorVertex;
import com.artem.uofmcampusmap.Instruction;
import com.artem.uofmcampusmap.OutdoorVertex;
import com.artem.uofmcampusmap.PassRouteData;
import com.artem.uofmcampusmap.R;
import com.artem.uofmcampusmap.Route;
import com.artem.uofmcampusmap.Vertex;
import com.artem.uofmcampusmap.DisplayRoute;
import com.artem.uofmcampusmap.buildings.DrawIndoorPathsFragment;
import com.artem.uofmcampusmap.buildings.DrawingPathView;
import com.artem.uofmcampusmap.buildings.Line;

/**
 * Created by Artem on 2017-05-15.
 */

public class ArmesFloor1Fragment extends DrawIndoorPathsFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_armes_floor1, container, false);

        DrawingPathView drawingPathsView = (DrawingPathView) view.findViewById(R.id.lines_view);

        setDrawingPathView(drawingPathsView);
        setBuilding(getResources().getString(R.string.armes));
        setFloor(1);

        displayRoute();

        return view;
    }

    /* default is xxhdpi, with an image size of 1080x1040 */
    @Override
    protected int findXPixelFor(double xCoordinate)
    {
        int xPixelPos = -1;

        if(xCoordinate == -22.5)
        {
            xPixelPos = 152;
        }
        else if(xCoordinate == 10)
        {
            xPixelPos = 225;
        }
        else if(xCoordinate == 18.75)
        {
            xPixelPos = 255;
        }
        else if(xCoordinate == 56.25)
        {
            xPixelPos = 340;
        }
        else if(xCoordinate == 71.25)
        {
            xPixelPos = 372;
        }
        else if(xCoordinate == 87.5)
        {
            xPixelPos = 402;
        }
        else if(xCoordinate == 92.5)
        {
            xPixelPos = 415;
        }
        else if(xCoordinate == 93.75)
        {
            xPixelPos = 423;
        }
        else if(xCoordinate == 120)
        {
            xPixelPos = 491;
        }
        else if(xCoordinate == 130)
        {
            xPixelPos = 500;
        }
        else if(xCoordinate == 163.75)
        {
            xPixelPos = 588;
        }
        else if(xCoordinate == 168.75)
        {
            xPixelPos = 598;
        }

        return xPixelPos;
    }

    /* default is is in xxhdpi, image size is based on 1080 x 1400*/
    @Override
    protected int findYPixelFor(double yCoordinate)
    {
        int yPixelPos = -1;

        if(yCoordinate == 95)
        {
            yPixelPos = 590;
        }
        else if(yCoordinate == 120)
        {
            yPixelPos = 538;
        }
        else if(yCoordinate == 122.5)
        {
            yPixelPos = 535;
        }
        else if(yCoordinate == 126.25)
        {
            yPixelPos = 520;
        }
        else if(yCoordinate == 132.5)
        {
            yPixelPos = 505;
        }
        else if(yCoordinate == 138.75)
        {
            yPixelPos = 492;
        }
        else if(yCoordinate == 156.25)
        {
            yPixelPos = 454;
        }
        else if(yCoordinate == 212.5)
        {
            yPixelPos = 325;
        }

        return yPixelPos;
    }
}
