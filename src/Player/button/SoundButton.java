package Player.button;

import Player.Main;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaPlayer;

public class SoundButton extends Button {
    final private ImageView icon = new ImageView(Main.class.getResource("resources/baseline_volume_up_white_18dp.png").toString());
    final private ImageView iconHover = new ImageView(Main.class.getResource("resources/baseline_volume_up_white_18dp_hover.png").toString());
    final private ImageView fIcon = new ImageView(Main.class.getResource("resources/baseline_volume_forbid_white_18dp.png").toString());
    final private ImageView fIconHover = new ImageView(Main.class.getResource("resources/baseline_volume_forbid_white_18dp_hover.png").toString());
    private boolean forbid;
    private MediaPlayer player;

    public SoundButton(DoubleBinding bind) {
        forbid = false;
        setGraphic(icon);
        getStylesheets().add(Main.class.getResource("resources/SoundButton.css").toExternalForm());
        setOnMouseEntered(mouseEvent -> {
            if (!forbid)
                setGraphic(iconHover);
            else
                setGraphic(fIconHover);
        });
        setOnMouseExited(mouseEvent -> {
            if (!forbid)
                setGraphic(icon);
            else
                setGraphic(fIcon);
        });
        setOnMouseClicked(mouseEvent -> {
            forbid = !forbid;
            if (forbid) {
                setGraphic(fIcon);
                if (player != null) {
                    player.volumeProperty().unbind();
                    player.setVolume(0);
                }
            } else {
                setGraphic(icon);
                if (player != null) {
                    player.volumeProperty().bind(bind);
                }
            }
        });
    }

    public void setPlayer(MediaPlayer player) {
        this.player = player;
    }
}
