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
import java.util.function.Consumer;

public final class RouteManager {
    private final RouteBean routeBean;
    private final ReadOnlyObjectProperty<MapViewParameters> mapParameters;
    private final Consumer<String> error;

    private final static double RADIUS_OF_DISK = 5;

    private final Pane paneItinerary = new Pane();
    private final Polyline itinerary = new Polyline();
    private final Circle disk = new Circle(RADIUS_OF_DISK);

    /**
     * Constructeur public de la classe.
     *
     * @param routeBean
     */


    public RouteManager(RouteBean routeBean, ReadOnlyObjectProperty<MapViewParameters> mapParameters, Consumer<String> error) {
        this.routeBean = routeBean;
        this.mapParameters = mapParameters;
        this.error = error;

        itinerary.setId("route");

        disk.setId("highlight");
        disk.setVisible(false);

        paneItinerary.getChildren().add(itinerary);
        paneItinerary.getChildren().add(disk);
        paneItinerary.setPickOnBounds(false);

        mapParameters.addListener((object, old, now) -> {
            if (old.zoom() == now.zoom() && routeBean.getRouteProperty().get()!=null)
                moveItinerary();
            else
                reCreateItinerary();
        });

        routeBean.getWaypoint().addListener((Observable event) -> {
            if(routeBean.getRouteProperty().get() != null )
                reCreateItinerary();
            else
                setDiskAndItineraryVisible();
        });

        routeBean.highlightedPositionProperty().addListener((object, old, now) -> {
            recreateDisklayout();
            setDiskAndItineraryVisible();
        });
       
        routeBean.getRouteProperty().addListener(event -> {
            if (routeBean.getRouteProperty().get() != null) {
                reCreateItinerary();
            }
        });

        
        disk.setOnMouseClicked(event -> {

            int nodeId = routeBean.getRouteProperty().get().nodeClosestTo(routeBean.highlightedPosition());
            Point2D p = disk.localToParent(event.getX(), event.getY());

            PointCh newPoint = mapParameters.get().pointAt(p.getX(), p.getY()).toPointCh();


            int index = routeBean.getRouteProperty().get().indexOfSegmentAt(routeBean.highlightedPosition())+1;

            boolean check=true;

            for (Waypoint i : routeBean.getWaypoint()) {
                if (i.nodeId() == nodeId) {
                    error.accept("Un point de passage est déjà présent à cet endroit !");
                    check = false;
                    break;
                }
            }
            if (check) routeBean.getWaypoint().add(index, new Waypoint(newPoint, nodeId));
        });
    }

    private void moveItinerary() {
        itinerary.setLayoutY(-mapParameters.get().y());
        itinerary.setLayoutX(-mapParameters.get().x());

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
        if (!Double.isNaN(routeBean.highlightedPositionProperty().get()) && routeBean.getRouteProperty().get() != null) {
        PointCh p2 = routeBean.getRouteProperty().get().pointAt(routeBean.highlightedPosition());
        PointWebMercator point2 = PointWebMercator.ofPointCh(p2);
        disk.setLayoutX(mapParameters.get().viewX(point2));
        disk.setLayoutY(mapParameters.get().viewY(point2));
    }}

    private void reCreateItinerary() {
        if (routeBean.getRouteProperty().get() != null) {
            Double[] listOfCoordinates = conversionCord(routeBean.getRouteProperty().get().points());


            PointWebMercator point = PointWebMercator.ofPointCh(routeBean.getWaypoint().get(routeBean.getWaypoint().size() - 1).pointCh());

            itinerary.getPoints().setAll(listOfCoordinates);

            recreateDisklayout();

            itinerary.setLayoutX(-mapParameters.get().x());
            itinerary.setLayoutY(-mapParameters.get().y());
        }
        setDiskAndItineraryVisible();
    }

    private Double [] conversionCord (List<PointCh> listPoints) {
        int arraySize = listPoints.size();
        int count=0;
        Double [] newListCoordinates = new Double[arraySize*2];

        for(int i = 0; i < arraySize; ++i) {
            PointWebMercator point = PointWebMercator.ofPointCh(listPoints.get(i));

            newListCoordinates[count] = point.xAtZoomLevel(mapParameters.get().zoom());
            ++count;

            newListCoordinates[count] = point.yAtZoomLevel(mapParameters.get().zoom());
            ++count;
        }
        return newListCoordinates;
    }

    public Pane pane(){
        return paneItinerary;
    }
}
