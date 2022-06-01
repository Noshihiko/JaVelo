package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;

import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectProperty;
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

    private final Polyline itinerary;
    private final Circle circle;
    private final Pane pane;

    /**
     * Constructeur public de la classe.
     *
     * @param routeBean bean de la route
     * @param mapViewParameters paramètres du fond de carte
     */
    public RouteManager(RouteBean routeBean, ReadOnlyObjectProperty<MapViewParameters> mapViewParameters) {
        this.routeBean = routeBean;
        this.mapViewParameters = mapViewParameters;
        this.itinerary = new Polyline();
        this.circle = new Circle(RADIUS_OF_DISK);
        this.pane = new Pane(itinerary, circle);

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
            if(routeBean.getRouteProperty().get() != null) recreateItinerary();
            else setDiskAndItineraryVisible();
        });

        routeBean.highlightedPositionProperty().addListener((object, old, now) -> {
            updateDiskLayout();
            setDiskAndItineraryVisible();
        });
       
        routeBean.getRouteProperty().addListener(event -> {
            if (routeBean.getRouteProperty().get() != null) {
                recreateItinerary();
            }
        });

        
        circle.setOnMouseClicked(event -> {
            var routeProperty = routeBean.getRouteProperty().get();
            var highlightedPosition = routeBean.highlightedPosition();
            var waypoints = routeBean.getWaypoint();


            int nodeId = routeProperty.nodeClosestTo(highlightedPosition);
            Point2D p = circle.localToParent(event.getX(), event.getY());

            PointCh newPoint = mapViewParameters.get().pointAt(p.getX(), p.getY()).toPointCh();

            int index = routeBean.indexOfNonEmptySegmentAt(highlightedPosition) + 1;

            waypoints.add(index, new Waypoint(newPoint, nodeId));

        });
    }

    /**
     * Permet de réactualiser l'affichage de l'itinerarire
     */

    private void updateItineraryLayout() {
        itinerary.setLayoutY(- mapViewParameters.get().y());
        itinerary.setLayoutX(- mapViewParameters.get().x());

        updateDiskLayout();
    }

    /**
     * Permet de rendre visible ou invisible le disque et l'itineraire
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
     * Permet de ré-actualiser l'affichage du disque
     */

    private void updateDiskLayout() {
        var highlightedPosition = routeBean.highlightedPosition();
        var routeProperty = routeBean.getRouteProperty().get();

        if (!Double.isNaN(highlightedPosition) && routeProperty != null) {
        PointCh p2 = routeProperty.pointAt(highlightedPosition);
        PointWebMercator point2 = PointWebMercator.ofPointCh(p2);

        circle.setLayoutX(mapViewParameters.get().viewX(point2));
        circle.setLayoutY(mapViewParameters.get().viewY(point2));
        }
    }

    /**
     * Permet de recréer l'itineraire
     */

    private void recreateItinerary() {
        if (routeBean.getRouteProperty().get() != null) {
            Double[] listOfCoordinates = conversionCord(routeBean.getRouteProperty().get().points());

            itinerary.getPoints().setAll(listOfCoordinates);

            updateItineraryLayout();
        }
        setDiskAndItineraryVisible();
    }

    /**
     * Retourne un tableau listant les coordonnèes des points constituants les aretes constituant la route
     *
     * @param listPoints liste des points constituants la route
     *
     * @return un tableau listant les coordonnèes des points constituants les aretes constituant la route
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
