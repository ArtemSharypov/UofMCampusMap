package com.artem.uofmcampusmap.buildings.armes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.artem.uofmcampusmap.Instruction;
import com.artem.uofmcampusmap.IndoorVertex;
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

public class ArmesFloor2Fragment extends DrawIndoorPathsFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_armes_floor2, container, false);

        DrawingPathView drawingPathsView = (DrawingPathView) view.findViewById(R.id.lines_view);
        setDrawingPathView(drawingPathsView);

        setBuilding(getResources().getString(R.string.armes));
        setFloor(2);

        displayRoute();

        return view;
    }

    /* default is is in xxhdpi, image size is based on 1080 x 1400*/
    @Override
    protected int findXDPIPositionFor(double xCoordinate) {
        int xDPIPos = -1;

        if (xCoordinate == 0) {
            xDPIPos = 100;
        } else if (xCoordinate == 12.5) {
            xDPIPos = 111;
        } else if (xCoordinate == 15) {
            xDPIPos = 116;
        } else if (xCoordinate == 22.5) {
            xDPIPos = 123;
        } else if (xCoordinate == 31.25) {
            xDPIPos = 133;
        } else if (xCoordinate == 37.5) {
            xDPIPos = 142;
        } else if (xCoordinate == 50) {
            xDPIPos = 108;
        } else if (xCoordinate == 62.5) {
            xDPIPos = 154;
        } else if (xCoordinate == 85) {
            xDPIPos = 197;
        } else if (xCoordinate == 108.5) {
            xDPIPos = 224;
        } else if (xCoordinate == 131.25) {
            xDPIPos = 249;
        } else if (xCoordinate == 156.25) {
            xDPIPos = 279;
        } else if (xCoordinate == 162.5) {
            xDPIPos = 283;
        } else if (xCoordinate == 173.75) {
            xDPIPos = 297;
        }

        return xDPIPos;
    }

    /* default is is in xxhdpi, image size is based on 1080 x 1400*/
    @Override
    protected int findYDPIPositionFor(double yCoordinate) {
        int yDPIPos = -1;

        if(yCoordinate == 6.25) {
            yDPIPos = 344;
        } else if (yCoordinate == 37.5) {
            yDPIPos = 314;
        } else if (yCoordinate == 112.5) {
            yDPIPos = 223;
        } else if (yCoordinate == 118.75) {
            yDPIPos = 216;
        } else if (yCoordinate == 125) {
            yDPIPos = 211;
        } else if (yCoordinate == 131.25) {
            yDPIPos = 207;
        } else if (yCoordinate == 137.5) {
            yDPIPos = 199;
        } else if (yCoordinate == 187.5) {
            yDPIPos = 135;
        } else if (yCoordinate == 206.25) {
            yDPIPos = 113;
        }

        return yDPIPos;
    }
}
