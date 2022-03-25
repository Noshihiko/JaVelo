package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.RoutePoint;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Copy;

import java.sql.Array;
import java.util.*;

import static ch.epfl.javelo.Math2.clamp;
import static java.util.Arrays.binarySearch;
import static java.util.Arrays.parallelSetAll;

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
        Preconditions.checkArgument(!(edges.isEmpty()));
        this.edgesClass = List.copyOf(edges);

        //tableau pour méthodes pointAt, elevationAt, nodeClosestTo et pointClosestTo
        distance = new double[edgesClass.size() + 1];
        distance[0] = 0;

        for (int i = 1; i < edgesClass.size(); i++) {
            distance[i] = distance[i - 1] + edgesClass.get(i).length();
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
        for (Edge aClass : edgesClass) {
            //pointsExtremums.add(edgesClass.get(i).pointAt(0));
            pointsExtremums.add(aClass.fromPoint());
        }
        //pointsExtremums.add(finalEdge.pointAt(finalEdge.length()));
        pointsExtremums.add(finalEdge.toPoint());

        return pointsExtremums;
    }

    /*
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
       */

    @Override
    public PointCh pointAt(double position) {
        position = clamp(0, position, length());
        int index = binarySearch(distance, position);

        if (index < 0) {index = - index - 2;}

        if (index >= edgesClass.size()) {
            return edgesClass.get(edgesClass.size() - 1).pointAt(position - distance[ edgesClass.size() - 1]);
        } else {
            return edgesClass.get(index).pointAt(position - distance[index]);
        }

    }


    @Override
    public double elevationAt(double position) {
        position = clamp(0, position, length());
        int index = binarySearch(distance, position);
        double elevation =0;

        if (index < 0) {index = - index - 2;}

        if (index >= edgesClass.size()) {
            elevation = edgesClass.get(edgesClass.size() - 1).elevationAt(position - distance[edgesClass.size() - 1]);
        } else if (index >=0){
            elevation = edgesClass.get(index).elevationAt(position - distance[index]);
        }
        return elevation;
    }

    @Override
    public int nodeClosestTo(double position) {
        position = clamp(0, position, length());
        int index = binarySearch(distance, position);

        if (index < 0) {
            index = - index - 2;
        } else if (index >= edgesClass.size()){
            index = edgesClass.size() - 1;
        }

        if ((position <= distance[index] / 2)) {
            return edgesClass.get(index).fromNodeId();
        } else {
            return edgesClass.get(index).toNodeId();
        }
    }

    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint pointClosest = RoutePoint.NONE;

        for (int i = 0; i < edgesClass.size(); i++) {
            Edge edge = edgesClass.get(i);
            double position = clamp(0, edge.positionClosestTo(point), length());
            pointClosest = pointClosest.min(edge.pointAt(position), position + distance[i], point.distanceTo(edge.pointAt(position)));
        }
        return pointClosest;
    }
}
