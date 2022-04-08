package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.*;

import static ch.epfl.javelo.Math2.clamp;
import static java.util.Arrays.binarySearch;

/**
 * Représente un itinéraire simple c.-à-d. reliant un point de départ à un point d'arrivée, sans point de passage intermédiaire.
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */
public final class SingleRoute implements Route {
    private final double[] distance;

    private final List<Edge> edgesClass;

    /**
     * Constructeur de SingleRoute
     *
     * @param edges l'itinéraire simple composé des arêtes données
     * @throws IllegalArgumentException si la liste d'arêtes est vide
     */
    public SingleRoute(List<Edge> edges) {
        Preconditions.checkArgument(!(edges.isEmpty()));
        this.edgesClass = List.copyOf(edges);

        //tableau du total des distances de la SingleRoute
        distance = new double[edgesClass.size() + 1];
        distance[0] = 0;

        for (int i = 1; i < edgesClass.size() + 1; ++i) {
            distance[i] = distance[i - 1] + edgesClass.get(i - 1).length();
        }
    }

    /**
     * Donne l'index du segment de l'itinéraire contenant la position donnée, toujours 0 dans le cas d'un itinéraire simple.
     *
     * @param position la position du segment pour calculer son index
     *
     * @return  l'index du segment de l'itinéraire contenant la position donnée, toujours 0 dans le cas d'un itinéraire simple.
     */
    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }

    /**
     * Donne la longueur de l'itinéraire, en mètres.
     * @return la longueur de l'itinéraire, en mètres.
     */
    @Override
    public double length() {
        double length = 0;

        for (Edge edge : edgesClass) {
            length += edge.length();
        }
        return length;
    }

    /**
     * Donne la totalité des arêtes de l'itinéraire.
     * @return la totalité des arêtes de l'itinéraire.
     */
    @Override
    public List<Edge> edges() {
        return this.edgesClass;
    }

    /**
     * Donne la totalité des points situés aux extrémités des arêtes de l'itinéraire.
     * @return la totalité des points situés aux extrémités des arêtes de l'itinéraire.
     */
    @Override
    public List<PointCh> points() {
        List<PointCh> pointsExtremums = new ArrayList<>();

        //liste de tous les premiers points des edges
        for (Edge aClass : edgesClass) {
            pointsExtremums.add(aClass.fromPoint());
        }
        //ajout du dernier point de la dernière edge
        pointsExtremums.add(edgesClass.get(edgesClass.size() - 1).toPoint());

        return pointsExtremums;
    }

    /**
     * Donne le point se trouvant à la position donnée le long de l'itinéraire.
     *
     * @param position la position donnée
     *
     * @return le point se trouvant à la position donnée le long de l'itinéraire.
     */
    @Override
    public PointCh pointAt(double position) {
        position = clamp(0, position, length());
        int index = binarySearch(distance, position);
        if (index < 0) index = - index - 2;


        return index >= edgesClass.size() ? edgesClass.get(edgesClass.size() - 1).toPoint()
                : edgesClass.get(index).pointAt(position - distance[index]);
    }

    /**
     * Donne l'altitude à la position donnée le long de l'itinéraire.
     *
     * @param position la position donnée
     *
     * @return l'altitude à la position donnée le long de l'itinéraire,
     * si l'arête contenant cette position n'a pas de profil, retourne NaN.
     */
    @Override
    public double elevationAt(double position) {
        position = clamp(0, position, length());
        int index = binarySearch(distance, position);
        if (index < 0) index = - index - 2;

        return index >= edgesClass.size() ? edgesClass.get(edgesClass.size() - 1).elevationAt(position - distance[edgesClass.size() - 1])
                : edgesClass.get(index).elevationAt(position - distance[index]);
    }

    /**
     * Donne l'identité du nœud appartenant à l'itinéraire et se trouvant le plus proche de la position donné.
     *
     * @param position la position donnée
     *
     * @return l'identité du nœud appartenant à l'itinéraire et se trouvant le plus proche de la position donné.
     */
    @Override
    public int nodeClosestTo(double position) {
        position = clamp(0, position, length());
        int index = binarySearch(distance, position);

        if (index < 0) index = -index - 2;
        else if (index >= edgesClass.size()) index = edgesClass.size() - 1;

        if ((position - distance[index] <= edgesClass.get(index).length() / 2)) {
            return edgesClass.get(index).fromNodeId();
        } else {
            return edgesClass.get(index).toNodeId();
        }
    }

    /**
     * Donne le point de l'itinéraire se trouvant le plus proche du point de référence donné.
     *
     * @param point point de référence
     *
     * @return le point de l'itinéraire se trouvant le plus proche du point de référence donné.
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint pointClosest = RoutePoint.NONE;
        double position;
        Edge edge;

        for (int i = 0; i < edgesClass.size(); ++i) {
            edge = edgesClass.get(i);
            position = clamp(0, edge.positionClosestTo(point), edge.length());
            pointClosest = pointClosest.min(edge.pointAt(position), position + distance[i], point.distanceTo(edge.pointAt(position)));
        }
        return pointClosest;
    }
}