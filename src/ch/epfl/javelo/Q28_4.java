package ch.epfl.javelo;

import static java.lang.Math.scalb;

/**
 * Convertit des nombres entre la représentation Q28.4 et d'autres représentations.
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */
public final class Q28_4 {
    final static int BITS_AFTER_POINT = 4;

    //Constructeur privé de Q28_4 qui rend la classe non instanciable.
    private Q28_4() {}

    /**
     * Calcule la valeur Q28.4 correspondant à l'entier donné.
     *
     * @param i l'entier dont on cherche la valeur Q28.4
     * @return la valeur Q28.4 correspondant à l'entier donné.
     */
    public static int ofInt(int i) {
        return (i << BITS_AFTER_POINT);
    }

    /**
     * Calcule la valeur de type double égale à la valeur Q28.4 donnée.
     *
     * @param q28_4 la valeur donnée dont on cherche la valeur décimale correspondante
     * @return la valeur de type double égale à la valeur Q28.4 donnée.
     */
    public static double asDouble(int q28_4) {
        return scalb((double) q28_4, -BITS_AFTER_POINT);
    }

    /**
     * Calcule la valeur de type float égale à la valeur Q28.4 donnée.
     *
     * @param q28_4 la valeur donnée dont on cherche la valeur float correspondante
     * @return la valeur de type float égale à la valeur Q28.4 donnée.
     */
    public static float asFloat(int q28_4) {
        return scalb(q28_4, -BITS_AFTER_POINT);
    }
}