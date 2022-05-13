package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
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

    //TODO
    // je pense pas qu'il faille le mettre en pv sinon ça en recrée pas
    // à demander
    private Pane paneItinerary;
    private Polyline itinerary;
    private Circle disk;

    private final double RADIUS_OF_DISK = 5;

    public RouteManager(RouteBean routeBean, ReadOnlyObjectProperty<MapViewParameters> mapParameters, Consumer<String> error) {
        this.routeBean = routeBean;
        this.mapParameters = mapParameters;
        this.error = error;

        itinerary = new Polyline();
        itinerary.setId("route");

        disk = new Circle(RADIUS_OF_DISK);
        disk.setId("highlight");
        disk.setVisible(false);

        paneItinerary = new Pane();
        paneItinerary.getChildren().add(itinerary);
        paneItinerary.getChildren().add(disk);
        paneItinerary.setPickOnBounds(false);

        //reCreateItinerary();

        mapParameters.addListener((object, old, now) -> {
            //disk.setVisible(true);
            //itinerary.setVisible(true);

            if (old.zoom() == now.zoom())
                moveItinerary();

            else {
                reCreateItinerary();
            }
        });

        routeBean.getWaypoint().addListener((InvalidationListener) event -> {
            if(routeBean.getRoute().get() != null ) {
                System.out.println("size pointtt : " +routeBean.getWaypoint().size());
                reCreateItinerary();
            }
            else setDiskAndItineraryFalse();
        });


        routeBean.highlightedPositionProperty().addListener(event -> {
            if (routeBean.highlightedPositionProperty() == null) {
                setDiskAndItineraryFalse();
            }
            else reCreateItinerary();
        });
       
        routeBean.getRoute().addListener(event -> {
            if (routeBean.getWaypoint() == null) {
                setDiskAndItineraryFalse();
            }
            else reCreateItinerary();
        });

        
        disk.setOnMouseClicked(event -> {

            int nodeId = routeBean.getRoute().get().nodeClosestTo(routeBean.highlightedPosition());
            Point2D p = disk.localToParent(event.getX(), event.getY());

            PointCh newPoint = mapParameters.get().pointAt(p.getX(), p.getY()).toPointCh();

            int index = routeBean.getRoute().get().indexOfSegmentAt(routeBean.highlightedPosition());

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
        setDiskAndItineraryFalse();

        itinerary.setLayoutX(-mapParameters.get().x());
        itinerary.setLayoutY(-mapParameters.get().y());

        setDiskAndItineraryTrue();
    }

    private void setDiskAndItineraryFalse() {
        disk.setVisible(false);
        itinerary.setVisible(false);
    }

    private void setDiskAndItineraryTrue() {
        if (routeBean.getRoute().get()!= null) {
            disk.setVisible(true);
            itinerary.setVisible(true);
        }
    }

    private void reCreateItinerary() {
        setDiskAndItineraryFalse();
        if (routeBean.getRoute().get() != null) {
            Double[] listeCoord = conversionCord(routeBean.getRoute().get().points());


            PointWebMercator point = PointWebMercator.ofPointCh(routeBean.getWaypoint().get(routeBean.getWaypoint().size() - 1).pointCh());
            System.out.println("dernier WAYPOINT : " + routeBean.getWaypoint().get(routeBean.getWaypoint().size() - 1).nodeId());
            System.out.println("dernier WAYPOINT      : " + point.yAtZoomLevel(mapParameters.get().zoom()));
            System.out.println("dernier point de route: " + listeCoord[routeBean.getRoute().get().points().size() * 2 - 1]);

            itinerary.getPoints().setAll(listeCoord);
        }
        itinerary.setLayoutX(-mapParameters.get().x());
        itinerary.setLayoutY(-mapParameters.get().y());

        setDiskAndItineraryTrue();

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
