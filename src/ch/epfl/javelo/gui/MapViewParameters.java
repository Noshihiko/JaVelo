package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;

import java.awt.geom.Point2D;


public record MapViewParameters(int zoom, double x, double y) {
    private static PointWebMercator p;

    public MapViewParameters(int zoom, double x, double y){
        this.zoom = zoom;
        this.x = x;
        this.y = y;

        p = PointWebMercator.of(zoom, x, y);
    }

    public Point2D topLeft() {
        return new Point2D() {

            @Override
            public double getX() {
                return Math.toDegrees(p.lon());
            }

            @Override
            public double getY() {
                return Math.toDegrees(p.lat());
            }

            @Override
            public void setLocation(double x1, double y1) {
               //   = x1;
               //   = y1;
            }
        };
    }

    public MapViewParameters withMinXY(double x, double y){
        return null;
    }

    public PointWebMercator pointAt(double x, double y){
        return null;
    }

    public double viewX(PointWebMercator that){
        return 0;
    }

    public double viewY(PointWebMercator that){
        return 0;
    }

}
