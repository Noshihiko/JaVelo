package ch.epfl.javelo;

/**
 * Validation d'un argument
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */
public final class Preconditions {
    private Preconditions() {
    }

    /**
     * Méthode de validation d'argument
     *
     * @param shouldBeTrue l'argument
     * @throws IllegalArgumentException si l'argument est faux
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}