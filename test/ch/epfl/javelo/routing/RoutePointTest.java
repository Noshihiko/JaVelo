package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import static ch.epfl.test.TestRandomizerP.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizerP.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RoutePointTest {

    @Test
    public void noneIsWellDefined() {
        assertEquals(RoutePoint.NONE.point(), null);
        assertEquals(RoutePoint.NONE.position(), Double.NaN);
        assertEquals(RoutePoint.NONE.distanceToReference(), Double.POSITIVE_INFINITY);
    }
    @Test
    public void withPositionsShiftedByWorksOnPositiveValues() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            PointCh point = new PointCh(rng.nextDouble(2485000, 2834000),
                    rng.nextDouble(1075000, 1296000));            double distanceToReference = rng.nextDouble();
            double position = rng.nextDouble();
            double positionDifference = rng.nextDouble();

            RoutePoint routePoint = new RoutePoint(point, position, distanceToReference);

            assertEquals(routePoint.point(),
                    routePoint.withPositionShiftedBy(positionDifference).point());

            assertEquals(position + positionDifference,
                    routePoint.withPositionShiftedBy(positionDifference).position());

            assertEquals(distanceToReference,
                    routePoint.withPositionShiftedBy(positionDifference).distanceToReference());

        }
    }

    @Test
    public void withPositionsShiftedByWorksOnNegativeValues() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            PointCh point = new PointCh(rng.nextDouble(2485000, 2834000),
                    rng.nextDouble(1075000, 1296000));            double distanceToReference = rng.nextDouble();
            double position = rng.nextDouble();
            double positionDifference = - rng.nextDouble();

            RoutePoint routePoint = new RoutePoint(point, position, distanceToReference);

            //TODO check if the point should stay the same
            assertEquals(routePoint.point(),
                    routePoint.withPositionShiftedBy(positionDifference).point());

            assertEquals(position + positionDifference,
                    routePoint.withPositionShiftedBy(positionDifference).position());

            assertEquals(distanceToReference,
                    routePoint.withPositionShiftedBy(positionDifference).distanceToReference());

        }
    }

    @Test
    public void minWorksOnRandomValues() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            RoutePoint routePoint1 = new RoutePoint(new PointCh(rng.nextDouble(2485000, 2834000),
                    rng.nextDouble(1075000, 1296000))
                    , rng.nextDouble(), rng.nextDouble());

            RoutePoint routePoint2 =  new RoutePoint(new PointCh(rng.nextDouble(2485000, 2834000),
                    rng.nextDouble(1075000, 1296000))
                    , rng.nextDouble(), rng.nextDouble());

            RoutePoint expected = (routePoint1.distanceToReference() <= routePoint2.distanceToReference()) ?
                    routePoint1 : routePoint2;

            RoutePoint actual = routePoint1.min(routePoint2);

            assertEquals(expected, actual);
        }
    }

    @Test
    public void minWorksWithEqualDistancesToReference() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            double distanceToReference = rng.nextDouble();

            RoutePoint routePoint1 = new RoutePoint(new PointCh(rng.nextDouble(2485000, 2834000),
                    rng.nextDouble(1075000, 1296000))
                    , rng.nextDouble(), distanceToReference);

            RoutePoint routePoint2 =  new RoutePoint(new PointCh(rng.nextDouble(2485000, 2834000),
                    rng.nextDouble(1075000, 1296000))
                    , rng.nextDouble(), distanceToReference);

            assertEquals(routePoint1, routePoint1.min(routePoint2)) ;

        }
    }

    @Test
    public void minOverLoadWorksWithFurtherRoutePointsInParameter() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            PointCh point = new PointCh(rng.nextDouble(2485000, 2834000),
                    rng.nextDouble(1075000, 1296000));
            double position = rng.nextDouble();
            double distanceToReference = rng.nextDouble();

            RoutePoint routePoint = new RoutePoint(new PointCh(rng.nextDouble(2485000, 2834000),
                    rng.nextDouble(1075000, 1296000))
                    , rng.nextDouble(), rng.nextDouble(0, distanceToReference));

            RoutePoint expected = routePoint;

            RoutePoint actual = routePoint.min(point, position, distanceToReference);

            assertEquals(expected, actual);
        }
    }

    @Test
    public void minOverloadWorkWithEqualDistancesToReference() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            PointCh point = new PointCh(rng.nextDouble(2485000, 2834000),
                    rng.nextDouble(1075000, 1296000));            double position = rng.nextDouble();
            double distanceToReference = rng.nextDouble();

            RoutePoint routePoint = new RoutePoint(new PointCh(rng.nextDouble(2485000, 2834000),
                    rng.nextDouble(1075000, 1296000))
                    , rng.nextDouble(), distanceToReference);

            assertEquals(routePoint, routePoint.min(point, position, distanceToReference));

        }
    }

    @Test
    public void minOverloadWorksWithCloserRoutePointsInParameter() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            PointCh point = new PointCh(rng.nextDouble(2485000, 2834000),
                    rng.nextDouble(1075000, 1296000));
            double position = rng.nextDouble();
            double distanceToReference = rng.nextDouble();
            double smallerDistanceToReference = rng.nextDouble(distanceToReference);

            RoutePoint routePoint = new RoutePoint(new PointCh(rng.nextDouble(2485000, 2834000),
                    rng.nextDouble(1075000, 1296000))
                    , rng.nextDouble(), distanceToReference);

            RoutePoint expected = new RoutePoint(point, position, smallerDistanceToReference);

            RoutePoint actual = routePoint.min(point, position, smallerDistanceToReference);

            assertEquals(expected.point(), actual.point());
            assertEquals(expected.position(), actual.position());
            assertEquals(expected.distanceToReference(), actual.distanceToReference());
        }
    }

}

