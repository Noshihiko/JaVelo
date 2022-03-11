package ch.epfl.javelo;
import static java.lang.Math.scalb;

/**
 * Convertit des nombres entre la représentation Q28.4 et d'autres représentations
 *
 *  @author Camille Espieux (324248)
 *  @author Chiara Freneix (329552)
 *
 */
public final class Q28_4 {
    private Q28_4(){}
    final static int NUMBER_OF_BITS_AFTER_POINT = 4;

    /**
     * Calcule la valeur Q28.4 correspondant à l'entier donné
     *
     * @param i
     * @return  la valeur Q28.4
     */
    public static int ofInt(int i){
        return (i<<NUMBER_OF_BITS_AFTER_POINT);
    }

    /**
     * Calcule la valeur de type double égale à la valeur Q28.4 donnée
     * @param q28_4
     * @return la valeur de type double
     */

    public static double asDouble(int q28_4){
        return scalb((double) q28_4, -NUMBER_OF_BITS_AFTER_POINT);
    }

    /**
     * Calcule la valeur de type float égale à la valeur Q28.4 donnée
     * @param q28_4
     * @return la valeur de type float
     */
    public static float asFloat(int q28_4){
        return scalb(q28_4,-NUMBER_OF_BITS_AFTER_POINT);
    }
}
