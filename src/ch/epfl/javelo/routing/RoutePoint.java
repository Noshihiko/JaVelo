package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

/**
 * Représente le point d'un itinéraire le plus proche d'un point de référence donné,
 * qui se trouve dans le voisinage de l'itinéraire.
 *
 * @param point               le point sur l'itinéraire
 * @param position            la position du point le long de l'itinéraire, en mètres
 * @param distanceToReference la distance, en mètres, entre le point et la référence.
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */
public record RoutePoint(PointCh point, double position, double distanceToReference) {

    //Représente un point inexistant
    public static final RoutePoint NONE = new RoutePoint(null, Double.NaN, Double.POSITIVE_INFINITY);

    /**
     * Crée un point identique au récepteur (this) mais dont la position est décalée de la différence donnée,
     * qui peut être positive ou négative.
     *
     * @param positionDifference différence de position (négative ou positive)
     *
     * @return un point identique, mais dont la position est décalée.
     */
    public RoutePoint withPositionShiftedBy(double positionDifference) {
        return (positionDifference == 0) ? this : new RoutePoint(this.point, this.position + positionDifference, this.distanceToReference);
    }

    /**
     * Retourne un point d'itinéraire.
     *
     * @param that point d'itinéraire que l'on compare
     *
     * @return this si sa distance à la référence est inférieur ou égale à celle de that
     * sinon, that.
     */
    public RoutePoint min(RoutePoint that) {
        return ((this.distanceToReference <= that.distanceToReference) ? this : that);
    }

    /**
     * Retourne un point d'itinéraire.
     *
     * @param thatPoint               attribut pour le point that
     * @param thatPosition            attribut pour le point that
     * @param thatDistanceToReference attribut pour le point that
     *
     * @return this si sa distance à la référence est inférieure ou égale à thatDistanceToReference
     * sinon, une nouvelle instance de RoutePoint dont les attributs sont les arguments passés à min.
     */
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference) {
        return ((this.distanceToReference <= thatDistanceToReference) ? this : new RoutePoint(thatPoint, thatPosition, thatDistanceToReference));
    }
}