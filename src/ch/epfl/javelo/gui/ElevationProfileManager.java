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
    private final Text stats = new Text();
    private final Line annotationLine = new Line();

    private final DoubleProperty mousePosition = new SimpleDoubleProperty(Double.NaN);

    private final Group newGroupEtiquette = new Group();
    private final VBox pane2 = new VBox(stats);
    private final Pane pane = new Pane(grid, polygon, newGroupEtiquette, annotationLine);
    private final BorderPane borderPane = new BorderPane(pane, null, null, pane2, null);

    private final Insets distanceRectangle = new Insets(10, 10, 20, 40);

    private final ObservableList<PathElement> gridUpdate = FXCollections.observableArrayList();
    private final ObjectProperty<Rectangle2D> rectangle = new SimpleObjectProperty<>(Rectangle2D.EMPTY);
    private final ObjectProperty<Transform> screenToWorld = new SimpleObjectProperty<>(new Affine());
    private final ObjectProperty<Transform> worldToScreen = new SimpleObjectProperty<>(new Affine());

    private Point2D point1, point2, point1Prime, point2Prime;

    private final static int METERS_TO_KM_CONVERSION = 1000;
    private final static int PIXEL_X_MINIMUM_DISTANCE = 50;
    private final static int PIXEL_Y_MINIMUM_DISTANCE = 25;
    private final static int TEXT_GRID_SIZE = 10;
    private final static int TEXT_GRID_HEIGHT_X = 2;
    private final static int TEXT_GRID_HEIGHT_Y = 0;

    private final static String TEXT_GRID_FONT = "Avenir";
    private final static String TEXT_DIRECTION_Y = "horizontal";
    private final static String TEXT_DIRECTION_X = "vertical";
    private final static String TEXT_NAME = "grid_label";


    private final static int[] POS_STEPS =
            {1_000, 2_000, 5_000, 10_000, 25_000, 50_000, 100_000};
    private final static int[] ELE_STEPS =
            {5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000};


    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> profilePrinted,
                                   ReadOnlyDoubleProperty position) {
        this.profilePrinted = profilePrinted;
        this.position = position;

        pane2.setId("profile_data");
        polygon.setId("profile");
        grid.setId("grid");

        borderPane.getStylesheets().add("elevation_profile.css");

        //********************************* Listener **********************************
        rectangle.addListener((p, oldS, newS) -> begin());

        profilePrinted.addListener((p, oldScene, newScene) -> begin());

        //***************************** Position de la souris *****************************
        pane.setOnMouseMoved(event -> {
            if (rectangle.get().contains(event.getX(), event.getY())) {
                Point2D mouseIRL = screenToWorld.get().transform(event.getX(), event.getY());
                mousePosition.set(Math.rint(mouseIRL.getX()));
            } else
                mousePosition.set(Double.NaN);
        });

        pane.setOnMouseExited(event -> mousePosition.set(Double.NaN));

        //********************************** Binding **********************************
        //de la ligne en gras
        annotationLine.layoutXProperty().bind(Bindings.createDoubleBinding(() ->
                worldToScreen.get().transform(position.get(), 0).getX(), worldToScreen, position));
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
     * Méthode appelée dans les addListeners qui appellent les autres méthodes créant l'interface.
     */
    private void begin() {
        if (profilePrinted.isNotNull().get()) {
            pointsAffines();
            setScreenToWorld();
            setWorldToScreen();
            gridAndEtiquetteCreation();
            statisticsText();
            profileCreation();
        }
    }

    /**
     * Ajout de tous les points au polygone.
     */
    private void profileCreation() {
        polygon.getPoints().clear();

        for (int i = (int) rectangle.get().getMinX(); i < rectangle.get().getMaxX(); ++i) {
            double xElevationAt = screenToWorld.get().transform(i, 0).getX();
            polygon.getPoints().add((double) i);
            polygon.getPoints().add(worldToScreen.get().transform(
                    0,
                    profilePrinted.get().elevationAt(xElevationAt)).getY());
        }

        polygon.getPoints().addAll(rectangle.get().getMaxX(), rectangle.get().getMaxY(),
                rectangle.get().getMinX(), rectangle.get().getMaxY());
    }

    /**
     * Ajout du texte contenant les statistiques.
     */
    private void statisticsText() {
        if (profilePrinted == null) return;
        String statistic = String.format("Longueur : %.1f km" +
                        "     Montée : %.0f m" +
                        "     Descente : %.0f m" +
                        "     Altitude : de %.0f m à %.0f m",

                profilePrinted.get().length() / METERS_TO_KM_CONVERSION,
                profilePrinted.get().totalAscent(),
                profilePrinted.get().totalDescent(),
                profilePrinted.get().minElevation(),
                profilePrinted.get().maxElevation()
        );
        stats.setText(statistic);
    }

    /**
     * Création de la grille et des étiquettes.
     */
    private void gridAndEtiquetteCreation() {
        grid.getElements().removeAll();
        gridUpdate.clear();
        newGroupEtiquette.getChildren().clear();

        double distanceInBetweenWidth = 0;
        double distanceInBetweenHeight = 0;

        //distance entre les lignes verticales
        int spacing = 0;
        double distanceInBetweenWidthPixels = 0;
        while (distanceInBetweenWidthPixels < PIXEL_X_MINIMUM_DISTANCE && spacing < POS_STEPS.length) {
            distanceInBetweenWidth = POS_STEPS[spacing];
            distanceInBetweenWidthPixels = worldToScreen.get().deltaTransform(distanceInBetweenWidth, 0).getX();
            spacing += 1;
        }

        //distance entre les lignes horizontales
        spacing = 0;
        double distanceInBetweenHeightPixels = 0;
        while (distanceInBetweenHeightPixels < PIXEL_Y_MINIMUM_DISTANCE && spacing < ELE_STEPS.length) {
            distanceInBetweenHeight = ELE_STEPS[spacing];
            distanceInBetweenHeightPixels = worldToScreen.get().deltaTransform(0, -distanceInBetweenHeight).getY();
            spacing += 1;
        }

        //Création des lignes de la grille selon les y
        int firstY = (int) (Math.ceil(profilePrinted.get().minElevation() / distanceInBetweenHeight) * distanceInBetweenHeight);
        for (int i = firstY; i <= profilePrinted.get().maxElevation(); i += distanceInBetweenHeight) {
            double yGrid = worldToScreen.get().transform(0, i).getY();

            gridUpdate.add(new MoveTo(rectangle.get().getMinX(), yGrid));
            gridUpdate.add(new LineTo(rectangle.get().getMaxX(), yGrid));

            Text text = new Text(distanceRectangle.getBottom(), yGrid - TEXT_GRID_SIZE, Integer.toString(i));

            text.setTextOrigin(VPos.TOP);
            text.prefWidth(TEXT_GRID_HEIGHT_Y);

            text.getStyleClass().addAll(TEXT_NAME, TEXT_DIRECTION_Y);
            text.setFont(Font.font(TEXT_GRID_FONT, TEXT_GRID_SIZE));
            newGroupEtiquette.getChildren().add(text);
        }

        //selon les x
        for (int i = 0; i <= profilePrinted.get().length(); i += distanceInBetweenWidth) {
            double xGrid = worldToScreen.get().transform(i, 0).getX();

            gridUpdate.add(new MoveTo(xGrid, rectangle.get().getMinY()));
            gridUpdate.add(new LineTo(xGrid, rectangle.get().getMaxY()));

            Text text = new Text(
                    xGrid,
                    rectangle.get().getMaxY() + TEXT_GRID_SIZE,
                    Integer.toString(i / METERS_TO_KM_CONVERSION));

            text.setTextOrigin(VPos.CENTER);
            text.prefWidth(TEXT_GRID_HEIGHT_X);

            text.getStyleClass().addAll(TEXT_NAME, TEXT_DIRECTION_X);
            text.setFont(Font.font(TEXT_GRID_FONT, TEXT_GRID_SIZE));
            newGroupEtiquette.getChildren().add(text);
        }
        grid.getElements().setAll(gridUpdate);
    }

    /**
     * Méthode qui permet de passer du système de coordonnées du panneau JavaFX contenant le rectangle bleu au système
     * de coordonnées du « monde réel ».
     */
    private void setScreenToWorld() {
        Affine transformationAffine = new Affine();

        double scaleX = (point2Prime.getX() - point1Prime.getX()) / (point2.getX() - point1.getX());
        double scaleY = (point1Prime.getY() - point2Prime.getY()) / (point2.getY() - point1.getY());

        transformationAffine.prependTranslation(
                - distanceRectangle.getLeft(),
                - point1.getY() - rectangle.get().getMinY());
        transformationAffine.prependScale(scaleX, scaleY);
        transformationAffine.prependTranslation(0, point2Prime.getY());

        screenToWorld.set(transformationAffine);
    }

    /**
     * Méthode qui permet de passer du système de coordonnées du « monde réel » au système de coordonnées du panneau
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

    /**
     * Méthode calculant les points pour les affines
     */
    private void pointsAffines(){
        double minElevation = profilePrinted.get().minElevation();
        double maxElevation = profilePrinted.get().maxElevation();

        point1 = new Point2D(0, rectangle.get().getHeight());
        point2 = new Point2D(rectangle.get().getWidth(), 0);

        point1Prime = new Point2D(0, maxElevation);
        point2Prime = new Point2D(profilePrinted.get().length(), minElevation);
    }
}
