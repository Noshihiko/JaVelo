package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

import static ch.epfl.javelo.Math2.interpolate;
import static ch.epfl.javelo.Preconditions.checkArgument;

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
                double[] valeurX = new double[samples.length];

                for (int i=0; i<samples.length; ++i){
                    valeurX[i] = distance*i ;
                }

                for (int i =0; i<(samples.length-1);++i){
                    double Y0 = samples[i];
                    double Y1 = samples[i+1];
                    if (valeurX[i]<x && valeurX[i+1]>x){
                        return interpolate(Y0, Y1, (valeurX[i+1]-x)/distance);
                    } else if (x==valeurX[i]){
                        return samples[i];
                    }

                }
            }
            throw new IllegalArgumentException();
        }
    }
}
