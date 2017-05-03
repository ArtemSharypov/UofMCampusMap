package com.artem.uofmcampusmap;

import java.util.ArrayList;

/**
 * Created by Artem on 2017-04-28.
 */

public class Route {

    //todo: switch vertexes instead for edges? easier to draw and show the distance of them
    private ArrayList<Vertex> route;
    private int currInstruction;
    private int maxInstructions;
    private long routeLength;

    Route()
    {
        route = new ArrayList<>();
        currInstruction = 0;
        maxInstructions = 0;
        routeLength = 0;
    }

    public Vertex topVertex()
    {
        if(route.size() > 0)
        {
            maxInstructions = route.size();
            currInstruction = 1;
        }

        return route.get(0);
    }

    public Vertex nextVertex()
    {
        Vertex vertex = null;

        if(currInstruction < maxInstructions)
        {
            vertex = route.get(currInstruction);
            currInstruction++;
        }

        return vertex;
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
        Route reversedRoute = new Route();
        Vertex currVertex = endVertex;

        while(currVertex != null)
        {
            reversedRoute.addVertexToStart(currVertex);
            currVertex = currVertex.getParent();
        }

        return reversedRoute;
    }

    public void addVertexToStart(Vertex vertex)
    {
        if(vertex != null)
        {
            route.add(0, vertex);
        }
    }

    public long getRouteLength() {
        if(route.size() > 2)
        {
            //full cost will be the vertex before the destination
            routeLength = route.get(route.size() - 2).getF();
        }

        return routeLength;
    }
}
