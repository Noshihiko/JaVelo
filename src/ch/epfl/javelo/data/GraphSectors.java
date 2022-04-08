package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.nio.ByteBuffer;
import java.util.*;

import static ch.epfl.javelo.Math2.clamp;

/**
 * Représente le tableau contenant les 16384 secteurs de JaVelo.
 *
 * @param buffer Mémoire tampon contenant la valeur des attributs de la totalité des secteurs
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */
public record GraphSectors(ByteBuffer buffer) {
    private final static int NUMBER_SECTOR = 128;
    private final static int FIRST_NODE = 4;
    private final static int NUMBER_NODES = 2;
    private final static int LENGTH_SECTOR = FIRST_NODE + NUMBER_NODES;


    /**
     * Enregistrement imbriqué qui représente un secteur
     *
     * @param startNodeId L'identité (index) du premier nœud du secteur
     * @param endNodeId   L'identité (index) du nœud situé juste après le dernier nœud du secteur
     */
    public record Sector(int startNodeId, int endNodeId) {
    }

    /**
     * Cherche tous les secteurs contenus dans le carré centré en un point.
     *
     * @param center   Le point donné
     * @param distance La distance entre le point et un des côtés du carré
     *
     * @return la liste de tous les secteurs ayant une intersection avec le carré centré au point donné.
     */
    public List<Sector> sectorsInArea(PointCh center, double distance) {
        int secteur = 0;

        //liste des sectors contenus dans le carré
        List<Sector> sectorsIntersect = new ArrayList<>();

        //calcule la largeur et la hauteur d'un secteur (ils font tous la même taille)
        double xSector = (SwissBounds.MAX_E - SwissBounds.MIN_E) / NUMBER_SECTOR;
        double ySector = (SwissBounds.MAX_N - SwissBounds.MIN_N) / NUMBER_SECTOR;

        //calcule carré centré au PointCh center
        double xBordGauche = clamp(SwissBounds.MIN_E, (center.e() - distance), SwissBounds.MAX_E);
        double xBordDroit = clamp(SwissBounds.MIN_E, (center.e() + distance), SwissBounds.MAX_E);
        double yBordBas = clamp(SwissBounds.MIN_N, (center.n() - distance), SwissBounds.MAX_N);
        double yBordHaut = clamp(SwissBounds.MIN_N, (center.n() + distance), SwissBounds.MAX_N);

        //numéro des secteurs entre 0 et 127
        int xMin = clamp(0, (int) ((xBordGauche - SwissBounds.MIN_E) / xSector), NUMBER_SECTOR-1);
        int xMax = clamp(0, (int) ((xBordDroit - SwissBounds.MIN_E) / xSector), NUMBER_SECTOR-1);
        int yMin = clamp(0, (int) ((yBordBas - SwissBounds.MIN_N) / ySector), NUMBER_SECTOR-1);
        int yMax = clamp(0, (int) ((yBordHaut - SwissBounds.MIN_N) / ySector), NUMBER_SECTOR-1);

        //boucle pour chercher les sectors en xMin, xMax, yMin et yMax contenus dans le carré créé plus haut
        for (int i = yMin; i <= yMax; ++i) {
            for (int j = xMin; j <= xMax; ++j) {
                secteur = i * NUMBER_SECTOR + j;
                sectorsIntersect.add(new Sector(buffer.getInt(secteur * LENGTH_SECTOR),
                        buffer.getInt(secteur * LENGTH_SECTOR) + Short.toUnsignedInt(buffer.getShort(secteur * LENGTH_SECTOR + FIRST_NODE))));
            }
        }
        return sectorsIntersect;
    }
}