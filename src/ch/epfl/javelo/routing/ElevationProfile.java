package ch.epfl.javelo.routing;

import java.util.DoubleSummaryStatistics;

import static ch.epfl.javelo.Functions.sampled;
import static ch.epfl.javelo.Preconditions.checkArgument;

/**
 * représente le profil en long d'un itinéraire simple ou multiple
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */

public final class ElevationProfile {
    private double length;
    private float[] elevationSamples;
    private int arrayLength;
    private double min, max;

    /**
     * Constructeur de la classe qui construit le profil en long d'un itinéraire de longueur length
     *
     * @param length
     *              longueur de l'itineraire
     * @param elevationSamples
     *              échantillons d'altitude, répartis uniformément le long de l'itinéraire
     *
     * @throws IllegalArgumentException si la longueur est négative ou nulle,
     *              ou si le tableau d'échantillons contient moins de 2 éléments
     */

    ElevationProfile(double length, float[] elevationSamples) {
        checkArgument(length > 0 && elevationSamples.length >= 2);
        this.length = length;
        this.elevationSamples = elevationSamples.clone();
        arrayLength = this.elevationSamples.length;
        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        for (int i = 0; i < arrayLength; ++i) {
            s.accept(this.elevationSamples[i]);
        }
        min = s.getMin();
        max = s.getMax();

    }

    /**
     *retourne la longueur du profil en mètres
     *
     * @return la longueur du profil en mètres
     */

    public double length() {
        return this.length;
    }

    /**
     * retourne l'altitude minimum du profil en mètres
     *
     * @return l'altitude minimum du profil en mètres
     */

    public double minElevation() {
        return min;
    }

    /**
     * retourne l'altitude maximum du profil en mètres
     *
     * @return l'altitude maximum du profil en mètres
     */

    public double maxElevation() {
        return max;
    }

    /**
     * retourne le dénivelé positif total du profil en mètres
     *
     * @return le dénivelé positif total du profil en mètres
     */


    public double totalAscent() {
        double totalAscent = 0;
        for (int i = 1; i < arrayLength; ++i) {
            if ((this.elevationSamples[i] - this.elevationSamples[i - 1]) > 0) {
                totalAscent += this.elevationSamples[i] - this.elevationSamples[i - 1];
            }
        }
        return totalAscent;
    }

    /**
     * retourne le dénivelé négatif total du profil en mètres
     *
     * @return le dénivelé négatif total du profil en mètres
     */

    public double totalDescent() {
        double totalDescent = 0;
        for (int i = 1; i < arrayLength; ++i) {
            if ((this.elevationSamples[i] - this.elevationSamples[i - 1]) < 0) {
                totalDescent += this.elevationSamples[i - 1] - this.elevationSamples[i];
            }
        }
        return totalDescent;
    }

    /**
     * retourne l'altitude du profil à la position donnée
     *
     * @param position
     *              position a laquelle on veut connaitre l'altitude
     *
     * @return l'altitude du profil à la position donnée
     */

    public double elevationAt(double position) {
        return sampled(this.elevationSamples, length).applyAsDouble(position);
    }
}
