package ch.epfl.javelo.routing;
import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;
import ch.epfl.javelo.routing.*;

import java.io.IOException;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;
import ch.epfl.javelo.routing.SingleRoute;


public class SingleRouteTest {

    private List<Edge> ListOfEdge =  new ArrayList<>();


    @Test
    void ElevationProfileConstructorThrowsOnInvalidCoordinates() {
        assertThrows(IllegalArgumentException.class, () -> {
            //System.out.println(ListOfEdgeEmpty.isEmpty());
            new SingleRoute(ListOfEdge);
        });
    }

    @Test
    void GoodEdgesWithOneEdge(){
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
        var actual = route.edges();
        assertEquals(edges, actual);
    }

    @Test
    void GoodEdgesWithTwoEdge(){
        int fromNodeId1 = 0;
        int toNodeId1 = 10;
        int toNodeId2 =15;
        PointCh fromPoint1 = new PointCh(2485000, 1075000);
        PointCh toPointCh1 = new PointCh(2485100, 1075100);
        PointCh toPoinchCh2 = new PointCh(2485150, 1075150);
        double length1 = 100;
        double length2 = 50;
        float[] samples = {300, 310, 305, 320, 300, 290, 305, 300, 310, 300};
        float[] samples2 = {300, 310, 320, 340, 350, 365, 385, 400, 410, 405};
        DoubleUnaryOperator a = Functions.sampled(samples, 100);
        DoubleUnaryOperator b = Functions.sampled(samples2, 100);
        Edge edge1 = new Edge(fromNodeId1, toNodeId1, fromPoint1, toPointCh1, length1, a);
        Edge edge2 = new Edge(toNodeId1,toNodeId2,toPointCh1,toPoinchCh2,length2,b);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        SingleRoute route = new SingleRoute(edges);
        var actual = route.edges();
        assertEquals(edges,actual);
    }

    @Test
    void GoodLengthWithOneEdge(){
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
        var actual = route.length();
        assertEquals(100,actual);
    }

    @Test
    void GoodLengthWithTwoEdge(){
        int fromNodeId1 = 0;
        int toNodeId1 = 10;
        int toNodeId2 =15;
        PointCh fromPoint1 = new PointCh(2485000, 1075000);
        PointCh toPointCh1 = new PointCh(2485100, 1075100);
        PointCh toPoinchCh2 = new PointCh(2485150, 1075150);
        double length1 = 100;
        double length2 = 50;
        float[] samples = {300, 310, 305, 320, 300, 290, 305, 300, 310, 300};
        float[] samples2 = {300, 310, 320, 340, 350, 365, 385, 400, 410, 405};
        DoubleUnaryOperator a = Functions.sampled(samples, 100);
        DoubleUnaryOperator b = Functions.sampled(samples2, 100);
        Edge edge1 = new Edge(fromNodeId1, toNodeId1, fromPoint1, toPointCh1, length1, a);
        Edge edge2 = new Edge(toNodeId1,toNodeId2,toPointCh1,toPoinchCh2,length2,b);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        SingleRoute route = new SingleRoute(edges);
        var actual = route.length();
        assertEquals(150,actual);
    }

    @Test
    void NegativeLength(){
        int fromNodeId = 0;
        int toNodeId = 10;
        PointCh fromPoint = new PointCh(2485000, 1075000);
        PointCh toPointCh = new PointCh(2485100, 1075100);
        double length = -10;
        float[] samples = {300, 310, 305, 320, 300, 290, 305, 300, 310, 300};
        DoubleUnaryOperator a = Functions.sampled(samples, 100);
        Edge edge = new Edge(fromNodeId, toNodeId, fromPoint, toPointCh, length, a);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge);
        SingleRoute route = new SingleRoute(edges);
        var actual = route.length();
        assertEquals(-10,actual);
    }

    @Test
    void NullLength(){
        int fromNodeId = 0;
        int toNodeId = 10;
        PointCh fromPoint = new PointCh(2485000, 1075000);
        PointCh toPointCh = new PointCh(2485100, 1075100);
        double length = 0;
        float[] samples = {300, 310, 305, 320, 300, 290, 305, 300, 310, 300};
        DoubleUnaryOperator a = Functions.sampled(samples, 100);
        Edge edge = new Edge(fromNodeId, toNodeId, fromPoint, toPointCh, length, a);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge);
        SingleRoute route = new SingleRoute(edges);
        var actual = route.length();
        assertEquals(0,actual);
    }


    @Test
    void GoodPointWithOneEdge(){
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
        List<PointCh> points = new ArrayList<>();
        points.add(fromPoint);
        points.add(toPointCh);
        var actual = route.points();
        assertEquals(points,actual);
    }

    @Test
    void GoodPointWithTwoEdge(){
        int fromNodeId1 = 0;
        int toNodeId1 = 10;
        int toNodeId2 =15;
        PointCh fromPoint1 = new PointCh(2485000, 1075000);
        PointCh toPointCh1 = new PointCh(2485100, 1075100);
        PointCh toPoinchCh2 = new PointCh(2485150, 1075150);
        double length1 = 100;
        double length2 = 50;
        float[] samples = {300, 310, 305, 320, 300, 290, 305, 300, 310, 300};
        float[] samples2 = {300, 310, 320, 340, 350, 365, 385, 400, 410, 405};
        DoubleUnaryOperator a = Functions.sampled(samples, 100);
        DoubleUnaryOperator b = Functions.sampled(samples2, 100);
        Edge edge1 = new Edge(fromNodeId1, toNodeId1, fromPoint1, toPointCh1, length1, a);
        Edge edge2 = new Edge(toNodeId1,toNodeId2,toPointCh1,toPoinchCh2,length2,b);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        SingleRoute route = new SingleRoute(edges);
        List<PointCh> points = new ArrayList<>();
        points.add(fromPoint1);
        points.add(toPointCh1);
        points.add(toPoinchCh2);
        var actual = route.points();
        assertEquals(points,actual);
    }

    @Test
    void PointAtBeginningPoint(){
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
        var actual = route.pointAt(-10);
        assertEquals(fromPoint,actual);
    }

    @Test
    void PointAtMidddlePoint(){
        int fromNodeId1 = 0;
        int toNodeId1 = 10;
        int toNodeId2 =15;
        PointCh fromPoint1 = new PointCh(2485000, 1075000);
        PointCh toPointCh1 = new PointCh(2485100, 1075100);
        PointCh toPoinchCh2 = new PointCh(2485150, 1075150);
        double length1 = 100;
        double length2 = 50;
        float[] samples = {300, 310, 305, 320, 300, 290, 305, 300, 310, 300};
        float[] samples2 = {300, 310, 320, 340, 350, 365, 385, 400, 410, 405};
        DoubleUnaryOperator a = Functions.sampled(samples, 100);
        DoubleUnaryOperator b = Functions.sampled(samples2, 100);
        Edge edge1 = new Edge(fromNodeId1, toNodeId1, fromPoint1, toPointCh1, length1, a);
        Edge edge2 = new Edge(toNodeId1,toNodeId2,toPointCh1,toPoinchCh2,length2,b);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        SingleRoute route = new SingleRoute(edges);
        var actual = route.pointAt(100);
        assertEquals(toPointCh1,actual);
    }


    @Test
    void PointAtEndingPoint(){
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
        var actual = route.pointAt(100);
        assertEquals(toPointCh,actual);
    }

    @Test
    void ElevationBeginingElevation(){
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
        var actual = route.elevationAt(-10);
        assertEquals(300,actual);
    }

    @Test
    void ElevationEndingElevation(){
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
        var actual = route.elevationAt(99.99);
        assertEquals(300,actual, 0.01);
    }

    @Test
    void ElevationEndingElevationWithOneEdge(){
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
        var actual = route.elevationAt(50);
        assertEquals(295,actual);
    }

    @Test
    void ElevationEndingElevationWithOneEdge2(){
        int fromNodeId = 0;
        int toNodeId = 10;
        PointCh fromPoint = new PointCh(2485000, 1075000);
        PointCh toPointCh = new PointCh(2485100, 1075100);
        double length = 100;
        float[] samples = {300, 310, 305, 320, 300, 290, 305, 300, 310, 300,290};
        DoubleUnaryOperator a = Functions.sampled(samples, 100);
        Edge edge = new Edge(fromNodeId, toNodeId, fromPoint, toPointCh, length, a);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge);
        SingleRoute route = new SingleRoute(edges);
        var actual = route.elevationAt(50);
        assertEquals(290,actual);
    }

    @Test
    void ElevationEndingElevationWithTwoEdge(){
        int fromNodeId1 = 0;
        int toNodeId1 = 10;
        int toNodeId2 =15;
        PointCh fromPoint1 = new PointCh(2485000, 1075000);
        PointCh toPointCh1 = new PointCh(2485100, 1075100);
        PointCh toPoinchCh2 = new PointCh(2485150, 1075150);
        double length1 = 100;
        double length2 = 50;
        float[] samples = {300, 310, 305, 320, 300, 290, 305, 300, 310, 300};
        float[] samples2 = {300, 310, 320, 340, 350, 365, 385, 400, 410, 405};
        DoubleUnaryOperator a = Functions.sampled(samples, 100);
        DoubleUnaryOperator b = Functions.sampled(samples2, 100);
        Edge edge1 = new Edge(fromNodeId1, toNodeId1, fromPoint1, toPointCh1, length1, a);
        Edge edge2 = new Edge(toNodeId1,toNodeId2,toPointCh1,toPoinchCh2,length2,b);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        SingleRoute route = new SingleRoute(edges);
        var actual = route.elevationAt(100);
        assertEquals(300,actual);
    }

    @Test
    void GoodBeginningNoeudWithTwoEdge(){
        int fromNodeId1 = 0;
        int toNodeId1 = 10;
        int toNodeId2 =15;
        PointCh fromPoint1 = new PointCh(2485000, 1075000);
        PointCh toPointCh1 = new PointCh(2485100, 1075100);
        PointCh toPoinchCh2 = new PointCh(2485150, 1075150);
        double length1 = 100;
        double length2 = 50;
        float[] samples = {300, 310, 305, 320, 300, 290, 305, 300, 310, 300};
        float[] samples2 = {300, 310, 320, 340, 350, 365, 385, 400, 410, 405};
        DoubleUnaryOperator a = Functions.sampled(samples, 100);
        DoubleUnaryOperator b = Functions.sampled(samples2, 100);
        Edge edge1 = new Edge(fromNodeId1, toNodeId1, fromPoint1, toPointCh1, length1, a);
        Edge edge2 = new Edge(toNodeId1,toNodeId2,toPointCh1,toPoinchCh2,length2,b);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        SingleRoute route = new SingleRoute(edges);
        var actual = route.nodeClosestTo(0);
        assertEquals(0,actual);
    }

    @Test
    void GoodEndingNoeudWithTwoEdge(){
        int fromNodeId1 = 0;
        int toNodeId1 = 10;
        int toNodeId2 =15;
        PointCh fromPoint1 = new PointCh(2485000, 1075000);
        PointCh toPointCh1 = new PointCh(2485100, 1075100);
        PointCh toPoinchCh2 = new PointCh(2485150, 1075150);
        double length1 = 100;
        double length2 = 50;
        float[] samples = {300, 310, 305, 320, 300, 290, 305, 300, 310, 300};
        float[] samples2 = {300, 310, 320, 340, 350, 365, 385, 400, 410, 405};
        DoubleUnaryOperator a = Functions.sampled(samples, 100);
        DoubleUnaryOperator b = Functions.sampled(samples2, 100);
        Edge edge1 = new Edge(fromNodeId1, toNodeId1, fromPoint1, toPointCh1, length1, a);
        Edge edge2 = new Edge(toNodeId1,toNodeId2,toPointCh1,toPoinchCh2,length2,b);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        SingleRoute route = new SingleRoute(edges);
        var actual = route.nodeClosestTo(150);
        assertEquals(15,actual);
    }

    @Test
    void GoodMiddleNoeudWithTwoEdge(){
        int fromNodeId1 = 0;
        int toNodeId1 = 10;
        int toNodeId2 =15;
        PointCh fromPoint1 = new PointCh(2485000, 1075000);
        PointCh toPointCh1 = new PointCh(2485100, 1075100);
        PointCh toPoinchCh2 = new PointCh(2485150, 1075150);
        double length1 = 100;
        double length2 = 50;
        float[] samples = {300, 310, 305, 320, 300, 290, 305, 300, 310, 300};
        float[] samples2 = {300, 310, 320, 340, 350, 365, 385, 400, 410, 405};
        DoubleUnaryOperator a = Functions.sampled(samples, 100);
        DoubleUnaryOperator b = Functions.sampled(samples2, 100);
        Edge edge1 = new Edge(fromNodeId1, toNodeId1, fromPoint1, toPointCh1, length1, a);
        Edge edge2 = new Edge(toNodeId1,toNodeId2,toPointCh1,toPoinchCh2,length2,b);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        SingleRoute route = new SingleRoute(edges);
        var actual = route.nodeClosestTo(100);
        assertEquals(10,actual);
    }

    @Test
    void GoodPointClosestBeginning(){
        int fromNodeId1 = 0;
        int toNodeId1 = 10;
        int toNodeId2 =15;
        PointCh fromPoint1 = new PointCh(2485000, 1075000);
        PointCh toPointCh1 = new PointCh(2485100, 1075100);
        PointCh toPoinchCh2 = new PointCh(2485150, 1075150);
        double length1 = 100;
        double length2 = 50;
        float[] samples = {300, 310, 305, 320, 300, 290, 305, 300, 310, 300};
        float[] samples2 = {300, 310, 320, 340, 350, 365, 385, 400, 410, 405};
        DoubleUnaryOperator a = Functions.sampled(samples, 100);
        DoubleUnaryOperator b = Functions.sampled(samples2, 100);
        Edge edge1 = new Edge(fromNodeId1, toNodeId1, fromPoint1, toPointCh1, length1, a);
        Edge edge2 = new Edge(toNodeId1,toNodeId2,toPointCh1,toPoinchCh2,length2,b);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        SingleRoute route = new SingleRoute(edges);
        RoutePoint expected = new RoutePoint(fromPoint1,0,0);
        var actual = route.pointClosestTo(new PointCh(2485000, 1075000));
        assertEquals(expected,actual);
    }


    @Test
    void GoodPointClosestMiddle(){
        int fromNodeId1 = 0;
        int toNodeId1 = 10;
        int toNodeId2 =15;
        PointCh fromPoint1 = new PointCh(2485000, 1075000);
        PointCh toPointCh1 = new PointCh(2485100, 1075100);
        PointCh toPoinchCh2 = new PointCh(2485150, 1075150);
        double length1 = 141.4;
        double length2 = 50;
        float[] samples = {300, 310, 305, 320, 300, 290, 305, 300, 310, 300};
        float[] samples2 = {300, 310, 320, 340, 350, 365, 385, 400, 410, 405};
        DoubleUnaryOperator a = Functions.sampled(samples, 141.4);
        DoubleUnaryOperator b = Functions.sampled(samples2, 141.4);
        Edge edge1 = new Edge(fromNodeId1, toNodeId1, fromPoint1, toPointCh1, length1, a);
        Edge edge2 = new Edge(toNodeId1,toNodeId2,toPointCh1,toPoinchCh2,length2,b);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        SingleRoute route = new SingleRoute(edges);
        RoutePoint expected = new RoutePoint(toPointCh1,141.4,0);
        var actual = route.pointClosestTo(new PointCh(2485100, 1075100));
        assertEquals(expected,actual);
    }


    @Test
    void GoodPointClosestEnding(){
        int fromNodeId1 = 0;
        int toNodeId1 = 10;
        int toNodeId2 =15;
        PointCh fromPoint1 = new PointCh(2485000, 1075000);
        PointCh toPointCh1 = new PointCh(2485100, 1075100);
        PointCh toPoinchCh2 = new PointCh(2485150, 1075150);
        double length1 = 100;
        double length2 = 50;
        float[] samples = {300, 310, 305, 320, 300, 290, 305, 300, 310, 300};
        float[] samples2 = {300, 310, 320, 340, 350, 365, 385, 400, 410, 405};
        DoubleUnaryOperator a = Functions.sampled(samples, 100);
        DoubleUnaryOperator b = Functions.sampled(samples2, 100);
        Edge edge1 = new Edge(fromNodeId1, toNodeId1, fromPoint1, toPointCh1, length1, a);
        Edge edge2 = new Edge(toNodeId1,toNodeId2,toPointCh1,toPoinchCh2,length2,b);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        SingleRoute route = new SingleRoute(edges);
        RoutePoint expected = new RoutePoint(toPoinchCh2,150,0);
        var actual = route.pointClosestTo(new PointCh(2485150, 1075150));
        assertEquals(expected,actual);
    }

    @Test
    void GoodPointClosestEndingClose(){
        int fromNodeId1 = 0;
        int toNodeId1 = 10;
        int toNodeId2 =15;
        PointCh fromPoint1 = new PointCh(2485000, 1075000);
        PointCh toPointCh1 = new PointCh(2485100, 1075100);
        PointCh toPoinchCh2 = new PointCh(2485150, 1075150);
        double length1 = 100;
        double length2 = 50;
        float[] samples = {300, 310, 305, 320, 300, 290, 305, 300, 310, 300};
        float[] samples2 = {300, 310, 320, 340, 350, 365, 385, 400, 410, 405};
        DoubleUnaryOperator a = Functions.sampled(samples, 100);
        DoubleUnaryOperator b = Functions.sampled(samples2, 100);
        Edge edge1 = new Edge(fromNodeId1, toNodeId1, fromPoint1, toPointCh1, length1, a);
        Edge edge2 = new Edge(toNodeId1,toNodeId2,toPointCh1,toPoinchCh2,length2,b);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        SingleRoute route = new SingleRoute(edges);

        RoutePoint expected = new RoutePoint(toPoinchCh2,150,1);
        var actual = route.pointClosestTo(new PointCh(2485149, 1075150));
        assertEquals(expected,actual);
    }


    @Test
    void length() {
        Edge edge1 = new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MAX_E, SwissBounds.MIN_N)
                , 100, Functions.constant(1.0));
        Edge edge2 = new Edge(0, 1, new PointCh(SwissBounds.MIN_E + 1, SwissBounds.MIN_N), new PointCh(SwissBounds.MAX_E - 1, SwissBounds.MIN_N)
                , 150, Functions.constant(1.0));
        Edge edge3 = new Edge(0, 1, new PointCh(SwissBounds.MIN_E + 2, SwissBounds.MIN_N), new PointCh(SwissBounds.MAX_E - 2, SwissBounds.MIN_N)
                , 1000, Functions.constant(1.0));

        List<Edge> list = new ArrayList<Edge>();
        list.add(edge1);
        list.add(edge2);
        list.add(edge3);
        SingleRoute singleRoute = new SingleRoute(list);
        assertEquals(1250, singleRoute.length());
    }


    @Test
    void points() {
        Edge edge1 = new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 1, SwissBounds.MIN_N)
                , 100, Functions.constant(1.0));
        Edge edge2 = new Edge(0, 1, new PointCh(SwissBounds.MIN_E + 1, SwissBounds.MIN_N), new PointCh(SwissBounds.MAX_E - 1, SwissBounds.MIN_N)
                , 150, Functions.constant(1.0));
        Edge edge3 = new Edge(0, 1, new PointCh(SwissBounds.MAX_E - 1, SwissBounds.MIN_N), new PointCh(SwissBounds.MAX_E - 2, SwissBounds.MIN_N)
                , 1000, Functions.constant(1.0));

        List<Edge> list = new ArrayList<>();
        list.add(edge1);
        list.add(edge2);
        list.add(edge3);
        SingleRoute singleRoute = new SingleRoute(list);

        List<PointCh> pointList = new ArrayList<>();
        pointList.add(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N));
        pointList.add(new PointCh(SwissBounds.MIN_E + 1, SwissBounds.MIN_N));
        pointList.add(new PointCh(SwissBounds.MAX_E - 1, SwissBounds.MIN_N));
        pointList.add(new PointCh(SwissBounds.MAX_E - 2, SwissBounds.MIN_N));


        for (int i = 0; i < pointList.size(); i++)
            assertEquals(pointList.get(i), singleRoute.points().get(i));

    }

    @Test
    void pointAtWorksOnHorizontalLine() {
        Edge edge1 = new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N)
                , 10, Functions.constant(1.0));
        Edge edge2 = new Edge(0, 1, new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N)
                , 10, Functions.constant(1.0));
        Edge edge3 = new Edge(0, 1, new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 30, SwissBounds.MIN_N)
                , 10, Functions.constant(1.0));

        List<Edge> list = new ArrayList<>();
        list.add(edge1);
        list.add(edge2);
        list.add(edge3);
        SingleRoute singleRoute = new SingleRoute(list);


        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), singleRoute.pointAt(-100));
        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), singleRoute.pointAt(0));
        assertEquals(new PointCh(SwissBounds.MIN_E + 30, SwissBounds.MIN_N), singleRoute.pointAt(30));


        assertEquals(new PointCh(SwissBounds.MIN_E + 15, SwissBounds.MIN_N), singleRoute.pointAt(15));
        assertEquals(new PointCh(SwissBounds.MIN_E + 30, SwissBounds.MIN_N), singleRoute.pointAt(40));
        assertEquals(new PointCh(SwissBounds.MIN_E + 25, SwissBounds.MIN_N), singleRoute.pointAt(25));

    }

    @Test
    void pointAtWorksOnVerticalLine() {
        Edge edge1 = new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 100)
                , 100, Functions.constant(1.0));
        Edge edge2 = new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 100), new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 150)
                , 50, Functions.constant(1.0));
        Edge edge3 = new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 150), new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 1000)
                , 850, Functions.constant(1.0));

        List<Edge> list = new ArrayList<>();
        list.add(edge1);
        list.add(edge2);
        list.add(edge3);
        SingleRoute singleRoute = new SingleRoute(list);

        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), singleRoute.pointAt(-100));
        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), singleRoute.pointAt(0));
        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 1000), singleRoute.pointAt(1250));
        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 150), singleRoute.pointAt(150));


        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 15), singleRoute.pointAt(15));
        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 250), singleRoute.pointAt(250));
        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 500), singleRoute.pointAt(500));

    }


    @Test
    void pointAtWorksOnObliqueLine() {
        Edge edge1 = new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N + 100)
                , 100 * Math.sqrt(2), Functions.constant(1.0));
        Edge edge2 = new Edge(0, 1, new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N + 100), new PointCh(SwissBounds.MIN_E + 500, SwissBounds.MIN_N + 500)
                , 400 * Math.sqrt(2), Functions.constant(1.0));
        Edge edge3 = new Edge(0, 1, new PointCh(SwissBounds.MIN_E + 500, SwissBounds.MIN_N + 500), new PointCh(SwissBounds.MIN_E + 1000, SwissBounds.MIN_N + 1000)
                , 500 * Math.sqrt(2), Functions.constant(1.0));

        List<Edge> list = new ArrayList<>();
        list.add(edge1);
        list.add(edge2);
        list.add(edge3);
        SingleRoute singleRoute = new SingleRoute(list);

        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), singleRoute.pointAt(0));
        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), singleRoute.pointAt(-9));
        assertEquals(new PointCh(SwissBounds.MIN_E + 1000, SwissBounds.MIN_N + 1000), singleRoute.pointAt(10000));
        assertEquals(new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N + 100), singleRoute.pointAt(100 * Math.sqrt(2)));

        assertEquals(new PointCh(SwissBounds.MIN_E + 15 / Math.sqrt(2), SwissBounds.MIN_N + 15 / Math.sqrt(2)), singleRoute.pointAt(15));
        assertEquals(new PointCh(SwissBounds.MIN_E + 250 / Math.sqrt(2), SwissBounds.MIN_N + 250 / Math.sqrt(2)), singleRoute.pointAt(250));
        assertEquals(new PointCh(SwissBounds.MIN_E + 500 / Math.sqrt(2), SwissBounds.MIN_N + 500 / Math.sqrt(2)), singleRoute.pointAt(500));


        assertEquals(new PointCh(SwissBounds.MIN_E + 499.99 / Math.sqrt(2), SwissBounds.MIN_N + 499.99 / Math.sqrt(2)), singleRoute.pointAt(499.99));


    }


    @Test
    void elevationAtWorksOnConstantAltitude() {
        Edge edge1 = new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 100)
                , 100, Functions.constant(1.0));
        Edge edge2 = new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 100), new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 150)
                , 50, Functions.constant(1.0));
        Edge edge3 = new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 150), new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 1000)
                , 850, Functions.constant(1.0));

        List<Edge> list = new ArrayList<>();
        list.add(edge1);
        list.add(edge2);
        list.add(edge3);
        SingleRoute singleRoute = new SingleRoute(list);

        assertEquals(1, singleRoute.elevationAt(0));
        assertEquals(1, singleRoute.elevationAt(-50));
        assertEquals(1, singleRoute.elevationAt(50));
        assertEquals(1, singleRoute.elevationAt(100));
        assertEquals(1, singleRoute.elevationAt(250));
        assertEquals(1, singleRoute.elevationAt(1000));
        assertEquals(1, singleRoute.elevationAt(100000));

    }


    @Test
    void elevationAtWorksOnSampledAltitude() {

        float[] tab1 = {0, 10, 20, 30, 40, 50};
        DoubleUnaryOperator f1 = Functions.sampled(tab1, 100);

        float[] tab2 = new float[]{100, 0, 100, 0, 100, 0};
        DoubleUnaryOperator f2 = Functions.sampled(tab2, 100);

        float[] tab3 = new float[]{1000, 1000, 1000,1000, 1000, 1000};
        DoubleUnaryOperator f3 = Functions.sampled(tab3, 100);



        Edge edge1 = new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 100)
                , 100, f1);

        Edge edge2 = new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 100), new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 150)
                , 100, f2);
        Edge edge3 = new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 150), new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 1000),
                100, f3);

        List<Edge> list = new ArrayList<>();
        list.add(edge1);
        list.add(edge2);
        list.add(edge3);
        SingleRoute singleRoute = new SingleRoute(list);

        assertEquals(0, singleRoute.elevationAt(0));
        assertEquals(0, singleRoute.elevationAt(-50));
        assertEquals(25, singleRoute.elevationAt(50));
        assertEquals(50, singleRoute.elevationAt(99.99999999), 0.0001);
        assertEquals(100, singleRoute.elevationAt(100.0000002), 0.0001);
        assertEquals(1000, singleRoute.elevationAt(260));
        assertEquals(1000, singleRoute.elevationAt(225.35678));
        assertEquals(1000, singleRoute.elevationAt(100000));

    }



    //EYA

    private final double DELTA = 1e-7;
    Path filePath = Path.of("lausanne");
    Graph e = Graph.loadFrom(filePath);



    private SingleRouteTest() throws IOException {
    }

    @Test
    void emptyListThrowsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> {SingleRoute a = new SingleRoute(new ArrayList<Edge>());});
    }

    @Test
    void ListDoesThrowsIllegalArgumentException() throws IOException {
        List<Edge> edges = new ArrayList<Edge>();
        edges.add(Edge.of(e,0,1,2));
        assertDoesNotThrow(() -> {new SingleRoute(edges);});
    }

    @Test
    void testIndexSegmentAt(){
        List<Edge> edges = new ArrayList<Edge>();
        edges.add(Edge.of(e,0,1,2));
        SingleRoute a = new SingleRoute(edges);
        assertEquals(0,a.indexOfSegmentAt(1000000000));
    }

    @Test
    void testLength(){
        List<Edge> edges = new ArrayList<Edge>();
        edges.add(Edge.of(e,232379,109351,109356));
        edges.add(Edge.of(e,232380,109349,109353));
        Edge b = Edge.of(e,232379,109351,109356);
        Edge f = (Edge.of(e,232380,109349,109353));
        System.out.println(b.length());
        System.out.println(f.length());
        SingleRoute a = new SingleRoute(edges);

        List<Edge> edges1 = new ArrayList<Edge>();
        edges1.add(Edge.of(e,0,90000,90000));
        SingleRoute a1 = new SingleRoute(edges1);


        List<Edge> edges2 = new ArrayList<Edge>();
        edges2.add(b);
        SingleRoute a2 = new SingleRoute(edges2);


        assertEquals(3.9375,a.length());
        assertEquals(95.125,a1.length());
        assertEquals(1.9375,a2.length());


    }


    @Test
    void pointsTest() throws IOException{

        List<Edge> edges = new ArrayList<Edge>();
        edges.add(Edge.of(e,232379,109351,109356));
        edges.add(Edge.of(e,232380,109349,109353));

        System.out.println("hey");
        System.out.println(e.edgeTargetNodeId(232379));//109349
        //109351
        System.out.println(e.edgeTargetNodeId(232380));

        PointCh pointfromNodeId = new PointCh(2537498.6875, 1154426.4375);
        PointCh pointToNodeId = new PointCh(2537497.625, 1154426.4375);
        PointCh pointToNodeId2 = new PointCh(2537495.1875,1154425.4375);
        PointCh pointToNodeId3 = new PointCh(2537500.6875, 1154426.375);
        List<PointCh> pointsExpected = new ArrayList<>();
        pointsExpected.add(pointfromNodeId);
        pointsExpected.add(pointToNodeId);
        pointsExpected.add(pointToNodeId2);
        pointsExpected.add(pointToNodeId3);

        PointCh testing = new PointCh(2537497.625,1154439.125);
        PointCh testing2 = new PointCh(2537500.6875, 1154426.375);
        System.out.println("starting");
        System.out.println(Math.toDegrees(pointfromNodeId.lat()));
        System.out.println(Math.toDegrees(pointfromNodeId.lon()));
        System.out.println(Math.toDegrees(testing.lat()));
        System.out.println(Math.toDegrees(testing.lon()));
        System.out.println(Math.toDegrees(testing2.lat()));
        System.out.println(Math.toDegrees(testing2.lon()));


        SingleRoute route1 = new SingleRoute(edges);

        Edge ed1 = Edge.of(e, 287113,134119 ,133636); //chemin de roseneck
        Edge ed2 = Edge.of(e, 285998,133636,133953);//chemin de beau rivage vers le bas
        System.out.println( e.nodeOutDegree(133636));
        System.out.println(e.nodeOutEdgeId(133636,0));
        System.out.println(e.nodeOutEdgeId(133636,1));
        System.out.println(e.nodeOutEdgeId(133636,2));
        System.out.println(e.edgeTargetNodeId(285997));
        System.out.println(e.edgeTargetNodeId(285998));
        System.out.println(e.edgeTargetNodeId(285999));
        List<Edge> edges2 = new ArrayList<Edge>();
        edges2.add(ed1);
        edges2.add(ed2);
        SingleRoute a1 = new SingleRoute(edges2);
        List<PointCh> pointsExpected2 = new ArrayList<>();
        pointsExpected2.add(e.nodePoint(134119));
        pointsExpected2.add(e.nodePoint(133636));
        pointsExpected2.add(e.nodePoint(133953));
        assertEquals(pointsExpected2,a1.points());
        //assertEquals(pointsExpected, route1.points());


    }


    @Test
    void pointAtOnNodeTest(){
        Edge ed1 = Edge.of(e, 287113,134119 ,133636); //chemin de roseneck
        Edge ed2 = Edge.of(e, 285998,133636,133953);
        List<Edge> edges2 = new ArrayList<Edge>();
        edges2.add(ed1);
        edges2.add(ed2);
        SingleRoute a1 = new SingleRoute(edges2);
        System.out.println(ed1.length());
        a1.pointAt(74.875);
        PointCh expected = e.nodePoint(133636);
        PointCh expected2 = e.nodePoint(134119);
        PointCh expected3 = e.nodePoint(133953);
        System.out.println(a1.length());
        assertEquals(expected,a1.pointAt(74.875));
        assertEquals(expected2,a1.pointAt(0));
        assertEquals(expected3,a1.pointAt(110.6875));

    }

    @Test
    void pointAtOutNodeTest(){
        Edge ed1 = Edge.of(e, 287113,134119 ,133636); //chemin de roseneck
        Edge ed2 = Edge.of(e, 285998,133636,133953);
        List<Edge> edges = new ArrayList<Edge>();
        edges.add(ed1);
        edges.add(ed2);
        System.out.println(ed1.length());
        System.out.println(ed2.length());

        SingleRoute a1 = new SingleRoute(edges);
        System.out.println(a1.length());
        System.out.println(a1.pointAt(200));
        PointCh expected = ed1.pointAt(60);
        PointCh expected2 = ed2.pointAt(20);
        PointCh expected3 = ed2.pointAt(35.8125);
        PointCh expected4 = ed1.pointAt(0);
        assertEquals(expected, a1.pointAt(60));
        assertEquals(expected2, a1.pointAt(94.875));
        assertEquals(expected3,ed2.toPoint());
        assertEquals(ed2.toPoint(),a1.pointAt(200));
        assertEquals(expected4,a1.pointAt(-50));


    }

    @Test
    void elevationAtOnNodes(){


        //todo chelou node du milieu même point mais pas même profil
        Edge ed1 = Edge.of(e, 287113,134119 ,133636); //chemin de roseneck
        Edge ed2 = Edge.of(e, 285998,133636,133953);
        List<Edge> edges2 = new ArrayList<Edge>();
        edges2.add(ed1);
        edges2.add(ed2);
        SingleRoute a1 = new SingleRoute(edges2);
        System.out.println(ed1.length());
        a1.pointAt(74.875);
        double expected = ed1.elevationAt(0);
        double expected2 = ed2.elevationAt(0);
        System.out.println(ed1.pointAt(74.875));
        System.out.println(ed2.pointAt(0));
        System.out.println(ed2.elevationAt(0));
        System.out.println(ed1.elevationAt(ed1.length()));
        double expected3 = ed2.elevationAt(35.8125);
        assertEquals(expected,a1.elevationAt(0));
        assertEquals(expected2,a1.elevationAt(74.875));
        assertEquals(expected3,a1.elevationAt(110.6875));
    }

    @Test
    void elevationAtOutNodes(){


        //todo node du milieu même point mais pas même profil
        Edge ed1 = Edge.of(e, 287113,134119 ,133636); //chemin de roseneck
        Edge ed2 = Edge.of(e, 285998,133636,133953);
        List<Edge> edges2 = new ArrayList<Edge>();
        edges2.add(ed1);
        edges2.add(ed2);
        SingleRoute a1 = new SingleRoute(edges2);
        System.out.println(ed1.length());
        System.out.println(ed2.length());
        a1.pointAt(74.875);
        System.out.println("At length " +ed1.elevationAt(74.875));
        System.out.println(ed2.pointAt(0));
        System.out.println("At length " + ed1.pointAt(74.875));
        System.out.println("At 0 " + ed2.elevationAt(0));
        double expected = ed1.elevationAt(60);
        double expected2 = ed2.elevationAt(20);
        double expected3 = ed2.elevationAt(35.8125);
        double expected4 = ed1.elevationAt(0);

        double expected5 = ed1.elevationAt(ed1.length());
        double expected6 = ed2.elevationAt(0);

        assertEquals(expected,a1.elevationAt(60));
        assertEquals(expected2,a1.elevationAt(94.875));
        assertEquals(expected3,a1.elevationAt(200));
        assertEquals(expected4,a1.elevationAt(-50));

        //assertEquals(expected5,a1.elevationAt(74.875));
        assertEquals(expected6,a1.elevationAt(74.875));
    }

    @Test
    void nodeClosestToTest(){

        Edge ed1 = Edge.of(e, 287113,134119 ,133636); //chemin de roseneck
        Edge ed2 = Edge.of(e, 285998,133636,133953);
        List<Edge> edges2 = new ArrayList<Edge>();
        edges2.add(ed1);
        edges2.add(ed2);
        SingleRoute a1 = new SingleRoute(edges2);
        double expected1 = 134119;
        assertEquals(expected1,a1.nodeClosestTo(0.8));
        assertEquals(133636, a1.nodeClosestTo(70));
        assertEquals(133636, a1.nodeClosestTo(76));
        assertEquals(134119, a1.nodeClosestTo(0));
        assertEquals(133636, a1.nodeClosestTo(74.875));
        assertEquals(133953, a1.nodeClosestTo(110.6875));
        assertEquals(133953, a1.nodeClosestTo(2000));
        assertEquals(133953, a1.nodeClosestTo(108));
        assertEquals(134119, a1.nodeClosestTo(-90));
        //assertEquals(133636, a1.nodeClosestTo(37.4375));
    }

    @Test
    void pointClosestToTest(){
        Edge ed1 = Edge.of(e, 287113,134119 ,133636); //chemin de roseneck
        Edge ed2 = Edge.of(e, 285998,133636,133953);
        List<Edge> edges2 = new ArrayList<Edge>();
        edges2.add(ed1);
        edges2.add(ed2);
        System.out.println(ed1.length());
        System.out.println(ed2.length());
        SingleRoute a1 = new SingleRoute(edges2);
        PointCh firstNode = new PointCh(2537789.875, 1151256.3125);
        PointCh secondNode = new PointCh(2537936.0625,1151212.9375);
        PointCh thirdNode = new PointCh(2537928.625, 1151178.0625);
        System.out.println(ed2.positionClosestTo(thirdNode));
        assertEquals(ed2.toPoint(),thirdNode);

        //assertEquals(35.8125, ed2.positionClosestTo(ed2.toPoint()));



        //assertEquals(new RoutePoint(firstNode,0,0), a1.pointClosestTo(firstNode));
        //assertEquals(new RoutePoint(secondNode,74.875,0), a1.pointClosestTo(secondNode));
        //assertEquals(new RoutePoint(ed2.toPoint(),a1.length(),0), a1.pointClosestTo(ed2.toPoint()));
        //46.509838, 6.629164
        double coordinateE2 = Ch1903.e(Math.toRadians(6.629164), Math.toRadians(46.509838));
        double coordinateN2 = Ch1903.n(Math.toRadians(6.629164), Math.toRadians(46.509838));
        PointCh point2 = new PointCh(coordinateE2, coordinateN2);
        RoutePoint routepoint2 = a1.pointClosestTo(point2);
        System.out.println(routepoint2);
        //PointCh[e=2537915.807002296, n=1151218.9474681087]



        //46.509725, 6.627964
        double coordinateE = Ch1903.e(Math.toRadians(6.627964), Math.toRadians(46.509725));
        double coordinateN = Ch1903.n(Math.toRadians(6.627964), Math.toRadians(46.509725));
        PointCh point = new PointCh(coordinateE, coordinateN);

        //46.509690, 6.628953

        double coordinateE1 = Ch1903.e(Math.toRadians(6.628953), Math.toRadians(46.509690));
        double coordinateN1 = Ch1903.n(Math.toRadians(6.628953), Math.toRadians(46.509690));
        PointCh point1 = new PointCh(coordinateE1, coordinateN1);
        System.out.println(point1);
        PointCh a = new PointCh(2537915.807002296, 1151218.9474681087);
        System.out.println(Math.toDegrees(a.lon()));
        System.out.println(Math.toDegrees(a.lat()));
        RoutePoint routepoint = a1.pointClosestTo(point1);
        System.out.println(routepoint);





    }


    @Test
    void findEdges() throws IOException{
        Path filePath = Path.of("lausanne/nodes_osmid.bin");
        LongBuffer osmIdBuffer;
        try (FileChannel channel = FileChannel.open(filePath)) {
            osmIdBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
        }
        System.out.println(osmIdBuffer.get(210000));
        System.out.println(osmIdBuffer.get(209775));
        System.out.println(osmIdBuffer.get(150000));
        System.out.println(osmIdBuffer.get(180000));
        System.out.println(osmIdBuffer.get(190000));
        System.out.println(osmIdBuffer.get(191227));
        System.out.println(osmIdBuffer.get(109350));//3389153419
        System.out.println(osmIdBuffer.get(134119));
        System.out.println(osmIdBuffer.get(134118));
        System.out.println(osmIdBuffer.get(134120));
        System.out.println(osmIdBuffer.get(134121));
        System.out.println(osmIdBuffer.get(209768));
        System.out.println(osmIdBuffer.get(209949));
        System.out.println(osmIdBuffer.get(209773));
        System.out.println(osmIdBuffer.get(194890));
        System.out.println(osmIdBuffer.get(109356));

        System.out.println(e.nodeOutDegree(109350));
        System.out.println(e.nodeOutEdgeId(109350,0));//232379
        System.out.println(e.nodeOutEdgeId(109350,1));//232380
        System.out.println(e.edgeAttributes(232379));
        System.out.println(e.edgeTargetNodeId(232379));
        System.out.println(e.edgeTargetNodeId(232380));
        System.out.println(osmIdBuffer.get(109351));
        System.out.println(osmIdBuffer.get(109349));
        System.out.println(e.edgeAttributes((232380)));
        //593969873
        System.out.println();

        int index = 0;
        for (int i = 0; i < osmIdBuffer.capacity(); i++) {
            if (osmIdBuffer.get(i) ==   564464307L) {
                index = i;
            }


        }
        System.out.println("index" + index);


    }


    @Test
    void findEdges2() throws IOException {
        Path filePath = Path.of("lausanne/nodes_osmid.bin");
        LongBuffer osmIdBuffer;
        try (FileChannel channel = FileChannel.open(filePath)) {
            osmIdBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
        }

        System.out.println(osmIdBuffer.get(109351));
        System.out.println(osmIdBuffer.get(109356));
        System.out.println(osmIdBuffer.get(109349));
        System.out.println(osmIdBuffer.get(109353));
        System.out.println(osmIdBuffer.get(134119));//premier noeuds chemin de roseneck
        System.out.println(osmIdBuffer.get(133636));//deuxième noeud
        System.out.println(osmIdBuffer.get(133631));
        System.out.println(osmIdBuffer.get(133953));//chemin de beau rivage vers le bas
        System.out.println(osmIdBuffer.get(133637));

    }



    //ARTHUR

    @Test
    void testInitializeSingleRoute(){
        List<Edge> l = new ArrayList<Edge>();

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        float[] profileTab = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        l.add(edge1);

        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        float[] profileTab2 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile2 = Functions.sampled(profileTab, 5);
        Edge edge2 = new Edge(1, 2, fromPoint2, toPoint2, 5, profile2);

        l.add(edge2);

        SingleRoute s = new SingleRoute(l);

        assertThrows(IllegalArgumentException.class, () -> {
            new SingleRoute(new ArrayList<Edge>());
        });
    }

    @Test
    void testLengthAndEdges(){
        List<Edge> l = new ArrayList<Edge>();
        List<PointCh> pointsList = new ArrayList<PointCh>();

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        float[] profileTab = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        //on ajoute pas le toPoint car l'edge2 fromPoint est ce point
        pointsList.add(fromPoint);
        l.add(edge1);

        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        float[] profileTab2 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile2 = Functions.sampled(profileTab, 5);
        Edge edge2 = new Edge(1, 2, fromPoint2, toPoint2, 5, profile2);

        l.add(edge2);

        pointsList.add(fromPoint2);
        pointsList.add(toPoint2);

        SingleRoute s = new SingleRoute(l);
        assertEquals(10, s.length());
        assertEquals(l, s.edges());
        assertEquals(pointsList, s.points());


        PointCh fromPoint3 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        PointCh toPoint3 = new PointCh(SwissBounds.MIN_E + 15, SwissBounds.MIN_N);
        float[] profileTab3 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile3 = Functions.sampled(profileTab, 5);
        Edge edge3 = new Edge(2, 1, fromPoint3, toPoint3, 5, profile3);

        l.add(edge3);
        //le fromPoint a deja été ajouté du coup (endPoint de l'edge 2)
        //pointsList.add(fromPoint3);
        pointsList.add(toPoint3);

        //length of s shouldn't have changed even if l was modified
        assertEquals(10, s.length());

        SingleRoute s2 = new SingleRoute(l);
        assertEquals(15, s2.length());
        assertEquals(l, s2.edges());
        assertEquals(pointsList, s2.points());


        //Test pointAt()

        for(int i = 0; i < 15; i++){
            assertEquals(new PointCh(SwissBounds.MIN_E + i, SwissBounds.MIN_N), s2.pointAt(i));
        }

        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), s2.pointAt(-2));
        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), s2.pointAt(-1));
        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), s2.pointAt(0));
        assertEquals(new PointCh(SwissBounds.MIN_E + 15, SwissBounds.MIN_N), s2.pointAt(15));
        assertEquals(new PointCh(SwissBounds.MIN_E + 15, SwissBounds.MIN_N), s2.pointAt(16));
        assertEquals(new PointCh(SwissBounds.MIN_E + 15, SwissBounds.MIN_N), s2.pointAt(20));
        assertEquals(new PointCh(SwissBounds.MIN_E + 15, SwissBounds.MIN_N), s2.pointAt(30));
    }


    @Test
    void testElevationAt(){
        List<Edge> l = new ArrayList<Edge>();

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        float[] profileTab = {500f, 505f, 510f, 515f, 520f, 525f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        l.add(edge1);

        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        float[] profileTab2 = {525f, 530f, 535f, 540f, 545f, 550f};
        DoubleUnaryOperator profile2 = Functions.sampled(profileTab2, 5);
        Edge edge2 = new Edge(1, 2, fromPoint2, toPoint2, 5, profile2);

        l.add(edge2);

        SingleRoute s = new SingleRoute(l);

        for(int i = 0; i < 10; i++){
            assertEquals(500 + 5*i, s.elevationAt(i));
        }

        assertEquals(500, s.elevationAt(0));
        assertEquals(500, s.elevationAt(-2));
        assertEquals(500, s.elevationAt(-10));
        assertEquals(550, s.elevationAt(10));
        assertEquals(550, s.elevationAt(11));


        assertEquals(550, s.elevationAt(14));
        assertEquals(550, s.elevationAt(100000));

        PointCh fromPoint3 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        PointCh toPoint3 = new PointCh(SwissBounds.MIN_E + 15, SwissBounds.MIN_N);
        float[] profileTab3 = new float[]{};
        DoubleUnaryOperator profile3 =Functions.constant(Double.NaN);
        Edge edge3 = new Edge(2, 1, fromPoint3, toPoint3, 5, profile3);

        l.add(edge3);

        SingleRoute s2 = new SingleRoute(l);

        //edge3 length is 5 (10 - 15)
        for(double i = 10; i < 18; i += 0.1){
            System.out.println(s2.elevationAt(i));
            assertEquals(Double.NaN, s2.elevationAt(i));
        }
    }

    @Test
    void testNodeClosestTo(){
        List<Edge> l = new ArrayList<Edge>();

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        float[] profileTab = {500f, 505f, 510f, 515f, 520f, 525f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        l.add(edge1);

        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        float[] profileTab2 = {525f, 530f, 535f, 540f, 545f, 550f};
        DoubleUnaryOperator profile2 = Functions.sampled(profileTab2, 5);
        Edge edge2 = new Edge(1, 2, fromPoint2, toPoint2, 5, profile2);

        l.add(edge2);

        SingleRoute s = new SingleRoute(l);

        assertEquals(2, s.nodeClosestTo(10));
        assertEquals(2, s.nodeClosestTo(10.1));
        assertEquals(2, s.nodeClosestTo(12));
        assertEquals(2, s.nodeClosestTo(20));
        assertEquals(2, s.nodeClosestTo(100));

        for(double i = 0; i < 10.0; i += 0.1){
            //System.out.println("i=" + i + " closest=" + s.nodeClosestTo(i));
            if(i <= 2.5){
                assertEquals(0, s.nodeClosestTo(i));
            }else if(i <= 7.5){
                assertEquals(1, s.nodeClosestTo(i));
            }else{
                assertEquals(2, s.nodeClosestTo(i));
            }
        }

        //according to the assistant, when we are at half way we should chose left node
        assertEquals(0, s.nodeClosestTo(2.5));
        assertEquals(1, s.nodeClosestTo(7.5));

        assertEquals(0, s.nodeClosestTo(-1));
        assertEquals(0, s.nodeClosestTo(0));
        assertEquals(0, s.nodeClosestTo(-6));
        assertEquals(0, s.nodeClosestTo(-1000));
    }

    @Test
    void testNearestPoint(){
        List<Edge> l = new ArrayList<Edge>();

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        float[] profileTab = {500f, 505f, 510f, 515f, 520f, 525f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        l.add(edge1);

        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        float[] profileTab2 = {525f, 530f, 535f, 540f, 545f, 550f};
        DoubleUnaryOperator profile2 = Functions.sampled(profileTab2, 5);
        Edge edge2 = new Edge(1, 2, fromPoint2, toPoint2, 5, profile2);

        l.add(edge2);

        SingleRoute s = new SingleRoute(l);

        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 1, SwissBounds.MIN_N), 1, 1), s.pointClosestTo(new PointCh(SwissBounds.MIN_E + 1, SwissBounds.MIN_N + 1)));
        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 2, SwissBounds.MIN_N), 2, 10), s.pointClosestTo(new PointCh(SwissBounds.MIN_E + 2, SwissBounds.MIN_N + 10)));

        //Math.sqrt(104) car distanceToReferee au carré = 2*2 + 10*10
        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N), 10, Math.sqrt(104)), s.pointClosestTo(new PointCh(SwissBounds.MIN_E + 12, SwissBounds.MIN_N + 10)));

        //testing with point already on the edge
        for(int i = 0; i <= 50; i++){
            if(i <= 10){
                assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + i, SwissBounds.MIN_N), i, 0), s.pointClosestTo(new PointCh(SwissBounds.MIN_E + i, SwissBounds.MIN_N)));
            }else{
                assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N), 10, i - 10), s.pointClosestTo(new PointCh(SwissBounds.MIN_E + i, SwissBounds.MIN_N)));
            }
        }

        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N), 5, 5), s.pointClosestTo(new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N + 5)));
        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N), 10, 10), s.pointClosestTo(new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N)));
        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 0, 3), s.pointClosestTo(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 3)));
    }

}
