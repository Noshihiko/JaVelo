package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListResourceBundle;

/**
 * Représente un itinéraire simple c.-à-d. reliant un point de départ à un point d'arrivée, sans point de passage intermédiaire
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */
public final class SingleRoute implements Route{
    private double[] distance;

    private final List<Edge> edgesClass;

    /**
     * Constructeur de SingleRoute
     *
     * @param edges
     *      l'itinéraire simple composé des arêtes données
     */

    public SingleRoute(List<Edge> edges){
        this.edgesClass = List.copyOf(edges);
        distance[0] = 0;
        for (int i=1; i<edgesClass.size(); i++) {
            distance[i] = distance[i-1]+ edgesClass.get(i).length();
        }
        Preconditions.checkArgument(!(edges.isEmpty()));
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


    @Override
    public PointCh pointAt(double position) {
        position = Math2.clamp(0,position,length());

        int a = Arrays.binarySearch(distance, position);
        if (a<0) {
            a = -(a+2);
        }
        return edgesClass.get(a).pointAt(position - distance[a]);
    }


    @Override
    public double elevationAt(double position) {
        position = Math2.clamp(0,position,length());
        //TODO
       // return ElevationProfile.elevationAt(position);
        //mettre elevationAt en static ?
        return 0;
    }

    @Override
    public int nodeClosestTo(double position) {
        position = Math2.clamp(0,position,length());
        //TODO
        //retourne l'identité du nœud appartenant à l'itinéraire et se trouvant le plus proche
        // de la position donnée
        return 0;
    }

    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        //TODOs
        //retourne le point de l'itinéraire se trouvant le plus proche du point de référence donné
        return null;
    }
}
