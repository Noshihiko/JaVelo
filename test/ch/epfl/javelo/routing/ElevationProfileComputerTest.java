package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;
import ch.epfl.javelo.routing.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.random.RandomGenerator;

import static ch.epfl.javelo.Functions.sampled;

//import static ch.epfl.javelo.TestManager.generateRandomIntInBounds;
import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.*;
import ch.epfl.javelo.routing.ElevationProfileComputer;


public class ElevationProfileComputerTest {
    private static final double DOUBLE_DELTA = 1e-5;

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
        //assertEquals(500.54544, profile.minElevation(), 0.00001);
    }

    @Test
    void EndNaNTest(){
        int fromNodeId = 0;
        int toNodeId = 10;
        PointCh fromPoint = new PointCh(2485000, 1075000);
        PointCh toPointCh = new PointCh(2485100, 1075100);
        double length = 100;
        float[] samples = {500, 502, 505, 510, 500, 520, 510, Float.NaN, Float.NaN, Float.NaN, };
        DoubleUnaryOperator a = Functions.sampled(samples, 100);
        Edge edge = new Edge(fromNodeId, toNodeId, fromPoint, toPointCh, length, a);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge);
        SingleRoute route = new SingleRoute(edges);

        ElevationProfile profile = ElevationProfileComputer.elevationProfile(route, 10);
        assertEquals(500, profile.minElevation());
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

    @Test
    void interpolationTest(){
        int fromNodeId = 0;
        int toNodeId = 10;
        PointCh fromPoint = new PointCh(2485000, 1075000);
        PointCh toPointCh = new PointCh(2485100, 1075100);
        double length = 80;
        float[] samples = {0, 50, 100, 150, Float.NaN, 150, 150};
        DoubleUnaryOperator a = Functions.sampled(samples, 80);
        Edge edge = new Edge(fromNodeId, toNodeId, fromPoint, toPointCh, length, a);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge);
        SingleRoute route = new SingleRoute(edges);

        ElevationProfile profile = ElevationProfileComputer.elevationProfile(route, 10);
    }




    @Test
    public void middleNaNTest(){

        int fromNodeId = 0;
        int toNodeId = 10;
        PointCh fromPoint = new PointCh(2685000, 1200000);
        PointCh toPoint = new PointCh(2685200, 1200200);
        double length = 50;
        float[] samples = {200, 200, 200, Float.NaN, Float.NaN, 200, 200, 200, 200 };
        DoubleUnaryOperator x = Functions.sampled(samples, 100);
        Edge edge = new Edge(fromNodeId, toNodeId, fromPoint, toPoint, length, x);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge);
        SingleRoute route = new SingleRoute(edges);

        ElevationProfile profile = ElevationProfileComputer.elevationProfile(route, 10);
        assertEquals(200, profile.minElevation());

    }

    @Test
    public void beginningNaNTest(){

        int fromNodeId = 0;
        int toNodeId = 10;
        PointCh fromPoint = new PointCh(2685000, 1200000);
        PointCh toPoint = new PointCh(2685200, 1200200);
        double length = 50;
        float[] samples = {Float.NaN, 200, 200, 200, 200, 200, 200, 200, 200, 200 };
        DoubleUnaryOperator x = Functions.sampled(samples, 100);
        Edge edge = new Edge(fromNodeId, toNodeId, fromPoint, toPoint, length, x);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge);
        SingleRoute route = new SingleRoute(edges);

        ElevationProfile profile = ElevationProfileComputer.elevationProfile(route, 10);
        assertEquals(200, profile.minElevation());

    }

    @Test
    public void endNaNTest(){

        int fromNodeId = 0;
        int toNodeId = 10;
        PointCh fromPoint = new PointCh(2685000, 1200000);
        PointCh toPoint = new PointCh(2685200, 1200200);
        double length = 50;
        float[] samples = { 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, Float.NaN };
        DoubleUnaryOperator x = Functions.sampled(samples, 100);
        Edge edge = new Edge(fromNodeId, toNodeId, fromPoint, toPoint, length, x);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge);
        SingleRoute route = new SingleRoute(edges);

        ElevationProfile profile = ElevationProfileComputer.elevationProfile(route, 10);
        assertEquals(200, profile.minElevation());

    }

    @Test
    public void multipleNaNTest(){

        int fromNodeId = 0;
        int toNodeId = 10;
        PointCh fromPoint = new PointCh(2685000, 1200000);
        PointCh toPoint = new PointCh(2685200, 1200200);
        double length = 50;
        float[] samples = { 200, 200, Float.NaN, 200, 200, Float.NaN, 200, 200, Float.NaN, 200, 200, Float.NaN, 200, 200, Float.NaN, 200 };
        DoubleUnaryOperator x = Functions.sampled(samples, 100);
        Edge edge = new Edge(fromNodeId, toNodeId, fromPoint, toPoint, length, x);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge);
        SingleRoute route = new SingleRoute(edges);

        ElevationProfile profile = ElevationProfileComputer.elevationProfile(route, 10);
        assertEquals(200, profile.minElevation());

    }

    @Test
    public void onlyNaNTest(){

        int fromNodeId = 0;
        int toNodeId = 10;
        PointCh fromPoint = new PointCh(2685000, 1200000);
        PointCh toPoint = new PointCh(2685200, 1200200);
        double length = 50;
        float[] samples = { Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN };
        DoubleUnaryOperator x = Functions.sampled(samples, 100);
        Edge edge = new Edge(fromNodeId, toNodeId, fromPoint, toPoint, length, x);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge);
        SingleRoute route = new SingleRoute(edges);

        ElevationProfile profile = ElevationProfileComputer.elevationProfile(route, 10);
        assertEquals(0, profile.minElevation());

    }

    @Test
    public void elevationProfileComputerThrowsException(){

        int fromNodeId = 0;
        int toNodeId = 10;
        PointCh fromPoint = new PointCh(2685000, 1200000);
        PointCh toPoint = new PointCh(2685200, 1200200);
        double length = 50;
        float[] samples = { 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, Float.NaN };
        DoubleUnaryOperator x = Functions.sampled(samples, 100);
        Edge edge = new Edge(fromNodeId, toNodeId, fromPoint, toPoint, length, x);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge);
        SingleRoute route = new SingleRoute(edges);

        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile profile = ElevationProfileComputer.elevationProfile(route, 0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile profile = ElevationProfileComputer.elevationProfile(route, -10);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile profile = ElevationProfileComputer.elevationProfile(route, -1);
        });



    }

    //MATHIS

    @Test
    void worksOnConstantRoad(){

        List<Edge> edges= new ArrayList<>();
        edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),100, Functions.constant(10)));
        SingleRoute route = new SingleRoute(edges);
        assertEquals(10,ElevationProfileComputer.elevationProfile(route,50).elevationAt(10));
        assertEquals(10,ElevationProfileComputer.elevationProfile(route,50).elevationAt(150));
        assertEquals(10,ElevationProfileComputer.elevationProfile(route,50).elevationAt(-50));
    }

    @Test
    void worksOnRouteWithSingleEdge(){

        List<Edge> edges= new ArrayList<>();
        float samples[] = {360f,370f};
        edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),100, Functions.sampled(samples,100)));
        SingleRoute route = new SingleRoute(edges);
        for (int i = 0; i <= 100; i++) {
            assertEquals(360+i/10.0,ElevationProfileComputer.elevationProfile(route,2).elevationAt(i),1e-4);
        }
    }
    @Test
    void worksOnRouteWithTwoEdges(){

        List<Edge> edges= new ArrayList<>();
        float samples[] = {360f,370f};
        float samples2[] = {370f,360f};
        edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),50, Functions.sampled(samples,50)));
        edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),50, Functions.sampled(samples2,50)));

        SingleRoute route = new SingleRoute(edges);
        for (int i = 0; i <= 50; i++) {
            assertEquals(360+2*i/10.0,ElevationProfileComputer.elevationProfile(route,2).elevationAt(i),1e-4);
        }
        for (int i = 0; i <= 50; i++) {
            assertEquals(370-2*i/10.0,ElevationProfileComputer.elevationProfile(route,2).elevationAt(50+i),1e-4);
        }
    }

    @Test
    void worksOnRouteWithThreeEdges(){

        List<Edge> edges= new ArrayList<>();
        float samples[] = {360f,370f};
        float samples2[] = {370f,360f};
        edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),50, Functions.sampled(samples,50)));
        edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),50, Functions.sampled(samples2,50)));
        float samples3[] = {360f,390f};

        edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),100, Functions.sampled(samples3,100)));
        SingleRoute route = new SingleRoute(edges);
        for (int i = 0; i <= 50; i++) {
            assertEquals(360+2*i/10.0,ElevationProfileComputer.elevationProfile(route,2).elevationAt(i),1e-4);
        }
        for (int i = 0; i <= 50; i++) {
            assertEquals(370-2*i/10.0,ElevationProfileComputer.elevationProfile(route,2).elevationAt(50+i),1e-4);
        }
        for (int i = 0; i <= 100; i++) {
            assertEquals(360+3*i/10.0,ElevationProfileComputer.elevationProfile(route,2).elevationAt(100+i),1e-4);
        }
    }
    @Test
    public void worksWithNoProfileRoad(){
        List<Edge> edges= new ArrayList<>();
        edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),50, Functions.constant(Float.NaN)));
        Route route = new SingleRoute(edges);
        for (int i = -11; i <= 100; i++) {
            assertEquals(0,ElevationProfileComputer.elevationProfile(route,2).elevationAt(i),1e-4);
        }
    }
    @Test
    public void firstRoadNoProfile(){
        List<Edge> edges= new ArrayList<>();
        edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),50, Functions.constant(Float.NaN)));
        float samples[] = {360f,370f};
        edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),50, Functions.sampled(samples,50)));

        Route route = new SingleRoute(edges);
        for (int i = -11; i <= 50; i++) {
            assertEquals(360,ElevationProfileComputer.elevationProfile(route,2).elevationAt(i),1e-4);
        }
        for (int i = 0; i <= 50; i++) {
            assertEquals(360+2*i/10.0,ElevationProfileComputer.elevationProfile(route,2).elevationAt(50+i),1e-4);
        }

    }
    @Test
    public void secondRoadNoProfile(){
        List<Edge> edges= new ArrayList<>();
        float samples[] = {360f,370f};
        edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),50, Functions.sampled(samples,50)));
        edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),50, Functions.constant(Float.NaN)));

        Route route = new SingleRoute(edges);

        for (int i = 0; i < 50; i++) {
            assertEquals(360+2*i/10.0,ElevationProfileComputer.elevationProfile(route,1).elevationAt(i),1e-4);
        }
        for (int i = 0; i <= 100; i++) {
            assertEquals(370,ElevationProfileComputer.elevationProfile(route,0.0001).elevationAt(50+i),1e-4);
        }

    }


    @Test
    public void roadInTheMiddleNoProfile(){
        List<Edge> edges= new ArrayList<>();
        float samples[] = {360f,370f};
        float samples2[] = {380,390f};
        edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),100, Functions.sampled(samples,100)));
        edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),100, Functions.constant(Float.NaN)));
        edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),100, Functions.sampled(samples2,100)));


        Route route = new SingleRoute(edges);

        for (int i = 0; i < 300; i++) {
            assertEquals(360+i/10.0,ElevationProfileComputer.elevationProfile(route,1).elevationAt(i),1e-4);
        }


    }

    @Test
    public void ULTIMATE(){
        List<Edge> edges= new ArrayList<>();
        edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),50, Functions.constant(Float.NaN)));
        float[] samples = {2000,5000};
        edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),150, Functions.sampled(samples,150)));
        float[] samples2 = {5000,4000};
        edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),40, Functions.sampled(samples2,40)));
        edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),160, Functions.constant(Float.NaN)));
        float[] samples3 = {8200,7000};
        edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),100, Functions.sampled(samples3,100)));
        float[] samples4 = {7000,10000};
        edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),80, Functions.sampled(samples4,80)));
        edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),150, Functions.constant(Float.NaN)));
        float[] samples5 = {4000,6100};
        edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),70, Functions.sampled(samples5,70)));
        edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),100, Functions.constant(Float.NaN)));

        //Data taken from this beautiful piazza post
        //https://piazza.com/class/kzifjghz6po4se?cid=560


        Route route = new SingleRoute(edges);

        assertEquals(2500,ElevationProfileComputer.elevationProfile(route,76).elevationAt(0));
        assertEquals(2500,ElevationProfileComputer.elevationProfile(route,76).elevationAt(20));
        assertEquals(2500,ElevationProfileComputer.elevationProfile(route,76).elevationAt(50));
        assertEquals(3000,ElevationProfileComputer.elevationProfile(route,76).elevationAt(100));
        assertEquals(3500,ElevationProfileComputer.elevationProfile(route,76).elevationAt(125));
        assertEquals(4000,ElevationProfileComputer.elevationProfile(route,76).elevationAt(150));
        assertEquals(4200,ElevationProfileComputer.elevationProfile(route,76).elevationAt(190));
        assertEquals(4375,ElevationProfileComputer.elevationProfile(route,76).elevationAt(225));
        assertEquals(5450,ElevationProfileComputer.elevationProfile(route,76).elevationAt(300));
        assertEquals(6525,ElevationProfileComputer.elevationProfile(route,76).elevationAt(375));
        assertEquals(7600,ElevationProfileComputer.elevationProfile(route,76).elevationAt(450));
        assertEquals(7937.5,ElevationProfileComputer.elevationProfile(route,76).elevationAt(525));
        assertEquals(6825,ElevationProfileComputer.elevationProfile(route,76).elevationAt(600));
        assertEquals(5712.5,ElevationProfileComputer.elevationProfile(route,76).elevationAt(675));
        assertEquals(4600,ElevationProfileComputer.elevationProfile(route,76).elevationAt(750));
        assertEquals(4600,ElevationProfileComputer.elevationProfile(route,76).elevationAt(800));
        assertEquals(4600,ElevationProfileComputer.elevationProfile(route,76).elevationAt(900));
        assertEquals(4600,ElevationProfileComputer.elevationProfile(route,76).elevationAt(1000));
    }




    //NOE


    private static final double DELTA = 1e-7;

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
    void elevationProfileExceptionTest1(){
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


























    //YOUSSEF
    /*

    Graph loadLausanne() {
        try {
            Graph g = Graph.loadFrom(Path.of("lausanne"));
            return g;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private int getRandomEdgeId(Graph g, int nodeId) {
        return g.nodeOutEdgeId(nodeId, generateRandomIntInBounds(0, g.nodeOutDegree(nodeId) - 1));
    }
    */



//    void testNodeSamples() {
//        Route r = createRandomRoute(2);
//        List<Edge> edges = r.edges();
//
//        Edge edge1 = edges.get(0);
//        Edge edge2 = edges.get(1);
//
//        System.out.println(r.elevationAt(edge1.length()));
//        System.out.println(r.elevationAt(edge1.length() + 0.1));
//    }

    /*

    Route createRandomRoute(int length) {
        Edge[] edges = new Edge[length];
        Graph g = loadLausanne();

        // start with random node
        int startNodeId = generateRandomIntInBounds(0, g.nodeCount() - 1);
        int edgeOutId = getRandomEdgeId(g, startNodeId);

        Edge e = Edge.of(g, edgeOutId, startNodeId, g.edgeTargetNodeId(edgeOutId));
        edges[0] = e;

        // System.out.println("ID= " + edgeOutId + "--" + e);

        for (int i = 1; i < length; i++) {
            int newNodeId = edges[i - 1].toNodeId();
            int newEdgeId = getRandomEdgeId(g, newNodeId);

            edges[i] = Edge.of(g, newEdgeId, newNodeId, g.edgeTargetNodeId(newEdgeId));

            // System.out.println("ID= " + newEdgeId + "--" + edges[i]);
        }

        return new SingleRoute(List.of(edges));
    }

    Route createKnownRoute() {
        List<Edge> edges = new ArrayList<>();
        DoubleUnaryOperator profile1 = Functions.sampled(new float[]{1, 5, 7, 8, 9}, 4);
        DoubleUnaryOperator profile2 = Functions.sampled(new float[]{3, 7, 8, 2}, 3);
        DoubleUnaryOperator profile3 = Functions.constant(Float.NaN);
        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N), 4, profile1));
        edges.add(new Edge(1, 2, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E, SwissBounds.MAX_N), 6, profile3));
        edges.add(new Edge(2, 0, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 6, SwissBounds.MAX_N), 3, profile2));
        SingleRoute route = new SingleRoute(edges);

        return route;
    }

    private PointCh relPoint(double e, double n) {
        double newE = Math2.clamp(SwissBounds.MIN_E, SwissBounds.MIN_E + e, SwissBounds.MAX_E);
        double newN = Math2.clamp(SwissBounds.MIN_N, SwissBounds.MIN_N + e, SwissBounds.MAX_N);

        return new PointCh(newE, newN);
    }

    Route createConstantTestRoute() {
        List<Edge> edges = new ArrayList<>();
        DoubleUnaryOperator profile1 = Functions.sampled(new float[]{0, 2, 4, 2, 1}, 4);
        DoubleUnaryOperator profile2 = Functions.sampled(new float[]{1, 0, 2, 0, 2}, 2);
        DoubleUnaryOperator profile3 = Functions.constant(Float.NaN);

        edges.add(new Edge(0, 1, relPoint(1, 1), relPoint(10, 10), 4, profile1));
        edges.add(new Edge(1, 2, relPoint(10, 10), relPoint(2, 20), 2, profile2));
        edges.add(new Edge(2, 0, relPoint(20, 2), relPoint(4, 4), 1, profile3));

        return new SingleRoute(edges);
    }

    Route createInterpolatedTestRoute() {
        List<Edge> edges = new ArrayList<>();
        DoubleUnaryOperator profile1 = Functions.sampled(new float[]{0, 2, 4, 2, 1}, 4);
        DoubleUnaryOperator profile2 = Functions.sampled(new float[]{1, 0, 2, 0, 2}, 2);
        DoubleUnaryOperator profile3 = Functions.constant(Float.NaN);


        edges.add(new Edge(0, 1, relPoint(1, 1), relPoint(10, 10), 4, profile1));
        edges.add(new Edge(1, 2, relPoint(10, 10), relPoint(2, 20), 2, profile2));
        edges.add(new Edge(2, 0, relPoint(20, 2), relPoint(4, 4), 1.5, profile3));
        edges.add(new Edge(0, 3, relPoint(1, 1), relPoint(6, 1), 2, profile2));

        return new SingleRoute(edges);
    }

    Route createKnownLausanneRoute() {
        List<Edge> edges = new ArrayList<>();

        Graph g = loadLausanne();
        int[][] edgeInfo = new int[][] {{108256, 51244, 51245}, {108258, 51245, 51247}, {108265, 51247, 51246}, {108260, 51246, 51247}, {108263, 51247, 51245}, {108258, 51245, 51247}, {108262, 51247, 51253}, {108276, 51253, 51193}, {108145, 51193, 51192}, {108144, 51192, 51193}};

        for(int[] e : edgeInfo) {
            edges.add(Edge.of(g, e[0], e[1], e[2]));
            System.out.println();
        }

        return new SingleRoute(edges);
    }

    */

    /*

    @Test
    void elevationProfileDoesThrowException() {
        Route r = createKnownRoute();

        // negative values
        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfileComputer.elevationProfile(r, -3);
        });

        // positive (=0) values
        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfileComputer.elevationProfile(r, 0);
        });

        // regular (>0) values
        assertDoesNotThrow(() -> {
            ElevationProfileComputer.elevationProfile(r, 1);
        });
    }

    //@Test
    void elevationProfileDoesWorkWithKnownRoute() {
        Route r = createConstantTestRoute();

        float[] expectedSamples = {0, 1, 2, 3, 4, 3, 2, 1.5f, 1, 1, 0, 2, 0, 2, 2, 2}; // step : 0.5, use this to double-check stuff.

        System.out.println(r.length() );
        ElevationProfile elev = ElevationProfileComputer.elevationProfile(r, 0.5);

        System.out.println(r.length() / 14 );
        assertEquals(r.length(), elev.length());
        assertEquals(0, elev.minElevation(), DOUBLE_DELTA);
        assertEquals(4, elev.maxElevation(), DOUBLE_DELTA);

        assertEquals(12, elev.totalAscent(), DOUBLE_DELTA);
        assertEquals(6, elev.totalDescent(), DOUBLE_DELTA);

        assertEquals(4, elev.elevationAt(2), DOUBLE_DELTA);
        assertEquals(2, elev.elevationAt(7), DOUBLE_DELTA);
    }

    */

    /*


    //@Test
    void elevationProfileDoesWorkWithInterpolatedRoute() {
        Route r = createInterpolatedTestRoute();

        ElevationProfile elev = ElevationProfileComputer.elevationProfile(r, 0.5);

        // [0.0, 1.0, 2.0, 3.0, 4.0, 3.0, 2.0, 1.5, 1.0, 0.0, 2.0, 0.0, 2.0, 1.5, 1.0, 0.5, 1, 0, 2.0, 0.0, 2.0]
        float[] expectedSamples = {0, 1, 2, 3, 4, 3, 2, 1.5f, 1, 1, 0, 2, 0, 2, 1.5f, 1f, 0.5f, 1, 0, 2, 0, 2}; // step : 0.5, use this to double-check stuff.

        assertEquals(r.length(), elev.length());
        assertEquals(0, elev.minElevation(), DOUBLE_DELTA);
        assertEquals(4, elev.maxElevation(), DOUBLE_DELTA);

        assertEquals(8, elev.totalAscent(), DOUBLE_DELTA);
        assertEquals(6, elev.totalDescent(), DOUBLE_DELTA);

        assertEquals(1.666666f, elev.elevationAt(15), DOUBLE_DELTA);
        assertEquals(1.5f, elev.elevationAt(8), DOUBLE_DELTA);

    }

    */


    //MADHI


    @Test
    void elevationProfileExceptionTest12(){
        Edge[] edges = new Edge[3];
        PointCh p1 = new PointCh(2600123, 1200456);
        PointCh p2 = new PointCh(2600456, 1200789);
        PointCh p3 = new PointCh(2600789, 1200123);
        PointCh p4 = new PointCh(2601000, 1201000);
        PointCh[] PointsTot ={p1, p2, p3, p4};
        edges[0] = new Edge(1, 2, p1, p2, p1.distanceTo(p2), x -> Double.NaN);
        edges[1] = new Edge(2, 3, p2, p3, p2.distanceTo(p3), x -> Double.NaN);
        edges[2] = new Edge(3, 4, p3, p4, p3.distanceTo(p4), x -> Double.NaN);
        SingleRoute route = new SingleRoute(List.of(edges));

        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile elev1 = ElevationProfileComputer.elevationProfile(route, 0);
        } );

        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile elev2 = ElevationProfileComputer.elevationProfile(route, -0.66);
        } );
        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile elev2 = ElevationProfileComputer.elevationProfile(route, -1);
        } );
    }

    @Test
    void OnlyNaNTest12(){
        var p1 = new PointCh(2600123, 1200456);
        var p2 = new PointCh(2600456, 1200789);
        /** in case x -> Double.NaN doesnt mean an empty profil use this :
         * float[] samples = {Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN};
         *     DoubleUnaryOperator profil = Functions.sampled(samples, p1.distanceTo(p2));
         *     Edge edge1 = new Edge(fromNodeId, toNodeId, p1, p2, p1.distanceTo(p2), profil);
         */
        var edge1 = new Edge(1, 2, p1, p2, p1.distanceTo(p2), x -> Double.NaN);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        SingleRoute route = new SingleRoute(edges);

        ElevationProfile NaNProfile = ElevationProfileComputer.elevationProfile(route, 10);

        assertEquals(0, NaNProfile.minElevation());
        assertEquals(0, NaNProfile.maxElevation());
        assertEquals(0, NaNProfile.totalAscent());
        assertEquals(0, NaNProfile.totalDescent());
    }

    /*@Test
    void BeginingNaNTest(){

        var p1 = new PointCh(2600123, 1200456);
        var p2 = new PointCh(2600456, 1200789);
        float[] samples = {Float.NaN, Float.NaN, Float.NaN, 500, 502, 505, 510, 500, 520, 510};
        DoubleUnaryOperator profil = Functions.sampled(samples, 100);
        var edge1 = new Edge(1, 2, p1, p2, p1.distanceTo(p2), profil);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        SingleRoute route = new SingleRoute(edges);

        ElevationProfile profile = ElevationProfileComputer.elevationProfile(route, 10);
        assertEquals(500.54544, profile.minElevation(), 0.00001);
    }*/

    @Test
    void EndNaNTest12(){

        var p1 = new PointCh(2600123, 1200456);
        var p2 = new PointCh(2600456, 1200789);
        float[] samples = {500, 502, 505, 510, 500, 520, 510, Float.NaN, Float.NaN, Float.NaN, };
        DoubleUnaryOperator profil = Functions.sampled(samples, 100);
        var edge1 = new Edge(1, 2, p1, p2, p1.distanceTo(p2), profil);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        SingleRoute route = new

                SingleRoute(edges);

        ElevationProfile profile = ElevationProfileComputer.
                elevationProfile(route, 10);
        assertEquals(500, profile.minElevation());
    }

    @Test
    void MiddleNaNTest12(){
        var p1 = new PointCh(2600123, 1200456);
        var p2 = new PointCh(2600456, 1200789);
        float[] samples = {500, 500, 500, 500, 500, Float.NaN, Float.NaN, Float.NaN, 500, 500 };
        DoubleUnaryOperator profil = Functions.sampled(samples, 100);
        var edge1 = new Edge(1, 2, p1, p2, p1.distanceTo(p2), profil);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        SingleRoute route = new SingleRoute(edges);

        ElevationProfile profile = ElevationProfileComputer.elevationProfile(route, 10);
        assertEquals(500, profile.minElevation());


    }




    //@Test
    void testingNaNCases() {
        var rng = newRandom();


        //Only NaN
        PointCh A0 = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 1000);
        PointCh B0 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N + 1000);

        Edge edge0 = new Edge(0, 1, A0, B0, 20, (x) -> Double.NaN);

        Route route0 = new SingleRoute(List.of(edge0));
        ElevationProfile test0 = ElevationProfileComputer.elevationProfile(route0, 1);

        for (int i = 0; i < RANDOM_ITERATIONS; i++) {
            assertEquals(0, test0.elevationAt(rng.nextDouble(0, 20)));
        }

        //[NaN, num, NaN]
        PointCh A1 = new PointCh(SwissBounds.MIN_E + 00, SwissBounds.MIN_N + 1000);
        PointCh B1 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N + 1000);
        PointCh C1 = new PointCh(SwissBounds.MIN_E + 40, SwissBounds.MIN_N + 1000);
        PointCh D1 = new PointCh(SwissBounds.MIN_E + 60, SwissBounds.MIN_N + 1000);

        Edge edge10 = new Edge(0, 1, A1, B1, 20, (x) -> Double.NaN);
        Edge edge11 = new Edge(0, 1, B1, C1, 20, (x) -> 100);
        Edge edge12 = new Edge(0, 1, C1, D1, 20, (x) -> Double.NaN);

        Route route1 = new SingleRoute(List.of(edge10, edge11, edge12));
        ElevationProfile test1 = ElevationProfileComputer.elevationProfile(route1, 1);

        for (int i = 0; i < RANDOM_ITERATIONS; i++) {
            double randPos = rng.nextDouble(0, 60);
            assertEquals(100, test1.elevationAt(randPos));
        }

        //[n0, NaN, n0] where n0 is a real positive number
        PointCh A2 = new PointCh(SwissBounds.MIN_E + 00, SwissBounds.MIN_N + 1000);
        PointCh B2 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N + 1000);
        PointCh C2 = new PointCh(SwissBounds.MIN_E + 40, SwissBounds.MIN_N + 1000);
        PointCh D2 = new PointCh(SwissBounds.MIN_E + 60, SwissBounds.MIN_N + 1000);

        Edge edge20 = new Edge(0, 1, A2, B2, 20, (x) -> 100);
        Edge edge21 = new Edge(0, 1, B2, C2, 20, (x) -> Double.NaN);
        Edge edge22 = new Edge(0, 1, C2, D2, 20, (x) -> 100);

        Route route2 = new SingleRoute(List.of(edge20, edge21, edge22));
        ElevationProfile test2 = ElevationProfileComputer.elevationProfile(route2, 1);

        for (int i = 0; i < RANDOM_ITERATIONS; i++) {
            double randPos = rng.nextDouble(0, 60);
            assertEquals(100, test2.elevationAt(randPos));
        }


        //[n0, NaN, n1] where n0, n1 are distinct real positive numbers
        PointCh A3 = new PointCh(SwissBounds.MIN_E + 00, SwissBounds.MIN_N + 1000);
        PointCh B3 = new PointCh(SwissBounds.MIN_E + 40, SwissBounds.MIN_N + 1000);
        PointCh C3 = new PointCh(SwissBounds.MIN_E + 60, SwissBounds.MIN_N + 1000);
        PointCh D3 = new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N + 1000);

        Edge edge30 = new Edge(0, 1, A3, B3, 40, (x) -> 100);
        Edge edge31 = new Edge(0, 1, B3, C3, 20, (x) -> Double.NaN);
        Edge edge32 = new Edge(0, 1, C3, D3, 40, (x) -> 200);

        Route route3 = new SingleRoute(List.of(edge30, edge31, edge32));
        ElevationProfile test3 = ElevationProfileComputer.elevationProfile(route3, 1);

        for (int i = 0; i < RANDOM_ITERATIONS; i++) {
            double randPos = rng.nextDouble(0, 100);
            if (randPos >= 60 && randPos < 100)
                assertEquals(200, test3.elevationAt(randPos));
            else if (randPos > 0 && randPos <= 40) {
                System.out.println(randPos);
                assertEquals(100, test3.elevationAt(randPos));
            }
            else {
                assertEquals(5 * randPos - 100, test3.elevationAt(randPos), 0.0001);
            }
        }

    }
    @Test
    void BeginingNaNTest12(){
        var rng = newRandom();

        PointCh A1 = new PointCh(SwissBounds.MIN_E + 00, SwissBounds.MIN_N + 1000);
        PointCh B1 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N + 1000);
        PointCh C1 = new PointCh(SwissBounds.MIN_E + 40, SwissBounds.MIN_N + 1000);
        PointCh D1 = new PointCh(SwissBounds.MIN_E + 60, SwissBounds.MIN_N + 1000);

        Edge edge10 = new Edge(0, 1, A1, B1, 20, (x) -> Double.NaN);
        Edge edge11 = new Edge(0, 1, B1, C1, 20, (x) -> 100);
        Route route1 = new SingleRoute(List.of(edge10, edge11));
        ElevationProfile test1 = ElevationProfileComputer.elevationProfile(route1, 1);

        for (int i = 0; i < RANDOM_ITERATIONS; i++) {
            double randPos = rng.nextDouble(0, 60);
            assertEquals(100, test1.elevationAt(randPos));
        }


    }



    //PIERRE


    private final static List<Edge> edges;
    private final static List<PointCh> points;
    static{
        points = new ArrayList<>();
        points.add(new PointCh(SwissBounds.MIN_E+0,SwissBounds.MIN_N+0));
        points.add(new PointCh(SwissBounds.MIN_E+8,SwissBounds.MIN_N+0));
        points.add(new PointCh(SwissBounds.MIN_E+17,SwissBounds.MIN_N+0));
        points.add(new PointCh(SwissBounds.MIN_E+23,SwissBounds.MIN_N+0));
        points.add(new PointCh(SwissBounds.MIN_E+31,SwissBounds.MIN_N+0));
        points.add(new PointCh(SwissBounds.MIN_E+40,SwissBounds.MIN_N+0));
        points.add(new PointCh(SwissBounds.MIN_E+46,SwissBounds.MIN_N+0));
        points.add(new PointCh(SwissBounds.MIN_E+54,SwissBounds.MIN_N+0));
        points.add(new PointCh(SwissBounds.MIN_E+63,SwissBounds.MIN_N+0));
        points.add(new PointCh(SwissBounds.MIN_E+69,SwissBounds.MIN_N+0));
        points.add(new PointCh(SwissBounds.MIN_E+1,SwissBounds.MIN_N+0));
        points.add(new PointCh(SwissBounds.MIN_E+2,SwissBounds.MIN_N+0));
        points.add(new PointCh(SwissBounds.MIN_E+3,SwissBounds.MIN_N+0));
        points.add(new PointCh(SwissBounds.MIN_E+4,SwissBounds.MIN_N+0));

        edges = new ArrayList<>();
        addEdge(0,1, new float[]{0,10,0,10,0});
        addNaNEdge(1,2);
        addEdge(2,3, new float[]{10,12,14,15,16,17,4});
        addNaNEdge(0,1);
        addNaNEdge(1,2);
        addEdge(3,4, new float[]{0,10,0,10,0});
        addNaNEdge(4,5);
        addEdge(5,6, new float[]{10,12,14,15,16,17,4});
        addEdge(6,7, new float[]{0,10,0,10,0});
        addNaNEdge(7,8);
        addEdge(8,9, new float[]{10,12,14,15,16,17,4});
        addNaNEdge(10,11);
        addEdge(11,12, new float[]{10,11});
        addNaNEdge(12,13);


    }
    private final static void addEdge(int from, int to, float samples[] ){
        double length = points.get(from).distanceTo(points.get(to));
        edges.add(new Edge(from, to, points.get(from), points.get(to),length,
                Functions.sampled(samples, length)
        ));
    }
    private final static void addNaNEdge(int from, int to){
        double length = points.get(from).distanceTo(points.get(to));
        edges.add(new Edge(from, to, points.get(from), points.get(to),length,
                Functions.constant(Float.NaN)
        ));
    }
    private final static RoutePoint ofRoutePoint(double e, double n, double position, double distance ){
        return new RoutePoint(ofPoint(e,n), position, distance);
    }
    private final static List<Edge> getEdges(int...ids){
        List<Edge> edgesC = new ArrayList<>();
        for(int i : ids){
            edgesC.add(edges.get(i));
        }
        return edgesC;
    }

    private static PointCh ofPoint(double e, double n){
        return new PointCh(SwissBounds.MIN_E+e, SwissBounds.MIN_N+n);

    }

    @Test
    public void elevationProfileWorksOnTrivialRoute(){
        Route route = new SingleRoute(getEdges(0,1,2));
        ElevationProfile elevationProfile = ElevationProfileComputer.elevationProfile(route, 2);
        double stepLength = (23.0/12.0);
        //System.out.println(edges.get(2).elevationAt(0.25));
        assertEquals(8.75f,elevationProfile.elevationAt(5.75),1e-7 );
        assertEquals(5/3.0, elevationProfile.elevationAt(4*stepLength),1e-7);
        assertEquals(10.5, elevationProfile.elevationAt(17.25),1e-7);
        assertEquals(85/6.0, elevationProfile.elevationAt(10*stepLength),1e-6);
        assertEquals(103/30.0, elevationProfile.elevationAt(5*stepLength),1e-6);
        assertEquals(5.2, elevationProfile.elevationAt(6*stepLength),1e-6);
        assertEquals(209/30.0, elevationProfile.elevationAt(7*stepLength),1e-6);
        assertEquals(131/15.0, elevationProfile.elevationAt(8*stepLength),1e-6);
        assertEquals(0, elevationProfile.elevationAt(-100),1e-6);
        assertEquals(0, elevationProfile.elevationAt(0),1e-6);
        assertEquals(4, elevationProfile.elevationAt(23),1e-6);
        assertEquals(4, elevationProfile.elevationAt(100),1e-6);
        assertEquals(23197/6000.0, elevationProfile.elevationAt(5.245*stepLength),1e-6);
        assertEquals(209053/30000.0, elevationProfile.elevationAt(7.001*stepLength),1e-6);
        //assertEquals(308149/30000.0, elevationProfile.elevationAt(8.473*stepLength),1e-7);
        assertEquals(23, elevationProfile.length());
        assertEquals(17, elevationProfile.maxElevation(),1);
        assertEquals(0,elevationProfile.minElevation());
    }

    @Test
    public void elevationProfileWorksOnFullNaNEdges(){
        ElevationProfile elevationProfile = ElevationProfileComputer.elevationProfile(new SingleRoute(getEdges(3,4)),2);
        assertEquals(17, elevationProfile.length());
        RandomGenerator random = TestRandomizer.newRandom();
        for(int i  = 0; i < 100; i++){
            assertEquals(0, elevationProfile.elevationAt(17*2*random.nextDouble()-17));
        }
        assertEquals(0, elevationProfile.elevationAt(0));
        assertEquals(0, elevationProfile.elevationAt(-100));
        assertEquals(0, elevationProfile.elevationAt(18));
        assertEquals(0, elevationProfile.elevationAt(5));
    }

    @Test
    public void elevationProfileWorksOnMultiNaNEdges(){
        Route route = new SingleRoute(getEdges(0,1,2,5,6,7,8,9,10));
        ElevationProfile elevationProfile = ElevationProfileComputer.elevationProfile(route, 2);
        double stepLength = (69/35.0);
       // assertEquals(4/7.0, elevationProfile.elevationAt(4*stepLength),1e-6);
        assertEquals(402/35.0, elevationProfile.elevationAt(9*stepLength),1e-6);
        assertEquals(482/175.0, elevationProfile.elevationAt(5*stepLength),1e-6);
        assertEquals(9.02857133333, elevationProfile.elevationAt(17*stepLength),1e-6);
        route = new SingleRoute(getEdges(11,12,13));
        elevationProfile = ElevationProfileComputer.elevationProfile(route, 0.1);



    }






    //@Test
    public void middle() {
        PointCh A3 = new PointCh(SwissBounds.MIN_E + 00, SwissBounds.MIN_N + 1000);
        PointCh B3 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N + 1000);
        PointCh C3 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N + 1000);
        PointCh D3 = new PointCh(SwissBounds.MIN_E + 30, SwissBounds.MIN_N + 1000);
        Edge edge30 = new Edge(0, 1, A3, B3, 10, (x) -> 10);
        Edge edge31 = new Edge(0, 1, B3, C3, 10, (x) -> Double.NaN);
        Edge edge32 = new Edge(0, 1, C3, D3, 10, (x) -> 20);
        Route route3 = new SingleRoute(List.of(edge30, edge31, edge32));
        ElevationProfile test3 = ElevationProfileComputer.elevationProfile(route3, 1);
        assertEquals(10, test3.elevationAt(10));
        assertEquals(11, test3.elevationAt(11));
        assertEquals(12, test3.elevationAt(12));
        assertEquals(13, test3.elevationAt(13));
        assertEquals(14, test3.elevationAt(14));
        assertEquals(15, test3.elevationAt(15));
        assertEquals(16, test3.elevationAt(16));
        assertEquals(20, test3.elevationAt(20));
        assertEquals(20, test3.elevationAt(22));
        assertEquals(20, test3.elevationAt(26));
    }








    //@Test
    void elevationProfileWorksProperly() {
        List<Edge> list = new ArrayList<>();
        DoubleUnaryOperator profile1 = Functions.sampled(new float[]{2, 1, 2, 3, 2, 1, 0.5f, 1, 1.5f, 1, 0.5f}, 10);
        DoubleUnaryOperator profile2 = Functions.sampled(new float[]{0.5f, 4, 5, 6, 6, 7}, 5);
        DoubleUnaryOperator profile3 = Functions.sampled(new float[]{7, 156, 134, 3, 456, 133, 34}, 6);

        list.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N), 10, profile1));
        list.add(new Edge(1, 2, new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 13, SwissBounds.MIN_N + 4), 5, profile2));
        list.add(new Edge(2, 3, new PointCh(SwissBounds.MIN_E + 13, SwissBounds.MIN_N + 4), new PointCh(SwissBounds.MIN_E + 13, SwissBounds.MIN_N + 10), 6, profile3));
        SingleRoute route = new SingleRoute(list);

        ElevationProfile elevationProfile = ElevationProfileComputer.elevationProfile(route, 1);
        ElevationProfile expected = new ElevationProfile(21, new float[]{2, 1, 2, 3, 2, 1, 0.5f, 1, 1.5f, 1, 0.5f, 4, 5, 6, 6, 7, 156, 134, 3, 456, 133, 34});
        assertEquals(expected.minElevation(), elevationProfile.minElevation());
    }

    //@Test
    void elevationProfileThrowsOnNegativeStepLength() {
        assertThrows(IllegalArgumentException.class, () -> ElevationProfileComputer.elevationProfile(new SingleRoute(List.of()), 0));
    }

    //@Test
    void elevationProfileWorksOnOnlyNan() {
        List<Edge> edges = new ArrayList<>();
        DoubleUnaryOperator profile1 = Functions.constant(Float.NaN);
        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N), 10, profile1));
        SingleRoute route = new SingleRoute(edges);
        ElevationProfile expected1 = new ElevationProfile(10, new float[]{0, 0, 0, 0, 0});
        ElevationProfile elevationProfile1 = ElevationProfileComputer.elevationProfile(route, 3);
        assertEquals(expected1, elevationProfile1);
    }

    //@Test
    void elevationProfileWorksOnNanAtTheBeginningAndEnd() {
        List<Edge> edges = new ArrayList<>();
        DoubleUnaryOperator profile1 = Functions.sampled(new float[]{3, 2, 3, 4, 6, 7, 9}, 6);
        DoubleUnaryOperator profile2 = Functions.constant(Float.NaN);
        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N), 6, profile2));
        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N), 6, profile1));
        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 6, SwissBounds.MAX_N), 6, profile2));
        SingleRoute route = new SingleRoute(edges);

        ElevationProfile expected1 = new ElevationProfile(18, new float[]{3, 3, 3, 4, 9, 9, 9});
        ElevationProfile elevationProfile1 = ElevationProfileComputer.elevationProfile(route, 3);
        assertEquals(expected1.minElevation(), elevationProfile1.minElevation());

    }

    //@Test
    void elevationProfileWorksOnNanInTheMiddle() {
        List<Edge> edges = new ArrayList<>();
        DoubleUnaryOperator profile1 = Functions.sampled(new float[]{1, 5, 7, 8, 9}, 4);
        DoubleUnaryOperator profile2 = Functions.sampled(new float[]{3, 7, 8, 2}, 3);
        DoubleUnaryOperator profile3 = Functions.constant(Float.NaN);
        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N), 4, profile1));
        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E, SwissBounds.MAX_N), 6, profile3));
        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 6, SwissBounds.MAX_N), 3, profile2));
        SingleRoute route = new SingleRoute(edges);

        ElevationProfile expected1 = new ElevationProfile(13, new float[]{1, 5, 7, 8, 9, 8, 7, 6, 5, 4, 3, 7, 8, 2});
        ElevationProfile elevationProfile1 = ElevationProfileComputer.elevationProfile(route, 1);
        assertEquals(expected1, elevationProfile1);
    }

    //@Test
    void elevationProfileWorksInComplexProfile() {
        List<Edge> edges = new ArrayList<>();
        DoubleUnaryOperator profile1 = Functions.sampled(new float[]{1, 2, 5, 4, 3}, 4);
        DoubleUnaryOperator profile2 = Functions.sampled(new float[]{5, 6, 5, 4}, 3);
        DoubleUnaryOperator profile3 = Functions.sampled(new float[]{1, 2, 3}, 2);
        DoubleUnaryOperator profile4 = Functions.constant(Float.NaN);

        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N), 4, profile1));
        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E, SwissBounds.MAX_N), 4, profile4));
        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E, SwissBounds.MAX_N), 3, profile2));
        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E, SwissBounds.MAX_N), 3, profile4));
        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E, SwissBounds.MAX_N), 2, profile3));
        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 6, SwissBounds.MAX_N), 4, profile4));
        SingleRoute route = new SingleRoute(edges);

        ElevationProfile expected1 = new ElevationProfile(20, new float[]{1, 2, 5, 4, 3, 3.5f, 4, 4.5f, 5, 6, 5, 4, 3, 2, 1, 2, 3, 3, 3, 3, 3});
        ElevationProfile elevationProfile1 = ElevationProfileComputer.elevationProfile(route, 1);
        assertEquals(expected1, elevationProfile1);

        ElevationProfile expected2 = new ElevationProfile(20, new float[]{1, 5, 3, 4, 5, 5, 3, 1, 3, 3, 3});
        ElevationProfile elevationProfile2 = ElevationProfileComputer.elevationProfile(route, 2);
        assertEquals(expected2, elevationProfile2);

    }
}