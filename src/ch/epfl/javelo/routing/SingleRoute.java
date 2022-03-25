package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.RoutePoint;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Copy;

import java.sql.Array;
import java.util.*;

import static ch.epfl.javelo.Math2.clamp;

/**
 * Représente un itinéraire simple c.-à-d. reliant un point de départ à un point d'arrivée, sans point de passage intermédiaire
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */
public final class SingleRoute implements Route {
    private double[] distance;

    private final List<Edge> edgesClass;

    /**
     * Constructeur de SingleRoute
     *
     * @param edges l'itinéraire simple composé des arêtes données
     */

    public SingleRoute(List<Edge> edges) {
        this.edgesClass = List.copyOf(edges);
        Preconditions.checkArgument(!(edges.isEmpty()));

        //tableau pour méthodes pointAt, elevationAt, nodeClosestTo et pointClosestTo
        distance = new double[edgesClass.size() + 1];
        distance[0] = 0;

        for (int i = 1; i < edgesClass.size(); i++) {
            distance[i + 1] = distance[i] + edgesClass.get(i - 1).length();
        }
    }

    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }

    @Override
    public double length() {
        double length = 0;

        for (Edge aClass : edgesClass) {
            length += aClass.length();
        }
        return length;
    }

    @Override
    public List<Edge> edges() {
        return this.edgesClass;
    }

    @Override
    public List<PointCh> points() {
        List<PointCh> pointsExtremums = new ArrayList<>();

        int indexFinalPoint = edgesClass.size() - 1;
        Edge finalEdge = edgesClass.get(indexFinalPoint);

        //liste de tous les premiers points des edges et du dernier de la dernière edge
        for (int i = 0; i < edgesClass.size(); i++) {
            //pointsExtremums.add(edgesClass.get(i).pointAt(0));
            pointsExtremums.add(edgesClass.get(i).fromPoint());
        }
        //pointsExtremums.add(finalEdge.pointAt(finalEdge.length()));
        pointsExtremums.add(finalEdge.toPoint());

        return pointsExtremums;
    }

    private int binarySearchIndex(double position) {
        position = clamp(0, position, length());

        //Recherche dichotomique dans le tableau distance :
        //index de l'arête sur laquelle se trouve la position
        int a = Arrays.binarySearch(distance, position);
        if (a < 0) {
            a = -(a + 2);
        } else if (a == distance.length - 1) {
            a = distance.length - 2;
        }
        return a;
    }

    @Override
    public PointCh pointAt(double position) {
        position = clamp(0, position, length());
        int a = binarySearchIndex(position);

        return edgesClass.get(a).pointAt(position - distance[a]);
    }


    @Override
    public double elevationAt(double position) {
        position = clamp(0, position, length());
        int a = binarySearchIndex(position);

        return edgesClass.get(a).elevationAt(position - distance[a]);
    }

    @Override
    public int nodeClosestTo(double position) {
        position = clamp(0, position, length());
        int a = binarySearchIndex(position);
        //a qui renvoie l'index de la edge

        if ((position <= distance[a] / 2)) {
            return edgesClass.get(a).fromNodeId();
        } else {
            return edgesClass.get(a).toNodeId();
        }
    }

    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint pointClosest = RoutePoint.NONE;

        for (int i = 0; i < edgesClass.size(); i++){
            Edge edge = edgesClass.get(i);
            double position = clamp(0, edge.positionClosestTo(point), length());
            pointClosest = pointClosest.min(edge.pointAt(position), position + distance[i], point.distanceTo(edge.pointAt(position)));
        }
        return pointClosest;
    }
}
