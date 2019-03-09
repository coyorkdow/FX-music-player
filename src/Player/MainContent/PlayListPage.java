package Player.MainContent;

import Player.Main;
import com.jfoenix.controls.JFXListView;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;


public class PlayListPage extends JFXScrollPane {

    private Label title;
    private StackPane content;
    Map<String, InfoPane> inListMediaRecord = new HashMap<>();


    public PlayListPage() {
        header.maxWidthProperty().bind(Bindings.createDoubleBinding(
                () -> getWidth() * 0.9, widthProperty()));
        header.minWidthProperty().bind(Bindings.createDoubleBinding(
                () -> getWidth() * 0.9, widthProperty()));
        JFXListView<InfoPane> list = new JFXListView<>();
        list.setEditable(true);
//        for (int i = 0; i < 100; i++) {
//            list.getItems().add(new InfoPane());
//        }
        Main.mediaURLTransFer.textProperty().addListener(observable -> {
            InfoPane infoPane = new InfoPane();
            list.getItems().add(0, infoPane);
            list.edit(0);
            if (inListMediaRecord.containsKey(Main.mediaURLTransFer.getText()))
                list.getItems().remove(inListMediaRecord.get(Main.mediaURLTransFer.getText()));
            inListMediaRecord.put(Main.mediaURLTransFer.getText(), infoPane);
        });
        Main.timeTransfer.textProperty().addListener((observable, old, newer) -> {
                    list.getItems().get(0).time.setText(newer);
                    if (list.getItems().size() > 1) {
                        double cal = list.getItems().size() * list.getItems().get(1).getHeight();
                        list.setMinHeight(cal * 2);
                    }
                }
        );
        Main.metaDataTransfer.prefWidthProperty().addListener(observable -> {
            switch (((Label) Main.metaDataTransfer.getChildren().get(0)).getText()) {
                case "album":
                    list.getItems().get(0).album.setText(
                            ((Label) Main.metaDataTransfer.getChildren().get(1)).getText());
                    break;
                case "artist":
                    list.getItems().get(0).artist.setText(
                            ((Label) Main.metaDataTransfer.getChildren().get(1)).getText());
                    break;
                case "title":
                    list.getItems().get(0).title.setText(
                            ((Label) Main.metaDataTransfer.getChildren().get(1)).getText());
                    break;
                case "image":
                    break;
                default:
            }
        });

        title = new Label("播放列表");
        title.setStyle("-fx-text-fill: #4D4D4D;" +
                        "-fx-background-color: TRANSPARENT;" +
                        "-fx-font-size: 24px;"
//                "-fx-font-weight: bold;"
        );
        title.maxWidthProperty().bind(Bindings.createDoubleBinding(
                () -> getWidth() * 0.9, widthProperty()));
        getMidBar().getChildren().add(title);
        content = new StackPane();
        list.maxWidthProperty().bind(Bindings.createDoubleBinding(
                () -> getWidth() * 0.9, widthProperty()));
        list.setPrefHeight(300);
        content.getChildren().add(list);
        getStylesheets().add(Main.class.getResource("resources/ScrollBar.css").toExternalForm());
        setContent(content);
//        JFXScrollPane.smoothScrolling((ScrollPane) this.getChildren().get(0));
    }
}
