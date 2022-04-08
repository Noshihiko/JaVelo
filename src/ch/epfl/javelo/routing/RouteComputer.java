package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.*;

/**
 * Retourne un itineraire le plus court et plus adapté pour les vélos
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */

public class RouteComputer {
    private Graph graph;
    private CostFunction costFunction;

    /**
     * Constructeur publique de RouteComputer
     *
     * @param graph        graph dans lequel on va chercher le parcours
     * @param costFunction fonction attribuant un cout particulier aux aretes en fonctions de criteres specifiques
     */

    public RouteComputer(Graph graph, CostFunction costFunction) {
        this.graph = graph;
        this.costFunction = costFunction;

    }

    /**
     * Trouve le chemin le plus court entre deux noeuds dans le graph
     *
     * @param startNodeId noeud de depart de l'itineraire
     * @param endNodeId   noeud d'arrivè de l'itineraire
     * @return une single route representant l'itineraire le plus court et optimisé entre les deux noeuds
     * @throws IllegalArgumentException si le noeud d'arrivé est egal au  noeud de départ
     */


    public Route bestRouteBetween(int startNodeId, int endNodeId) {

        /**
         * Cree un enregistrement contenant un noeud et sa distance
         *
         * @param nodeId   noeud associé à la distance
         * @param distance distance au noeud
         * @return un enregistrement d'un noeud et sa distance associée
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
        PriorityQueue<WeightedNode> en_exploration = new PriorityQueue<>();
        float[] distance = new float[nbrNodes];
        int[] predecessor = new int[nbrNodes];

        Arrays.fill(distance, Float.POSITIVE_INFINITY);
        Arrays.fill(predecessor, 0);

        distance[startNodeId] = 0;
        en_exploration.add(new WeightedNode(startNodeId, distance[startNodeId]));
        int actualNodeExploredId = startNodeId;

        //********************* trouve noeud à explorer ******************

        while (actualNodeExploredId != endNodeId) {
            //*************** mise a jour tblo exploration : quel noeud il reste a explorer ***********
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
                        en_exploration.add(new WeightedNode(ToNodeId, distance[ToNodeId]
                                + (float) nextPointCh.distanceTo(endPointCh)));
                    }
                }
            }
            distance[actualNodeExploredId] = Float.NEGATIVE_INFINITY;

            do {
                if (en_exploration.isEmpty()) return null;

                else actualNodeExploredId = en_exploration.remove().nodeId;

            } while (distance[actualNodeExploredId] == Float.NEGATIVE_INFINITY);
        }
        return creationRoute(startNodeId, endNodeId, predecessor);
    }

    /**
     * Reconstitue une route correspondant à l'itineraire le plus court et optimisé entre deux noeuds
     *
     * @param startNodeId noeud de depart
     * @param endNodeId noeud d'arrivée
     * @param predecessor tableau de predecesseurs pour chaque noeud
     *
     * @return une single route correspondant à l'itineraire le plus court et optimisé entre les deux noeuds
     */

    private Route creationRoute(int startNodeId, int endNodeId, int[] predecessor) {
        int actualNodeId = endNodeId;
        List<Edge> edges = new ArrayList<>();

        while (actualNodeId != startNodeId) {

            int prevNodeId = predecessor[actualNodeId];
            for (int i = 0; i < graph.nodeOutDegree(prevNodeId); ++i) {

                int EdgeId = graph.nodeOutEdgeId(prevNodeId, i);
                int ToNodeId = graph.edgeTargetNodeId(EdgeId);

                if (ToNodeId == actualNodeId) {
                    edges.add(Edge.of(graph, EdgeId, prevNodeId, actualNodeId));
                    break;
                }
            }
            actualNodeId = prevNodeId;
        }
        Collections.reverse(edges);

        return new SingleRoute(edges);
    }


}