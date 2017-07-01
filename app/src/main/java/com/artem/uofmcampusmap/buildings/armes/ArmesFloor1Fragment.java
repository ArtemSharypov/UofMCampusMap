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
import com.artem.uofmcampusmap.buildings.DrawingPathView;
import com.artem.uofmcampusmap.buildings.Line;

/**
 * Created by Artem on 2017-05-15.
 */

public class ArmesFloor1Fragment extends Fragment implements DisplayRoute {
    private DrawingPathView drawingPathsView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_armes_floor1, container, false);

        drawingPathsView = (DrawingPathView) view.findViewById(R.id.lines_view);
        displayRoute();

        return view;
    }

    /*
        default will be xhdpi, 720x1280
        xCoord - pixel
        -22.5 - 152
        10 - 225
        18.75 - 255
        56.25 - 340
        71.25 - 372
        87.5 - 402
        92.5 - 415
        93.75 - 423
        120 - 491
        130 - 500
        163.75 - 588
        168.75 - 598
     */
    private int findXPixelFor(double xCoordinate)
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

    /* default is is in xhdpi, 720x1280
        y coordinate - pixel pos
        95 - 590
        120 - 538
        122.5 - 535
        126.25 - 520
        132.5 - 505
        138.75 - 492
        156.25 - 454
        212.5 - 325
     */
    private int findYPixelFor(double yCoordinate)
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

    private boolean entranceOrExitInstruc(Instruction instruction)
    {
        boolean entranceExitInstruc = false;

        if((instruction.getDestination() instanceof OutdoorVertex && instruction.getSource() instanceof IndoorVertex) ||
                (instruction.getDestination() instanceof IndoorVertex && instruction.getSource() instanceof OutdoorVertex))
        {
            entranceExitInstruc = true;
        }

        return entranceExitInstruc;
    }

    private boolean stairsInstruction(Instruction instruction)
    {
        IndoorVertex source;
        IndoorVertex dest;
        boolean isStairsInstruc = false;

        if(instruction.getSource() instanceof IndoorVertex && instruction.getDestination() instanceof IndoorVertex)
        {
            source = (IndoorVertex) instruction.getSource();
            dest = (IndoorVertex) instruction.getDestination();

            if(source.getFloor() != dest.getFloor())
            {
                isStairsInstruc = true;
            }
        }

        return isStairsInstruc;
    }

    @Override
    public void displayRoute() {
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
                Line line = createLine(currInstruction);

                if(line != null)
                {
                    drawingPathsView.addPathToStart(line);
                }
            }
            else
            {
                if(!entranceOrExitInstruc(currInstruction) && !stairsInstruction(currInstruction))
                {
                    break;
                }
            }
            startPosOfIndoors--;
        }

        drawingPathsView.setPosWithinRoute(startPosOfIndoors + 1);
        drawingPathsView.updatePathPos(currInstructionPos);

        currInstructionPos++;
        currInstruction = route.getInstructionAt(currInstructionPos);

        while(currInstruction != null && checkIfValidInstruc(currInstruction))
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
