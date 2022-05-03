package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;

public final class RouteBean {
    RouteComputer path;
    public ObservableList<Waypoint> waypoints;
    public ReadOnlyObjectProperty<Route> route;
    public DoubleProperty highlightedPosition;
    public ReadOnlyObjectProperty<ElevationProfile> elevationProfile;

    public RouteBean(RouteComputer path){
        this.path = path;
    }

    public DoubleProperty highlightedPositionProperty(){
        return this.highlightedPosition;
    }

    public double highlightedPosition(){
        return highlightedPosition.doubleValue();
    }

    public void setHighlightedPosition(double newValue){
        highlightedPosition.setValue(newValue);
    }
}
