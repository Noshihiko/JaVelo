package ch.epfl.javelo;
import static ch.epfl.javelo.Preconditions.checkArgument;

/**
 * Extrait une séquence de bits d'un vecteur de 32 bits
 *
 *  @author Camille Espieux (324248)
 *  @author Chiara Freneix (329552)
 *
 */
public final class Bits {
    static final int lengthBits = 32;

    private Bits(){}

    /**
     * Extrait du vecteur de 32 bits value la plage de length bits (commençant au bit d'index
     * start) qu'elle interprète comme une valeur signée en complément à deux
     *
     * @param value
     *              le vecteur de 32 bits
     * @param start
     *              début de la plage de bits extraits
     * @param length
     *              plage de bits extraits
     *
     * @return la plage de length bits commençant au bit d'index start
     * @throws IllegalArgumentException
     *              si la plage est invalide, c.à.d. si elle n'est pas totalement incluse dans
     *              l'intervalle allant de 0 à 31 (inclus)
     */
    public static int extractSigned(int value, int start, int length) {
        checkArgument((0<=length) && (0<=start) && ((length+start)<=lengthBits));
        if (length==0) return 0;
        int r =value << (lengthBits-(length+start));

        return r >> (lengthBits-length);
    }

    /**
     * Extrait du vecteur de 32 bits value la plage de length bits (commençant au bit d'index
     * start) qu'elle interprète comme une valeur non signée
     *
     * @param value
     *              le vecteur de 32 bits
     * @param start
     *              début de la plage de bits extraits
     * @param length
     *              plage de bits extraits
     *
     * @return la plage de length bits commençant au bit d'index start
     * @throws IllegalArgumentException
     *              si la plage est invalide, c.à.d. si elle n'est pas totalement incluse dans
     *              l'intervalle allant de 0 à 32 (inclus)
     */
    public static int extractUnsigned(int value, int start, int length) {
        checkArgument((0<=start) && ((length+start)<=lengthBits) && (length < lengthBits) && (0<=length));
        if (length==0) return 0;
        int r =value << (lengthBits-(length+start));

        return (r >>> (lengthBits-length));
    }
}