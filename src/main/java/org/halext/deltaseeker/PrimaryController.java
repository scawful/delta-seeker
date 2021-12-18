package org.halext.deltaseeker;

import java.io.IOException;
import java.util.Set;

import javax.websocket.DeploymentException;

import org.halext.deltaseeker.service.Client;
import org.halext.deltaseeker.service.Model;
import org.halext.deltaseeker.service.data.Historical;
import org.json.simple.parser.ParseException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class PrimaryController {

    Client client = new Client();

    @FXML TextField tickerInput;
    @FXML Label modelOutput;
    @FXML LineChart<String, Number> mainChart;

    @FXML
    public void initialize() {
        mainChart.lookup(".chart-plot-background").setStyle("-fx-background-color: #30384b;");
    }

    @FXML
    private void switchToTrainingMode() throws IOException {
        DeltaSeeker.setRoot("training");
    }

    @FXML
    public void loadChart( String ticker ) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(ticker);
        for ( int i = 0; i < Historical.candles.size(); i++ )
        {
            String shortDate = Historical.candles.get(i).getDatetime().toString().substring(4,10);
            series.getData().add(new XYChart.Data<>(shortDate, Historical.candles.get(i).getOpen() ));
        }
        mainChart.getData().add(series);
        series.getNode().setStyle("-fx-stroke: #405050;");
        
        Platform.runLater(()
                -> {

            Set<Node> nodes = mainChart.lookupAll(".series" + 0);
            for (Node n : nodes) {
                n.setStyle("-fx-background-color: black, white;\n"
                        + "    -fx-background-insets: 0, 2;\n"
                        + "    -fx-background-radius: 5px;\n"
                        + "    -fx-padding: 1px;");
            }

            series.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: #9DAAD9;");
        });

        mainChart.axisSortingPolicyProperty();
        mainChart.setAnimated(false);// disable animation
    }

    @FXML
    private void loadInstrument() throws IOException {
        Model pricePredictionModel = new Model();
        pricePredictionModel.createPriceHistory( tickerInput.getText() );
        loadChart( tickerInput.getText() );
        modelOutput.setText(pricePredictionModel.getDebugOutput());
    }

    @FXML
    private void postAccessToken() throws IOException, org.json.simple.parser.ParseException {
        client.retrieveKeyFile();
        client.postAccessToken();
    }

    @FXML
    private void startStreamingSession() throws IOException, ParseException, DeploymentException, java.text.ParseException {
        client.openStream();
    }
}
