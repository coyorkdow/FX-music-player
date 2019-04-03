package Player.MainContent;

import Player.Main;
import Player.MediaInfo;
import javafx.collections.MapChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

public class InfoPane extends GridPane {

    private HBox titleBox;
    private Label icon;
    private Label title;
    private Label artist;
    private Label album;
    private Label time;
    private String path;

    private boolean timeFix = false;

    private MapChangeListener<String, Object> musicMetaDataListener = (c) -> {
        if (c.wasAdded())
            handleMetaData(c.getKey(), c.getValueAdded());
    };

//    private MediaInfo mediaInfo;

    public InfoPane() {
//        mediaInfo = new MediaInfo();
        ResourceBundle LOC = ResourceBundle.getBundle("insidefx/undecorator/resources/localization", Locale.getDefault());
        titleBox = new HBox();
        artist = new Label(LOC.getString("UnknownArtist"));
        album = new Label(LOC.getString("UnknownAlbum"));
        time = new Label();
        title = new Label(LOC.getString("UnknownTitle"));
        icon = new Label();
        icon.setGraphic(new ImageView(Main.class.getResource("resources/baseline_equalizer_black_small.png").toString()));
        icon.setPadding(new Insets(0, 5, 0, 0));
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
        titleBox.getChildren().setAll(icon, title);
        GridPane.setConstraints(titleBox, 0, 0);
        GridPane.setConstraints(artist, 1, 0);
        GridPane.setConstraints(album, 2, 0);
        GridPane.setConstraints(time, 3, 0);
        getChildren().addAll(titleBox, artist, album, time);

        setPrefWidth(400);

//        getStylesheets().add(Main.class.getResource("resources/InfoPane.css").toExternalForm());
    }

    public void setTime(String time) {
        this.time.setText(time);
    }

    public void setTitle(String title) {
//        mediaInfo.setTitle(title);
        this.title.setText(title);
    }

    public void setArtist(String artist) {
//        mediaInfo.setArtist(artist);
        this.artist.setText(artist);
    }

    public void setAlbum(String album) {
//        mediaInfo.setAlbum(album);
        this.album.setText(album);
    }

    public void setPath(String path) {
        this.path = path;
    }

    public MediaInfo getMediaInfo() {
//        return mediaInfo;
        MediaInfo mediaInfo = new MediaInfo();
        mediaInfo.setPath(path);
        mediaInfo.setAlbum(album.getText());
        mediaInfo.setArtist(artist.getText());
        mediaInfo.setTitle(title.getText());
        return  mediaInfo;
    }

    public String getTime() {
        return time.getText();
    }

    public void setPlayingIcon() {
        titleBox.getChildren().setAll(icon, title);
    }

    public boolean timeFix() {
        return timeFix;
    }

    public void setTimeFix(boolean t) {
        timeFix = t;
    }

    public void removePlayingIcon() {
        titleBox.getChildren().setAll(title);
    }

    void autoInfo(File file) {
        try {
            Media media = new Media(file.toURI().toURL().toExternalForm());
            media.getMetadata().addListener(musicMetaDataListener);
            Thread.sleep(100);
//            media = null;
        } catch (Exception e) {
        }
        timeFix = true;
    }

    private void handleMetaData(String key, Object value) {
        if (key.equals("album")) {
            this.setAlbum(value.toString());
        }
        if (key.equals("artist")) {
            this.setArtist(value.toString());
        }
        if (key.equals("title")) {
            this.setTitle(value.toString());
        }
    }
}
