package ch.epfl.javelo.routing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ElevationProfile1Test {

    @Test
    void length() {
        float[] samples = {0,-2,4,6,10,5,-5,-7.5f};
        ElevationProfile e = new ElevationProfile(12,samples) ;
        assertEquals(12,e.length());
    }
    @Test
    void throwsOnNegativeLength(){

        float[] samples = {0,-2,4,6,10,5,-5,-7.5f};
        assertThrows(IllegalArgumentException.class,()->{
            ElevationProfile e = new ElevationProfile(-12,samples);});
    }
    @Test
    void throwsOn1Sample(){

        float[] samples = {0};
        assertThrows(IllegalArgumentException.class,()->{
            ElevationProfile e = new ElevationProfile(12,samples);});
    }

    @Test
    void minElevation() {

        float[] samples = {0,-2,4,6,10,5,-5,-7.5f};
        ElevationProfile e = new ElevationProfile(12,samples);
        assertEquals(-7.5,e.minElevation());
    }

    @Test
    void maxElevation() {

        float[] samples = {0,-2,4,6,10,5,-5,-7.5f};
        ElevationProfile e = new ElevationProfile(12,samples);
        assertEquals(10,e.maxElevation());
    }

    @Test
    void totalAscent() {

        float[] samples = {0,-2,4,6,10,5,-5,-7.5f};
        ElevationProfile e = new ElevationProfile(12,samples);
        assertEquals(12,e.totalAscent());
    }

    @Test
    void totalDescent() {
        float[] samples = {0,-2,4,6,10,5,-5,-7.5f};
        ElevationProfile e = new ElevationProfile(12,samples);
        assertEquals(19.5,e.totalDescent());
    }

    @Test
    void elevationAt() {
        float[] samples = {0,-2,4,10,5,-5};
        ElevationProfile e = new ElevationProfile(10,samples);
        //negative
        assertEquals(0,e.elevationAt(-1));
        //above length
        assertEquals(-5,e.elevationAt(13));
        // examples calculated in the middle
        assertEquals(7,e.elevationAt(5));
        assertEquals(-1,e.elevationAt(1));
        assertEquals(0,e.elevationAt(9));

    }

}
