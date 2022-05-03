package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;

import ch.epfl.javelo.projection.WebMercator;
import javafx.geometry.Point2D;


public record MapViewParameters(int zoom, double x, double y) {
    private static PointWebMercator p;

    public MapViewParameters(int zoom, double x, double y){
        this.zoom = zoom;
        this.x = x;
        this.y = y;

        p = PointWebMercator.of(zoom, x, y);
    }

    public Point2D topLeft() {
        return new Point2D(x,y);
    }

    public MapViewParameters withMinXY(double x, double y){
        //retourne une instance de MapViewParameters identique au récepteur,
        //si ce n'est que les coordonnées du coin haut-gauche sont celles passées en arguments à la méthode
            return new MapViewParameters(this.zoom, x, y);
    }

    public PointWebMercator pointAt(double x, double y){
        //prend en arguments les coordonnées x et y d'un point,
        // exprimées par rapport au coin haut-gauche de la portion de carte affichée à l'écran
        // et retourne ce point sous la forme d'une instance de PointWebMercator
        x = WebMercator.lon(x + topLeft().getX());
        y = WebMercator.lat(y + topLeft().getY());
        return new PointWebMercator(x, y);
    }

    //prennent en argument un point Web Mercator et retournent la position x ou y correspondante,
    //exprimée par rapport au coin haut-gauche de la portion de carte affichée à l'écran
    public int viewX(PointWebMercator that){
        return (int) (that.x() - topLeft().getX());
    }

    public int viewY(PointWebMercator that){
        return (int) (that.y() - topLeft().getY());
    }

}
