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
        var expected1 = 0.4607182559;

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
        var expected1 = -90;

        assertEquals(expected1, actual1, DELTA);
    }

}
