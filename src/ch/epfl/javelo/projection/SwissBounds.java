package ch.epfl.javelo.projection;

/**
 * Vérifie si un couple de coordonnées appartient au territoire suisse.
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */
public final class SwissBounds {
    public final static double MIN_E = 2_485_000;
    public final static double MAX_E = 2_834_000;
    public final static double MIN_N = 1_075_000;
    public final static double MAX_N = 1_296_000;

    public final static double WIDTH = MAX_E - MIN_E;
    public final static double HEIGHT = MAX_N - MIN_N;

    private SwissBounds() {}

    /**
     * Vérifie si les coordonnées données se trouvent en Suisse ou non.
     *
     * @param e la coordonnée Est
     * @param n la coordonnée Nord
     *
     * @return vrai si les coordonnées sont en Suisse,
     * sinon faux.
     */
    //TODO : comment on comment un boolean ?
    public static boolean containsEN(double e, double n) {
        return (MIN_E <= e && e <= MAX_E) && (MIN_N <= n && n <= MAX_N);
    }
}