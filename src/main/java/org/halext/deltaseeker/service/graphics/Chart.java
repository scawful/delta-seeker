package org.halext.deltaseeker.service.graphics;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;

public class Chart {

    private static final String CHARTPLOTBG = ".chart-plot-background";
    private Integer legendColorIndex = 1;

    LineChart<String, Number> lineChart;
    BarChart<String, Number> barChart;
    
    public Chart(LineChart<String, Number> lineChart, BarChart<String, Number> barChart) {
        this.lineChart = lineChart;
        this.barChart = barChart;
    }

    public void initBackground() {
        lineChart.lookup(CHARTPLOTBG).setStyle("-fx-background-color: #30384b;");
        barChart.setVisible(false);
    }

    public void updateChartBackground() {
        lineChart.lookup(CHARTPLOTBG).setStyle("-fx-background-color: transparent;");
        barChart.lookup(CHARTPLOTBG).setStyle("-fx-background-color: #30384b;");
    }

    public void setChartBackgroundColor(LineChart<String, Number> newChart) {
        newChart.lookup(CHARTPLOTBG).setStyle("-fx-background-color: #30384b;");
    }
    
    public void setPriceLineColor(XYChart.Series<String, Number> priceSeries) {
        priceSeries.getNode().setStyle("-fx-stroke: #405050;");
        priceSeries.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: #9DAAD9;");

        // Platform.runLater(()
        //         -> {

        //     Set<Node> nodes = lineChart.lookupAll(".series" + 0);
        //     for (Node n : nodes) {
        //         n.setStyle("-fx-background-color: white;\n"
        //                 + "    -fx-background-insets: 0, 2;\n"
        //                 + "    -fx-background-radius: 5px;\n"
        //                 + "    -fx-padding: 1px;");
        //     }

        //     Node ns = lineChart.lookup(".default-color0.chart-line-symbol");
        //     ns.setStyle("-fx-background-color: #9DAAD9;");
        //     priceSeries.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: #9DAAD9;");
        // });    
    }

    public void setMovingAverageColor(XYChart.Series<String, Number> movingAvgSeries, Color color) {
        String rgb = String.format("%d, %d, %d",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));

        Platform.runLater(() -> {
            System.out.println(".default-color" + legendColorIndex + ".chart-line-symbol");
            Node ns = lineChart.lookup(".default-color" + legendColorIndex + ".chart-line-symbol");
            try {
                ns.setStyle("-fx-background-color: rgba(" + rgb + ", 1.0);");
            } catch ( NullPointerException e ) {
                e.printStackTrace();
            }
            legendColorIndex++;
        });
        
        movingAvgSeries.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: rgba(" + rgb + ", 1.0);");
    }
}
