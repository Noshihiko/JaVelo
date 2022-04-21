package ch.epfl.javelo.gui;

import javafx.application.Platform;
import javafx.scene.Node;

public final class BaseMapManager {
    public boolean redrawNeeded;
    public Node canvas;

    public BaseMapManager(Node canvas){
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
    }

    /*la carte est dessinée sur une instance de Canvas, elle-même
    placée dans un panneau de type Pane
    Utiliser des liens JavaFX bindings pour faire en sorte que
    sa largeur et hauteur soient toujours égales à celles du panneau :
    canvas.widthProperty().bind(pane.widthProperty());

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
    public void pane(){}

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
