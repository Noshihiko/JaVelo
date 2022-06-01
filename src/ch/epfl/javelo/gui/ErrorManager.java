package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Gère l'affichage de messages d'erreur.
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */

public final class ErrorManager {
    private final VBox errorMessage;
    private final Text text;
    private final SequentialTransition seqTransition;

    private final static int APPEAR_MESSAGE_DURATION = 200;
    private final static int PAUSE_MESSAGE_DURATION = 2000;
    private final static int FADE_MESSAGE_DURATION = 500;
    private final static float OPACITY_OFF = 0f;
    private final static float OPACITY_ON = 0.8f;
    private final static String STYLE_SHEET_ERROR = "error.css";

    public ErrorManager() {
        errorMessage = new VBox();
        text = new Text();
        errorMessage.getStylesheets().add(STYLE_SHEET_ERROR);
        errorMessage.getChildren().add(text);

        FadeTransition appearTransition = new FadeTransition(Duration.millis(APPEAR_MESSAGE_DURATION), errorMessage);
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(PAUSE_MESSAGE_DURATION));
        FadeTransition disappearTransition = new FadeTransition(Duration.millis(FADE_MESSAGE_DURATION), errorMessage);
        appearTransition.setFromValue(OPACITY_OFF);
        appearTransition.setToValue(OPACITY_ON);
        disappearTransition.setFromValue(OPACITY_ON);
        disappearTransition.setToValue(OPACITY_OFF);

        seqTransition = new SequentialTransition(appearTransition, pauseTransition, disappearTransition);

        errorMessage.setMouseTransparent(true);
    }

    /**
     * Methode retournant le panneau contenant les points de passage.
     *
     * @return le panneau contenant les points de passage
     */

    public Pane pane() {
        return this.errorMessage;
    }

    /**
     * Fait apparaitre temporairement à l'écran un message d'erreur
     *
     * @param message le message d'erreur à afficher
     */

    public void displayError(String message) {
        java.awt.Toolkit.getDefaultToolkit().beep();
        text.setText(message);
        seqTransition.stop();
        seqTransition.play();
    }
}
