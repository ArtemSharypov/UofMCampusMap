package com.artem.uofmcampusmap;

import java.util.ArrayList;

/**
 * Created by Artem on 2017-04-28.
 */

public class Route {
    private ArrayList<Edge> route;
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

    public Edge getInstructionAt(int pos)
    {
        Edge edge = null;

        if(pos < route.size() && pos >= 0)
        {
            edge = route.get(pos);
        }

        return edge;
    }

    public int getNumInstructions()
    {
        return route.size();
    }

    //todo for this and next instruction implement the check for indoorvertex's, to see what building it currently is in.
    //when the source is a building, switch to that floor/layout and draw in the lines as needed for that layout
    public Edge getFirstInstruction()
    {
        if(route.size() > 0)
        {
            maxInstructions = route.size();
            currInstruction = 1;
        }

        return route.get(0);
    }

    public Edge getNextInstruction()
    {
        Edge currEdge = null;

        if(currInstruction < maxInstructions)
        {
            currEdge = route.get(currInstruction);
            currInstruction++;
        }

        return currEdge;
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
        Edge edgeToAdd;

        if(endVertex != null)
        {
            currVertex = endVertex.getParent();
            prevVertex = endVertex;
            reversedRoute = new Route();

            while (currVertex != null) {
                edgeToAdd = new Edge(currVertex, prevVertex);
                reversedRoute.addInstructionToStart(edgeToAdd);

                prevVertex = currVertex;
                currVertex = currVertex.getParent();
            }
        }

        return reversedRoute;
    }

    private void addInstructionToStart(Edge edge)
    {
        if(edge != null)
        {
            route.add(0, edge);
            routeLength += edge.getWeight();
            maxInstructions++;
        }
    }

    private void addInstructionToEnd(Edge edge)
    {
        if(edge != null)
        {
            route.add(edge);
            routeLength += edge.getWeight();
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
                Edge firstInstruc = routeToCombine.route.get(0);
                Edge thisLastInstruc = route.get(maxInstructions - 1);

                //Connect the end destination of this, with the starting location of the second route
                Edge connection = new Edge(thisLastInstruc.getDestination(), firstInstruc.getSource());
                this.addInstructionToEnd(connection);
            }

            //Add any other instructions from the second route into this one
            for(Edge edge : routeToCombine.route)
            {
                this.addInstructionToEnd(edge);
            }
        }
    }
}
