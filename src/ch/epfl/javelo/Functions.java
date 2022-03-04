package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

import static ch.epfl.javelo.Math2.interpolate;
import static ch.epfl.javelo.Preconditions.checkArgument;
/**
 *  Crée des objets représentant des fonctions mathématiques des réels vers les réels
 *
 *  @author Camille Espieux (324248)
 *  @author Chiara Freneix (329552)
 *
 */
public final class Functions {
    private Functions(){}

    /**
     * Donne une fonction constante, dont la valeur est toujours y
     *
     * @param y
     *
     * @return y
     */
    public static DoubleUnaryOperator constant(double y){
        return new Constant(y);
    }

    /**
     * Méthode permettant de rendre une fonction constante, dont la valeur est toujours y
     *
     * @param y
     */
    private static final record Constant (double y)
            implements DoubleUnaryOperator {

        /**
         * Empêche de changer la valeur de y attribuée dans la méthode Constant()
         *
         * @param y
         *
         * @return le y, attribut de la méthode Constant()
         */
        @Override
        public double applyAsDouble(double y) {
            return this.y;
        }
    }

    /**
     * Calcule une fonction obtenue par interpolation linéaire entre les échantillons samples,
     * espacés régulièrement et couvrant la plage allant de 0 à xMax
     *
     * @param samples
     *          tableau contenant les échantillons
     * @param xMax
     *          borne supérieure de la plage commençant à 0
     *
     * @return une fonction obtenue par interpolation linéaire entre les échantillons samples
     * @throws IllegalArgumentException
     *          si le tableau samples contient moins de deux éléments,
     *      ou si xMax est inférieur ou égal à 0
     */
    public static DoubleUnaryOperator sampled(float[] samples, double xMax){
        checkArgument(samples.length >=2 && xMax>0);
        return new Sampled(samples, xMax);

    }

    /**
     * Calcule l'interpolation linéaire entre les échantillons samples,
     * espacés régulièrement et couvrant la plage allant de 0 à xMax
     *
     * @param samples
     *          tableau contenant les échantillons
     * @param xMax
     *          borne supérieure de la plage commençant à 0
     * @return la première valeur du tableau samples
     *          si x<=0
     * @return la dernière valeur du tableau samples
     *          si x>= xMax
     * @return l'interpolation linéaire de Y0 et Y1 avec (valeurX[i+1]-x)/distance) qui correspond à x
     *          si valeurX[i]<x && valeurX[i+1]>x
     * @return la valeur du tableau correspondant à l'indice i
     *          si x == valeurX[i]
     * @throws IllegalArgumentException
     *          si la méthode ne retourne pas de valeurs
     */
    private static final record Sampled(float[] samples, double xMax) implements DoubleUnaryOperator {

        /**
         * Permet d'effectuer l'interpolation linéaire entre les échantillons samples,
         * espacés régulièrement et couvrant la plage allant de 0 à xMax
         * @param x
         *      la borne supérieure de l'intervalle
         * @return la première valeur du tableau samples
         *      si x<= 0
         * @return la dernière valeur du tableau samples
         *      si x>= xMax
         * @return l'interpolation linéaire de Y0 et Y1 avec (valeurX[i+1]-x)/distance) qui correspond à x
         *      si valeurX[i]<x && valeurX[i+1]>x
         * @return la valeur du tableau correspondant à l'indice i
         *      si x == valeurX[i]
         * @throws IllegalArgumentException
         *      si la méthode ne retourne pas de valeurs
         */
        @Override
        public double applyAsDouble(double x) {

            if (x<=0) return samples[0];
            else if (x>=xMax) return samples[samples.length-1];
            else {
                double distance = xMax / (samples.length - 1);
                double[] valeurX = new double[samples.length];

                for (int i=0; i<samples.length; ++i){
                    valeurX[i] = distance*i ;
                }

                for (int i =0; i<(samples.length-1);++i){
                    double Y0 = samples[i];
                    double Y1 = samples[i+1];
                    if (valeurX[i]<x && valeurX[i+1]>x){
                        return interpolate(Y0, Y1, (valeurX[i+1]-x)/distance);
                    } else if (x==valeurX[i]){
                        return samples[i];
                    }

                }
            }
            throw new IllegalArgumentException();
        }
    }
}
