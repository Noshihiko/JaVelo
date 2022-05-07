/*package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.Route;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;

public final class RouteManager {
    RouteBean path;
    ReadOnlyObjectProperty<MapViewParameters> parameters;
    Consumer<Error> error;
    private Pane paneItinerary;
    Polyline itinerary;
    Circle disk;
    private final double RADIUS_OF_DISK = 5;

    public RouteManager(RouteBean path, ReadOnlyObjectProperty<MapViewParameters> parameters, Consumer<Error> error){
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
        l faut noter que la polyligne et le disque sont toujours présents dans le graphe de scène,
        mais il ne sont pas toujours visible. Par exemple, si aucun itinéraire n'existe pas c.-à-d.
        si la propriété route du bean contient null — alors ni l'un ni l'autre ne sont visibles.


        //tblo points: tblopointarretes creer dans route bean de types { x0, y0, x1, y1, ... } : routeBean.getPoints ?
        itinerary.getPoints().addAll(conversionCord(RouteBean.Route().points()));

        path.routeProperty().addListener(event->{

        });

        paneItinerary.addEventHandler(observable -> {
            itinerary.setLayoutX(itinerary.getLayoutX() + parameters.get().topLeft().getX());
            itinerary.setLayoutX(itinerary.getLayoutY() + parameters.get().topLeft().getY());
        });

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



}*/
