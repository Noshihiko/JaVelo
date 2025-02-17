package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.geometry.Point2D;

import java.io.IOException;

/**
 * Gère l'affichage et l'interaction avec le fond de carte.
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */
public final class BaseMapManager {
    public final TileManager tiles;
    public final WaypointsManager points;
    public final ObjectProperty<MapViewParameters> mapViewParameters;

    private final Canvas canvas = new Canvas();
    private final Pane pane = new Pane(canvas);

    private Point2D draggedPoint;
    private boolean redrawNeeded;

    private final static int ZOOM_MIN = 8;
    private final static int ZOOM_MAX = 19;
    private final static int MAP_PIXEL = 256;
    private final static int ZOOM_TIME_MS = 200;

    /**
     * Constructeur public de la classe.
     *
     * @param tiles             le gestionnaire de tuiles à utiliser pour obtenir les tuiles de la carte
     * @param points            le gestionnaire des points de passage
     * @param mapViewParameters une propriété JavaFX contenant les paramètres de la carte affichée
     */
    public BaseMapManager(TileManager tiles, WaypointsManager points, ObjectProperty<MapViewParameters> mapViewParameters) {
        this.tiles = tiles;
        this.points = points;
        this.mapViewParameters = mapViewParameters;

        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        canvas.sceneProperty().addListener((p, oldScene, newScene) -> {
            assert oldScene == null;
            newScene.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        canvas.widthProperty().addListener((p, oldScene, newScene) -> redrawOnNextPulse());
        canvas.heightProperty().addListener((p, oldScene, newScene) -> redrawOnNextPulse());
        mapViewParameters.addListener((p, oldScene, newScene) -> redrawOnNextPulse());

        eventManagement();
    }

    /**
     * Retourne le panneau JavaFX affichant le fond de carte.
     *
     * @return le panneau JavaFX affichant le fond de carte
     */
    public Pane pane() {
        return pane;
    }

    /**
     * Permet de demander un redessin de la carte au prochain battement.
     */
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    /**
     * Effectue le redessin si et seulement si l'attribut redrawNeeded est vrai.
     */
    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;

        GraphicsContext context = canvas.getGraphicsContext2D();
        int zoom;
        double xCoordinate;
        double yCoordinate;
        TileManager.TileId tilesId;
        double mapX = mapViewParameters.get().x();
        double mapY = mapViewParameters.get().y();

        int minX = (int) (mapX / MAP_PIXEL);
        int minY = (int) (mapY / MAP_PIXEL);

        int maxX = (int) ((mapX + pane.getWidth()) / MAP_PIXEL);
        int maxY = (int) ((mapY + pane.getHeight()) / MAP_PIXEL);


        for (int i = minX; i <= maxX; ++i) {
            for (int j = minY; j <= maxY; ++j) {
                xCoordinate = i * MAP_PIXEL - mapX;
                yCoordinate = j * MAP_PIXEL - mapY;

                zoom = mapViewParameters.get().zoom();
                tilesId = new TileManager.TileId(zoom, i, j);

                if (TileManager.TileId.isValid(zoom, i, j)) {
                    try {
                        context.drawImage(tiles.imageForTileAt(tilesId), xCoordinate, yCoordinate);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    /**
     * Méthode qui gère et détecte différents événements.
     */
    private void eventManagement(){
        SimpleLongProperty minScrollTime = new SimpleLongProperty();
        pane.setOnScroll(event -> {
            if (event.getDeltaY() == 0d)
                return;
            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get())
                return;
            minScrollTime.set(currentTime + ZOOM_TIME_MS);

            int zoomDelta = (int) Math.signum(event.getDeltaY());
            int mapZoom = mapViewParameters.get().zoom();

            int zoomLvl = Math2.clamp(ZOOM_MIN,mapZoom + zoomDelta, ZOOM_MAX);

            PointWebMercator mouseBeforeZoom = PointWebMercator.of(mapZoom,
                    mapViewParameters.get().x() + event.getX(),
                    mapViewParameters.get().y() + event.getY());

            double xZoomed = mouseBeforeZoom.xAtZoomLevel(zoomLvl) - event.getX();
            double yZoomed = mouseBeforeZoom.yAtZoomLevel(zoomLvl) - event.getY();

            mapViewParameters.setValue(new MapViewParameters(zoomLvl, xZoomed, yZoomed));
            redrawOnNextPulse();
        });

        pane.setOnMousePressed(event -> draggedPoint = new Point2D(event.getX(), event.getY()));

        pane.setOnMouseDragged(event -> {
            draggedPoint = draggedPoint.subtract(event.getX(), event.getY());

            double x = mapViewParameters.get().x() + draggedPoint.getX();
            double y = mapViewParameters.get().y() + draggedPoint.getY();

            mapViewParameters.setValue(mapViewParameters.get().withMinXY(x,y));

            draggedPoint = new Point2D(event.getX(), event.getY());
            redrawOnNextPulse();
        });

        pane.setOnMouseReleased(event -> draggedPoint = null);

        pane.setOnMouseClicked(event -> {
            if (event.isStillSincePress())
                points.addWaypoint(event.getX(), event.getY());

            redrawOnNextPulse();
        });
    }
}