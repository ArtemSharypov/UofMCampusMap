package com.artem.uofmcampusmap.buildings;

import com.artem.uofmcampusmap.Edge;
import com.artem.uofmcampusmap.Route;

/**
 * Created by Artem on 2017-06-21.
 */

public interface DisplayIndoorRoutes {

    void displayIndoorRoute(Edge pathToShow);
    void hideIndoorPath(Edge pathToHide);
    void showAllIndoorPaths(int instructionStart, Route route);
}
