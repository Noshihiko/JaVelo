package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.*;

/**
 * Représente un générateur d'itinéraire au format GPX.
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */
public class GpxGenerator {

    /**
     * Constructeur privé de la classe, car elle est immuable.
     */
    private GpxGenerator() {
    }

    /**
     * Crée un nouveau document.
     *
     * @return le nouveau document créé
     */
    private static Document newDocument() {
        try {
            return DocumentBuilderFactory
                    .newDefaultInstance()
                    .newDocumentBuilder()
                    .newDocument();
        } catch (ParserConfigurationException e) {
            throw new Error(e);
        }
    }

    /**
     * Retourne le document GPX (de type Document) correspondant.
     *
     * @param route            l'itinéraire
     * @param elevationProfile le profil de cet itinéraire
     * @return le document GPX (de type Document) correspondant
     */
    public static Document createGpx(Route route, ElevationProfile elevationProfile) {
        Document doc = newDocument();

        Element root = doc
                .createElementNS("https://www.topografix.com/GPX/1/1",
                        "gpx");
        doc.appendChild(root);

        root.setAttributeNS(
                "http://www.w3.org/2001/XMLSchema-instance",
                "xsi:schemaLocation",
                "https://www.topografix.com/GPX/1/1 "
                        + "https://www.topografix.com/GPX/1/1/gpx.xsd");
        root.setAttribute("version", "1.1");
        root.setAttribute("creator", "JaVelo");

        Element metadata = doc.createElement("metadata");
        root.appendChild(metadata);

        Element name = doc.createElement("name");
        metadata.appendChild(name);
        name.setTextContent("Route JaVelo");

        Element rte = doc.createElement("rte");
        root.appendChild(rte);

        for (Edge edge : route.edges()) {
            String lon = "%.5f";
            String lat = "%.5f";
            String elevation = "%.2f";
            PointCh point = edge.fromPoint();

            Element routePoint = doc.createElement("rtept");
            routePoint.setAttribute("lat", String.format(lat, Math.toDegrees(point.lat())));
            routePoint.setAttribute("lon", String.format(lon, Math.toDegrees(point.lon())));
            rte.appendChild(routePoint);

            Element elevationPoint = doc.createElement("ele");
            elevationPoint.setTextContent(String.format(elevation, edge.elevationAt(edge.positionClosestTo(point))));
            //elevationPoint.setTextContent(String.format(elevation, elevationProfile.elevationAt(edge.positionClosestTo(point))));
            routePoint.appendChild(elevationPoint);
        }

        return doc;
    }

    /**
     * Ecrit le document GPX correspondant dans le fichier nameFile.
     *
     * @param nameFile         le nom du fichier
     * @param route            l'itinéraire
     * @param elevationProfile le profil de cet itinéraire
     *
     * @throws IOException en cas d'erreur d'entrée/sortie
     */
    public static void writeGpx(String nameFile, Route route, ElevationProfile elevationProfile) throws IOException {

        try {
            Document doc = createGpx(route, elevationProfile);
            Writer w = Files.newBufferedWriter(Path.of(nameFile));

            Transformer transformer = TransformerFactory
                    .newDefaultInstance()
                    .newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc),
                    new StreamResult(w));
        } catch (TransformerException e) {
            throw new Error(e);
        }
    }
}