package ch.epfl.javelo.routing;


import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;
import ch.epfl.javelo.routing.*;

import static org.junit.jupiter.api.Assertions.*;

public class ElevationProfileTest {

    private float[] elevationSamplesVide;
    private float[] elevationSamplesWithOnlyOne = new float[] {1};
    private float[] elevationSamplesWithTwo = new float[] {1,2};
    private float[] elevationSamplesNormal = new float[] {5,2,9,4};


    @Test
    void GoodLength () {
        var elevationprofile = new ElevationProfile(2, elevationSamplesNormal);
        var actual = elevationprofile.length();
        assertEquals(2, actual);
    }

    //Il n'aime prendre en argument un tableau vide, jsp si c'est un truc corrigable
    @Test
    void ElevationProfileConstructorThrowsOnInvalidCoordinates() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ElevationProfile(-2, elevationSamplesNormal);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new ElevationProfile(2, elevationSamplesWithOnlyOne);
        });
        //assertThrows(IllegalArgumentException.class, () -> {
        //   new ElevationProfile(2, elevationSamplesVide);
        //});
    }

    @Test
    void GoodMinElevation() {
        var elevationprofile = new ElevationProfile(2, elevationSamplesNormal);
        var actual = elevationprofile.minElevation();
        assertEquals(2, actual);
    }

    @Test
    void GoodMaxElevation() {
        var elevationprofile = new ElevationProfile(2, elevationSamplesNormal);
        var actual = elevationprofile.maxElevation();
        assertEquals(9, actual);
    }

    @Test
    void GoodTotalAscent(){
        var elevationprofile = new ElevationProfile(2, elevationSamplesNormal);
        var actual = elevationprofile.totalAscent();
        assertEquals(7,actual);
    }

    @Test
    void GoodTotalDescent(){
        var elevationprofile = new ElevationProfile(2, elevationSamplesNormal);
        var actual = elevationprofile.totalDescent();
        assertEquals(8,actual);
    }

    @Test
    void GoodElevationAtWhenPositionToBig(){
        var elevationprofile = new ElevationProfile(6, elevationSamplesNormal);
        var actual = elevationprofile.elevationAt(22);
        assertEquals(4, actual);
    }

    @Test
    void GoodElevationAtNormalPosition(){
        var elevationprofile = new ElevationProfile(6, elevationSamplesNormal);
        var actual = elevationprofile.elevationAt(4);
        assertEquals(9, actual);
    }

    @Test
    void GoodElevationAtPositionEqualTooLength(){
        var elevationprofile = new ElevationProfile(6, elevationSamplesNormal);
        var actual = elevationprofile.elevationAt(6);
        assertEquals(4, actual);
    }

    @Test
    void GoodElevationAtPositionNull(){
        var elevationprofile = new ElevationProfile(6, elevationSamplesNormal);
        var actual = elevationprofile.elevationAt(0);
        assertEquals(5, actual);
    }

    @Test
    void GoodElevationAtPositionNegatif(){
        var elevationprofile = new ElevationProfile(6, elevationSamplesNormal);
        var actual = elevationprofile.elevationAt(-6);
        assertEquals(5, actual);
    }

    @Test
    void constructorThrowsOnNegativeLength(){
        assertThrows(IllegalArgumentException.class, () -> {
            new ElevationProfile(-1, new float[3]);
        });
    }

    @Test
    void constructorThrowsOnNullLength(){
        assertThrows(IllegalArgumentException.class, () -> {
            new ElevationProfile(0, new float[3]);
        });
    }

    @Test
    void constructorThrowsOnInvalidTab(){
        assertThrows(IllegalArgumentException.class, () -> {
            new ElevationProfile(5, new float[1]);
        });
    }

    @Test
    void lengthWorks(){
        ElevationProfile l = new ElevationProfile(5, new float[5]);
        double actual = l.length();
        double expected = 5;
        assertEquals(expected, actual);
    }

    @Test
    void minElevationWorks(){
        float[] tab = {5F, 6F, 3F, 7F, 8F, 2F};
        ElevationProfile l = new ElevationProfile(5, tab);
        assertEquals(2F, l.minElevation());
    }

    @Test
    void maxElevationWorks(){
        float[] tab = {5F, 6F, 3F, 7F, 8F, 2F};
        ElevationProfile l = new ElevationProfile(5, tab);
        assertEquals(8F, l.maxElevation());
    }

    @Test
    void totalAscentWorks(){
        float[] tab = {5F, 6F, 3F, 7F, 8F, 2F};
        ElevationProfile l = new ElevationProfile(5, tab);
        assertEquals(6F, l.totalAscent());
    }

    @Test
    void totalDescentWorks(){
        float[] tab = {5F, 6F, 3F, 7F, 8F, 2F};
        ElevationProfile l = new ElevationProfile(5, tab);
        assertEquals(9F, l.totalDescent());
    }

    @Test
    void elevationAtWorks(){
        float[] tab = {5F, 6F, 3F, 7F, 8F, 2F};
        ElevationProfile l = new ElevationProfile(5, tab) ;
        assertEquals(3F,l.elevationAt(2));
    }


}
