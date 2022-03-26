package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;

public class MultiRoute implements Route{
    private final List<Route> route;

    public MultiRoute(List<Route> segments){
        Preconditions.checkArgument(!(segments.isEmpty()));
        this.route = List.copyOf(segments);
    }

    //TODO
    @Override
    public int indexOfSegmentAt(double position) {
        position = Math2.clamp(0, position, route.size());
        return nodeClosestTo(position);

    }

    @Override
    public double length() {
        double length = 0;

        for (Route aClass : route) {
            for (int i = 0; i < route.size(); i++){
                length += aClass.edges().get(i).length();
            }
        }
        return length;
    }

    @Override
    public List<Edge> edges() {
        List<Edge> edges = new ArrayList<>();

        for (Route aClass : route){
            edges.addAll(aClass.edges());
        }
        return edges;
    }

    //est-ce que si l'on utilise points() de SingleRoute, on aura le dernier point de la dernière edge de la route (i-1)
    // qui sera compté
    // et le premier point de la premiere edge de la route (i) aussi (est ce que c'est la meme chose)
    //si oui, alors méthode ci-dessous fausse
    @Override
    public List<PointCh> points() {
        List<PointCh> points = new ArrayList<>();

        for (Route aClass : route){
            points.addAll(aClass.points());
        }
        return points;

        /*
        List<PointCh> pointsExtremums = new ArrayList<>();
        Route routeFinal = route.get(route.size()-1);

        PointCh finalPoint = routeFinal.edges().get(edges().size() - 1).toPoint();


        //liste de tous les premiers points des edges et du dernier de la dernière edge
        for (Route aClass : route) {
            for (int i = 0; i < aClass.) {
                pointsExtremums.add(aClass.bClass.fromPoint());
            }
        }

        pointsExtremums.add(finalEdge.toPoint());

        return pointsExtremums;

         */

    }

    //TODO
    @Override
    public PointCh pointAt(double position) {
        return null;
    }

    //TODO
    @Override
    public double elevationAt(double position) {
        return 0;
    }

    //TODO
    @Override
    public int nodeClosestTo(double position) {
        return 0;
    }

    //TODO
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        return null;
    }
}
