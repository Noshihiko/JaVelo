package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Q28_4Tests {
    public static final double DELTA = 1e-7;

    @Test
    void ofIntWorksWithNormalValues(){
        int actual1 = Q28_4.ofInt(25);
        int expected1 = (int)Math.scalb(25,4);
        assertEquals(expected1,actual1,DELTA);

        int actual2 = Q28_4.ofInt(25000000);
        int expected2 =  (int)Math.scalb(25000000,4);;
        assertEquals(expected2,actual2,DELTA);
    }

    @Test
    void asDoubleWorksWithNormalValues(){
        double actual1 = Q28_4.asDouble(Q28_4.ofInt(0));
        double expected1 = 0;
        assertEquals(expected1,actual1,DELTA);

        double actual2 = Q28_4.asDouble(Q28_4.ofInt(16));
        double expected2 = 16;
        assertEquals(expected2,actual2,DELTA);

        double actual3 = Q28_4.asDouble(Q28_4.ofInt(31));
        double expected3 = 31;
        assertEquals(expected3,actual3,DELTA);

        double actual4 = Q28_4.asDouble(1);
        double expected4 = Math.pow(2,-4);
        assertEquals(expected4,actual4,DELTA);
    }

    @Test
    void asFloatWorksWithNormalValues(){
        float actual1 =(float) Q28_4.asDouble(Q28_4.ofInt(0));
        float expected1 = 0;
        assertEquals(expected1,actual1,DELTA);

        float actual2 =(float) Q28_4.asDouble(Q28_4.ofInt(16));
        float expected2 = 16;
        assertEquals(expected2,actual2,DELTA);

        float actual3 = (float) Q28_4.asDouble(Q28_4.ofInt(31));
        float expected3 = 31;
        assertEquals(expected3,actual3,DELTA);
    }
}
