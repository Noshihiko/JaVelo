package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;

import javafx.beans.InvalidationListener;
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

    //TODO
    // je pense pas qu'il faille le mettre en pv sinon ça en recrée pas
    // à demander
    private final Pane paneItinerary;
    private final Polyline itinerary;
    private final Circle disk;

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

        mapParameters.addListener((object, old, now) -> {

            if (old.zoom() == now.zoom() && routeBean.getRouteProperty().get()!=null)
                moveItinerary();

            else {
                reCreateItinerary();
            }
        });

        routeBean.getWaypoint().addListener((InvalidationListener) event -> {
            if(routeBean.getRouteProperty().get() != null ) {
                System.out.println("size pointtt : " +routeBean.getWaypoint().size());
                reCreateItinerary();
            }
            else setDiskAndItineraryVisible();
        });


        routeBean.highlightedPositionProperty().addListener(event -> {
            if (routeBean.highlightedPositionProperty() == null) {
                setDiskAndItineraryVisible();
            }
            else reCreateItinerary();

        });
       
        routeBean.getRouteProperty().addListener(event -> {
            reCreateItinerary();
        });

        
        disk.setOnMouseClicked(event -> {

            int nodeId = routeBean.getRouteProperty().get().nodeClosestTo(routeBean.highlightedPosition());
            Point2D p = disk.localToParent(event.getX(), event.getY());

            PointCh newPoint = mapParameters.get().pointAt(p.getX(), p.getY()).toPointCh();

            //TODO
            // SEGMENT AT retourne pas la bonne valeur car ça place un waypoint à la fin et pas au milieu

            int index = routeBean.getRouteProperty().get().indexOfSegmentAt(routeBean.highlightedPosition());
            System.out.println("INDEX OF WYAYPOINT : " +index);

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

        itinerary.setLayoutX(-mapParameters.get().x());
        itinerary.setLayoutY(-mapParameters.get().y());

        recreateDisklayout();
    }

    private void setDiskAndItineraryVisible() {
        if (routeBean.getRouteProperty().get()!= null) {
            itinerary.setVisible(true);
            if (Double.isNaN(routeBean.highlightedPosition())) {
                disk.setVisible(false);
            }
            else disk.setVisible(true);
        }
        else {
            disk.setVisible(false);
            itinerary.setVisible(false);
        }
    }

    private void recreateDisklayout() {
        PointCh p2 = routeBean.getRouteProperty().get().pointAt(routeBean.highlightedPosition());
        PointWebMercator point2 = PointWebMercator.ofPointCh(p2);
        disk.setLayoutX(mapParameters.get().viewX(point2));
        disk.setLayoutY(mapParameters.get().viewY(point2));
        //System.out.println(">>> " + point2.xAtZoomLevel(mapParameters.get().zoom()));
    }

    private void reCreateItinerary() {

        if (routeBean.getRouteProperty().get() != null) {
            Double[] listOfCoordinates = conversionCord(routeBean.getRouteProperty().get().points());


            PointWebMercator point = PointWebMercator.ofPointCh(routeBean.getWaypoint().get(routeBean.getWaypoint().size() - 1).pointCh());
            //System.out.println("dernier WAYPOINT : " + routeBean.getWaypoint().get(routeBean.getWaypoint().size() - 1).nodeId());
            //System.out.println("dernier WAYPOINT      : " + point.yAtZoomLevel(mapParameters.get().zoom()));
            //System.out.println("dernier point de route: " + listOfCoordinates[routeBean.getRouteProperty().get().points().size() * 2 - 1]);

            itinerary.getPoints().setAll(listOfCoordinates);

            recreateDisklayout();

        }
        itinerary.setLayoutX(-mapParameters.get().x());
        itinerary.setLayoutY(-mapParameters.get().y());

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
