# University of Manitoba Campus Map

A map designed for the University of Manitoba campus, it contains building layouts, as well as provide turn by turn navigations to get from different buildings on campus, as well as navigate find paths to specific rooms within buildings. It also contains pathings to different parking lots, as well as bus stops.

# Features
-Indoor & Outdoor Navigation 

-Support to show floor layouts of each building with room labels

-Detailed instructions for each step, alongside an estimated distance and time for the route.

# How it works
Routing is done by an A Star algorithm. It picks the best current position on the map, then compares the next potential steps that take the least amount of time, and repeat it until the destination is reached.

For indoor navigation, each level of the building is treated as a seperate entity, where each level gets its own individual route, which is then combined with any other necessary parts.

# Google Play Store
Published under https://play.google.com/store/apps/details?id=com.artem.uofmcampusmap&hl=en
