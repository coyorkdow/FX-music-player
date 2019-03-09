package Player;

import com.jfoenix.controls.JFXSlider;
import javafx.beans.binding.Bindings;

import java.text.DecimalFormat;

public class TimeSlider extends JFXSlider {
    public TimeSlider(double min, double max, double value) {
        super(min, max, value);
//        setValueFactory(new Callback<JFXSlider, StringBinding>() {
////            @Override
////            public StringBinding call(JFXSlider arg0) {
////                return Bindings.createStringBinding(new java.util.concurrent.Callable<String>(){
////                    @Override
////                    public String call() throws Exception{
////                        return new DecimalFormat("#.0").format(getValue());
////                    }
////                }, valueProperty());
////            }
////        });
        setBlockIncrement(1f);
        setValueFactory(slider -> Bindings.createStringBinding(() -> {
            int time = (int) (slider.getValue() + 0.5);
            return new DecimalFormat("00:").format(time / 60) +
                    new DecimalFormat("00").format(time % 60);
        }, slider.valueProperty()));

        getStylesheets().add(Main.class.getResource("resources/TimeSlider.css").toExternalForm());
    }
}
