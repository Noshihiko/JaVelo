package ch.epfl.javelo.data;

import java.nio.IntBuffer;

import static ch.epfl.javelo.Bits.extractUnsigned;
import static ch.epfl.javelo.Q28_4.asDouble;

/**
 * Représente le tableau de tous les nœuds du graphe JaVelo
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 *
 * @param buffer
 */
public record GraphNodes(IntBuffer buffer) {
    //à vérifier mais normalement c'est bon
    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;

    private static final int nbrOfAttributs = 3;
    private static final int debutBitAretes = 28;
    private static final int tailleAretesSortantes = 4;
    private static final int debutBitIdentite = 0;
    private static final int tailleIdentite = 28;

    /**
     * @return le nombre total de noeuds
     */
    public int count(){
        return buffer().capacity()/nbrOfAttributs;
    }

    /**
     *
     *
     * @param nodeId
     *          le nœud d'identité donné
     * @return la coordonnée E du nœud d'identité donné
     */
    public double nodeE(int nodeId){
        return asDouble(buffer().get(NODE_INTS*nodeId+OFFSET_E));
    }

    /**
     *
     *
     * @param nodeId
     *          le nœud d'identité donné
     * @return la coordonnée N du nœud d'identité donné
     */
    public double nodeN(int nodeId){
        return asDouble(buffer().get(NODE_INTS*nodeId+OFFSET_N));
    }

    /**
     *
     *
     * @param nodeId
     *          le nœud d'identité donné
     * @return le nombre d'arêtes sortant du nœud d'identité donné
     */
    public int outDegree(int nodeId){
        return extractUnsigned(buffer().get(NODE_INTS*nodeId+OFFSET_OUT_EDGES),debutBitAretes,tailleAretesSortantes);
    }

    /**
     * Vérifie si l'index de l'arrête est valide (compris entre 0, inclus, et le nombre d'arêtes sortant du nœud, exclus)
     * puis donne l'identité de la edgeIndex-ième arête sortant du nœud d'identité nodeId
     *
     * @param nodeId
     *          le nœud d'identité donné
     * @param edgeIndex
     *          index de l'arête
     * @return l'identité de la edgeIndex-ième arête sortant du nœud d'identité nodeId
     */
    public int edgeId(int nodeId, int edgeIndex){
        assert 0 <= edgeIndex && edgeIndex < outDegree(nodeId);
        return (extractUnsigned(buffer().get(NODE_INTS*nodeId+OFFSET_OUT_EDGES),debutBitIdentite,tailleIdentite))+edgeIndex;
    }

}
