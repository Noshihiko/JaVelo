package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import com.sun.javafx.collections.ImmutableObservableList;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.scene.transform.*;

import java.util.StringJoiner;


public final class ElevationProfileManager {
    private final ReadOnlyObjectProperty<ElevationProfile> profilePrinted;
    ReadOnlyDoubleProperty position;
    //1
    private final BorderPane borderPane;
    private final Pane pane;
    private final VBox pane2;
    private final Path grid;
    private final Polygon polygon;
    private final Group group;
    private final Text etiquette1, etiquette2, etiquette3;
    private final Line highlightedPosition;
    private final Text statistics;
    private final Insets distanceRectangle;

    private final ObservableList<PathElement> gridUpdate;
    private ObservableList<Double> pointPolygone = null;
    private ObjectProperty<Rectangle2D> rectangle;
    private final ObjectProperty<Transform> screenToWorld = new SimpleObjectProperty<>(new Affine());
    private final ObjectProperty<Transform> worldToScreen = new SimpleObjectProperty<>(new Affine());


    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> profilePrinted, ReadOnlyDoubleProperty position) {
        this.profilePrinted = profilePrinted;
        this.position = position;

        //TODO c'est pas ca : comment on l'initialise
       gridUpdate = new ImmutableObservableList<>();

        //Rectangle contenant le profil
        distanceRectangle = new Insets(10, 10, 20, 40);

        //création de l'interface

        grid = new Path();
        polygon = new Polygon();
        highlightedPosition = new Line();

        etiquette1 = new Text();
        etiquette2 = new Text();
        etiquette3 = new Text();
        statistics = new Text();

        group = new Group(etiquette1, etiquette2);
        pane = new Pane(grid, group, polygon);
        pane2 = new VBox(etiquette3);
        borderPane = new BorderPane(pane,null,null,pane2,null);

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
        highlightedPosition.layoutXProperty().bind(Bindings.createDoubleBinding( () -> {
            return mousePositionOnProfileProperty().get();
        }, position));
        highlightedPosition.startYProperty().bind(Bindings.select(rectangle, "minY"));
        highlightedPosition.endYProperty().bind(Bindings.select(rectangle, "maxY"));
        highlightedPosition.visibleProperty().bind(position.greaterThanOrEqualTo(0));

        gridCreation();
        statisticsText();

    //******************************* Gestion des événements **************************
        //détecte les mouvements du pointeur de la souris lorsqu'elle survole ce panneau
        pane.setOnMouseMoved(event ->{
            double x = event.getX();
            double y = event.getY();
           // mousePositionOnProfileProperty(x, y);
            //TODO
        });

        //détecte la sortie du pointeur de la souris du panneau
        pane.setOnMouseExited(event -> {
            //TODO
        });

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

    private void gridCreation(){
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
        }
        for (int i = 1; i < rectangle.get().getWidth()/distanceInBetweenWidth; ++i){
            gridUpdate.add(new MoveTo(i,0));
            gridUpdate.add(new LineTo(i,rectangle.get().getHeight()));
        }
        grid.getElements().setAll(gridUpdate);
    }


    private void setWorldToScreen(Point2D p1, Point2D p2, Point2D p1prime, Point2D p2prime) throws NonInvertibleTransformException {

        Affine transformationAffine = new Affine();
        screenToWorld.set(transformationAffine);
        worldToScreen.set(screenToWorld.get().createInverse());

    }


    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {

        //  la position du pointeur de la souris
        // le long du profil (en mètres, arrondie à l'entier le plus proche),
        // ou NaN si le pointeur de la
        // souris ne se trouve pas au-dessus du profil
        //TODO
        return null;
    }


    public Pane pane() {
        return borderPane;
    }
}
