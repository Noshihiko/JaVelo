package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

import static ch.epfl.javelo.Math2.interpolate;
import static ch.epfl.javelo.Preconditions.checkArgument;
import static java.lang.Math.ceil;
import static java.lang.Math.floor;

public final class Functions {
    private Functions(){}

    public static DoubleUnaryOperator constant(double y){
        return new Constant(y);
    }

    private static final record Constant (double y)
            implements DoubleUnaryOperator {

        @Override
        public double applyAsDouble(double y) {
            return this.y;
        }
    }

    public static DoubleUnaryOperator sampled(float[] samples, double xMax){
        checkArgument(samples.length >=2 && xMax>0);
        return new Sampled(samples, xMax);

    }

    private static final record Sampled(float[] samples, double xMax) implements DoubleUnaryOperator {
        @Override
        public double applyAsDouble(double x) {

            if (x<=0) return samples[0];
            else if (x>=xMax) return samples[samples.length-1];
            else {
                double distance = xMax / (samples.length - 1);
                double[] valeurX = new double[samples.length - 1];

                for (int i=0; i<samples.length; ++i){
                    valeurX[i] = distance*i ;
                }

                for (int i =0; i<(samples.length-1);++i){
                    int Y0 = (int) floor(samples[i] / distance);
                    int Y1 = (int) ceil(samples[i+1] / distance);
                    return interpolate(samples[Y0], samples[Y1], (valeurX[i]+valeurX[i+1])/distance);
                }
            }
            throw new IllegalArgumentException();
        }
    }
}
