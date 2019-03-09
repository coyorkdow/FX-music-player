package Player.MainContent;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class InfoPane extends GridPane {

    HBox titleBox;
    Label title;
    Label artist;
    Label album;
    Label time;

    public InfoPane() {
        titleBox = new HBox();
        artist = new Label("wmr");
        album = new Label("wmr");
        time = new Label("wmr");
        title = new Label("wmr");

        titleBox.setPadding(new Insets(0, 2, 0, 0));
        artist.setPadding(new Insets(0, 2, 0, 0));
        album.setPadding(new Insets(0, 2, 0, 0));
        time.setPadding(new Insets(0, 2, 0, 0));

        GridPane.setHalignment(titleBox, HPos.LEFT);
        GridPane.setHalignment(artist, HPos.CENTER);
        GridPane.setHalignment(artist, HPos.CENTER);
        GridPane.setHalignment(time, HPos.CENTER);
        artist.setStyle("-fx-text-fill: #4D4D4D;" +
                "-fx-background-color: TRANSPARENT;" +
                "-fx-font-size: 14px;");
        album.setStyle("-fx-text-fill: #4D4D4D;" +
                "-fx-background-color: TRANSPARENT;" +
                "-fx-font-size: 14px;");
        time.setStyle("-fx-text-fill: #4D4D4D;" +
                "-fx-background-color: TRANSPARENT;" +
                "-fx-font-size: 14px;");
        title.setStyle("-fx-text-fill: #4D4D4D;" +
                "-fx-background-color: TRANSPARENT;" +
                "-fx-font-size: 14px;");

        for (int i = 0; i < 3; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(30);
            getColumnConstraints().add(column);
        }
        ColumnConstraints column = new ColumnConstraints();
        column.setPercentWidth(10);
        getColumnConstraints().add(column);
        titleBox.getChildren().add(title);

        GridPane.setConstraints(titleBox, 0, 0);
        GridPane.setConstraints(artist, 1, 0);
        GridPane.setConstraints(album, 2, 0);
        GridPane.setConstraints(time, 3, 0);
        getChildren().addAll(titleBox, artist, album, time);

        setPrefWidth(400);

//        getStylesheets().add(Main.class.getResource("resources/InfoPane.css").toExternalForm());


    }
}
