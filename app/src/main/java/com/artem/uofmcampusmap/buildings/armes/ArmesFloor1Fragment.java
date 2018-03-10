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
    protected int findXDPIPositionFor(double xCoordinate)
    {
        int xDPIPos = 0;

        if(xCoordinate == -22.5)
        {
            xDPIPos = 38;
        }
        else if(xCoordinate == 10)
        {
            xDPIPos = 83;
        }
        else if(xCoordinate == 18.75)
        {
            xDPIPos = 101;
        }
        else if(xCoordinate == 56.25)
        {
            xDPIPos = 154;
        }
        else if(xCoordinate == 71.25)
        {
            xDPIPos = 173;
        }
        else if(xCoordinate == 87.5)
        {
            xDPIPos = 191;
        }
        else if(xCoordinate == 92.5)
        {
            xDPIPos = 198;
        }
        else if(xCoordinate == 93.75)
        {
            xDPIPos = 203;
        }
        else if(xCoordinate == 120)
        {
            xDPIPos = 246;
        }
        else if(xCoordinate == 130)
        {
            xDPIPos = 249;
        }
        else if(xCoordinate == 163.75)
        {
            xDPIPos = 305;
        }
        else if(xCoordinate == 168.75)
        {
            xDPIPos = 310;
        }

        return xDPIPos;
    }

    /* default is is in xxhdpi, image size is based on 1080 x 1400*/
    @Override
    protected int findYDPIPositionFor(double yCoordinate)
    {
        int yDPIPos = 0;

        if(yCoordinate == 95)
        {
            yDPIPos = 247;
        }
        else if(yCoordinate == 120)
        {
            yDPIPos = 215;
        }
        else if(yCoordinate == 122.5)
        {
            yDPIPos = 211;
        }
        else if(yCoordinate == 126.25)
        {
            yDPIPos = 206;
        }
        else if(yCoordinate == 132.5)
        {
            yDPIPos = 197;
        }
        else if(yCoordinate == 138.75)
        {
            yDPIPos = 187;
        }
        else if(yCoordinate == 156.25)
        {
            yDPIPos = 163;
        }
        else if(yCoordinate == 212.5)
        {
            yDPIPos = 85;
        }

        return yDPIPos;
    }
}
