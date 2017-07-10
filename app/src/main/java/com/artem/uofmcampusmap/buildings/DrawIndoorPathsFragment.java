package com.artem.uofmcampusmap.buildings;

import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.WindowManager;

import com.artem.uofmcampusmap.DisplayRoute;
import com.artem.uofmcampusmap.IndoorVertex;
import com.artem.uofmcampusmap.Instruction;
import com.artem.uofmcampusmap.PassRouteData;
import com.artem.uofmcampusmap.Route;
import com.artem.uofmcampusmap.Vertex;

/**
 * Created by Artem on 2017-07-07.
 */

public abstract class DrawIndoorPathsFragment extends Fragment implements DisplayRoute {

    private DrawingPathView drawingPathView;
    private String building;
    private int floor;
    private final int DEFAULT_SCREEN_WIDTH = 1080;
    private final int DEFAULT_SCREEN_HEIGHT = 1920;

    protected void setDrawingPathView(DrawingPathView drawingPathView)
    {
        this.drawingPathView = drawingPathView;
    }

    protected void setBuilding(String building)
    {
        this.building = building;
    }

    protected void setFloor(int floor)
    {
        this.floor = floor;
    }

    abstract protected int findXPixelFor(double xCoordinate);
    abstract protected int findYPixelFor(double yCoordinate);

    //Scales a position of a pixel in the X coordinate, from 1080x1920 to the current screen resolution
    protected int scaleXPixelPos(int origXPixelPos)
    {
        int xPixelPos = origXPixelPos;
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int scale;

        //If the screen resolutions width is different than the default 1080 used for positions, then have to divide
        //curr width / 1080 to get the scale, which is applied to the xPixelPos
        if(width != DEFAULT_SCREEN_WIDTH)
        {
            scale = width / DEFAULT_SCREEN_WIDTH;
            xPixelPos *= scale;
        }

        return xPixelPos;
    }

    //Scales a position of a pixel in the Y coordinate, from 1080x1920 to the current screen resolution
    protected int scaleYPixelPos(int origYPixelPos)
    {
        int yPixelPos = origYPixelPos;
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        int scale;

        //If the screen resolutions height is different than the default 1920 used for positions, then have to divide
        //curr height / 1920 to get the scale, which is applied to the yPixelPos
        if(height != DEFAULT_SCREEN_HEIGHT)
        {
            scale = height / DEFAULT_SCREEN_HEIGHT;
            yPixelPos *= scale;
        }

        return yPixelPos;
    }

    protected boolean checkIfValidInstruc(Instruction instruction)
    {
        return checkIfValidVertex(instruction.getSource()) && checkIfValidVertex(instruction.getDestination());
    }

    //For a Vertex to be valid, It must be a IndoorVertex, that is within the expected building, and is on the expected floor
    private boolean checkIfValidVertex(Vertex vertex)
    {
        boolean valid = false;
        IndoorVertex indoorVertex;

        if(vertex instanceof IndoorVertex)
        {
            indoorVertex = (IndoorVertex) vertex;

            if(indoorVertex.getBuilding().equals(building) && indoorVertex.getFloor() == floor)
            {
                valid = true;
            }
        }

        return valid;
    }

    //Updates the route that is displayed within the drawingPathView, simply updates the position the view holds
    //and then forces the view to draw itself again
    @Override
    public void updateDisplayedRoute() {
        PassRouteData activity = (PassRouteData) getActivity();
        int currInstructionPos = activity.getCurrInstructionPos();

        drawingPathView.updatePathPos(currInstructionPos);
        drawingPathView.invalidate();
    }

    // Displays a route that comes from the Activity, starting at the current position within that route.
    @Override
    public void displayRoute()
    {
        PassRouteData activity = (PassRouteData) getActivity();
        int currInstructionPos = activity.getCurrInstructionPos();
        Route route = activity.getRoute();
        int startPosOfIndoors = currInstructionPos;
        Instruction currInstruction;

        if(route != null)
        {
            //Creates Lines (Paths that will be drawn) from the current position, going backwards until the Instruction is no longer
            //within the building, or is no longer within this floor.
            while (startPosOfIndoors >= 0)
            {
                currInstruction = route.getInstructionAt(startPosOfIndoors);

                if (checkIfValidInstruc(currInstruction))
                {
                    Line line = createLine(currInstruction);

                    if (line != null)
                    {
                        drawingPathView.addPathToStart(line);
                    }
                }
                else
                {
                    if (!currInstruction.isExitBuildingInstruction() && !currInstruction.isStairsInstruction())
                    {
                        break;
                    }
                }

                startPosOfIndoors--;
            }

            startPosOfIndoors++;

            drawingPathView.setPosWithinRoute(startPosOfIndoors);
            drawingPathView.updatePathPos(currInstructionPos);

            currInstructionPos++;

            //Creates Lines (Paths that will be drawn) from the current position, going forwards until the Instruction is no longer
            //within the building, or is no longer within this floor.
            while (currInstructionPos < route.getNumInstructions())
            {
                currInstruction = route.getInstructionAt(currInstructionPos);

                if (checkIfValidInstruc(currInstruction))
                {
                    Line line = createLine(currInstruction);

                    if (line != null)
                    {
                        drawingPathView.addPathToEnd(line);
                    }
                }
                else
                {
                    if (!currInstruction.isExitBuildingInstruction() && !currInstruction.isStairsInstruction())
                    {
                        break;
                    }
                }

                currInstructionPos++;
            }

            drawingPathView.invalidate();
        }
    }

    //Creates a Line from the Instruction's source position, to the destination in terms of pixel location
    //that is dependent on the screen resolution and the building layout
    private Line createLine(Instruction instructionToUse)
    {
        IndoorVertex source = (IndoorVertex) instructionToUse.getSource();
        IndoorVertex destination = (IndoorVertex) instructionToUse.getDestination();
        Line line = null;

        int sourceX = findXPixelFor(source.getPosition().getX());
        int sourceY = findYPixelFor(source.getPosition().getY());
        int destX = findXPixelFor(destination.getPosition().getX());
        int destY = findYPixelFor(destination.getPosition().getY());

        if(sourceX >= 0 && sourceY >= 0 && destX >= 0 && destY >= 0)
        {
            line = new Line(sourceX, sourceY, destX, destY);
        }

        return line;
    }
}
