package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.InvalidationListener;

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
    private LinkedHashMap<Pair<Integer, Integer>, Route> memoryRoute = new LinkedHashMap<>(20);

    private int MAX_STEP_LENGTH = 5;

    public RouteBean(RouteComputer path){
        this.path = path;
        this.route = new SimpleObjectProperty<>(null);
        this.elevationProfile = new SimpleObjectProperty<>();
        this.waypoints = FXCollections.observableArrayList(new ArrayList<>());
        this.highlightedPosition = new SimpleDoubleProperty(Double.NaN);

        /*waypoints.addListener((InvalidationListener) event-> {
            List<Route> r = new ArrayList<>();
            //List<Route> r = new ArrayList<>(null);
            //Route a;
            Pair<Integer,Integer> k;

            //crée une SingleRoute pour chaque paire de points si elle n'est pas déjà dans le cache mémoire et la rajoute
            //à une liste de routes
            if (waypoints.size() < 2){

                route.set(null);

                elevationProfile.set(null);
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

            //conditions permettant de recalculer la route et le profil d'élévation


            } else if (route.get()!=null){

                elevationProfile.setValue(ElevationProfileComputer.elevationProfile(route.get(), MAX_STEP_LENGTH));

                route.set(new MultiRoute(r));
            }
        });*/

        waypoints.addListener((InvalidationListener) event-> {
            List<Route> r = new ArrayList<>();
            //List<Route> r = new ArrayList<>(null);
            //Route a;
            Pair<Integer,Integer> k;
            System.out.println("size of waypoint dans routebean : " +waypoints.size());

            //crée une SingleRoute pour chaque paire de points si elle n'est pas déjà dans le cache mémoire et la rajoute
            //à une liste de routes
            if (waypoints.size() < 2) {
                route.set(null);
                elevationProfile.set(null);
            }
            else {

                for (int i = 0; i < waypoints.size() - 1; ++i) {
                    k = new Pair<>(i, i+1);

                    if (memoryRoute.containsKey(k)) {
                        r.add(memoryRoute.get(k));
                    } else {
                        //si le deuxieme est nul ca fonctionne quand meme ?
                        r.add(path.bestRouteBetween(waypoints.get(i).nodeId(), waypoints.get(i+1).nodeId()));
                        if (memoryRoute.size() > 100) {
                            memoryRoute.remove(memoryRoute.keySet().iterator().next());
                        }
                        memoryRoute.put(k, r.get(r.size() - 1));

                        //elevationProfile.setValue(ElevationProfileComputer.elevationProfile(r.get(i), MAX_STEP_LENGTH));
                        //route.set(new MultiRoute(r));
                    }
                    route.set(new MultiRoute(r));
                    elevationProfile.setValue(ElevationProfileComputer.elevationProfile(route.get(), MAX_STEP_LENGTH));

                }


                //conditions permettant de recalculer la route et le profil d'élévation


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
