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
        int xPixelPos = 0;

        if(xCoordinate == -22.5)
        {
            xPixelPos = 114;
        }
        else if(xCoordinate == 10)
        {
            xPixelPos = 247;
        }
        else if(xCoordinate == 18.75)
        {
            xPixelPos = 302;
        }
        else if(xCoordinate == 56.25)
        {
            xPixelPos = 460;
        }
        else if(xCoordinate == 71.25)
        {
            xPixelPos = 517;
        }
        else if(xCoordinate == 87.5)
        {
            xPixelPos = 572;
        }
        else if(xCoordinate == 92.5)
        {
            xPixelPos = 594;
        }
        else if(xCoordinate == 93.75)
        {
            xPixelPos = 607;
        }
        else if(xCoordinate == 120)
        {
            xPixelPos = 736;
        }
        else if(xCoordinate == 130)
        {
            xPixelPos = 745;
        }
        else if(xCoordinate == 163.75)
        {
            xPixelPos = 913;
        }
        else if(xCoordinate == 168.75)
        {
            xPixelPos = 930;
        }

        return xPixelPos;
    }

    /* default is is in xxhdpi, image size is based on 1080 x 1400*/
    @Override
    protected int findYPixelFor(double yCoordinate)
    {
        int yPixelPos = 0;

        if(yCoordinate == 95)
        {
            yPixelPos = 740;
        }
        else if(yCoordinate == 120)
        {
            yPixelPos = 643;
        }
        else if(yCoordinate == 122.5)
        {
            yPixelPos = 632;
        }
        else if(yCoordinate == 126.25)
        {
            yPixelPos = 616;
        }
        else if(yCoordinate == 132.5)
        {
            yPixelPos = 589;
        }
        else if(yCoordinate == 138.75)
        {
            yPixelPos = 559;
        }
        else if(yCoordinate == 156.25)
        {
            yPixelPos = 488;
        }
        else if(yCoordinate == 212.5)
        {
            yPixelPos = 253;
        }

        return yPixelPos;
    }
}
