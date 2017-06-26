package com.artem.uofmcampusmap;

/**
 * Created by Artem on 2017-04-28.
 */

public interface PassRouteData {

    void passStartLocation(String source);
    String getStartLocation();
    void passStartRoom(String room);
    String getStartRoom();

    void passDestinationLocation(String destination);
    String getDestinationLocation();
    void passDestinationRoom(String room);
    String getDestinationRoom();

    void passRoute(Route route);
    Route getRoute();

    int getCurrInstructionPos();
    void setCurrInstructionPos(int currInstructionPos);

}
