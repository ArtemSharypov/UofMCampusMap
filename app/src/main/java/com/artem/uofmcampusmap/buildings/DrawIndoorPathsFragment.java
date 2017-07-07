package com.artem.uofmcampusmap.buildings;

import android.support.v4.app.Fragment;

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

    protected boolean checkIfValidInstruc(Instruction instruction)
    {
        return checkIfValidVertex(instruction.getSource()) && checkIfValidVertex(instruction.getDestination());
    }

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

    @Override
    public void updateDisplayedRoute() {
        PassRouteData activity = (PassRouteData) getActivity();
        int currInstructionPos = activity.getCurrInstructionPos();

        drawingPathView.updatePathPos(currInstructionPos);
        drawingPathView.invalidate();
    }

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
