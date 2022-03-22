package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;
import java.util.ListResourceBundle;

/**
 * Représente un itinéraire simple c.-à-d. reliant un point de départ à un point d'arrivée, sans point de passage intermédiaire
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */
public final class SingleRoute implements Route{
    private final List<Edge> edgesClass;
    public SingleRoute(List<Edge> edges){
        this.edgesClass = List.copyOf(edges);
        Preconditions.checkArgument(!(edges.isEmpty()));
    }


    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }

    @Override
    public double length() {
        double length = 0;

        //demander à assistant si je dois faire ce qui m'est proposé en orange
        for (int i = 0; i < edgesClass.size(); i++){
            length += edgesClass.get(i).length();
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
        for (int i = 0; i < edgesClass.size(); i++){
           //pointsExtremums.add(edgesClass.get(i).pointAt(0));
            //manière plus simple de faire
           pointsExtremums.add(edgesClass.get(i).fromPoint());
        }
        //pointsExtremums.add(finalEdge.pointAt(finalEdge.length()));
        //manière plus simple de faire
        pointsExtremums.add(finalEdge.toPoint());

        return pointsExtremums;
    }



    //à faire :
    @Override
    public PointCh pointAt(double position) {
        position = Math2.clamp(0,position,length());
        return null;
    }


    //assistant
    @Override
    public double elevationAt(double position) {
        position = Math2.clamp(0,position,length());
        //return ElevationProfile.elevationAt(position);
        //mettre elevationAt en static ?
        return 0;
    }

    @Override
    public int nodeClosestTo(double position) {
        position = Math2.clamp(0,position,length());
        return 0;
    }

    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        return null;
    }
}
