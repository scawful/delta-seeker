package org.halext.deltaseeker;

import java.io.IOException;
import java.util.Set;

import org.halext.deltaseeker.service.Model;
import org.halext.deltaseeker.service.data.Historical;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextField;

public class TrainingController {

    @FXML TextField tickerInput;

    @FXML LineChart<String, Number> trainingChart;

    @FXML
    public void populateData( String ticker ) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(ticker);
        for ( int i = 0; i < Historical.candles.size(); i++ )
        {
            String shortDate = Historical.candles.get(i).datetime.toString().substring(4,10);
            series.getData().add(new XYChart.Data<>(shortDate, Historical.candles.get(i).open ));
        }
        trainingChart.getData().add(series);
        series.getNode().setStyle("-fx-stroke: #405050;");
        
        trainingChart.lookup(".chart-plot-background").setStyle("-fx-background-color: #6f6274;");

        Platform.runLater(()
                -> {

            Set<Node> nodes = trainingChart.lookupAll(".series" + 0);
            for (Node n : nodes) {
                n.setStyle("-fx-background-color: black, white;\n"
                        + "    -fx-background-insets: 0, 2;\n"
                        + "    -fx-background-radius: 5px;\n"
                        + "    -fx-padding: 1px;");
            }

            series.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: black;");
        });

        trainingChart.axisSortingPolicyProperty();
    }

    @FXML
    private void generateModel() throws IOException {
       
        Model.createPriceHistory( tickerInput.getText() );
        populateData( tickerInput.getText() );
    }
}
