package ch.epfl.javelo.data;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.List;

import static ch.epfl.javelo.Bits.extractUnsigned;
import static ch.epfl.javelo.Q28_4.asDouble;
import static ch.epfl.javelo.Q28_4.asFloat;

/**
 * Permet de recuperer les informations concernant les arretes
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 *
 */

public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {
    private static final int OFFSET_TARGET_NODE_ID =0;
    private static final int OFFSET_LENGTH = OFFSET_TARGET_NODE_ID + Integer.BYTES;
    private static final int OFFSET_ELEVATION = OFFSET_LENGTH + Short.BYTES;
    private static final int OFFSET_IDENTITY = OFFSET_ELEVATION + Short.BYTES;
    private static final int OFFSET_BYTES_PER_EDGE = OFFSET_IDENTITY + Short.BYTES;

    /**
     *
     */

    private enum Types {
        INEXISTANT, NOT_COMPRESSED, COMPRESSED_IN_Q4_4, COMPRESSED_IN_Q0_4;

        private static final List<Types> ALL_types = List.of(values());
        //private static final int COUNT = ALL_types.size();
    }

    /**
     * Retourne vrai ou faux en fonction du sens de l'arrete
     *
     * @param edgeId : identité de l'arrete donnèe en paramètre
     *
     * @return vrai si l'arrete va dans le sens inverse du noeud OSM dont elle provient
     */

    boolean isInverted(int edgeId) {
        int INDEX = edgeId*OFFSET_BYTES_PER_EDGE + OFFSET_TARGET_NODE_ID;
        return (edgesBuffer.getInt(INDEX) < 0);
    }

    /**
     * Retourne l'identité du noeud destination de l'arrete
     *
     * @param edgeId : identite de l'arrete
     *
     * @return l'identité du noeud destination de l'arrete
     */

    int targetNodeId(int edgeId) {
        int INDEX = edgeId*OFFSET_BYTES_PER_EDGE + OFFSET_TARGET_NODE_ID;
        int nod_identity = edgesBuffer.getInt(INDEX);
        return nod_identity>=0 ? nod_identity : ~nod_identity;
    }

    /**
     * Retourne la longueur de l'arrete passè en parametre
     *
     * @param edgeId : identite de l'arrete
     *
     * @return la longueur de l'arrete en mètres
     */

    double length(int edgeId) {
        int INDEX = edgeId*OFFSET_BYTES_PER_EDGE + OFFSET_LENGTH;
        return asDouble(Short.toUnsignedInt(edgesBuffer.getShort(INDEX)));
    }

    /**
     * Retourne le denivelé positif de l'arrete en mètres
     *
     * @param edgeId : identite de l'arrete
     *
     * @return le denivelé positif de l'arrete en mètres
     */

    double elevationGain(int edgeId) {
        int INDEX = edgeId*OFFSET_BYTES_PER_EDGE + OFFSET_ELEVATION;
        return asDouble(Short.toUnsignedInt(edgesBuffer.getShort(INDEX)));
    }

    /**
     * Indique si l'arrete possede un profil
     *
     * @param edgeId : identite de l'arrete
     *
     * @return vrai si l'arrete possède un profil
     */

    boolean hasProfile(int edgeId) {
        int profiletype = extractUnsigned(profileIds.get(edgeId), 30,2);
        return (Types.ALL_types.get(profiletype) != Types.INEXISTANT);
    }

    float[] profileSamples(int edgeId) {
        int numberSamples = (int) (1+ Math.ceil(length(edgeId)/2));
        int INDEX = extractUnsigned(profileIds.get(edgeId), 0,30);
        float [] ProfileSamples = {};
        int start = 0;

        switch (Types.ALL_types.get(extractUnsigned(profileIds.get(edgeId), 30,2))) {
            case INEXISTANT: return ProfileSamples;

            case NOT_COMPRESSED:
                for (int i=0; i<numberSamples; ++i) {
                    ProfileSamples[i] = asFloat(Short.toUnsignedInt(elevations.get(INDEX+i))); }
                return ProfileSamples;

            case COMPRESSED_IN_Q4_4:
                ProfileSamples[0] = asFloat(Short.toUnsignedInt(elevations.get(INDEX)));
                for (int i=1; i<numberSamples; ++i) {
                    start += 16;
                    int m = extractUnsigned(elevations.get(INDEX), start, 8);
                    ProfileSamples[i] = asFloat()
            }
        }


    }

    /**
     * Retourne l'identité de l'ensemble d'attributs attaché à l'arrête
     *
     * @param edgeId : identite de l'arrete
     *
     * @return l'identité de l'ensemble d'attributs attaché à l'arrête
     */

    int attributesIndex(int edgeId) {
        int INDEX = edgeId*OFFSET_BYTES_PER_EDGE + OFFSET_IDENTITY;
        return Short.toUnsignedInt(edgesBuffer.getShort(INDEX));

    }
}
