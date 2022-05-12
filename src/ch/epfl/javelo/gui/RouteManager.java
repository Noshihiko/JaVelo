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
        itinerary.setId("Route");

        disk = new Circle(RADIUS_OF_DISK);
        disk.setId("highlight");
        disk.setVisible(false);

        paneItinerary = new Pane();
        paneItinerary.getChildren().add(itinerary);
        paneItinerary.getChildren().add(disk);
        paneItinerary.setPickOnBounds(false);

        //reCreateItinerary();

        /*mapParameters.addListener((object, old, now) -> {
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
            //System.out.println(" test 1: listener sourie");
            //System.out.println(routeBean.getWaypoint());
            if(routeBean.getRoute().get() != null ) {
                System.out.println(" test 2: route non nulle?");
                reCreateItinerary();
            }
            else setDiskAndItineraryFalse();
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
        itinerary.setLayoutX(itinerary.getLayoutX() + mapParameters.get().topLeft().getX());
        itinerary.setLayoutY(itinerary.getLayoutY() + mapParameters.get().topLeft().getY());
    }

    private void displayDiskAndItinerary(double xItinerary, double yItinerary, double xCircle, double yCircle) {
        PointCh newPointItinerary = mapParameters.get().pointAt(xItinerary, yItinerary).toPointCh();
        PointWebMercator pointI = PointWebMercator.ofPointCh(newPointItinerary);
        xItinerary = mapParameters.get().viewX(pointI);
        yItinerary = mapParameters.get().viewY(pointI);

        itinerary.setLayoutX(xItinerary);
        itinerary.setLayoutY(yItinerary);

        PointCh newPointCircle = mapParameters.get().pointAt(xCircle, yCircle).toPointCh();
        PointWebMercator pointC = PointWebMercator.ofPointCh(newPointCircle);
        xCircle = mapParameters.get().viewX(pointC);
        yCircle = mapParameters.get().viewY(pointC);

        disk.setLayoutX(xCircle);
        disk.setLayoutY(yCircle);


    }



    private void setDiskAndItineraryFalse() {
        disk.setVisible(false);
        itinerary.setVisible(false);
    }

    private void reCreateItinerary() {
        Double[] liste = conversionCord(routeBean.getWaypoint());
        //System.out.println(liste);
        itinerary.getPoints().setAll(liste);

        itinerary.setLayoutX(mapParameters.get().x());
        itinerary.setLayoutY(mapParameters.get().y());

        itinerary.setVisible(true);
        disk.setVisible(true);
    }

    private Double [] conversionCord (ObservableList<Waypoint> listPoints) {
        int arraySize = listPoints.size();
        //System.out.println(listPoints.size());
        //ObservableList<Double> newListCoordinates = new SimpleListProperty<>();
        Double [] newListCoordinates = new Double[arraySize*2];

        for(int i = 0; i < arraySize; ++i) {
            //list double
            PointWebMercator point = PointWebMercator.ofPointCh(listPoints.get(i).pointCh());
            //System.out.println("index" +i+" "+ point.xAtZoomLevel(mapParameters.get().zoom()));
           for (int count = 0; count < arraySize*2; ++count) {
               newListCoordinates[count] = point.xAtZoomLevel(mapParameters.get().zoom());
               ++count;
               newListCoordinates[count] = point.yAtZoomLevel(mapParameters.get().zoom());
           }
        }

        //System.out.print(newListCoordinates[3]);
        return newListCoordinates;
    }

    public Pane pane(){
        return paneItinerary;
    }
}
