package com.artem.uofmcampusmap.buildings.armes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.artem.uofmcampusmap.IndoorVertex;
import com.artem.uofmcampusmap.Instruction;
import com.artem.uofmcampusmap.PassRouteData;
import com.artem.uofmcampusmap.R;
import com.artem.uofmcampusmap.Route;
import com.artem.uofmcampusmap.Vertex;
import com.artem.uofmcampusmap.buildings.DisplayIndoorRoutes;
import com.artem.uofmcampusmap.buildings.DrawingPathView;
import com.artem.uofmcampusmap.buildings.Line;

/**
 * Created by Artem on 2017-05-15.
 */

public class ArmesFloor1Fragment extends Fragment implements DisplayIndoorRoutes {
    private DrawingPathView drawingPathsView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_armes_floor1, container, false);

        drawingPathsView = (DrawingPathView) view.findViewById(R.id.lines_view);
        displayIndoorRoute();

        return view;
    }

    /*
        default will be xhdpi, 720x1280

     */
    private int findXPixelFor(double xCoordinate)
    {
        int xPixelPos = -1;

        return xPixelPos;
    }

    private int findYPixelFor(double yCoordinate)
    {
        int yPixelPos = -1;

        return yPixelPos;
    }

    private boolean checkIfValidVertex(Vertex vertex)
    {
        boolean valid = false;
        IndoorVertex indoorVertex;

        if(vertex instanceof IndoorVertex)
        {
            indoorVertex = (IndoorVertex) vertex;

            if(indoorVertex.getBuilding().equals(getResources().getString(R.string.armes))
                    && indoorVertex.getFloor() == 1)
            {
                valid = true;
            }
        }

        return valid;
    }

    private boolean checkIfValidInstruc(Instruction instruction)
    {
        return checkIfValidVertex(instruction.getSource()) && checkIfValidVertex(instruction.getDestination());
    }

    @Override
    public void updateDisplayedRoute() {
        PassRouteData activity = (PassRouteData) getActivity();
        int currInstructionPos = activity.getCurrInstructionPos();

        drawingPathsView.updatePathPos(currInstructionPos);
        drawingPathsView.invalidate();
    }

    @Override
    public void displayIndoorRoute() {
        PassRouteData activity = (PassRouteData) getActivity();
        int currInstructionPos = activity.getCurrInstructionPos();
        Route route = activity.getRoute();

        int startPosOfIndoors = currInstructionPos;
        Instruction currInstruction;

        while(startPosOfIndoors >= 0)
        {
            currInstruction = route.getInstructionAt(startPosOfIndoors);

            if(checkIfValidInstruc(currInstruction))
            {
                startPosOfIndoors--;
                Line line = createLine(currInstruction);

                if(line != null)
                {
                    drawingPathsView.addPathToStart(line);
                }
            }
            else
            {
                break;
            }
        }

        drawingPathsView.setPosWithinRoute(startPosOfIndoors + 1);
        drawingPathsView.updatePathPos(currInstructionPos);

        currInstructionPos++;
        currInstruction = route.getInstructionAt(currInstructionPos);

        while(checkIfValidInstruc(currInstruction))
        {
            Line line = createLine(currInstruction);

            if(line != null)
            {
                drawingPathsView.addPathToEnd(line);
            }

            currInstructionPos++;
            currInstruction = route.getInstructionAt(currInstructionPos);
        }

        drawingPathsView.invalidate();
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
