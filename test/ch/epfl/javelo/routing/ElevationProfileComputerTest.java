package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.javelo.routing.ElevationProfileComputer.elevationProfile;
import static java.lang.Float.NaN;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ElevationProfileComputerTest {
    private static final double DELTA = 1e-7;

    @Test
    void ElevationProfileComputerWorkOnNormalValues() {

        var p1 = new PointCh(2600123, 1200456);
        var p2 = new PointCh(2600456, 1200789);
        var p3 = new PointCh(2600789, 1200123);
        var p4 = new PointCh(2601000, 1201000);

        var edge1 = new Edge(1, 2, p1, p2, p1.distanceTo(p2), d -> 100);
        var edge2 = new Edge(2, 3, p3, p4, p3.distanceTo(p4), d -> 100);

        var tab = new float[]{NaN, 289, 345, NaN, 367};
        var testElevationProfile = new ElevationProfile(20, tab);
        var actualtab = new float[]{289, 289, 345, (float) 362.6, 367};
        var actualElevationProfile = new ElevationProfile(20, actualtab);

        var listeEdge = List.of(edge1, edge2);

        var testRoute = new SingleRoute(listeEdge);

        assertEquals(elevationProfile(testRoute, 5), actualElevationProfile);
    }


}
