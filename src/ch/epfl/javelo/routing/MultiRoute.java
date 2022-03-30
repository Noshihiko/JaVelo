package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;

import static ch.epfl.javelo.Math2.clamp;

public class MultiRoute implements Route {
    private final List<Route> route;

    public MultiRoute(List<Route> segments) {
        Preconditions.checkArgument(!(segments.isEmpty()));
        this.route = List.copyOf(segments);
    }

    private double distance(int setting) {
        double distance = 0;
        for (int i = 0; i < setting; ++i) {
            distance += route.get(i).length();
        }
        return distance;
    }

    @Override
    public int indexOfSegmentAt(double position) {
        position = clamp(0, position, route.size());
        int index = 0;
        double distanceMin = 0;
        double distanceMax = 0;

        for (int i = 0; i < route.size(); ++i) {
            distanceMax = distance(i+1);
            distanceMin = distance(i);
            if ((position < distanceMax) && ((position) > distanceMin)) {
                index = i;
            }
        }
        return index;
    }

    @Override
    public double length() {
        double length = 0;

        for (Route aClass : route) {
            length += aClass.length();
        }
        return length;
    }

    @Override
    public List<Edge> edges() {
        List<Edge> edges = new ArrayList<>();

        for (Route aClass : route) {
            edges.addAll(aClass.edges());
        }
        return edges;
    }

    @Override
    public List<PointCh> points() {
        List<PointCh> pointsExtremums = new ArrayList<>();

        //boucle pour aller chercher dans chaque route sauf la dernière les premiers points de chaque edge
        for (int i = 0; i < route.size()-2; i++) {
            pointsExtremums =route.get(i).points();
            pointsExtremums.remove(route.get(i).edges().get(edges().size() - 1).toPoint());
        }
        //ajout des points extremums des edges de la dernière route
        pointsExtremums = route.get(route.size()-1).points();

        return pointsExtremums;
    }

    @Override
    public PointCh pointAt(double position) {
        position = clamp(0, position, length());

        int index = indexOfSegmentAt(position);
        position = clamp(0, position, route.get(index).length());

        return route.get(index).pointAt(position);
    }

    @Override
    public double elevationAt(double position) {
        position = clamp(0, position, length());

        int index = indexOfSegmentAt(position);
        position = clamp(0, position, route.get(index).length());

        return route.get(index).elevationAt(position);
    }

    @Override
    public int nodeClosestTo(double position) {
        position = clamp(0, position, length());

        int index = indexOfSegmentAt(position);
        position = clamp(0, position, route.get(index).length());

        return route.get(index).nodeClosestTo(position);
    }

    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint pointClosest = RoutePoint.NONE;

        for (Route aClass : route) {
            pointClosest = pointClosest.min(aClass.pointClosestTo(point));
        }
        return pointClosest;
    }
}