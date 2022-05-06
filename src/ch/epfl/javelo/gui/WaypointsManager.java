package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;
import java.util.function.Consumer;


public final class WaypointsManager {
    public final Graph reseauRoutier;
    public final ObjectProperty<MapViewParameters> parameters;
    public final ObservableList<Waypoint> listWaypoints;
    public final Consumer<String> error;
    private Pane pane;


    public WaypointsManager(Graph reseauRoutier, ObjectProperty<MapViewParameters> parameters, ObservableList<Waypoint> listWaypoints, Consumer<String> error){
        this.reseauRoutier = reseauRoutier;
        this.parameters = parameters;
        this.listWaypoints = listWaypoints;
        this.error = error;

        pane = new Pane();

        pane.setPickOnBounds(false);

        listWaypoints.addListener((ListChangeListener<? super Waypoint>) Observable -> {
            System.out.println("test 1 listener liswaypoint");
            ClearListWaypoints();
            CreateNewListWaypoints();

        });
    }

    public Pane pane(){
        return pane;
    }


    public void addWaypoint(double x, double y) {
        //waypoint a la position d'un node le plus proche ds 1000m a l'aide de node closest to avec distance 500m
        //si trouve pas ecrit l'erreur ds l'enonce
        //->nouveau pt ch avec cooordonnee d'un node trouve

        //double newX = x + MapViewParameters.topLeft().getX();
         Waypoint NewWaypoint = CreateNewWaypoint(x, y);
        if (NewWaypoint != null) {
            listWaypoints.add(NewWaypoint);
        }
        /*PointCh newPoint = parameters.get().pointAt(x, y).toPointCh();

        int nodeClosestId = reseauRoutier.nodeClosestTo(newPoint, 500);
        if (nodeClosestId == -1) {
            error.accept("Aucune route à proximité !");
        }
        else {
            listWaypoints.add(new Waypoint(newPoint, nodeClosestId));
        }*/
    }

    private Waypoint CreateNewWaypoint(double x, double y) {
        //************************** TEST *******************************
        System.out.println(x +"et "+y);
        //************************** TEST *******************************

        PointCh newPoint = parameters.get().pointAt(x, y).toPointCh();

        int nodeClosestId = reseauRoutier.nodeClosestTo(newPoint, 500);

        //************************** TEST *******************************
        System.out.println("node Id: " +nodeClosestId);
        //************************** TEST *******************************

        if (nodeClosestId == -1) {
            error.accept("Aucune route à proximité !");
        }
        else return new Waypoint(newPoint, nodeClosestId);
        return null;
    }


    private void CreateGroupPerWaypoint(){
            for (int i=0; i<listWaypoints.size(); ++i) {
                System.out.println(listWaypoints.get(i));
                DrawWaypoint(listWaypoints.get(i), i);
            }
    }


    private void DrawWaypoint(Waypoint w, int index){
        //creer un ensemble des groupes correspondants aux waypoints ?

        //************************** TEST *******************************
        System.out.println("test 3 index waypoint: " +index);
        //************************** TEST *******************************
        Group newGroup = new Group();
        pane().getChildren().add(newGroup);

        PointWebMercator point = PointWebMercator.ofPointCh(w.pointCh());
        double x = parameters.get().viewX(point);
        double y = parameters.get().viewY(point);

        //************************** TEST *******************************
        System.out.println("x " +x);
        System.out.println("y " +y);
        //************************** TEST *******************************

        newGroup.setOnMouseClicked(event -> {
            listWaypoints.remove(index);
            pane().getChildren().remove(newGroup);
        });

        //?? Ca sert a quoi au juste si tte facon on a pas acces a ces info en dehors
        /*newGroup.setOnMousePressed(event -> {
            if(event.isStillSincePress()) {
                double newX = event.getX();
                double newY = event.getY();
            }
        });*/

        newGroup.setOnMouseDragged(event -> {
            //newGroup.setLayoutX(event.getX());
            //ewGroup.setLayoutY(event.getY());

            newGroup.setTranslateX(event.getSceneX());
            newGroup.setTranslateY(event.getSceneY());
        });

        newGroup.setOnMouseReleased(event-> {
            if(event.isStillSincePress()) {
                listWaypoints.remove(index);
                pane().getChildren().remove(newGroup);
            }

            if(!event.isStillSincePress()){

                Waypoint waypointChanged = CreateNewWaypoint(event.getSceneX(), event.getSceneY());
                System.out.println(event.getSceneX() +event.getSceneY() );
                listWaypoints.set(index, waypointChanged);
                ClearListWaypoints();
                CreateNewListWaypoints();

            }
        });

        newGroup.setLayoutX(x);
        newGroup.setLayoutY(y);

        newGroup.getStyleClass().add("pin");

        SVGPath outline = new SVGPath();
        SVGPath interior = new SVGPath();

        outline.getStyleClass().add("pin_outside");
        interior.getStyleClass().add("pin_inside");

        outline.setContent("M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20");
        interior.setContent("M0-23A1 1 0 000-29 1 1 0 000-23");

        newGroup.getChildren().add(outline);
        newGroup.getChildren().add(interior);

        String position;

        if(index == 0) position = String.valueOf(Position.first);
        else {
            position = (index==listWaypoints.size() -1) ? String.valueOf(Position.last) :
                    String.valueOf(Position.middle);
        }
        newGroup.getStyleClass().add(position);
        }


    private void ClearListWaypoints() {
        pane().getChildren().clear();
    }

    private void CreateNewListWaypoints() {
        CreateGroupPerWaypoint();
        System.out.println("test 2 createnewlistwaypoint");
    }

    private enum Position {
        first, middle, last;
    }

    }




