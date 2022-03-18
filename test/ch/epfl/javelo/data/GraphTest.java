package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static ch.epfl.javelo.data.Attribute.*;
import static org.junit.jupiter.api.Assertions.*;

public class GraphTest {

    @Test
    void loadFromOpensWell(){
        try {
            Path path = Path.of("lausanne");
            Graph lausanne = Graph.loadFrom(path);
        } catch (IOException e){}
    }

    @Test
    void easyMethodsWorkWell(){
        //2 nodes
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234,
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234
        });
        GraphNodes ns = new GraphNodes(b);

        //1 sector
        ByteBuffer SectorsBuffer = ByteBuffer.allocate(16384 * (Integer.BYTES + Short.BYTES));
        SectorsBuffer.putInt(0, 835836139);
        SectorsBuffer.putShort(4, (short) 1);
        GraphSectors Graphsectors = new GraphSectors(SectorsBuffer);

        //1 edge
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
                (short) 0x180C, (short) 0xFEFF, (short) 0xFFFE, (short) 0xF000
        });
        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

        //1 attribut
        List<AttributeSet> attributeSets = new ArrayList<>();
        attributeSets.add(AttributeSet.of(HIGHWAY_SERVICE));

        Graph graph = new Graph(ns, Graphsectors, edges, attributeSets);

        assertEquals(2, graph.nodeCount());

        PointCh pointNode1 = new PointCh(2_600_000, 1_200_000);
        assertEquals(pointNode1, graph.nodePoint(0));
        assertEquals(pointNode1, graph.nodePoint(1));

        assertEquals(2, graph.nodeOutDegree(0));
        assertEquals(0x1234, graph.nodeOutEdgeId(0,0));

        assertEquals(12, graph.edgeTargetNodeId(0));
        assertTrue(graph.edgeIsInverted(0));

        assertEquals(16.6875, graph.edgeLength(0));
        assertEquals(16.0, graph.edgeElevationGain(0));

        float[] expectedSamples = new float[]{
                384.0625f, 384.125f, 384.25f, 384.3125f, 384.375f,
                384.4375f, 384.5f, 384.5625f, 384.6875f, 384.75f
        };
        DoubleUnaryOperator a = Functions.sampled(expectedSamples, 16.6875);
    }

    @Test
    void nodeClosestToTest(){
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234,
                2_800_000 << 4,
                1_100_000 << 4,
                0x2_000_1834
        });
        GraphNodes ns = new GraphNodes(b);

        //1 sector
        ByteBuffer SectorsBuffer = ByteBuffer.allocate(16384 * (Integer.BYTES + Short.BYTES));
        SectorsBuffer.putInt(0, 1);
        SectorsBuffer.putShort(4, (short) 1);
        GraphSectors Graphsectors = new GraphSectors(SectorsBuffer);

        GraphSectors.Sector sector1 = new GraphSectors.Sector(1, 2);
        ArrayList<GraphSectors.Sector> sectors = new ArrayList();
        sectors.add(sector1);

        //1 edge
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
                (short) 0x180C, (short) 0xFEFF, (short) 0xFFFE, (short) 0xF000
        });
        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

        //1 attribut
        List<AttributeSet> attributeSets = new ArrayList<>();
        attributeSets.add(AttributeSet.of(HIGHWAY_SERVICE));

        Graph graph = new Graph(ns, Graphsectors, edges, attributeSets);

        PointCh actual = new PointCh(2_485_000, 1_200_000);
        assertEquals(1, graph.nodeClosestTo(actual, 2));
    }


    private static final int SUBDIVISIONS_PER_SIDE = 128;
    private static final int SECTORS_COUNT = SUBDIVISIONS_PER_SIDE * SUBDIVISIONS_PER_SIDE;


    private static GraphSectors createSectorsGraph() {
        ByteBuffer sectorsBuffer = ByteBuffer.allocate(SECTORS_COUNT * (Integer.BYTES + Short.BYTES));
        for (int i = 0; i < SECTORS_COUNT; i += 1) {
            sectorsBuffer.putInt(i*Integer.BYTES ,i);
            sectorsBuffer.putShort(i*(Integer.BYTES+Short.BYTES),(short) 1);
        }
        assert !sectorsBuffer.hasRemaining();
        return new GraphSectors(sectorsBuffer);
    }

    private static GraphNodes createNodeGraph() {
        var nodesCount = 10_0000;
        var buffer = IntBuffer.allocate(3 * nodesCount);
        for (int i = 0; i < nodesCount*3; i += 1) {
            var e = 2_600_000 + i;
            var n = 1_200_000 + i;
            var nodeId = i;
            buffer.put(3 * i, e);
            buffer.put(3 * i + 1, n);
        }
        return new GraphNodes(buffer);
    }


    private static final double DELTA = 1./16;


    @Test
    void graphCreationTests() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne"));
    }

    private void assertThrows(Class<IOException> ioExceptionClass, Object o) {
    }

    @Test
    void nodeCount() throws IOException{
        Graph graph = Graph.loadFrom(Path.of("lausanne"));
        System.out.println(graph.nodeCount());
    }

    @Test
    void nodePoint() throws IOException{
        Graph graph = Graph.loadFrom(Path.of("lausanne"));

        double lon = Math.toRadians(6.6013034);
        double lat = Math.toRadians(46.6326106);

        assertEquals( Ch1903.e(lon ,lat), graph.nodePoint(2022).e(), DELTA );
        assertEquals( Ch1903.n( lon,lat), graph.nodePoint(2022).n(), DELTA);
    }


    @Test
    void nodeOutDegree()  throws IOException{
        Graph graph = Graph.loadFrom(Path.of("lausanne"));
        LongBuffer osmNodesBuffer;
        try (FileChannel channel = FileChannel.open(Path.of("lausanne/nodes_osmid.bin"))) {
            osmNodesBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
        }

        int index = nodeFinder(575140169, osmNodesBuffer);
        assertEquals(5, graph.nodeOutDegree(index));

        index = nodeFinder(280615, osmNodesBuffer);
        assertEquals(4, graph.nodeOutDegree(index));
    }

    @Test
    void nodeOutEdgeId()  throws IOException{
        Graph graph = Graph.loadFrom(Path.of("lausanne"));

        LongBuffer osmNodesBuffer;
        try (FileChannel channel = FileChannel.open(Path.of("lausanne/nodes_osmid.bin"))) {
            osmNodesBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
        }

        int index = nodeFinder(575140169, osmNodesBuffer);
        int edgeIndex = graph.nodeOutEdgeId(index, 0);

        assertEquals(edgeIndex+1, graph.nodeOutEdgeId(index, 1));
        assertEquals(edgeIndex+2, graph.nodeOutEdgeId(index, 2));
        assertEquals(edgeIndex+3, graph.nodeOutEdgeId(index, 3));
        assertEquals(edgeIndex+4, graph.nodeOutEdgeId(index, 4));
    }

    @Test
    void nodeClosestToOnNode() throws IOException{
        Graph graph = Graph.loadFrom(Path.of("lausanne"));

        LongBuffer osmNodesBuffer;
        try (FileChannel channel = FileChannel.open(Path.of("lausanne/nodes_osmid.bin"))) {
            osmNodesBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
        }

        int index = nodeFinder(341350633, osmNodesBuffer);
        double lon =Math.toRadians(6.6568706);
        double lat =Math.toRadians( 46.5613478);
        double e = Ch1903.e(lon,lat);
        double n = Ch1903.n(lon, lat) ;

        int computed = graph.nodeClosestTo(new PointCh(e, n), 100);
        System.out.println(computed);

        PointCh foundPoint = graph.nodePoint(computed);

        double foundE= foundPoint.e();
        double foundN = foundPoint.n();
        assertEquals(e,foundE,DELTA);
        assertEquals(n,foundN,DELTA);
    }


    @Test
    void nodeClosestToWorksOnRandom() throws IOException{
        Graph graph = Graph.loadFrom(Path.of("lausanne"));

        LongBuffer osmNodesBuffer;
        try (FileChannel channel = FileChannel.open(Path.of("lausanne/nodes_osmid.bin"))) {
            osmNodesBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
        }

        int index = nodeFinder(736459726, osmNodesBuffer);
        double lon =Math.toRadians(6.6784712);
        double lat =Math.toRadians( 46.5337000);
        double e = Ch1903.e(lon,lat);
        double n = Ch1903.n(lon, lat) ;

        int computed = graph.nodeClosestTo(new PointCh(e, n), 1000);

        PointCh foundPoint = graph.nodePoint(computed);

        double foundE= foundPoint.e();
        double foundN = foundPoint.n();
        assertEquals(e,foundE,DELTA);
        assertEquals(n,foundN,DELTA);
    }


    @Test
    void edgeTargetNodeId() throws  IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne"));

        LongBuffer osmNodesBuffer;
        try (FileChannel channel = FileChannel.open(Path.of("lausanne/nodes_osmid.bin"))) {
            osmNodesBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
        }
        int index = nodeFinder(253204523, osmNodesBuffer);
        int targetIndex = nodeFinder(313148231, osmNodesBuffer);

        assertEquals( targetIndex,
                graph.edgeTargetNodeId(graph.nodeOutEdgeId(index ,1)));

        targetIndex = nodeFinder(649303371, osmNodesBuffer);
        assertEquals( targetIndex,
                graph.edgeTargetNodeId(graph.nodeOutEdgeId(index ,0)));
    }

    @Test
    void edgeIsInverted() throws IOException{
        Graph graph = Graph.loadFrom(Path.of("lausanne"));

        LongBuffer osmNodesBuffer;
        try (FileChannel channel = FileChannel.open(Path.of("lausanne/nodes_osmid.bin"))) {
            osmNodesBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
        }
        int index = nodeFinder(253204523, osmNodesBuffer);

        int edgeIndex = graph.nodeOutEdgeId(index ,0);
        assertTrue(graph.edgeIsInverted(edgeIndex));

        edgeIndex = graph.nodeOutEdgeId(index ,1);
        assertFalse(graph.edgeIsInverted(edgeIndex));

    }

    @Test
    void edgeAttributes() throws  IOException{
        Graph graph = Graph.loadFrom(Path.of("lausanne"));

        LongBuffer osmNodesBuffer;
        try (FileChannel channel = FileChannel.open(Path.of("lausanne/nodes_osmid.bin"))) {
            osmNodesBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
        }
        int index = nodeFinder(253204523, osmNodesBuffer);
        int edgeIndex = graph.nodeOutEdgeId(index ,0);

        AttributeSet output = AttributeSet.of(HIGHWAY_TERTIARY, SURFACE_ASPHALT);
        assertEquals(output, graph.edgeAttributes(edgeIndex));

        edgeIndex = graph.nodeOutEdgeId(index ,1);
        output = AttributeSet.of(HIGHWAY_TERTIARY, SURFACE_ASPHALT);
        assertEquals(output, graph.edgeAttributes(edgeIndex));


        index = nodeFinder(1505761262, osmNodesBuffer);
        edgeIndex = graph.nodeOutEdgeId(index ,0);

        output = AttributeSet.of(HIGHWAY_MOTORWAY, SURFACE_ASPHALT, ONEWAY_YES);
        assertEquals(output, graph.edgeAttributes(edgeIndex));

        edgeIndex = graph.nodeOutEdgeId(index ,1);
        output = AttributeSet.of(HIGHWAY_MOTORWAY, SURFACE_ASPHALT, ONEWAY_YES);
        assertEquals(output, graph.edgeAttributes(edgeIndex));
    }

    @Test
    void edgeLengthOnSmallRoute() throws IOException{
        Graph graph = Graph.loadFrom(Path.of("lausanne"));

        LongBuffer osmNodesBuffer;
        try (FileChannel channel = FileChannel.open(Path.of("lausanne/nodes_osmid.bin"))) {
            osmNodesBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
        }
        int index = nodeFinder(416356047, osmNodesBuffer);
        int edgeIndex = graph.nodeOutEdgeId(index ,1);
        double expectedLength = 45 ;
        assertEquals(expectedLength, graph.edgeLength(edgeIndex),5);
    }

    @Test
    void edgeElevationGain() {
    }

    @Test
    void edgeProfile() {
    }

    @Test
    void testNodeClosestToNapoleon() throws IOException {
        Graph g = Graph.loadFrom(Path.of("lausanne"));
        System.out.println(Ch1903.e(Math.toRadians(6.5790772), Math.toRadians(46.5218976)));
        System.out.println(Ch1903.n(Math.toRadians(6.5790772), Math.toRadians(46.5218976)));
        PointCh napoleon = new PointCh(Ch1903.e(Math.toRadians(6.5790772), Math.toRadians(46.5218976)), Ch1903.n(Math.toRadians(6.5790772), Math.toRadians(46.5218976)));
        System.out.println(g.nodeClosestTo(napoleon, 100));
        assertEquals(153713, g.nodeClosestTo(napoleon, 100));
        // equivalent osm de : https://www.openstreetmap.org/n
    }

    private int nodeFinder(int osmId, LongBuffer buffer){
        for (int i = 0; i < buffer.capacity() ; i++) {
            if (buffer.get(i) == osmId) return i;
        }
        System.out.println("node not found");
        return -1;
    }

}
