package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Insets;
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
    private BorderPane borderPane;
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

        //Rectangle contenant le profil
        distanceRectangle = new Insets(10, 10, 20, 40);
        //1404
        //Affine affine = new Affine(worldToScreen.getValue());
        //TODO : quelles sont les valeurs à mettre ?
       /*
        affine.prependTranslation();
        affine.prependScale();
        affine.prependTranslation();

        affine.createInverse()
                */
        //3
        //TODO aled guillaume
        highlightedPosition.layoutXProperty().bind(Bindings.createDoubleBinding( () -> {
            highlightedPosition.getLayoutX()
                }, position.get()));
        highlightedPosition.startYProperty().bind(Bindings.select(rectangle, "minY"));
        highlightedPosition.endYProperty().bind(Bindings.select(rectangle, "maxY"));
        highlightedPosition.visibleProperty().bind(position.greaterThanOrEqualTo(0));

        //4
        int[] POS_STEPS =
                { 1_000, 2_000, 5_000, 10_000, 25_000, 50_000, 100_000 };
        int[] ELE_STEPS =
                { 5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000 };

    }



    public ReadOnlyObjectProperty mousePositionOnProfileProperty() {
       //TODO
        return null;
    }

    public Pane pane() {
        return borderPane;
    }
}
