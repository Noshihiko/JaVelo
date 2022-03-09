package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Représente le tableau contenant les 16384 secteurs de JaVelo
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 *
 * @param buffer
 *         Mémoire tampon contenant la valeur des attributs de la totalité des secteurs
 */
public record GraphSectors(ByteBuffer buffer) {
    public static final int nbrSector = 128;

    /**
     * Représente un secteur
     *
     * @param startNodeId
     *        L'identité (index) du premier nœud du secteur
     * @param endNodeId
     *        L'identité (index) du nœud situé juste après le dernier nœud du secteur
     *
     * @return un secteur
     */
    public record Sector(int startNodeId, int endNodeId) {
    }

    /**
     * Cherche tous les secteurs contenus dans le carré centré en un point
     *
     * @param center
     *        Le point donné
     * @param distance
     *        La distance entre le point et un des côtés du carré
     *
     * @return la liste de tous les secteurs ayant une intersection avec le carré centré au point donné
     */
    public List<Sector> sectorsInArea(PointCh center, double distance) {
        //liste de sectors intersect dans le carré
        List<Sector> sectorsIntersect = new ArrayList<>();

        double xSector = (SwissBounds.MAX_E - SwissBounds.MIN_E)/nbrSector;
        double ySector = (SwissBounds.MAX_N - SwissBounds.MIN_N)/nbrSector;

        double xBordGauche = center.e() - distance;
        double xBordDroit = center.e() + distance;
        double yBordBas = center.n() - distance;
        double yBordHaut = center.n() - distance;

        int xMin = (int)((xBordGauche - SwissBounds.MIN_E)/xSector);
        int xMax = (int) ((xBordDroit - SwissBounds.MIN_E)/xSector);
        int yMin = (int) ((yBordBas - SwissBounds.MIN_N)/ySector);
        int yMax = (int) ((yBordHaut - SwissBounds.MIN_N)/ySector);

        for (int i = yMin; i<=yMax; ++i){
            for (int j = xMin; j<= xMax; ++j){
                int secteur = j*nbrSector +i;
                byte secteurBit = buffer.get(secteur);
                // +1 ou +6?
                sectorsIntersect.add(new Sector(buffer.getInt(secteur),buffer.getInt(secteur+6)));
            }
        }
        return sectorsIntersect;
    }
}




