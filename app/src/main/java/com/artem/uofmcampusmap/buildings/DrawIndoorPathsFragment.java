package com.artem.uofmcampusmap.buildings;

import android.support.v4.app.Fragment;

import com.artem.uofmcampusmap.DisplayRoute;
import com.artem.uofmcampusmap.IndoorVertex;
import com.artem.uofmcampusmap.Instruction;
import com.artem.uofmcampusmap.PassRouteData;
import com.artem.uofmcampusmap.Route;
import com.artem.uofmcampusmap.Vertex;


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

    /*For x and y coordinates, armes is the only unchanged building. Per each building west of armes, between the current building and
        armes, x gets changed by -1000 and for every building east, x gets changed by +1000.
          For each building north of armes, that is between the current building and armes, y gets changed by +1000. y gets changed by
           -1000 for each building south of armes with the same property as above
        */
    abstract protected int findXDPIPositionFor(double xCoordinate);
    abstract protected int findYDPIPositionFor(double yCoordinate);

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
                    if (!currInstruction.isExitBuildingInstruction() && !currInstruction.isStairsInstruction() && !currInstruction.isChangeBuildingsInstruction())
                    {
                        //Since this stops once the instruction is no longer considered valid (is outdoors or is on a different floor), it has to go to
                        //the position that is after this one, aka +1 pos over.
                        startPosOfIndoors++;
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

        int sourceX = findXDPIPositionFor(source.getPosition().getX());
        int sourceY = findYDPIPositionFor(source.getPosition().getY());
        int destX = findXDPIPositionFor(destination.getPosition().getX());
        int destY = findYDPIPositionFor(destination.getPosition().getY());

        if(sourceX >= 0 && sourceY >= 0 && destX >= 0 && destY >= 0)
        {
            line = new Line(sourceX, sourceY, destX, destY);
        }

        return line;
    }
}
