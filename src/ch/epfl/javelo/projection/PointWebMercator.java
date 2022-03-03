package ch.epfl.javelo.projection;

import static ch.epfl.javelo.Preconditions.checkArgument;
import static java.lang.Math.scalb;

public record PointWebMercator(double x, double y) {
    public PointWebMercator {
        checkArgument((x>=0 && x<=1 && y>=0 && y<=1));
    }

    public static PointWebMercator of(int zoomLevel, double x, double y){
        x=scalb(x,-zoomLevel-8);
        y=scalb(y,-zoomLevel-8);
        PointWebMercator point = new PointWebMercator(x,y);
        return point;
    }

    public static PointWebMercator ofPointCh(PointCh pointCh){
        PointWebMercator point = new PointWebMercator(WebMercator.x(pointCh.lon()),WebMercator.y(pointCh.lat()));
        return point;
    }

    public double xAtZoomLevel(int zoomLevel){
        return scalb(x,zoomLevel+8);
    }
    public double yAtZoomLevel(int zoomLevel){
        return scalb(y,zoomLevel+8);
    }

    public double lon(){
        return WebMercator.lon(this.x);
    }
    public double lat(){
        return WebMercator.lat(this.y);
    }

    public PointCh toPointCh(){

        double lonSB = Ch1903.e(this.lon(),this.lat());
        double latSB =  Ch1903.n(this.lon(),this.lat());
        if (SwissBounds.containsEN(lonSB,latSB)){
            double lonWM = WebMercator.lon(this.x);
            double latWM = WebMercator.lat(this.y);
           PointCh point = new PointCh(Ch1903.e(lonWM,latWM),Ch1903.n(lonWM,latWM));
            return point;
        } else {
            return null;
        }
    }
}
