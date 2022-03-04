package ch.epfl.javelo.projection;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
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

    double DELTA1 = 2;
    @Test
    public void toPointChWorksOnKnownValues(){//Fonctionne pour DELTA = 1e-6
        PointCh actualCh = new PointCh(Ch1903.e(Math.toRadians(6.5790772),Math.toRadians(46.5218976)),Ch1903.n(Math.toRadians(6.5790772),Math.toRadians(46.5218976)));
        PointWebMercator pointWebMercator = PointWebMercator.ofPointCh(actualCh);
        PointCh point = pointWebMercator.toPointCh();
        assertEquals(pointWebMercator.lon(), point.lon(), DELTA1);
        assertEquals(pointWebMercator.lat(), point.lat(), DELTA1);
    }





    @Test
    void pointWebMercatorConstructorThrowsOnInvalidCoordinates() {
        assertThrows(IllegalArgumentException.class, () -> {
            new PointWebMercator(0.5, -0.3);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new PointWebMercator(0.5, 1.3);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new PointWebMercator(-0.1, 0.3);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new PointWebMercator(1.5, 0.9);
        });
    }

    @Test
    void GoodOf() {
        var expected = new PointWebMercator(0.00048828125,0.00048828125);
        var actual =  PointWebMercator.of(2, 0.5, 0.5);
        assertEquals(expected, actual);
    }

    @Test
    void GoodOfPointCh() {
        // var lon = Ch1903.lon(2533132,1152206);
        // var lat = Ch1903.lat(2533132,1152206);
        //lon = :0.1146203493124217
        //lat = 0.811889008461824
        var expected = new PointWebMercator(WebMercator.x(0.1146203493124217), WebMercator.y(0.811889008461824));
        var actual = PointWebMercator.ofPointCh(new PointCh(2533132,1152206));
        assertEquals(expected, actual);
    }


    @Test
    void GoodxAtZoomLevel(){
        int zoomLevel = 2;
        var expected = 512;
        PointWebMercator a = new PointWebMercator(0.5,0.5);
        var actual = a.xAtZoomLevel(zoomLevel);
        assertEquals(expected, actual);
    }

    @Test
    void GoodyAtZoomLevel(){
        int zoomLevel = 2;
        var expected = 512;
        PointWebMercator a = new PointWebMercator(0.5,0.5);
        var actual = a.yAtZoomLevel(zoomLevel);
        assertEquals(expected, actual);
    }

    @Test
    void Goodlon(){
        PointWebMercator a = new PointWebMercator(0.5182423951719917,0.3536813812215855);
        var actual = a.lon();
        var expected =0.1146203493124217;
        assertEquals(expected, actual,9);
    }

    @Test
    void Goodlat(){
        PointWebMercator a = new PointWebMercator(0.5182423951719917,0.3536813812215855);
        var actual = a.lat();
        var expected =0.811889008461824;
        assertEquals(expected, actual,9);
    }

    @Test
    void FindNullPointCh (){
        PointWebMercator a = new PointWebMercator(0.99999,0.00001);
        var actual = a.toPointCh();
        assertEquals(null, actual);
    }

    @Test
    void pointWebMercatorConstructorThrowsOnInvalidCoordinates1() {
        assertThrows(IllegalArgumentException.class, () -> {
            new PointWebMercator(0, 1.09);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new PointWebMercator(-1, -3);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new PointWebMercator(-1, 0.4);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new PointWebMercator(0.2, -0.33);
        });
    }

    @Test
    void pointWebMercatorConstructorWorksOnValidCoordinates() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var x = rng.nextDouble(0, 1);
            var y = rng.nextDouble(0, 1);
            new PointWebMercator(x, y);
        }
    }



    @Test
    void pointWebMercatorLonWorksWithKnownValues() {
        var actual1 = new PointWebMercator(0.46,0).lon();
        var expected1 = -0.2513274123;
        assertEquals(expected1, actual1, DELTA);

        var actual2 = new PointWebMercator(0.0585,0).lon();
        var expected2 = -2.774026313;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = new PointWebMercator(0.256,0).lon();
        var expected3 = -1.533097215;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = new PointWebMercator(0.6578,0).lon();
        var expected4 = 0.9914866415;
        assertEquals(expected4, actual4, DELTA);

        var actual5 = new PointWebMercator(0.97,0).lon();
        var expected5 = 2.953097094;
        assertEquals(expected5, actual5, DELTA);
    }

    @Test
    void pointWebMercatorLatWorksWithKnownValues() {
        var actual1 = new PointWebMercator(0,0.5).lat();
        var expected1 = 0;
        assertEquals(expected1, actual1, DELTA);

        var actual2 = new PointWebMercator(0,0.7).lat();
        var expected2 = -1.0162403355;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = new PointWebMercator(0,0.836464).lat();
        var expected3 = -1.3304676770;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = new PointWebMercator(0,0.6578).lat();
        var expected4 = -0.8602344686;
        assertEquals(expected4, actual4, DELTA);

        var actual5 = new PointWebMercator(0,0.12).lat();
        var expected5 = 1.3876124945;
        assertEquals(expected5, actual5, DELTA);
    }

    //toPointCh --> null si outside switzerland sinon pt
    @Test
    void toPointChIsNotWithinSwitzerland(){
        assertEquals(null, new PointWebMercator(0.5095390103395061, 0.34851902922043904).toPointCh());
        assertEquals(null, new PointWebMercator(0.531385012654321, 0.3565392913661625).toPointCh());
        assertEquals(null, new PointWebMercator(0.5171324231481481, 0.35915551772855403).toPointCh());
    }


    @Test
    void toPointChWorksOnKnownValues1(){
        PointCh ptc1 = new PointCh(2500000,1200000);
        PointCh ptc2 = new PointCh(2500000,1080000);
        PointCh ptc3 = new PointCh(2490000,1100000);
        PointWebMercator ptw1 = new PointWebMercator(0.5170143117283951, 0.3519560067073336);
        PointWebMercator ptw2 = new PointWebMercator(0.5170861459876543, 0.3563039646390195);
        PointWebMercator ptw3 = new PointWebMercator(0.5167156022654321, 0.35559147463996593);
        assertEquals(ptc1.lon(),ptw1.lon(), DELTA);
        assertEquals(ptc1.lat(),ptw1.lat(), DELTA);
        assertEquals(ptc2.lon(),ptw2.lon(), DELTA);
        assertEquals(ptc2.lat(),ptw2.lat(), DELTA);
        assertEquals(ptc3.lon(),ptw3.lon(), DELTA);
        assertEquals(ptc3.lat(),ptw3.lat(), DELTA);
    }

    @Test
    void ofPointChIsCorrectlyInstanciated(){
        PointCh ch1 = new PointCh(2500000,1100000);
        PointCh ch2 = new PointCh(2520000,1250000);
        PointWebMercator pt1 = PointWebMercator.ofPointCh(ch1);
        PointWebMercator pt2 = PointWebMercator.ofPointCh(ch2);
        assertEquals(ch1.lon(), pt1.lon(), DELTA);
        assertEquals(ch2.lon(), pt2.lon(), DELTA);
        assertEquals(ch1.lat(), pt1.lat(), DELTA);
        assertEquals(ch2.lat(), pt2.lat(), DELTA);
    }


    @Test
    void ofComputesTheCorrectZooming(){
        PointWebMercator pt1 = PointWebMercator.of(3,1576.96,409.6);
        PointWebMercator pt2 = PointWebMercator.of(7,6553.6,14745.6);
        assertEquals(0.77, pt1.x(), DELTA );
        assertEquals(0.2, pt1.y(), DELTA );
        assertEquals(0.2, pt2.x(), DELTA );
        assertEquals(0.45, pt2.y(), DELTA );
    }

    @Test
    void atZoomLevelComputesTheCorrectZooming(){
        PointWebMercator pt1 = new PointWebMercator(0.77,0.2);
        PointWebMercator pt2 = new PointWebMercator(0.2,0.45);
        assertEquals(Math.scalb(0.77,11), pt1.xAtZoomLevel(3), DELTA );
        assertEquals(Math.scalb(0.2,11), pt1.yAtZoomLevel(3), DELTA );
        assertEquals(Math.scalb(0.2,15), pt2.xAtZoomLevel(7), DELTA );
        assertEquals(Math.scalb(0.45,15), pt2.yAtZoomLevel(7), DELTA );
    }

    private final static double lon = Math.toRadians(6.5790772);
    private final static double lat = Math.toRadians(46.5218976);

    @Test
    void pointMercatorConstructorThrowsOnInvalidCoordinates() {
        assertThrows(IllegalArgumentException.class, () -> {
            new PointWebMercator(17.15, 0.69);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new PointWebMercator(-15, 17);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new PointWebMercator(0.5, 95);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new PointWebMercator(-0.5, -0.2);
        });
    }

    @Test
    void pointMercatorConstructorWorksOnValidCoordinates() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var x = rng.nextDouble(0, 1);
            var y = rng.nextDouble(0, 1);
            new PointWebMercator(x, y);
        }
    }

    @Test
    void ofOnNapoleonZoom19() {

        PointWebMercator Napoleon = new PointWebMercator(0.518275214444,0.353664894749);
        PointWebMercator realNapoleonZoom19 = PointWebMercator.of(19,69561722,47468099);

        assertEquals(Napoleon.x(),realNapoleonZoom19.x(), 1e-6);
        assertEquals(Napoleon.y(),realNapoleonZoom19.y(), 1e-6);

    }

    @Test
    void ofPointChOnNapoleon() {

        PointWebMercator Napoleon = new PointWebMercator(0.518275214444,0.353664894749);
        PointWebMercator realNapoleon = PointWebMercator.ofPointCh(new PointCh(Ch1903.e(lon,lat),Ch1903.n(lon,lat)));

        assertEquals(Napoleon.x(),realNapoleon.x(), 1e-6);
        assertEquals(Napoleon.y(),realNapoleon.y(), 1e-6);
    }

    @Test
    void xAtZoomLevel() {
        PointWebMercator Napoleon = new PointWebMercator(0.518275214444,0.353664894749);
        assertEquals(69561722,Napoleon.xAtZoomLevel(19),1e0);
    }

    @Test
    void yAtZoomLevel() {
        PointWebMercator Napoleon = new PointWebMercator(0.518275214444,0.353664894749);
        assertEquals(47468099,Napoleon.yAtZoomLevel(19),1e0);
    }

    @Test
    void lon() {
        PointWebMercator Napoleon = new PointWebMercator(0.518275214444,0.353664894749);

        assertEquals(lon, Napoleon.lon(), 1e-6);
    }

    @Test
    void lat() {
        PointWebMercator Napoleon = new PointWebMercator(0.518275214444,0.353664894749);

        assertEquals(lat, Napoleon.lat(), 1e-6);
    }

    @Test
    void toPointChNapoleon() {
        PointCh Napoleon = new PointCh(Ch1903.e(lon,lat),Ch1903.n(lon,lat));
        PointWebMercator realNapoleon = new PointWebMercator(0.518275214444,0.353664894749);

        assertEquals(Napoleon.e(),realNapoleon.toPointCh().e(), 1e-4);
        assertEquals(Napoleon.n(),realNapoleon.toPointCh().n(), 1e-4);
    }

    @Test
    void toPointChOutOfBounds() {
        PointWebMercator testPoint = new PointWebMercator(0.17,0.17);

        assertEquals(null,testPoint.toPointCh());
        assertEquals(null,testPoint.toPointCh());
    }

}
