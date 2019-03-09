package Player;

import Player.MainContent.PlayListPage;
import Player.button.*;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import insidefx.undecorator.Undecorator;
import insidefx.undecorator.UndecoratorScene;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ChangeListener;
import javafx.collections.MapChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.ResourceBundle;


public class Main extends Application {

    static public HBox metaDataTransfer;
    static public Text timeTransfer;
    static public Text mediaURLTransFer;

    private double xOffset = 0;
    private double yOffset = 0;
    private boolean mediaLoaded;
    private boolean mediaPlaying;

    private Stage stage;

    private Image defaultCover = new Image(Main.class.getResource("resources/default-cover-art.png").toExternalForm());
    private ResourceBundle LOC = ResourceBundle.getBundle("insidefx/undecorator/resources/localization", Locale.getDefault());

    BorderPane root;

    private PlayPane playPane;
    private PlayButton playButton, pauseButton;
    private SoundButton soundButton;
    private TimeSlider slider;
    private SoundSlider soundVolumeSlider;
    private SkipPreviousButton skipPrevious;
    private SkipNextButton skipNext;
    private TitleBar titleBar;

    Accordion leftView;

    private Media media;
    //    private MediaView mediaView;
    private MediaPlayer mediaPlayer;
    private Label timeCur, timeTot;
    private Label musicImage, musicTitle, musicArtist, musicAlbum;

    private StackPane mainContent;

    private InvalidationListener sliderChangeListener =
            o -> mediaPlayer.seek(Duration.seconds(slider.getValue()));

    private ChangeListener<Duration> playerListener =
            (observableValue, duration, t1) -> {
                slider.valueProperty().removeListener(sliderChangeListener);
                slider.setValue(mediaPlayer.getCurrentTime().toSeconds());
                slider.valueProperty().addListener(sliderChangeListener);
                int time = (int) (slider.getValue() + 0.5);
                timeCur.setText(new DecimalFormat("00:").format(time / 60) +
                        new DecimalFormat("00").format(time % 60));
            };

    private ChangeListener<Duration> sliderDraggingPlayerListener =
            (observableValue, duration, t1) -> {
                int time = (int) (slider.getValue() + 0.5);
                timeCur.setText(new DecimalFormat("00:").format(time / 60) +
                        new DecimalFormat("00").format(time % 60));
            };

    //    The property is true when the user is dragging the thumb and false once they release it.
    private ChangeListener<Boolean> sliderValueChangingListener =
            (observableValue, aBoolean, t1) -> {
                if (t1) {
                    slider.valueProperty().removeListener(sliderChangeListener);
                    mediaPlayer.currentTimeProperty().removeListener(playerListener);
                    mediaPlayer.currentTimeProperty().addListener(sliderDraggingPlayerListener);
                } else {
                    mediaPlayer.currentTimeProperty().removeListener(sliderDraggingPlayerListener);
                    mediaPlayer.seek(Duration.seconds(slider.getValue()));
                    slider.valueProperty().addListener(sliderChangeListener);
                    mediaPlayer.currentTimeProperty().addListener(playerListener);
                }
            };

    private DoubleBinding volumeBind;

    private MapChangeListener<String, Object> musicMetaDataListener = (c) -> {
        if (c.wasAdded())
            handleMetaData(c.getKey(), c.getValueAdded());
    };

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        mediaLoaded = false;
        mediaPlaying = false;
        media = null;
        timeTransfer = new Text();
        mediaURLTransFer = new Text();
        metaDataTransfer = new HBox();
        metaDataTransfer.getChildren().addAll(new Label(), new Label());
        metaDataTransfer.setPrefSize(1, 1);

        musicAlbum = new Label();
        musicArtist = new Label();
        musicTitle = new Label();
        musicImage = new Label();

        titleBar = new TitleBar();
        leftView = new Accordion();

        setPlayPane();
        setSoundPane();

        timeTransfer.textProperty().bind(timeTot.textProperty());

        titleBar.leftColour.setMaxWidth(leftView.getWidth());
        titleBar.leftColour.setMinWidth(leftView.getWidth());

        BorderPane.setAlignment(playPane, Pos.BOTTOM_CENTER);

        mainContent = new StackPane(new PlayListPage());

        root = new BorderPane();
        root.setTop(titleBar);
        root.setLeft(leftView);
        root.setCenter(mainContent);
        root.setBottom(playPane);
        root.setRight(new Label());
        root.setPrefSize(650, 300);
        // Set the Style-properties of the BorderPane
        root.setStyle("-fx-padding: 0;");

        titleBar.setOnMousePressed(mouseEvent -> {
            xOffset = mouseEvent.getSceneX();
            yOffset = mouseEvent.getSceneY();
        });
        //move around here
        titleBar.setOnMouseDragged(mouseEvent -> {
            if (!mouseEvent.isPrimaryButtonDown())
                return;
            stage.setX(mouseEvent.getScreenX() - xOffset);
            stage.setY(mouseEvent.getScreenY() - yOffset);
        });

        UndecoratorScene undecoratorScene = new UndecoratorScene(stage, root);
        undecoratorScene.setFadeInTransition();
        stage.setOnCloseRequest(windowEvent -> {
            windowEvent.consume();
            undecoratorScene.setFadeOutTransition();
        });
        stage.setScene(undecoratorScene);
        stage.sizeToScene();
        stage.toFront();

        // Set minimum size based on client area's minimum sizes
        Undecorator undecorator = undecoratorScene.getUndecorator();
        stage.setMinWidth(undecorator.getPrefWidth());
        stage.setMinHeight(undecorator.getPrefHeight());

        titleBar.setOnMouseClicked((mouseEvent -> {
            if(mouseEvent.getButton().toString().equals("SECONDARY")) {
                if (undecorator.contextMenu.isShowing()) {
                    undecorator.contextMenu.hide();
                } else {
                    undecorator.contextMenu.show(titleBar, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                }
            }
        }));

        stage.setTitle("FX Music Player");
        stage.show();
        stage.setWidth(800);
        stage.setHeight(600);

        leftView.setPrefWidth(leftView.getWidth());
        leftView.setPrefHeight(leftView.getHeight());
        titleBar.leftColour.setMaxWidth(leftView.getWidth());
        titleBar.leftColour.setMinWidth(leftView.getWidth());

        leftView.widthProperty().addListener((observableValue, number, t1) -> {
            titleBar.leftColour.setMaxWidth(leftView.getWidth());
            titleBar.leftColour.setMinWidth(leftView.getWidth());
            if (leftView.getWidth() == leftView.getPrefWidth()) {
                undecorator.setTitle("");
                leftView.searchBtn.setOpacity(1);
            } else {
                undecorator.setTitle("FX Music Player");
                leftView.searchBtn.setOpacity(0);
            }
        });
        setButtonAction();
    }

    private void setPlayPane() {
        skipPrevious = new SkipPreviousButton();
        skipNext = new SkipNextButton();
        playPane = new PlayPane();

        playButton = new PlayButton(PlayButton.ButtonType.PLAY);
        pauseButton = new PlayButton(PlayButton.ButtonType.PAUSE);
        pauseButton.setVisible(false);

        slider = new TimeSlider(0, 0, 0);

        playPane.getChildren().addAll(skipPrevious, skipNext, playButton, slider);
//        playPane.setAlignment(Pos.CENTER);
        playPane.setPadding(new Insets(5, 5, 1, 1));
        playPane.setVgap(10);

        playButton.setOnAction(actionEvent -> switchPauseToPlay());

        pauseButton.setOnAction(actionEvent -> switchPlayToPause());

        timeCur = new Label("00:00");
        timeTot = new Label("00:00");
        timeCur.setStyle("-fx-text-fill: #c6ccdd;" +
                "-fx-background-color: TRANSPARENT;");
        timeTot.setStyle("-fx-text-fill: #c6ccdd;" +
                "-fx-background-color: TRANSPARENT;");


        PlayPane.setHalignment(timeCur, HPos.CENTER);
        PlayPane.setHalignment(timeTot, HPos.CENTER);
        PlayPane.setConstraints(timeCur, 1, 1);
        PlayPane.setConstraints(timeTot, 5, 1);
        playPane.getChildren().addAll(timeCur, timeTot);

        PlayPane.setHalignment(skipPrevious, HPos.RIGHT);
        PlayPane.setHalignment(skipNext, HPos.LEFT);
        PlayPane.setHalignment(playButton, HPos.CENTER);
        PlayPane.setHalignment(pauseButton, HPos.CENTER);
        PlayPane.setConstraints(playButton, 3, 0);
        PlayPane.setConstraints(pauseButton, 3, 0);
        PlayPane.setConstraints(skipPrevious, 2, 0);
        PlayPane.setConstraints(skipNext, 4, 0);
        PlayPane.setConstraints(slider, 2, 1);
        PlayPane.setColumnSpan(slider, 3);

        HBox metaDataBox = new HBox();
        VBox metaDataTextBox = new VBox();
        metaDataTextBox.getChildren().addAll(musicTitle, musicArtist);
        metaDataBox.getChildren().addAll(musicImage, metaDataTextBox);
        metaDataTextBox.setPadding(new Insets(0, 0, 0, 5));
        musicTitle.setStyle("-fx-text-fill: #c6ccdd;" +
                "-fx-background-color: TRANSPARENT;" +
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bolder;" +
//                "-fx-pref-width: 300px;" +
                "-fx-pref-height: 40px;");
        musicArtist.setStyle("-fx-text-fill: #c6ccdd;" +
                "-fx-background-color: TRANSPARENT;" +
//                " -fx-pref-width: 300px;" +
                "-fx-pref-height: 30px;" +
                "-fx-font-size: 14px;");
        musicImage.setStyle("-fx-background-color: TRANSPARENT;");
        musicImage.setGraphic(new ImageView(defaultCover));
        musicImage.setMinSize(95, 95);
        PlayPane.setConstraints(metaDataBox, 0, 0);
        PlayPane.setRowSpan(metaDataBox, 2);
        playPane.getChildren().add(metaDataBox);


    }

    private void setSoundPane() {
        GridPane soundBox = new GridPane();
        soundVolumeSlider = new SoundSlider(0, 100, 50);
        volumeBind = Bindings.createDoubleBinding(
                () -> soundVolumeSlider.getValue() / 100, soundVolumeSlider.valueProperty());
        soundButton = new SoundButton(soundVolumeSlider, volumeBind);
        soundBox.setHgap(5);
        soundBox.add(soundButton, 0, 0);
        soundBox.add(soundVolumeSlider, 1, 0);

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(60);
        soundBox.getColumnConstraints().add(0, column1);

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(40);
        soundBox.getColumnConstraints().add(1, column1);

        PlayPane.setHalignment(soundButton, HPos.RIGHT);
        PlayPane.setHalignment(soundVolumeSlider, HPos.CENTER);

        PlayPane.setHalignment(soundBox, HPos.RIGHT);
        PlayPane.setConstraints(soundBox, 6, 0);
        playPane.getChildren().add(soundBox);

//        soundButton.setOnMouseClicked( mouseEvent -> {
//            if(soundButton.isForbid())
//                mediaPlayer.volumeProperty().unbind();
//            else
//                mediaPlayer.volumeProperty().bind(Bindings.createDoubleBinding(
//                        () -> soundVolumeSlider.getValue() / 100, soundVolumeSlider.valueProperty()));
//        });

    }

    private void setButtonAction() {
        FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
//        leftView.addMusicButton.setOnAction(actionEvent -> {
//            File file = fileChooser.showOpenDialog(stage);
//            if (file != null) {
//                openFile(file);
//            }
//        });
        leftView.addMusicButton.setOnAction(actionEvent -> {
            File file = fileChooser.showOpenDialog(stage);
            if (file != null)
                openFile(file);
        });

        leftView.playListButton.setOnAction(actionEvent -> {
            //        TreeView<File> leftView = new TreeView<>(
//                new TreeFileItem(new File("C:\\")));
//            root.setCenter(new TreeView<>(new TreeFileItem(new File("C:/"))));
        });
    }

    private void openFile(File file) {
        try {
            try {
                new Media(file.toURI().toURL().toExternalForm());
            } catch (MediaException e) {
                if (e.getType().equals(MediaException.Type.MEDIA_UNSUPPORTED)) {
                    JFXDialogLayout content = new JFXDialogLayout();
                    content.setHeading(new Text(LOC.getString("ERROR")));
                    content.setBody(new Text(LOC.getString("MEDIA_UNSUPPORTED")));
                    JFXDialog dialog = new JFXDialog(mainContent, content, JFXDialog.DialogTransition.CENTER);
                    FlatButton button = new FlatButton(LOC.getString("OK"));
                    button.setOnAction(actionEvent -> dialog.close());
                    content.setActions(button);
                    dialog.show();
                    return;
                }
            }
            if (mediaLoaded) {
                mediaPlayer.stop();
                mediaLoaded = false;
            }
            media = new Media(file.toURI().toURL().toExternalForm());
            mediaURLTransFer.setText(file.toURI().toURL().toExternalForm());
            musicImage.setGraphic(new ImageView(defaultCover));
            musicArtist.setText("");
            musicTitle.setText("");
            musicAlbum.setText("");

            media = new Media(file.toURI().toURL().toExternalForm());
            mediaPlayer = new MediaPlayer(media);
            soundButton.setPlayer(mediaPlayer);
//            mediaView = new MediaView(mediaPlayer);
//            root.setCenter(mediaView);
            mediaLoaded = true;
            switchPauseToPlay();

            try {
                slider.maxProperty().unbind();
                timeTot.textProperty().unbind();
                slider.valueProperty().removeListener(sliderChangeListener);
                slider.valueChangingProperty().removeListener(sliderValueChangingListener);
                mediaPlayer.currentTimeProperty().removeListener(playerListener);
                mediaPlayer.currentTimeProperty().removeListener(sliderDraggingPlayerListener);
                mediaPlayer.volumeProperty().unbind();
                media.getMetadata().removeListener(musicMetaDataListener);
            } catch (Exception e) {
            }

            try {
                slider.maxProperty().bind(Bindings.createDoubleBinding(
                        () -> mediaPlayer.getTotalDuration().toSeconds(),
                        mediaPlayer.totalDurationProperty())
                );
                timeTot.textProperty().bind(Bindings.createStringBinding(
                        () -> {
                            int time = (int) (slider.maxProperty().getValue() + 0.5);
                            return new DecimalFormat("00:").format(time / 60) +
                                    new DecimalFormat("00").format(time % 60);
                        }, slider.valueProperty())
                );
            } catch (Exception e) {
            }

            slider.valueProperty().addListener(sliderChangeListener);
            mediaPlayer.currentTimeProperty().addListener(playerListener);
            slider.valueChangingProperty().addListener(sliderValueChangingListener);

            mediaPlayer.volumeProperty().bind(volumeBind);

            media.getMetadata().addListener(musicMetaDataListener);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void switchPauseToPlay() {
        if (!mediaPlaying) {
            playButton.setVisible(false);
            pauseButton.setVisible(true);
            playPane.getChildren().remove(playButton);
            playPane.getChildren().remove(slider);
            playPane.getChildren().add(pauseButton);
            playPane.getChildren().add(slider);
            mediaPlaying = true;
        }
        if (mediaLoaded)
            mediaPlayer.play();
    }

    private void switchPlayToPause() {
        if (mediaPlaying) {
            pauseButton.setVisible(false);
            playButton.setVisible(true);
            playPane.getChildren().remove(pauseButton);
            playPane.getChildren().remove(slider);
            playPane.getChildren().add(playButton);
            playPane.getChildren().add(slider);
            mediaPlaying = false;
        }
        if (mediaLoaded)
            mediaPlayer.pause();
    }

    private void handleMetaData(String key, Object value) {
        if (key.equals("album")) {
            musicAlbum.setText(value.toString());
            ((Label) metaDataTransfer.getChildren().get(0)).setText(key);
            ((Label) metaDataTransfer.getChildren().get(1)).setText(value.toString());
            metaDataTransfer.setPrefWidth(metaDataTransfer.getPrefWidth() + 0.1);
        }
        if (key.equals("artist")) {
            musicArtist.setText(value.toString());
            ((Label) metaDataTransfer.getChildren().get(0)).setText(key);
            ((Label) metaDataTransfer.getChildren().get(1)).setText(value.toString());
            metaDataTransfer.setPrefWidth(metaDataTransfer.getPrefWidth() + 0.1);
        }
        if (key.equals("title")) {
            musicTitle.setText(value.toString());
            ((Label) metaDataTransfer.getChildren().get(0)).setText(key);
            ((Label) metaDataTransfer.getChildren().get(1)).setText(value.toString());
            metaDataTransfer.setPrefWidth(metaDataTransfer.getPrefWidth() + 0.1);
        }
        if (key.equals("image")) {
            musicImage.setGraphic(new ImageView(scale((Image) value,
                    (int) musicImage.getWidth(), (int) musicImage.getHeight(), false)));
            ((Label) metaDataTransfer.getChildren().get(0)).setText(key);
            ((Label) metaDataTransfer.getChildren().get(1)).setGraphic(new ImageView((Image) value));
            metaDataTransfer.setPrefWidth(metaDataTransfer.getPrefWidth() + 0.1);
        }
    }

    private static void configureFileChooser(final FileChooser fileChooser) {
//        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("mp3", "*.mp3"),
                new FileChooser.ExtensionFilter("All files", "*.*")
//                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
    }

    private static Image scale(Image source, int targetWidth, int targetHeight, boolean preserveRatio) {
        ImageView imageView = new ImageView(source);
        imageView.setPreserveRatio(preserveRatio);
        imageView.setFitWidth(targetWidth);
        imageView.setFitHeight(targetHeight);
        return imageView.snapshot(null, null);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
