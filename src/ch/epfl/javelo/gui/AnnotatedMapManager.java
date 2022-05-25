/*
package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.function.Consumer;

public final class AnnotatedMapManager {
    private final Graph reseauRoutier;
    private final TileManager gestionnaireTuiles;
    private final Consumer<String> error;
    private final RouteBean routeBean;
    private final Pane pane;
    private final RouteManager routeManager;
    private final MapViewParameters mapParameters;
    private final int ZOOM_AT_START = 12;
    private final int X_AT_START = 543200;
    private final int Y_AT_START = 370650;
    private final ReadOnlyObjectProperty<MapViewParameters> readOnlyMapParameters;
    private final ObjectProperty<MapViewParameters> onlyMapParameters;
    private final BaseMapManager mapManager;
    private final WaypointsManager waypointsManager;
    private final StackPane empilementPane;
    private final ObjectProperty mousePositionOnRouteProperty;


    public AnnotatedMapManager(Graph reseauRoutier, TileManager gestionnaireTuiles, RouteBean routeBean, Consumer<String> error) {
        this.reseauRoutier = reseauRoutier;
        this.gestionnaireTuiles = gestionnaireTuiles;
        this.error = error;
        this.routeBean = routeBean;
        pane = new Pane();
        mapParameters = new MapViewParameters(ZOOM_AT_START, X_AT_START, Y_AT_START);
        readOnlyMapParameters = new SimpleObjectProperty<>(mapParameters);
        onlyMapParameters = new SimpleObjectProperty<>(mapParameters);

        //creation d'un waypoint manager
        waypointsManager = new WaypointsManager(reseauRoutier, onlyMapParameters, routeBean.getWaypoint() ,error);

        // Creation d'une Route manager
        routeManager = new RouteManager(routeBean, readOnlyMapParameters, error);

        //creation de base map manager
        mapManager = new BaseMapManager(gestionnaireTuiles, waypointsManager , onlyMapParameters);

        empilementPane = new StackPane(mapManager.pane(), routeManager.pane(), waypointsManager.pane());
        empilementPane.setStyle("map.css");

        //TODO a faire
        mousePositionOnRouteProperty = new SimpleObjectProperty();
    }


    public Pane pane() {
        return pane;
    }

    public ObjectProperty mousePositionOnRouteProperty() {
        return mousePositionOnRouteProperty;
    }
}*/
