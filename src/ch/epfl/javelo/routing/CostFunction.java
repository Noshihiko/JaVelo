package ch.epfl.javelo.routing;

public interface CostFunction {

    /**
     * Calcule le facteur par lequel la longueur de l'arête d'identité edgeId doit être multipliée
     *
     * @param nodeId
     *          Nœud d'identité de l'arête d'identité edgeId
     * @param edgeId
     *          Arête d'identité donnée, partant du nœud d'identité nodeId
     *
     * @return le facteur par lequel la longueur de l'arête d'identité edgeId doit être multipliée
     *          (ce facteur est supérieur ou égal à 1)
     */
    abstract double costFactor(int nodeId, int edgeId);
}
