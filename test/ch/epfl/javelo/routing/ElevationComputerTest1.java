package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
<<<<<<< HEAD
import ch.epfl.javelo.Math2;
=======
        >>>>>>> origin/master
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;
import ch.epfl.javelo.routing.*;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static ch.epfl.javelo.Functions.sampled;
import static org.junit.jupiter.api.Assertions.*;
import ch.epfl.javelo.routing.ElevationProfileComputer;

<<<<<<< HEAD
        =======
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static ch.epfl.javelo.routing.ElevationProfileComputer.elevationProfile;
import static java.lang.Float.NaN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
>>>>>>> origin/master

public class ElevationProfileComputerTest {

<<<<<<< HEAD
    @Test
            void elevationProfileExceptionTest(){
        int fromNodeId = 0;
        int toNodeId = 10;
        PointCh fromPoint = new PointCh(2485000, 1075000);
        PointCh toPointCh = new PointCh(2485100, 1075100);
        double length = 100;
        float[] samples = {300, 310, 305, 320, 300, 290, 305, 300, 310, 300};
        DoubleUnaryOperator a = Functions.sampled(samples, 100);
        Edge edge = new Edge(fromNodeId, toNodeId, fromPoint, toPointCh, length, a);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge);
        SingleRoute route = new SingleRoute(edges);

        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile elevationProfile1 = ElevationProfileComputer.elevationProfile(route, 0);
        } );

        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile elevationProfile1 = ElevationProfileComputer.elevationProfile(route, -1);
        } );
    }

    @Test
    void OnlyNaNTest(){
        int fromNodeId = 0;
        int toNodeId = 10;
        PointCh fromPoint = new PointCh(2485000, 1075000);
        PointCh toPointCh = new PointCh(2485100, 1075100);
        double length = 100;
        float[] samples = {Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN};
        DoubleUnaryOperator a = Functions.sampled(samples, 100);
        Edge edge = new Edge(fromNodeId, toNodeId, fromPoint, toPointCh, length, a);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge);
        SingleRoute route = new SingleRoute(edges);

        ElevationProfile NaNProfile = ElevationProfileComputer.elevationProfile(route, 10);
        assertEquals(0, NaNProfile.minElevation());
        assertEquals(0, NaNProfile.maxElevation());
        assertEquals(0, NaNProfile.totalAscent());
        assertEquals(0, NaNProfile.totalDescent());
    }

    @Test
    void BeginingNaNTest(){
        int fromNodeId = 0;
        int toNodeId = 10;
        PointCh fromPoint = new PointCh(2485000, 1075000);
        PointCh toPointCh = new PointCh(2485100, 1075100);
        double length = 100;
        float[] samples = {Float.NaN, Float.NaN, Float.NaN, 500, 502, 505, 510, 500, 520, 510};
        DoubleUnaryOperator a = Functions.sampled(samples, 100);
        Edge edge = new Edge(fromNodeId, toNodeId, fromPoint, toPointCh, length, a);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge);
        SingleRoute route = new SingleRoute(edges);

        ElevationProfile profile = ElevationProfileComputer.elevationProfile(route, 10);
        assertEquals(500.54544, profile.minElevation(), 0.00001);
    }

    @Test
    void EndNaNTest(){
=======
   /* @Test
    void ElevationProfileComputerWorkOnNormalValues() {

        var p1 = new PointCh(2600123, 1200456);
        var p2 = new PointCh(2600456, 1200789);
        var p3 = new PointCh(2600789, 1200123);
        var p4 = new PointCh(2601000, 1201000);

        var edge1 = new Edge(1, 2, p1, p2, p1.distanceTo(p2), d -> 100);


        var tab = new float[]{NaN, 289, 345, NaN, 367};
        var testElevationProfile = new ElevationProfile(20, tab);
        var actualtab = new float[]{289, 289, 345, (float) 362.6, 367};
        var actualElevationProfile = new ElevationProfile(20, actualtab);

        var listeEdge = List.of(edge1, edge2);

        var testRoute = new SingleRoute(listeEdge);

        assertEquals(elevationProfile(testRoute, 5).elevationAt(1), actualElevationProfile.elevationAt(1));
    }*/

        @Test
        void elevationProfileExceptionTest(){
>>>>>>> origin/master
            int fromNodeId = 0;
            int toNodeId = 10;
            PointCh fromPoint = new PointCh(2485000, 1075000);
            PointCh toPointCh = new PointCh(2485100, 1075100);
            double length = 100;
<<<<<<< HEAD
            float[] samples = {500, 502, 505, 510, 500, 520, 510, Float.NaN, Float.NaN, Float.NaN, };
=======
            float[] samples = {300, 310, 305, 320, 300, 290, 305, 300, 310, 300};
>>>>>>> origin/master
            DoubleUnaryOperator a = Functions.sampled(samples, 100);
            Edge edge = new Edge(fromNodeId, toNodeId, fromPoint, toPointCh, length, a);
            List<Edge> edges = new ArrayList<>();
            edges.add(edge);
            SingleRoute route = new SingleRoute(edges);
<<<<<<< HEAD

            ElevationProfile profile = ElevationProfileComputer.elevationProfile(route, 10);
            assertEquals(500, profile.minElevation());
=======

            assertThrows(IllegalArgumentException.class, () -> {
                ElevationProfile elevationProfile1 = ElevationProfileComputer.elevationProfile(route, 0);
            } );

            assertThrows(IllegalArgumentException.class, () -> {
                ElevationProfile elevationProfile1 = ElevationProfileComputer.elevationProfile(route, -1);
            } );
>>>>>>> origin/master
        }

        @Test
        void MiddleNaNTest(){
            int fromNodeId = 0;
            int toNodeId = 10;
            PointCh fromPoint = new PointCh(2485000, 1075000);
            PointCh toPointCh = new PointCh(2485100, 1075100);
            double length = 100;
            float[] samples = {500, 500, 500, 500, 500, Float.NaN, Float.NaN, Float.NaN, 500, 500 };
            DoubleUnaryOperator a = Functions.sampled(samples, 100);
            Edge edge = new Edge(fromNodeId, toNodeId, fromPoint, toPointCh, length, a);
            List<Edge> edges = new ArrayList<>();
            edges.add(edge);
            SingleRoute route = new SingleRoute(edges);

            ElevationProfile profile = ElevationProfileComputer.elevationProfile(route, 10);
            assertEquals(500, profile.minElevation());

<<<<<<< HEAD
        }

        @Test
        void multipleTunnelsTest(){
            int fromNodeId = 0;
            int toNodeId = 10;
            PointCh fromPoint = new PointCh(2485000, 1075000);
            PointCh toPointCh = new PointCh(2485100, 1075100);
            double length = 100;
            float[] samples = {Float.NaN, Float.NaN, 500, 500, Float.NaN, Float.NaN, Float.NaN, 500, 500, 500, Float.NaN, Float.NaN, Float.NaN, 500, 500, Float.NaN, Float.NaN };
            DoubleUnaryOperator a = Functions.sampled(samples, 100);
            Edge edge = new Edge(fromNodeId, toNodeId, fromPoint, toPointCh, length, a);
            List<Edge> edges = new ArrayList<>();
            edges.add(edge);
            SingleRoute route = new SingleRoute(edges);

            ElevationProfile profile = ElevationProfileComputer.elevationProfile(route, 10);
            assertEquals(500, profile.minElevation());

        }
=======
>>>>>>> origin/master
    }

