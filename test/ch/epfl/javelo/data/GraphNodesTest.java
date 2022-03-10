package ch.epfl.javelo.data;

import org.junit.jupiter.api.Test;

import java.nio.IntBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GraphNodesTest {
    //With buffer vide, essayer capacity et les autres
    //With buffer non vide :
    //      NodeId <0, =0, >0 :
    @Test
    void testProf() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234
        });
        System.out.println( 0x2_000_1234 );
        GraphNodes ns = new GraphNodes(b);
        assertEquals(1, ns.count());
        assertEquals(2_600_000, ns.nodeE(0));
        assertEquals(1_200_000, ns.nodeN(0));
        assertEquals(2, ns.outDegree(0));
        assertEquals(0x1234, ns.edgeId(0, 0));
        assertEquals(0x1235, ns.edgeId(0, 1));
    }

    @Test
    void MarcheAvecBornesInf(){
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_485_000 << 4,
                1_075_000 << 4,
                0x2_000_1234
        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(1, ns.count());
        assertEquals(2_485_000, ns.nodeE(0));
        assertEquals(1_075_000, ns.nodeN(0));
        assertEquals(2, ns.outDegree(0));
        assertEquals(0x1234, ns.edgeId(0, 0));
        assertEquals(0x1235, ns.edgeId(0, 1));
    }

    @Test
    void MarcheAvecBornesSup(){
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_834_000 << 4,
                1_296_000 << 4,
                0x2_000_1234
        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(1, ns.count());
        assertEquals(2_834_000, ns.nodeE(0));
        assertEquals(1_296_000, ns.nodeN(0));
        assertEquals(2, ns.outDegree(0));
        assertEquals(0x1234, ns.edgeId(0, 0));
        assertEquals(0x1235, ns.edgeId(0, 1));
    }

    @Test
    void HorsBornes(){
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_835_000 << 4,
                1_294_000 << 4,
                0x2_000_1234
        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(1, ns.count());
        assertEquals(2_835_000, ns.nodeE(0));
        assertEquals(1_294_000, ns.nodeN(0));
        assertEquals(2, ns.outDegree(0));
        assertEquals(0x1234, ns.edgeId(0, 0));
        assertEquals(0x1235, ns.edgeId(0, 1));
    }
    @Test
    void teacherTest(){
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234
        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(1, ns.count());
        assertEquals(2_600_000, ns.nodeE(0));
        assertEquals(1_200_000, ns.nodeN(0));
        assertEquals(2, ns.outDegree(0));
        assertEquals(0x1234, ns.edgeId(0, 0));
        assertEquals(0x1235, ns.edgeId(0, 1));
    }

    @Test
    void severalNodesTest(){
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234,
                6_600_000 << 4,
                1_200_700 << 4,
                0b11110000000000000000000000000000,
                1_607_000 << 4,
                1_289_000 << 4,
                0b11001010111111101011101010111110
        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(3, ns.count());
        assertEquals(2_600_000, ns.nodeE(0));
        assertEquals(1_200_000, ns.nodeN(0));
        assertEquals(2, ns.outDegree(0));
        assertEquals(0x1234, ns.edgeId(0, 0));
        assertEquals(0x1235, ns.edgeId(0, 1));

        assertEquals(6_600_000, ns.nodeE(1));
        assertEquals(1_200_700, ns.nodeN(1));
        assertEquals(15, ns.outDegree(1));
        assertEquals(0, ns.edgeId(1, 0));
        assertEquals(0x0008, ns.edgeId(1, 8));

        assertEquals(1_607_000, ns.nodeE(2));
        assertEquals(1_289_000, ns.nodeN(2));
        assertEquals(12, ns.outDegree(2));
        assertEquals(0b00001010111111101011101010111110, ns.edgeId(2, 0));
        assertEquals(0b00001010111111101011101010111111, ns.edgeId(2, 1));
    }





    @Test
    void readingTrivialBuffer() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 <<4,
                1_200_000 <<4,
                0x2_000_1234
        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(1, ns.count());
        assertEquals(2_600_000, ns.nodeE(0));
        assertEquals(1_200_000, ns.nodeN(0));
        assertEquals(2, ns.outDegree(0));
        assertEquals(0x1234, ns.edgeId(0, 0));
        assertEquals(0x1235, ns.edgeId(0, 1));
    }


    @Test
    void reading2NodesBuffer(){
        IntBuffer c = IntBuffer.wrap(new int[]{
                2_600_000 <<4,
                1_200_000 <<4,
                0x2_000_1234,
                2_500_000 <<4,
                1_100_000 <<4,
                0x5_000_1234
        });
        GraphNodes ns = new GraphNodes(c);


        assertEquals(2, ns.count());
        assertEquals(2_500_000, ns.nodeE(1));
        assertEquals(1_100_000, ns.nodeN(1));
        assertEquals(5, ns.outDegree(1));
        assertEquals(0x1234, ns.edgeId(1, 0));
        assertEquals(0x1238, ns.edgeId(1, 4));

    }

    //EYA

    IntBuffer node1 = IntBuffer.wrap(new int[]{
            2_600_000 << 4,
            1_200_000 << 4,
            0x2_000_1234,
            1200 << 4,
            1200 <<4,
            2,
            4_325_045 << 4,
            2_245_125 << 4,
            0x3_125_1246,
            0,
            0,
            0

    });
    GraphNodes n1 = new GraphNodes(node1);

    @Test
    public void countIsEqualToWhatWasAsked(){
        GraphNodes node1 = new GraphNodes(IntBuffer.allocate(10));
        assertEquals(10/3, node1.count());
        GraphNodes node2 = new GraphNodes(IntBuffer.allocate(2));
        assertEquals(2/3, node2.count());
        GraphNodes node3 = new GraphNodes(IntBuffer.allocate(1000));
        assertEquals(1000/3, node3.count());
    }

    @Test
    public void countWorksForEmptyBuffer() {
        GraphNodes node1 = new GraphNodes(IntBuffer.allocate(0));
        assertEquals(0/3, node1.count());
    }


    @Test
    public void nodeEWorksOnRandomValues(){
        assertEquals(2_600_000,n1.nodeE(0));
        assertEquals(1200,n1.nodeE(1));
        assertEquals(4_325_045,n1.nodeE(2));
        assertEquals(0,n1.nodeE(3));

    }

    @Test
    public void nodeNWorksOnRandomValues(){
        assertEquals(1_200_000, n1.nodeN(0));
        assertEquals(1200,n1.nodeN(1));
        assertEquals(2_245_125 ,n1.nodeN(2));
        assertEquals(0,n1.nodeN(3));
    }

    @Test
    public void nodeNThrowsOnNegativeValues(){

    }



    @Test
    public void outDegreeWorks(){
        assertEquals(2, n1.outDegree(0));
        assertEquals(0, n1.outDegree(1));
        assertEquals(3, n1.outDegree(2));
        assertEquals(0, n1.outDegree(3));

    }

    @Test
    public void edgeIdWorks(){
        assertEquals(0x1234, n1.edgeId(0, 0));
        assertEquals(0x1235, n1.edgeId(0, 1));

        assertThrows(AssertionError.class , () -> {
            n1.edgeId(1,0);
        });

        System.out.println(0x1234);
        System.out.println(0x2_00_1234);


        assertEquals(19206726, n1.edgeId(2, 0));
        assertEquals(19206727, n1.edgeId(2, 1));
        assertEquals(19206728, n1.edgeId(2, 2));
        ;

    }

}