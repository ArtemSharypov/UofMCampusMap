package com.artem.uofmcampusmap;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Artem on 2017-04-28.
 */

public class MapGraph {
    private HashMap<String, ArrayList<Vertex>> buildingsWithEntrances;

    public MapGraph()
    {
        buildingsWithEntrances = new HashMap<>();
        populateGraph();
    }

    public void addVertex(String keyName, ArrayList<Vertex> entranceVertices)
    {
        if(buildingsWithEntrances.containsKey(keyName))
        {
            entranceVertices.addAll(buildingsWithEntrances.get(keyName));
        }

        buildingsWithEntrances.put(keyName, entranceVertices);
    }

    private void populateGraph()
    {
        //todo: fill the graph with the necessary entrances
    }

    public Route findRoute(String startLocation, String endLocation)
    {
        Route route = null;
        Route prevRoute;
        ArrayList<Vertex> entrances;
        ArrayList<Vertex> exits;

        if(buildingsWithEntrances.containsKey(startLocation) && buildingsWithEntrances.containsKey(endLocation))
        {
            entrances = buildingsWithEntrances.get(startLocation);
            exits = buildingsWithEntrances.get(endLocation);

            //Find the shortest path from all entrances of a building to another buildings exits
            for(Vertex currEntrance: entrances)
            {
                for(Vertex currExit: exits)
                {
                    prevRoute = pathFinder(currEntrance, currExit);

                    if(route == null)
                    {
                        route = prevRoute;
                    }
                    else if(prevRoute != null && prevRoute.currRouteQuicker(route))
                    {
                        route = prevRoute;
                    }
                }
            }
        }

        return route;
    }

    //todo: might be better to split into its own class
    //Find a path from start to end if there exists one
    private Route pathFinder(Vertex start, Vertex end)
    {
        Route route = null;
        PathfinderVertex startPF;
        PathfinderVertex endPF;
        ArrayList<PathfinderVertex> closedList; //vertices evaluated
        ArrayList<PathfinderVertex> openList; //vertices not yet evaluated
        PathfinderVertex lowestCostVertex;
        ArrayList<PathfinderVertex> successors; //vertices that are connected to lowest cost vertex
        PathfinderVertex vertexToCompare;

        if(start != null && end != null)
        {
            startPF = new PathfinderVertex(start);
            startPF.setG(0);
            endPF = new PathfinderVertex(end);
            closedList = new ArrayList<>();
            openList = new ArrayList<>();

            openList.add(startPF);

            while(!openList.isEmpty())
            {
                lowestCostVertex = findLowestCostVertex(openList);

                openList.remove(lowestCostVertex);
                successors = generateSuccessors(lowestCostVertex);

                //find the connections the next vertices have
                for(PathfinderVertex currVertex: successors)
                {
                    if(currVertex.equals(endPF))
                    {
                        //Save the route and clear everything
                        route = reverseRoute(currVertex);
                        openList.clear();
                        break;
                    }

                    //g = parent g + distance between parent and this
                    currVertex.setG(lowestCostVertex.getG() + lowestCostVertex.findConnection(currVertex).getWeight());
                    currVertex.calculateH(endPF);
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

                        //Remove the vertex in the closed list if its F is bigger, and add current to open list
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

    private Route reverseRoute(PathfinderVertex endVertex)
    {
        Route reversedRoute = null;

        //todo:go from endVertex to the start via parents

        return reversedRoute;
    }

    private ArrayList<PathfinderVertex> generateSuccessors(PathfinderVertex parent)
    {
        ArrayList<Edge> connections;
        ArrayList<PathfinderVertex> successors = new ArrayList<>();
        PathfinderVertex convertedVertex;

        if(parent != null)
        {
            connections = parent.getConnections();

            for(Edge edge: connections)
            {
                convertedVertex = new PathfinderVertex(edge.getDestination());
                convertedVertex.setParent(parent);
                successors.add(convertedVertex);
            }
        }

        return successors;
    }

    private PathfinderVertex findLowestCostVertex(ArrayList<PathfinderVertex> setToSearch)
    {
        PathfinderVertex currVertex = null;
        double currCost = 0;

        for(PathfinderVertex vertex: setToSearch)
        {
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
