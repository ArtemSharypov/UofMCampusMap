package com.artem.uofmcampusmap;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;

/**
 * Created by Artem on 2017-05-01.
 */

public class RouteFinder {

    //Find a path from start to end if there exists one using an A* algorithm
    public Route findRoute(Vertex start, Vertex end)
    {
        Route route = null;
        Vertex source;
        Vertex destination;
        Vertex currVertex;
        HashSet<Vertex> closedList; //vertices evaluated
        PriorityQueue<Vertex> openList; //vertices not yet evaluated
        Comparator<Vertex> comparator = new Comparator<Vertex>() {
            @Override
            public int compare(Vertex vertex1, Vertex vertex2) {
                if(vertex1.getF() < vertex2.getF())
                {
                    return -1;
                }
                else if(vertex1.getF() > vertex2.getF())
                {
                    return 1;
                }
                else
                {
                    return 0;
                }
            }
        };

        if(start != null && end != null)
        {
            if(start instanceof IndoorVertex)
                source = new IndoorVertex((IndoorVertex) start);
            else
                source = new OutdoorVertex((OutdoorVertex) start);

            if(end instanceof IndoorVertex)
                destination = new IndoorVertex((IndoorVertex) end);
            else
                destination = new OutdoorVertex((OutdoorVertex) end);

            source.setG(0);
            closedList = new HashSet<>();
            openList = new PriorityQueue<>(5, comparator);

            openList.add(source);

            //openList being empty means a route has been found, or that there is no possible paths between start/destination
            while(!openList.isEmpty())
            {
                currVertex = openList.poll();

                if(currVertex.equals(destination))
                {
                    //Save the route and clear everything
                    route = Route.reverseRoute(currVertex);
                    openList.clear();
                    break;
                }

                for(Vertex currSuccessor: currVertex.getConnections())
                {
                    if(checkIfValidSuccessor(currSuccessor, currVertex))
                    {
                         /* For a successor to be added into the open set, it must have one of three conditions:
                             -Not be in the closed set
                             -Be in the open set, and now have a lower G cost compared to before
                             -Not be in the open or closed set.
                         */
                        if (openList.contains(currSuccessor))
                        {
                            double tempGCost = currVertex.getG() + currVertex.getDistanceFrom(currSuccessor);

                            if (currSuccessor.getG() > tempGCost)
                            {
                                openList.remove(currSuccessor);

                                currSuccessor.setParent(currVertex);
                                currSuccessor.setG(tempGCost);
                                currSuccessor.calculateF();

                                openList.add(currSuccessor);
                            }
                        }
                        else
                        {
                            if (!closedList.contains(currSuccessor))
                            {
                                currSuccessor.setParent(currVertex);
                                currSuccessor.setG(currVertex.getG() + currVertex.getDistanceFrom(currSuccessor));
                                currSuccessor.calculateH(destination);
                                currSuccessor.calculateF();

                                openList.add(currSuccessor);
                            }
                        }
                    }
                }

                closedList.add(currVertex);
            }
        }

        return route;
    }

    //Used to check if a parents successor is valid to use in the A* algorithm
    private boolean checkIfValidSuccessor(Vertex successor, Vertex parent)
    {
        boolean valid = false;

        //For a successor to be valid, it has to be of the same Vertex type, ie both Indoor or Outdoor
        //As well as if it is an IndoorVertex, they must be on the same floor.
        if(successor instanceof IndoorVertex && parent instanceof IndoorVertex)
        {
            if(((IndoorVertex) successor).getFloor() == ((IndoorVertex) parent).getFloor())
            {
                valid = true;
            }
        }
        else if(successor instanceof OutdoorVertex && parent instanceof OutdoorVertex)
        {
            valid = true;
        }

        return valid;
    }
}
