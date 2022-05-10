package ch.epfl.javelo.gui;


import ch.epfl.javelo.routing.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.LinkedHashMap;
import java.util.List;

public final class RouteBean {
    RouteComputer path;

    private ObjectProperty<Route> route = new SimpleObjectProperty<>();
    private ObjectProperty<ElevationProfile> elevationProfile = new SimpleObjectProperty<>();
    //private ObservableList<Waypoint> waypoints = FXCollections.observableArrayList();

    //seules propriétés modifiables depuis l'extérieur :
    private ObservableList<Waypoint> waypoints = null;
    private DoubleProperty highlightedPosition;

    private LinkedHashMap<Pair<Integer, Integer>, Route> memoryRoute = new LinkedHashMap<>(20);
    private int MAX_STEP_LENGTH = 5;

    public RouteBean(RouteComputer path){
        this.path = path;

        waypoints.addListener((ListChangeListener<? super Waypoint>) o -> {
            elevationProfile.setValue(ElevationProfileComputer.elevationProfile(route.get(), MAX_STEP_LENGTH ));

            /*
            if(//TODO getRoute()!= null
            ) {
            route.setValue(new MultiRoute());
            } else {
                route.set(null);
            }

             */
/*
        });

        checkArgument();
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

    public ReadOnlyObjectProperty<Route> getRoute(ObjectProperty<Route> route){
        return this.route = route;
    }

    public ReadOnlyObjectProperty<ElevationProfile> getElevationProfileProperty(ObjectProperty<ElevationProfile> elevationProfile){
        return this.elevationProfile = elevationProfile;
    }

    public ObservableList<Waypoint> getWaypoint(){
        return this.waypoints;
    }

    public void route(){

    }

    private void checkArgument() {
        if (waypoints.size() < 2 || route.get().edges()== null) {
            route = null;
            elevationProfile = null;
        }

        /*
        La propriété contenant la position mise en évidence
        contient soit un nombre valide donnant cette position
        —exprimée en mètres depuis le départ de l'itinéraire—
        soit NaN dans le cas où aucune position ne doit être
        mise en évidence.
        => c'est fait automatiquement non ?
         */
/*
    }
    //1349, 1353, 1391

}
*/