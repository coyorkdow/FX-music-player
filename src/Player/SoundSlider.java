package Player;

import com.jfoenix.controls.JFXSlider;

class SoundSlider extends JFXSlider {
    SoundSlider(double min, double max, double value) {
        super(min, max, value);
        setBlockIncrement(1f);
        getStylesheets().add(Main.class.getResource("resources/SoundSlider.css").toExternalForm());
    }
}
