package Player.MainContent;

import Player.Main;
import Player.button.FlatButton;
import com.jfoenix.controls.JFXListView;
import control.ColorChooser;
import control.Lyric;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class CurPlayPage extends StackPane {

    private MediaPlayer audioMediaPlayer = null;
    final private BarChart<String, Number> bc;
    final private XYChart.Series<String, Number> series;
    private JFXListView<Label> lyricView;

    private ColorChooser[] colorChooser = new ColorChooser[32];
    private Lyric lyric = null;

    private ArrayList<Object> curLabels = null;

    private ResourceBundle LOC = ResourceBundle.getBundle("insidefx/undecorator/resources/localization", Locale.getDefault());

    private ChangeListener<Duration> lyricScrollListener = (observableValue, duration, t1) -> {
        if (lyric.contains(audioMediaPlayer.getCurrentTime())) {
            ArrayList<Object> labelList = lyric.getLinked(audioMediaPlayer.getCurrentTime());
            if (labelList.equals(curLabels))
                return;
            if (curLabels != null) {
                for (Object object : curLabels)
                    ((Label) object).setTextFill(Color.GREY);
            }
            for (Object object : labelList)
                ((Label) object).setTextFill(Color.WHITE);
            lyricView.scrollTo((Label) labelList.get(0));
            curLabels = labelList;
        }
    };

    public CurPlayPage() {
        Rectangle clip = new Rectangle();
        this.setClip(clip);
        clip.widthProperty().bind(this.widthProperty());
        clip.heightProperty().bind(this.heightProperty());

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis(0, 50, 0);

        bc = new BarChart<>(xAxis, yAxis);
        bc.setLegendVisible(false);
        bc.setAnimated(false);
        bc.setBarGap(0);
        bc.setCategoryGap(2);
        bc.setVerticalGridLinesVisible(false);
        bc.setHorizontalGridLinesVisible(false);
        bc.setHorizontalZeroLineVisible(false);
        bc.setVerticalZeroLineVisible(false);
        bc.setPadding(new Insets(0));
        yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis, null, "dB"));
        xAxis.setTickLabelFill(Color.TRANSPARENT);
        yAxis.setTickLabelFill(Color.TRANSPARENT);

        series = new XYChart.Series<>();

        for (int i = 0; i < 32; i++) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(Integer.toString(i + 1), 50);
            series.getData().add(data);
        }
        bc.getData().add(series);

        this.getChildren().add(bc);

        VBox box = new VBox();
        FlatButton addLyric = new FlatButton(new ImageView(Main.class.getResource("resources/baseline_add_black_18dp_small.png").toString()));
        FlatButton hideOrShowLyric = new FlatButton(new ImageView(Main.class.getResource("resources/outline_not_interested_black_18dp_small.png").toString()));
        hideOrShowLyric.setOnAction(MouseEvent -> {
            if (lyricView.getOpacity() == 1) {
                lyricView.setOpacity(0);
                hideOrShowLyric.setGraphic(new ImageView(Main.class.getResource("resources/outline_fiber_manual_record_black_18dp_small.png").toString()));
            } else {
                lyricView.setOpacity(1);
                hideOrShowLyric.setGraphic(new ImageView(Main.class.getResource("resources/outline_not_interested_black_18dp_small.png").toString()));
            }
        });
        addLyric.prefHeightProperty().bind(addLyric.prefWidthProperty());
        addLyric.setOnAction(mouseEvent -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("lrc", "*.lrc"),
                    new FileChooser.ExtensionFilter(LOC.getString("AllFiles"), "*.*")
            );
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                Platform.runLater(() -> setLyric(file));
            }
        });
        HBox hBox = new HBox(addLyric, hideOrShowLyric);
        hBox.setStyle("-fx-background-color: rgba(100, 100, 100, 0.8);");
        hBox.setOpacity(0);
        hBox.setOnMouseEntered(mouseEvent -> {
            if (audioMediaPlayer != null)
                hBox.setOpacity(1);
        });
        hBox.setOnMouseExited(mouseEvent -> {
            if (audioMediaPlayer != null)
                hBox.setOpacity(0);
        });
        lyricView = new JFXListView<>();
        lyricView.maxWidthProperty().bind(lyricView.minWidthProperty());
        lyricView.minWidthProperty().bind(Bindings.createDoubleBinding(() -> bc.getWidth() * 0.8, bc.widthProperty()));
        lyricView.maxHeightProperty().bind(lyricView.minHeightProperty());
        lyricView.minHeightProperty().bind(Bindings.createDoubleBinding(() -> bc.getHeight() * 0.5, bc.heightProperty()));
        lyricView.addEventFilter(MouseEvent.MOUSE_PRESSED, MouseEvent::consume);
        box.getChildren().addAll(hBox, lyricView);
        box.maxWidthProperty().bind(box.minWidthProperty());
        box.minWidthProperty().bind(lyricView.widthProperty());
        box.maxHeightProperty().bind(box.minHeightProperty());
        box.minHeightProperty().bind(Bindings.createDoubleBinding(() -> bc.getHeight() * 0.7, bc.heightProperty()));
        box.setStyle("-fx-alignment: center;");
        this.getChildren().add(box);

        getStylesheets().add(Main.class.getResource("resources/CurPlayPage.css").toExternalForm());
        stop();
    }

    public void setLyric(File file) {
        try {
            audioMediaPlayer.currentTimeProperty().removeListener(lyricScrollListener);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        Platform.runLater(() -> lyricView.getItems().clear());
        lyric = null;
        try {
            lyric = new Lyric(file);
            lyric.initIterator();
            while (lyric.hasNext()) {
                String[] lyricSameTime = lyric.next();
                for (String str : lyricSameTime) {
                    Label label = new Label(str);
                    label.setTextFill(Color.GREY);
                    label.setStyle("-fx-font-size: 16px;");
                    lyric.link(label);
                    Platform.runLater(() -> lyricView.getItems().add(label));
                }
            }
        } catch (IllegalArgumentException e) {
            lyric = null;
        }
        if (lyric != null)
            Platform.runLater(() -> {
                try {
                    lyricView.scrollTo(0);
                    curLabels = null;
                    audioMediaPlayer.currentTimeProperty().addListener(lyricScrollListener);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            });
    }

    public void setMedia(MediaPlayer mediaPlayer) {
        audioMediaPlayer = mediaPlayer;
        int rand = (int) (Math.random() * 100);
        for (int i = 0; i < colorChooser.length; i++)
            colorChooser[i] = new ColorChooser(rand);
        for (int i = 0; i < series.getData().size(); i++) {
            bc.lookup(".data" + i + ".chart-bar").setStyle("-fx-background-color:" +
                    colorChooser[i].nextColor() + ";");
            series.getData().get(i).setYValue(0); //Top Series
        }
        if (lyric != null)
            try {
                lyricView.scrollTo(0);
                curLabels = null;
                audioMediaPlayer.currentTimeProperty().addListener(lyricScrollListener);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
    }

    public void start() {
        try {
            audioMediaPlayer.setAudioSpectrumListener((double d, double d1, float[] magnitudes, float[] phases) -> {
                for (int i = 0; i < series.getData().size(); i++) {
//                if (d - t >= 1)
                    bc.lookup(".data" + i + ".chart-bar").setStyle("-fx-background-color:" +
                            colorChooser[i].nextColor() + ";");
                    series.getData().get(i).setYValue(magnitudes[i] + 60); //Top Series
                }
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void stop() {
        for (XYChart.Data<String, Number> data : series.getData())
            data.setYValue(0);
    }

}