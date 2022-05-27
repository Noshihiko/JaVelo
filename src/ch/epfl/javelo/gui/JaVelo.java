package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.GpxGenerator;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.nio.file.Path;

public final class JaVelo extends Application {
    private final String PATH_GRAPH  = "javelo-data";
    private final String PATH_CACHE_TILES = "osm-cache";
    private final String PATH_HOST_TILES = "tile.openstreetmap.org";
    private final String TITLE = "JaVelo";

    private Graph graph;
    private CityBikeCF costFunction;
    private RouteBean bean;
    private RouteComputer routeComputer;
    private TileManager tileManager;
    private ElevationProfileManager profile;

    private ErrorManager errorManager = new ErrorManager();
    private AnnotatedMapManager map = new AnnotatedMapManager(graph, tileManager, bean, errorManager.);


    private SplitPane carteAndProfil =  new SplitPane();

    private MenuItem menuItem = new MenuItem("Exporter GPX");
    private Menu menu = new Menu("Fichier");
    private MenuBar menuBar = new MenuBar(menu);


    private StackPane carteProfilAndError = new StackPane(carteAndProfil, errorManager.pane());
    private BorderPane borderPane;


    //construire l'interface graphique finale en combinant les parties gérées par les classes écrites précédemment et en
    // y ajoutant le menuBar très simple présenté plus haut
    @Override
    public void start(Stage primaryStage) throws Exception {
        graph = Graph.loadFrom(Path.of(PATH_GRAPH));
        costFunction = new CityBikeCF(graph);

        routeComputer = new RouteComputer(graph, costFunction);
        bean = new RouteBean(routeComputer);

        carteAndProfil.setOrientation(Orientation.VERTICAL);
        carteAndProfil.setResizableWithParent(profile.pane(), false);

        //todo
        // Le panneau contenant le message d'erreur est généralement totalement transparent,
        // donc invisible, sauf en cas d'erreur.

        if (bean.getRouteProperty() == null) {
            carteProfilAndError.getChildren().remove(errorManager.pane());
        }

        menuItem.disableProperty().bind(bean.getRouteProperty().isNull());
        menu.getItems().add(menuItem);

        menu.setOnAction( o -> {
            try {
                GpxGenerator.writeGpx();
            } catch(){

            }
        });

        bean.highlightedPositionProperty().bind(o -> {
            if (map.mousePositionOnRouteProperty().get() >=0) {
                bean.setHighlightedPosition(map.mousePositionOnRouteProperty().get());
            } else {
                bean.setHighlightedPosition(profile.mousePositionOnProfileProperty().get());
            }
        });
        borderPane = new BorderPane(carteProfilAndError, menuBar,null, null, null);
        borderPane.setMinSize(800, 600);
    }
}
