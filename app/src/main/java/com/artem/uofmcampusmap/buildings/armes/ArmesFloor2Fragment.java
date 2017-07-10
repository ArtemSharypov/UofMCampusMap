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

    //These pixel positions are the DEFAULT current ones for a 768x1280 image size currently
    //todo switch default to 1080*1920
    @Override
    protected int findXPixelFor(double xCoordinate) {
        int xPixelPos = -1;

        if (xCoordinate == 0) {
            xPixelPos = 207;
        } else if (xCoordinate == 12.5) {
            xPixelPos = 233;
        } else if (xCoordinate == 15) {
            xPixelPos = 243;
        } else if (xCoordinate == 22.5) {
            xPixelPos = 255;
        } else if (xCoordinate == 31.25) {
            xPixelPos = 279;
        } else if (xCoordinate == 37.5) {
            xPixelPos = 297;
        } else if (xCoordinate == 50) {
            xPixelPos = 324;
        } else if (xCoordinate == 62.5) {
            xPixelPos = 362;
        } else if (xCoordinate == 85) {
            xPixelPos = 416;
        } else if (xCoordinate == 108.5) {
            xPixelPos = 474;
        } else if (xCoordinate == 131.25) {
            xPixelPos = 527;
        } else if (xCoordinate == 156.25) {
            xPixelPos = 591;
        } else if (xCoordinate == 162.5) {
            xPixelPos = 599;
        } else if (xCoordinate == 173.75) {
            xPixelPos = 629;
        }

        scaleXPixelPos(xPixelPos);

        return xPixelPos;
    }

    //These pixel positions are the DEFAULT current ones for a 768x1280 image size currently
    //todo switch default to 1080*1920
    @Override
    protected int findYPixelFor(double yCoordinate) {
        int yPixelPos = -1;

        if (yCoordinate == 37.5) {
            yPixelPos = 730;
        } else if (yCoordinate == 112.5) {
            yPixelPos = 537;
        } else if (yCoordinate == 118.75) {
            yPixelPos = 525;
        } else if (yCoordinate == 125) {
            yPixelPos = 515;
        } else if (yCoordinate == 131.25) {
            yPixelPos = 505;
        } else if (yCoordinate == 137.5) {
            yPixelPos = 491;
        } else if (yCoordinate == 187.5) {
            yPixelPos = 348;
        } else if (yCoordinate == 206.25) {
            yPixelPos = 309;
        }

        scaleYPixelPos(yPixelPos);

        return yPixelPos;
    }
}
