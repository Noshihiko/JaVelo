package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

public record RoutePoint(PointCh point, double position, double distanceToReference) {

    public static final RoutePoint NONE = new RoutePoint(null,Double.NaN,Double.POSITIVE_INFINITY);

    RoutePoint withPositionShiftedBy(double positionDifference){
        return new RoutePoint(this.point,this.position - positionDifference, this.distanceToReference);
    }

    RoutePoint min(RoutePoint that){
        if (this.distanceToReference<= that.distanceToReference){
            return new RoutePoint(that.point, that.position, this.distanceToReference);
        } else {
            return that;
        }
    }

    RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference){
        if (this.distanceToReference<= thatDistanceToReference){
            return this;
        } else {
            return new RoutePoint (thatPoint, thatPosition, thatDistanceToReference);
        }
    }

}
