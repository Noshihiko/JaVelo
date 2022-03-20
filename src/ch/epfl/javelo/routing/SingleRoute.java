package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente un itinéraire simple c.-à-d. reliant un point de départ à un point d'arrivée, sans point de passage intermédiaire
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */
public final class SingleRoute implements Route{

    public SingleRoute(List<Edge> edges){
        //  retourner l'itinéraire simple composé des arêtes données
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
        for (int i = 0; i < edges().size(); i++){
            length += edges().get(i).length();
        }
        return length;
    }

    @Override
    public List<Edge> edges() {
        //pas bon : pk?
        return this.edges();
    }

    @Override
    public List<PointCh> points() {
        List<PointCh> pointsExtremums = new ArrayList<>();

        int indexFinalPoint = edges().size() - 1;
        Edge finalEdge = edges().get(indexFinalPoint);

        //liste de tous les premiers points des edges et du dernier de la dernière edge
        for (int i = 0; i < edges().size(); i++){
           pointsExtremums.add(edges().get(i).pointAt(0));
        }
        pointsExtremums.add(finalEdge.pointAt(finalEdge.length()));

        return pointsExtremums;
    }

    //conditions position
    private double positionCheck(double position){
        if (position < 0){
            position = 0;
        } else if (position > length()){
            position = length();
        }
        return position;
    }

    //à faire :
    @Override
    public PointCh pointAt(double position) {
        position = positionCheck(position);
        return null;
    }


    //assistant
    @Override
    public double elevationAt(double position) {
        position = positionCheck(position);
        //return ElevationProfile.elevationAt(position);
        //mettre elevationAt en static ?
        return 0;
    }

    @Override
    public int nodeClosestTo(double position) {
        position = positionCheck(position);
        return 0;
    }

    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        return null;
    }
}
