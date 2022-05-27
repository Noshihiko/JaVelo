/*
package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.property.*;

import javafx.geometry.Point2D;
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
    private final DoubleProperty mousePositionOnRouteProperty;
    private final ObjectProperty<Point2D> mousePositionProperty;


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


        mousePositionOnRouteProperty = new SimpleDoubleProperty();
        mousePositionProperty = new SimpleObjectProperty<>();

        mapManager.pane().setOnMouseMoved(event -> {
            mousePositionProperty.setValue(new Point2D(event.getX(), event.getY()));
            setMousePositionOnRouteProperty();
        });

        mapManager.pane().setOnMouseExited(event -> {
            mousePositionProperty.setValue(null);
            mousePositionOnRouteProperty.set(Double.NaN);
        });
    }

    public Pane pane() {
        return pane;
    }

    public ReadOnlyDoubleProperty mousePositionOnRouteProperty() {
        return mousePositionOnRouteProperty;
    }

    private void setMousePositionOnRouteProperty() {

        double x = mousePositionProperty.getValue().getX();
        double y = mousePositionProperty.getValue().getY();

        PointWebMercator mousePos = mapParameters.pointAt(x, y);

        RoutePoint pointRoute = routeBean.getRouteProperty().get().pointClosestTo(mousePos.toPointCh());

        PointWebMercator pointRouteWeb = PointWebMercator.ofPointCh(pointRoute.point());

        double xPointRoute = mapParameters.viewX(pointRouteWeb);
        double yPointRoute = mapParameters.viewY(pointRouteWeb);

        double uX = xPointRoute - x;
        double uY = yPointRoute - y;

        if (Math2.norm(uX , uY) >= 15 ) mousePositionOnRouteProperty.set(pointRoute.position());
        else mousePositionOnRouteProperty.set(Double.NaN);
    }




}
*/