package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;


//TODO demander si ok si je fais un addAll quand je bind les children
public final class ElevationProfileManager {
    private final ReadOnlyObjectProperty<ElevationProfile> profilePrinted;
    ReadOnlyDoubleProperty position;
    //1
    private final BorderPane borderPane;
    private Pane pane;
    private VBox pane2;
    private Path grid;
    private Polygon polygon;

    private Group group;
    private Text etiquette1, etiquette2, etiquette3;
    private Line highlightedPosition;
    private Text statistics;

    //2
    private Insets distanceRectangle;

    //3
    //1733
    private ObjectProperty<Rectangle2D> rectangle;
    private ObjectProperty<Transform> screenToWorld, worldToScreen;

    //4

    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> profilePrinted, ReadOnlyDoubleProperty position) {
        this.profilePrinted = profilePrinted;
        this.position = position;

        //création de l'interface
        pane = new Pane();
        pane2 = new VBox();
        grid = new Path();

        group = new Group();
        etiquette1 = new Text();
        etiquette2 = new Text();
        etiquette3 = new Text();

        polygon = new Polygon();
        highlightedPosition = new Line();
        statistics = new Text();

        double x2 = profilePrinted.get().length() / 1000;


        //TODO pour le premier point, il faut utiliser les affines
        polygon.getPoints().addAll(0.0, 0.0,
                distanceRectangle.getLeft(), distanceRectangle.getBottom(),
                distanceRectangle.getRight(), distanceRectangle.getBottom()
        );

        pane2.setId("profile_data");
        polygon.setId("profile");
        grid.setId("grid");

        borderPane = new BorderPane(pane,null,null,pane2,null);
        borderPane.getStylesheets().add("elevation_profile.css");

        etiquette1.getStyleClass().addAll("grid_label", "horizontal");
        etiquette2.getStyleClass().addAll("grid_label", "vertical");

        group.getChildren().addAll(etiquette1, etiquette2);
        pane.getChildren().addAll(grid, group, polygon);
        pane2.getChildren().add(etiquette3);

        borderPane.centerProperty().set(pane);
        borderPane.bottomProperty().set(pane2);

        //Rectangle contenant le profil
        distanceRectangle = new Insets(10, 10, 20, 40);
        //1404
        Affine affine = new Affine(worldToScreen.getValue());
        //TODO : quelles sont les valeurs à mettre ?
       /*
        affine.prependTranslation();
        affine.prependScale();
        affine.prependTranslation();

        affine.createInverse()
                */

        double minElevation = profilePrinted.get().minElevation();
        double maxElevation = profilePrinted.get().maxElevation();
        Point2D p1 = new Point2D(0, rectangle.get().getMaxY());
        Point2D p2 = new Point2D(rectangle.get().getMaxX(), 0);
        Point2D p1prime = new Point2D(0, maxElevation);
        Point2D p2prime = new Point2D(profilePrinted.get().length(), minElevation);

    }

    private Transform screenToWorld(Point2D p1, Point2D p2, Point2D p1prime, Point2D p2prime) {

        Affine transformationAffine = new Affine();

        transformationAffine.prependTranslation(p1prime.getX()-p1.getX(), p2prime.getY()-p2.getY());
        double sx = (p1prime.getX()-p2prime.getX())/(p2.getX() - p1.getX());
        double sy = (p1prime.getY()-p2prime.getY())/(p2.getY() - p1.getY());
        transformationAffine.prependScale(sx, sy);
        transformationAffine.prependTranslation();



        for (int i=0; i<profilePrinted.get().size(); ++i){
            double nbrPoints = nbrPixels;
            double length = profilePrinted.get().length()/nbrPoints;

        }
        //un point par pixel
        //creer un rec ord ? nope
        //point x distance depuis depart de la route et y hauteur de la route
        //elevation at(point)
        //ajouter un polygone
        //translation de length metre
        //min elevation au altitude 0 et max elevation
        //premiere translation
        Transform coord = new Transform();
        coords.prependTranslation();
        coords.prependScale();

        //3
        highlightedPosition.layoutXProperty().bind(Bindings.createDoubleBinding( () -> {
            return mousePositionOnProfileProperty().get();
                }, position));
        highlightedPosition.startYProperty().bind(Bindings.select(rectangle, "minY"));
        highlightedPosition.endYProperty().bind(Bindings.select(rectangle, "maxY"));
        highlightedPosition.visibleProperty().bind(position.greaterThanOrEqualTo(0));

        //4
        int[] POS_STEPS =
                { 1_000, 2_000, 5_000, 10_000, 25_000, 50_000, 100_000 };
        int[] ELE_STEPS =
                { 5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000 };


    }



    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
       //TODO
        return null;
    }

    private record Point(double length, double elevationAtLength){}
    //point (distance elevatioon)



    private Transform worldToScreen(Transform coords) {
        return screenToWorld(coords).createInverse();
    }

    public Pane pane() {
        return borderPane;
    }
}
