package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GraphSectorTest2 {

    public GraphSectorTest2() {
    }

    @Test
    void SectorTestWithSectorWithOneNoeudAndAtTheBeginningOfTheMap() {
        ByteBuffer SectorsBuffer = ByteBuffer.allocate(16384 * (Integer.BYTES + Short.BYTES));
        SectorsBuffer.putInt(0, 835836139);

        SectorsBuffer.putShort(4, (short) 1);

        GraphSectors Graphsectors = new GraphSectors(SectorsBuffer);
        GraphSectors.Sector sector1 = new GraphSectors.Sector(835836139, 835836140);
        ArrayList<GraphSectors.Sector> sectors = new ArrayList();
        sectors.add(sector1);

        PointCh actual1 = new PointCh(Ch1903.e(Math.toRadians(5.96), Math.toRadians(45.82)),
                Ch1903.n(Math.toRadians(5.96), Math.toRadians(45.82)));

        System.out.println(actual1);

        assertEquals(sectors, Graphsectors.sectorsInArea(actual1, 100));
    }

    @Test
    void SectorTestWithSectorWithOneNoeudAndAtTheEndingOfTheMap() {
        ByteBuffer SectorsBuffer = ByteBuffer.allocate(16384 * (Integer.BYTES + Short.BYTES));
        SectorsBuffer.putInt(SectorsBuffer.capacity() - (Integer.BYTES + Short.BYTES), 835836139);
        System.out.println(SectorsBuffer.capacity() - (Integer.BYTES + Short.BYTES));

        SectorsBuffer.putShort(SectorsBuffer.capacity() - (Short.BYTES), (short) 1);
        System.out.println(SectorsBuffer.capacity() - (Short.BYTES));

        GraphSectors Graphsectors = new GraphSectors(SectorsBuffer);
        GraphSectors.Sector sector1 = new GraphSectors.Sector(835836139, 835836140);
        ArrayList<GraphSectors.Sector> sectors = new ArrayList();
        sectors.add(sector1);

        PointCh actual1 = new PointCh(Ch1903.e(Math.toRadians(10.48), Math.toRadians(47.77)),
                Ch1903.n(Math.toRadians(10.48), Math.toRadians(47.77)));
        PointCh actual = new PointCh(2834000, 1296000);

        System.out.println(actual);

        assertEquals(sectors, Graphsectors.sectorsInArea(actual, 10));
    }

    @Test
    void GraphSectorWorksNormally() {
        byte[] bufferId = new byte[128 * 128 * 3];
        for (int i = 0; i < bufferId.length; ++i) {
            bufferId[i] = (byte) i;
        }

        ByteBuffer buffer = ByteBuffer.wrap(bufferId);
        PointCh point = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        GraphSectors sectors1 = new GraphSectors(buffer);
        List<GraphSectors.Sector> expected1 = new ArrayList<>();
        expected1.add(new GraphSectors.Sector(buffer.getInt(0), buffer.getShort(4) + buffer.getInt(0)));
        assertArrayEquals(expected1.toArray(), sectors1.sectorsInArea(point, 1).toArray());

        List<GraphSectors.Sector> expected2 = new ArrayList<>();
        GraphSectors sectors2 = new GraphSectors(buffer);
        expected2.add(new GraphSectors.Sector(buffer.getInt(0), buffer.getShort(4) + buffer.getInt(0)));
        expected2.add(new GraphSectors.Sector(buffer.getInt(6), buffer.getShort(10) + buffer.getInt(6)));
        expected2.add(new GraphSectors.Sector(buffer.getInt(128 * 6), buffer.getShort(128 * 6 + 4) + buffer.getInt(128 * 6)));
        expected2.add(new GraphSectors.Sector(buffer.getInt(129 * 6), buffer.getShort(129 * 6 + 4) + buffer.getInt(129 * 6)));
        assertArrayEquals(expected2.toArray(), sectors2.sectorsInArea(point, 2800).toArray());

    }



    @Test
    void GraphsSectorsWorksTrivial(){
        byte[] tab = new byte[48];
        for (byte i = 0; i<48; i++){
            tab[i] =  i;
        }
        ByteBuffer b = ByteBuffer.wrap(tab) ;
        List<GraphSectors.Sector> output = new ArrayList<GraphSectors.Sector>();

    }

    @Test
    void GraphSectorsWorksWith00(){

        byte[] tab = new byte[98304];

        for (int i = 0; i< 98304; i+= 6){

            tab[i] = (byte) Bits.extractUnsigned(i*4, 24, 8);
            tab[i+1] = (byte) Bits.extractUnsigned(i*4, 16, 8);
            tab[i+2] = (byte) Bits.extractUnsigned(i*4, 8, 8);
            tab[i+3] = (byte) Bits.extractUnsigned(i*4, 0, 8);

            tab[i+4]= (byte) 0 ;
            tab[i+5] = (byte) 1;

        }

        ByteBuffer buffer = ByteBuffer.wrap(tab);

        GraphSectors graph = new GraphSectors(buffer);

        ArrayList<GraphSectors.Sector> output = new ArrayList<>();
        output.add(new GraphSectors.Sector(0, 1));

        List<GraphSectors.Sector> actual = graph.sectorsInArea( new PointCh(SwissBounds.MIN_E + 100 , SwissBounds.MIN_N +100), 5);


        assertEquals(output.get(0), actual.get(0) );

    }

    @Test
    void GraphSectorsWorksWithExpected(){

        byte[] tab = new byte[98304];

        for (int i = 0; i< 16384; i++){

            tab[i*6] = (byte) Bits.extractUnsigned(i*4, 24, 8);
            tab[6*i+1] = (byte) Bits.extractUnsigned(i*4, 16, 8);
            tab[6*i+2] = (byte) Bits.extractUnsigned(i*4, 8, 8);
            tab[6*i+3] = (byte) Bits.extractUnsigned(i*4, 0, 8);

            tab[6*i+4]= (byte) 0 ;
            tab[6*i+5] = (byte) 4;

        }

        ByteBuffer buffer = ByteBuffer.wrap(tab);

        GraphSectors graph = new GraphSectors(buffer);

        ArrayList<GraphSectors.Sector> output = new ArrayList<>();

        for (int i =0; i< 384; i+=128 ){
            for (int j = 0; j<3; j++){
                output.add(new GraphSectors.Sector((j+i)*4, (j+i+1)*4));
            }
        }

        List<GraphSectors.Sector> actual = graph.sectorsInArea(
                new PointCh(SwissBounds.MIN_E+ 3700, SwissBounds.MIN_N + 2500), 2000);


        //actual.set(6,new GraphSectors.Sector(0, 0));

        //ici

        assertArrayEquals(output.toArray(), actual.toArray());

    }

    @Test
    void GraphSectorsWorksWithEntireMap(){

        byte[] tab = new byte[98304];

        for (int i = 0; i< 16384; i++){

            tab[i*6] = (byte) Bits.extractUnsigned(i*4, 24, 8);
            tab[6*i+1] = (byte) Bits.extractUnsigned(i*4, 16, 8);
            tab[6*i+2] = (byte) Bits.extractUnsigned(i*4, 8, 8);
            tab[6*i+3] = (byte) Bits.extractUnsigned(i*4, 0, 8);

            tab[6*i+4]= (byte) 0 ;
            tab[6*i+5] = (byte) 4;

        }

        ByteBuffer buffer = ByteBuffer.wrap(tab);

        GraphSectors graph = new GraphSectors(buffer);

        ArrayList<GraphSectors.Sector> output = new ArrayList<>();

        for (int j =0; j< 128; j++ ){
            for (int i = 0; i<128; i++){
                output.add(new GraphSectors.Sector((j*128+i)*4, (j*128+i+1)*4));
            }
        }

        List<GraphSectors.Sector> actual = graph.sectorsInArea(
                new PointCh(SwissBounds.MIN_E+SwissBounds.WIDTH/2 +10, SwissBounds.MIN_N+SwissBounds.HEIGHT/2 + 10 ), SwissBounds.WIDTH);

        assertArrayEquals(output.toArray(), actual.toArray());

    }

}
