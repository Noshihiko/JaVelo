package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
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

    public boolean redrawNeeded;
    private int canvasSize;

    private Point2D draggedPoint;

    public BaseMapManager(TileManager tiles, WaypointsManager points, ObjectProperty<MapViewParameters> parameters){
        this.canvas = new Canvas();
        this.pane = new Pane(canvas);
        this.tiles = tiles;
        this.points = points;
        this.parameters = parameters;

        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        pane.setOnScroll(event -> {
            int zoomLvl = (int)(parameters.get().zoom() + event.getDeltaY());
            zoomLvl = Math2.clamp(8, zoomLvl, 19);

            PointWebMercator mouseBefore = new PointWebMercator(event.getX(), event.getY());
            mouseBefore = new PointWebMercator(mouseBefore.xAtZoomLevel(zoomLvl), mouseBefore.yAtZoomLevel(zoomLvl));

            java.awt.geom.Point2D mouse = parameters.get().topLeft();
            double x1 = (mouse.getX() - mouseBefore.x())/Math.pow(2,zoomLvl);
            double y1 = (mouse.getY() - mouseBefore.y())/Math.pow(2,zoomLvl);
            //Point2D mouseAfter = new Point2D(mouseBefore.lon(), mouseBefore.lat());

            parameters.setValue(new MapViewParameters(zoomLvl, x1, y1));
            //pane.contains(mouseAfter);
        });

        pane.setOnMousePressed(event -> {
            draggedPoint = new Point2D(event.getX(), event.getY());
        });

        pane.setOnMouseDragged(event -> {
            draggedPoint.subtract(event.getX(), event.getY());
            double x = parameters.get().x() + draggedPoint.getX();
            double y = parameters.get().y() + draggedPoint.getY();

            parameters.setValue(new MapViewParameters(parameters.get().zoom(), x, y));
        });

        pane.setOnMouseReleased(event -> {
            draggedPoint = null;
        });

        pane.setOnMouseClicked(event -> {
            if (!event.isStillSincePress()) {
                PointCh clicked = new PointCh(event.getX(), event.getY());

                //comment fait-on pour récup le nodeId ??
                int nodeId = 0;
                //faut-il modifier seulement points ou faut-il également rajouter
                //quelque chose à "parameters"?
                points.listPoints.add(new Waypoint(clicked, nodeId));
            }
        });

        canvasSize = (int) Math.pow(2, parameters.get().zoom());
        /*
        parameters.addListener(() -> {

        });

         */

    }

    public Pane pane(){
        return this.pane;
    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;
        //constante pour 256 pixels à créer
        try {
            GraphicsContext context = canvas.getGraphicsContext2D();
            java.awt.geom.Point2D topLeft = parameters.get().topLeft();
            java.awt.geom.Point2D downRight = new java.awt.geom.Point2D.Double(topLeft.getX() + pane.getWidth(),topLeft.getY() - pane.getHeight());

            int xFirstTile = Math.floorDiv((int)topLeft.getX(), 256);
            int yFirstTile = Math.floorDiv((int)topLeft.getY(), 256);

            int xLastTile = Math2.ceilDiv((int)downRight.getX(), 256);
            int yLastTile = Math2.ceilDiv((int)downRight.getY(), 256);


            for (int i = xFirstTile; i < xLastTile; ++i){
                for (int j = yFirstTile; j > yLastTile; ++j){
                    double coorX = i*256 - topLeft.getX();
                    double coorY = j*256 - topLeft.getY();

                    tilesId = new TileManager.TileId(parameters.get().zoom(), i, j);
                    context.drawImage(tiles.imageForTileAt(tilesId), coorX, coorY);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
}