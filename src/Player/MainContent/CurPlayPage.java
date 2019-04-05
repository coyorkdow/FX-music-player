package Player.MainContent;

import Player.Main;
import com.jfoenix.controls.JFXListView;
import control.ColorChooser;
import control.Lyric;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.util.ArrayList;

public class CurPlayPage extends StackPane {

    private MediaPlayer audioMediaPlayer;
    private XYChart.Data[] seriesData;
    final private BarChart<String, Number> bc;
    private JFXListView<Label> lyricView;

    private ColorChooser[] colorChooser = new ColorChooser[32];
    private Lyric lyric = null;

    private ArrayList<Object> curLabels = null;

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

        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
//        series1.setName("Series Neg");
        seriesData = new XYChart.Data[32];

        for (int i = 0; i < seriesData.length; i++) {
            seriesData[i] = new XYChart.Data<>(Integer.toString(i + 1), 50);
            series1.getData().add(seriesData[i]);
        }
        bc.getData().add(series1);

        this.getChildren().add(bc);

        lyricView = new JFXListView<>();
        lyricView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        lyricView.maxWidthProperty().bind(lyricView.minWidthProperty());
        lyricView.minWidthProperty().bind(Bindings.createDoubleBinding(() -> bc.getWidth() * 0.9, bc.widthProperty()));
        lyricView.maxHeightProperty().bind(lyricView.minHeightProperty());
        lyricView.minHeightProperty().bind(Bindings.createDoubleBinding(() -> bc.getHeight() * 0.5, bc.heightProperty()));

        lyricView.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> mouseEvent.consume());

        this.getChildren().add(lyricView);

        getStylesheets().add(Main.class.getResource("resources/CurPlayPage.css").toExternalForm());
        stop();
    }

    public void setLyric(File file) {
        lyricView.getItems().clear();
        lyric = null;
        try {
            lyric = new Lyric(file);
            lyric.initIterator();
            Label label;
            while (lyric.hasNext()) {
                String[] lyricSameTime = lyric.next();
                for (String str : lyricSameTime) {
                    label = new Label(str);
                    label.setTextFill(Color.GREY);
                    label.setStyle("-fx-font-size: 16px;");
                    lyricView.getItems().add(label);
                    lyric.link(label);
                }
            }
        } catch (IllegalArgumentException e) {
            lyric = null;
        }
    }

    public void setMedia(MediaPlayer mediaPlayer) {
        audioMediaPlayer = mediaPlayer;
        int rand = (int) (Math.random() * 100);
        for (int i = 0; i < colorChooser.length; i++)
            colorChooser[i] = new ColorChooser(rand);
        for (int i = 0; i < seriesData.length; i++) {
//                if (d - t >= 1)
            bc.lookup(".data" + i + ".chart-bar").setStyle("-fx-background-color:" +
                    colorChooser[i].nextColor() + ";");
            seriesData[i].setYValue(0); //Top Series
//                series1Data[i].setYValue(phases[i] * 10);
//                series2Data[i].setYValue(-(magnitudes[i]+60));//Bottom series
        }
        if (lyric != null)
            try {
                lyricView.scrollTo(0);
                curLabels = null;
                audioMediaPlayer.currentTimeProperty().addListener((observableValue, duration, t1) -> {
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
                        }
                );
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
    }

    public void start() {
        try {
            audioMediaPlayer.setAudioSpectrumListener((double d, double d1, float[] magnitudes, float[] phases) -> {
                for (int i = 0; i < seriesData.length; i++) {
//                if (d - t >= 1)
                    bc.lookup(".data" + i + ".chart-bar").setStyle("-fx-background-color:" +
                            colorChooser[i].nextColor() + ";");
                    seriesData[i].setYValue(magnitudes[i] + 60); //Top Series
                }
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void stop() {
        for (int i = 0; i < seriesData.length; i++) {
            seriesData[i].setYValue(0); //Top Series
        }
    }

}