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

    public double getRouteLength()
    {
        return routeLength;
    }
}
