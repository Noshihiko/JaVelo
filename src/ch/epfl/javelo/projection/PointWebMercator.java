package ch.epfl.javelo.projection;

import static ch.epfl.javelo.Preconditions.checkArgument;
import static java.lang.Math.scalb;

/**
 * Représente un point dans le système Web Mercator.
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */
public record PointWebMercator(double x, double y) {
    private static final int MAP_PIXEL = 8;

    /**
     * Constructeur public compact de l'enregistrement PointWebMercator.
     *
     * @param x la coordonnée x du point
     * @param y la coordonnée y du point
     * @throws IllegalArgumentException si les coordonnées ne sont pas comprises dans l'intervalle [0;1]
     */
    public PointWebMercator {
        checkArgument((x >= 0 && x <= 1) && (y >= 0 && y <= 1));
    }

    /**
     * Convertit les coordonnées x et y du point en fonction du niveau de zoom.
     *
     * @param zoomLevel le niveau de zoom
     * @param x         la coordonnée x du point
     * @param y         la coordonnée y du point
     *
     * @return le point dont les coordonnées sont x et y au niveau de zoom zoomLevel.
     */
    public static PointWebMercator of(int zoomLevel, double x, double y) {
        x = scalb(x, -zoomLevel - MAP_PIXEL);
        y = scalb(y, -zoomLevel - MAP_PIXEL);

        return new PointWebMercator(x, y);
    }

    /**
     * Convertit le point du système de coordonnées suisse donné en un point Web Mercator.
     *
     * @param pointCh le point dans le système de coordonnées suisse
     * @return le point Web Mercator correspondant au point du système de coordonnées suisse donné.
     */
    public static PointWebMercator ofPointCh(PointCh pointCh) {
        return new PointWebMercator(WebMercator.x(pointCh.lon()), WebMercator.y(pointCh.lat()));
    }

    /**
     * Convertit la coordonnée x d'un point en fonction du niveau de zoom.
     *
     * @param zoomLevel le niveau de zoom
     * @return la coordonnée x au niveau de zoom donné.
     */
    public double xAtZoomLevel(int zoomLevel) {
        return scalb(x, zoomLevel + MAP_PIXEL);
    }

    /**
     * Convertit la coordonnée y d'un point en fonction du niveau de zoom.
     *
     * @param zoomLevel le niveau de zoom
     * @return la coordonnée y au niveau de zoom donné.
     */
    public double yAtZoomLevel(int zoomLevel) {
        return scalb(y, zoomLevel + MAP_PIXEL);
    }

    /**
     * Calcule la longitude du point en radian.
     * @return la longitude du point en radian.
     */
    public double lon() {
        return WebMercator.lon(this.x);
    }

    /**
     * Calcule la latitude du point en radian.
     * @return la latitude du point.
     */
    public double lat() {
        return WebMercator.lat(this.y);
    }

    /**
     * Calcule le point de coordonnées suisses se trouvant à la même position que le récepteur.
     * @return le point de coordonnées suisses se trouvant à la même position que le récepteur,
     *  si ce point n'est pas dans les limites de la Suisse définies par SwissBounds, retourne null.
     */
    public PointCh toPointCh() {
        double lonSB = Ch1903.e(this.lon(), this.lat());
        double latSB = Ch1903.n(this.lon(), this.lat());

        if (SwissBounds.containsEN(lonSB, latSB)) {
            double lonWM = WebMercator.lon(this.x);
            double latWM = WebMercator.lat(this.y);

            return new PointCh(Ch1903.e(lonWM, latWM), Ch1903.n(lonWM, latWM));
        } else {
            return null;
        }
    }
}