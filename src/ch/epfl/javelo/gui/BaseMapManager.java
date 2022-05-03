package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

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

        canvas.widthProperty().addListener(o -> redrawOnNextPulse());
        canvas.heightProperty().addListener(o -> redrawOnNextPulse());

        pane.setOnScroll(event -> {
            int zoomLvl = (int)(parameters.get().zoom() + event.getDeltaY());
            zoomLvl = Math2.clamp(8, zoomLvl, 19);

            //PointWebMercator mouse = new PointWebMercator(event.getX(), event.getY());
            //mouse = new PointWebMercator(mouse.xAtZoomLevel(zoomLvl), mouse.yAtZoomLevel(zoomLvl));

            PointWebMercator topLeftBeforeZoom = new PointWebMercator(parameters.get().topLeft().getX(), parameters.get().topLeft().getY());

            //double x1 = (topLeft.getX() - mouse.x())/Math.pow(2,zoomLvl);
            //double y1 = (topLeft.getY() - mouse.y())/Math.pow(2,zoomLvl);

            double x1 = topLeftBeforeZoom.xAtZoomLevel(zoomLvl);
            double y1 = topLeftBeforeZoom.yAtZoomLevel(zoomLvl);

            parameters.setValue(new MapViewParameters(zoomLvl, x1, y1));
            //pane.contains(mouseAfter);
        });

        pane.setOnMousePressed(event -> {
            draggedPoint = new Point2D(event.getX(), event.getY());
            System.out.println("test");
        });

        pane.setOnMouseDragged(event -> {
            draggedPoint=draggedPoint.subtract(event.getX(), event.getY());
            double x = parameters.get().x() + draggedPoint.getX();
            double y = parameters.get().y() + draggedPoint.getY();
            parameters.setValue(parameters.get().withMinXY(x,y));
        });

        pane.setOnMouseReleased(event -> {
            draggedPoint = null;
        });

        pane.setOnMouseClicked(event -> {
            if (event.isStillSincePress()) {
                points.addWaypoint(parameters.get().x()+event.getX(), parameters.get().y()+event.getY());
            }
        });

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        /*
        parameters.addListener(o -> {
           redrawOnNextPulse();
        });
         */

        redrawOnNextPulse();

    }

    public Pane pane(){
        return this.pane;
    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;

        GraphicsContext context = canvas.getGraphicsContext2D();
       // Point2D topLeft = parameters.get().topLeft();
       // Point2D downRight = new Point2D(topLeft.getX() + pane.getWidth(),topLeft.getY() - pane.getHeight());
        int minX = (int)(parameters.get().x() / MAP_PIXEL);
        int minY = (int)(parameters.get().y() / MAP_PIXEL);

        int maxX = (int)(minX + pane.getWidth() / MAP_PIXEL);
        int maxY = (int)(minY + pane.getHeight() / MAP_PIXEL);

        //int xFirstTile = (int)(topLeft.getX() / MAP_PIXEL);
        //int yFirstTile = (int)(topLeft.getY() / MAP_PIXEL);

        //int xLastTile = Math2.ceilDiv((int)downRight.getX(), MAP_PIXEL);
        //int yLastTile = Math2.ceilDiv((int)downRight.getY(), MAP_PIXEL);
        context.clearRect(0,0, canvas.getWidth(), canvas.getHeight());

        for (int i = minX; i <= maxX; ++i){
            for (int j = minY; j <= maxY; ++j){
                double coorX = i * MAP_PIXEL - parameters.get().x();
                double coorY = j * MAP_PIXEL - parameters.get().y();

                tilesId = new TileManager.TileId(parameters.get().zoom(), i, j);

                if (TileManager.TileId.isValid(parameters.get().zoom(), i, j)) {
                    try {
                        System.out.println(pane.getHeight());
                        System.out.println(canvas.getWidth());
                        System.out.println(canvas.getHeight());

                        context.drawImage(tiles.imageForTileAt(tilesId), coorX, coorY);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else System.out.println("pas de tileId");
            }

        }


        /*
        try {
            tilesId = new TileManager.TileId(12, 2121, 1447);
            context.drawImage(tiles.imageForTileAt(tilesId),0 , 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
         */
    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
}