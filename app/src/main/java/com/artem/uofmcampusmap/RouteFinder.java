package com.artem.uofmcampusmap;

import java.util.ArrayList;
import java.util.HashSet;

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
        Vertex lowestCostVertex;
        Vertex vertexToCompare;
        ArrayList<Vertex> closedList; //vertices evaluated
        ArrayList<Vertex> openList; //vertices not yet evaluated
        ArrayList<Vertex> successors; //vertices that are connected to lowest cost vertex

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
            closedList = new ArrayList<>();
            openList = new ArrayList<>();

            openList.add(source);

            //openList being empty means a route has been found, or that there is no possible paths between start/destination
            while(!openList.isEmpty())
            {
                lowestCostVertex = findLowestCostVertex(openList);

                openList.remove(lowestCostVertex);
                successors = generateSuccessors(lowestCostVertex, end);

                //find the connections the next vertices have
                for(Vertex currVertex: successors)
                {
                    if(currVertex.equals(destination))
                    {
                        //Save the route and clear everything
                        route = Route.reverseRoute(currVertex);
                        openList.clear();
                        break;
                    }

                    //g = parent g + distance between parent and this
                    currVertex.setG(lowestCostVertex.getG() + lowestCostVertex.getDistanceFrom(currVertex));
                    currVertex.calculateH(destination);
                    currVertex.calculateF();

                    if(openList.contains(currVertex))
                    {
                        vertexToCompare = openList.get(openList.indexOf(currVertex));

                        //Remove the vertex in the open list if its F is bigger than the current
                        if(vertexToCompare != null)
                        {
                            if(vertexToCompare.getF() > currVertex.getF())
                            {
                                openList.remove(vertexToCompare);
                                openList.add(currVertex);
                            }
                        }
                    }
                    else if(closedList.contains(currVertex))
                    {
                        vertexToCompare = closedList.get(closedList.indexOf(currVertex));

                        //Remove the vertex in the closed list if its F (total distance between it and destination) is bigger,
                        // and add current to open list
                        if(vertexToCompare != null)
                        {
                            if(vertexToCompare.getF() > currVertex.getF())
                            {
                                closedList.remove(vertexToCompare);
                                openList.add(currVertex);
                            }
                        }
                    }
                    else //new vertex, add it to open
                    {
                        openList.add(currVertex);
                    }

                }

                closedList.add(lowestCostVertex);
            }
        }

        return route;
    }

    //Adds all valid connections from the parent vertex into the List
    //If the destination is on a given floor, any successors must be on the same floor as it to be added
    private ArrayList<Vertex> generateSuccessors(Vertex parent, Vertex destination)
    {
        HashSet<Vertex> connections;
        ArrayList<Vertex> successors = new ArrayList<>();
        Vertex childVertex;

        boolean parentIsIndoor = false;
        IndoorVertex parentIndoor = null;
        boolean childIsIndoor = false;

        if(parent != null)
        {
            if(parent instanceof IndoorVertex){
                parentIsIndoor = true;
                parentIndoor = (IndoorVertex) parent;
            }

            connections = parent.getConnections();

            for(Vertex currConnection: connections)
            {
                if(currConnection instanceof IndoorVertex)
                {
                    childVertex = new IndoorVertex((IndoorVertex) currConnection);
                    childIsIndoor = true;
                }
                else
                    childVertex = new OutdoorVertex((OutdoorVertex) currConnection);

                //For a successor, if the parent is Indoor, then the child has to be an IndoorVertex as well as being on the same floor
                //as the parent
                //Or simply the child vertex being an Outdoor's one
                if(((childIsIndoor && parentIsIndoor)&& ((IndoorVertex)childVertex).getFloor() == parentIndoor.getFloor()) ||
                        childVertex instanceof OutdoorVertex)
                {
                    childVertex.setParent(parent);
                    childVertex.setG(parent.getG() + parent.getDistanceFrom(childVertex));
                    childVertex.calculateH(destination);
                    childVertex.calculateF();
                    successors.add(childVertex);
                }
            }
        }

        return successors;
    }

    //todo replace this with just a priorityqueue?
    //Finds the next vertex that has the smallest estimated total cost from it to the destination
    private Vertex findLowestCostVertex(ArrayList<Vertex> vertexList)
    {
        Vertex currVertex = null;
        double currCost = 0;

        for(Vertex vertex: vertexList)
        {
            //F is the estimated total cost from this vertex to the destination
            if(currVertex == null)
            {
                currVertex = vertex;
                currCost = vertex.getF();
            }
            else if(vertex.getF() < currCost)
            {
                currVertex = vertex;
                currCost = vertex.getF();
            }
        }

        return currVertex;
    }
}
