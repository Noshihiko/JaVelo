package ch.epfl.javelo.data;


import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class GraphEdgesTest {

    @Test
    void teacherTest(){
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 2022
        edgesBuffer.putShort(8, (short) 2022);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 3. Index du premier échantillon : 1.
                (3 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000
        });

        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);

        assertTrue(edges.isInverted(0));
        assertEquals(12, edges.targetNodeId(0));
        assertEquals(16.6875, edges.length(0));
        assertEquals(16.0, edges.elevationGain(0));
        assertEquals(2022, edges.attributesIndex(0));
        float[] expectedSamples = new float[]{
                384.0625f, 384.125f, 384.25f, 384.3125f, 384.375f,
                384.4375f, 384.5f, 384.5625f, 384.6875f, 384.75f
        };
        assertArrayEquals(expectedSamples, edges.profileSamples(0));
    }

    @Test
    void testSimpleSampleType1(){

        float[] expectedSamples = new float[]{
                384.75f, 384.75f, 384.75f
        };

        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Identité noeud de destination : 4
        edgesBuffer.putInt(0, 0b00000000000000000000000000000100);
        // Longueur : 64 (= 4 m)
        edgesBuffer.putShort(4, (short) 64);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 2022
        edgesBuffer.putShort(8, (short) 2022);

        //Type1
        //identité premier échantillon du profil : 1
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                0b01000000000000000000000000000001
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0x180C,
                (short) 0x180C, (short) 0x180C
        });

        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);

        assertTrue(edges.hasProfile(0));
        assertArrayEquals(expectedSamples, edges.profileSamples(0));

    }

    @Test
    void simpleTestSampleType2(){
        float[] expectedSamples = new float[]{
                0f, -0.0625f, -1.125f, -1f
        };

        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Identité noeud de destination : 5
        edgesBuffer.putInt(0, 0b00000000000000000000000000000100);
        // Longueur : 96 (= 6 m)
        edgesBuffer.putShort(4, (short) 96);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 2022
        edgesBuffer.putShort(8, (short) 2022);

        //Type2
        //identité premier échantillon du profil : 0
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                0b10000000000000000000000000000000
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0xFFEF, (short) 0x0200
        });

        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        assertArrayEquals(expectedSamples, edges.profileSamples(0));
    }

    @Test
    void oneOutOfTwoSampleType2Test(){

        float[] expectedSamples = new float[]{
                0f, -0.0625f, -1.125f
        };

        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Identité noeud de destination : 3
        edgesBuffer.putInt(0, 0b000000000000000000000000000000011);
        // Longueur : 64 (= 4 m)
        edgesBuffer.putShort(4, (short) 64);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 2022
        edgesBuffer.putShort(8, (short) 2022);

        //Type2
        //identité premier échantillon du profil : 0
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                0b10000000000000000000000000000000
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0xFFEF, (short) 0x0200
        });

        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        assertArrayEquals(expectedSamples, edges.profileSamples(0));

    }

    @Test
    void oneOutOfFourTestSampleType3(){

        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 224);
        // Identité de l'ensemble d'attributs OSM : 2022
        edgesBuffer.putShort(8, (short) 2022);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 3. Index du premier échantillon : 1.
                (3 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000
        });

        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);

        assertTrue(edges.isInverted(0));
        assertEquals(12, edges.targetNodeId(0));
        assertEquals(16.6875, edges.length(0));
        assertEquals(14.0, edges.elevationGain(0));
        assertEquals(2022, edges.attributesIndex(0));
        float[] expectedSamples = new float[]{
                384.0625f, 384.125f, 384.25f, 384.3125f, 384.375f,
                384.4375f, 384.5f, 384.5625f, 384.6875f, 384.75f
        };
        assertArrayEquals(expectedSamples, edges.profileSamples(0));

    }


    @Test
    void testType1Et2() {

        ByteBuffer edgesBuffer = ByteBuffer.allocate(30);
        // Profil 1 :
        // Sens : Normal. Nœud destination : 53.
        edgesBuffer.putInt(0, 0b110101);
        // Longueur : 4.0m
        edgesBuffer.putShort(4, (short)0x04_0);
        // Dénivelé : -0.25m
        edgesBuffer.putShort(6, (short)0b1111111111111100);
        edgesBuffer.putShort(8, (short)2102);

        //Profil 2 :
        edgesBuffer.putInt(10, 0b01100);
// Longueur : 0x12.b m (= 17.6875 m)
        edgesBuffer.putShort(14, (short) 0x11_b);
// Dénivelé : 0x10.0 m (= 26.75 m)
        edgesBuffer.putShort(16, (short) 0x1A_c);
// Identité de l'ensemble d'attributs OSM : tkt
        edgesBuffer.putShort(18, (short) 30921);
        for (int i = 0; i < 5; i++) {
            edgesBuffer.putShort(i+20, (short)0b0);
        }

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 1 | Index du premier echantillon : 0
                (1 << 30 ),
                // Type : 2. Index du premier échantillon : 4.
                (2 << 30) | 4,
                // Type : 0.
                0
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short)0x180C, (short)0x181C, (short)0x180D,
                (short) 1102.23f,
                (short) 0x180C, (short) 0xBE0F,
                (short) 0x2E20, (short) 0xFFEE,
                (short) 0x2020, (short) 0x1000
        });

        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);

        assertFalse(edges.isInverted(1));
        assertEquals(12, edges.targetNodeId(1));
        assertEquals(17.6875, edges.length(1));
        assertEquals(26.75, edges.elevationGain(1));
        assertEquals(30921, edges.attributesIndex(1));
        float[] expectedSamplesType2 = new float[]{
                384.75f, 380.625f, 381.5625f, 384.4375f, 386.4375f, 386.375f,
                385.25f, 387.25f, 389.25f, 390.25f,
        };
        assertFalse(edges.isInverted(0));
        assertEquals(53, edges.targetNodeId(0));
        assertEquals(4.0, edges.length(0));
        //assertEquals(-4.0/16, edges.elevationGain(0));
        assertEquals(2102, edges.attributesIndex(0));

        float[] expectedSamplesType1 = new float []{
                384.75f, 385.75f, 384.8125f
        };

        assertArrayEquals(expectedSamplesType1, edges.profileSamples(0));

        assertArrayEquals(expectedSamplesType2, edges.profileSamples(1));
        assertArrayEquals(new float[]{}, edges.profileSamples(2));
    }










    @Test
    void testGiven(){
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
// Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
// Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
// Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
// Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 3. Index du premier échantillon : 1.
                (3 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000
        });

        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);

        assertTrue(edges.isInverted(0));
        assertEquals(12, edges.targetNodeId(0));
        assertEquals(16.6875, edges.length(0));
        assertEquals(16.0, edges.elevationGain(0));
        assertEquals(2022, edges.attributesIndex(0));
        float[] expectedSamples = new float[]{
                384.0625f, 384.125f, 384.25f, 384.3125f, 384.375f,
                384.4375f, 384.5f, 384.5625f, 384.6875f, 384.75f
        };
        assertArrayEquals(expectedSamples, edges.profileSamples(0));

    }

    @Test
    void testEdges2(){
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
// Sens : direct. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
// Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 208);
// Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
// Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 2. Index du premier échantillon : 1.
                (2 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0xABCD,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF005,
                (short) 0x0200
        });

        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);

        assertFalse(edges.isInverted(0));
        assertEquals(12, edges.targetNodeId(0));
        assertEquals(13, edges.length(0));
        assertEquals(16.0, edges.elevationGain(0));
        assertEquals(2022, edges.attributesIndex(0));
        float[] expectedSamples = new float[]{
                384.75f , 384.625f , 384.5625f , 384.5f , 384.375f , 383.375f , 383.6875f , 383.8125f} ;

        assertArrayEquals(expectedSamples, edges.profileSamples(0));
    }
    /*
    @Test
    void testEdges(){
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
// Sens : direct. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
// Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 208);
// Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
// Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 2. Index du premier échantillon : 1.
                (1 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 2345,
                (short) 200*16, (short) 20*16,
                (short) 100*16, (short) -10*16,
                (short) 35*16,  (short) 0*16,
                (short) 100*16, (short) -1*16,
        });

        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);

        /* assertFalse(edges.isInverted(0));
        assertEquals(12, edges.targetNodeId(0));
        assertEquals(13, edges.length(0));
        assertEquals(16.0, edges.elevationGain(0));
        assertEquals(2022, edges.attributesIndex(0));
        float[] expectedSamples = new float[]{
                200f ,20f , 100f , -10f , 35f , 0f , 100f , -1f} ;

        assertArrayEquals(expectedSamples, edges.profileSamples(0));
    }

     */






    @Test
    void isInverted() {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);


        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 3. Index du premier échantillon : 1.
                (3 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000
        });

        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);

        assertTrue(edges.isInverted(0));

    }

    @Test
    void targetNodeId() {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 3. Index du premier échantillon : 1.
                (3 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000
        });

        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);


        assertEquals(12, edges.targetNodeId(0));
    }

    @Test
    void length() {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);

        // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);


        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 3. Index du premier échantillon : 1.
                (3 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000
        });

        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        assertEquals(16.6875, edges.length(0));
    }

    @Test
    void elevationGain() {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);

        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);


        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 3. Index du premier échantillon : 1.
                (3 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000
        });

        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        assertEquals(16.0, edges.elevationGain(0));
    }

    @Test
    void hasProfile() {
    }

    @Test
    void profileSamples() {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 2022
        edgesBuffer.putShort(8, (short) 2022);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 3. Index du premier échantillon : 1.
                (3 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000
        });

        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);


        float[] expectedSamples = new float[]{
                384.0625f, 384.125f, 384.25f, 384.3125f, 384.375f,
                384.4375f, 384.5f, 384.5625f, 384.6875f, 384.75f
        };
        assertArrayEquals(expectedSamples, edges.profileSamples(0));
    }

    @Test
    void attributesIndex() {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);

        // Identité de l'ensemble d'attributs OSM : 2022
        edgesBuffer.putShort(8, (short) 2022);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 3. Index du premier échantillon : 1.
                (3 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000
        });

        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        assertEquals(2022, edges.attributesIndex(0));
    }

    @Test
    void teacherTestProfile1(){
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
// Sens : pas inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
// Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
// Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
// Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 1. Index du premier échantillon : 1.
                (1 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0b1100000001100,
                (short) 0b1100000101010,
                (short) 0b1100000101010,
                (short) 0b1011110101010,
                (short) 0b1011110110010,
                (short) 0b1100000110001,
                (short) 0b1100000110001,
                (short) 0b1011111110001,
                (short) 0b1011111110001,
                (short) 0b1011111100001
        });

        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);

        assertFalse(edges.isInverted(0));
        assertEquals(12, edges.targetNodeId(0));
        assertEquals(16.6875, edges.length(0));
        assertEquals(16.0, edges.elevationGain(0));
        assertEquals(2022, edges.attributesIndex(0));
        float[] expectedSamples = new float[]
                {384.75f, 386.625f, 386.625f, 378.625f, 379.125f, 387.0625f, 387.0625f, 383.0625f, 383.0625f, 382.0625f};
        System.out.println("Expected");
        System.out.println(Arrays.toString(expectedSamples));
        System.out.println();
        assertArrayEquals(expectedSamples, edges.profileSamples(0));
    }

    @Test
    void teacherTestProfile2(){
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
// Sens : pas inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
// Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
// Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
// Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 2. Index du premier échantillon : 1.
                (2 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C,
                (short) 0x1E00,
                (short) 0x8008,
                (short) 0x7F00,
                (short) 0xC000,
                (short) 0xF000,
        });

        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);

        assertFalse(edges.isInverted(0));
        assertEquals(12, edges.targetNodeId(0));
        assertEquals(16.6875, edges.length(0));
        assertEquals(16.0, edges.elevationGain(0));
        assertEquals(2022, edges.attributesIndex(0));
        float[] expectedSamples = new float[]{
                384.75f,
                384.75f+1.875f,
                384.75f+1.875f,
                384.75f+1.875f-8f,
                384.75f+1.875f-8f+0.5f,
                384.75f+1.875f-8f+0.5f+7.9375f,
                384.75f+1.875f-8f+0.5f+7.9375f,
                384.75f+1.875f-8f+0.5f+7.9375f-4,
                384.75f+1.875f-8f+0.5f+7.9375f-4,
                384.75f+1.875f-8f+0.5f+7.9375f-4-1
        };
        System.out.println("Expected");
        System.out.println(Arrays.toString(expectedSamples));
        System.out.println();
        assertArrayEquals(expectedSamples, edges.profileSamples(0));
        assertArrayEquals(expectedSamples, edges.profileSamples(0));
    }

}