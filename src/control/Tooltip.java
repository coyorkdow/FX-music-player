package control;

import javafx.util.Duration;

public class Tooltip extends javafx.scene.control.Tooltip {

    public Tooltip(String s) {
        super(s);
        init();
    }

    private void init() {
        setShowDelay(new Duration(100));
        setStyle("-fx-background-color: #DAE9E9;" +
                "-fx-background-radius: 5% 5% 5% 5%;" +
                "-fx-text-fill: #272927;" +
                "-fx-font-size: 12px;"
        );
    }

}
