
package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.function.Consumer;

public final class AnnotatedMapManager {
    private final Graph reseauRoutier;
    private final TileManager tileManager;
    private final Consumer<String> error;
    private final RouteBean routeBean;
    private final RouteManager routeManager;
    private final static int ZOOM_AT_START = 12;
    private final static int X_AT_START = 543200;
    private final static int Y_AT_START = 370650;

    private final ObjectProperty<MapViewParameters> mapViewParameters;
    private final BaseMapManager baseMapManager;
    private final WaypointsManager waypointsManager;
    private final StackPane pane;
    private final DoubleProperty mousePositionOnRouteProperty;
    private final ObjectProperty<Point2D> mousePositionProperty;


    public AnnotatedMapManager(Graph reseauRoutier, TileManager tileManager, RouteBean routeBean, Consumer<String> error) {
        this.reseauRoutier = reseauRoutier;
        this.tileManager = tileManager;
        this.error = error;
        this.routeBean = routeBean;

        mapViewParameters = new SimpleObjectProperty<>(new MapViewParameters(ZOOM_AT_START, X_AT_START, Y_AT_START));

        //creation d'un waypoint manager
        waypointsManager = new WaypointsManager(reseauRoutier, mapViewParameters, routeBean.getWaypoint() ,error);

        // Creation d'une Route manager
        routeManager = new RouteManager(routeBean, mapViewParameters, error);

        //creation de base map manager
        baseMapManager = new BaseMapManager(tileManager, waypointsManager , mapViewParameters);

        pane = new StackPane(baseMapManager.pane(),
                routeManager.pane(),
                waypointsManager.pane());
        pane.getStylesheets().add("map.css");

        mousePositionOnRouteProperty = new SimpleDoubleProperty();
        mousePositionProperty = new SimpleObjectProperty<>();

        pane.setOnMouseMoved(event -> {
            mousePositionProperty.setValue(new Point2D(event.getX(), event.getY()));
        });

        pane.setOnMouseExited(event -> {
            mousePositionProperty.setValue(null);
        });

        mousePositionOnRouteProperty.bind(Bindings.createDoubleBinding(this::setMousePositionOnRouteProperty,
                mapViewParameters, mousePositionProperty, routeBean.getRouteProperty()));
    }

    public Pane pane() {
        return pane;
    }

    public ReadOnlyDoubleProperty mousePositionOnRouteProperty() {
        return mousePositionOnRouteProperty;
    }

    private Double setMousePositionOnRouteProperty() {
        if (mousePositionProperty.get() == null || routeBean.getRouteProperty().get() == null)
            return Double.NaN;

        double x = mousePositionProperty.getValue().getX();
        double y = mousePositionProperty.getValue().getY();

        MapViewParameters mapParameters = mapViewParameters.get();

        PointWebMercator mousePos = mapParameters.pointAt(x, y);

        if (mousePos.toPointCh() == null) return Double.NaN;

        RoutePoint pointRoute = routeBean.getRouteProperty().get().pointClosestTo(mousePos.toPointCh());

        PointWebMercator pointRouteWeb = PointWebMercator.ofPointCh(pointRoute.point());

        double xPointRoute = mapParameters.viewX(pointRouteWeb);
        double yPointRoute = mapParameters.viewY(pointRouteWeb);

        double uX = xPointRoute - x;
        double uY = yPointRoute - y;

        if (Math2.norm(uX , uY) <= 15) return pointRoute.position();
        else return Double.NaN;
    }




}
