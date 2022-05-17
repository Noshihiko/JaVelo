package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;

import java.io.*;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.*;
import java.util.function.DoubleUnaryOperator;

/**
 * Représente le graphe JaVelo.
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */
public final class Graph {
    private static final int OFFSET_NODE_CLOSEST = -1;

    private final GraphNodes nodes;
    private final GraphSectors sectors;
    private final GraphEdges edges;
    private final List<AttributeSet> attributeSets;

    /**
     * Constructeur de Graph
     *
     * @param nodes         Représente le tableau de tous les nœuds du graphe JaVelo
     * @param sectors       Représente le tableau contenant les 16384 secteurs de JaVelo
     * @param edges         Permet de récupérer les informations concernant les arêtes
     * @param attributeSets Représente un ensemble d'attributs OpenStreetMap
     */
    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets) {
        this.nodes = nodes;
        this.sectors = sectors;
        this.edges = edges;
        this.attributeSets = List.copyOf(attributeSets);
    }

    /**
     * Permet de créer un ByteBuffer que l'on convertit et qu'on utilise comme attribut pour créer nos différents objets.
     *
     * @param basePath chemin pour accéder aux fichiers dans le répertoire
     * @param nameFile nom du fichier dont on cherche à récupérer le contenu
     * @throws IOException en cas d'erreur d'entrée/sortie, p. ex. si l'un des fichiers attendus n'existe pas
     *
     * @return un ByteBuffer utilisé dans loadFrom.
     */
    private static ByteBuffer bufferFile(Path basePath, String nameFile) throws IOException {
        ByteBuffer buffer;
        try (FileChannel channel = FileChannel.open(basePath.resolve(nameFile))) {
            buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        }
        return buffer;
    }

    /**
     * Donne le graphe JaVelo obtenu à partir des fichiers dans "lausanne".
     *
     * @param basePath chemin pour accéder aux fichiers dans le répertoire
     * @throws IOException en cas d'erreur d'entrée/sortie, par exemple, si l'un des fichiers attendus n'existe pas
     *
     * @return le graphe JaVelo obtenu à partir des fichiers se trouvant dans le répertoire dont le chemin d'accès est basePath.
     */

    public static Graph loadFrom(Path basePath) throws IOException {
        //création des différents attributs à l'aide des fichiers contenus dans "lausanne"
        GraphNodes nodes = new GraphNodes((bufferFile(basePath, "nodes.bin")).asIntBuffer());
        GraphEdges edges = new GraphEdges(bufferFile(basePath, "edges.bin"),
                bufferFile(basePath, "profile_ids.bin").asIntBuffer(),
                bufferFile(basePath, "elevations.bin").asShortBuffer());
        GraphSectors sectors = new GraphSectors(bufferFile(basePath, "sectors.bin"));

        List<AttributeSet> attributes = new ArrayList<>();
        LongBuffer a = bufferFile(basePath, "attributes.bin").asLongBuffer();

        //boucle pour récupérer tous les AttributeSet contenus dans le LongBuffer a
        for (int i = 0; i < a.capacity(); i++) {
            attributes.add(new AttributeSet(a.get(i)));
        }

        return new Graph(nodes, sectors, edges, attributes);
    }

    /**
     * Donne le nombre total de nœuds dans le graphe.
     * @return le nombre total de nœuds dans le graphe.
     */

    public int nodeCount() {
        return nodes.count();
    }

    /**
     * Donne la position du nœud d'identité donnée.
     *
     * @param nodeId identité du nœud dont on cherche la position
     *
     * @return la position du nœud d'identité donnée.
     */

    public PointCh nodePoint(int nodeId) {
        return new PointCh(nodes.nodeE(nodeId), nodes.nodeN(nodeId));
    }

    /**
     * Donne le nombre d'arêtes sortant du nœud d'identité donnée.
     *
     * @param nodeId identité du nœud dont on cherche le nombre d'arêtes
     *
     * @return le nombre d'arêtes sortant du nœud d'identité donnée.
     */

    public int nodeOutDegree(int nodeId) {
        return nodes.outDegree(nodeId);
    }

    /**
     * Donne l'identité de l'edgeIndex-ième arête sortant du nœud d'identité nodeId.
     *
     * @param nodeId    identité du nœud
     * @param edgeIndex index de l'arête sortant du nœud d'identité nodeId
     *
     * @return l'identité de l'edgeIndex-ième arête sortant du nœud d'identité nodeId.
     */

    public int nodeOutEdgeId(int nodeId, int edgeIndex) {
        return nodes.edgeId(nodeId, edgeIndex);
    }

    /**
     * Donne l'identité du nœud se trouvant le plus proche du point donné, à la distance maximale donnée (en mètres).
     *
     * @param point          point donné dont on cherche le nœud le plus proche selon la distance searchDistance
     * @param searchDistance distance maximale donnée pour chercher un nœud proche du point donné
     *
     * @return l'identité du nœud se trouvant le plus proche du point donné, à la distance maximale donnée (en mètres).
     */
    public int nodeClosestTo(PointCh point, double searchDistance) {
        int nodeId = OFFSET_NODE_CLOSEST;
        double minDistance = searchDistance * searchDistance;
        double distance;
        //Réduction de la zone de recherche
        List<GraphSectors.Sector> sectorsClosePoint = sectors.sectorsInArea(point, searchDistance);

        for (GraphSectors.Sector sector : sectorsClosePoint) {
            //Comparaison de tous les nœuds contenus dans cette zone réduite de sectors
            for (int i = sector.startNodeId(); i < sector.endNodeId(); ++i) {
                distance = nodePoint(i).squaredDistanceTo(point);
                if (distance <= minDistance) {
                    nodeId = i;
                    minDistance = distance;
                }
            }
        }
        return nodeId;
    }

    /**
     * Donne l'identité du nœud destination de l'arête d'identité donnée.
     *
     * @param edgeId identité de l'arête dont on cherche le nœud correspondant
     *
     * @return l'identité du nœud destination de l'arête d'identité donnée.
     */
    public int edgeTargetNodeId(int edgeId) {
        return edges.targetNodeId(edgeId);
    }

    /**
     * Vérifie si l'arête d'identité donnée va dans le sens contraire de la voie OSM dont elle provient.
     *
     * @param edgeId identité de l'arête que l'on vérifie
     *
     * @return true ssi l'arête d'identité donnée va dans le sens contraire de la voie OSM dont elle provient.
     */
    public boolean edgeIsInverted(int edgeId) {
        return edges.isInverted(edgeId);
    }

    /**
     * Donne l'ensemble des attributs OSM attachés à l'arête d'identité donnée.
     *
     * @param edgeId identité de l'arête étudiée
     *
     * @return l'ensemble des attributs OSM attachés à l'arête d'identité donnée.
     */
    public AttributeSet edgeAttributes(int edgeId) {
        return attributeSets.get(edges.attributesIndex(edgeId));
    }

    /**
     * Donne la longueur, en mètres, de l'arête d'identité donnée.
     *
     * @param edgeId identité de l'arête étudiée
     *
     * @return la longueur, en mètres, de l'arête d'identité donnée.
     */
    public double edgeLength(int edgeId) {
        return edges.length(edgeId);
    }

    /**
     * Donne le dénivelé positif total de l'arête d'identité donnée.
     *
     * @param edgeId identité de l'arête étudiée
     *
     * @return le dénivelé positif total de l'arête d'identité donnée.
     */
    public double edgeElevationGain(int edgeId) {
        return edges.elevationGain(edgeId);
    }

    /**
     * Donne une fonction possédant soit le profil en long de l'arête d'identité donnée, soit NaN.
     *
     * @param edgeId l'identité de l'arête étudiée
     *
     * @return le profil en long de l'arête d'identité donnée, sous la forme d'une fonction,
     * si l'arête ne possède pas de profil, une fonction constante égale à NaN.
     */
    public DoubleUnaryOperator edgeProfile(int edgeId) {
        return edges.hasProfile(edgeId) ? Functions.sampled(edges.profileSamples(edgeId), edgeLength(edgeId)) : Functions.constant(Double.NaN);
    }
}