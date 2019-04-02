package Player;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

class TitleBar extends HBox {
    Region leftColour;

    TitleBar() {
        Rectangle clip = new Rectangle();
        this.setClip(clip);
        clip.widthProperty().bind(this.widthProperty());
        clip.heightProperty().bind(this.heightProperty());

        leftColour = new Region();
        leftColour.setStyle("-fx-background-color:  #e6dfda;");
        setPrefHeight(30);
        setMaxHeight(30);
        getChildren().add(leftColour);
    }
}
