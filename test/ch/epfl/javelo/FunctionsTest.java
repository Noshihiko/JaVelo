package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
public class FunctionsTest {

    private static final double DELTA = 1e-7;

    @Test
    void checkConstant() {
        var actual = Functions.constant(7).applyAsDouble(-999999999);
        var expected = 7;

        assertEquals(expected, actual, DELTA);
    }

    @Test
    void checkSampled() {
        float[] samples1 = {3, 4};
        var actual1 = Functions.sampled(samples1, 1).applyAsDouble(0.5);
        var expected1 = 3.5;
        assertEquals(expected1, actual1, DELTA);

        float[] samples2 = {4, 5, 10};
        var actual2 = Functions.sampled(samples2, 1).applyAsDouble(0.25);
        var expected2 = 4.5;
        assertEquals(expected2, actual2, DELTA);

        float[] samples3 = {1, 1, 1, 1, 1};
        var actual3 = Functions.sampled(samples3, 4).applyAsDouble(3.4);
        var expected3 = 1;
        assertEquals(expected3, actual3, DELTA);
    }

    @Test
    void checkSampledExceptions() {
        float[] samples1 = {};
        assertThrows(IllegalArgumentException.class, () -> {
            Functions.sampled(samples1, 2);
        });

        float[] samples2 = {1,2};
        assertThrows(IllegalArgumentException.class, () -> {
            Functions.sampled(samples2, -1);
        });
    }

    @Test
    void DoubleUnaryOperatorConstantTest(){
        DoubleUnaryOperator a = Functions.constant(7);
        assertEquals(7, a.applyAsDouble(3));
    }

    @Test
    void DoubleUnaryOperatorInterpolateTestSimple(){
        float samples[] = {0, 2, 4, 6, 8, 10};
        double xMax = 10;
        DoubleUnaryOperator a = Functions.sampled(samples, xMax);
        assertEquals(1, a.applyAsDouble(1));
    }

    @Test
    void DoubleUnaryOperatorInterpolateTestSimple2(){
        float samples[] = {0, 2, 0, 2};
        double xMax = 18;
        DoubleUnaryOperator a = Functions.sampled(samples, xMax);
        assertEquals(0, a.applyAsDouble(-2));
    }

    @Test
    void DoubleUnaryOperatorInterpolateTestError(){
        float samples[] = {};
        double xMax = 18;
        assertThrows(IllegalArgumentException.class, () -> {
            DoubleUnaryOperator a = Functions.sampled(samples, xMax);
        });
    }


    @Test
    void DoubleUnaryOperatorInterpolateTestSimple3(){
        float samples[] = {0, 2, 0, 2};
        double xMax = 18;
        DoubleUnaryOperator a = Functions.sampled(samples, xMax);
        assertEquals(0, a.applyAsDouble(-2));
    }


    @Test
    void constantsWorks() {
        DoubleUnaryOperator f = Functions.constant(1.0);

        assertEquals(1.0, f.applyAsDouble(1.0));
        assertEquals(1.0, f.applyAsDouble(2.0));
        assertEquals(1.0, f.applyAsDouble(0));
        assertEquals(1.0, f.applyAsDouble(-1.0));
        assertEquals(1.0, f.applyAsDouble(10000.0));

    }

    @Test
    void sampledWorksOnSimpleFunction() {

        float[] values = new float[10];

        for (int i = 0; i < values.length; i++){
            values[i] = i;
        }
        DoubleUnaryOperator f = Functions.sampled(values, 9);

        assertEquals(1.0, f.applyAsDouble(1.0), DELTA);
        assertEquals(2.0, f.applyAsDouble(2.0), DELTA);
        assertEquals(0, f.applyAsDouble(0), DELTA);
    }

    @Test
    void sampledWorksOnNormalFunction() {

        float[] values ;

        values = new float[]{1, 4, 2, 8, 7, 25};

        DoubleUnaryOperator f = Functions.sampled(values, 25);

        assertEquals(4.4, f.applyAsDouble(12.0), DELTA);
        assertEquals(8.0, f.applyAsDouble(15.0), DELTA);


    }

    @Test
    void sampledWorksOnMinMax() {

        float[] values ;

        values = new float[]{1, 4, 2, 8, 7, 25};

        DoubleUnaryOperator f = Functions.sampled(values, 25);

        assertEquals(1, f.applyAsDouble(0.0), DELTA);
        assertEquals(25, f.applyAsDouble(25.0), DELTA);



    }


    @Test
    void constant() {
        assertEquals(17,Functions.constant(17).applyAsDouble(95));
    }

    @Test
    void weirdSampled() {
        float[] samples = {17,-95,69,420,-15};
        assertEquals(-39,Functions.sampled(samples, 8).applyAsDouble(1));
        assertEquals(-13,Functions.sampled(samples, 8).applyAsDouble(3));
        assertEquals(202.5,Functions.sampled(samples, 8).applyAsDouble(7));
        assertEquals(17,Functions.sampled(samples, 8).applyAsDouble(-17));
        assertEquals(-15,Functions.sampled(samples, 8).applyAsDouble(17));
    }

    @Test
    void simpleSampled() {
        float[] samples = {17,15,26,2,7};
        assertEquals(16,Functions.sampled(samples, 8).applyAsDouble(1));
        assertEquals(20.5,Functions.sampled(samples, 8).applyAsDouble(3));
        assertEquals(14,Functions.sampled(samples, 8).applyAsDouble(5));
        assertEquals(4.5,Functions.sampled(samples, 8).applyAsDouble(7));
        assertEquals(17,Functions.sampled(samples, 8).applyAsDouble(-17));
        assertEquals(7,Functions.sampled(samples, 8).applyAsDouble(17));
    }


}

