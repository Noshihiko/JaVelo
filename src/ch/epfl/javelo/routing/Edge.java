package ch.epfl.javelo.routing;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import java.util.function.DoubleUnaryOperator;

import static ch.epfl.javelo.Math2.interpolate;
import static ch.epfl.javelo.Math2.projectionLength;
import static ch.epfl.javelo.projection.Ch1903.e;
import static ch.epfl.javelo.projection.Ch1903.n;

public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint, double length, DoubleUnaryOperator profile) {

    static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId) {
        /*qui retourne une instance de Edge dont les attributs fromNodeId et
        //toNodeId sont ceux donnés, les autres étant ceux de l'arête d'identité edgeId dans le graphe Graph.
        point depart arrete
        le point d'arrivée de l'arête
        la longueur de l'arête, en mètres
        le profil en long de l'arête
        */

        double length = Graph.edgeLength(edgeId);

    }

    //lon lat ou E et N ?
    public double positionClosestTo(PointCh point) {
        return projectionLength(fromPoint.e(), fromPoint.n(), toPoint.e(), toPoint.n(), point.e(), point.n());
    }

    public PointCh pointAt(double position) {
        double rapport = position / length;
        double EofPoint = interpolate(toPoint.e(), fromPoint.e(), rapport);
        double NofPoint = interpolate(toPoint.n(), fromPoint.n(), rapport);
        return new PointCh(EofPoint, NofPoint);
    }

    public double elevationAt(double position) {
        return profile.applyAsDouble(position/length);
    }
}
