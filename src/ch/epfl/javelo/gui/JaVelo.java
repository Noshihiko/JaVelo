package ch.epfl.javelo.gui;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public final class JaVelo extends Application {
    private final BorderPane borderPane;
    private final SplitPane carteAndProfil;

    private final Pane map, profil, error;
    private final StackPane carteProfilAndError;


    public JaVelo() {
        borderPane = new BorderPane();

        borderPane.getChildren().add();

        carteAndProfil = new SplitPane();
        carteAndProfil.setOrientation(Orientation.VERTICAL);
        error = new Pane();
        carteProfilAndError = new StackPane(carteAndProfil, error);

        //creation de dexu panneaux fils de carte profil and error: carte et profil

        //TODO
        //if (itineraire existe) then creer aussi profil, sinon que un paneau represantat la carte

        map = new Pane();
        profil = new Pane();

        carteAndProfil.setResizableWithParent(profil, false);


        borderPane.getChildren().add(carteAndProfil);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

    }
}
