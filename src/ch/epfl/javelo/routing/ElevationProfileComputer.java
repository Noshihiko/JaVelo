package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;


import java.util.Arrays;

import static ch.epfl.javelo.Preconditions.checkArgument;
import static java.lang.Double.isNaN;

/**
 * Calcule le profil en long d'un itinéraire
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */

public final class ElevationProfileComputer {
    private ElevationProfileComputer() {}

    /**
     * calcule le profil en long de l'itinéraire
     *
     * @param route         route dont on veut construire le profil en long
     * @param maxStepLength maximum d'espacement en mètres entre les échantillons du profil
     * @throws IllegalArgumentException si maxStepLength est égale ou inférieure à 0
     *
     * @return le profil en long de l'itinéraire
     */

    public static ElevationProfile elevationProfile(Route route, double maxStepLength) {
        checkArgument(maxStepLength > 0);

        int numberOfSamples = (int) (Math.ceil(route.length() / maxStepLength) + 1);
        float[] routeProfile = new float[numberOfSamples];
        float maxLength = (float) (route.length() / (numberOfSamples - 1));

        float firstValidValue = 0, lastValidValue = 0;
        int positionLastValidValue = 0, positionFirstValidValue = -1;

        //*****************************Premier remplissage ****************************************
        //Passe le tableau et remplace les NaN au debut par la première valeur valide
        // (resp à la fin, remplace les NaN par la dernière valeur valide)
        for (int i = 0; i < numberOfSamples; ++i) {

            routeProfile[i] = (float) route.elevationAt(i * maxLength);
            if (positionFirstValidValue < 0 && !isNaN(routeProfile[i])) {

                firstValidValue = routeProfile[i];
                positionFirstValidValue = i;

                lastValidValue = routeProfile[i];
                positionLastValidValue = i;
            }

            if (positionLastValidValue < i && !isNaN(routeProfile[i])) {

                lastValidValue = routeProfile[i];
                positionLastValidValue = i;
            }
        }
        //Si le tableau ne contient que des NaN, alors la positionFirstValue reste negative à -1 et le tableau est rempli
        //qu'avec des 0
        if (positionFirstValidValue < 0) Arrays.fill(routeProfile, 0, numberOfSamples, 0);
        else {
            Arrays.fill(routeProfile, 0, positionFirstValidValue, firstValidValue);
            Arrays.fill(routeProfile, positionLastValidValue, numberOfSamples, lastValidValue);

            //******************************Deuxième remplissage *************************************
            //Les extrémités n'étant plus des valeurs NaN, on procède à remplir les valeurs NaN se trouvant au milieu de
            //valeurs valides
            float latestValidValue = 0, nextValidValue;
            int positionNextValidValue, count, positionLatestValidValue = 0;

            for (int i = 0; i < numberOfSamples - 1; ++i) {
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
                        float spacing = (float) (j - positionLatestValidValue) / (positionNextValidValue - positionLatestValidValue);
                        routeProfile[j] = (float) Math2.interpolate(latestValidValue, nextValidValue, spacing);

                    }
                    i = count - 1;
                }
            }
        }
        return new ElevationProfile(route.length(), routeProfile);
    }
}

