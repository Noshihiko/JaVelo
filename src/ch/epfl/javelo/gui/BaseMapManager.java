package ch.epfl.javelo.gui;

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

    public BaseMapManager(TileManager tiles, WaypointsManager points, ObjectProperty<MapViewParameters> parameters){
        this.canvas = new Canvas();
        this.pane = new Pane(canvas);

        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        pane.setOnScroll(event -> {
            int zoomLvl = (int)(parameters.get().zoom() + event.getDeltaY());

            PointWebMercator mouseBefore = new PointWebMercator(event.getX(), event.getY());
            mouseBefore = new PointWebMercator(mouseBefore.xAtZoomLevel(zoomLvl), mouseBefore.yAtZoomLevel(zoomLvl));

            Point2D mouse = parameters.get().topLeft();
            double x = (mouse.getX() - mouseBefore.x())/zoomLvl;
            double y = (mouse.getY() - mouseBefore.y())/zoomLvl;
            //Point2D mouseAfter = new Point2D(mouseBefore.lon(), mouseBefore.lat());

            parameters.setValue(new MapViewParameters(zoomLvl,x,y));
            //pane.contains(mouseAfter);
        });

        pane.setOnMousePressed(event -> {
            //TODO
        });

        pane.setOnMouseDragged(event -> {
            //TODO
        });

        pane.setOnMouseReleased(event -> {
            //TODO
        });

        pane.setOnMouseClicked(event -> {
            //TODO
        });

        this.tiles = tiles;
        this.points = points;
        this.parameters = parameters;

        canvasSize = (int) Math.pow(2, parameters.get().zoom());
    }

    public Pane pane(){
        return this.pane;
    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;

        GraphicsContext context = canvas.getGraphicsContext2D();

        try {
            for(int i = 0; i < canvasSize; ++i) {
                for (int j = 0; j < canvasSize; ++j) {
                    tilesId = new TileManager.TileId(parameters.get().zoom(), i, j);
                    context.drawImage(tiles.imageForTileAt(tilesId), i, j);
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