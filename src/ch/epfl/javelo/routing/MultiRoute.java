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
        position = clamp(0, position, length());
        int index = 0;
        int indexMulti = 0;

        while (position > 0) {
            if (route.get(index).length() < position) {
                indexMulti += route.get(index).indexOfSegmentAt(route.get(index).length()) + 1;
            } else {
                indexMulti += route.get(index).indexOfSegmentAt(position);
            }
            position -= route.get(index).length();
            index += 1;
        }

        return indexMulti;

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

        pointsExtremums.add(route.get(0).points().get(0));
        for (Route aClass : route) {
            for (int i = 1; i < aClass.points().size(); ++i) {
                pointsExtremums.add(aClass.points().get(i));
            }
        }
        return pointsExtremums;
    }

    @Override
    public PointCh pointAt(double position) {
        position = clamp(0, position, length());
        int index = 0;

        for (Route aClass : route) {
            if (position > aClass.length()) {
                position -= aClass.length();
            } else if (position >= 0) {
                return aClass.pointAt(position);
            }
        }

        return null;
    }

    @Override
    public double elevationAt(double position) {
        position = clamp(0, position, length());

        for (Route aClass : route) {
            if (position > aClass.length()) {
                position -= aClass.length();
            } else if (position >= 0) {
                return aClass.elevationAt(position);
            }
        }

        return -1;
    }

    @Override
    public int nodeClosestTo(double position) {
        position = clamp(0, position, length());

        for (Route aClass : route) {
            if (position > aClass.length()) {
                position -= aClass.length();
            } else if (position >= 0) {
                return aClass.nodeClosestTo(position);
            }
        }

        return -1;
    }

    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint pointClosest = RoutePoint.NONE;
        boolean checkIf;
        double position = 0;
        double distance = 0;

        for (Route aClass : route) {
            checkIf = pointClosest.distanceToReference()
                    > aClass.pointClosestTo(point).distanceToReference();

            if (checkIf) {
                pointClosest = aClass.pointClosestTo(point);
                position = distance + pointClosest.position();
                distance += aClass.length();
            }
        }
        return new RoutePoint(pointClosest.point(), position, pointClosest.distanceToReference());
    }
}