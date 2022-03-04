package ch.epfl.javelo.projection;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public final class WebMercatorTest {
    private static final double DELTA = 1e-7;

    @Test
    void checkX() {
        var actual1 = WebMercator.x(37);
        var expected1 = 6.38873289;

        assertEquals(expected1, actual1, DELTA);
    }

    @Test
    void checkY() {
        var actual1 = WebMercator.y(14);
        var expected1 = 0.0737617187;

        assertEquals(expected1, actual1, DELTA);
    }

    @Test
    void checkLon() {
        var actual1 = WebMercator.lon(5);
        var expected1 = 28.274333882308;

        assertEquals(expected1, actual1, DELTA);
    }

    @Test
    void checkLat() {
        var actual1 = WebMercator.lat(33);
        var expected1 = -1.5707963;

        assertEquals(expected1, actual1, DELTA);
    }


    @Test
    void FindxWithLon() {
        var lon = 1;
        var expected = 0.6591549431;
        var actual = WebMercator.x(lon);
        assertEquals(expected, actual,9);
    }


    @Test
    void FindyWithLat() {
        var lat = 15;
        var expected = 0.6234719739055;
        var actual =WebMercator.y(lat);
        assertEquals(expected, actual,9);
    }

    @Test
    void FindLonWithx(){
        var x = 0.5;
        var expected = 0;
        var actual =WebMercator.lon(x);
        assertEquals(expected, actual);
    }

    @Test
    void FindLonWithxButxIsNull(){
        var x = 0;
        var expected = - Math.PI;
        var actual =WebMercator.lon(x);
        assertEquals(expected, actual);
    }

    @Test
    void FindLonWithxButxIsBig(){
        var x = 100;
        var expected = 625.1769381;
        var actual =WebMercator.lon(x);
        assertEquals(expected, actual,6);
    }

    @Test
    void FindLatWithyButyIsNegative(){
        var y = -0.5;
        var expected = 1.5670614456731;
        var actual =WebMercator.lat(y);
        assertEquals(expected, actual,9);
    }

    @Test
    void FindLatWithy() {
        var y = 0.5;
        var expected = 0;
        var actual = WebMercator.lat(y);
        assertEquals(expected, actual);
    }

    @Test
    void FindLatWithyNull() {
        var y = 0;
        var expected = 1.4844222297453;
        var actual = WebMercator.lat(y);
        assertEquals(expected, actual,9);
    }
}
