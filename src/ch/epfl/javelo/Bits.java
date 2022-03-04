package ch.epfl.javelo;

import static ch.epfl.javelo.Preconditions.checkArgument;
/**
 *
 *
 *  @author Camille Espieux (324248)
 *  @author Chiara Freneix (329552)
 *
 */

public final class Bits {
    private Bits(){}

    static int extractSigned(int value, int start, int length) {
        checkArgument((0<=length) && (0<=start) && ((length+start)<=31));
        if (length==0) return 0;
        int r =value << (32-(length+start));
        return r >> (32-length);
    }


    static int extractUnsigned(int value, int start, int length) {
        checkArgument((0<=start) && ((length+start)<=31) && (length < 32) && (0<=length));
        if (length==0) return 0;
        int r =value << (32-(length+start));
        return (r >>> (32-length));
    }
}