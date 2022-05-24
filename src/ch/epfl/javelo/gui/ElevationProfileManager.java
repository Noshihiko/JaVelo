
package ch.epfl.javelo.gui;


import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;

import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.*;

import java.util.StringJoiner;


public final class ElevationProfileManager {
    private final ReadOnlyObjectProperty<ElevationProfile> profilePrinted;
    private final ReadOnlyDoubleProperty position;

    //1
    private final BorderPane borderPane;
    private final Pane pane;
    private final VBox pane2;
    private final Path grid;
    private final Polygon polygon;
    private final Group group;
    private final Text etiquette1, etiquette2, etiquette3;
    private final Line annotationLine;
    private final Insets distanceRectangle;
    private final Group newGroupEtiquettes;

    private final ObservableList<PathElement> gridUpdate;
    private ObservableList<Double> pointPolygone;
    private ObjectProperty<Rectangle2D> rectangle;
    private final ObjectProperty<Transform> screenToWorld = new SimpleObjectProperty<>(new Affine());
    private final ObjectProperty<Transform> worldToScreen = new SimpleObjectProperty<>(new Affine());
    private final ReadOnlyDoubleProperty mousePosition;

    private final Point2D p1, p2, p1prime, p2prime;


    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> profilePrinted, ReadOnlyDoubleProperty position) throws NonInvertibleTransformException {
        //TODO vérifier si c'est ça qu'il veut que l'on fasse
        if (profilePrinted.get().totalDescent() == 0) {
            this.profilePrinted = null;
        } else {
            this.profilePrinted = profilePrinted;
        }
        //TODO faut il faire un check si position = 0 ?
        this.position = position;

        //TODO vérifier si c'est ok
       gridUpdate = FXCollections.observableArrayList();

        //Distance du rectangle contenant le profil avec le panneau
        distanceRectangle = new Insets(10, 10, 20, 40);
        //TODO COMMENT ON FAIT rectangle ? faut il mettre le insets dedans?
        rectangle = new SimpleObjectProperty<>();
        rectangle.set(Rectangle2D.EMPTY);

        //création de l'interface
        mousePosition = new SimpleDoubleProperty();
        grid = new Path();
        polygon = new Polygon();
        annotationLine = new Line();

        etiquette1 = new Text();
        etiquette2 = new Text();
        etiquette3 = new Text();

        group = new Group(etiquette1, etiquette2);
        pane = new Pane(grid, group, polygon);
        pane2 = new VBox(etiquette3);
        borderPane = new BorderPane(pane,null,null,pane2,null);
        newGroupEtiquettes = new Group();
        pane().getChildren().add(newGroupEtiquettes);

        pointPolygone = FXCollections.observableArrayList();
        //Ajout de tous les points au polygone
        for (int i = 0; i <= rectangle.get().getWidth(); ++i) {
            pointPolygone.addAll((double)i, profilePrinted.get().elevationAt(i),
                    distanceRectangle.getLeft(), profilePrinted.get().minElevation(),
                    distanceRectangle.getRight(), profilePrinted.get().minElevation());
        }
        polygon.getPoints().setAll(pointPolygone);

        pane2.setId("profile_data");
        polygon.setId("profile");
        grid.setId("grid");

        borderPane.getStylesheets().add("elevation_profile.css");

        etiquette1.getStyleClass().addAll("grid_label", "horizontal");
        etiquette2.getStyleClass().addAll("grid_label", "vertical");

        //binding des éléments de la ligne en gras
        annotationLine.layoutXProperty().bind(Bindings.createDoubleBinding( () -> {
            return mousePositionOnProfileProperty().get();
        }, position));
        annotationLine.startYProperty().bind(Bindings.select(rectangle, "minY"));
        annotationLine.endYProperty().bind(Bindings.select(rectangle, "maxY"));
        annotationLine.visibleProperty().bind(position.greaterThanOrEqualTo(0));


        //calcul des différentes distances entre les lignes horizontales et verticales de la grille
        //tblo en constant
        int[] POS_STEPS =
                { 1_000, 2_000, 5_000, 10_000, 25_000, 50_000, 100_000 };
        int[] ELE_STEPS =
                { 5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000 };

        gridAndEtiquetteCreation();
        statisticsText();

        //détecte les mouvements du pointeur de la souris lorsqu'elle survole ce panneau
        pane.setOnMouseMoved(event ->{
            Point2D mouseIRL = screenToWorld.get().deltaTransform(event.getX(), event.getY());
            mousePositionOnProfileProperty().add((int)mouseIRL.getX());
            mousePositionOnProfileProperty().add((int)mouseIRL.getY());
            //modifier les valeurs de la propriété mousePositionOnProfileProperty
        });

        //détecte la sortie du pointeur de la souris du panneau
        pane.setOnMouseExited(event -> {
            mousePositionOnProfileProperty().add(Double.NaN);
            // faut mettre la propriété
            // mousePositionOnProfileProperty en NaN
        });


    //******************************* Transformations *********************************
        double minElevation = profilePrinted.get().minElevation();
        double maxElevation = profilePrinted.get().maxElevation();

        p1 = new Point2D(0, rectangle.get().getMaxY());
        p2 = new Point2D(rectangle.get().getMaxX(), 0);

        p1prime = new Point2D(0, maxElevation);
        p2prime = new Point2D(profilePrinted.get().length(), minElevation);

        setScreenToWorld();
        setWorldToScreen();
    }



    private void statisticsText(){
        if (profilePrinted != null) {
            StringJoiner statistic = new StringJoiner("     ");

            statistic.add("Longueur : %.1f km" + profilePrinted.get().length() / 1000);
            statistic.add("Montée : %.0f m" + profilePrinted.get().totalAscent());
            statistic.add("Descente : %.0f m" + profilePrinted.get().totalDescent());
            statistic.add("Altitude : de %.0f m à %.0f m" + profilePrinted.get().minElevation() + profilePrinted.get().maxElevation());
            etiquette3.setId(statistic.toString());
        }
    }

    private void gridAndEtiquetteCreation(){
        //calcul des différentes distances entre les lignes horizontales et verticales de la grille
        int[] POS_STEPS =
                { 1_000, 2_000, 5_000, 10_000, 25_000, 50_000, 100_000 };
        int[] ELE_STEPS =
                { 5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000 };
        double distanceInBetweenWidth = 0;
        double distanceInBetweenHeight = 0;

        //lignes verticales
        for (int i = 0; i < POS_STEPS.length; ++i) {
            distanceInBetweenWidth = worldToScreen.get().deltaTransform(POS_STEPS[i],0).getX();
            if (distanceInBetweenWidth >=25) {
                break;
            }
        }
        //lignes horizontales
        for (int i = 0; i < ELE_STEPS.length; ++i) {
            distanceInBetweenHeight = worldToScreen.get().deltaTransform(0,ELE_STEPS[i]).getY();
            if (distanceInBetweenHeight >=50) {
                break;
            }
        }

        //Création des lignes de la grille
        for (int i = 1; i < rectangle.get().getHeight()/distanceInBetweenHeight; ++i){
            gridUpdate.add(new MoveTo(0,i));
            gridUpdate.add(new LineTo(rectangle.get().getWidth(),i));


            Text text = new Text(0, i, Double.toString(profilePrinted.get().minElevation() + (distanceInBetweenHeight*i)));

            text.prefWidth(0);

            text.setTextOrigin(VPos.TOP);
            text.getStyleClass().addAll("grid_label", "horizontal");
            text.setFont(Font.font("Avenir", 10));
            newGroupEtiquettes.getChildren().add(text);
        }

        for (int i = 1; i < rectangle.get().getWidth()/distanceInBetweenWidth; ++i){
            gridUpdate.add(new MoveTo(i,0));
            gridUpdate.add(new LineTo(i,rectangle.get().getHeight()));

            Text text = new Text(i, 0, Double.toString(screenToWorld.get().deltaTransform(i,0).getX()));

            text.setTextOrigin(VPos.TOP);
            text.getStyleClass().addAll("grid_label", "horizontal");
            text.setFont(Font.font("Avenir", 10));
            newGroupEtiquettes.getChildren().add(text);
        }
        grid.getElements().setAll(gridUpdate);
    }

    private void setWorldToScreen() throws NonInvertibleTransformException {
        Affine transformationAffine = new Affine();
        screenToWorld.set(transformationAffine);
        worldToScreen.set(screenToWorld.get().createInverse());

    }

    private void setScreenToWorld() {

        Affine transformationAffine = new Affine();

        transformationAffine.prependTranslation(-10, -40);
        double sx = (p1prime.getX() - p2prime.getX()) / (p2.getX() - p1.getX());
        double sy = (p1prime.getY() - p2prime.getY()) / (p2.getY() - p1.getY());
        transformationAffine.prependScale(sx, sy);
        transformationAffine.prependTranslation(0, p1prime.getX());

        screenToWorld.set(transformationAffine);
    }

    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return mousePosition;
    }

    public Pane pane() {
        return borderPane;
    }
}
