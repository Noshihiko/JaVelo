package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import java.util.List;

/**
 * Représente un itinéraire
 *
 *  @author Camille Espieux (324248)
 *  @author Chiara Freneix (329552)
 *
 */
public interface Route {

    /**
     * Calcule l'index du segment à la position donnée (en mètres)
     *
     * @param position
     *          la position du segment pour calculer son index
     *
     * @return l'index du segment à la position donnée (en mètres)
     */
    abstract int indexOfSegmentAt(double position);

    /**
     * @return la longueur de l'itinéraire, en mètres
     */
    abstract double length();

    /**

     * @return la totalité des arêtes de l'itinéraire
     */
    abstract List<Edge> edges();

    /**
     *
     * @return la totalité des points situés aux extrémités des arêtes de l'itinéraire
     */
    abstract List<PointCh> points();

    /**
     * Cherche le point correspondant à une position donnée
     *
     * @param position
     *
     * @return le point se trouvant à la position donnée le long de l'itinéraire
     */
    abstract PointCh pointAt(double position);

    /**
     * Cherche l'altitude à la position donnée
     *
     * @param position
     * @return l'altitude à la position donnée le long de l'itinéraire
     */
    abstract double elevationAt(double position);

    /**
     * Donne l'identité du nœud le plus proche de la position donnée
     *
     * @param position
     * @return l'identité du nœud appartenant à l'itinéraire et se trouvant le plus proche de la position donnée
     */
    abstract int nodeClosestTo(double position);

    /**
     * Cherche le point le plus proche d'un autre point
     *
     * @param point
     *          point de référence
     * @return le point de l'itinéraire se trouvant le plus proche du point de référence donné
     */
    abstract RoutePoint pointClosestTo(PointCh point);
}
