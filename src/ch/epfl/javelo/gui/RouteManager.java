package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.function.Consumer;

public final class RouteManager {
    RouteBean path;
    ReadOnlyObjectProperty<MapViewParameters> parameters;
    Consumer<String> error;
    private Pane paneItinerary;
    Polyline itinerary;
    Circle disk;
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

        createItinerary();

        //*******************************************************************
         /*l faut noter que la polyligne et le disque sont toujours présents dans le graphe de scène,
        mais il ne sont pas toujours visible. Par exemple, si aucun itinéraire n'existe pas c.-à-d.
        si la propriété route du bean contient null — alors ni l'un ni l'autre ne sont visibles.*/
        //autre listener je me souviens plus sur quoi :((
        RouteBean.highlightedPosition().addListener(event -> {
            //est ce quand je set a false, je dois reset a vrai qqe part ????
            if (RouteBean.getRoute() == null) {
                changeDiskAndItineraryLayout();
            }
        });
        //*******************************************************************


        //*******************************************************************
        //changement ou reconstruction si ca bouge et ou si c'est zoomer
        parameters.addListener((object, old, now) -> {
            disk.setVisible(true);
            itinerary.setVisible(true);

            if (old.zoom() == now.zoom())
                moveItinerary();

            else if (old.zoom() != now.zoom()) {
                createItinerary();
            }
        });
        //*******************************************************************


        //*******************************************************************
        disk.setOnMouseClicked(event -> {
            //point ch position souris
            PointCh newPoint = parameters.get().pointAt(event.getSceneX(), event.getSceneY()).toPointCh();

            //pointch.indefOfSegmentAt(route de route bean); = index du waypoint
            int index = newPoint.indexOfSegmentAt(RouteBean.getRoute());

            //node closest to to find thye node id
            int nodeId = nodeClosestTo(newPoint, 100);
            boolean check=true;

            for (Waypoint i :RouteBean.get().waypoints) {
                if (RouteBean.get().waypoints.get(i).nodeId() == nodeId) {
                    error.accept("Un point de passage est déjà présent à cet endroit !");
                    check=false;
                    break;
                }
            }
            if( check )
            //listewaypoimts.add(index, waypiint)
            RouteBean.get().waypoints.add(index, new Waypoint(newPoint, nodeId));

            //higlighted position de route bean -> position sur le route du highlited point (point ch)

        });
        //*******************************************************************
    }

    private void moveItinerary() {
        itinerary.setLayoutX(itinerary.getLayoutX() + parameters.get().topLeft().getX());
        itinerary.setLayoutX(itinerary.getLayoutY() + parameters.get().topLeft().getY());
    }

    private void changeDiskAndItineraryLayout() {
        disk.setVisible(false);
        itinerary.setVisible(false);
    }

    private void createItinerary() {
        itinerary.getPoints().clear();
        itinerary.getPoints().addAll(conversionCord(RouteBean.getRoute().get().points()));
    }

    private double[] conversionCord (ArrayList<PointCh> listPoints) {
        int arraySize = listPoints.size();
        int arrayCoordinatesSize = arraySize*2;
        int counter =0;
        double [] newListCoordinates = new double[arrayCoordinatesSize];

        for(int i=0; i<arraySize; ++i) {
            PointWebMercator point = PointWebMercator.ofPointCh(listPoints.get(i));
            newListCoordinates[counter] = point.xAtZoomLevel(parameters.get().zoom());
            ++counter;
            newListCoordinates[counter] = point.yAtZoomLevel(parameters.get().zoom());
            ++counter;
        }
        return newListCoordinates;
    }

    public Pane pane(){
        return paneItinerary;
    }



}
