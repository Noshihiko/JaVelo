package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static java.lang.Math.pow;

/**
 * Représente le graphe JaVelo
 *
 *  @author Camille Espieux (324248)
 *  @author Chiara Freneix (329552)
 *
 */
public final class Graph {

    private static PointCh COOR;
    private static AttributeSet OSM_ATTRIBUTES;
    private static Graph graphLoadFrom;
    private static final int OFFSET_NODE_CLOSEST = -1;

    public GraphNodes nodes;
    public GraphSectors sectors;
    public GraphEdges edges;
    public List<AttributeSet> attributeSets;

    /**
     * Constructeur de Graph
     *
     * @param nodes
     * @param sectors
     * @param edges
     * @param attributeSets
     */
    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets) {
        this.nodes = nodes;
        this.sectors = sectors;
        this.edges = edges;
        this.attributeSets = attributeSets;
    }

    public static Graph loadFrom(Path basePath) throws IOException {
        GraphNodes nodes = new GraphNodes((bufferFile(basePath, "nodes.bin")).asIntBuffer());
        GraphEdges edges = new GraphEdges(bufferFile(basePath, "edges.bin"),
                bufferFile(basePath, "profile_ids.bin").asIntBuffer(),
                bufferFile(basePath, "elevations.bin").asShortBuffer());
        GraphSectors sectors = new GraphSectors(bufferFile(basePath, "sectors.bin"));

        List<AttributeSet> attributes = new ArrayList<>();
        LongBuffer a = bufferFile(basePath, "attributes.bin").asLongBuffer();
        int lengthBuffer = a.capacity();

        for (int i = 0; i < lengthBuffer; i++) {
            AttributeSet b = new AttributeSet(a.get(i));
            attributes.add(b);
        }

        LongBuffer nodes_osmId = bufferFile(basePath, "nodes_osmid.bin").asLongBuffer();

        return new Graph(nodes, sectors, edges, attributes);
    }

    private static ByteBuffer bufferFile(Path basePath, String nameFile) throws IOException {
        ByteBuffer buffer;
        try (FileChannel channel = FileChannel.open(basePath.resolve(nameFile))) {
            buffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        }
        return buffer;
    }

    public int nodeCount() {
        return nodes.count();
    }

    public PointCh nodePoint(int nodeId) {
        return COOR = new PointCh(nodes.nodeE(nodeId), nodes.nodeN(nodeId));
    }

    public int nodeOutDegree(int nodeId) {
        return nodes.outDegree(nodeId);
    }

    public int nodeOutEdgeId(int nodeId, int edgeIndex) {
        return nodes.edgeId(nodeId, edgeIndex);
    }

    public int nodeClosestTo(PointCh point, double searchDistance) {
        int nodeId = OFFSET_NODE_CLOSEST;
        double minDistance = pow(searchDistance, 2);
        List<GraphSectors.Sector> sectorsClosePoint = sectors.sectorsInArea(point, searchDistance);

        for (int i = 0; i < sectorsClosePoint.size(); i++) {
            GraphSectors.Sector sector = sectorsClosePoint.get(i);

            for (int j = sector.startNodeId(); j < sector.endNodeId(); j++) {
                if (nodePoint(j).squaredDistanceTo(point) <= minDistance) {
                    nodeId = j;
                    minDistance = nodePoint(j).squaredDistanceTo(point);
                }
            }
        }
        return nodeId;
    }

    public int edgeTargetNodeId(int edgeId) {
        return edges.targetNodeId(edgeId);
    }

    public boolean edgeIsInverted(int edgeId) {
        return edges.isInverted(edgeId);
    }

    public AttributeSet edgeAttributes(int edgeId) {
        return OSM_ATTRIBUTES = new AttributeSet(edges.attributesIndex(edgeId));
    }

    public double edgeLength(int edgeId) {
        return edges.length(edgeId);
    }

    public double edgeElevationGain(int edgeId) {
        return edges.elevationGain(edgeId);
    }

    public DoubleUnaryOperator edgeProfile(int edgeId) {
        if (edges.hasProfile(edgeId)) {
            return Functions.sampled(edges.profileSamples(edgeId), edgeLength(edgeId));
        } else {
            return Functions.constant(Double.NaN);
        }
    }
}
