package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;


import java.util.Arrays;

import static ch.epfl.javelo.Preconditions.checkArgument;
import static java.lang.Double.isNaN;

public final class ElevationProfileComputer {
    private ElevationProfileComputer() {
    }

    public static ElevationProfile elevationProfile(Route route, double maxStepLength) {
        checkArgument(maxStepLength > 0);
        int nbrEchantillons = (int) (Math.ceil(route.length() / maxStepLength) + 1);
        float[] routeProfile = new float[nbrEchantillons];

        //Premier remplissage ****************************************
        float firstValidValue = 0;
        float lastValidValue = 0;
        int positionLastValidValue = 0;
        int positionFirstvalidValue = -1;

        for (int i = 0; i < nbrEchantillons; ++i) {
            routeProfile[i] = (float) route.elevationAt(i);
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
        if (positionFirstvalidValue < 0) Arrays.fill(routeProfile, 0, nbrEchantillons - 1, 0);
        else {
            Arrays.fill(routeProfile, 0, positionFirstvalidValue - 1, firstValidValue);
            Arrays.fill(routeProfile, positionLastValidValue + 1, nbrEchantillons - 1, lastValidValue);
        }
        //*************************************************************

        //Deuxieme remplissage ****************************************
        float latestValidValue = 0, nextValidValue = 0;
        int count = 0;

        for (int i = 0; i < nbrEchantillons - 1; ++i) {
            count = i + 1;
            if (!isNaN(routeProfile[i]) && isNaN(routeProfile[i + 1])) {
                latestValidValue = routeProfile[i];
            }
            if (isNaN(routeProfile[i])) {
                while (isNaN(routeProfile[count])) {
                    count++;
                }
                nextValidValue = routeProfile[count];
                for (int j = i; j < count; ++j) {
                    routeProfile[j] = (float) Math2.interpolate(latestValidValue, nextValidValue, i / nbrEchantillons);
                }
                i = count;
            }
        }
        //************************************************************
        return new ElevationProfile(route.length(), routeProfile);
    }
}

