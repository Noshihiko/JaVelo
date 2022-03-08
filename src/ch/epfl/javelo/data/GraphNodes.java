package ch.epfl.javelo.data;
import ch.epfl.javelo.Bits;
import java.nio.IntBuffer;

public record GraphNodes(IntBuffer buffer) {
    //à vérifier mais normalement c'est bon
    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;

    private static final int debutBitAretes = 0;
    private static final int tailleAretesSortantes = 4;
    private static final int debutBitIdentite = 3;
    private static final int tailleIdentite = 28;

    public int count(){
        return buffer().capacity();
    }

    public double nodeE(int nodeId){
        return buffer().get(NODE_INTS*nodeId+OFFSET_E);
    }
    public double nodeN(int nodeId){
        return buffer().get(NODE_INTS*nodeId+OFFSET_N);
    }

    public int outDegree(int nodeId){
        return Bits.extractUnsigned(buffer().get(NODE_INTS*nodeId+OFFSET_OUT_EDGES),debutBitAretes,tailleAretesSortantes);
    }

    public int edgeId(int nodeId, int edgeIndex){
        assert 0 <= edgeIndex && edgeIndex < outDegree(nodeId);
        return Bits.extractUnsigned(buffer().get(NODE_INTS*nodeId+OFFSET_OUT_EDGES),debutBitIdentite,tailleIdentite);
    }

}
