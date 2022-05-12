package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.function.Consumer;

import static javafx.application.Application.launch;

public class Stage9Test extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Graph graph = Graph.loadFrom(Path.of("lausanne"));
        Path cacheBasePath = Path.of(".");
        String tileServerHost = "tile.openstreetmap.org";
        TileManager tileManager = new TileManager(cacheBasePath, tileServerHost);
        RouteBean routeBean = new RouteBean(new RouteComputer(graph, new CityBikeCF(graph)));//change

        MapViewParameters mapViewParameters = new MapViewParameters(12, 543200, 370650);
        ObjectProperty<MapViewParameters> mapViewParametersP = new SimpleObjectProperty<>(mapViewParameters);


        ObservableList<Waypoint> waypointObservableList = FXCollections.observableArrayList(
                new Waypoint(new PointCh(2532697, 1152350), 159049),
                new Waypoint(new PointCh(2538659, 1154350), 117669));
        routeBean.setHighlightedPosition(500);//change

        System.out.println("waypointObservableList size: "+waypointObservableList.size());
        for (int i =0;i<waypointObservableList.size();i++) {
            routeBean.setWaypoint(waypointObservableList.get(i));
        }
        ObservableList<Waypoint> waypoints = routeBean.getWaypoint();//Change
        System.out.println("****"+waypoints);


        Consumer<String> errorConsumer = new ErrorConsumer();
        RouteManager routeManager = new RouteManager(routeBean, mapViewParametersP, errorConsumer);//route
        WaypointsManager waypointsManager;
        waypointsManager = new WaypointsManager(graph, mapViewParametersP, waypoints, errorConsumer);
        System.out.println("% "+waypoints);


        BaseMapManager baseMapManager = new BaseMapManager(tileManager, waypointsManager, mapViewParametersP);


        StackPane mainPane = new StackPane(baseMapManager.pane(),
                waypointsManager.pane(), routeManager.pane());//change
        mainPane.getStylesheets().add("map.css");
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(300);
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();


    }

    private static final class ErrorConsumer implements Consumer<String> {
        @Override
        public void accept(String s) {
            System.out.println(s);
        }
    }
}