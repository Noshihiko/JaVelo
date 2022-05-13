package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.geometry.Point2D;

import java.io.IOException;

public final class BaseMapManager {
    public TileManager tiles;
    public WaypointsManager  points;
    public ObjectProperty<MapViewParameters> parameters;

    public Pane pane;
    public Canvas canvas;
    public TileManager.TileId tilesId;
    private Point2D draggedPoint;

    public boolean redrawNeeded;
    private final int MAP_PIXEL = 256;

    public BaseMapManager(TileManager tiles, WaypointsManager points, ObjectProperty<MapViewParameters> parameters){
        this.tiles = tiles;
        this.points = points;
        this.parameters = parameters;

        this.canvas = new Canvas();
        this.pane = new Pane(canvas);

        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        pane.setOnScroll(event -> {
            int VALUE_ONE_SCROLL = 26;
            int ZOOM_MIN = 8;
            int ZOOM_MAX = 19;

            int zoomLvl = parameters.get().zoom() + (int) (event.getDeltaY()/VALUE_ONE_SCROLL) ;
            zoomLvl = Math2.clamp(ZOOM_MIN, zoomLvl, ZOOM_MAX);

            PointWebMercator mouseBeforeZoom = PointWebMercator.of(parameters.get().zoom(),
                    parameters.get().x() + event.getX(), parameters.get().y() + event.getY());

            double xMAfter = mouseBeforeZoom.xAtZoomLevel(zoomLvl);
            double yMAfter = mouseBeforeZoom.yAtZoomLevel(zoomLvl);

            double xTLAfter= xMAfter - event.getX();
            double yTLAfter = yMAfter - event.getY();

            System.out.println("zoom : " + zoomLvl);
            parameters.setValue(new MapViewParameters(zoomLvl, xTLAfter, yTLAfter));

            redrawOnNextPulse();
        });

        pane.setOnMousePressed(event -> draggedPoint = new Point2D(event.getX(), event.getY()));

        pane.setOnMouseDragged(event -> {
            draggedPoint = draggedPoint.subtract(event.getX(), event.getY());

            double x = parameters.get().x() + draggedPoint.getX();
            double y = parameters.get().y() + draggedPoint.getY();

            parameters.setValue(parameters.get().withMinXY(x,y));

            draggedPoint = new Point2D(event.getX(), event.getY());

            redrawOnNextPulse();
        });

        pane.setOnMouseReleased(event -> draggedPoint = null);

        pane.setOnMouseClicked(event -> {
            if (event.isStillSincePress()) {
                points.addWaypoint(event.getX(), event.getY());
            }
            redrawOnNextPulse();
        });

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        parameters.addListener(o -> redrawOnNextPulse());
        canvas.widthProperty().addListener(o -> redrawOnNextPulse());
        canvas.heightProperty().addListener(o -> redrawOnNextPulse());

    }

    public Pane pane(){
        return pane;
    }

    private void redrawIfNeeded() {

        if (!redrawNeeded) return;
        redrawNeeded = false;
        System.out.println("Redrawifneeded : "+parameters.get().zoom());

        GraphicsContext context = canvas.getGraphicsContext2D();
        double coorX;
        double coorY;

        int minX = (int)(parameters.get().x() / MAP_PIXEL);
        int minY = (int)(parameters.get().y() / MAP_PIXEL);

        int maxX = (int)((parameters.get().x() + pane.getWidth()) / MAP_PIXEL);
        int maxY = (int)((parameters.get().y()+ pane.getHeight()) / MAP_PIXEL);


        for (int i = minX; i <= maxX; ++i){
            for (int j = minY; j <= maxY; ++j){
                coorX = i * MAP_PIXEL - parameters.get().x();
                coorY = j * MAP_PIXEL - parameters.get().y();

                tilesId = new TileManager.TileId(parameters.get().zoom(), i, j);

                System.out.println(TileManager.TileId.isValid(parameters.get().zoom(), i, j));

                if (TileManager.TileId.isValid(parameters.get().zoom(), i, j)) {
                    try {
                        context.drawImage(tiles.imageForTileAt(tilesId), coorX, coorY);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
}