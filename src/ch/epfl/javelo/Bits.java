package ch.epfl.javelo;

import static ch.epfl.javelo.Preconditions.checkArgument;

public final class Bits {
    private Bits(){}

    static int extractSigned(int value, int start, int length) {
        checkArgument((0<=length) && (0<=start) && ((length+start)<=31));
        int r =value << (32-(length+start));
        return r >> (32-length);
    }


    static int extractUnsigned(int value, int start, int length) {
        checkArgument((0<=length) && (0<=start) && ((length+start)<=31) && (length < 32));
        int r =value << (32-(length+start));
        return r >>> (32-length);
    }
}