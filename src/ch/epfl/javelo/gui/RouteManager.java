package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.function.Consumer;

public final class RouteManager {
    private final RouteBean path;
    private final ReadOnlyObjectProperty<MapViewParameters> parameters;
    private final Consumer<String> error;
    private final Pane paneItinerary;
    private Polyline itinerary;
    private Circle disk;
    private final double RADIUS_OF_DISK = 5;

    public RouteManager(RouteBean path, ReadOnlyObjectProperty<MapViewParameters> parameters, Consumer<String> error) {
        this.path = path;
        this.parameters = parameters;
        this.error = error;
        this.itinerary = new Polyline();
        this.disk = new Circle();

        paneItinerary = new Pane();
        paneItinerary.setPickOnBounds(false);

        itinerary = new Polyline();
        itinerary.setId("Route");

        disk = new Circle();
        disk.setRadius(RADIUS_OF_DISK);
        disk.setId("highlight");
        paneItinerary.getChildren().add(itinerary);
        paneItinerary.getChildren().add(disk);

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

        path.highlightedPositionProperty().addListener(event -> {
            if (path.highlightedPositionProperty() == null) {
                setDiskAndItineraryFalse();
            }
            else reCreateItinerary();
        });
       
        path.getRoute().addListener(event -> {
            if (path.getWaypoint() == null) {
                setDiskAndItineraryFalse();
            }
            else reCreateItinerary();
        });

        path.getWaypoint().addListener((InvalidationListener) event -> {
            System.out.println(" test 1: listener sourie");
            System.out.println(path.getWaypoint());
            if(path.getRoute().get() != null ) {
                System.out.println(" test 2: route non nulle?");
                reCreateItinerary();
            }
            else setDiskAndItineraryFalse();
        });

        
        disk.setOnMouseClicked(event -> {

            int nodeId = path.getRoute().get().nodeClosestTo(path.highlightedPosition());
            Point2D p = disk.localToParent(event.getX(), event.getY());

            PointCh newPoint = parameters.get().pointAt(p.getX(), p.getY()).toPointCh();

            int index = path.getRoute().get().indexOfSegmentAt(path.highlightedPosition());

            boolean check=true;

            for (Waypoint i :path.getWaypoint()) {
                if (i.nodeId() == nodeId) {
                    error.accept("Un point de passage est déjà présent à cet endroit !");
                    check=false;
                    break;
                }
            }
            if( check )
                path.getWaypoint().add(index, new Waypoint(newPoint, nodeId));
        });
        
    }

    private void moveItinerary() {
        itinerary.setLayoutX(itinerary.getLayoutX() + parameters.get().topLeft().getX());
        itinerary.setLayoutX(itinerary.getLayoutY() + parameters.get().topLeft().getY());
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

    private void setDiskAndItineraryFalse() {
        disk.setVisible(false);
        itinerary.setVisible(false);
    }

    private void reCreateItinerary() {
        Double[] liste = conversionCord(path.getWaypoint());
        System.out.println(liste);
        itinerary.getPoints().setAll( liste );

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
        newListCoordinates=null;
        int count =0;

        for(int i=0; i<arraySize; ++i) {
            //list double
            PointWebMercator point = PointWebMercator.ofPointCh(listPoints.get(i).pointCh());
            System.out.println("index" +i+" "+ point.xAtZoomLevel(parameters.get().zoom()));
            newListCoordinates[count] = point.xAtZoomLevel(parameters.get().zoom());
            ++count;
            newListCoordinates[count] = point.yAtZoomLevel(parameters.get().zoom());
            ++count;
        }

        System.out.print(newListCoordinates[3]);

        return newListCoordinates;
    }

    public Pane pane(){
        return paneItinerary;
    }



}
