package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.*;

/**
 * Retourne un itinéraire le plus court et plus adapté pour les vélos
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */

public final class RouteComputer {
    private final Graph graph;
    private final CostFunction costFunction;

    /**
     * Constructeur public de RouteComputer
     *
     * @param graph        graph dans lequel on va chercher le parcours
     * @param costFunction fonction attribuant un cout particulier aux arêtes en fonction de critères spécifiques
     */

    public RouteComputer(Graph graph, CostFunction costFunction) {
        this.graph = graph;
        this.costFunction = costFunction;

    }

    /**
     * Trouve le chemin le plus court entre deux nœuds dans le graph
     *
     * @param startNodeId nœud de depart de l'itinéraire
     * @param endNodeId   nœud d'arrivée de l'itinéraire
     * @return une singleRoute représentant l'itinéraire le plus court et optimisé entre les deux nœuds
     * @throws IllegalArgumentException si le nœud d'arrivée est égal au nœud de départ
     */


    public Route bestRouteBetween(int startNodeId, int endNodeId) {

        /**
         * Crée un enregistrement contenant un nœud et sa distance
         *
         * @param nodeId   nœud associé à la distance
         * @param distance distance au nœud
         * @return un enregistrement d'un nœud et sa distance associée
         */

        record WeightedNode(int nodeId, float distance)
                implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }

        Preconditions.checkArgument(startNodeId != endNodeId);

        int nbrNodes = graph.nodeCount();
        PointCh endPointCh = graph.nodePoint(endNodeId);
        PriorityQueue<WeightedNode> enExploration = new PriorityQueue<>();
        float[] distance = new float[nbrNodes];
        int[] predecessor = new int[nbrNodes];

        Arrays.fill(distance, Float.POSITIVE_INFINITY);

        distance[startNodeId] = 0;
        enExploration.add(new WeightedNode(startNodeId, distance[startNodeId]));
        int actualNodeExploredId = startNodeId;

        //********************* trouve noeud à explorer ******************

        while (actualNodeExploredId != endNodeId) {
            //*************** mis à jour tableau exploration : quel nœud il reste à explorer ***********
            for (int i = 0; i < graph.nodeOutDegree(actualNodeExploredId); ++i) {

                int edgeId = graph.nodeOutEdgeId(actualNodeExploredId, i);
                int ToNodeId = graph.edgeTargetNodeId(edgeId);

                if (distance[ToNodeId] != Float.NEGATIVE_INFINITY) {

                    PointCh nextPointCh = graph.nodePoint(ToNodeId);
                    float d = (float) ((costFunction.costFactor(actualNodeExploredId, edgeId) * graph.edgeLength(edgeId))
                            + distance[actualNodeExploredId]);

                    if (d < distance[ToNodeId]) {

                        distance[ToNodeId] = d;
                        predecessor[ToNodeId] = actualNodeExploredId;
                        enExploration.add(new WeightedNode(ToNodeId, distance[ToNodeId]
                                + (float) nextPointCh.distanceTo(endPointCh)));
                    }
                }
            }
            distance[actualNodeExploredId] = Float.NEGATIVE_INFINITY;

            do {
                if (enExploration.isEmpty()) return null;

                else actualNodeExploredId = enExploration.remove().nodeId;

            } while (distance[actualNodeExploredId] == Float.NEGATIVE_INFINITY);
        }
        return creationRoute(startNodeId, endNodeId, predecessor);
    }

    /**
     * Reconstitue une route correspondant à l'itinéraire le plus court et optimisé entre deux nœuds
     *
     * @param startNodeId noeud de depart
     * @param endNodeId nœud d'arrivée
     * @param predecessor tableau de prédécesseurs pour chaque nœud
     *
     * @return une single route correspondant à l'itinéraire le plus court et optimisé entre les deux nœuds
     */

    private Route creationRoute(int startNodeId, int endNodeId, int[] predecessor) {
        int actualNodeId = endNodeId;
        List<Edge> edges = new ArrayList<>();

        while (actualNodeId != startNodeId) {

            int prevNodeId = predecessor[actualNodeId];
            for (int i = 0; i < graph.nodeOutDegree(prevNodeId); ++i) {

                int edgeId = graph.nodeOutEdgeId(prevNodeId, i);
                int toNodeId = graph.edgeTargetNodeId(edgeId);

                if (toNodeId == actualNodeId) {
                    edges.add(Edge.of(graph, edgeId, prevNodeId, actualNodeId));
                    break;
                }
            }
            actualNodeId = prevNodeId;
        }
        Collections.reverse(edges);

        return new SingleRoute(edges);
    }


}