package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

import static ch.epfl.javelo.Math2.squaredNorm;
import static java.lang.Math.sqrt;

/**
 * Représente un point dans le système de coordonnées suisse.
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */
public record PointCh(double e, double n) {

    /**
     * Constructeur public de l'enregistrement PointCh
     *
     * @param e la coordonnée est du point
     * @param n la coordonnée nord du point
     * @throws IllegalArgumentException si les coordonnées fournies ne sont pas dans les limites de la Suisse,
     * définies par SwissBounds.
     */
    public PointCh {
        Preconditions.checkArgument(SwissBounds.containsEN(e, n));
    }

    /**
     * Calcule le carré de la distance en mètres séparant le récepteur (this) de l'argument that.
     *
     * @param that l'argument
     * @return le carré de la distance en mètres séparant le récepteur de l'argument.
     */
    public double squaredDistanceTo(PointCh that) {
        return squaredNorm((that.e - e), (that.n - n));
    }

    /**
     * Calcule la distance en mètres séparant le récepteur (this) de l'argument that.
     *
     * @param that l'argument
     * @return la distance en mètres séparant le récepteur de l'argument.
     */
    public double distanceTo(PointCh that) { return sqrt(squaredDistanceTo(that)); }

    /**
     * Calcule la longitude du point, dans le système WGS84, en radians.
     * @return la longitude du point, dans le système WGS84, en radians.
     */
    public double lon() {
        return Ch1903.lon(e, n);
    }

    /**
     * Calcule la latitude du point, dans le système WGS84, en radians.
     * @return la latitude du point, dans le système WGS84, en radians.
     */
    public double lat() {
        return Ch1903.lat(e, n);
    }
}