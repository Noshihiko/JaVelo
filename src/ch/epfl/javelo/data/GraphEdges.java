package ch.epfl.javelo.data;

import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.List;

import static ch.epfl.javelo.Bits.extractSigned;
import static ch.epfl.javelo.Bits.extractUnsigned;
import static ch.epfl.javelo.Math2.ceilDiv;
import static ch.epfl.javelo.Q28_4.asDouble;
import static ch.epfl.javelo.Q28_4.asFloat;

/**
 * Permet de recuperer les informations concernant les arretes
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 *
 * @param edgesBuffer
 *              memoire tampon contenant la valeur des attributs figurant dans la première table
 * @param profileIds
 *              la mémoire tampon contenant la valeur des attributs figurant dans la seconde table
 * @param elevations
 *              la mémoire tampon contenant la totalité des échantillons des profils, compressés ou non
 */

public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {
    private static final int OFFSET_TARGET_NODE_ID =0;
    private static final int OFFSET_LENGTH = OFFSET_TARGET_NODE_ID + Integer.BYTES;
    private static final int OFFSET_ELEVATION = OFFSET_LENGTH + Short.BYTES;
    private static final int OFFSET_IDENTITY = OFFSET_ELEVATION + Short.BYTES;
    private static final int OFFSET_BYTES_PER_EDGE = OFFSET_IDENTITY + Short.BYTES;

    /**
     * Enumeration privé representant les differents types de profils
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

    public boolean isInverted(int edgeId) {
        int index = edgeId*OFFSET_BYTES_PER_EDGE + OFFSET_TARGET_NODE_ID;
        return (edgesBuffer.getInt(index) < 0);
    }

    /**
     * Retourne l'identité du noeud destination de l'arrete
     *
     * @param edgeId : identite de l'arrete
     *
     * @return l'identité du noeud destination de l'arrete
     */

    public int targetNodeId(int edgeId) {
        int index = edgeId*OFFSET_BYTES_PER_EDGE + OFFSET_TARGET_NODE_ID;
        int nod_identity = edgesBuffer.getInt(index);
        return nod_identity>=0 ? nod_identity : ~nod_identity;
    }

    /**
     * Retourne la longueur de l'arrete passè en parametre
     *
     * @param edgeId : identite de l'arrete
     *
     * @return la longueur de l'arrete en mètres
     */

    public double length(int edgeId) {
        int index = edgeId*OFFSET_BYTES_PER_EDGE + OFFSET_LENGTH;
        return asDouble(Short.toUnsignedInt(edgesBuffer.getShort(index)));
    }

    /**
     * Retourne le denivelé positif de l'arrete en mètres
     *
     * @param edgeId : identite de l'arrete
     *
     * @return le denivelé positif de l'arrete en mètres
     */

    public double elevationGain(int edgeId) {
        int index = edgeId*OFFSET_BYTES_PER_EDGE + OFFSET_ELEVATION;
        return asDouble(Short.toUnsignedInt(edgesBuffer.getShort(index)));
    }

    /**
     * Indique si l'arrete possede un profil
     *
     * @param edgeId : identite de l'arrete
     *
     * @return vrai si l'arrete possède un profil
     */

    public boolean hasProfile(int edgeId) {
        int profileType = extractUnsigned(profileIds.get(edgeId), 30,2);
        return (Types.ALL_types.get(profileType) != Types.INEXISTANT);
    }

    /**
     * Retourne le tableau des échantillons du profil de l'arête
     *
     * @param edgeId : identite de l'arrete
     *
     * @return le tableau des échantillons du profil de l'arête d'identité donnée
     */

    public float[] profileSamples(int edgeId) {
        int numberSamples = 1 + ceilDiv(Short.toUnsignedInt(edgesBuffer.getShort(edgeId*OFFSET_BYTES_PER_EDGE + OFFSET_LENGTH)), Q28_4.ofInt(2));
        int index = extractUnsigned(profileIds.get(edgeId), 0,30);

        float ProfileSamples[] = new float[numberSamples];

        boolean isInverted =(edgesBuffer.getInt(edgeId*OFFSET_BYTES_PER_EDGE + OFFSET_TARGET_NODE_ID) < 0);


        switch (Types.ALL_types.get(extractUnsigned(profileIds.get(edgeId), 30, 2))) {
            case INEXISTANT -> {
                return new float[0];
            }

            case NOT_COMPRESSED -> {
                for (int i = 0; i < numberSamples; ++i) {
                    ProfileSamples[i] = asFloat(elevations.get(index + i));
                }
                break;

            }
            case COMPRESSED_IN_Q4_4 -> {
                ProfileSamples[0] = asFloat((elevations.get(index)));
                for (int i = 0; i < numberSamples-1; ++i) {
                    ProfileSamples[i+1] = ProfileSamples[i] + asFloat(extractSigned(elevations.get(index + 1 +i/2), 8-(i%2)*8, 8));
                    }
            break;
            }

            case COMPRESSED_IN_Q0_4 -> {
                ProfileSamples[0] = asFloat((elevations.get(index)));
                for (int i = 0; i < numberSamples-1; ++i) {
                    ProfileSamples[i + 1] = ProfileSamples[i] + asFloat(extractSigned(elevations.get(index + 1 +(i / 4)), 12-(i % 4)*4, 4));
                    }
                break;
            }

        }
        if (isInverted) {
            for (int i=0; i< Math.floor(ProfileSamples.length/2); ++i) {
                float temp = ProfileSamples[i];
                ProfileSamples[i] = ProfileSamples[ProfileSamples.length-i-1];
                ProfileSamples[ProfileSamples.length-i-1] = temp;
            }
        }
        return ProfileSamples;

    }

    /**
     * Retourne l'identité de l'ensemble d'attributs attaché à l'arrête
     *
     * @param edgeId : identite de l'arrete
     *
     * @return l'identité de l'ensemble d'attributs attaché à l'arrête
     */

    public int attributesIndex(int edgeId) {
        int index = edgeId*OFFSET_BYTES_PER_EDGE + OFFSET_IDENTITY;
        return Short.toUnsignedInt(edgesBuffer.getShort(index));

    }
}
