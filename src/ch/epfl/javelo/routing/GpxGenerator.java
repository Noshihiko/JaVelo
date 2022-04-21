package ch.epfl.javelo.routing;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.Writer;

public class GpxGenerator {

    private GpxGenerator(){}


    private static Document newDocument() {
        try {
            return DocumentBuilderFactory
                    .newDefaultInstance()
                    .newDocumentBuilder()
                    .newDocument();
        } catch (ParserConfigurationException e) {
            throw new Error(e); // Should never happen
        }
    }

    public static Document createGpx(Route route, ElevationProfile profile){
        Document doc = newDocument(); // voir plus bas

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

        Element rte = doc.createElement("route");
        name.appendChild(rte);

        for (int i=0; i < route.points().size(); ++i){
            String a = "";
            String b = "";

            Element rtept = doc.createElement("point");
            rtept.setAttribute("coordinates", String.format(a, route.points().get(i).lon() + " " + route.points().get(i).lat()));
            rte.appendChild(rtept);

            Element ele = doc.createElement("elevation");
            ele.setAttribute("elevation", String.format(b, profile.elevationAt(route.edges().get(i).length()) ));
            rtept.appendChild(ele);
        }

        return doc;
    }

    public static void writeGpx(String nameFile, Route route, ElevationProfile profile) throws IOException {
        /*
        try {
            Document doc = //TODO;
            Writer w = //TODO ;

            Transformer transformer = TransformerFactory
                    .newDefaultInstance()
                    .newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc),
                    new StreamResult(w));
        } catch (TransformerConfigurationException e) {
            throw new Error(e); //Should never happen
        }

         */
    }
}