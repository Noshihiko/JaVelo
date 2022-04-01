package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.data.GraphEdges;
import ch.epfl.javelo.data.GraphNodes;

import java.util.*;

//ajouter un final ?
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
        PriorityQueue<WeightedNode> en_exploration = new PriorityQueue<>();
        float[] distance = new float[nbrNodes];
        int[] predecesseur = new int[nbrNodes];
        Arrays.fill(distance, Float.POSITIVE_INFINITY);
        Arrays.fill(predecesseur, 0);
        distance[startNodeId] = 0;
        en_exploration.add(new WeightedNode(startNodeId, distance[startNodeId]));

        //********************* trouve noeud à explorer ******************
        while (!en_exploration.isEmpty()) {
            WeightedNode actualNodeExplored = en_exploration.remove();

            //************************+ remplissage tableau route la plus courte **************
            if (actualNodeExplored.nodeId == endNodeId) {
                int actualNodeId = endNodeId;
                List<Edge> edges = new ArrayList<>();

                while (predecesseur[actualNodeId] != startNodeId) {
                    for (int i = 0; i < graph.nodeOutDegree(actualNodeId); ++i) {
                        int ToNodeId = graph.edgeTargetNodeId(graph.nodeOutEdgeId(actualNodeId, i));
                        if (ToNodeId == predecesseur[actualNodeId]) {
                            edges.add(Edge.of(graph, graph.nodeOutEdgeId(actualNodeId, i), actualNodeId, ToNodeId));
                        }
                    }
                    actualNodeId = predecesseur[actualNodeId];
                }
                return new SingleRoute(edges);

            }
            //*************** mise a jour tblo exploration : quel noeud il reste a explorer ***********
            for (int i = 0; i < graph.nodeOutDegree(actualNodeExplored.nodeId); ++i) {
                int ToNodeId = graph.edgeTargetNodeId(graph.nodeOutEdgeId(actualNodeExplored.nodeId, i));
                float d = (float) ((costFunction.costFactor(actualNodeExplored.nodeId, graph.nodeOutEdgeId(actualNodeExplored.nodeId, i)) * graph.edgeLength(graph.nodeOutEdgeId(actualNodeExplored.nodeId, i)) + distance[actualNodeExplored.nodeId]));
                if (d < distance[ToNodeId]) {
                    distance[ToNodeId] = d;
                    predecesseur[ToNodeId] = actualNodeExplored.nodeId;
                    en_exploration.add(new WeightedNode(ToNodeId, distance[ToNodeId]));
                }
            }
        }
        return null;
    }

}
