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

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;


public final class ElevationProfileManager {
    private final ReadOnlyObjectProperty<ElevationProfile> profilePrinted;
    private final ReadOnlyDoubleProperty position;

    private final Path grid = new Path();
    private final Polygon polygon = new Polygon();
    private final Text etiquette1 = new Text();
    private final Text etiquette2 = new Text();
    private final Text etiquette3 = new Text();

    private final Group newGroupEtiquettes = new Group();
    private final Group group = new Group(etiquette1, etiquette2);
    private final VBox pane2 = new VBox(etiquette3);
    private final Pane pane = new Pane(grid, group, polygon, newGroupEtiquettes);
    private final BorderPane borderPane = new BorderPane(pane,null,null,pane2,null);

    private final Line annotationLine = new Line();
    private final Insets distanceRectangle = new Insets(10, 10, 20, 40);

    private final ObservableList<PathElement> gridUpdate = FXCollections.observableArrayList();
    private List<Object> pointPolygone = new ArrayList<>();
    private ObjectProperty<Rectangle2D> rectangle = new SimpleObjectProperty<>(Rectangle2D.EMPTY);
    private final ObjectProperty<Transform> screenToWorld = new SimpleObjectProperty<>(new Affine());
    private final ObjectProperty<Transform> worldToScreen = new SimpleObjectProperty<>(new Affine());

    private final Point2D p1, p2, p1prime, p2prime;

    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> profilePrinted, ReadOnlyDoubleProperty position) {
        this.profilePrinted = profilePrinted;
        this.position = position;

        pane2.setId("profile_data");
        polygon.setId("profile");
        grid.setId("grid");

        borderPane.getStylesheets().add("elevation_profile.css");

        etiquette1.getStyleClass().addAll("grid_label", "horizontal");
        etiquette2.getStyleClass().addAll("grid_label", "vertical");
      //  gridAndEtiquetteCreation();
        //statisticsText();
        polygonePoints();


    //***************************** Transformations *******************************
        double minElevation = profilePrinted.get().minElevation();
        double maxElevation = profilePrinted.get().maxElevation();

        p1 = new Point2D(0, rectangle.get().getMaxY());
        p2 = new Point2D(rectangle.get().getMaxX(), 0);

        p1prime = new Point2D(0, maxElevation);
        p2prime = new Point2D(profilePrinted.get().length(), minElevation);

        setScreenToWorld();
        setWorldToScreen();


    //********************************* Listener **********************************
        rectangle.addListener( o -> {
         //   gridAndEtiquetteCreation();
          //  statisticsText();
            polygonePoints();
        });

        profilePrinted.addListener( o -> {
            //gridAndEtiquetteCreation();
            //statisticsText();
            polygonePoints();
        });


    //********************************** Binding **********************************
        //de la ligne en gras
        annotationLine.layoutXProperty().bind(Bindings.createDoubleBinding( () -> {
            return mousePositionOnProfileProperty().get();
        }, position));
        annotationLine.startYProperty().bind(Bindings.select(rectangle, "minY"));
        annotationLine.endYProperty().bind(Bindings.select(rectangle, "maxY"));
        annotationLine.visibleProperty().bind(position.greaterThanOrEqualTo(0));

        //du rectangle
        rectangle.bind(Bindings.createObjectBinding( () -> {
            double conditionWidth = pane.getWidth() - (distanceRectangle.getLeft() + distanceRectangle.getRight());
            double conditionHeight = pane.getHeight() - (distanceRectangle.getBottom() + distanceRectangle.getTop());

            return new Rectangle2D(distanceRectangle.getLeft(), distanceRectangle.getTop(),
                    (conditionWidth <= 0) ? 0 : conditionWidth, (conditionHeight <= 0) ? 0 : conditionHeight
            );
        }, pane.heightProperty(), pane.widthProperty()));
    }


    //Ajout de tous les points au polygone
    private void polygonePoints(){
        pointPolygone.clear();

        polygon.getPoints().addAll(rectangle.get().getMinX(), rectangle.get().getMaxY(),
                rectangle.get().getMaxX(), rectangle.get().getMaxY());
        for (int i = 0; i <= rectangle.get().getWidth(); ++i) {
            double xElevationAt = screenToWorld.get().transform(i,0).getX();
            polygon.getPoints().add(i, worldToScreen.get().transform(xElevationAt, profilePrinted.get().elevationAt(xElevationAt)).getY());
        }


        /*
        pointPolygone.addAll(List.of(new Point2D(rectangle.get().getMinX(), rectangle.get().getMaxY()),
                new Point2D(rectangle.get().getMaxX(), rectangle.get().getMaxY())));

        for (int i = 0; i <= rectangle.get().getWidth(); ++i) {
            double xElevationAt = screenToWorld.get().transform(i,0).getX();
            pointPolygone.add(new Point2D(i, worldToScreen.get().transform(xElevationAt, profilePrinted.get().elevationAt(xElevationAt)).getY()));
        }


       polygon.getPoints().setAll(pointPolygone);

         */
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


        //TODO ne faudrait il pas mieux faire une boucle while
        //lignes verticales
        /*
        for (int i = 0; i < POS_STEPS.length; ++i) {
            distanceInBetweenWidth = worldToScreen.get().deltaTransform(POS_STEPS[i],0).getX();
            if (distanceInBetweenWidth >= 25) {
                break;
            }
        }
         */
       int k = 0;
       while (distanceInBetweenWidth < 25 && k < POS_STEPS.length) {
           distanceInBetweenWidth = worldToScreen.get().deltaTransform(POS_STEPS[k],0).getX();
           k += 1;
       }

       k = 0;
        while (distanceInBetweenHeight < 50 && k < ELE_STEPS.length) {
            distanceInBetweenHeight = worldToScreen.get().deltaTransform(0,ELE_STEPS[k]).getY();
            k += 1;
        }

        /*
        //lignes horizontales
        for (int i = 0; i < ELE_STEPS.length; ++i) {
            distanceInBetweenHeight = worldToScreen.get().deltaTransform(0, ELE_STEPS[i]).getY();
            if (distanceInBetweenHeight >= 50) {
                break;
            }
        }

         */

        //Création des lignes de la grille
        for (int i = 1; i < rectangle.get().getHeight()/distanceInBetweenHeight; ++i){
            gridUpdate.add(new MoveTo(distanceRectangle.getLeft(), i * distanceInBetweenHeight));
            gridUpdate.add(new LineTo(rectangle.get().getWidth() - distanceRectangle.getRight(), i * distanceInBetweenHeight));

            Text text = new Text(0, i, Integer.toString((int)((profilePrinted.get().minElevation() + (distanceInBetweenHeight * i) / 1000))));

            text.setTextOrigin(VPos.TOP);
            text.prefWidth(0);

            text.getStyleClass().addAll("grid_label", "horizontal");
            text.setFont(Font.font("Avenir", 10));
            newGroupEtiquettes.getChildren().add(text);

            grid.getElements().setAll(gridUpdate);
        }

        //TODO verifier si c'est les bonnes infos pour étiquettes
        // et on inverse pas width et height
        // jpense qu'il y a un pb dans le calcul de la distance
        for (int i = 1; i < rectangle.get().getWidth() / distanceInBetweenWidth; ++i){
            gridUpdate.add(new MoveTo(i * distanceInBetweenWidth,distanceRectangle.getBottom()));
            gridUpdate.add(new LineTo(i * distanceInBetweenWidth, rectangle.get().getHeight() - distanceRectangle.getTop()));

            Text text = new Text(i, 0, Integer.toString((int)screenToWorld.get().deltaTransform(i,0).getX()));


            text.setTextOrigin(VPos.CENTER);
            text.prefWidth(text.getX() + 2);
            text.getStyleClass().addAll("grid_label", "vertical");
            text.setFont(Font.font("Avenir", 10));
            newGroupEtiquettes.getChildren().add(text);
            grid.getElements().setAll(gridUpdate);
        }
        //grid.getElements().setAll(gridUpdate);
    }

    private void setWorldToScreen(){
        Affine transformationAffine = new Affine();

        try {
            screenToWorld.set(transformationAffine);
            worldToScreen.set(screenToWorld.get().createInverse());
        } catch (NonInvertibleTransformException e) {
            e.printStackTrace();
        }
    }

    //TODO changer nom sx et sy
    // changer nom p1prime et p2prime et p1 et p2
    // ce serait un pb de -10 et -40 ?
    private void setScreenToWorld() {
        Affine transformationAffine = new Affine();

        double sx = (p1prime.getX() - p2prime.getX()) / (p2.getX() - p1.getX());
        double sy = (p1prime.getY() - p2prime.getY()) / (p2.getY() - p1.getY());

        transformationAffine.prependTranslation(-10, -40);
        transformationAffine.prependScale(sx, sy);
        transformationAffine.prependTranslation(0, p1prime.getX());

        screenToWorld.set(transformationAffine);
    }

    //TODO vaut-il mieux recréer un chaque fois un doubleproperty ou alors faire doubleproperty.set(double.nan) ?
    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        DoubleProperty mousePosition = new SimpleDoubleProperty(Double.NaN);
        //mousePosition.set(Double.NaN);

        //détecte les mouvements du pointeur de la souris lorsqu'elle survole ce panneau
        pane.setOnMouseMoved(event ->{
            Point2D mouseIRL = screenToWorld.get().deltaTransform(event.getX(), event.getY());
            mousePosition.set((int)mouseIRL.getX());
            mousePosition.set((int)mouseIRL.getY());
        });

        //détecte la sortie du pointeur de la souris du panneau
        pane.setOnMouseExited(event -> {
            mousePosition.set(Double.NaN);
        });

        return mousePosition;
    }

    public Pane pane() {
        return borderPane;
    }
}
