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
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(-1, 0, 32);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0, -1, 32);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0, 15, 17);
        });
    }

    @Test
    void checkExtractUnsignedException() {
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(0, 0, 32);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(0, 1, 31);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(0, -1, 31);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(-1, 1, 30);
        });
    }

    @Test
    void checkExtractUnsigned() {
        var actual1 = Bits.extractUnsigned(15038, 8, 4);
        var expected1 = 10;
        assertEquals(expected1, actual1, DELTA);
        var actual2 = Bits.extractUnsigned(-889275714, 8, 4);
        var expected2 = 10;
        assertEquals(expected2, actual2, DELTA);
    }

    @Test
    void checkExtractSignedTest1() {
        var actual1 = Bits.extractSigned(5, 3, 0);
        var expected1 = 40;
        assertEquals(expected1, actual1, DELTA);
    }

    @Test
    void checkExtractSignedTest2() {
        var actual1 = Bits.extractSigned(-889275714, 8, 4);
        var expected1 = -6;
        assertEquals(expected1, actual1, DELTA);
    }

    @Test
    void extractSignedSimpleTest1(){
        int a = 22;
        int result = Bits.extractSigned(a, 2, 2);
        assertEquals(1, result);
    }

    @Test
    void extractSignedSimpleTest2(){
        int a = 22;
        int result = Bits.extractSigned(a, 29, 1);
        assertEquals(0, result);
    }

    @Test
    void extractSignedSimpleTestAvec1(){
        int a = 22;
        int result = Bits.extractSigned(a, 2, 3);
        assertEquals(-3, result);
    }

    @Test
    void extractSignedErrorTest1(){
        int a = 22;
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(a, 32, 2);
        });
    }

    @Test
    void extractSignedErrorTest2(){
        int a = 22;
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(a, 30, 9);
        });
    }

    @Test
    void extractSignedErrorTest3(){
        int a = 22;
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(a, 32, 1);
        });
    }

    @Test
    void extractSignedErrorTest4(){
        int a = 22;
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(a, -1, -1);
        });
    }

    @Test
    void extractUnsignedSimpleTest1(){
        int a = 22;
        int result = Bits.extractUnsigned(a, 2, 2);
        assertEquals(1, result);
    }

    @Test
    void extractUnsignedSimpleTest2(){
        int a = 22;
        int result = Bits.extractUnsigned(a, 29, 1);
        assertEquals(0, result);
    }

    @Test
    void extractUnsignedErrorTest1(){
        int a = 22;
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(a, 32, 2);
        });
    }

    @Test
    void extractUnsignedErrorTest2(){
        int a = 22;
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(a, 30, 9);
        });
    }

    @Test
    void extractUnsignedErrorTest3(){
        int a = 22;
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(a, 32, 1);
        });
    }

    @Test
    void extractUnsignedErrorTest4(){
        int a = 22;
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(a, -1, -1);
        });
    }


}
