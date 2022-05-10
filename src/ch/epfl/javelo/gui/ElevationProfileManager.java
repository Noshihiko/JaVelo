package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

public final class ElevationProfileManager {
    ReadOnlyObjectProperty<ElevationProfile> profilePrinted = null;
    ReadOnlyDoubleProperty position;

    private Pane pane;

    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> profilePrinted, ReadOnlyDoubleProperty position) {
        this.profilePrinted = profilePrinted;
        this.position = position;
    }



    public ObservableValue<? extends Number> mousePositionOnProfileProperty() {
       //TODO
        return null;
    }

    public Parent pane() {
        return pane;
    }
}
