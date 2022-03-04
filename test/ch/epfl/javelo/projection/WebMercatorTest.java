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


    @Test
    public void xWorksOnKnownValues() {
        var actual1 = WebMercator.x(3.4);
        var expected1 = 1.041126807;
        assertEquals(expected1, actual1, DELTA);

        var actual2 = WebMercator.x(7.8);
        var expected2 = 1.741408556;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = WebMercator.x(2.9);
        var expected3 = 0.961549335;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = WebMercator.x(10.456);
        var expected4 = 2.164124085;
        assertEquals(expected4, actual4, DELTA);

        var actual5 = WebMercator.x(5.56789);
        var expected5 = 1.386157216;
        assertEquals(expected5, actual5, DELTA);
    }

    @Test
    public void yWorksOnKnownValues() {
        var actual1 = WebMercator.y(47);
        var expected1 = 0.519768309;
        assertEquals(expected1, actual1, DELTA);

        var actual2 = WebMercator.y(46.1);
        var expected2 = 0.7023264761;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = WebMercator.y(46.23);
        var expected3 = 0.6661786215;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = WebMercator.y(44.456);
        var expected4 = 0.4216189802;
        assertEquals(expected4, actual4, DELTA);

        var actual5 = WebMercator.y(47.56789);
        var expected5 = 0.4268919942;
        assertEquals(expected5, actual5, DELTA);
    }

    @Test
    public void lonWorksOnKnownValues() {
        var actual1 = WebMercator.lon(1.5);
        var expected1 = 6.283185307;
        assertEquals(expected1, actual1, DELTA);

        var actual2 = WebMercator.lon(1.0585);
        var expected2 = 3.509158994;
        assertEquals(expected2, actual2, DELTA  );

        var actual3 = WebMercator.lon(4.256);
        var expected3 = 23.59964401;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = WebMercator.lon(3.6578);
        var expected4 = 19.84104256;
        assertEquals(expected4, actual4, DELTA);

        var actual5 = WebMercator.lon(0.97);
        var expected5 = 2.953097094;
        assertEquals(expected5, actual5, DELTA);

    }

    @Test
    public void latWorksOnKnownValues() {
        var actual1 = WebMercator.lat(0.5);
        var expected1 = 0;
        assertEquals(expected1, actual1, DELTA);

        var actual2 = WebMercator.lat(0.7);
        var expected2 = -1.0162403355;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = WebMercator.lat(0.836464);
        var expected3 = -1.3304676770;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = WebMercator.lat(0.6578);
        var expected4 = -0.8602344686;
        assertEquals(expected4, actual4, DELTA);

        var actual5 = WebMercator.lat(0.12);
        var expected5 = 1.3876124945;
        assertEquals(expected5, actual5, DELTA);

    }

}
