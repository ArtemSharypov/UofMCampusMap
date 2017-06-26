package com.artem.uofmcampusmap;

import java.util.ArrayList;

/**
 * Created by Artem on 2017-04-28.
 */

public class Route {
    private ArrayList<Instruction> route;
    private int currInstruction;
    private int maxInstructions;
    private double routeLength;

    Route()
    {
        route = new ArrayList<>();
        currInstruction = 0;
        maxInstructions = 0;
        routeLength = 0;
    }

    public Instruction getInstructionAt(int pos)
    {
        Instruction instruction = null;

        if(pos < route.size() && pos >= 0)
        {
            instruction = route.get(pos);
        }

        return instruction;
    }

    public int getNumInstructions()
    {
        return route.size();
    }

    public Instruction getFirstInstruction()
    {
        if(route.size() > 0)
        {
            maxInstructions = route.size();
            currInstruction = 1;
        }

        return route.get(0);
    }

    public Instruction getNextInstruction()
    {
        Instruction currInstruction = null;

        if(this.currInstruction < maxInstructions)
        {
            currInstruction = route.get(this.currInstruction);
            this.currInstruction++;
        }

        return currInstruction;
    }

    public boolean currRouteQuicker(Route route)
    {
        boolean quicker = false;

        if(routeLength < route.getRouteLength())
        {
            quicker = true;
        }

        return quicker;
    }

    public static Route reverseRoute(Vertex endVertex)
    {
        Route reversedRoute = null;
        Vertex currVertex;
        Vertex prevVertex;
        Instruction instructionToAdd;

        if(endVertex != null)
        {
            currVertex = endVertex.getParent();
            prevVertex = endVertex;
            reversedRoute = new Route();

            while (currVertex != null) {
                instructionToAdd = new Instruction(currVertex, prevVertex);
                reversedRoute.addInstructionToStart(instructionToAdd);

                prevVertex = currVertex;
                currVertex = currVertex.getParent();
            }
        }

        return reversedRoute;
    }

    private void addInstructionToStart(Instruction instruction)
    {
        if(instruction != null)
        {
            route.add(0, instruction);
            routeLength += instruction.getWeight();
            maxInstructions++;
        }
    }

    private void addInstructionToEnd(Instruction instruction)
    {
        if(instruction != null)
        {
            route.add(instruction);
            routeLength += instruction.getWeight();
            maxInstructions++;
        }
    }

    public double getRouteLength()
    {
        return routeLength;
    }

    //Adds the second route onto this route and just return a new route
    public void combineRoutes(Route routeToCombine)
    {
        if(routeToCombine != null)
        {
            //Only connect the two routes if they actually have more than one instruction
            if(this.maxInstructions > 0 && routeToCombine.maxInstructions > 0)
            {
                Instruction firstInstruc = routeToCombine.route.get(0);
                Instruction thisLastInstruc = route.get(maxInstructions - 1);

                //Connect the end destination of this, with the starting location of the second route
                Instruction connection = new Instruction(thisLastInstruc.getDestination(), firstInstruc.getSource());
                this.addInstructionToEnd(connection);
            }

            //Add any other instructions from the second route into this one
            for(Instruction instruction : routeToCombine.route)
            {
                this.addInstructionToEnd(instruction);
            }
        }
    }
}
