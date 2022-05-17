package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;

import static ch.epfl.javelo.Math2.clamp;

/**
 * Représente un itinéraire multiple, composé d'une séquence d'itinéraires contigus nommés segments.
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */
public final class MultiRoute implements Route {
    private final List<Route> routeList;

    /**
     * Constructeur de MultiRoute
     *
     * @param segments liste des segments composants l'itinéraire
     * @throws IllegalArgumentException si la liste de segments est vide
     */
    public MultiRoute(List<Route> segments) {
        Preconditions.checkArgument(!(segments.isEmpty()));
        this.routeList = List.copyOf(segments);
    }

    /**
     * Donne l'index du segment de l'itinéraire contenant la position donnée.
     *
     * @param position la position du segment pour calculer son index
     *
     * @return l'index du segment de l'itinéraire contenant la position donnée.
     */
    @Override
    public int indexOfSegmentAt(double position) {
        position = clamp(0, position, length());
        int index = 0;
        int indexMulti = 0;

        while (position > 0) {
            if (routeList.get(index).length() < position) {
                indexMulti += routeList.get(index).indexOfSegmentAt(routeList.get(index).length()) + 1;
            } else {
                indexMulti += routeList.get(index).indexOfSegmentAt(position);
            }
            position -= routeList.get(index).length();
            index += 1;
        }
        return indexMulti;
    }

    /**
     * Donne la longueur de l'itinéraire, en mètres.
     * @return la longueur de l'itinéraire, en mètres.
     */
    @Override
    public double length() {
        double length = 0;

        for (Route route : routeList) {
            length += route.length();
        }
        return length;
    }

    /**
     * Donne la totalité des arêtes de l'itinéraire.
     * @return la totalité des arêtes de l'itinéraire.
     */
    @Override
    public List<Edge> edges() {
        List<Edge> edges = new ArrayList<>();

        for (Route route : routeList) {
            edges.addAll(route.edges());
        }
        return edges;
    }

    /**
     * Donne la totalité des points situés aux extrémités des arêtes de l'itinéraire, sans doublons.
     * @return la totalité des points situés aux extrémités des arêtes de l'itinéraire, sans doublons.
     */
    @Override
    public List<PointCh> points() {
        List<PointCh> pointsExtremums = new ArrayList<>();

        pointsExtremums.add(routeList.get(0).points().get(0));
        for (Route route : routeList) {
            for (int i = 1; i < route.points().size(); ++i) {
                pointsExtremums.add(route.points().get(i));
            }
        }
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

        for (Route route : routeList) {
            if (position > route.length()) {
                position -= route.length();
            } else {
                return route.pointAt(position);
            }
        }
        return null;
    }

    /**
     * Donne l'altitude à la position donnée le long de l'itinéraire.
     *
     * @param position la position donnée
     *
     * @return l'altitude à la position donnée le long de l'itinéraire,
     * si l'arête contenant cette position n'a pas de profil, vaut NaN.
     */
    @Override
    public double elevationAt(double position) {
        position = clamp(0, position, length());

        for (Route route : routeList) {
            if (position > route.length()) {
                position -= route.length();
            } else {
                return route.elevationAt(position);
            }
        }
        return -1;
    }

    /**
     * Donne l'identité du nœud appartenant à l'itinéraire et se trouvant le plus proche de la position donnée.
     *
     * @param position la position donnée
     *
     * @return l'identité du nœud appartenant à l'itinéraire et se trouvant le plus proche de la position donnée.
     */
    @Override
    public int nodeClosestTo(double position) {
        position = clamp(0, position, length());

        for (Route route : routeList) {
            if (position > route.length()) {
                position -= route.length();
            } else {
                return route.nodeClosestTo(position);
            }
        }
        return -1;
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
        boolean checkIf;
        double position = 0;
        double distance = 0;

        for (Route route : routeList) {
            checkIf = pointClosest.distanceToReference() > route.pointClosestTo(point).distanceToReference();

            if (checkIf) {
                pointClosest = route.pointClosestTo(point);
                position = distance + pointClosest.position();
                distance += route.length();
            }
        }
        return new RoutePoint(pointClosest.point(), position, pointClosest.distanceToReference());
    }
}