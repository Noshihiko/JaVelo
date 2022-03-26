package ch.epfl.javelo.projection;

import static ch.epfl.javelo.Math2.asinh;
import static java.lang.Math.PI;
import static java.lang.Math.tan;
import static java.lang.Math.atan;
import static java.lang.Math.sinh;

/**
 * Convertit entre les coordonnées WGS 84 et les coordonnées Web Mercator
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */
public final class WebMercator {
    private WebMercator() {
    }

    /**
     * calcule de la coordonnée x de la projection d'un point grâce à sa longitude
     *
     * @param lon la longitude du point en radians
     * @return la coordonnée x de la projection d'un point se trouvant à la longitude lon
     */
    public static double x(double lon) {
        return (lon + PI) / (2d * PI);
    }

    /**
     * calcule de la coordonnée y de la projection d'un point grâce à sa latitude
     *
     * @param lat la latitude du point en radians
     * @return la coordonnée y de la projection d'un point se trouvant à la latitude lat
     */
    public static double y(double lat) {
        double a = tan(lat);
        return (PI - asinh(a)) / (2d * PI);
    }

    /**
     * calcule la longitude, en radians, d'un point selon sa coordonnée x
     *
     * @param x coordonnée x du point
     * @return la longitude d'un point dont la projection se trouve à la coordonnée x donnée
     */
    public static double lon(double x) {
        return 2d * PI * x - PI;
    }

    /**
     * calcule la latitude, en radians, d'un point selon sa coordonnée y
     *
     * @param y coordonnée y du point
     * @return la latitude d'un point dont la projection se trouve à la coordonnée y donnée
     */
    public static double lat(double y) {
        return atan(sinh(PI - 2d * PI * y));
    }
}