package ch.epfl.javelo.routing;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Locale;

public class RouteComputerTest {

    public final class KmlPrinter {
        private static final String KML_HEADER =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<kml xmlns=\"http://www.opengis.net/kml/2.2\"\n" +
                        "     xmlns:gx=\"http://www.google.com/kml/ext/2.2\">\n" +
                        "  <Document>\n" +
                        "    <name>JaVelo</name>\n" +
                        "    <Style id=\"byBikeStyle\">\n" +
                        "      <LineStyle>\n" +
                        "        <color>a00000ff</color>\n" +
                        "        <width>4</width>\n" +
                        "      </LineStyle>\n" +
                        "    </Style>\n" +
                        "    <Placemark>\n" +
                        "      <name>Path</name>\n" +
                        "      <styleUrl>#byBikeStyle</styleUrl>\n" +
                        "      <MultiGeometry>\n" +
                        "        <LineString>\n" +
                        "          <tessellate>1</tessellate>\n" +
                        "          <coordinates>";

        private static final String KML_FOOTER =
                "          </coordinates>\n" +
                        "        </LineString>\n" +
                        "      </MultiGeometry>\n" +
                        "    </Placemark>\n" +
                        "  </Document>\n" +
                        "</kml>";

        public static void write(String fileName, Route route)
                throws IOException {
            try (PrintWriter w = new PrintWriter(fileName)) {
                w.println(KML_HEADER);
                for (PointCh p : route.points())
                    w.printf(Locale.ROOT,
                            "            %.5f,%.5f\n",
                            Math.toDegrees(p.lon()),
                            Math.toDegrees(p.lat()));
                w.println(KML_FOOTER);
            }
        }
    }
    @Test
    public void RouteComputertest6() throws IOException {
            Graph g = Graph.loadFrom(Path.of("lausanne"));
            CostFunction cf = new CityBikeCF(g);
            RouteComputer rc = new RouteComputer(g, cf);
            Route r = rc.bestRouteBetween(159049, 117669);
            KmlPrinter.write("javelo.kml", r);
        }

    @Test
    public void RouteComputertestOuestSuisse() throws IOException {
        Graph g = Graph.loadFrom(Path.of("ch_west"));
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);
        Route r = rc.bestRouteBetween(2046055, 2694240);
        System.out.println(r.length());
        KmlPrinter.write("javelo.kml", r);
    }
    }

