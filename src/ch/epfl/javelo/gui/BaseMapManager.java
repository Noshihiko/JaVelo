package ch.epfl.javelo.gui;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Order;


import static javafx.application.Application.launch;

public final class BaseMapManager {
    public boolean redrawNeeded;

    public BaseMapManager(TileManager tiles, WaypointsManager points, ObjectProperty<MapViewParameters> parameters){
        Canvas canvas = new Canvas();
        Pane pane = new Pane(canvas);
        //pane.setMinSize(); peut-être pas besoin de le faire
        //pane.setMaxSize();

        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        
    }

    /*
     pour dessin carte :
     utiliser getGraphicsContext2D
     ensuite utiliser drawImage pour dessiner les tuiles visibles
     (tuiles obtenues du gestionnaire de tuiles, si exception la tuile
     n'est pas dessinée)

     re-dessin carte :
     elle doit-être redessinée si niveau de zoom/coin haut-gauche
     change ou dimensions du canevas changent
     il faut qu'il y ait un laps d'attente, car coûteux :
     attendre le prochain battement (pulse) JavaFX :
     attribut booléen redrawNeeded true ssi un re-dessin est nécessaire
     + méthode privée donnée redrawIfNeeded()
       Méthode devant être appelée à chaque battement (constructeur : texte ajouté)
       + Méthode permettant de demander un re-dessin
     Appeler la méthode redrawOnNextPulse lorsqu'un re-dessin est nécessaire
     */

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;

        // … à faire : dessin de la carte
    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

}
