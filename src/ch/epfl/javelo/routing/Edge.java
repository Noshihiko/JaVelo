package ch.epfl.javelo.routing;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

import static ch.epfl.javelo.Math2.interpolate;
import static ch.epfl.javelo.Math2.projectionLength;
import static ch.epfl.javelo.projection.Ch1903.e;
import static ch.epfl.javelo.projection.Ch1903.n;

public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint, double length,
                   DoubleUnaryOperator profile) {

    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId) {
        PointCh fromPoint = graph.nodePoint(fromNodeId);
        PointCh toPoint = graph.nodePoint(toNodeId);
        double length = graph.edgeLength(edgeId);
        DoubleUnaryOperator profile = graph.edgeProfile(edgeId);
        return new Edge(fromNodeId, toNodeId, fromPoint, toPoint, length, profile);
    }

    public double positionClosestTo(PointCh point) {
        return projectionLength(fromPoint.e(), fromPoint.n(), toPoint.e(), toPoint.n(), point.e(), point.n());
    }

    public PointCh pointAt(double position) {
        double rapport = position / length;
        double EofPoint = interpolate(fromPoint.e(), toPoint.e(), rapport);
        double NofPoint = interpolate(fromPoint.n(), toPoint.n(), rapport);
        return new PointCh(EofPoint, NofPoint);
    }

    public double elevationAt(double position) {
        return profile.applyAsDouble(position / length);
    }
}
