package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import javafx.scene.layout.Pane;


import java.util.Collection;
import java.util.function.Consumer;

public final class WaypointsManager {
    public final Graph reseauRoutier;
    public final ObjectProperty<MapViewParameters> parameters;
    public final ObservableList<Waypoint> listPoints;
    public final Consumer<String> error;
    private Pane pane;


    public WaypointsManager(Graph reseauRoutier, ObjectProperty<MapViewParameters> parameters, ObservableList<Waypoint> listPoints, Consumer<String> error){
        this.reseauRoutier = reseauRoutier;
        this.parameters = parameters;
        this.listPoints = listPoints;
        this.error = error;
        pane.setPickOnBounds(false);


        listPoints.addListener(observable -> lambdas);

    }



    public Pane pane(){
        return pane;
    }




    public void addWaypoint(double x, double y) {
        //waypoint a la position d'un node le plus proche ds 1000m a l'aide de node closest to avec distance 500m
        //si trouve pas ecrit l'erreur ds l'enonce
        //->nouveau pt ch avec cooordonnee d'un node trouve

        parameters.get().pointAt(x, y);
        PointCh newPoint = new PointCh(x, y);

        PointCh newPoint = PointWebMercator.of(parameters.get().zoom(),x,y).toPointCh();

        int nodeClosestId = reseauRoutier.nodeClosestTo(newPoint, 500);
        if (nodeClosestId == -1) {
            error.accept("Aucune route à proximité !");
        }
        else {
            listPoints.add(new Waypoint(newPoint, nodeClosestId));
        }


        //listener ds waypoint mnager a utiliser avec observable list et listener va dire ca a changer et effecteur ce qu'il ya  uìds le listener: en gros ca va dire que si les waypoint changent ca recalcule òle route
    }


    private void Path(){
            for (int i=0; i<listPoints.size(); ++i) {
                addGroup(listPoints.get(i));
            }
    }

    private void addGroup(Waypoint w){
        Group newGroup = new Group();
        pane().getChildren().add(newGroup);

        PointWebMercator point = PointWebMercator.ofPointCh(w.pointCh());
        double x = parameters.get().viewX(point);
        double y = parameters.get().viewY(point);

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

        int i = listPoints.indexOf(w);
        String position;

        if(i == 0) position = String.valueOf(Position.first);
        else {
            position = (i==listPoints.size() -1) ? String.valueOf(Position.last) :
                    String.valueOf(Position.middle);
        }
        newGroup.getStyleClass().add(position);
    }

    private enum Position {
        first, middle, last;
    }



}
