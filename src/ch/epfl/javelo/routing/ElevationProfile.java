package ch.epfl.javelo.routing;

import java.util.DoubleSummaryStatistics;

import static ch.epfl.javelo.Functions.sampled;
import static ch.epfl.javelo.Preconditions.checkArgument;

/**
 * Représente le profil en long d'un itinéraire simple ou multiple
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */

public final class ElevationProfile {
    private final double length;
    private final float[] elevationSamples;
    private final int arrayLength;
    private final double min;
    private final double max;
    private double totalAscent, totalDescent;

    /**
     * Constructeur de la classe qui construit le profil en long d'un itinéraire de longueur length.
     *
     * @param length           longueur de l'itinéraire
     * @param elevationSamples échantillons d'altitude, répartis uniformément le long de l'itinéraire
     * @throws IllegalArgumentException si la longueur est négative ou nulle,
     *                                  ou si le tableau d'échantillons contient moins de 2 éléments
     */
    public ElevationProfile(double length, float[] elevationSamples) {
        checkArgument(length > 0 && elevationSamples.length >= 2);
        this.length = length;
        this.elevationSamples = elevationSamples.clone();
        arrayLength = this.elevationSamples.length;
        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        double elevationDifference;

        s.accept(this.elevationSamples[0]);
        for (int i = 1; i < arrayLength; ++i) {
            s.accept(this.elevationSamples[i]);

            elevationDifference = this.elevationSamples[i] - this.elevationSamples[i - 1];

            if (elevationDifference > 0) {
                totalAscent += elevationDifference;

            } else if (elevationDifference < 0) {
                totalDescent -= elevationDifference;
            }
        }
        min = s.getMin();
        max = s.getMax();
    }

    /**
     * Retourne la longueur du profil en mètres.
     * @return la longueur du profil en mètres.
     */
    public double length() {
        return this.length;
    }

    /**
     * Retourne l'altitude minimum du profil en mètres.
     * @return l'altitude minimum du profil en mètres.
     */
    public double minElevation() {
        return min;
    }

    /**
     * Retourne l'altitude maximum du profil en mètres.
     * @return l'altitude maximum du profil en mètres.
     */
    public double maxElevation() {
        return max;
    }

    /**
     * Retourne le dénivelé positif total du profil en mètres.
     * @return le dénivelé positif total du profil en mètres.
     */
    public double totalAscent() {
        return totalAscent;
    }

    /**
     * Retourne le dénivelé négatif total du profil en mètres.
     * @return le dénivelé négatif total du profil en mètres.
     */
    public double totalDescent() {
        return totalDescent;
    }

    /**
     * Retourne l'altitude du profil à la position donnée.
     *
     * @param position position à laquelle on veut connaître l'altitude
     *
     * @return l'altitude du profil à la position donnée.
     */
    public double elevationAt(double position) {
        return sampled(this.elevationSamples, length).applyAsDouble(position);
    }
}
