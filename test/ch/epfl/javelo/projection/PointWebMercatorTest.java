package ch.epfl.javelo.projection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class PointWebMercatorTest {
    double DELTA = 1e-7;
    @Test
    void pointWebMercatorConstructorWorks (){
        PointWebMercator actual = new PointWebMercator(1,1);
        assertEquals(1,actual.x(),DELTA);
        assertEquals(1,actual.y(),DELTA);

        PointWebMercator actual2 = new PointWebMercator(0,0);
        assertEquals(0,actual2.x(),DELTA);
        assertEquals(0,actual2.y(),DELTA);

        assertThrows(IllegalArgumentException.class, () -> {
            new PointWebMercator(-1,-1);
        });

        PointWebMercator actual3 = new PointWebMercator(1.0/256,1.0/256);
        assertEquals(1.0/256,actual3.x(),DELTA);
        assertEquals(1.0/256,actual3.y(),DELTA);

    }
    @Test
    void ofWorksForNormalValues (){
        PointWebMercator actual1 = PointWebMercator.of(0, 256,256);
        PointWebMercator expected1 = new PointWebMercator(1,1);
        assertEquals(expected1.x(),actual1.x(),DELTA);
        assertEquals(expected1.y(),actual1.y(),DELTA);

        PointWebMercator actual2 = PointWebMercator.of(0, 0,0);
        PointWebMercator expected2 = new PointWebMercator(0,0);
        assertEquals(expected2.x(),actual2.x(),DELTA);
        assertEquals(expected2.y(),actual2.y(),DELTA);

        PointWebMercator actual3 = PointWebMercator.of(3, 0,0);
        PointWebMercator expected3 = new PointWebMercator(0,0);
        assertEquals(expected3.x(),actual3.x(),DELTA);
        assertEquals(expected3.y(),actual3.y(),DELTA);
    }

    @Test
    void ofPointChWorksNormally () {
        PointCh actualCh = new PointCh(Ch1903.e(Math.toRadians(6.5790772),Math.toRadians(46.5218976)),Ch1903.n(Math.toRadians(6.5790772),Math.toRadians(46.5218976)));
        PointWebMercator actual = PointWebMercator.ofPointCh(actualCh);
        System.out.println(WebMercator.x(6.5790557));
        assertEquals(0.518275214444,actual.x(),DELTA);
        assertEquals(0.353664894749,actual.y(),DELTA);
    }

    @Test
    public void toPointChWorksOnKnownValues(){//Fonctionne pour DELTA = 1e-6
        PointCh actualCh = new PointCh(Ch1903.e(Math.toRadians(6.5790772),Math.toRadians(46.5218976)),Ch1903.n(Math.toRadians(6.5790772),Math.toRadians(46.5218976)));
        PointWebMercator pointWebMercator = PointWebMercator.ofPointCh(actualCh);
        PointCh point = pointWebMercator.toPointCh();
        assertEquals(pointWebMercator.lon(), point.lon(), DELTA);
        assertEquals(pointWebMercator.lat(), point.lat(), DELTA);
    }

}
