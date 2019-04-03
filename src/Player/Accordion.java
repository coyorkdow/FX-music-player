package Player;

import Player.button.FlatButton;
import com.jfoenix.controls.JFXTextField;
import control.Tooltip;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;


import java.util.Locale;
import java.util.ResourceBundle;


class Accordion extends HBox {

    private boolean fold;
    FlatButton searchBtn, curPlayButton, playListButton, addMusicButton;
    FlatButton search;
    private VBox popBox;
    JFXTextField searchField;

    Accordion(Main main) {

        ResourceBundle LOC = ResourceBundle.getBundle("insidefx/undecorator/resources/localization", Locale.getDefault());

        fold = false;
        VBox buttonBox = new VBox();
        popBox = new VBox();

        FlatButton btn = new FlatButton(new ImageView(Main.class.getResource("resources/baseline_menu_black_18dp.png").toString()));
        searchBtn = new FlatButton(new ImageView(Main.class.getResource("resources/baseline_search_black_18dp.png").toString()));
        curPlayButton = new FlatButton(new ImageView(Main.class.getResource("resources/baseline_equalizer_black_18dp.png").toString()));
        playListButton = new FlatButton(new ImageView(Main.class.getResource("resources/baseline_playlist_play_black_18dp.png").toString()));
        addMusicButton = new FlatButton(new ImageView(Main.class.getResource("resources/baseline_add_black_18dp.png").toString()));

        curPlayButton.setTooltip(new Tooltip(LOC.getString("CurPlay")));
        playListButton.setTooltip(new Tooltip(LOC.getString("PlayList")));
        addMusicButton.setTooltip(new Tooltip(LOC.getString("Open")));

        buttonBox.getChildren().addAll(btn, searchBtn, curPlayButton, playListButton, addMusicButton);
        getChildren().add(buttonBox);

        Region dummy = new Region();
        dummy.setMinWidth(46 * 6);
        dummy.setMaxWidth(46 * 6);
//        dummy.setPrefHeight(46 * 6);
        dummy.setMinHeight(38);
        dummy.setMaxHeight(38);
        popBox.getChildren().add(dummy);

        searchField = new JFXTextField();
        searchField.setPromptText(LOC.getString("Search"));
        searchField.getStylesheets().add(Main.class.getResource("resources/SearchField.css").toExternalForm());
        searchField.setStyle("-fx-background-color: rgba(255, 255, 255, 0.6);");
        searchField.setMinHeight(34);

        GridPane searchBox = new GridPane();
        searchBox.setPadding(new Insets(2, 2, 2, 2));
        ColumnConstraints colum1 = new ColumnConstraints();
        colum1.setPercentWidth(3);
        ColumnConstraints colum2 = new ColumnConstraints();
        colum2.setPercentWidth(80);
        ColumnConstraints colum3 = new ColumnConstraints();
        colum3.setPercentWidth(17);
        searchBox.getColumnConstraints().addAll(colum1, colum2, colum3);
        Region dummyField = new Region();
        dummyField.setMaxHeight(38);
        dummyField.setStyle("-fx-background-color: rgba(255, 255, 255, 0.6);");
        searchBox.add(dummyField, 0, 0);
        searchBox.add(searchField, 1, 0);

        search = new FlatButton();
        search.setMinSize(34, 34);
        search.getStyleClass().add("search");

        searchBox.add(search, 2, 0);
        searchBox.setMaxHeight(38);
        popBox.getChildren().add(searchBox);

        searchBtn.setOnAction(actionEvent -> {
            if (!fold) {
                fold = true;
                Duration cycleDuration = Duration.millis(100);
                Timeline timeline = new Timeline(
                        new KeyFrame(cycleDuration, new KeyValue(prefWidthProperty(),
                                getWidth() + 46 * 6, Interpolator.EASE_BOTH))
                );
                timeline.play();
                timeline.setOnFinished(actionEvent1 -> {
                    getChildren().add(popBox);
                    main.setLeftView(true);
                });
            }
        });
        searchBtn.setTooltip(new Tooltip(LOC.getString("Search")));

        btn.setOnAction(actionEvent -> {
            if (!fold) {
                fold = true;
                Duration cycleDuration = Duration.millis(100);
                Timeline timeline = new Timeline(
                        new KeyFrame(cycleDuration, new KeyValue(prefWidthProperty(),
                                getWidth() + 46 * 6, Interpolator.EASE_BOTH))
                );
                timeline.play();
                timeline.setOnFinished(actionEvent1 -> {
                    getChildren().add(popBox);
                    main.setLeftView(true);
                });
            } else {
                fold = false;
                getChildren().remove(popBox);
                Duration cycleDuration = Duration.millis(100);
                Timeline timeline = new Timeline(
                        new KeyFrame(cycleDuration, new KeyValue(prefWidthProperty(),
                                getWidth() - 46 * 6, Interpolator.EASE_BOTH))
                );
                timeline.play();
                timeline.setOnFinished(actionEvent1 -> main.setLeftView(false));
            }
        });

        setStyle(" -fx-background-color: linear-gradient(to bottom, #e6dfda, #94918b);");
//        heightProperty().addListener((observableValue, number, t1) -> {
//            if(getHeight() < 10 * getPrefWidth())
//                setStyle(" -fx-background-color: linear-gradient(to bottom, #e6dfda, #8d8783);");
//            else
//                setStyle(" -fx-background-color: linear-gradient(to bottom, #e6dfda, #94918b, #8d8783);");
//        });
    }

}
