/*package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SingleRouteTest1 {

    double DELTA = 1e-7;

    PointCh p1 = new PointCh(2600123, 1200456);
    PointCh p2 = new PointCh(2600456, 1200789);
    PointCh p3 = new PointCh(2600789, 1200123);
    PointCh p4 = new PointCh(2601000, 1201000);

    Edge edge1 = new Edge(1, 2, p1, p2, 10, d -> 100);
    Edge edge2 = new Edge(3, 4, p3, p4, 15, d -> 100);


    @Test
    public void SingleRouteConstructorIAEWorks(){
        assertThrows(IllegalArgumentException.class, ()-> new SingleRoute(new ArrayList<>()));
    }


    @Test
    public void  lengthWorks(){

        List<Edge> la = new ArrayList<>();
        la.add(edge1);
        la.add(edge2);
        var test1 = new SingleRoute(la);

        var actual2 = test1.length();
        var expected2 = edge1.length()+edge2.length();

        assertEquals(expected2, actual2, DELTA);

    }


    @Test
    public void edgesWorks(){

        List<Edge> la = new ArrayList<>();
        la.add(edge1);
        la.add(edge2);
        var test1 = new SingleRoute(la);

        var actual = test1.edges();

        assertEquals(la, actual);

    }


    @Test
    public void pointsWorks(){

        List<Edge> la = new ArrayList<>();
        la.add(edge1);
        la.add(edge2);
        var test1 = new SingleRoute(la);

        //size
        var actual1 = test1.points().size();
        var expected1 = 3;

        //inside
        var actual = test1.points();
        var expected = new ArrayList<>();
        expected.add(edge1.fromPoint());
        expected.add(edge2.fromPoint());
        expected.add(edge2.toPoint());

        assertEquals(expected, actual);
        assertEquals(expected1, actual1);

    }
    /*
    //mettre binarySearchIndex en public pour la tester puis de nouveau en pv
    @Test
    public void binarySearchIndexWorks(){

        List<Edge> la = new ArrayList<>();
        la.add(edge1);
        la.add(edge2);
        var test1 = new SingleRoute(la);

        //teste valeurs : négative, entre edges, sur les nodes, et après itinéraire

        var actual1 = test1.binarySearchIndex(-4);
        var actual2 = test1.binarySearchIndex(0);
        var actual3 = test1.binarySearchIndex(5);
        var actual4 = test1.binarySearchIndex(10);
        var actual5 = test1.binarySearchIndex(15);
        var actual6 = test1.binarySearchIndex(25);
        var actual7 = test1.binarySearchIndex(35);
        var actual8 = test1.binarySearchIndex(55);

        var expected123 = 0;
        var expected45 = 1;
        //var expected678 = 2;


        System.out.println(actual1);
        System.out.println(actual2);
        System.out.println(actual3);
        System.out.println(actual4);
        System.out.println(actual5);
        System.out.println(actual6);
        System.out.println(actual7);
        System.out.println(actual8);


        assertEquals(expected123, actual1);
        assertEquals(expected123, actual2);
        assertEquals(expected123, actual3);
        assertEquals(expected45, actual4);
        assertEquals(expected45, actual5);
        assertEquals(1, actual6);
        assertEquals(1, actual7);
        assertEquals(1, actual8);

    }
    */

    @Test
    public void pointAtWorks(){

        List<Edge> la = new ArrayList<>();
        la.add(edge1);
        la.add(edge2);
        var test1 = new SingleRoute(la);

        var positionNull = 0;
        var position1 = edge1.length();
        var positionBetween = edge1.length() + 5;
        var positionOut = edge1.length() + edge2.length();
        var positionOutOfBonds = test1.length() + 20;

        var actualNull = test1.pointAt(positionNull);
        var actual1 = test1.pointAt(position1);
        var actualBetween = test1.pointAt(positionBetween);
        var actualOut = test1.pointAt(positionOut);
        var actualOOB = test1.pointAt(positionOutOfBonds);

        var expectedNull = la.get(0).fromPoint();
        var expected1 = la.get(0).toPoint();
        var expectedBetween = la.get(1).fromPoint();
        var expectedOut = la.get(1).toPoint();
        var expectedOOB = la.get(1).toPoint();


        //le 1er marche, voir avec les autres, si c'est une erreur de tests ou alors dans mon code
        //j'ai modif le test qui était faux, nouvelle erreur :
        // IOOBE : surement à cause du a et méthode
        assertEquals(expectedNull, actualNull);
        assertEquals(expected1, actual1);
        assertEquals(expectedBetween, actualBetween);
        assertEquals(expectedOut, actualOut);
        assertEquals(expectedOOB, actualOOB);

    }


    @Test
    public void elevationAtNaN(){

        List<Edge> la = new ArrayList<>();
        la.add(new Edge(1, 2, p1, p2, 10, d -> Double.NaN));
        la.add(edge2);
        var test1 = new SingleRoute(la);

        var position = 0;
        var actual = test1.elevationAt(position);
        var expected = la.get(0).elevationAt(position);

        System.out.println(test1.elevationAt(position));
        assertEquals(expected, actual);
    }


    @Test
    public void elevationAtWorksNormally(){

        List<Edge> la = new ArrayList<>();
        la.add(edge1);
        la.add(edge2);
        var test1 = new SingleRoute(la);

        var positionNull = 0;
        var position1 = edge1.length();
        var positionBetween = edge1.length() + 5;
        var positionOut = edge1.length() + edge2.length();
        var positionOutOfBonds = test1.length() + 20;

        var actualNull = test1.elevationAt(positionNull);
        var actual1 = test1.elevationAt(position1);
        var actualBetween = test1.elevationAt(positionBetween);
        var actualOut = test1.elevationAt(positionOut);
        var actualOOB = test1.elevationAt(positionOutOfBonds);

        var expectedNull = la.get(0).elevationAt(positionNull);
        var expected1 = la.get(1).elevationAt(position1);
        var expectedBetween = la.get(1).elevationAt(positionBetween);
        var expectedOut = la.get(1).elevationAt(positionOut);
        var expectedOOB = la.get(1).elevationAt(positionOutOfBonds);

        System.out.println(actualNull);
        System.out.println(actual1);
        System.out.println(actualBetween);
        System.out.println(actualOut);
        System.out.println(actualOOB);
        //le 1er marche, voir avec les autres, si c'est une erreur de tests ou alors dans mon code
        assertEquals(expectedNull, actualNull);
        assertEquals(expected1, actual1);
        assertEquals(expectedBetween, actualBetween);
        assertEquals(expectedOut, actualOut);
        assertEquals(expectedOOB, actualOOB);

    }


    @Test
    public void nodeClosestToWorks(){

        List<Edge> la = new ArrayList<>();
        la.add(edge1);
        la.add(edge2);
        var test1 = new SingleRoute(la);

        var positionNull = 0;
        var position1 = edge1.length();
        var positionBetween = edge1.length() + 5;
        var positionOut = edge1.length() + edge2.length();
        var positionOutOfBonds = test1.length() + 20;

        var actualNull = test1.nodeClosestTo(positionNull);
        var actual1 = test1.nodeClosestTo(position1);
        var actualBetween = test1.nodeClosestTo(positionBetween);
        var actualOut = test1.nodeClosestTo(positionOut);
        var actualOOB = test1.nodeClosestTo(positionOutOfBonds);

        System.out.println(actualNull);
        System.out.println(actual1);
        System.out.println(actualBetween);
        System.out.println(actualOut);
        System.out.println(actualOOB);

        var expectedNull = 1;
        var expected1 = 2;
        var expectedBetween = 3;
        var expectedOut = 4;
        var expectedOOB = 4;

        assertEquals(expectedNull, actualNull);
        assertEquals(expected1, actual1);
        assertEquals(expectedBetween, actualBetween);
        assertEquals(expectedOut, actualOut);
        assertEquals(expectedOOB, actualOOB);

    }

    //pointClosestToWorks : jsp quel truc mettre pour les actual
    @Test
    public void pointClosestToWorks(){

        List<Edge> la = new ArrayList<>();
        la.add(edge1);
        la.add(edge2);
        var test1 = new SingleRoute(la);


        var actual1 = test1.pointClosestTo(p1);
        var actual2 = test1.pointClosestTo(p2);
        var actual3 = test1.pointClosestTo(p3);
        var actual4 = test1.pointClosestTo(p4);

        var expected1 = la.get(0).fromPoint();
        var expected2 = la.get(0).toPoint();
        var expected3 = la.get(1).fromPoint();
        var expected4 = la.get(1).toPoint();

        //1er et 3ème marchent, pas les autres
        assertEquals(expected1, actual1);
        assertEquals(expected2, actual2);
        assertEquals(expected3, actual3);
        assertEquals(expected4, actual4);


    }

}*/
