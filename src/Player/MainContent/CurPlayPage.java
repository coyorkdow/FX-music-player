package Player.MainContent;

import Player.Main;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;

public class CurPlayPage extends StackPane {

    private MediaPlayer audioMediaPlayer;
    private XYChart.Data[] seriesData;
    final private BarChart<String, Number> bc;

    ColorChooser[] colorChooser = new ColorChooser[32];

    public CurPlayPage() {
//        Media media = new Media(this.getClass().getResource("fripSide-only my railgun.mp3").toExternalForm());
//        audioMediaPlayer = new MediaPlayer(media);
//        audioMediaPlayer.setAudioSpectrumNumBands(32);

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
        series1.setName("Series Neg");
        seriesData = new XYChart.Data[32];

        for (int i = 0; i < seriesData.length; i++) {
            seriesData[i] = new XYChart.Data<>(Integer.toString(i + 1), 50);
            series1.getData().add(seriesData[i]);
        }
        bc.getData().add(series1);
        bc.getStylesheets().add(Main.class.getResource("resources/CurPlayPage.css").toExternalForm());
        this.getChildren().add(bc);
        stop();
    }

    public void setMedia(MediaPlayer mediaPlayer){
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
    }

    public void start() {
        try {
            audioMediaPlayer.setAudioSpectrumListener((double d, double d1, float[] magnitudes, float[] phases) -> {
                for (int i = 0; i < seriesData.length; i++) {
//                if (d - t >= 1)
                    bc.lookup(".data" + i + ".chart-bar").setStyle("-fx-background-color:" +
                            colorChooser[i].nextColor() + ";");
                    seriesData[i].setYValue(magnitudes[i] + 60); //Top Series
//                50 * fuc((magnitudes[i] + 60) / 120)
//                series1Data[i].setYValue(phases[i] * 10);
                }
            });
        } catch (Exception e) {
        }
    }

    public void stop() {
        for (int i = 0; i < seriesData.length; i++) {
            seriesData[i].setYValue(0); //Top Series
        }
    }

}