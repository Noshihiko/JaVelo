package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.*;

/**
 * Représente le profil en long d'un itinéraire simple ou multiple
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */

public class RouteComputer {
    private Graph graph;
    private CostFunction costFunction;

    public RouteComputer(Graph graph, CostFunction costFunction) {
        this.graph = graph;
        this.costFunction = costFunction;

    }

    record WeightedNode(int nodeId, float distance)
            implements Comparable<WeightedNode> {
        @Override
        public int compareTo(WeightedNode that) {
            return Float.compare(this.distance, that.distance);
        }
    }


    public Route bestRouteBetween(int startNodeId, int endNodeId) {
        Preconditions.checkArgument(startNodeId != endNodeId);
        int nbrNodes = graph.nodeCount();
        PointCh endPointCh = graph.nodePoint(endNodeId);
        PriorityQueue<WeightedNode> en_exploration = new PriorityQueue<>();
        float[] distance = new float[nbrNodes];
        int[] predecesseur = new int[nbrNodes];

        Arrays.fill(distance, Float.POSITIVE_INFINITY);
        Arrays.fill(predecesseur, 0);
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
                        predecesseur[ToNodeId] = actualNodeExploredId;
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
        //************************+ remplissage tableau route la plus courte **************
        int actualNodeId = endNodeId;
        List<Edge> edges = new ArrayList<>();

        //******
        while (actualNodeId != startNodeId) {
            int prevNodeId = predecesseur[actualNodeId];
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
        //********
    }
}