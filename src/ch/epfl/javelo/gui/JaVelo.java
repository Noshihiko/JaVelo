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

    private Graph graph;
    private CityBikeCF costFunction;
    private RouteBean bean;
    private RouteComputer routeComputer;
    private TileManager tileManager;
    private ElevationProfileManager profile;
    private Consumer<String> errorConsumer;

    private ErrorManager errorManager;
    private AnnotatedMapManager map;


    private SplitPane carteAndProfil;

    private MenuItem menuItem;
    private Menu menu;
    private MenuBar menuBar;


    private StackPane carteProfilAndError;
    private BorderPane borderPane;



    //construire l'interface graphique finale en combinant les parties gérées par les classes écrites précédemment et en
    // y ajoutant le menuBar très simple présenté plus haut
    @Override
    public void start(Stage primaryStage) throws Exception {
        errorManager = new ErrorManager();
        carteAndProfil =  new SplitPane();

        menuItem = new MenuItem("Exporter GPX");
        menu = new Menu("Fichier");
        menuBar = new MenuBar(menu);
        carteProfilAndError = new StackPane(carteAndProfil, errorManager.pane());

        graph = Graph.loadFrom(Path.of(PATH_GRAPH));
        costFunction = new CityBikeCF(graph);

        routeComputer = new RouteComputer(graph, costFunction);
        bean = new RouteBean(routeComputer);

        map = new AnnotatedMapManager(graph, tileManager, bean, errorConsumer);

        carteAndProfil.setOrientation(Orientation.VERTICAL);
        carteAndProfil.setResizableWithParent(profile.pane(), false);


        if (bean.getRouteProperty() == null) {
            carteProfilAndError.getChildren().remove(errorManager.pane());
        }

        menuItem.disableProperty().bind(bean.getRouteProperty().isNull());
        menu.getItems().add(menuItem);

        menu.setOnAction( o -> {
            try {
                GpxGenerator.writeGpx(TITLE_ITINERAIRE, bean.getRouteProperty().get(), bean.getElevationProfileProperty().get());
            } catch( java.io.IOException e){
                throw new UncheckedIOException(e);
            }
        });

        bean.highlightedPositionProperty().bind(Bindings.when(map.mousePositionOnRouteProperty()
                .greaterThanOrEqualTo(0)).then(map.mousePositionOnRouteProperty())
                .otherwise(profile.mousePositionOnProfileProperty()));


        borderPane = new BorderPane(carteProfilAndError, menuBar,null, null, null);
        borderPane.setMinSize(800, 600);
    }
}
