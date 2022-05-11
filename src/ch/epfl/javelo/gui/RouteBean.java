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

//1349, 1391, 1430
public final class RouteBean {
    //TODO : pv ou public ?
    private RouteComputer path;

    //propriétés publiques en lecture seule :
    private ObjectProperty<Route> route = new SimpleObjectProperty<>();
    public ObjectProperty<ElevationProfile> elevationProfile = new SimpleObjectProperty<>();

    //seules propriétés modifiables depuis l'extérieur :
    //private ObservableList<Waypoint> waypoints = FXCollections.observableArrayList();
    public ObservableList<Waypoint> waypoints = null;
    private DoubleProperty highlightedPosition;

    //cache-mémoire des routes
    //TODO : demander pour le initialCapacity
    private LinkedHashMap<Pair<Integer, Integer>, Route> memoryRoute = new LinkedHashMap<>(20);
    private int MAX_STEP_LENGTH = 5;

    public RouteBean(RouteComputer path){
        this.path = path;

        waypoints.addListener((ListChangeListener<? super Waypoint>) observable -> {
            //******** creer une methode a part **************************************
            List<Route> r = null;
            Route a;
            Pair<Integer,Integer> k;

            for (int i = 0; i < waypoints.size() - 1; ++i) {
                k = new Pair<>(i, i+1);

                if (memoryRoute.containsKey(k)) {
                    r.add(memoryRoute.get(k));
                } else {
                    r.add(path.bestRouteBetween(waypoints.get(i).nodeId(), waypoints.get(i+1).nodeId()));
                    if (memoryRoute.size() > 100) {
                        memoryRoute.remove(memoryRoute.keySet().iterator().next());
                    }
                    memoryRoute.put(k, r.get(r.size() - 1));
                }
            }

            if (waypoints.size() < 2){
                route.set(null);
                elevationProfile.set(null);
            } else {
                elevationProfile.setValue(ElevationProfileComputer.elevationProfile(route.get(), MAX_STEP_LENGTH));
                route.set(new MultiRoute(r));
            }
        });
    }

    public DoubleProperty highlightedPositionProperty(){
        return this.highlightedPosition;
    }

    public double highlightedPosition(){
        return highlightedPosition.doubleValue();
    }

    public void setHighlightedPosition(double newValue){
        if (newValue < 0) newValue = Double.NaN;
        highlightedPosition.setValue(newValue);
    }

    public ReadOnlyObjectProperty<Route> getRoute(ObjectProperty<Route> route){
        return this.route = route;
    }

    public ReadOnlyObjectProperty<ElevationProfile> getElevationProfileProperty(ObjectProperty<ElevationProfile> elevationProfile){
        return this.elevationProfile = elevationProfile;
    }
    // Vu que c'est deja en public je pense pas ça serve
    public ObservableList<Waypoint> getWaypoint(){
        return waypoints;
    }

    public RouteComputer getPath(){
        return path;
    }

    //TODO
    public void indexOfSegmentAt(Route route){
    }
}
