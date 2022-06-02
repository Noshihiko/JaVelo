package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;

import ch.epfl.javelo.routing.Route;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.List;

/**
 * Gère l'affichage de la route.
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */
public final class RouteManager {
    private final RouteBean routeBean;
    private final ReadOnlyObjectProperty<MapViewParameters> mapViewParameters;

    private final static double RADIUS_OF_DISK = 5;
    private final static String POLYLINE_ID_ROUTE = "route";
    private final static String CIRCLE_ID_HIGHLIGHT = "highlight";

    private final Polyline itinerary = new Polyline();
    private final Circle circle = new Circle(RADIUS_OF_DISK);
    private final Pane pane = new Pane(itinerary, circle);

    /**
     * Constructeur public de la classe.
     *
     * @param routeBean bean de la route
     * @param mapViewParameters paramètres du fond de carte
     */
    public RouteManager(RouteBean routeBean, ReadOnlyObjectProperty<MapViewParameters> mapViewParameters) {
        this.routeBean = routeBean;
        this.mapViewParameters = mapViewParameters;

        itinerary.setId(POLYLINE_ID_ROUTE);
        circle.setId(CIRCLE_ID_HIGHLIGHT);

        pane.setPickOnBounds(false);
        setDiskAndItineraryVisible();

        mapViewParameters.addListener((object, oldMapParameters, newMapParameters) -> {
            if ((oldMapParameters.zoom() == newMapParameters.zoom() && routeBean.getRouteProperty().get() != null))
                updateItineraryLayout();
            else recreateItinerary();
        });

        routeBean.getWaypoint().addListener((Observable event) -> {
            if (routeBean.getRouteProperty().get() != null)
                recreateItinerary();
            else setDiskAndItineraryVisible();
        });

        routeBean.highlightedPositionProperty().addListener((object, old, now) -> {
            updateDiskLayout();
            setDiskAndItineraryVisible();
        });
       
        routeBean.getRouteProperty().addListener(event -> {
            if (routeBean.getRouteProperty().get() != null)
                recreateItinerary();
        });

        
        circle.setOnMouseClicked(event -> {
            Route routeProperty = routeBean.getRouteProperty().get();
            double highlightedPosition = routeBean.highlightedPosition();
            ObservableList<Waypoint> waypoints = routeBean.getWaypoint();

            int nodeId = routeProperty.nodeClosestTo(highlightedPosition);
            Point2D p = circle.localToParent(event.getX(), event.getY());

            PointCh newPoint = mapViewParameters.get().pointAt(p.getX(), p.getY()).toPointCh();

            int index = routeBean.indexOfNonEmptySegmentAt(highlightedPosition) + 1;

            waypoints.add(index, new Waypoint(newPoint, nodeId));
        });
    }

    /**
     * Permet de réactualiser l'affichage de l'itinéraire.
     */
    private void updateItineraryLayout() {
        itinerary.setLayoutY(- mapViewParameters.get().y());
        itinerary.setLayoutX(- mapViewParameters.get().x());

        updateDiskLayout();
    }

    /**
     * Permet de rendre visible ou invisible le disque et l'itinéraire.
     */

    private void setDiskAndItineraryVisible() {
        if (routeBean.getRouteProperty().get()!= null) {
            itinerary.setVisible(true);
            circle.setVisible(!Double.isNaN(routeBean.highlightedPosition()));
        }
        else {
            circle.setVisible(false);
            itinerary.setVisible(false);
        }
    }

    /**
     * Permet de réactualiser l'affichage du disque.
     */
    private void updateDiskLayout() {
        double highlightedPosition = routeBean.highlightedPosition();
        Route routeProperty = routeBean.getRouteProperty().get();

        if (!Double.isNaN(highlightedPosition) && routeProperty != null) {
        PointCh pointCh = routeProperty.pointAt(highlightedPosition);
        PointWebMercator pointWB = PointWebMercator.ofPointCh(pointCh);

        circle.setLayoutX(mapViewParameters.get().viewX(pointWB));
        circle.setLayoutY(mapViewParameters.get().viewY(pointWB));
        }
    }

    /**
     * Permet de recréer l'itinéraire.
     */
    private void recreateItinerary() {
        Route routeProperty = routeBean.getRouteProperty().get();

        if (routeProperty != null) {
            Double[] listOfCoordinates = conversionCord(routeProperty.points());

            itinerary.getPoints().setAll(listOfCoordinates);
            updateItineraryLayout();
        }
        setDiskAndItineraryVisible();
    }

    /**
     * Retourne un tableau listant les coordonnées des points constituants les arêtes constituant la route.
     *
     * @param listPoints liste des points constituants la route
     *
     * @return un tableau listant les coordonnées des points constituants les arêtes constituant la route
     */

    private Double [] conversionCord (List<PointCh> listPoints) {
        int count = 0;
        PointWebMercator point;
        int zoom = mapViewParameters.get().zoom();
        int arraySize = listPoints.size();
        Double [] newListCoordinates = new Double[arraySize*2];

        for (PointCh pointCh : listPoints) {
            point = PointWebMercator.ofPointCh(pointCh);

            newListCoordinates[count] = point.xAtZoomLevel(zoom);
            ++count;

            newListCoordinates[count] = point.yAtZoomLevel(zoom);
            ++count;
        }
        return newListCoordinates;
    }

    /**
     * Retourne le panneau JavaFX affichant le fond de carte.
     *
     * @return le panneau JavaFX affichant le fond de carte
     */
    public Pane pane(){
        return pane;
    }
}
