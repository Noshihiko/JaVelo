package ch.epfl.javelo.data;
import ch.epfl.javelo.Bits;

import java.nio.IntBuffer;

public record GraphNodes(IntBuffer buffer) {
    //à vérifier mais normalement c'est bon
    //peut etre que c'est pas bon au final vu que j'ai dû rajouter lignes 9-13 :clown:
    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;

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
        return Bits.extractUnsigned(buffer().get(NODE_INTS*nodeId+OFFSET_OUT_EDGES),0,4);
    }
    public int edgeId(int nodeId, int edgeIndex){
        assert 0 <= edgeIndex && edgeIndex < outDegree(nodeId);
        return Bits.extractUnsigned(buffer().get(NODE_INTS*nodeId+OFFSET_OUT_EDGES),3,28);
    }

}
