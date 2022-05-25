package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;


public final class ErrorManager {
    //private final Pane pane;
    private final VBox errorMessage;
    private final Text text;


    public ErrorManager() {
        //pane = new Pane();
        errorMessage = new VBox();
        text = new Text();
        errorMessage.setStyle("error.css");
        errorMessage.getChildren().add(text);

        errorMessage.setMouseTransparent(true);
    }

    public Pane pane() {
        return this.errorMessage;
    }

    public void displayError(String message) {
        text.setText(message);

        FadeTransition appearTransition = new FadeTransition(Duration.millis(200), errorMessage);
        appearTransition.setFromValue(0);
        appearTransition.setToValue(0.8);
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(2000));
        FadeTransition disappearTransition = new FadeTransition(Duration.millis(500), errorMessage);
        disappearTransition.setFromValue(0.8);
        disappearTransition.setToValue(0);
        SequentialTransition seqTransition = new SequentialTransition(appearTransition, pauseTransition, disappearTransition);
        seqTransition.playFromStart();
        //seqTransition.play();
        //seqTransition.stop();

        java.awt.Toolkit.getDefaultToolkit().beep();

    }
}
