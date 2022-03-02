package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

/**
 * Représente un point dans le système de coordonnées suisse.
 *
 * @author Camille Espieux (324248)
 *
 */
public record PointCh(double e, double n) {
    public PointCh{
        Preconditions.checkArgument(SwissBounds.containsEN(e,n));
    }

    /**
     * Calcule le carré de la distance en mètres séparant le récepteur (this) de l'argument that.
     *
     * @param that
     *          l'argument
     * @return le carré de la distance en mètres séparant le récepteur de l'argument.
     */
    public double squaredDistanceTo(PointCh that){
        double x = that.e - e;
        double y = that.n - n;
        return Math2.squaredNorm(x,y);
    }

    /**
     * Calcule la distance en mètres séparant le récepteur (this) de l'argument that.
     *
     * @param that
     *          l'argument
     * @return la distance en mètres séparant le récepteur de l'argument.
     */
    public double distanceTo(PointCh that){
        return Math.sqrt(squaredDistanceTo(that));
    }

    /**
     * Calcule la longitude du point, dans le système WGS84, en radians.
     *
     * @return la longitude du point, dans le système WGS84, en radians.
     */
    public double lon(){
        return Ch1903.lon(e,n);
    }

    /**
     * Calcule la latitude du point, dans le système WGS84, en radians.
     *
     * @return la latitude du point, dans le système WGS84, en radians.
     */
    public double lat(){
        return Ch1903.lat(e,n);
    }
}

