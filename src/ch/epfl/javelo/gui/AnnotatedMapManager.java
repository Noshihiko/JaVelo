package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.layout.Pane;

import java.util.function.Consumer;

public final class AnnotatedMapManager {
    private final Graph reseauRoutier;
    private final TileManager gestionnaireTuiles;
    private final Consumer<String> error;
    private final RouteBean itineraire;
    private final Pane pane;
    private final RouteManager routeManager;
    private final MapViewParameters mapParameters;
    private final int ZOOM_AT_START = 12;
    private final int X_AT_START = 543200;
    private final int Y_AT_START = 370650;
    private final ReadOnlyObjectProperty<MapViewParameters> readOnlyParameters;


    public AnnotatedMapManager(Graph reseauRoutier, TileManager gestionnaireTuiles, RouteBean itineraire, Consumer<String> error) {
        this.reseauRoutier = reseauRoutier;
        this.gestionnaireTuiles = gestionnaireTuiles;
        this.error = error;
        this.itineraire = itineraire;
        pane = new Pane();
        mapParameters = new MapViewParameters(ZOOM_AT_START, X_AT_START, Y_AT_START);
        readOnlyParameters = new SimpleObjectProperty<>(mapParameters);

        // Creation d'une Route manager
        routeManager = new RouteManager(itineraire, readOnlyParameters, error);

        //creation
    }


    public Pane pane() {
        return pane;
    }
}
