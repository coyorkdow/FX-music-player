package Player;

import com.jfoenix.controls.JFXSlider;
import javafx.beans.binding.Bindings;

import java.text.DecimalFormat;

class TimeSlider extends JFXSlider {
    TimeSlider(double min, double max, double value) {
        super(min, max, value);
        setBlockIncrement(1f);
        setValueFactory(slider -> Bindings.createStringBinding(() -> {
            int time = (int) (slider.getValue() + 0.5);
            return new DecimalFormat("00:").format(time / 60) +
                    new DecimalFormat("00").format(time % 60);
        }, slider.valueProperty()));

        getStylesheets().add(Main.class.getResource("resources/TimeSlider.css").toExternalForm());
    }
}
