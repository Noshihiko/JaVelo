package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

import java.nio.channels.Pipe;

public record PointWebMercator(double x, double y) {
    public PointWebMercator {
        Preconditions.checkArgument(((x>=0 && x<=1) && (y>=0 && y<=1)));
    }

    //normalement c'est corrigé, mais a verif
    public static PointWebMercator of(int zoomLevel, double x, double y){
        x=Math.scalb(x,-zoomLevel);
        y=Math.scalb(y,-zoomLevel);
        PointWebMercator point = new PointWebMercator(x,y);

        //x = point.xAtZoomLevel(zoomLevel);
        //y = point.yAtZoomLevel(zoomLevel);
        return point;
    }

    public static PointWebMercator ofPointCh(PointCh pointCh){
        //double lon = Ch1903.lon(pointCh.e(),pointCh.n());
        //double lat = Ch1903.lat(pointCh.e(),pointCh.n());

        //tu peux simplifier en utilisant pointCh
        //normalement c'est corrigé, mais a verif
        double x = pointCh.lon();
        double y = pointCh.lat();
        PointWebMercator point = new PointWebMercator(x,y);
        return point;
    }

    public double xAtZoomLevel(int zoomLevel){
        return Math.scalb(x,zoomLevel+8);
    }
    public double yAtZoomLevel(int zoomLevel){
        return Math.scalb(y,zoomLevel+8);
    }

    public double lon(){
        return WebMercator.lon(this.x);
    }
    public double lat(){
        return WebMercator.lat(this.y);
    }

    public PointCh toPointCh(){

        //convertir les coordonnées en WM.lon et lat de x et y
        //normalement c'est corrigé, mais a verif
        double lon = WebMercator.lon(this.x);
        double lat = WebMercator.lat(this.y);
        if (SwissBounds.containsEN(lon,lat)){
           PointCh point = new PointCh(lon,lat);
            return point;
        } else {
            return null;
        }
    }
}
