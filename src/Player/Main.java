package Player;

import Player.MainContent.CurPlayPage;
import Player.MainContent.PlayListPage;
import Player.button.*;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import insidefx.undecorator.Undecorator;
import insidefx.undecorator.UndecoratorScene;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ChangeListener;
import javafx.collections.MapChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.ResourceBundle;


public class Main extends Application {

    //    It is used to transfer information to the PlayListPage.
    static public HBox metaDataTransfer;
    static public Text timeTransfer;
    static public Text mediaPathTransFer;
    private boolean transfer = true;

    private double xOffset = 0;
    private double yOffset = 0;
    private boolean mediaLoaded = false;
    private boolean mediaPlaying = false;

    private Stage stage;

    private Image defaultCover = scale(new Image(Main.class.getResource("resources/default-cover-art.png").toExternalForm()),
            95, 95, false);
    private ResourceBundle LOC = ResourceBundle.getBundle("insidefx/undecorator/resources/localization", Locale.getDefault());

    private PlayPane playPane;
    private PlayButton playButton, pauseButton;
    private SoundButton soundButton;
    private TimeSlider slider;
    private SoundSlider soundVolumeSlider;
    private SkipPreviousButton skipPrevious;
    private SkipNextButton skipNext;
    private TitleBar titleBar;

    private Undecorator undecorator;

    private Accordion leftView;

    private Region rect;

    private Media media;
    private MediaPlayer mediaPlayer;
    private Label timeCur, timeTot;
    private Label musicImage, musicTitle, musicArtist, musicAlbum;

    private StackPane mainContent;
    private PlayListPage playListPage;
    private CurPlayPage curPlayPage;

    //    Listener of time slider's value (valueProperty). Set mediaPlayer's current time as the value of the slider.
    private InvalidationListener sliderChangeListener =
            o -> mediaPlayer.seek(Duration.seconds(slider.getValue()));

    //    Listener of current time of mediaPlayer (currentTimeProperty). Ones called, remove sliderChangeListener
//    firstly, then set time slider's value as the current time, set current time text. Lastly, re-add
//    sliderChangeListener to time slider.
    private ChangeListener<Duration> playerListener =
            (observableValue, duration, t1) -> {
                slider.valueProperty().removeListener(sliderChangeListener);
                slider.setValue(mediaPlayer.getCurrentTime().toSeconds());
                slider.valueProperty().addListener(sliderChangeListener);
                int time = (int) (slider.getValue() + 0.5);
                timeCur.setText(new DecimalFormat("00:").format(time / 60) +
                        new DecimalFormat("00").format(time % 60));
            };

    //    Another listener of time slider's value (valueProperty). It only works while dragging slider. While user is
//    dragging time slider, current time text shall be changed but mediaPlayer would not be effected.
    private ChangeListener<Duration> sliderDraggingPlayerListener =
            (observableValue, duration, t1) -> {
                int time = (int) (slider.getValue() + 0.5);
                timeCur.setText(new DecimalFormat("00:").format(time / 60) +
                        new DecimalFormat("00").format(time % 60));
            };

    //    Listener of time slider's value changing (valueChangingProperty).The property is true when the user is
//    dragging the thumb and false once they release it.
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

    //    Listener of music meta data, it will try to get the media's title, artist, album and image cover.
    private MapChangeListener<String, Object> musicMetaDataListener = (c) -> {
        if (c.wasAdded())
            handleMetaData(c.getKey(), c.getValueAdded());
    };

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        media = null;
        timeTransfer = new Text();
        mediaPathTransFer = new Text();
        metaDataTransfer = new HBox();
        metaDataTransfer.getChildren().addAll(new Label(), new Label());
        metaDataTransfer.setPrefSize(1, 1);

        musicAlbum = new Label();
        musicArtist = new Label();
        musicTitle = new Label();
        musicImage = new Label();

        titleBar = new TitleBar();
        leftView = new Accordion(this);

        setPlayPane();
        setSoundPane();

//        Transfer total time of media to the PlayListPage. PlayListPage is listening the timeTransfer's textProperty.
        timeTransfer.textProperty().bind(timeTot.textProperty());

        titleBar.leftColour.minWidthProperty().bind(leftView.prefWidthProperty());

        BorderPane.setAlignment(playPane, Pos.BOTTOM_CENTER);

        mainContent = new StackPane();
        playListPage = new PlayListPage(this);
        curPlayPage = new CurPlayPage();

        mainContent.getChildren().add(playListPage);

        BorderPane root = new BorderPane();
        root.setTop(titleBar);
        root.setLeft(leftView);
        root.setCenter(mainContent);
        root.setBottom(playPane);

        rect = new Region();
        rect.setPrefWidth(1);
        root.setRight(rect);
        root.setPrefSize(650, 300);

        // Set the Style-properties of the BorderPane
        root.setStyle("-fx-padding: 0;");

        titleBar.setOnMousePressed(mouseEvent -> {
            xOffset = mouseEvent.getSceneX();
            yOffset = mouseEvent.getSceneY();
        });
        //move around here
        titleBar.setOnMouseDragged(mouseEvent -> {
            if (!mouseEvent.isPrimaryButtonDown() || stage.isFullScreen())
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
        undecorator = undecoratorScene.getUndecorator();
        stage.setMinWidth(undecorator.getPrefWidth());
        stage.setMinHeight(undecorator.getPrefHeight());

        titleBar.setOnMouseClicked((mouseEvent -> {
            if (mouseEvent.getButton().toString().equals("SECONDARY")) {
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
//        musicImage.setGraphic(new ImageView(defaultCover));
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
        soundButton = new SoundButton(volumeBind);
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
    }

    public void loadExistedMusic(MediaInfo mediaInfo) {
        try {
            try {
                new Media(new File(mediaInfo.getPath()).toURI().toURL().toExternalForm());
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
            playListPage.setTimeFix(true);
            media = new Media(new File(mediaInfo.getPath()).toURI().toURL().toExternalForm());
            musicImage.setGraphic(new ImageView(defaultCover));
//            if (mediaInfo.getCover() != null)
//                musicImage.setGraphic(new ImageView(scale(mediaInfo.getCover(),
//                        (int) musicImage.getWidth(), (int) musicImage.getHeight(), false)));
            musicAlbum.setText(mediaInfo.getAlbum());
            musicArtist.setText(mediaInfo.getArtist());
            musicTitle.setText(mediaInfo.getTitle());
            transfer = false;
            try{
                curPlayPage.setLyric(new File(mediaInfo.getPath().substring(0, mediaInfo.getPath().lastIndexOf('.')) + ".lrc"));
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
            preparePlay();
            media.getMetadata().addListener(musicMetaDataListener);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void setButtonAction() {
        FileChooser fileChooser = new FileChooser();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        configureFileChooser(fileChooser);
        final ContextMenu openMenu = new ContextMenu();
        openMenu.setAutoHide(true);
        MenuItem openFileItem = new MenuItem(LOC.getString("OpenFile"));
        openFileItem.setOnAction(actionEvent -> {
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                playListPage.releaseSearch();
                Platform.runLater(() -> openFile(file));
            }
        });
        openFileItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN, KeyCombination.SHORTCUT_DOWN));
        MenuItem openDirItem = new MenuItem(LOC.getString("OpenDir"));
        openDirItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHIFT_DOWN, KeyCombination.SHORTCUT_DOWN));
        openDirItem.setOnAction(actionEvent -> {
            File file = directoryChooser.showDialog(stage);
            if (file != null)
                openAll(file);
        });
        openMenu.getItems().addAll(openFileItem, openDirItem);
        leftView.addMusicButton.setContextMenu(openMenu);
        leftView.addMusicButton.setOnAction(actionEvent -> {
            if (openMenu.isShowing()) {
                openMenu.hide();
            } else {
                openMenu.show(leftView.addMusicButton, Side.RIGHT, 0, 38);
            }
        });

        leftView.curPlayButton.setOnAction(actionEvent -> {
            mainContent.getChildren().setAll(curPlayPage);
            rect.setStyle("-fx-background-color: black;");
        });

        leftView.playListButton.setOnAction(actionEvent -> {
            mainContent.getChildren().setAll(playListPage);
            rect.setStyle("-fx-background-color: white;");
            playListPage.releaseSearch();
        });

        leftView.search.setOnAction(actionEvent -> {
            playListPage.releaseSearch();
            playListPage.search(leftView.searchField.getText());
        });

        skipNext.setOnAction(actionEvent -> playListPage.playNext(this));
        skipPrevious.setOnAction(actionEvent -> playListPage.playPrevious(this));
    }

    private void openAll(File file) {
        try {
            playListPage.releaseSearch();
            File[] fileList = file.listFiles();
            if (fileList != null && fileList.length != 0) {
                Thread thread = new Thread(() -> {
                    for (File eachFile : fileList) {
                        playListPage.addMusic(eachFile);
                    }
                    if (!playListPage.timeUpdate.isAlive())
                        playListPage.timeUpdate.start();
                });
                thread.setDaemon(true);
                thread.start();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
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
            playListPage.setTimeFix(false);
            media = new Media(file.toURI().toURL().toExternalForm());

            transfer = true;
//            Transfer media file's path to the PlayListPage. PlayListPage is listening the widthProperty of
//            mediaPathTransFer.
            mediaPathTransFer.setText(file.toString());

            musicImage.setGraphic(new ImageView(defaultCover));
            musicAlbum.setText(LOC.getString("UnknownAlbum"));
            musicArtist.setText(LOC.getString("UnknownArtist"));
            String[] s = file.toString().split("\\\\");
            musicTitle.setText(s[s.length - 1].split("\\.")[0]);
            try{
                curPlayPage.setLyric(new File(file.toString().substring(0, file.toString().lastIndexOf('.')) + ".lrc"));
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
            preparePlay();

            media.getMetadata().addListener(musicMetaDataListener);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void preparePlay() {
        mediaPlayer = new MediaPlayer(media);
        soundButton.setPlayer(mediaPlayer);
        mediaLoaded = true;
        curPlayPage.setMedia(mediaPlayer);
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
            System.out.println(e.getMessage());
        }
        try {
//            Set maximum value of time slider as the media's total time. Binding is used because total time could not be
//            get immediately.
            slider.maxProperty().bind(Bindings.createDoubleBinding(
                    () -> media.getDuration().toSeconds(),
                    media.durationProperty())
            );

//            Bind total time text with media's total time.
            timeTot.textProperty().bind(Bindings.createStringBinding(
                    () -> {
                        int time = (int) (slider.maxProperty().getValue() + 0.5);
                        return new DecimalFormat("00:").format(time / 60) +
                                new DecimalFormat("00").format(time % 60);
                    }, slider.valueProperty())
            );
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        slider.valueProperty().addListener(sliderChangeListener);
        mediaPlayer.currentTimeProperty().addListener(playerListener);
        slider.valueChangingProperty().addListener(sliderValueChangingListener);

        mediaPlayer.volumeProperty().bind(volumeBind);


//        Stop audio visualized effect at the end of playing.
        mediaPlayer.setOnEndOfMedia(() -> curPlayPage.stop());
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
            curPlayPage.start();
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
        if (mediaLoaded) {
            mediaPlayer.pause();
//            curPlayPage.stop();
        }
    }

    //    How to transfer meta data to the PlayListPage? Ones meta data is gotten, save the data into the metaDataTransfer,
//    then change its width. Meanwhile PlayListPage is listening the widthProperty of metaDataTransfer.
    private void handleMetaData(String key, Object value) {
        if (key.equals("album")) {
            musicAlbum.setText(value.toString());
            if (transfer) {
                ((Label) metaDataTransfer.getChildren().get(0)).setText(key);
                ((Label) metaDataTransfer.getChildren().get(1)).setText(value.toString());
                metaDataTransfer.setPrefWidth(metaDataTransfer.getPrefWidth() + 0.1);
            }
        }
        if (key.equals("artist")) {
            musicArtist.setText(value.toString());
            if (transfer) {
                ((Label) metaDataTransfer.getChildren().get(0)).setText(key);
                ((Label) metaDataTransfer.getChildren().get(1)).setText(value.toString());
                metaDataTransfer.setPrefWidth(metaDataTransfer.getPrefWidth() + 0.1);
            }
        }
        if (key.equals("title")) {
            musicTitle.setText(value.toString());
            if (transfer) {
                ((Label) metaDataTransfer.getChildren().get(0)).setText(key);
                ((Label) metaDataTransfer.getChildren().get(1)).setText(value.toString());
                metaDataTransfer.setPrefWidth(metaDataTransfer.getPrefWidth() + 0.1);
            }
        }
        if (key.equals("image")) {
            musicImage.setGraphic(new ImageView(scale((Image) value,
                    (int) musicImage.getWidth(), (int) musicImage.getHeight(), false)));
//            ((Label) metaDataTransfer.getChildren().get(0)).setText(key);
//            ((Label) metaDataTransfer.getChildren().get(1)).setGraphic(new ImageView((Image) value));
//            metaDataTransfer.setPrefWidth(metaDataTransfer.getPrefWidth() + 0.1);
        }
    }

    void setLeftView(boolean t) {
        if (!t) {
            undecorator.setTitle("");
            leftView.searchBtn.setOpacity(1);
        } else {
            undecorator.setTitle("FX Music Player");
            leftView.searchBtn.setOpacity(0);
        }
    }

    private void configureFileChooser(final FileChooser fileChooser) {
//        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("mp3", "*.mp3"),
                new FileChooser.ExtensionFilter(LOC.getString("AllFiles"), "*.*")
        );
    }

    //    Resize image as specified size.
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
