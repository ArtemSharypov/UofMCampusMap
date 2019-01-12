package com.artem.uofmcampusmap;


public class Instruction {
    private Vertex source;
    private Vertex destination;
    private int distance;
    private int direction;

    public Instruction(Vertex source, Vertex destination)
    {
        this.source = source;
        this.destination = destination;
        calculateDistance();
        findDirection();
    }

    //Used for special case of route optimization, since the vertex's dont directly connect to eachother
    public Instruction(Vertex source, Vertex destination, int direction)
    {
        this.source = source;
        this.destination = destination;
        this.direction = direction;
        calculateDistance();
    }

    //Checks if source and destination are both indoors
    public boolean isIndoorInstruction()
    {
        return source instanceof IndoorVertex && destination instanceof IndoorVertex;
    }

    //Checks if source and destination are both outdoors
    public boolean isOutdoorInstruction()
    {
        return source instanceof OutdoorVertex && destination instanceof OutdoorVertex;
    }

    //Checks if source is indoors, and destination is outdoor (aka exit the building)
    public boolean isExitBuildingInstruction()
    {
        return source instanceof IndoorVertex && destination instanceof OutdoorVertex;
    }

    //checks if it's a enter building instruction. Indoor -> Outdoor vertex connection
    public boolean isEnterBuildingInstruction()
    {
        return source instanceof OutdoorVertex && destination instanceof IndoorVertex;
    }

    //Checks if source and destination are on different floors, hence being stairs/elevator
    public boolean isStairsInstruction()
    {
        boolean isStairsInstruc = false;

        if(isIndoorInstruction())
        {
            if(((IndoorVertex) source).getFloor() != ((IndoorVertex) destination).getFloor())
            {
                isStairsInstruc = true;
            }
        }

        return isStairsInstruc;
    }

    //Checks if source and destination are both indoors, as well as if they are different buildings
    //being different buildings means that it is a exit current / enter another
    public boolean isChangeBuildingsInstruction()
    {
        boolean isChangeBuildingsInstruc = false;

        if(isIndoorInstruction())
        {
            if(!((IndoorVertex) source).getBuilding().equals(((IndoorVertex) destination).getBuilding()))
            {
                isChangeBuildingsInstruc = true;
            }
        }

        return isChangeBuildingsInstruc;
    }

    //Only used for general directions, ie East/West or North/South
    private void findDirection()
    {
        if(source != null && destination != null)
        {
            //Assume we're just going in the direction from source -> destination always
            direction = source.directionToVertexIs(destination);
        }
    }

    private void calculateDistance()
    {
        this.distance = source.getDistanceFrom(destination);
    }

    //Used to guarantee that the distance will be converted from feet to metres in case of IndoorVertex's
    public int getDistanceInMetres()
    {
        int metreDistance = distance;
        final double METERS_PER_FEET = 0.3048;

        if(source instanceof IndoorVertex && destination instanceof IndoorVertex)
        {
            metreDistance = (int) (distance * METERS_PER_FEET);
        }

        return metreDistance;
    }

    public Vertex getSource() {
        return source;
    }

    public Vertex getDestination() {
        return destination;
    }

    public int getCurrDirection()
    {
        return direction;
    }

    //Creates String directions for this instruction, depending on the conditions that they satisfy
    public String getTextDirections()
    {
        String toString = "Go straight for " + distance;

        if(source instanceof IndoorVertex)
        {
            //Indoor -> Outdoor means exiting a building
            if(destination instanceof OutdoorVertex)
            {
                toString = "Exit the building";
            }
            else
            {
                //Indoor -> Indoor
                IndoorVertex sourceIndoor = (IndoorVertex) source;
                IndoorVertex destIndoor = (IndoorVertex) destination;

                //Being on the same floor means that the distance will be in feet
                if(sourceIndoor.getFloor() == destIndoor.getFloor())
                {
                    toString += " feet";
                }
                else
                {
                    //Check if there are stairs involved (if floors change)
                    if(sourceIndoor.getFloor() < destIndoor.getFloor())
                    {
                        toString = "Go up ";
                    }
                    else
                    {
                        toString = "Go down ";
                    }

                    toString += Math.abs(((IndoorVertex) source).getFloor() - ((IndoorVertex) destination).getFloor());
                    toString += " floor(s)";
                }
            }
        }
        else
        {
            //Outdoor -> Indoor means that a building is to be entered
            if(destination instanceof IndoorVertex)
            {
                toString = "Enter " + ((IndoorVertex) destination).getBuilding();
            }
            else
            {
                //Else its just a distance in metres
                toString += "m";
            }
        }

        return toString;
    }

}
