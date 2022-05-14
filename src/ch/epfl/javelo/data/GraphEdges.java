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
 * Permet de récupérer les informations concernant les arêtes.
 *
 * @param edgesBuffer mémoire tampon contenant la valeur des attributs figurant dans la première table
 * @param profileIds  mémoire tampon contenant la valeur des attributs figurant dans la seconde table
 * @param elevations  mémoire tampon contenant la totalité des échantillons des profils, compressés ou non
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */

public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {
    private static final int OFFSET_TARGET_NODE_ID = 0;
    private static final int OFFSET_LENGTH = OFFSET_TARGET_NODE_ID + Integer.BYTES;
    private static final int OFFSET_ELEVATION = OFFSET_LENGTH + Short.BYTES;
    private static final int OFFSET_IDENTITY = OFFSET_ELEVATION + Short.BYTES;
    private static final int OFFSET_BYTES_PER_EDGE = OFFSET_IDENTITY + Short.BYTES;
    private static final int OFFSET_IDENTITY_FIRST_SAMPLE = 30;
    private static final int OFFSET_PROFILE_TYPE = 2;
    private static final int OFFSET_VALUE_PER_SHORT_Q4_4 = 2;
    private static final int OFFSET_VALUE_PER_SHORT_Q0_4 = 4;

    /**
     * Décompresse le code en fonction du format de compression.
     *
     * @param compressionRate indique combien de valeurs sont contenues en un short
     * @param numberSamples nombre d'échantillons
     * @param index index d'où on commence pour récupérer les valeurs
     *
     * @return un profil d'échantillons décompressés.
     */
    private float[] decompression(int compressionRate, int numberSamples, int index) {
        int bitsPerValue = Short.SIZE / compressionRate;
        float[] profileSamples = new float[numberSamples];
        profileSamples[0] = asFloat((elevations.get(index)));

        for (int i = 0; i < numberSamples - 1; ++i) {
            profileSamples[i + 1] = profileSamples[i] + asFloat(extractSigned(elevations.get(index + 1 + (i / compressionRate)), (16 - bitsPerValue) - (i % compressionRate) * bitsPerValue, bitsPerValue));
        }

        return profileSamples;
    }

    /**
     * Enumeration privée représentant les différents types de profils
     */
    private enum Types {
        INEXISTANT, NOT_COMPRESSED, COMPRESSED_IN_Q4_4, COMPRESSED_IN_Q0_4;

        private static final List<Types> ALL_types = List.of(values());
    }

    /**
     * Retourne vrai ou faux en fonction du sens de l'arête.
     *
     * @param edgeId identité de l'arête donnée en paramètre
     *
     * @return vrai si l'arête va dans le sens inverse du nœud OSM dont elle provient.
     */
    public boolean isInverted(int edgeId) {
        int index = edgeId * OFFSET_BYTES_PER_EDGE + OFFSET_TARGET_NODE_ID;
        return (edgesBuffer.getInt(index) < 0);
    }

    /**
     * Retourne l'identité du nœud destination de l'arête.
     *
     * @param edgeId identité de l'arête
     *
     * @return l'identité du nœud destination de l'arête.
     */
    public int targetNodeId(int edgeId) {
        int index = edgeId * OFFSET_BYTES_PER_EDGE + OFFSET_TARGET_NODE_ID;
        int nodeIdentity = edgesBuffer.getInt(index);

        return nodeIdentity >= 0 ? nodeIdentity : ~nodeIdentity;
    }

    /**
     * Retourne la longueur de l'arête passée en paramètre.
     *
     * @param edgeId identité de l'arête
     *
     * @return la longueur de l'arête en mètres.
     */
    public double length(int edgeId) {
        int index = edgeId * OFFSET_BYTES_PER_EDGE + OFFSET_LENGTH;
        return asDouble(Short.toUnsignedInt(edgesBuffer.getShort(index)));
    }

    /**
     * Retourne le dénivelé positif de l'arête en mètres.
     *
     * @param edgeId identité de l'arête
     *
     * @return le dénivelé positif de l'arête en mètres.
     */
    public double elevationGain(int edgeId) {
        int index = edgeId * OFFSET_BYTES_PER_EDGE + OFFSET_ELEVATION;
        return asDouble(Short.toUnsignedInt(edgesBuffer.getShort(index)));
    }

    /**
     * Indique si l'arête possède un profil.
     *
     * @param edgeId identité de l'arête
     *
     * @return vrai si l'arête possède un profil.
     */
    public boolean hasProfile(int edgeId) {
        int profileType = extractUnsigned(profileIds.get(edgeId), OFFSET_IDENTITY_FIRST_SAMPLE, OFFSET_PROFILE_TYPE);
        return (Types.ALL_types.get(profileType) != Types.INEXISTANT);
    }

    /**
     * Retourne le tableau des échantillons du profil de l'arête.
     *
     * @param edgeId identité de l'arête
     *
     * @return le tableau des échantillons du profil de l'arête d'identité donnée.
     */
    public float[] profileSamples(int edgeId) {
        int numberSamples = 1 + ceilDiv(Short.toUnsignedInt(edgesBuffer.getShort(edgeId * OFFSET_BYTES_PER_EDGE + OFFSET_LENGTH)), Q28_4.ofInt(2));
        int index = extractUnsigned(profileIds.get(edgeId), OFFSET_TARGET_NODE_ID, OFFSET_IDENTITY_FIRST_SAMPLE);

        float[] profileSamples = new float[numberSamples];

        boolean isInverted = (edgesBuffer.getInt(edgeId * OFFSET_BYTES_PER_EDGE + OFFSET_TARGET_NODE_ID) < 0);
        int profileType = extractUnsigned(profileIds.get(edgeId), OFFSET_IDENTITY_FIRST_SAMPLE, OFFSET_PROFILE_TYPE);

        switch (Types.ALL_types.get(profileType)) {
            case INEXISTANT -> {
                return new float[0];
            }

            case NOT_COMPRESSED -> {
                for (int i = 0; i < numberSamples; ++i) {
                    profileSamples[i] = asFloat(Short.toUnsignedInt(elevations.get(index + i)));
                }
            }

            case COMPRESSED_IN_Q4_4 -> profileSamples = decompression(OFFSET_VALUE_PER_SHORT_Q4_4, numberSamples, index);

            case COMPRESSED_IN_Q0_4 -> profileSamples = decompression(OFFSET_VALUE_PER_SHORT_Q0_4, numberSamples, index);
        }

        if (isInverted) {
            for (int i = 0; i < profileSamples.length / 2; ++i) {
                float temp = profileSamples[i];
                profileSamples[i] = profileSamples[profileSamples.length - i - 1];
                profileSamples[profileSamples.length - i - 1] = temp;
            }
        }
        return profileSamples;
    }

    /**
     * Retourne l'identité de l'ensemble d'attributs attaché à l'arête.
     *
     * @param edgeId identité de l'arête
     *
     * @return l'identité de l'ensemble d'attributs attaché à l'arête.
     */
    public int attributesIndex(int edgeId) {
        int index = edgeId * OFFSET_BYTES_PER_EDGE + OFFSET_IDENTITY;
        return Short.toUnsignedInt(edgesBuffer.getShort(index));
    }
}