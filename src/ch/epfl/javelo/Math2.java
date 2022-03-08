package ch.epfl.javelo;

import static ch.epfl.javelo.Preconditions.checkArgument;
import static java.lang.Math.*;

/**
 * Classe qui s'occupe de gérer des calculs mathématiques.
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 *
 */
public final class Math2 {
    private Math2(){}

    /**
     * Calcule la partie entière par excès de la division de x par y
     *
     * @param x
     *          le dividende
     * @param y
     *          le diviseur
     * @throws IllegalArgumentException
     *          si x est négatif ou si y est négatif ou nul
     * @return la partie entière par excès de la division de x par y
     */
    public static int ceilDiv(int x, int y){
        checkArgument(x>=0 && y >0);
        return (x+y -1)/y ;
    }

    /**
     * Calcule la position d'un point sur une droite
     *
     * @param y0
     * @param y1
     * @param x
     * @return la coordonnée y du point se trouvant sur la droite passant par (0,y0) et (1,y1) et de coordonnée x donnée.
     */
    public static double interpolate(double y0, double y1, double x){
        return fma((y1-y0),x, y0);
    }

    /**
     * Limite une valeur entière à un intervalle.
     *
     * @param min
     *          la borne inférieure entière de l'intervalle
     * @param v
     *          la valeur entière
     * @param max
     *          la borne maximale entière de l'intervalle
     * @throws IllegalArgumentException
     *          si le minimum est strictement supérieur au maximum
     * @return le minimum si v est inférieur au minimum de l'intervalle,
     *          le maximum si v est supérieur au maximum de l'intervalle,
     *          sinon v.
     */
    public static int clamp(int min, int v, int max){
        if(min>max){
            throw new IllegalArgumentException();
        } else if (v<min){
            return min;
        } else if (v>max){
            return max;
        } else {
            return v;
        }
    }

    /**
     Limite une valeur décimale à un intervalle.
     *
     * @param min
     *          la borne inférieure décimale de l'intervalle
     * @param v
     *          la valeur décimale
     * @param max
     *          la borne maximale décimale de l'intervalle
     * @throws IllegalArgumentException
     *          si le minimum est strictement supérieur au maximum
     * @return le minimum si v est inférieur au minimum de l'intervalle,
     *          le maximum si v est supérieur au maximum de l'intervalle,
     *          sinon v.
     */
    public static double clamp(double min, double v, double max){
        if(min>max) throw new IllegalArgumentException();
        else if (v<min) return min;
        else if (v>max) return max;
        else return v;

    }

    /**
     * Calcule le sinus hyperbolique inverse de son argument
     * @param x
     * @return le sinus hyperbolique inverse de x
     */
    public static double asinh(double x){
        return log(x+ sqrt(1+x*x));
    }

    /**
     * Calcule le produit scalaire des vecteurs u et v.
     * @param uX
     *          composante selon l'axe x du vecteur u
     * @param uY
     *          composante selon l'axe y du vecteur u
     * @param vX
     *          composante selon l'axe x du vecteur v
     * @param vY
     *          composante selon l'axe y du vecteur v
     * @return le produit scalaire des vecteurs u et v.
     */
    public static double dotProduct(double uX, double uY, double vX, double vY){
        return uX*vX + uY*vY;
    }

    /**
     * Calcule le carré de la norme du vecteur u
     *
     * @param uX
     *          composante selon l'axe x du vecteur u
     * @param uY
     *          composante selon l'axe y du vecteur u
     * @return le carré de la norme du vecteur u
     */
    public static double squaredNorm(double uX, double uY){
        return uX*uX + uY*uY;
    }

    /**
     * Calcule la norme du vecteur u
     *
     * @param uX
     *          composante selon l'axe x du vecteur u
     * @param uY
     *          composante selon l'axe y du vecteur u
     * @return la norme du vecteur u
     */
    public static double norm(double uX, double uY){
        return sqrt(squaredNorm(uX,uY));
    }

    /**
     * calcule de la longueur de la projection des vecteurs AP et AB
     *
     * @param aX
     *          composante selon l'axe x du vecteur a
     * @param aY
     *          composante selon l'axe y du vecteur a
     * @param bX
     *          composante selon l'axe x du vecteur b
     * @param bY
     *          composante selon l'axe y du vecteur b
     * @param pX
     *          composante selon l'axe x du vecteur p
     * @param pY
     *          composante selon l'axe y du vecteur p
     * @return la longueur de la projection des vecteurs AP et AB
     */
    public static double projectionLength(double aX, double aY, double bX, double bY, double pX, double pY){
        return dotProduct(pX-aX,pY-aY,bX-aX,bY-aY)/norm(bX-aX,bY-aY);
    }

}
