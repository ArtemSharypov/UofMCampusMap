package com.artem.uofmcampusmap;

/**
 * Created by Artem on 2017-04-28.
 */

public interface PassRouteData {

    void passStartLocation(String source);
    String getStartLocation();
    void passDestinationLocation(String destination);
    String getDestinationLocation();

}