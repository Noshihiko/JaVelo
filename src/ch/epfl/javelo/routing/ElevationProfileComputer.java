package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

/**
 * Représente un calcul de profil en long
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */
public final class ElevationProfileComputer {
    private ElevationProfileComputer(){}

    public static ElevationProfile elevationProfile(Route route, double maxStepLength){
        Preconditions.checkArgument(maxStepLength>0);
        // retourne le profil en long de l'itinéraire route, en garantissant que l'espacement entre les échantillons
        // du profil est d'au maximum maxStepLength mètres
        return null;
    }
}
