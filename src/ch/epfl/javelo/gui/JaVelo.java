package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.GpxGenerator;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.function.Consumer;


/**
 * Classe principale de l'application.
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */

public final class JaVelo extends Application {
    private final static String PATH_GRAPH = "javelo-data";
    private final static String PATH_CACHE_TILES = "osm-cache";
    private final static String PATH_HOST_TILES = "tile.openstreetmap.org";
    private final static String TITLE = "JaVelo";
    private final static String TITLE_ITINERARY = "javelo.gpx";
    private final static String MAP_STYLE_SHEET = "map.css";
    private final static int MINIMUM_WINDOW_WIDTH = 800;
    private final static int MINIMUM_WINDOW_HEIGHT = 600;
    private final static String DOCUMENT_MENU_NAME = "Fichier";
    private final static String EXPORTATION_MENU_NAME = "Exporter GPX";

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        ErrorManager errorManager = new ErrorManager();
        Consumer<String> errorConsumer = errorManager::displayError;

        SplitPane mapAndProfile = new SplitPane();
        mapAndProfile.setOrientation(Orientation.VERTICAL);

        StackPane mapProfileAndError = new StackPane(mapAndProfile, errorManager.pane());

        Menu menu = new Menu(DOCUMENT_MENU_NAME);
        MenuItem menuItem = new MenuItem(EXPORTATION_MENU_NAME);
        MenuBar menuBar = new MenuBar(menu);

        TileManager tileManager = new TileManager(Path.of(PATH_CACHE_TILES), PATH_HOST_TILES);

        Graph graph = Graph.loadFrom(Path.of(PATH_GRAPH));
        CityBikeCF costFunction = new CityBikeCF(graph);

        RouteComputer routeComputer = new RouteComputer(graph, costFunction);
        RouteBean bean = new RouteBean(routeComputer);
        ElevationProfileManager elevationProfile = new ElevationProfileManager(bean.getElevationProfileProperty(), bean.highlightedPositionProperty());

        AnnotatedMapManager mapManager = new AnnotatedMapManager(graph, tileManager, bean, errorConsumer);

        BorderPane mainPane = new BorderPane(mapProfileAndError, menuBar, null, null, null);

        mapAndProfile.getItems().add(mapManager.pane());

        bean.getRouteProperty().addListener((p, oldRoute, newRoute) -> {
            if (newRoute == null) {
                mapAndProfile.getItems().remove(elevationProfile.pane());
            } else {
                if (oldRoute == null)
                    mapAndProfile.getItems().add(elevationProfile.pane());
            }
        });
        SplitPane.setResizableWithParent(elevationProfile.pane(), false);

        bean.highlightedPositionProperty().bind(Bindings.when(mapManager.mousePositionOnRouteProperty()
                        .greaterThanOrEqualTo(0)).then(mapManager.mousePositionOnRouteProperty())
                .otherwise(elevationProfile.mousePositionOnProfileProperty()));

        menuItem.disableProperty().bind(bean.getRouteProperty().isNull());
        menu.getItems().add(menuItem);


        menu.setOnAction(o -> {
            try {
                GpxGenerator.writeGpx(TITLE_ITINERARY, bean.getRouteProperty().get(), bean.getElevationProfileProperty().get());
            } catch (java.io.IOException e) {
                throw new UncheckedIOException(e);
            }
        });
        mainPane.getStylesheets().add(MAP_STYLE_SHEET);

        primaryStage.setMinWidth(MINIMUM_WINDOW_WIDTH);
        primaryStage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.setTitle(TITLE);
        primaryStage.show();
    }
}
