package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.GpxGenerator;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.function.Consumer;

public final class JaVelo extends Application {
    private final String PATH_GRAPH  = "javelo-data";
    private final String PATH_CACHE_TILES = "osm-cache";
    private final String PATH_HOST_TILES = "tile.openstreetmap.org";
    private final String TITLE = "JaVelo";
    private final String TITLE_ITINERAIRE = "javelo.gpx";

    public static void main(String[] v02) { launch(v02);

    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        ErrorManager errorManager = new ErrorManager();
        Consumer<String> errorConsumer = errorManager::displayError;

        SplitPane carteAndProfil =  new SplitPane();
        carteAndProfil.setOrientation(Orientation.VERTICAL);

        StackPane carteProfilAndError = new StackPane(carteAndProfil, errorManager.pane());

        MenuItem menuItem = new MenuItem("Exporter GPX");
        Menu menu = new Menu("Fichier");
        MenuBar menuBar = new MenuBar(menu);

        TileManager tileManager = new TileManager(Path.of(PATH_HOST_TILES), PATH_CACHE_TILES);

        Graph graph = Graph.loadFrom(Path.of(PATH_GRAPH));
        CityBikeCF costFunction = new CityBikeCF(graph);

        RouteComputer routeComputer = new RouteComputer(graph, costFunction);
        RouteBean bean = new RouteBean(routeComputer);
        ElevationProfileManager elevationProfile;

        AnnotatedMapManager map = new AnnotatedMapManager(graph, tileManager, bean, errorConsumer);

        BorderPane borderPane = new BorderPane(carteProfilAndError, menuBar,null, null, null);
        borderPane.setId(TITLE);
        borderPane.setMinSize(800, 600);

        carteAndProfil.getItems().add(map.pane());


        if (bean.getRouteProperty() == null)  {
           // carteAndProfil.getItems().remove(elevationProfile.pane());
            elevationProfile = null;
            carteProfilAndError.getChildren().remove(errorManager.pane());
        } else {
            elevationProfile =  new ElevationProfileManager(bean.getElevationProfileProperty(), bean.highlightedPositionProperty());
            carteAndProfil.getItems().add(elevationProfile.pane());

        }
        carteAndProfil.setResizableWithParent(elevationProfile.pane(), false);

        bean.highlightedPositionProperty().bind(Bindings.when(map.mousePositionOnRouteProperty()
                        .greaterThanOrEqualTo(0)).then(map.mousePositionOnRouteProperty())
                .otherwise(elevationProfile.mousePositionOnProfileProperty()));

        menuItem.disableProperty().bind(bean.getRouteProperty().isNull());
        menu.getItems().add(menuItem);


        bean.getRouteProperty().addListener( o -> {
            if (bean.getRouteProperty().isNull().get()) {

            } else {

            }
        });


        menu.setOnAction( o -> {
            try {
                GpxGenerator.writeGpx(TITLE_ITINERAIRE, bean.getRouteProperty().get(), bean.getElevationProfileProperty().get());
            } catch( java.io.IOException e){
                throw new UncheckedIOException(e);
            }
        });


    }
}
