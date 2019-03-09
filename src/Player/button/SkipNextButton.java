package Player.button;


import Player.Main;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;


public class SkipNextButton extends Button {
    final private ImageView icon = new ImageView(Main.class.getResource("resources/outline_skip_next_white_18dp.png").toString());
    final private ImageView iconHover = new ImageView(Main.class.getResource("resources/outline_skip_next_white_18dp_hover.png").toString());

    public SkipNextButton() {
        setGraphic(icon);
        getStylesheets().add(Main.class.getResource("resources/SkipButton.css").toExternalForm());
        setOnMouseEntered(mouseEvent -> setGraphic(iconHover));
        setOnMouseExited(mouseEvent -> setGraphic(icon));
    }

}
