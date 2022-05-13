package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;


//TODO demander si ok si je fais un addAll quand je bind les children
public final class ElevationProfileManager {
    ReadOnlyObjectProperty<ElevationProfile> profilePrinted;
    ReadOnlyDoubleProperty position;
    //1
    private BorderPane borderPane;
    private Pane pane;
    private VBox pane2;
    private Path path;
    private Polygon polygon;

    private Group group;
    private Text text1, text2, text3;

    //2
    private Insets distanceRectangle;

    //3
    private ObjectProperty<Rectangle2D> rectangleBleu;
    private ObjectProperty<Transform> screenToWorld, worldToScreen;

    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> profilePrinted, ReadOnlyDoubleProperty position) {
        this.profilePrinted = profilePrinted;
        this.position = position;

        //création de l'interface
        borderPane = new BorderPane();
        pane = new Pane();
        pane2 = new VBox();
        path = new Path();

        group = new Group();
        text1 = new Text();
        text2 = new Text();

        polygon = new Polygon();

        double x2 = profilePrinted.get().length() / 1000;

        polygon.getPoints().addAll(0.0, 0.0,
                0.1, 0.1,
                0.2, 0.2
        );

        text3 = new Text();

        pane2.setId("profile_data");
        polygon.setId("profile");
        path.setId("grid");

        borderPane.getStylesheets().add("elevation_profile.css");

        text1.getStyleClass().addAll("grid_label", "horizontal");
        text2.getStyleClass().addAll("grid_label", "vertical");

        group.getChildren().addAll(text1, text2);
        pane.getChildren().addAll(path, group, polygon);
        pane2.getChildren().add(text3);

        borderPane.centerProperty().set(pane);
        borderPane.bottomProperty().set(pane2);

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
    }



    public ReadOnlyObjectProperty mousePositionOnProfileProperty() {
       //TODO
        return null;
    }

    public Pane pane() {
        return borderPane;
    }
}
