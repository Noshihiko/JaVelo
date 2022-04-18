package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;

import java.awt.geom.Point2D;


public record MapViewParameters(int zoom, double x, double y) {
    private static PointWebMercator p;

    public MapViewParameters(int zoom, double x, double y){
        this.zoom = zoom;
        this.x = x;
        this.y = y;

        p = PointWebMercator.of(zoom, x, y);
    }

    public Point2D topLeft() {
        return new Point2D() {

            @Override
            public double getX() {
                return Math.toDegrees(p.lon());
            }

            @Override
            public double getY() {
                return Math.toDegrees(p.lat());
            }

            @Override
            public void setLocation(double x1, double y1) {
               //   = x1;
               //   = y1;
            }
        };
    }

    public MapViewParameters withMinXY(double x, double y){
        //retourne une instance de MapViewParameters identique au récepteur,
        //si ce n'est que les coordonnées du coin haut-gauche sont celles passées en arguments à la méthode
        if ((this.x == x)&&(this.y == y)) {
            return new MapViewParameters(this.zoom, x, y);
        } else {
            return null;
        }
    }

    public PointWebMercator pointAt(double x, double y){
        //prend en arguments les coordonnées x et y d'un point,
        // exprimées par rapport au coin haut-gauche de la portion de carte affichée à l'écran
        // et retourne ce point sous la forme d'une instance de PointWebMercator
        return PointWebMercator.of(this.zoom, x, y);
    }


    //prennent en argument un point Web Mercator et retournent la position x ou y correspondante,
    //exprimée par rapport au coin haut-gauche de la portion de carte affichée à l'écran
    public int viewX(PointWebMercator that){
        return (int) that.x() / (256 * this.zoom);
        //ou alors juste par 256 ou alors par le x du coin haut-gauche ?? jsp go demander
    }

    public int viewY(PointWebMercator that){
        return (int) that.y() / (256 * this.zoom);
    }

}
