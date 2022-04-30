package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.function.Consumer;

public final class WaypointsManager {
    public final Graph reseauRoutier;
    public final ObjectProperty<MapViewParameters> parameters;
    public final ObservableList<Waypoint> listPoints;
    public final Consumer<String> error;

    listPoints.addListener(new ListChangeListener() {
        @Override
        public void onChanged(ListChangeListener.Change change) {
            System.out.println("change!");
        }
    }

    public WaypointsManager(Graph reseauRoutier, ObjectProperty<MapViewParameters> parameters, ObservableList<Waypoint> listPoints, Consumer<String> error){
        this.reseauRoutier = reseauRoutier;
        this.parameters = parameters;
        this.listPoints = listPoints;
        this.error = error;
    }

    public listPoint.listener()

    public pane(){

    }

    public void addWaypoint(double x, double y) {
        //waypoint a la position d'un node le plus proche ds 1000m a l'aide de node closest to avec distance 500m
        //si trouve pas ecrit l'erreur ds l'enonce
        //->nouveau pt ch avec cooordonnee d'un node trouve
        PointCh newPoint = new PointCh(x, y);
        int nodeClosestId = reseauRoutier.nodeClosestTo(newPoint, 500);
        if (nodeClosestId == -1) {
            error.accept("Aucune route à proximité !");
        }
        else {
            listPoints.add(new Waypoint(newPoint, nodeClosestId));
        }

        //listener ds waypoint mnager a utiliser avec observable list et listener va dire ca a changer et effecteur ce qu'il ya  uìds le listener: en gros ca va dire que si les waypoint changent ca recalcule òle route
    }
}
