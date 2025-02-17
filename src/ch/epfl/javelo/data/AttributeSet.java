package ch.epfl.javelo.data;

import java.util.StringJoiner;

import static ch.epfl.javelo.Preconditions.checkArgument;
import static ch.epfl.javelo.data.Attribute.ALL;

/**
 * Représente un ensemble d'attributs OpenStreetMap.
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */
public record AttributeSet(long bits) {
    public AttributeSet {
        checkArgument((bits >> Attribute.COUNT) == 0);
    }

    /**
     * Retourne un ensemble contenant uniquement les attributs donnés en argument.
     *
     * @param attributes attributs contenus dans l'ensemble
     *
     * @return un ensemble contenant uniquement les attributs donnés en argument.
     */
    public static AttributeSet of(Attribute... attributes) {
        long seq = 0;
        for (Attribute attribute : attributes) {
            long mask = 1L << attribute.ordinal();
            seq = (mask | seq);
        }
        return new AttributeSet(seq);
    }

    /**
     * Permet de savoir si l'ensemble récepteur contient l'attribut donné.
     *
     * @param attribute attribut cherché
     *
     * @return true ssi l'ensemble récepteur (this) contient l'attribut donné
     * sinon, false.
     */
    public boolean contains(Attribute attribute) {
        return ((1L << attribute.ordinal()) & this.bits) != 0;
    }

    /**
     * Permet de savoir si deux ensembles d'attributs ont des elements en commun.
     *
     * @param that ensemble passé en argument
     *
     * @return true ssi l'intersection de l'ensemble récepteur (this) avec celui
     * passé en argument (that) n'est pas vide,
     * false sinon.
     */
    public boolean intersects(AttributeSet that) {
        return (that.bits & this.bits) != 0;
    }

    /**
     * Rassemble la représentation textuelle des éléments de l'ensemble entourés d'accolades
     * ({}) et séparés par des virgules dans un String.
     * @return une chaîne String.
     */
    public String toString() {
        StringJoiner string = new StringJoiner(",", "{", "}");

        for (Attribute attribute : ALL) {
            if (this.contains(attribute)) {
                string.add(attribute.keyValue());
            }
        }
       return string.toString();
    }
}