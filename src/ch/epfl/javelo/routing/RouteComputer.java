package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.data.GraphEdges;
import ch.epfl.javelo.data.GraphNodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

//ajouter un final ?
public class RouteComputer {
    private Graph graph;
    private CostFunction costFunction;

    public RouteComputer(Graph graph, CostFunction costFunction) {
        this.graph = graph;
        this.costFunction = costFunction;

    }

    Route bestRouteBetween(int startNodeId, int endNodeId) {
        Preconditions.checkArgument(startNodeId != endNodeId);
        int nbrNodes = graph.nodeCount();
        HashSet<Integer> en_exploration = new HashSet<>();
        en_exploration.add(startNodeId);
        float[] distance = new float[nbrNodes];
        int[] predecesseur = new int[nbrNodes];
        Arrays.fill(distance, Float.POSITIVE_INFINITY);
        Arrays.fill(predecesseur, 0);
        distance[startNodeId] = 0;

        //********************* trouve noeud à explorer ******************
        while (!en_exploration.isEmpty()) {
            float min = Float.POSITIVE_INFINITY;
            int actualNodeExploredId = -1;
            for (Integer N : en_exploration) {
                if (distance[N] < min) {
                    min = distance[N];
                    actualNodeExploredId = N;
                }
            }
            en_exploration.remove(actualNodeExploredId);
            //************************+ remplissage tableau route la plus courte **************
            if (actualNodeExploredId == endNodeId) {
                int nId = actualNodeExploredId;
                List<Edge> edges = new ArrayList<>();

                while (predecesseur[nId] != startNodeId ) {
                    for (int i = 0; i < graph.nodeOutDegree(actualNodeExploredId); ++i) {
                        int ToNodeId = graph.edgeTargetNodeId(graph.nodeOutEdgeId(actualNodeExploredId, i));
                        if (ToNodeId == predecesseur[nId]) {
                            edges.add(Edge.of(graph, graph.nodeOutEdgeId(actualNodeExploredId, i), nId, ToNodeId));
                        }
                    }
                    nId = predecesseur[nId];
                }
                return new SingleRoute(edges);

            }
            //*************** mise a jour tblo exploration : quel noeud il reste a explorer ***********
            for (int i = 0; i < graph.nodeOutDegree(actualNodeExploredId); ++i) {
                int ToNodeId = graph.edgeTargetNodeId(graph.nodeOutEdgeId(actualNodeExploredId, i));
                float d = (float) ((costFunction.costFactor(actualNodeExploredId, graph.nodeOutEdgeId(actualNodeExploredId, i)) + distance[actualNodeExploredId]) * graph.edgeLength(graph.nodeOutEdgeId(actualNodeExploredId, i)));

                if (d < distance[ToNodeId]) {
                    distance[ToNodeId] = d;
                    predecesseur[ToNodeId] = actualNodeExploredId;
                    en_exploration.add(ToNodeId);
                }

            }

        }
        return null;
    }

}
