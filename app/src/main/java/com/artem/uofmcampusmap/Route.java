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

    private void addInstructionToStart(Instruction instruction)
    {
        if(instruction != null && instruction.getSource() != null && instruction.getDestination() != null)
        {
            if(route.size() > 0)
            {
                Instruction nextInstruct = route.get(0);
                int nextDirection = nextInstruct.getCurrDirection();
                int currDirection = instruction.getCurrDirection();
                Instruction optimizedInstruc;
                Vertex source;
                Vertex destination;

                //Direction has to be one of North, East, West, or South. 0 means no specific one
                if(currDirection != 0 && nextDirection == currDirection)
                {
                    source = instruction.getSource();
                    destination = nextInstruct.getDestination();
                    optimizedInstruc = new Instruction(source, destination, currDirection);

                    //Remove the instruction and the direction that now gets skipped over
                    route.remove(0);
                    routeLength -= nextInstruct.getWeight();
                    directions.remove(0); //Have to remove directions twice since the direction for this instruction has already been added
                    directions.remove(0);

                    //Add the now optimized instruction that skips over the un-necessary vertex, and update the directions to take
                    route.add(0, optimizedInstruc);
                    routeLength += optimizedInstruc.getWeight();
                    addDirectionToStart(optimizedInstruc);

                }
                else
                {
                    route.add(0, instruction);
                    routeLength += instruction.getWeight();
                    maxInstructions++;
                }
            }
            else
            {
                route.add(0, instruction);
                routeLength += instruction.getWeight();
                maxInstructions++;
            }
        }
    }

    private void addDirectionToStart(Instruction instruction)
    {
        String directionToAdd = instruction.getTextDirections();

        if(route.size() > 0)
        {
            Vertex source = instruction.getSource();
            Vertex destination = instruction.getDestination();
            int sourceToDestDirect = source.directionToVertexIs(destination);
            int destToSourceDirect = destination.directionToVertexIs(source);

            String turnDirections = turnDirections(sourceToDestDirect, destToSourceDirect);
            directionToAdd = turnDirections + directionToAdd;
        }

        directions.add(0, directionToAdd);
    }

    /*  For a source -> destination that is either North/South, then if destination -> source is either East/West it has to turn
        accordingly to switch directions. Same goes for East/West then North/South.
        Essentially means that the source is that direction from the destination in terms of N/S/W/E
     */
    private String turnDirections(int sourceToDestination, int destinationToSource)
    {
        String turnDirections = "";
        final String left = "Turn left and ";
        final String right = "Turn right and ";

        if(sourceToDestination == Vertex.NORTH)
        {
            if(destinationToSource == Vertex.EAST)
            {
                turnDirections = right;
            }
            else if(destinationToSource == Vertex.WEST)
            {
                turnDirections = left;
            }
        }
        else if(sourceToDestination == Vertex.SOUTH)
        {
            if(destinationToSource == Vertex.EAST)
            {
                turnDirections = left;
            }
            else if(destinationToSource == Vertex.WEST)
            {
                turnDirections = right;
            }
        }
        else if(sourceToDestination == Vertex.WEST)
        {
            if(destinationToSource == Vertex.NORTH)
            {
                turnDirections = right;
            }
            else if(destinationToSource == Vertex.SOUTH)
            {
                turnDirections = left;
            }
        }
        else if(sourceToDestination == Vertex.EAST)
        {
            if(destinationToSource == Vertex.NORTH)
            {
                turnDirections = left;
            }
            else if(destinationToSource == Vertex.SOUTH)
            {
                turnDirections = right;
            }
        }

        return turnDirections;
    }

    private void addInstructionToEnd(Instruction instruction)
    {
        if(instruction != null && instruction.getSource() != null && instruction.getDestination() != null)
        {
            int routeSize = route.size();

            if(routeSize > 0)
            {
                Instruction prevInstruction = route.get(routeSize -1);
                int currDirection = instruction.getCurrDirection();
                int prevDirection = prevInstruction.getCurrDirection();
                Instruction optimizedInstruc;
                Vertex source;
                Vertex destination;

                //Direction has to be one of North, East, West, or South. 0 means no specific one
                if (prevDirection != 0 && currDirection == prevDirection)
                {
                    source = prevInstruction.getSource();
                    destination = instruction.getDestination();
                    optimizedInstruc = new Instruction(source, destination, currDirection);

                    //Remove the instruction and the direction that now gets skipped over
                    route.remove(routeSize-1);
                    routeLength -= prevInstruction.getWeight();
                    directions.remove(routeSize-1); //Have to remove directions twice since the direction for this instruction has already been added
                    directions.remove(routeSize-1);

                    //Add the now optimized instruction that skips over the un-necessary vertex, and update the directions to take
                    route.add(optimizedInstruc);
                    routeLength += optimizedInstruc.getWeight();
                    addDirectionToEnd(optimizedInstruc);
                }
                else
                {
                    route.add(instruction);
                    routeLength += instruction.getWeight();
                    maxInstructions++;
                }
            }
            else
            {
                route.add(instruction);
                routeLength += instruction.getWeight();
                maxInstructions++;
            }
        }
    }

    private void addDirectionToEnd(Instruction instruction)
    {
        String directionToAdd = instruction.getTextDirections();

        if(route.size() > 0)
        {
            Vertex source = instruction.getSource();
            Vertex destination = instruction.getDestination();
            int sourceToDestDirect = source.directionToVertexIs(destination);
            int destToSourceDirect = destination.directionToVertexIs(source);

            String turnDirections = turnDirections(sourceToDestDirect, destToSourceDirect);
            directionToAdd = turnDirections + directionToAdd;
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
