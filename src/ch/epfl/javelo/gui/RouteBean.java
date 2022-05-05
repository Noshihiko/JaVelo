package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;

public final class RouteBean {
    //public ou pv?
    RouteComputer path;

    public ObservableList<Waypoint> waypoints;
    public DoubleProperty highlightedPosition;
    //pourquoi devons-nous les mettre en public si on ne veut pas qu'ils
    //soient accessibles depuis l'extérieur ?
    public ObjectProperty<Route> route;
    public ObjectProperty<ElevationProfile> elevationProfile;
    private int MAX_STEP_LENGTH = 5;

    public RouteBean(RouteComputer path){
        this.path = path;
        /*
        waypoints.addListener( o -> {

            elevationProfile.setValue(ElevationProfileComputer.elevationProfile(route.get(), MAX_STEP_LENGTH ));

L'itinéraire doivent être recalculés chaque fois
que les points de passage changent.
 Lors d'un changement, le meilleur itinéraire (simple)
reliant chaque point de passage à son successeur est déterminé
et ces itinéraires sont combinés en un unique itinéraire multiple.

Le calcul d'un itinéraire étant une opération coûteuse, il est
toutefois important d'éviter le recalcul d'itinéraires déjà
calculés. Pour ce faire, nous vous conseillons d'utiliser un petit
 cache mémoire représenté par une table associant à une paire de
  nœuds le meilleur itinéraire (simple) les reliant.

Chaque fois qu'un itinéraire (simple) doit être calculé, la table
est consultée, et si elle contient l'itinéraire, il en est
simplement extrait. Sinon, il est calculé au moyen du calculateur
d'itinéraire et le résultat inséré dans la table.

Bien entendu, il ne faut pas que la table grossisse de manière
incontrôlée, et nous vous conseillons donc soit de n'y stocker
que les itinéraires (simples) correspondant à l'itinéraire
(multiple) courant, soit de limiter sa taille en procédant de la
même manière que pour le cache mémoire des tuiles.
        } );

         */


    }

    public DoubleProperty highlightedPositionProperty(){
        return this.highlightedPosition;
    }

    public double highlightedPosition(){
        return highlightedPosition.doubleValue();
    }

    public void setHighlightedPosition(double newValue){
        highlightedPosition.setValue(newValue);
    }

    public ReadOnlyObjectProperty<Route> castRoute(ObjectProperty<Route> route){
        return this.route = route;
    }

    public ReadOnlyObjectProperty<ElevationProfile> castElevationProfile(ObjectProperty<ElevationProfile> elevationProfile){
        return this.elevationProfile = elevationProfile;
    }
}
