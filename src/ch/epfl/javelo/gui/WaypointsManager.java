package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.function.Consumer;

/**
 * Gère l'ajout de points intermediaires le long de l'itineraire.
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */

public final class WaypointsManager {
    public final Graph roadNetworkGraph;
    public final ObjectProperty<MapViewParameters> parameters;
    public final ObservableList<Waypoint> listWaypoints;
    public final Consumer<String> error;
    private final Pane pane;

    private final static int SEARCH_DISTANCE = 500;
    private final static int OFFSET_NODE_CLOSEST = -1;
    private final static String ERROR_MESSAGE = "Aucune route à proximité !";
    private final static String EXTERIOR_BORDER_WAYPOINT_LAYOUT_SVG_PATH = "M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20";
    private final static String INTERIOR_BORDER_WAYPOINT_LAYOUT_SVG_PATH = "M0-23A1 1 0 000-29 1 1 0 000-23";

    /**
     * Constructeur public de la classe.
     *
     * @param roadNetworkGraph le graph du réseau routier
     * @param parameters la propriété JavaFX contenant les paramètres de la carte affichée
     * @param listWaypoints la liste observable de tous les points de passage
     * @param error un objet permettant de signaler les erreurs
     */

    public WaypointsManager(Graph roadNetworkGraph, ObjectProperty<MapViewParameters> parameters, ObservableList<Waypoint> listWaypoints, Consumer<String> error) {
        this.roadNetworkGraph = roadNetworkGraph;
        this.parameters = parameters;
        this.listWaypoints = listWaypoints;
        this.error = error;

        pane = new Pane();
        pane.setPickOnBounds(false);

        listWaypoints.addListener((ListChangeListener<? super Waypoint>) Observable -> {
            pane().getChildren().clear();
            createNewListWaypoints();

        });

        parameters.addListener(Observable -> {
            pane().getChildren().clear();
            createNewListWaypoints();
        });
    }

    /**
     * Methode retournant le panneau contenant les points de passage.
     *
     * @return le panneau contenant les points de passage
     */

    public Pane pane() {
        return pane;
    }

    /**
     * Créer un Waypoint (en faisant appel a la methode createNewWaypoint) et l'ajoute à la liste des Waypoints.
     *
     * @param x abscisse lorsque la sourie clique pour ajouter un point
     * @param y ordonnée lorsque la sourie clique pour ajouter un point
     */

    public void addWaypoint(double x, double y) {
        Waypoint NewWaypoint = createNewWaypoint(x, y);

        if (NewWaypoint != null) {
            listWaypoints.add(NewWaypoint);
        } else error.accept(ERROR_MESSAGE);
    }

    /**
     * Methode servant à créer un nouveau Waypoint à partir d'un noeud se trouvant dans un cercle de 1000 m
     * de diamètre centré sur le point donné, lance un message d'erreur sinon.
     *
     * @param x abscisse lorsque la sourie clique pour ajouter un point
     * @param y ordonnée lorsque la sourie clique pour ajouter un point
     *
     * @return le Waypoint créer à partir du noeud le plus proche trouvé
     */

    private Waypoint createNewWaypoint(double x, double y) {
        PointCh newPoint = parameters.get().pointAt(x, y).toPointCh();

        if(newPoint != null) {

            int nodeClosestId = roadNetworkGraph.nodeClosestTo(newPoint, SEARCH_DISTANCE);

            if (nodeClosestId != OFFSET_NODE_CLOSEST) {
                return new Waypoint(newPoint, nodeClosestId);
            }

        }
        return null;
    }

    /**
     * Se charge de l'affichage des Waypoints à l'écran.
     *
     * @param waypoint Waypoint à afficher
     * @param index index dans la liste des Waypoints du Waypoint à afficher
     */

    private void drawWaypoint(Waypoint waypoint, int index) {
        Group newGroup = new Group();
        pane().getChildren().add(newGroup);

        PointWebMercator point = PointWebMercator.ofPointCh(waypoint.pointCh());
        layoutPoint(point, newGroup);
        /*double x = parameters.get().viewX(point);
        double y = parameters.get().viewY(point);

        newGroup.setLayoutX(x);
        newGroup.setLayoutY(y);*/

        newGroup.getStyleClass().add("pin");

        SVGPath outline = new SVGPath();
        SVGPath interior = new SVGPath();

        outline.getStyleClass().add("pin_outside");
        interior.getStyleClass().add("pin_inside");

        outline.setContent(EXTERIOR_BORDER_WAYPOINT_LAYOUT_SVG_PATH);
        interior.setContent(INTERIOR_BORDER_WAYPOINT_LAYOUT_SVG_PATH);

        newGroup.getChildren().add(outline);
        newGroup.getChildren().add(interior);

        String position;

        //Mettre des costes/variables a la place de 0 et du dernier element ?
        if (index == 0) position = String.valueOf(Position.first);
        else {
            position = (index == listWaypoints.size() - 1) ? String.valueOf(Position.last) :
                    String.valueOf(Position.middle);
        }
        newGroup.getStyleClass().add(position);

        //Listeners sur le Waypoint permettant d'ajourner le layout du waypoint
        newGroup.setOnMouseDragged(event -> {
            newGroup.setLayoutX(event.getSceneX());
            newGroup.setLayoutY(event.getSceneY());

        });

        newGroup.setOnMouseReleased(event -> {
            if (event.isStillSincePress()) {
                listWaypoints.remove(index);
                pane().getChildren().remove(newGroup);
            }

            if (!event.isStillSincePress()) {

                Waypoint waypointChanged = createNewWaypoint(event.getSceneX(), event.getSceneY());
                if (waypointChanged != null) {
                    listWaypoints.set(index, waypointChanged);
                    pane().getChildren().clear();
                    createNewListWaypoints();
                } else {
                    error.accept(ERROR_MESSAGE);
                    layoutWaypointsList();
                }
            }
        });
    }

    /**
     * Met à jour l'affichage de tous les waypoints de la liste.
     */

    private void layoutWaypointsList() {
        for (int i = 0; i < listWaypoints.size(); ++i) {
            Waypoint waypoint = listWaypoints.get(i);
            Node node = pane.getChildren().get(i);

            PointWebMercator point = PointWebMercator.ofPointCh(waypoint.pointCh());
            /*double x = parameters.get().viewX(point);
            double y = parameters.get().viewY(point);

            marker.setLayoutX(x);
            marker.setLayoutY(y);*/
            layoutPoint(point, node);
        }
    }

    /**
     * Methode s'occupant de géré les detail de l'affichage d'un point
     *
     * @param point le point web mercator à affiché correspondant à la position du waypoint
     * @param node le noeud servant à l'afficher
     */

    private void layoutPoint( PointWebMercator point, Node node) {
        double x = parameters.get().viewX(point);
        double y = parameters.get().viewY(point);

        node.setLayoutX(x);
        node.setLayoutY(y);
    }

    /**
     * Methode créant une nouvelle liste de Waypoint et qui remet à jour leur affichage en
     * appelant la methode layoutWaypoints.
     */

    private void createNewListWaypoints() {
        for (int i = 0; i < listWaypoints.size(); ++i) {
            drawWaypoint(listWaypoints.get(i), i);
        }
        layoutWaypointsList();
    }

    /**
     * Enumeration correspondante aux trois differentes couleures de Waypoint en fonction de leur position
     * sur l'itineraire.
     */

    private enum Position {
        first, middle, last;
    }
}




