package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;

import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Regroupant les propriétés relatives aux points de passage et à l'itinéraire correspondant.
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */
public final class RouteBean {
    private final RouteComputer path;

    //todo certains doivent ils etre en public
    private final ObjectProperty<Route> route = new SimpleObjectProperty<>(null);
    private final ObjectProperty<ElevationProfile> elevationProfile = new SimpleObjectProperty<>();
    private final ObservableList<Waypoint> waypoints = FXCollections.observableArrayList(new ArrayList<>());
    private final DoubleProperty highlightedPosition = new SimpleDoubleProperty(Double.NaN);

    //cache-mémoire des routes
    //TODO faut il creer cela dans le constructeur?
    private Key key;
    private final LinkedHashMap<Key, Route> memoryRoute = new LinkedHashMap<>(INITIAL_CAPACITY);

    private final static int MAX_STEP_LENGTH = 5;
    private final static int MAX_CAPACITY = 100;
    private final static int INITIAL_CAPACITY = 20;

    /**
     * Constructeur public de la classe.
     *
     * @param path un calcul d'itinéraire utilisé pour déterminer le meilleur itinéraire reliant deux points de passage
     */
    public RouteBean(RouteComputer path) {
        this.path = path;

        waypoints.addListener((Observable event) -> {
            List<Route> listSingleRoute = new ArrayList<>();

            //todo faut il creer une constante ?
            if (waypoints.size() < 2) routeAndElevationProfileNull();
            else {
                for (int i = 0; i < waypoints.size() - 1; ++i) {
                    int firstWaypointNode = waypoints.get(i).nodeId();
                    int secondWaypointNode = waypoints.get(i + 1).nodeId();

                    //TODO le if est utile maintenant qu'on a la méthode dans routebean?
                    if (firstWaypointNode == secondWaypointNode) continue;
                    key = new Key(firstWaypointNode, secondWaypointNode);

                    Route routePath = memoryRoute.get(key);
                    if (memoryRoute.containsKey(key) && routePath != null) listSingleRoute.add(routePath);
                    else {
                        listSingleRoute.add(path.bestRouteBetween(key.NodeId1(), key.NodeId2()));

                        if (memoryRoute.size() > MAX_CAPACITY) memoryRoute.remove(memoryRoute.keySet().iterator().next());

                        memoryRoute.put(key, listSingleRoute.get(listSingleRoute.size() - 1));
                    }
                }

                if (listSingleRoute.contains(null)) routeAndElevationProfileNull();
                else {
                    route.set(new MultiRoute(listSingleRoute));
                    elevationProfile.setValue(ElevationProfileComputer.elevationProfile(route.get(), MAX_STEP_LENGTH));
                }
            }
        });
    }

    /**
     * Méthode qui set la route et le profil d'élévation à null.
     */
    private void routeAndElevationProfileNull() {
        route.set(null);
        elevationProfile.set(null);
    }

    /**
     * Enregistrement permettant de créer une clé. Clé ensuite utilisée pour la LinkedHashMap memoryRoute
     */
    private record Key(Integer NodeId1, Integer NodeId2) {
    }

    /**
     * Retourne la propriété elle-même, de type DoubleProperty.
     *
     * @return la propriété elle-même, de type DoubleProperty
     */
    public DoubleProperty highlightedPositionProperty() {
        return this.highlightedPosition;
    }

    /**
     * Retourne le contenu de la propriété, de type double.
     *
     * @return le contenu de la propriété, de type double
     */
    public double highlightedPosition() {
        return highlightedPosition.doubleValue();
    }

    /**
     * Stocke la valeur donnée en argument dans la propriété.
     *
     * @param newValue la potentielle nouvelle valeur de la propriété
     */
    public void setHighlightedPosition(double newValue) {
        if (newValue < 0) newValue = Double.NaN;
        highlightedPosition.setValue(newValue);
    }

    /**
     * Retourne la propriété route sous la forme d'une valeur de type ObjectProperty<Route>.
     *
     * @return la propriété route sous la forme d'une valeur de type ObjectProperty<Route>
     */
    public ReadOnlyObjectProperty<Route> getRouteProperty() {
        return route;
    }

    /**
     * Retourne la propriété elevationProfile sous la forme d'une valeur de type ObjectProperty<ElevationProfile>.
     *
     * @return la propriété elevationProfile sous la forme d'une valeur de type ObjectProperty<ElevationProfile>
     */
    public ReadOnlyObjectProperty<ElevationProfile> getElevationProfileProperty() {
        return elevationProfile;
    }

    /**
     * Retourne la liste de waypoint(s).
     *
     * @return la liste de waypoint(s)
     */
    public ObservableList<Waypoint> getWaypoint() {
        return waypoints;
    }

    /**
     * Stocke un waypoint dans la liste de waypoint(s).
     *
     * @param waypoint un waypoint qui va être rajouté à la liste observable de waypoint(s)
     */
    public void setWaypoint(Waypoint waypoint) {
        waypoints.add(waypoint);
    }

    /**
     * Retourne l'itinéraire.
     *
     * @return l'itinéraire
     */
    public RouteComputer getPath() {
        return path;
    }

    /**
     * Retourne l'index du segment contenant la position, en ignorant les segments vides.
     * @param position la position du segment
     *
     * @return l'index du segment contenant la position, en ignorant les segments vides.
     */
    public int indexOfNonEmptySegmentAt(double position) {
        int index = route.get().indexOfSegmentAt(position);
        for (int i = 0; i <= index; i += 1) {
            int n1 = waypoints.get(i).nodeId();
            int n2 = waypoints.get(i + 1).nodeId();
            if (n1 == n2) index += 1;
        }
        return index;
    }
}
