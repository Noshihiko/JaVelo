package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.InvalidationListener;

import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Pair;


import java.util.ArrayList;


import java.util.LinkedHashMap;
import java.util.List;

//1349, 1391, 1430
public final class RouteBean {
    private RouteComputer path;
    //propriétés publiques en lecture seule :
    private ObjectProperty<Route> route;
    private ObjectProperty<ElevationProfile> elevationProfile;

    //seules propriétés modifiables depuis l'extérieur :
    private ObservableList<Waypoint> waypoints = FXCollections.observableArrayList();
    private DoubleProperty highlightedPosition = new SimpleDoubleProperty();

    //cache-mémoire des routes
    //TODO : demander pour le initialCapacity
    private Key key;
    private LinkedHashMap<Key, Route> memoryRoute = new LinkedHashMap<>(20);

    private int MAX_STEP_LENGTH = 5;

    public RouteBean(RouteComputer path){
        this.path = path;
        this.route = new SimpleObjectProperty<>(null);
        this.elevationProfile = new SimpleObjectProperty<>();
        this.waypoints = FXCollections.observableArrayList(new ArrayList<>());
        this.highlightedPosition = new SimpleDoubleProperty(Double.NaN);

        waypoints.addListener((Observable event)-> {
            List<Route> listSingleRoute = new ArrayList<>();

            //*********************** TEST ************************
            for (int i=0; i<waypoints.size(); ++i){
                System.out.println(waypoints.get(i).nodeId());
            }
            //***************************************************

            if (waypoints.size() < 2) {
                route.set(null);
                elevationProfile.set(null);
                System.out.println("array size " +waypoints.size());
            }
            else {

                for (int i = 0; i < waypoints.size() - 1; ++i) {
                    key = new Key(waypoints.get(i).nodeId(), waypoints.get(i+1).nodeId());

                    if (memoryRoute.containsKey(key) && memoryRoute.get(key)!=null) {
                        listSingleRoute.add(memoryRoute.get(key));

                    } else {
                        listSingleRoute.add(path.bestRouteBetween(key.NodeId1(), key.NodeId2()));

                        //System.out.println(" avant dernier waypoint de la route : " +waypoints.get(i).nodeId());
                        //System.out.println(" dernier point de la route : " +waypoints.get(i+1).nodeId());

                        if (memoryRoute.size() > 100) {
                            memoryRoute.remove(memoryRoute.keySet().iterator().next());
                        }

                        memoryRoute.put(key, listSingleRoute.get(listSingleRoute.size() - 1));
                    }

                    route.set(new MultiRoute(listSingleRoute));

                    elevationProfile.setValue(ElevationProfileComputer.elevationProfile(route.get(), MAX_STEP_LENGTH));
                }
            }
        });

    }

    private record Key (Integer NodeId1, Integer NodeId2) {}

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

    public ReadOnlyObjectProperty<Route> getRoute(){
        return route;
    }

    public ReadOnlyObjectProperty<ElevationProfile> getElevationProfileProperty(){
        return elevationProfile;
    }

    public ObservableList<Waypoint> getWaypoint(){
        return waypoints;
    }

    public void setWaypoint(Waypoint waypoint){
        waypoints.add(waypoint);
    }

    public RouteComputer getPath(){
        return path;
    }

    }
