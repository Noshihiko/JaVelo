package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

import static ch.epfl.javelo.Math2.asinh;
import static java.lang.Math.PI;
import static java.lang.Math.tan;
import static java.lang.Math.atan;
import static java.lang.Math.sinh;


public final class WebMercator {
    private WebMercator(){}

    static double x(double lon){
        return (lon + PI)/(2d*PI);
    }

    static double y(double lat){
        double a = tan(lat);
        return (PI - asinh(a))/(2d*PI);
    }

    static double lon(double x){
        return 2d*PI*x - PI;
    }

    static double lat(double y){
        return atan(sinh(PI -2d*PI*y));
    }
}
