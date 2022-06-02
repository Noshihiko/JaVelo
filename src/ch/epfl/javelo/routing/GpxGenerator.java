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
import java.util.Locale;

/**
 * Représente un générateur d'itinéraire au format GPX.
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */
public final class GpxGenerator {
    private final static String longitude = "%.5f";
    private final static String latitude = "%.5f";
    private final static String elevation = "%.2f";


    /**
     * Constructeur privé de la classe, car elle est non instanciable.
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
        Double position = 0d;
        Document doc = newDocument();

        Element root = doc
                .createElementNS("http://www.topografix.com/GPX/1/1",
                        "gpx");
        doc.appendChild(root);

        root.setAttributeNS(
                "http://www.w3.org/2001/XMLSchema-instance",
                "xsi:schemaLocation",
                "http://www.topografix.com/GPX/1/1 "
                        + "http://www.topografix.com/GPX/1/1/gpx.xsd");
        root.setAttribute("version", "1.1");
        root.setAttribute("creator", "JaVelo");

        Element metadata = doc.createElement("metadata");
        root.appendChild(metadata);

        Element name = doc.createElement("name");
        metadata.appendChild(name);
        name.setTextContent("Route JaVelo");

        Element rte = doc.createElement("rte");
        root.appendChild(rte);

        //**Récupère le premier point de chaque edge et rajoute ses données au doc**
        for (Edge edge : route.edges()) {
            position += edge.length();
            addPoints(elevationProfile, doc, rte, position, edge.fromPoint());
        }
        //**Récupère le dernier point de la dernière edge et rajoute ses données au doc**
        Edge edge = route.edges().get(route.edges().size() - 1);
        addPoints(elevationProfile, doc, rte, position, edge.toPoint());

        return doc;
    }

    /**
     * Rajoute les points de l'itinéraire au document.
     *
     * @param elevationProfile le profil de cet itinéraire
     * @param doc le document que l'on modifie
     * @param rte la route de l'itinéraire
     * @param position la distance sur l'itinéraire
     * @param point le point qu'on cherche à rajouter au document
     */
    private static void addPoints(ElevationProfile elevationProfile, Document doc, Element rte, Double position, PointCh point) {
        Element routePoint = doc.createElement("rtept");
        routePoint.setAttribute("lat", String.format(
                        Locale.ROOT,
                        latitude,
                        Math.toDegrees(point.lat())));
        routePoint.setAttribute("lon", String.format(
                        Locale.ROOT,
                        longitude,
                        Math.toDegrees(point.lon())));
        rte.appendChild(routePoint);

        Element elevationPoint = doc.createElement("ele");
        elevationPoint.setTextContent(String.format(
                Locale.ROOT,
                elevation,
                elevationProfile.elevationAt(position)));
        routePoint.appendChild(elevationPoint);
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