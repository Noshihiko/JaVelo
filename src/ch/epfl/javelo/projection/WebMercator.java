package ch.epfl.javelo.projection;

import static ch.epfl.javelo.Math2.asinh;
import static java.lang.Math.*;

/**
 * Convertit entre les coordonnées WGS 84 et les coordonnées Web Mercator.
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */
public final class WebMercator {
    private static final double TAU = 2d * PI;
    private WebMercator() {}

    /**
     * Calcule la coordonnée x de la projection d'un point grâce à sa longitude.
     *
     * @param lon la longitude du point en radians
     * @return la coordonnée x de la projection d'un point se trouvant à la longitude lon.
     */
    public static double x(double lon) {
        return (lon + PI) / (TAU);
    }

    /**
     * Calcule de la coordonnée y de la projection d'un point grâce à sa latitude.
     *
     * @param lat la latitude du point en radians
     * @return la coordonnée y de la projection d'un point se trouvant à la latitude lat.
     */
    public static double y(double lat) {
        return ( PI - asinh(tan(lat)) ) / (TAU);
    }

    /**
     * Calcule la longitude, en radians, d'un point selon sa coordonnée x.
     *
     * @param x la coordonnée x du point
     * @return la longitude d'un point dont la projection se trouve à la coordonnée x donnée.
     */
    public static double lon(double x) {
        return (TAU * x) - PI;
    }

    /**
     * Calcule la latitude, en radians, d'un point selon sa coordonnée y.
     *
     * @param y la coordonnée y du point
     * @return la latitude d'un point dont la projection se trouve à la coordonnée y donnée.
     */
    public static double lat(double y) {
        return atan( sinh(PI - (TAU * y)) );
    }
}