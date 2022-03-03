package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

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
}

