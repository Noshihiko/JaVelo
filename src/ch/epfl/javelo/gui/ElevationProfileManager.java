package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
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

/**
 * Gère l'affichage et l'interaction avec le profil en long d'un itinéraire.
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */
public final class ElevationProfileManager {
    private final ReadOnlyObjectProperty<ElevationProfile> profilePrinted;
    private final ReadOnlyDoubleProperty position;

    private final Path grid = new Path();
    private final Polygon polygon = new Polygon();
    private final Text etiquette1 = new Text();
    private final Text etiquette2 = new Text();
    private final Text etiquette3 = new Text();
    private final Line annotationLine = new Line();

    private DoubleProperty mousePosition = new SimpleDoubleProperty(Double.NaN);

    private final Group newGroupEtiquettes = new Group();
    private final Group group = new Group(etiquette1, etiquette2);
    private final VBox pane2 = new VBox(etiquette3);
    private final Pane pane = new Pane(grid, group, polygon, newGroupEtiquettes, annotationLine);
    private final BorderPane borderPane = new BorderPane(pane, null, null, pane2, null);


    private final Insets distanceRectangle = new Insets(10, 10, 20, 40);

    private final ObservableList<PathElement> gridUpdate = FXCollections.observableArrayList();
    private List<Object> pointPolygone = new ArrayList<>();
    private ObjectProperty<Rectangle2D> rectangle = new SimpleObjectProperty<>(Rectangle2D.EMPTY);
    private final ObjectProperty<Transform> screenToWorld = new SimpleObjectProperty<>(new Affine());
    private final ObjectProperty<Transform> worldToScreen = new SimpleObjectProperty<>(new Affine());

    private Point2D p1, p2, p1prime, p2prime;

    private final static int METERS_TO_KM_CONVERSION = 1000;

    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> profilePrinted, ReadOnlyDoubleProperty position) {
        this.profilePrinted = profilePrinted;
        this.position = position;

        pane2.setId("profile_data");
        polygon.setId("profile");
        grid.setId("grid");

        borderPane.getStylesheets().add("elevation_profile.css");

        etiquette1.getStyleClass().addAll("grid_label", "horizontal");
        etiquette2.getStyleClass().addAll("grid_label", "vertical");
        //gridAndEtiquetteCreation();
        //statisticsText();
        //profileCreation();


        //********************************* Listener **********************************
        rectangle.addListener((p, oldS, newS) -> {
            if (profilePrinted.isNotNull().get()) {
                double minElevation = profilePrinted.get().minElevation();
                double maxElevation = profilePrinted.get().maxElevation();

                p1 = new Point2D(0, rectangle.get().getHeight());
                p2 = new Point2D(rectangle.get().getWidth(), 0);

                p1prime = new Point2D(0, maxElevation);
                p2prime = new Point2D(profilePrinted.get().length(), minElevation);

                setScreenToWorld();
                setWorldToScreen();
                gridAndEtiquetteCreation();
                statisticsText();
                profileCreation();
            }
        });

        profilePrinted.addListener((p, oldS, newS) -> {
            if (profilePrinted.get() == null) return;
            double minElevation = profilePrinted.get().minElevation();
            double maxElevation = profilePrinted.get().maxElevation();

            p1 = new Point2D(0, rectangle.get().getHeight());
            p2 = new Point2D(rectangle.get().getWidth(), 0);

            p1prime = new Point2D(0, maxElevation);
            p2prime = new Point2D(profilePrinted.get().length(), minElevation);

            setScreenToWorld();
            setWorldToScreen();
            gridAndEtiquetteCreation();
            statisticsText();
            profileCreation();

        });

        //***************************** Position de la souris *****************************
        pane.setOnMouseMoved(event -> {
            if (rectangle.get().contains(event.getX(), event.getY())) {
                Point2D mouseIRL = screenToWorld.get().transform(event.getX(), event.getY());
                mousePosition.set(Math.rint(mouseIRL.getX()));
            } else {
                mousePosition.set(Double.NaN);
            }
        });

        pane.setOnMouseExited(event -> mousePosition.set(Double.NaN));

        //********************************** Binding **********************************
        //de la ligne en gras
        annotationLine.layoutXProperty().bind(Bindings.createDoubleBinding(() -> worldToScreen.get().transform(position.get(), 0).getX(), worldToScreen, position));
        annotationLine.startYProperty().bind(Bindings.select(rectangle, "minY"));
        annotationLine.endYProperty().bind(Bindings.select(rectangle, "maxY"));
        annotationLine.visibleProperty().bind(position.greaterThanOrEqualTo(0));

        //du rectangle
        rectangle.bind(Bindings.createObjectBinding(() -> {
            double conditionWidth = pane.getWidth() - (distanceRectangle.getLeft() + distanceRectangle.getRight());
            double conditionHeight = pane.getHeight() - (distanceRectangle.getBottom() + distanceRectangle.getTop());

            return new Rectangle2D(distanceRectangle.getLeft(), distanceRectangle.getTop(),
                    (conditionWidth <= 0) ? 0 : conditionWidth, (conditionHeight <= 0) ? 0 : conditionHeight
            );
        }, pane.heightProperty(), pane.widthProperty()));
    }


    /**
     * Ajout de tous les points au polygone.
     */
    private void profileCreation() {
        pointPolygone.clear();
        polygon.getPoints().clear();

        for (int i = (int) rectangle.get().getMinX(); i < rectangle.get().getMaxX(); ++i) {
            double xElevationAt = screenToWorld.get().transform(i, 0).getX();
            polygon.getPoints().add((double) i);
            polygon.getPoints().add(worldToScreen.get().transform(0, profilePrinted.get().elevationAt(xElevationAt)).getY());
        }

        polygon.getPoints().addAll(rectangle.get().getMaxX(), rectangle.get().getMaxY(),
                rectangle.get().getMinX(), rectangle.get().getMaxY());
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

    /**
     * Ajout du texte contenant les statistiques.
     */
    private void statisticsText() {
        if (profilePrinted == null) return;
        //if (profilePrinted.isNotNull().get()) {
        String statistic = String.format("Longueur : %.1f km" +
                        "     Montée : %.0f m" +
                        "     Descente : %.0f m" +
                        "     Altitude : de %.0f m à %.0f m",

                profilePrinted.get().length() / METERS_TO_KM_CONVERSION, profilePrinted.get().totalAscent(), profilePrinted.get().totalDescent(), profilePrinted.get().minElevation(),
                profilePrinted.get().maxElevation()
        );

        etiquette3.setText(statistic);
        //}
    }

    /**
     * Création de la grille et des étiquettes.
     */
    private void gridAndEtiquetteCreation() {
        grid.getElements().removeAll();
        gridUpdate.clear();
        newGroupEtiquettes.getChildren().clear();

        int[] POS_STEPS =
                {1_000, 2_000, 5_000, 10_000, 25_000, 50_000, 100_000};
        int[] ELE_STEPS =
                {5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000};

        double distanceInBetweenWidth = 0;
        double distanceInBetweenHeight = 0;

        //distance entre les lignes verticales
        int k = 0;
        double distanceInBetweenWidthPixels = 0;
        while (distanceInBetweenWidthPixels < 25 && k < POS_STEPS.length) {
            distanceInBetweenWidth = POS_STEPS[k];
            distanceInBetweenWidthPixels = worldToScreen.get().deltaTransform(distanceInBetweenWidth, 0).getX();
            k += 1;
        }

        //distance entre les lignes horizontales
        k = 0;
        double distanceInBetweenHeightPixels = 0;
        while (distanceInBetweenHeightPixels < 50 && k < ELE_STEPS.length) {
            distanceInBetweenHeight = ELE_STEPS[k];
            distanceInBetweenHeightPixels = worldToScreen.get().deltaTransform(0, -distanceInBetweenHeight).getY();
            k += 1;
        }

        //Création des lignes de la grille
        //selon les y
        int firstY = (int) (Math.ceil(profilePrinted.get().minElevation() / distanceInBetweenHeight) * distanceInBetweenHeight);
        for (int i = firstY; i <= profilePrinted.get().maxElevation(); i += distanceInBetweenHeight) {
            double yGrid = worldToScreen.get().transform(0, i).getY();

            gridUpdate.add(new MoveTo(rectangle.get().getMinX(), yGrid));
            gridUpdate.add(new LineTo(rectangle.get().getMaxX(), yGrid));

            Text text = new Text(20, yGrid, Integer.toString(i));

            text.setTextOrigin(VPos.TOP);
            text.prefWidth(0);

            text.getStyleClass().addAll("grid_label", "horizontal");
            text.setFont(Font.font("Avenir", 10));
            newGroupEtiquettes.getChildren().add(text);
        }
        //todo methode pv grid update
        //selon les x
        for (int i = 0; i <= profilePrinted.get().length(); i += distanceInBetweenWidth) {
            double xGrid = worldToScreen.get().transform(i, 0).getX();

            gridUpdate.add(new MoveTo(xGrid, rectangle.get().getMinY()));
            gridUpdate.add(new LineTo(xGrid, rectangle.get().getMaxY()));

            Text text = new Text(xGrid, rectangle.get().getMaxY() + 10, Integer.toString(i/1000));

            text.setTextOrigin(VPos.CENTER);
            text.prefWidth(2);

            text.getStyleClass().addAll("grid_label", "vertical");
            text.setFont(Font.font("Avenir", 10));
            newGroupEtiquettes.getChildren().add(text);
        }
        grid.getElements().setAll(gridUpdate);
    }


    //TODO changer nom sx et sy, p1prime et p2prime et p1 et p2

    /**
     * Méthode qui permet de passer du système de coordonnées du panneau JavaFX contenant le rectangle bleu au système
     * de coordonnées du «monde réel».
     */
    private void setScreenToWorld() {
        Affine transformationAffine = new Affine();

        double sx = (p2prime.getX() - p1prime.getX()) / (p2.getX() - p1.getX());
        double sy = (p1prime.getY() - p2prime.getY()) / (p2.getY() - p1.getY());

        transformationAffine.prependTranslation(-distanceRectangle.getLeft(), -p1.getY() - rectangle.get().getMinY());
        transformationAffine.prependScale(sx, sy);
        transformationAffine.prependTranslation(0, p2prime.getY());

        screenToWorld.set(transformationAffine);
    }

    /**
     * Méthode qui permet de passer du système de coordonnées du «monde réel» au  du système de coordonnées du panneau
     * JavaFX contenant le rectangle bleu.
     */
    private void setWorldToScreen() {
        try {
            worldToScreen.set(screenToWorld.get().createInverse());
        } catch (NonInvertibleTransformException e) {
            worldToScreen.set(new Affine());
        }
    }

    /**
     * Retourne une propriété en lecture seule contenant la position du pointeur de la souris le long du profil
     * (en mètres, arrondie à l'entier le plus proche).
     *
     * @return une propriété en lecture seule contenant la position du pointeur de la souris le long du profil
     */
    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return mousePosition;
    }

    /**
     * Retourne le panneau contenant le dessin du profil.
     *
     * @return borderPane, le panneau à la racine de la hiérarchie
     */
    public Pane pane() {
        return borderPane;
    }
}
