package ch.epfl.javelo;

public final class Q28_4 {
    private Q28_4(){}

    static int ofInt(int i){
        return (i<<4);
    }

    static double asDouble(int q28_4){
        return Math.scalb(q28_4,-4);
    }

    static float asFloat(int q28_4){
        return Math.scalb(q28_4,-4);
    }
}
