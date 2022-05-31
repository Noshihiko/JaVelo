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

public final class RouteManager {
    private final RouteBean routeBean;
    private final ReadOnlyObjectProperty<MapViewParameters> mapViewParameters;

    private final static double RADIUS_OF_DISK = 5;

    private final Polyline itinerary = new Polyline();
    private final Circle disk = new Circle(RADIUS_OF_DISK);
    //TODO isok ou vaut mieux faire un addAll dans constructeur ?
    private final Pane pane = new Pane(itinerary, disk);

    /**
     * Constructeur public de la classe.
     *
     * @param routeBean
     */
    public RouteManager(RouteBean routeBean, ReadOnlyObjectProperty<MapViewParameters> mapViewParameters) {
        this.routeBean = routeBean;
        this.mapViewParameters = mapViewParameters;

        itinerary.setId("route");
        disk.setId("highlight");

        disk.setVisible(false);
        pane.setPickOnBounds(false);

        mapViewParameters.addListener((object, old, now) -> {
            if ((old.zoom() == now.zoom() && routeBean.getRouteProperty().get() != null))
                moveItinerary();
            else recreateItinerary();

        });

        routeBean.getWaypoint().addListener((Observable event) -> {
            if(routeBean.getRouteProperty().get() != null) recreateItinerary();
            else setDiskAndItineraryVisible();
        });

        routeBean.highlightedPositionProperty().addListener((object, old, now) -> {
            recreateDisklayout();
            setDiskAndItineraryVisible();
        });
       
        routeBean.getRouteProperty().addListener(event -> {
            if (routeBean.getRouteProperty().get() != null) {
                recreateItinerary();
            }
        });

        
        disk.setOnMouseClicked(event -> {
            var routeProperty = routeBean.getRouteProperty().get();
            var highlightedPosition = routeBean.highlightedPosition();
            var waypoints = routeBean.getWaypoint();


            int nodeId = routeProperty.nodeClosestTo(highlightedPosition);
            Point2D p = disk.localToParent(event.getX(), event.getY());

            PointCh newPoint = mapViewParameters.get().pointAt(p.getX(), p.getY()).toPointCh();

            int index = routeBean.indexOfNonEmptySegmentAt(highlightedPosition) + 1;

            waypoints.add(index, new Waypoint(newPoint, nodeId));

        });
    }

    private void moveItinerary() {
        itinerary.setLayoutY(- mapViewParameters.get().y());
        itinerary.setLayoutX(- mapViewParameters.get().x());

        recreateDisklayout();
    }

    private void setDiskAndItineraryVisible() {
        if (routeBean.getRouteProperty().get()!= null) {
            itinerary.setVisible(true);
            disk.setVisible(!Double.isNaN(routeBean.highlightedPosition()));
        }
        else {
            disk.setVisible(false);
            itinerary.setVisible(false);
        }
    }

    private void recreateDisklayout() {
        var highlightedPosition = routeBean.highlightedPosition();
        var routeProperty = routeBean.getRouteProperty().get();

        if (!Double.isNaN(highlightedPosition) && routeProperty != null) {
        PointCh p2 = routeProperty.pointAt(highlightedPosition);
        PointWebMercator point2 = PointWebMercator.ofPointCh(p2);

        disk.setLayoutX(mapViewParameters.get().viewX(point2));
        disk.setLayoutY(mapViewParameters.get().viewY(point2));
        }
    }

    private void recreateItinerary() {
        if (routeBean.getRouteProperty().get() != null) {
            Double[] listOfCoordinates = conversionCord(routeBean.getRouteProperty().get().points());


            PointWebMercator point = PointWebMercator.ofPointCh(routeBean.getWaypoint().get(routeBean.getWaypoint().size() - 1).pointCh());

            itinerary.getPoints().setAll(listOfCoordinates);

            recreateDisklayout();

            itinerary.setLayoutX(-mapViewParameters.get().x());
            itinerary.setLayoutY(-mapViewParameters.get().y());
        }
        setDiskAndItineraryVisible();
    }

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

    public Pane pane(){
        return pane;
    }
}
