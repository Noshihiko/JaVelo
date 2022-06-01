package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.function.Consumer;

/**
 * Gère l'affichage de la carte annotée où sont superposés l'itinéraire et les points de passage
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */

public final class AnnotatedMapManager {
    private final Graph roadNetworkGraph;
    private final TileManager tileManager;
    private final Consumer<String> error;
    private final RouteBean routeBean;
    private final RouteManager routeManager;
    private final static int ZOOM_AT_START = 12;
    private final static int X_AT_START = 543200;
    private final static int Y_AT_START = 370650;
    private final static int MAXIMUM_DISTANCE_MOUSE_FROM_ITINERARY = 15;
    private final static String MAP_STYLE_SHEET = "map.css";

    private final ObjectProperty<MapViewParameters> mapViewParameters;
    private final BaseMapManager baseMapManager;
    private final WaypointsManager waypointsManager;
    private final StackPane pane;
    private final DoubleProperty mousePositionOnRouteProperty;
    private final ObjectProperty<Point2D> mousePositionProperty;

    /**
     * Constructeur publique de la classe.
     *
     * @param roadNetworkGraph le graph du réseau routier
     * @param tileManager le gestionnaire de tuiles OpenStreetMap
     * @param routeBean le bean de l'itinéraire
     * @param error un consommateur d'erreurs permettant de signaler une erreur
     */

    public AnnotatedMapManager(Graph roadNetworkGraph, TileManager tileManager, RouteBean routeBean, Consumer<String> error) {
        this.roadNetworkGraph = roadNetworkGraph;
        this.tileManager = tileManager;
        this.error = error;
        this.routeBean = routeBean;

        mapViewParameters = new SimpleObjectProperty<>(new MapViewParameters(ZOOM_AT_START, X_AT_START, Y_AT_START));

        //creation d'un Waypoint manager
        waypointsManager = new WaypointsManager(roadNetworkGraph, mapViewParameters, routeBean.getWaypoint() ,error);

        // Creation d'un Route manager
        routeManager = new RouteManager(routeBean, mapViewParameters);

        //creation de base map manager
        baseMapManager = new BaseMapManager(tileManager, waypointsManager , mapViewParameters);

        //initialisation du panneau
        pane = new StackPane(baseMapManager.pane(),
                routeManager.pane(),
                waypointsManager.pane());
        pane.getStylesheets().add(MAP_STYLE_SHEET);

        mousePositionOnRouteProperty = new SimpleDoubleProperty();
        mousePositionProperty = new SimpleObjectProperty<>();

        //Listeners
        pane.setOnMouseMoved(event -> mousePositionProperty.setValue(new Point2D(event.getX(), event.getY())));

        pane.setOnMouseExited(event -> mousePositionProperty.setValue(null));

        mousePositionOnRouteProperty.bind(Bindings.createDoubleBinding(this::setMousePositionOnRouteProperty,
                mapViewParameters, mousePositionProperty, routeBean.getRouteProperty()));
    }

    /**
     * Methode retournant le panneau contenant les points de passage.
     *
     * @return le panneau contenant les points de passage
     */

    public Pane pane() {
        return pane;
    }

    /**
     * Methode retournant la propriété en lecture seule contenant la position de la sourie le long de l'itineraire.
     *
     * @return la propriété en lecture seule contenant la position de la sourie le long de l'itineraire
     */

    public ReadOnlyDoubleProperty mousePositionOnRouteProperty() {
        return mousePositionOnRouteProperty;
    }

    /**
     * Met à jour la position de la sourie le long de l'itineraire
     *
     * @return la valeure correspondant au point le plus proche de la sourie sur l'itineraire
     *          lorsque la distance est inferieure à 15 pixels
     */

    private Double setMousePositionOnRouteProperty() {
        if (mousePositionProperty.get() == null || routeBean.getRouteProperty().get() == null)
            return Double.NaN;

        double xOfMouse = mousePositionProperty.getValue().getX();
        double yOfMouse = mousePositionProperty.getValue().getY();

        MapViewParameters mapParameters = mapViewParameters.get();

        PointWebMercator mousePosition = mapParameters.pointAt(xOfMouse, yOfMouse);

        if (mousePosition.toPointCh() == null) return Double.NaN;

        RoutePoint closestPointRoute = routeBean.getRouteProperty().get().pointClosestTo(mousePosition.toPointCh());

        PointWebMercator closestPointWM = PointWebMercator.ofPointCh(closestPointRoute.point());

        double xPointRoute = mapParameters.viewX(closestPointWM);
        double yPointRoute = mapParameters.viewY(closestPointWM);

        double uX = xPointRoute - xOfMouse;
        double uY = yPointRoute - yOfMouse;

        if (Math2.norm(uX , uY) <= MAXIMUM_DISTANCE_MOUSE_FROM_ITINERARY) return closestPointRoute.position();
        else return Double.NaN;
    }
}
