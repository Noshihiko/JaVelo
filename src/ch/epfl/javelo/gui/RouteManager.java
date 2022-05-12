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

import java.util.function.Consumer;

public final class RouteManager {
    private final RouteBean routeBean;
    //chez Max, pas un read only TODO
    private final ReadOnlyObjectProperty<MapViewParameters> parameters;
    private final Consumer<String> error;
    private final Pane paneItinerary;
    private Polyline itinerary;
    private Circle disk;

    private final double RADIUS_OF_DISK = 5;

    public RouteManager(RouteBean routeBean, ReadOnlyObjectProperty<MapViewParameters> parameters, Consumer<String> error) {
        this.routeBean = routeBean;
        this.parameters = parameters;
        this.error = error;

        itinerary = new Polyline();
        itinerary.setId("Route");

        disk = new Circle(RADIUS_OF_DISK);
        disk.setId("highlight");

        paneItinerary = new Pane();
        paneItinerary.getChildren().add(itinerary);
        paneItinerary.getChildren().add(disk);
        paneItinerary.setPickOnBounds(false);

        //reCreateItinerary();

        /*parameters.addListener((object, old, now) -> {
            disk.setVisible(true);
            itinerary.setVisible(true);

            if (old.zoom() == now.zoom())
                moveItinerary();

            else {
                reCreateItinerary();
                displayDiskAndItinerary();
            }
        });*/

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

        routeBean.getWaypoint().addListener((InvalidationListener) event -> {
            System.out.println(" test 1: listener sourie");
            System.out.println(routeBean.getWaypoint());
            if(routeBean.getRoute().get() != null ) {
                System.out.println(" test 2: route non nulle?");
                reCreateItinerary();
            }
            else setDiskAndItineraryFalse();
        });

        
        disk.setOnMouseClicked(event -> {

            int nodeId = routeBean.getRoute().get().nodeClosestTo(routeBean.highlightedPosition());
            Point2D p = disk.localToParent(event.getX(), event.getY());

            PointCh newPoint = parameters.get().pointAt(p.getX(), p.getY()).toPointCh();

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
    /*
    private void moveItinerary() {
        itinerary.setLayoutX(itinerary.getLayoutX() + parameters.get().topLeft().getX());
        itinerary.setLayoutY(itinerary.getLayoutY() + parameters.get().topLeft().getY());
    }

    private void displayDiskAndItinerary(double xItinerary, double yItinerary, double xCircle, double yCircle) {
        PointCh newPointItinerary = parameters.get().pointAt(xItinerary, yItinerary).toPointCh();
        PointWebMercator pointI = PointWebMercator.ofPointCh(newPointItinerary);
        xItinerary = parameters.get().viewX(pointI);
        yItinerary = parameters.get().viewY(pointI);

        itinerary.setLayoutX(xItinerary);
        itinerary.setLayoutY(yItinerary);

        PointCh newPointCircle = parameters.get().pointAt(xCircle, yCircle).toPointCh();
        PointWebMercator pointC = PointWebMercator.ofPointCh(newPointCircle);
        xCircle = parameters.get().viewX(pointC);
        yCircle = parameters.get().viewY(pointC);

        disk.setLayoutX(xCircle);
        disk.setLayoutY(yCircle);


    }

     */

    private void setDiskAndItineraryFalse() {
        disk.setVisible(false);
        itinerary.setVisible(false);
    }

    private void reCreateItinerary() {
        Double[] liste = conversionCord(routeBean.getWaypoint());
        //System.out.println(liste);
        itinerary.getPoints().setAll(liste);

        itinerary.setLayoutX(parameters.get().x());
        itinerary.setLayoutY(parameters.get().y());

        itinerary.setVisible(true);
        disk.setVisible(true);
    }

    private Double [] conversionCord (ObservableList<Waypoint> listPoints) {
        int arraySize = listPoints.size();
        System.out.println(listPoints.size());
        //ObservableList<Double> newListCoordinates = new SimpleListProperty<>();
        Double [] newListCoordinates = new Double[arraySize*2];
        int count =0;

        for(int i=0; i < arraySize; ++i) {
            //list double
            PointWebMercator point = PointWebMercator.ofPointCh(listPoints.get(i).pointCh());
            //System.out.println("index" +i+" "+ point.xAtZoomLevel(parameters.get().zoom()));
            newListCoordinates[count] = point.xAtZoomLevel(parameters.get().zoom());
            ++count;
            newListCoordinates[count] = point.yAtZoomLevel(parameters.get().zoom());
            ++count;
        }

        //System.out.print(newListCoordinates[3]);

        return newListCoordinates;
    }

    public Pane pane(){
        return paneItinerary;
    }

}
