package ch.epfl.javelo.routing;

import java.util.DoubleSummaryStatistics;

import static ch.epfl.javelo.Functions.sampled;
import static ch.epfl.javelo.Preconditions.checkArgument;
import static java.lang.Math.abs;


//Q3: check pour savoir si elevationsample contine bien que le deniveleé et pas l'haiteir en metre : est ce qu0il faut faire des calculs
//en plus ou comme ca ca va?


public final class ElevationProfile {
    private double length;
    private float[] elevationSamples;
    private int arrayLength;


    ElevationProfile(double length, float[] elevationSamples) {
        checkArgument(length > 0 && elevationSamples.length >= 2);
        this.length = length;
        this.elevationSamples = elevationSamples.clone();
        arrayLength = this.elevationSamples.length;

    }

    public double length() {
        return this.length;
    }

    public double minElevation() {
        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        for (int i = 0; i < arrayLength; ++i) {
            s.accept(this.elevationSamples[i]);
        }
        return s.getMin();
    }


    public double maxElevation() {
        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        for (int i = 0; i < arrayLength; ++i) {
            s.accept(this.elevationSamples[i]);
        }
        return s.getMax();
    }


    public double totalAscent() {
        double totalAscent = 0;
        for (int i = 1; i < arrayLength; ++i) {
            if ((this.elevationSamples[i] - this.elevationSamples[i - 1]) > 0) {
                totalAscent += this.elevationSamples[i] - this.elevationSamples[i - 1];
            }
        }
        return totalAscent;
    }


    public double totalDescent() {
        double totalDescent = 0;
        for (int i = 1; i < arrayLength; ++i) {
            if ((this.elevationSamples[i] - this.elevationSamples[i - 1]) < 0) {
                totalDescent += this.elevationSamples[i - 1] - this.elevationSamples[i];
            }
        }
        return totalDescent;
    }

    public double elevationAt(double position) {
        return sampled(this.elevationSamples, length).applyAsDouble(position);
    }
}
