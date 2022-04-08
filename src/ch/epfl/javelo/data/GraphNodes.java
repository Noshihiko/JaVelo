package ch.epfl.javelo.data;

import java.nio.IntBuffer;

import static ch.epfl.javelo.Bits.extractUnsigned;
import static ch.epfl.javelo.Q28_4.asDouble;

/**
 * Représente le tableau de tous les nœuds du graphe JaVelo.
 *
 * @param buffer la mémoire tampon contenant la valeur des attributs de la totalité des nœuds du graphe
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */
public record GraphNodes(IntBuffer buffer) {
    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;

    private static final int NBR_OF_ATTRIBUTS = 3;
    private static final int DEBUT_BIT_EDGES = 28;
    private static final int LENGTH_EDGES_OUT = 4;
    private static final int DEBUT_BIT_IDENTITY = 0;
    private static final int LENGTH_IDENTITY = 28;

    /**
     * Retourne le nombre total de nœuds.
     * @return le nombre total de nœuds.
     */
    public int count() {
        return buffer().capacity() / NBR_OF_ATTRIBUTS;
    }

    /**
     * Donne la coordonnée E du nœud d'identité nodeId.
     *
     * @param nodeId le nœud d'identité donné
     *
     * @return la coordonnée E du nœud d'identité donné.
     */
    public double nodeE(int nodeId) {
        return asDouble(buffer().get(NODE_INTS * nodeId + OFFSET_E));
    }

    /**
     * Donne la coordonnée N du nœud d'identité nodeId.
     *
     * @param nodeId le nœud d'identité donné
     *
     * @return la coordonnée N du nœud d'identité donné.
     */
    public double nodeN(int nodeId) {
        return asDouble(buffer().get(NODE_INTS * nodeId + OFFSET_N));
    }

    /**
     * Donne le nombre d'arêtes sortant du nœud d'identité nodeId.
     *
     * @param nodeId le nœud d'identité donné
     *
     * @return le nombre d'arêtes sortant du nœud d'identité donné.
     */
    public int outDegree(int nodeId) {
        return extractUnsigned(buffer().get(NODE_INTS * nodeId + OFFSET_OUT_EDGES), DEBUT_BIT_EDGES, LENGTH_EDGES_OUT);
    }

    /**
     * Vérifie si l'index de l'arête est valide (compris entre 0 inclus et le nombre d'arêtes sortant du nœud, exclus)
     * puis donne l'identité de l'edgeIndex-ième arête sortant du nœud d'identité nodeId.
     *
     * @param nodeId    le nœud d'identité donné
     * @param edgeIndex l'index de l'arête
     *
     * @return l'identité de l'edgeIndex-ième arête sortant du nœud d'identité nodeId.
     */
    public int edgeId(int nodeId, int edgeIndex) {
        assert (0 <= edgeIndex) && (edgeIndex < outDegree(nodeId));
        return (extractUnsigned(buffer().get(NODE_INTS * nodeId + OFFSET_OUT_EDGES), DEBUT_BIT_IDENTITY, LENGTH_IDENTITY))
                + edgeIndex;
    }

}