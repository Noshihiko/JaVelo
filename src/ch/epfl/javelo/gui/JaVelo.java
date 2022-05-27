package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
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

    private SplitPane carteAndProfil =  new SplitPane();

    private MenuItem menuItem = new MenuItem("Exporter GPX");
    private Menu menu = new Menu("Fichier");
    private MenuBar menuBar = new MenuBar(menu);

    private Pane map = new Pane();
    private Pane profil = new Pane ();
    private Pane error = new Pane();
    private StackPane carteProfilAndError = new StackPane(carteAndProfil, error);
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
        carteAndProfil.setResizableWithParent(profil, false);

        //todo
        // Le panneau contenant le message d'erreur est généralement totalement transparent,
        // donc invisible, sauf en cas d'erreur.

        if (bean.getRouteProperty() == null) {
            carteProfilAndError.getChildren().remove(error);
        }

        menuItem.disableProperty().bind(bean.getRouteProperty().isNull());
        menu.getItems().add(menuItem);

        menu.setOnAction( o -> {
            try {

            }
        });

        bean.highlightedPositionProperty().bind(o -> {
            if (AnnotatedMapManager.mousePositionOnRouteProperty()) {
                bean.setHighlightedPosition();
            }
        });
        borderPane = new BorderPane(carteProfilAndError, menuBar,null, null, null);
        borderPane.setMinSize(800, 600);
    }
}
