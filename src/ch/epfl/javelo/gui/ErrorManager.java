package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;


public final class ErrorManager {
    private final VBox errorMessage;
    private final Text text;
    private final SequentialTransition seqTransition;

    public ErrorManager() {
        errorMessage = new VBox();
        text = new Text();
        errorMessage.getStylesheets().add("error.css");
        errorMessage.getChildren().add(text);

        FadeTransition appearTransition = new FadeTransition(Duration.millis(200), errorMessage);
        appearTransition.setFromValue(0);
        appearTransition.setToValue(0.8);
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(2000));
        FadeTransition disappearTransition = new FadeTransition(Duration.millis(500), errorMessage);
        disappearTransition.setFromValue(0.8);
        disappearTransition.setToValue(0);
        seqTransition = new SequentialTransition(appearTransition, pauseTransition, disappearTransition);

        errorMessage.setMouseTransparent(true);
    }

    public Pane pane() {
        return this.errorMessage;
    }

    public void displayError(String message) {
        java.awt.Toolkit.getDefaultToolkit().beep();
        text.setText(message);
        seqTransition.stop();
        seqTransition.play();
    }
}
