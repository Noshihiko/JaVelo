package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static ch.epfl.javelo.Bits.extractUnsigned;

public final class Graph {

    private static PointCh COOR;
    private static AttributeSet OSM_ATTRIBUTES;
    private static Graph graphLoadFrom;

    public GraphNodes nodes;
    public GraphSectors sectors;
    public GraphEdges edges;
    public List<AttributeSet> attributeSets;

    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets){
        this.nodes = nodes;
        this.sectors = sectors;
        this.edges = edges;
        this.attributeSets = attributeSets;
    }

    static Graph loadFrom(Path basePath) throws IOException{
        GraphNodes nodes = new GraphNodes((bufferFile(basePath,"nodes.bin")).asIntBuffer());
        GraphEdges edges = new GraphEdges(bufferFile(basePath,"edges.bin"),
                bufferFile(basePath,"profile_ids.bin").asIntBuffer(),
                bufferFile(basePath,"elevations.bin").asShortBuffer());
        GraphSectors sectors = new GraphSectors(bufferFile(basePath,"sectors.bin"));

        List<AttributeSet> attributes = new ArrayList<>();
        int lengthBuffer = bufferFile(basePath,"attributes.bin").asLongBuffer().capacity();

        for (int i = 0; i<lengthBuffer; i++){
            AttributeSet a = new AttributeSet(bufferFile(basePath,"attributes.bin").asLongBuffer().get(i));
            attributes.add(a);
        }

        LongBuffer nodes_osmId = bufferFile(basePath,"nodes_osmid.bin").asLongBuffer();

        return new Graph(nodes,sectors,edges,attributes);
    }

    private static ByteBuffer bufferFile(Path basePath, String nameFile) throws IOException {
        ByteBuffer buffer;
        try (FileChannel channel = FileChannel.open(basePath.resolve(nameFile))) {
            buffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        }
        return buffer;
    }

    public int nodeCount(){
        return nodes.count();
    }

    public PointCh nodePoint(int nodeId){
        return COOR = new PointCh(nodes.nodeE(nodeId), nodes.nodeN(nodeId));
    }

    public int nodeOutDegree(int nodeId){
        return nodes.outDegree(nodeId);
    }

    public int nodeOutEdgeId(int nodeId, int edgeIndex){
        return nodes.edgeId(nodeId,edgeIndex);
    }

    public int nodeClosestTo(PointCh point, double searchDistance){

    }

    public int edgeTargetNodeId(int edgeId){
        return edges.targetNodeId(edgeId);
    }

    public boolean edgeIsInverted(int edgeId){
        return edges.isInverted(edgeId);
    }

    public AttributeSet edgeAttributes(int edgeId){
        return OSM_ATTRIBUTES = new AttributeSet(edges.attributesIndex(edgeId));
    }

    public double edgeLength(int edgeId){
        return edges.length(edgeId);
    }

    public double edgeElevationGain(int edgeId){
        return edges.elevationGain(edgeId);
    }

    // bien fausse sa mere, j'ai modif types et all types to protected au lieu de final
    public DoubleUnaryOperator edgeProfile(int edgeId){
        if (!(edges.hasProfile(edgeId))) {
            return Double.NaN;
        } else {
            int profileType = extractUnsigned(profileIds.get(edgeId), 30,2);
            return (GraphEdges.Types.ALL_types.get(profileType));
        }
    }
}
