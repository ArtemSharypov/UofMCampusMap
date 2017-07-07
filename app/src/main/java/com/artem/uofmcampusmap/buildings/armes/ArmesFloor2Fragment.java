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

    /* temp DP points for 768x1280, dpi is 318, or 318/160 = 1.9875
        Calculate pixel size for this one
        then just convert between different screen resolutions with the original pixels
        xy - dp - pixel
    floor2
        allen north ent (0, 131), 104, 254 - 207, 505
        allen south ent (0, 118), 104, 263 - 207, 523

        22/131 - 128, 254 - 255, 505
        22/118 - 128, 263 - 255, 523

        12/125 - 117, 259 - 233, 515
        12/37 - 117, 367 - 233, 730
        12/187 - 117, 175 - 233, 348
        11/206 - 117, 155 - 233, 309

        0/37 - 104, 367 - 207, 730
        0/187 - 104, 175 - 207, 348

        15/6 - 122, 400 - 243, 796

        31/118 - 140, 265 - 279, 525
        31/131 - 140, 252 - 279, 505
        31/137 - 140, 247 - 279, 491
        31/112 - 140, 270 - 279, 537

        37/125 - 149, 259 - 297, 515

        50/137 - 163, 247 - 324, 491
        50/131 - 163, 252 - 324, 505

        62/137 - 182, 247 - 362, 491
        62/118 - 182, 265 - 362, 525
        62/131 - 182, 252 - 362, 505
        62/112 - 182, 270 - 362, 537

        85/112 - 209, 270 - 416, 537
        85/118 - 209, 265 - 416, 525
        85/131 - 209, 252 - 416, 505
        85/137 - 209, 247 - 416, 491

        108/112 - 238, 270 - 474, 537
        108/118 - 238, 265 - 474, 525

        131/112 - 265, 270 - 527, 537
        131/118 - 265, 265 - 527, 525
        131/131 - 265, 252 - 527, 505

        156/125 - 297, 259 - 591, 515

        162/131 - 301, 252 - 599, 505
        162/137 - 301, 247 - 599, 491

        173/118 - 316, 263 - 629, 525
        173/131 - 316, 254 - 629, 505
     */

    //todo change the floor image as a constant size, changing depending on if it goes from mdpi or such
    //then can just use pixels offset as needed
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

        return yPixelPos;
    }
}
