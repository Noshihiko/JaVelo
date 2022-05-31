package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;

import javafx.geometry.Point2D;

/**
 * Enregistrement qui représente les paramètres du fond de carte présenté dans l'interface graphique.
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */
public record MapViewParameters(int zoom, double x, double y) {

    /**
     * Retourne les coordonnées du coin haut-gauche sous la forme d'un objet de type Point2D.
     *
     * @return les coordonnées du coin haut-gauche sous la forme d'un objet de type Point2D
     */
    public Point2D topLeft() {
        return new Point2D(x, y);
    }

    /**
     * Retourne une instance de MapViewParameters identique au récepteur, si ce n'est que les coordonnées du coin haut-gauche
     * sont celles passées en arguments à la méthode.
     *
     * @param x la coordonnée x d'un point
     * @param y la coordonnée y d'un point
     * @return une instance de MapVieuxParameters où le coin en haut à gauche se situera au niveau du point dont les
     * coordonnées sont données en argument de la méthode
     */
    public MapViewParameters withMinXY(double x, double y) {
        return new MapViewParameters(this.zoom, x, y);
    }

    /**
     * Retourne le point, dont les coordonnées sont données en argument de la méthode, sous la forme d'une instance de PointWebMercator.
     *
     * @param x la coordonnée x d'un point, exprimée par rapport au coin haut-gauche de la portion de carte affichée à l'écran
     * @param y la coordonnée y d'un point, exprimée par rapport au coin haut-gauche de la portion de carte affichée à l'écran
     * @return ce point sous la forme d'une instance de PointWebMercator
     */
    public PointWebMercator pointAt(double x, double y) {
        return PointWebMercator.of(this.zoom, x + this.x, y + this.y);
    }

    /**
     * Retourne la position x correspondante, exprimée par rapport au coin haut-gauche de la portion de carte affichée à l'écran.
     *
     * @param that un point Web Mercator
     * @return la position x correspondante, exprimée par rapport au coin haut-gauche de la portion de carte affichée à l'écran
     */
    public int viewX(PointWebMercator that) {
        return (int) (that.xAtZoomLevel(this.zoom) - topLeft().getX());
    }

    /**
     * Retourne la position y correspondante, exprimée par rapport au coin haut-gauche de la portion de carte affichée à l'écran.
     *
     * @param that un point Web Mercator
     * @return la position y correspondante, exprimée par rapport au coin haut-gauche de la portion de carte affichée à l'écran
     */
    public int viewY(PointWebMercator that) {
        return (int) (that.yAtZoomLevel(this.zoom) - topLeft().getY());
    }
}
