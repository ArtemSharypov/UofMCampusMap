# Turn by Turn Navigation System for the University of Manitoba Campus 

Currently can route from most buildings that are used by departments or for class / labs, aswell as most parking lots and bus stops.

# Features
-Indoor & Outdoor Navigation
-Support to show floor layouts of each building with room labels
-Map labels of each building
-Routing based on the current location to/from a location
-Detailed instructions alongside the estimated time it will take

# How it works
Routing is done by an A Star algorithm, which creates the fastest route in a matter of seconds. Specifically it goes through each point on the map, which is chosen by the lowest total distance from a starting location to a destination. 
It will also optimize routes (as in skip over points) when two points are in the same direction.

For indoor navigation, each level of the building is treated as a seperate entity, where the route is created then added together once the optimal path is found.
