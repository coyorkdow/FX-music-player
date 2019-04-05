package Player.MainContent;

import Player.Main;
import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.File;
import java.text.DecimalFormat;
import java.util.*;


public class PlayListPage extends JFXScrollPane {

    final private JFXListView<InfoPane> list;
    private Label title;
    private Map<Integer, InfoPane> inListMediaRecord;
    private Vector<Pair<Integer, InfoPane>> searchHold;
    private ResourceBundle LOC = ResourceBundle.getBundle("insidefx/undecorator/resources/localization", Locale.getDefault());
    private InfoPane curPlaying = null;
    private Boolean timeFix = false;

    private String keyWord;

    public Thread timeUpdate;
    final private Object lock = new Object();

    public PlayListPage(Main main) {
        this.inListMediaRecord = new HashMap<>();
        this.searchHold = new Vector<>();
        header.maxWidthProperty().bind(Bindings.createDoubleBinding(
                () -> getWidth() * 0.9, widthProperty()));
        header.minWidthProperty().bind(Bindings.createDoubleBinding(
                () -> getWidth() * 0.9, widthProperty()));
        list = new JFXListView<>();
        list.setEditable(true);

        final ContextMenu itemMenu = new ContextMenu();
        itemMenu.setAutoHide(true);
        MenuItem play = new MenuItem(LOC.getString("Play"));
        MenuItem remove = new MenuItem(LOC.getString("Remove"));
        itemMenu.getItems().addAll(play, remove);

        play.setOnAction(actionEvent -> {
            InfoPane selectedPane = list.getSelectionModel().getSelectedItem();
            main.loadExistedMusic(selectedPane.getMediaInfo());
            if (curPlaying != null)
                curPlaying.removePlayingIcon();
            selectedPane.setPlayingIcon();
            curPlaying = selectedPane;
        });

        remove.setOnAction(actionEvent -> {
            InfoPane selectedPane = list.getSelectionModel().getSelectedItem();
            if (selectedPane.equals(curPlaying))
                curPlaying = null;
            list.getItems().remove(selectedPane);
        });

        list.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().toString().equals("SECONDARY")) {
                if (itemMenu.isShowing()) {
                    itemMenu.hide();
                } else {
                    itemMenu.show(list.getSelectionModel().getSelectedItem(), mouseEvent.getScreenX(), mouseEvent.getScreenY());
                }
            } else if (mouseEvent.getClickCount() == 2 && mouseEvent.getButton().toString().equals("PRIMARY")) {
                InfoPane selectedPane = list.getSelectionModel().getSelectedItem();
                main.loadExistedMusic(selectedPane.getMediaInfo());
                if (curPlaying != null)
                    curPlaying.removePlayingIcon();
                selectedPane.setPlayingIcon();
                curPlaying = selectedPane;
            }
        });

        Main.mediaPathTransFer.textProperty().addListener(observable -> {
            InfoPane infoPane = new InfoPane();
            if (curPlaying != null)
                curPlaying.removePlayingIcon();
            curPlaying = infoPane;
            list.getItems().add(0, infoPane);
            int hash = new File(Main.mediaPathTransFer.getText()).hashCode();
            if (inListMediaRecord.containsKey(hash))
                list.getItems().remove(inListMediaRecord.get(hash));
            String[] s = Main.mediaPathTransFer.getText().split("\\\\");
            infoPane.setTitle(s[s.length - 1].split("\\.")[0]);
            infoPane.setPath(Main.mediaPathTransFer.getText());
            inListMediaRecord.put(hash, infoPane);
        });
        Main.timeTransfer.textProperty().addListener((observable, old, newer) -> {
                    if (timeFix)
                        return;
                    list.getItems().get(0).setTime(newer);
                }
        );
        Main.metaDataTransfer.prefWidthProperty().addListener(observable -> {
            switch (((Label) Main.metaDataTransfer.getChildren().get(0)).getText()) {
                case "album":
                    list.getItems().get(0).setAlbum(
                            ((Label) Main.metaDataTransfer.getChildren().get(1)).getText());
                    break;
                case "artist":
                    list.getItems().get(0).setArtist(
                            ((Label) Main.metaDataTransfer.getChildren().get(1)).getText());
                    break;
                case "title":
                    list.getItems().get(0).setTitle(
                            ((Label) Main.metaDataTransfer.getChildren().get(1)).getText());
                    break;
                case "image":
//                    list.getItems().get(0).setCover(((ImageView)
//                            (((Label) Main.metaDataTransfer.getChildren().get(1)).getGraphic())).getImage());
                    break;
                default:
            }
        });

        title = new Label(LOC.getString("PlayList"));
        title.setStyle("-fx-text-fill: #4D4D4D;" +
                        "-fx-background-color: TRANSPARENT;" +
                        "-fx-font-size: 24px;"
//                "-fx-font-weight: bold;"
        );
        title.maxWidthProperty().bind(Bindings.createDoubleBinding(
                () -> getWidth() * 0.9, widthProperty()));
        getMidBar().getChildren().add(title);
        StackPane content = new StackPane();
        list.maxWidthProperty().bind(Bindings.createDoubleBinding(
                () -> getWidth() * 0.9, widthProperty()));
        list.setPrefHeight(300);
        list.minHeightProperty().bind(list.prefHeightProperty());
        list.maxHeightProperty().bind(list.prefHeightProperty());
        list.getItems().addListener(new ListChangeListener<InfoPane>() {
            @Override
            public void onChanged(Change<? extends InfoPane> change) {
                try {
                    double cal = 19 * list.getItems().size();
                    if (cal > 300)
                        list.setPrefHeight(cal);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });
        content.getChildren().add(list);
        getStylesheets().add(Main.class.getResource("resources/ScrollBar.css").toExternalForm());
        setContent(content);
//        JFXScrollPane.smoothScrolling((ScrollPane) this.getChildren().get(0));
        timeUpdate = new Thread(() -> {
            Media media;
            MediaPlayer mediaPlayer;
            int size = 0;
            do {
                if (size == list.getItems().size())
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                    }
                size = list.getItems().size();
                try {
                    for (int i = 0; i < size; i++) {
                        InfoPane infoPane;
                        infoPane = list.getItems().get(i);
                        if (!infoPane.timeFix())
                            continue;
                        try {
                            media = new Media(new File(infoPane.getMediaInfo().getPath()).toURI().toURL().toExternalForm());
                            mediaPlayer = new MediaPlayer(media);
                            while (media.getDuration().equals(Duration.UNKNOWN))
                                Thread.sleep(100);
                            int time = (int) (media.getDuration().toSeconds() + 0.5);
                            Platform.runLater(() -> infoPane.setTime(new DecimalFormat("00:").format(time / 60) +
                                    new DecimalFormat("00").format(time % 60)));
                            infoPane.setTimeFix(false);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } while (!Thread.currentThread().isInterrupted());
        });
        timeUpdate.setDaemon(true);
    }

    public synchronized void addMusic(File file) {
        if (inListMediaRecord.containsKey(file.hashCode()))
            return;
        InfoPane infoPane = new InfoPane();
        try {
            new Media(file.toURI().toURL().toExternalForm());
        } catch (Exception e) {
            return;
        }
        String[] s = file.toString().split("\\\\");
        infoPane.setTitle(s[s.length - 1].split("\\.")[0]);
        infoPane.setPath(file.toString());
        infoPane.removePlayingIcon();
        inListMediaRecord.put(file.hashCode(), infoPane);
        synchronized (lock) {
            if (!searchHold.isEmpty() && !s[s.length - 1].split("\\.")[0].contains(keyWord)) {
                searchHold.add(new Pair<>(0, infoPane));
            } else {
                Platform.runLater(() -> list.getItems().add(0, infoPane));
            }
        }
        infoPane.autoInfo(file);
    }

    public void search(String keyWord) {
        this.keyWord = keyWord;
        new Thread(() -> {
            if (keyWord.equals(""))
                return;
            synchronized (lock) {
                int size = list.getItems().size();
                for (int i = 0; i < size; i++) {
                    InfoPane infoPane = list.getItems().get(i);
                    if (!infoPane.getMediaInfo().getTitle().contains(keyWord)) {
                        searchHold.add(new Pair<>(i, infoPane));
                    }
                }
                Platform.runLater(() -> title.setText("\"" + keyWord + "\" " + LOC.getString("Result")));
                for (Pair<Integer, InfoPane> pair : searchHold) {
                    Platform.runLater(() -> list.getItems().remove(pair.getValue()));
                }
            }
        }).start();
    }

    public synchronized void releaseSearch() {
        synchronized (lock) {
            title.setText(LOC.getString("PlayList"));
            for (Pair<Integer, InfoPane> pair : searchHold) {
                list.getItems().add(pair.getKey(), pair.getValue());
            }
            searchHold.clear();
        }
    }

    public void playNext(Main main) {
        if (curPlaying == null)
            return;
        int size = list.getItems().size();
        for (int i = 0; i < size; i++) {
            if (list.getItems().get(i).equals(curPlaying)) {
                InfoPane selectedPane;
                if (i == size - 1)
                    selectedPane = list.getItems().get(0);
                else selectedPane = list.getItems().get(i + 1);
                main.loadExistedMusic(selectedPane.getMediaInfo());
                curPlaying.removePlayingIcon();
                selectedPane.setPlayingIcon();
                curPlaying = selectedPane;
                break;
            }
        }
    }

    public void playPrevious(Main main) {
        if (curPlaying == null)
            return;
        int size = list.getItems().size();
        for (int i = 0; i < size; i++) {
            if (list.getItems().get(i).equals(curPlaying)) {
                InfoPane selectedPane;
                if (i == 0)
                    selectedPane = list.getItems().get(size - 1);
                else selectedPane = list.getItems().get(i - 1);
                main.loadExistedMusic(selectedPane.getMediaInfo());
                curPlaying.removePlayingIcon();
                selectedPane.setPlayingIcon();
                curPlaying = selectedPane;
                break;
            }
        }
    }

    public void setTimeFix(Boolean b) {
        timeFix = b;
    }
}
