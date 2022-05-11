package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;

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

        paneItinerary = new Pane();
        paneItinerary.setPickOnBounds(false);

        itinerary.setId("Route");
        disk.setRadius(RADIUS_OF_DISK);
        disk.setId("highlight");
        paneItinerary.getChildren().add(itinerary);
        paneItinerary.getChildren().add(disk);

        reCreateItinerary();

        parameters.addListener((object, old, now) -> {
            disk.setVisible(true);
            itinerary.setVisible(true);

            if (old.zoom() == now.zoom())
                moveItinerary();

            else {
                reCreateItinerary();
            }
        });

        path.highlightedPositionProperty().addListener(event -> {
            if (path.getWaypoint() == null) {
                changeDiskAndItineraryLayout();
            }
        });
       
        path.getRoute().addListener(event -> {
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

    private void changeDiskAndItineraryLayout() {
        disk.setVisible(false);
        itinerary.setVisible(false);
    }

    private void reCreateItinerary() {
        itinerary.getPoints().clear();
        itinerary.getPoints().addAll( conversionCord(path.getWaypoint()) );
    }

    private ObservableList<Double> conversionCord (ObservableList<Waypoint> listPoints) {
        int arraySize = listPoints.size();
        ObservableList<Double> newListCoordinates = new SimpleListProperty<>();

        for(int i=0; i<arraySize; ++i) {
            PointWebMercator point = PointWebMercator.ofPointCh(listPoints.get(i).pointCh());
            newListCoordinates.add(point.xAtZoomLevel(parameters.get().zoom()));
            newListCoordinates.add(point.yAtZoomLevel(parameters.get().zoom()));
        }
        return newListCoordinates;
    }

    public Pane pane(){
        return paneItinerary;
    }



}
