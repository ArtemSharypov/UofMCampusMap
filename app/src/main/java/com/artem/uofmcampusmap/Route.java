package com.artem.uofmcampusmap;

import java.util.ArrayList;

/**
 * Created by Artem on 2017-04-28.
 */

public class Route {
    private ArrayList<Instruction> route;
    private ArrayList<String> directions;
    private int maxInstructions;
    private double routeLength;

    Route()
    {
        route = new ArrayList<>();
        directions = new ArrayList<>();
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

    public String getDirectionsAt(int pos)
    {
        String direction = "";

        if(pos < directions.size() && pos >= 0)
        {
            direction = directions.get(pos);
        }

        return  direction;
    }

    public int getNumInstructions()
    {
        return route.size();
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
                reversedRoute.addDirectionToStart(instructionToAdd);
                reversedRoute.addInstructionToStart(instructionToAdd);

                prevVertex = currVertex;
                currVertex = currVertex.getParent();
            }
        }

        return reversedRoute;
    }

    private String createTurnDirections(int prevDirection, int currDirection)
    {
        String turnDirections = "";

        if(prevDirection != currDirection)
        {
            if(prevDirection == Vertex.NORTH)
            {
                turnDirections = createNorthDirectionTo(currDirection);
            }
            else if(currDirection == Vertex.NORTH)
            {
                turnDirections = createNorthDirectionTo(prevDirection);
            }
            else if(prevDirection == Vertex.SOUTH)
            {
                turnDirections = createSouthDirectionTo(currDirection);
            }
            else if(currDirection == Vertex.NORTH)
            {
                turnDirections = createSouthDirectionTo(prevDirection);
            }
        }

        return turnDirections;
    }

    private String createNorthDirectionTo(int secDirection)
    {
        String turnDirection = " and turn ";

        if(secDirection == Vertex.EAST)
        {
            turnDirection += "right.";
        }
        else if(secDirection == Vertex.WEST)
        {
            turnDirection += "left.";
        }

        return turnDirection;
    }

    private String createSouthDirectionTo(int secDirection)
    {
        String turnDirection = " and turn ";

        if(secDirection == Vertex.EAST)
        {
            turnDirection += "left.";
        }
        else if(secDirection == Vertex.WEST)
        {
            turnDirection += "right.";
        }

        return turnDirection;
    }

    private void addInstructionToStart(Instruction instruction)
    {
        if(instruction != null && instruction.getSource() != null && instruction.getDestination() != null)
        {
            route.add(0, instruction);
            routeLength += instruction.getWeight();
            maxInstructions++;
        }
    }

    private void addDirectionToStart(Instruction instruction)
    {
        String directionToAdd = instruction.getTextDirections();

        if(route.size() > 0)
        {
            int currDirection = instruction.getCurrDirection();
            int previousDirection = route.get(0).getCurrDirection();

            if (previousDirection >= 0) {
                if (currDirection != previousDirection) {
                    directionToAdd += createTurnDirections(previousDirection, currDirection);
                }
            }
        }

        directions.add(0, directionToAdd);
    }

    private void addInstructionToEnd(Instruction instruction)
    {
        if(instruction != null && instruction.getSource() != null && instruction.getDestination() != null)
        {
            route.add(instruction);
            routeLength += instruction.getWeight();
            maxInstructions++;
        }
    }

    private void addDirectionToEnd(Instruction instruction)
    {
        int routeSize = route.size();
        String directionToAdd = instruction.getTextDirections();

        if(routeSize > 0)
        {
            int currDirection = instruction.getCurrDirection();
            int previousDirection = route.get(routeSize - 1).getCurrDirection();

            if (previousDirection >= 0) {
                if (currDirection != previousDirection) {
                    directionToAdd += createTurnDirections(previousDirection, currDirection);
                }
            }
        }

        directions.add(directionToAdd);
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
                addDirectionToEnd(connection);
                addInstructionToEnd(connection);
            }

            //Add any other instructions from the second route into this one
            for(Instruction instruction : routeToCombine.route)
            {
                addDirectionToEnd(instruction);
                addInstructionToEnd(instruction);
            }
        }
    }
}
