package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;


import java.util.Arrays;

import static ch.epfl.javelo.Preconditions.checkArgument;
import static java.lang.Double.isNaN;

/**
 * Calcule le profil en long d'un itineraire
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */

public final class ElevationProfileComputer {
    private ElevationProfileComputer() {
    }

    /**
     * calcule le profile en long de l'itineraire
     *
     * @param route
     *          route dont on veut construire le profil en long
     * @param maxStepLength
     *          maximum d'espacement en mètres entre les echantillons du profil
     * @return le profil en long de l'itineraire
     */

    public static ElevationProfile elevationProfile(Route route, double maxStepLength) {
        checkArgument(maxStepLength > 0);
        int nbrEchantillons = (int) (Math.ceil(route.length() / maxStepLength) + 1);
        float[] routeProfile = new float[nbrEchantillons];

        //Premier remplissage ****************************************
        float firstValidValue = 0;
        float lastValidValue = 0;
        int positionLastValidValue = 0;
        int positionFirstvalidValue = -1;
        float espacement = (float) (route.length() / (nbrEchantillons - 1));

        for (int i = 0; i < nbrEchantillons; ++i) {

            routeProfile[i] = (float) route.elevationAt(i * espacement);
            if (positionFirstvalidValue < 0 && !isNaN(routeProfile[i])) {
                firstValidValue = routeProfile[i];
                positionFirstvalidValue = i;
                lastValidValue = routeProfile[i];
                positionLastValidValue = i;
            }
            if (positionLastValidValue < i && !isNaN(routeProfile[i])) {
                lastValidValue = routeProfile[i];
                positionLastValidValue = i;
            }
        }
        if (positionFirstvalidValue < 0) Arrays.fill(routeProfile, 0, nbrEchantillons, 0);
        else {
            Arrays.fill(routeProfile, 0, positionFirstvalidValue, firstValidValue);
            Arrays.fill(routeProfile, positionLastValidValue, nbrEchantillons, lastValidValue);
            ;

            //*************************************************************

            //Deuxieme remplissage ****************************************
            float latestValidValue = 0, nextValidValue;
            int count;
            int positionNextValidValue, positionLatestValidValue = 0;


            for (int i = 0; i < nbrEchantillons - 1; ++i) {
                count = i + 1;
                if (!isNaN(routeProfile[i]) && isNaN(routeProfile[i + 1])) {
                    latestValidValue = routeProfile[i];
                    positionLatestValidValue = i;
                }
                if (isNaN(routeProfile[i])) {
                    while (isNaN(routeProfile[count])) {
                        count++;
                    }
                    nextValidValue = routeProfile[count];
                    positionNextValidValue = count;
                    for (int j = i; j < count; ++j) {
                        float precis = (float) (j - positionLatestValidValue) / (positionNextValidValue - positionLatestValidValue);
                        routeProfile[j] = (float) Math2.interpolate(latestValidValue, nextValidValue, precis);

                    }
                    i = count - 1;
                }
            }
        }
        //************************************************************
        return new ElevationProfile(route.length(), routeProfile);
    }
}

