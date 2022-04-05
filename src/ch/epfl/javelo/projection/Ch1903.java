package ch.epfl.javelo.projection;

import static java.lang.Math.pow;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

/**
 * Permet de convertir les coordonnées WGS 84 en coordonnées suisses et vice versa.
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */

public final class Ch1903 {
    // Constructeur privé pour rendre la classe non instanciable.
    private Ch1903() {}

    /**
     * Convertit la coordonnée Est du point de longitude lon et latitude lat dans le système WGS84.
     *
     * @param lon la longitude du point
     * @param lat la latitude du point
     *
     * @return la coordonnée Est du point de longitude lon et latitude lat dans le système WGS84.
     */
    public static double e(double lon, double lat) {
        double lon1 = pow(10, -4) * (3_600 * toDegrees(lon) - 26_782.5);
        double lat1 = pow(10, -4) * (3_600 * toDegrees(lat) - 169_028.66);

        return 2_600_072.37 + (211_455.93 * lon1) - (10_938.51 * lon1 * lat1) - (0.36 * lon1 * lat1 * lat1)
                - (44.54 * lon1 * lon1 * lon1);
    }

    /**
     * Convertit la coordonnée Nord du point de longitude lon et latitude lat dans le système WGS84.
     *
     * @param lon la longitude du point
     * @param lat la latitude du point
     *
     * @return la coordonnée Nord du point de longitude lon et latitude lat dans le système WGS84.
     */
    public static double n(double lon, double lat) {
        double lon1 = pow(10, -4) * (3_600 * toDegrees(lon) - 26_782.5);
        double lat1 = pow(10, -4) * (3_600 * toDegrees(lat) - 169_028.66);

        return 1_200_147.07 + (308_807.95 * lat1) + (3_745.25 * lon1 * lon1) + (76.63 * lat1 * lat1)
                - (194.56 * lat1 * lon1 * lon1) + (119.79 * lat1 * lat1 * lat1);
    }

    /**
     * Convertit les coordonnées du système WGS84 d'un point en sa longitude.
     *
     * @param e la coordonnée selon l'Est
     * @param n la coordonnée selon le Nord
     *
     * @return la longitude dans le système WGS84 du point dont les coordonnées sont e et n dans le système suisse.
     */
    public static double lon(double e, double n) {
        double x = pow(10, -6) * (e - 2_600_000);
        double y = pow(10, -6) * (n - 1_200_000);
        double lon1 = 2.677_909_4 + (4.728_982 * x) + (0.791_484 * x * y) + (0.130_6 * x * y * y) - (0.043_6 * x * x * x);

        return toRadians(lon1 * 100 / 36);
    }

    /**
     * Convertit les coordonnées du système WGS84 d'un point en sa latitude.
     *
     * @param e
     * la coordonnée selon l'Est
     * @param n la coordonnée selon le Nord
     *
     * @return la latitude dans le système WGS84 du point dont les coordonnées sont e et n dans le système suisse.
     */
    public static double lat(double e, double n) {
        double x = pow(10, -6) * (e - 2_600_000);
        double y = pow(10, -6) * (n - 1_200_000);
        double lat1 = 16.902_389_2 + (3.238_272 * y) - (0.270_978 * x * x) - (0.002_528 * y * y)
                - (0.044_7 * y * x * x) - (0.014 * y * y * y);

        return toRadians(lat1 * 100 / 36);
    }
}