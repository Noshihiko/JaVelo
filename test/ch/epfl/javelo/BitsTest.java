package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BitsTest {
    private static final double DELTA = 1e-7;

    @Test
    void checkExtractSignedException() {
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0, 0, 32);
        });
    }

    @Test
    void checkExtractSigned() {
        var actual1 = Bits.extractSigned(5, 3, 0);
        var expected1 = 40;
        assertEquals(expected1, actual1, DELTA);
    }
}
