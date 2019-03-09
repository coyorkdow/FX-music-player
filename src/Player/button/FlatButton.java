package Player.button;

import Player.Main;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

public class FlatButton extends Button {

    public FlatButton(){
        getStylesheets().add(Main.class.getResource("resources/FlatButton.css").toExternalForm());
    }

    public FlatButton(ImageView icon) {
        setGraphic(icon);
        getStylesheets().add(Main.class.getResource("resources/FlatButton.css").toExternalForm());
    }

    public FlatButton(String text){
        setText(text);
        getStylesheets().add(Main.class.getResource("resources/FlatButton.css").toExternalForm());
        getStyleClass().add("textbtn");
    }
}
