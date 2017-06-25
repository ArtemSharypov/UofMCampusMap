package com.artem.uofmcampusmap.buildings.armes;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.artem.uofmcampusmap.Edge;
import com.artem.uofmcampusmap.IndoorVertex;
import com.artem.uofmcampusmap.R;
import com.artem.uofmcampusmap.Route;
import com.artem.uofmcampusmap.Vertex;
import com.artem.uofmcampusmap.buildings.DisplayIndoorRoutes;

/**
 * Created by Artem on 2017-05-15.
 */

public class ArmesFloor2Fragment extends Fragment implements DisplayIndoorRoutes {

    /* temp DP points for 768x1280
        Calculate pixel size for this one
        then just convert between different screen resolutions with the original pixels
        xy - dp - pixel
    floor2
        allen north ent (0, 131), 104, 254
        allen south ent (0, 118), 104, 263

        22/131 - 128, 254
        22/118 - 128, 263

        12/125 - 117, 259
        12/37 - 117, 367
        12/187 - 117, 175
        11/206 - 117, 155

        0/37 - 104, 367
        0/187 - 104, 175

        15/6 - 122, 400

        31/118 - 140, 265
        31/131 - 140, 252
        31/137 - 140, 247
        31/112 - 140, 270

        37/125 - 149, 259

        50/137 - 163, 247
        50/131 - 163, 252

        62/137 - 182, 247
        62/118 - 182, 265
        62/131 - 182, 252
        62/112 - 182, 270

        85/112 - 209, 270
        85/118 - 209, 265
        85/131 - 209, 252
        85/137 - 209, 247

        108/112 - 238, 270
        108/118 - 238, 265

        131/112 - 265, 270
        131/118 - 265, 265
        131/131 - 265, 252

        156/125 - 297, 259

        162/131 - 301, 252
        162/137 - 301, 247

        173/118 - 316, 263
        173/131 - 316, 254

     floor 1
     */

    private ImageView drawingLinesView;

    //todo change the floor image as a constant size, changing depending on if it goes from mdpi or such
    //then can just use pixels offset as needed
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_armes_floor2, container, false);

        drawingLinesView = (ImageView) view.findViewById(R.id.lines_view);

        return view;
    }

    private int findXPixelFor(double xCoordinate)
    {
        int xPixelPos = -1;

        if(xCoordinate == 0)
        {

        }
        else if(xCoordinate == 12.5)
        {

        }
        else if(xCoordinate == 15)
        {

        }
        else if(xCoordinate == 22.5)
        {

        }
        else if(xCoordinate == 31.25)
        {

        }
        else if(xCoordinate == 37.5)
        {

        }
        else if(xCoordinate == 50)
        {

        }
        else if(xCoordinate == 62.5)
        {

        }
        else if(xCoordinate == 85)
        {

        }
        else if(xCoordinate == 108.5)
        {

        }
        else if(xCoordinate == 131.25)
        {

        }
        else if(xCoordinate == 156.25)
        {

        }
        else if(xCoordinate == 162.5)
        {

        }
        else if(xCoordinate == 173.75)
        {

        }

        return xPixelPos;
    }

    private int findYPixelFor(double yCoordinate)
    {
        int yPixelPos = -1;

        if(yCoordinate == 37.5)
        {

        }
        else if(yCoordinate == 112.5)
        {

        }
        else if(yCoordinate == 118.75)
        {

        }
        else if(yCoordinate == 125)
        {

        }
        else if(yCoordinate == 131.25)
        {

        }
        else if(yCoordinate == 137.5)
        {

        }
        else if(yCoordinate == 187.5)
        {

        }
        else if(yCoordinate == 206.25)
        {

        }

        return yPixelPos;
    }

    @Override
    public void displayIndoorRoute(Edge pathToShow)
    {
        if(checkIfValidVertex(pathToShow.getSource()) && checkIfValidVertex(pathToShow.getDestination()))
        {
            IndoorVertex sourceVertex = (IndoorVertex) pathToShow.getSource();
            int sourceXPos = findXPixelFor(sourceVertex.getPosition().getX());
            int sourceYPos = findYPixelFor(sourceVertex.getPosition().getY());

            IndoorVertex destinationVertex = (IndoorVertex) pathToShow.getDestination();
            int destXPos = findXPixelFor(destinationVertex.getPosition().getX());
            int destYPos = findYPixelFor(destinationVertex.getPosition().getY());

            //draw the line on the canvas overlay
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            Bitmap bitmap = Bitmap.createBitmap(displayMetrics.widthPixels, displayMetrics.heightPixels,
                    Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            drawingLinesView.setImageBitmap(bitmap);

            Paint paint = new Paint();
            paint.setColor(Color.RED);

            canvas.drawLine(sourceXPos, sourceYPos, destXPos, destYPos, paint);
        }
    }

    @Override
    public void hideIndoorPath(Edge pathToHide) {

        if(checkIfValidVertex(pathToHide.getSource()) && checkIfValidVertex(pathToHide.getDestination()))
        {
            IndoorVertex sourceVertex = (IndoorVertex) pathToHide.getSource();
            int sourceXPos = findXPixelFor(sourceVertex.getPosition().getX());
            int sourceYPos = findYPixelFor(sourceVertex.getPosition().getY());

            IndoorVertex destinationVertex = (IndoorVertex) pathToHide.getDestination();
            int destXPos = findXPixelFor(destinationVertex.getPosition().getX());
            int destYPos = findYPixelFor(destinationVertex.getPosition().getY());

            //draw the line on the canvas overlay except transparent this time
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            Bitmap bitmap = Bitmap.createBitmap(displayMetrics.widthPixels, displayMetrics.heightPixels,
                    Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            drawingLinesView.setImageBitmap(bitmap);

            Paint paint = new Paint();
            paint.setColor(Color.TRANSPARENT);

            canvas.drawLine(sourceXPos, sourceYPos, destXPos, destYPos, paint);
        }
    }

    private boolean checkIfValidVertex(Vertex vertex)
    {
        boolean valid = false;
        IndoorVertex indoorVertex;

        if(vertex instanceof IndoorVertex)
        {
            indoorVertex = (IndoorVertex) vertex;

            if(indoorVertex.getBuilding().equals(getResources().getString(R.string.armes))
                    && indoorVertex.getFloor() == 2)
            {
                valid = true;
            }
        }

        return valid;
    }

    @Override
    public void showAllIndoorPaths(int instructionStart, Route route)
    {
        //Draw any routes that correspond to this floor of the building
        for(int i = instructionStart; instructionStart < route.getNumInstructions(); i++)
        {
            Edge currInstruction = route.getInstructionAt(i);

            if(checkIfValidVertex(currInstruction.getSource()) && checkIfValidVertex(currInstruction.getDestination()))
            {
                displayIndoorRoute(currInstruction);

            }
            else
            {
                break;
            }
        }
    }
}
