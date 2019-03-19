package Player;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

class TitleBar extends HBox {
    Region leftColour;

    TitleBar() {
        leftColour = new Region();
        leftColour.setStyle("-fx-background-color:  #e6dfda;");
        setPrefHeight(30);
        setMaxHeight(30);
        getChildren().add(leftColour);
//        setStyle("-fx-background-color:black;");
    }
}
