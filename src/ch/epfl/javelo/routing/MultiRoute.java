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

    //TOCHECK
    @Override
    public double length() {
        double length = 0;

        for (Route aClass : route) {
            length += aClass.length();
            /* ,
            for (int i = 0; i < route.size(); i++){
                length += aClass.edges().get(i).length();
            }
             */
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

    // faut-il faire comme pour length ?
    @Override
    public List<PointCh> points() {
        List<PointCh> pointsExtremums = new ArrayList<>();
        Route routeFinal = route.get(route.size()-1);

        //boucle pour aller chercher dans chaque route
        for (Route aClass : route) {
            //boucle pour aller chercher dans la route le premier point de chaque edge
            for (int i = 0; i < aClass.edges().size() ; i++) {
                pointsExtremums.add(aClass.edges().get(i).fromPoint());
            }
        }
        pointsExtremums.add(routeFinal.edges().get(edges().size() - 1).toPoint());

        return pointsExtremums;
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
