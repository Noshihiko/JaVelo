package ch.epfl.javelo.routing;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

import static ch.epfl.javelo.Math2.interpolate;
import static ch.epfl.javelo.Math2.projectionLength;

/**
 * Représente une arête d'un itinéraire
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */

/**
 * Enregistrement d'un Edge
 *
 * @param fromNodeId identité du nœud de départ de l'arête
 * @param toNodeId   identité du nœud d'arrivée de l'arête
 * @param fromPoint  point de départ de l'arête
 * @param toPoint    point d'arrivée de l'arête
 * @param length     longueur de l'arête en mètres
 * @param profile    profil en long de l'arête
 */

public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint, double length,
                   DoubleUnaryOperator profile) {


    /**
     * Retourne une instance de Edge
     *
     * @param graph      graphe JaVelo
     * @param edgeId     identité de l'arête
     * @param fromNodeId identité du nœud de départ de l'arête
     * @param toNodeId   identité du nœud d'arrivée de l'arête
     * @return une instance de Edge
     */

    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId) {
        PointCh fromPoint = graph.nodePoint(fromNodeId);
        PointCh toPoint = graph.nodePoint(toNodeId);
        double length = graph.edgeLength(edgeId);
        DoubleUnaryOperator profile = graph.edgeProfile(edgeId);

        return new Edge(fromNodeId, toNodeId, fromPoint, toPoint, length, profile);
    }

    /**
     * Retourne la position le long de l'arête en mètres qui se trouve la plus proche du point donné
     *
     * @param point point donné dont on cherche la position la plus proche sur l'arête
     * @return la position le long de l'arête en mètres qui se trouve la plus proche du point donné
     */

    public double positionClosestTo(PointCh point) {
        return projectionLength(fromPoint.e(), fromPoint.n(), toPoint.e(), toPoint.n(), point.e(), point.n());
    }

    /**
     * Retourne le point se trouvant à la position donnée sur l'arête exprimée en mètres
     *
     * @param position position sur l'arête, en mètres
     * @return le point se trouvant à la position donnée sur l'arête exprimée en mètres
     */

    public PointCh pointAt(double position) {
        double rapport = position / length;
        double e = interpolate(fromPoint.e(), toPoint.e(), rapport);
        double n = interpolate(fromPoint.n(), toPoint.n(), rapport);

        return new PointCh(e, n);
    }

    /**
     * Retourne l'altitude, en mètres, à la position donnée sur l'arête
     *
     * @param position position sur l'arête, en mètres
     * @return l'altitude en mètres à la position donnée sur l'arête
     */

    public double elevationAt(double position) {
        return profile.applyAsDouble(position);
    }
}