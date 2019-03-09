package Player.button;

import Player.Main;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;


public class PlayButton extends Button {

    public enum ButtonType {PLAY, PAUSE}

    public PlayButton(ButtonType type) {
        ImageView play = new ImageView(Main.class.getResource("resources/outline_play_arrow_white_18dp.png").toString());
        ImageView playHover = new ImageView(Main.class.getResource("resources/outline_play_arrow_white_18dp_hover.png").toString());
        ImageView pause = new ImageView(Main.class.getResource("resources/outline_pause_white_18dp.png").toString());
        ImageView pauseHover = new ImageView(Main.class.getResource("resources/outline_pause_white_18dp_hover.png").toString());

        if(type == ButtonType.PLAY){
//            setText("\u25B7");
            setGraphic(play);
            setOnMouseEntered(mouseEvent -> setGraphic(playHover));
            setOnMouseExited(mouseEvent -> setGraphic(play));
            setPadding(new Insets(0, 0, 0, 0));
            getStyleClass().add("play");
        }
        else {
//            setText("\u23f8");
            setGraphic(pause);
            setOnMouseEntered(mouseEvent -> setGraphic(pauseHover));
            setOnMouseExited(mouseEvent -> setGraphic(pause));
            setPadding(new Insets(0, 0, 0, 0));
            getStyleClass().add("pause");
        }
        getStylesheets().add(Main.class.getResource("resources/PlayButton.css").toExternalForm());
        setMinSize(USE_PREF_SIZE,USE_PREF_SIZE);
        setMaxSize(USE_PREF_SIZE,USE_PREF_SIZE);
    }

}
