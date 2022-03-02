package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

public final class Functions {
    private Functions(){}

    public static DoubleUnaryOperator constant(double y){
        return new Constant(y);
    }

    private static final record Constant (double y)
            implements DoubleUnaryOperator {

        @Override
        public double applyAsDouble(double y) {
            return y;
        }
    }

    public static DoubleUnaryOperator sampled(float[] samples, double xMax){
        if (samples.length >=2 && xMax>0){
            return new Sampled(samples, xMax);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static final record Sampled(float[] samples, double xMax) implements DoubleUnaryOperator {
        @Override
        public double applyAsDouble(double x) {
            if (x<0) return 0;
            else if (x<xMax) return xMax;
            else {

                double distance = xMax / (samples.length - 1);
                int Y0 = (int) Math.floor(x / distance);
                int Y1 = (int) Math.ceil(x / distance);

                return Math2.interpolate(samples[Y0], samples[Y1], x - Y0);
            }
        }
    }
}
