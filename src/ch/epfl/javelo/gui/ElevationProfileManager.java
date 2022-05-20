package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
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

    private ObjectProperty<Transform> screenToWorld = new SimpleObjectProperty<>(new Affine());
    private ObjectProperty<Transform> worldToScreen = new SimpleObjectProperty<>(new Affine());


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

    //******************************* Transformations *********************************
        double minElevation = profilePrinted.get().minElevation();
        double maxElevation = profilePrinted.get().maxElevation();
        Point2D p1 = new Point2D(0, rectangle.get().getMaxY());
        Point2D p2 = new Point2D(rectangle.get().getMaxX(), 0);
        Point2D p1prime = new Point2D(0, maxElevation);
        Point2D p2prime = new Point2D(profilePrinted.get().length(), minElevation);

    }

    private void setScreenToWorld(Point2D p1, Point2D p2, Point2D p1prime, Point2D p2prime) {

        Affine transformationAffine = new Affine();

        transformationAffine.prependTranslation(-10, -40);
        double sx = (p1prime.getX() - p2prime.getX()) / (p2.getX() - p1.getX());
        double sy = (p1prime.getY() - p2prime.getY()) / (p2.getY() - p1.getY());
        transformationAffine.prependScale(sx, sy);
        transformationAffine.prependTranslation(0, p1prime.getX());

        screenToWorld.set(transformationAffine);
    }

    private void setWorldToScreen(Point2D p1, Point2D p2, Point2D p1prime, Point2D p2prime) {

        Affine transformationAffine = new Affine();
        screenToWorld.set(transformationAffine);
        worldToScreen.set(screenToWorld.get().createInverse());
    }
    //********************************* fin transformations ****************************************

        //3


        //4
        //TODO paramètre de distanceInBetween ?
        //TODO faut il deux types de distanceInBetween ?
        int[] POS_STEPS =
                { 1_000, 2_000, 5_000, 10_000, 25_000, 50_000, 100_000 };
        int[] ELE_STEPS =
                { 5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000 };

        double distanceInBetweenWidth = worldToScreen.get().deltaTransform(,);
        double distanceInBetweenHeight = worldToScreen.get().deltaTransform(,);

        //TODO quelles valeurs faut il mettre aux distances
        if (distanceInBetweenWidth < 25) {
            distanceInBetweenWidth = Double.NaN;
        }
        if (distanceInBetweenHeight < 50) {
            distanceInBetweenHeight = Double.NaN;
        }
        //TODO puis je faire un addAll ou suis je obligée de faire de trucs séparément ?
        // si séparément, je laisse comme ça, ou je dois mettre un index ?
        // i à 1 ou 0 ? car techniquement à 0 on voit pas la grille donc pas utile

        for (int i = 1; i < rectangle.get().getWidth()/distanceInBetweenWidth; ++i){
            grid.getElements().add(new MoveTo(0,i));
            grid.getElements().add(new LineTo(rectangle.get().getWidth(),i));
        }
        for (int i = 1; i < rectangle.get().getHeight()/distanceInBetweenHeight){
            grid.getElements().add(new MoveTo(i,0));
            grid.getElements().add(new LineTo(i,rectangle.get().getHeight()))
        }
    }



    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {

        highlightedPosition.layoutXProperty().bind(Bindings.createDoubleBinding( () -> {
            return mousePositionOnProfileProperty().get();
        }, position));
        highlightedPosition.startYProperty().bind(Bindings.select(rectangle, "minY"));
        highlightedPosition.endYProperty().bind(Bindings.select(rectangle, "maxY"));
        highlightedPosition.visibleProperty().bind(position.greaterThanOrEqualTo(0));


        //TODO
        return null;
    }




    public Pane pane() {
        return borderPane;
    }
}
